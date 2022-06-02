package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import data.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import requests.orderCompleteRequest;
import response.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OrderController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public OrderController(BillingRepo repo,Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    public Integer checkIsPremium(SignedJWT user) throws ParseException {
        int premium = 0; //0 = not admin, 1 = admin
        List<String> roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        //check if a user is admin or employee
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).equalsIgnoreCase("premium"))
            {
                premium = 1;
            }
        }
        return premium;
    }

    @GetMapping("/order/payment")
    public ResponseEntity<OrderPaymentResponse> orderPayment(@AuthenticationPrincipal SignedJWT user) throws ParseException, StripeException {

        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        int isPremium = checkIsPremium(user);

        //retrieve user's cart
        CartDetailsResponse cartDetails = this.repo.cartRetrieve(userId.intValue(), isPremium);

        //extract total
        BigDecimal total = cartDetails.getTotal();

        //extract string of titles
        StringBuilder title = new StringBuilder();
        List<Item> items = cartDetails.getItems();
        for (int i = 0; i < items.size(); i++) {
            title.append(items.get(i).getMovieTitle());
            if (i < items.size()) {
                title.append(", ");
            }
        }
        Long amountInTotalCents = total.multiply(BigDecimal.valueOf(100)).longValue();

        //extract key value pair of userId
        Map<String, String> userIdMap = new HashMap<String, String>() {{
            put("userId", userId.toString());
        }};

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams
                        .builder()
                        .setCurrency("USD") // This will always be the same for our project
                        .setDescription(title.toString())
                        .setAmount(amountInTotalCents)
                        // We use MetaData to keep track of the user that should pay for the order
                        .putMetadata("userId", Long.toString(userId))
                        .build();
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);


        //create body for response object
        OrderPaymentResponse response = new OrderPaymentResponse()
                .setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED)
                .setPaymentIntentId(paymentIntent.getId())
                .setClientSecret(paymentIntent.getClientSecret());


        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/order/complete")
    public ResponseEntity<CartActionResponse> orderComplete(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody orderCompleteRequest request
    ) throws ParseException, StripeException {

        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        PaymentIntent paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());

        //verification
        if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded")) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }

        Long pUserId = Long.valueOf(paymentIntent.getMetadata().get("userId"));
        if (!pUserId.equals(userId)) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
        }

        int isPremium = checkIsPremium(user);

        //retrieve user's cart
        CartDetailsResponse cartDetails = this.repo.cartRetrieve(userId.intValue(), isPremium);

        //extract total
        BigDecimal total = cartDetails.getTotal();

        //extract items list
        List<Item> items = cartDetails.getItems();

        //create a new billing.sale record
        this.repo.createSaleRecord(userId.intValue(), total);

        // populate the billing.sale_item with the contents of the user's billing.cart.
        this.repo.createSaleItem(userId.intValue(), items);

        // clear user's cart
        this.repo.cartClear(userId.intValue());

        //create body for response object
        CartActionResponse response = new CartActionResponse()
                .setResult(BillingResults.ORDER_COMPLETED);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping ("/order/list")
    public ResponseEntity<OrderListResponse> getUserSales(@AuthenticationPrincipal SignedJWT user) throws ParseException {
        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        OrderListResponse response = this.repo.getSalesList(userId.intValue());

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/order/detail/{saleId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long saleId
    ) throws ParseException {
        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        int isPremium = checkIsPremium(user);

        OrderDetailResponse response = this.repo.getSalesById(userId.intValue(), saleId.intValue(), isPremium);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);


    }

}
