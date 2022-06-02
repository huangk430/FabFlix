package requests;

import java.security.Key;

public class orderCompleteRequest {
    private String paymentIntentId;
    private Key id;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public orderCompleteRequest setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }

    public Key getId() {
        return id;
    }

    public orderCompleteRequest setId(Key id) {
        this.id = id;
        return this;
    }
}
