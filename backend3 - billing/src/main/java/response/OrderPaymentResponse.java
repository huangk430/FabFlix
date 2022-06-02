package response;

import com.github.klefstad_teaching.cs122b.core.result.Result;
import com.stripe.model.PaymentIntent;

public class OrderPaymentResponse {
    private Result result;
    private String paymentIntentId;
    private String clientSecret;

    public Result getResult() {
        return result;
    }

    public OrderPaymentResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public OrderPaymentResponse setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public OrderPaymentResponse setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
