package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.Order;
import com.stripe.model.PaymentIntent;
import data.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import requests.CartInsertRequest;
import requests.orderCompleteRequest;
import response.CartActionResponse;
import response.CartDetailsResponse;
import response.OrderPaymentResponse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
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

    //returns a Result as response
    @PostMapping("/cart/insert")
    public ResponseEntity<CartActionResponse> cartInsert(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody CartInsertRequest request
    ) throws ParseException {

        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //errorcheck
        if (request.getQuantity() < 1) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }

        if (request.getQuantity() > 10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }

        //insert userid, movieid, quantity into DB
        this.repo.cartInsert(userId.intValue(), request.getMovieId().intValue(), request.getQuantity());

        //create body for response object
        CartActionResponse response = new CartActionResponse()
                .setResult(BillingResults.CART_ITEM_INSERTED);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/cart/update")
    public ResponseEntity<CartActionResponse> cartUpdate(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody CartInsertRequest request
    ) throws ParseException {
        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //errorcheck
        if (request.getQuantity() < 1) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }

        if (request.getQuantity() > 10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }

        //insert userid, movieid, quantity into DB
        this.repo.cartUpdate(userId.intValue(), request.getMovieId().intValue(), request.getQuantity());

        //create body for response object
        CartActionResponse response = new CartActionResponse()
                .setResult(BillingResults.CART_ITEM_UPDATED);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<CartActionResponse> cartDelete(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId
    ) throws ParseException {

        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //insert userid, movieid, quantity into DB
        this.repo.cartDelete(userId.intValue(), movieId.intValue());

        //create body for response object
        CartActionResponse response = new CartActionResponse()
                .setResult(BillingResults.CART_ITEM_DELETED);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/cart/retrieve")
    public ResponseEntity<CartDetailsResponse> cartRetrieve(
            @AuthenticationPrincipal SignedJWT user
    ) throws ParseException {

        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //check if premium
        int isPremium = checkIsPremium(user);

        //insert userid, movieid, quantity into DB
        CartDetailsResponse response = this.repo.cartRetrieve(userId.intValue(), isPremium);


        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @PostMapping("/cart/clear")
    public ResponseEntity<CartActionResponse> cartClear(
            @AuthenticationPrincipal SignedJWT user
    ) throws ParseException {

        Long userId = user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //insert userid, movieid, quantity into DB
        this.repo.cartClear(userId.intValue());

        //create body for response object
        CartActionResponse response = new CartActionResponse()
                .setResult(BillingResults.CART_CLEARED);

        //response entity = object
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}

