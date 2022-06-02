import React, { useState, useEffect } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import {orderComplete, orderPayment} from "../backend/orders";

import CheckoutForm from "../pages/CheckoutForm";
import {useUser} from "../hook/User";
import "index.css";
import Button from "@mui/material/Button";

const stripePromise = loadStripe("pk_test_51L1KT6A6FPmczoGVaNZ6wlz6oTx3DEOF006dCb7tZqBD5X3htwfpgbwiv2K8QVqUcsKsPvTJTaU6GyqDwn91Kf1i00q5kUixme");

//CREATE PAYMENT INTENT
export default function App() {
    const [paymentIntentId, setPaymentIntentId] = useState("");
    const [clientSecret, setClientSecret] = useState("");

    const {accessToken} = useUser();

    //calling get request from /order/payment endpoint to get paymentintentid
    const createPaymentIntent = () => {
        orderPayment(accessToken)
            .then(response => {
                console.log(response.data)
                setClientSecret(response.data.clientSecret)
                setPaymentIntentId(response.data.paymentIntentId)
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }


    useEffect(() => {createPaymentIntent()}, []);


    const appearance = {
        theme: 'stripe',
    };
    const options = {
        clientSecret,
        appearance,
    };

    return (
        <div className="App">
            {clientSecret && (
                <Elements options={options} stripe={stripePromise}>
                    <CheckoutForm />

                </Elements>
            )}
        </div>
    );
}