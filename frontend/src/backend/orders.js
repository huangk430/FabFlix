import Config from "backend/config.json";
import Axios from "axios";

export async function orderPayment(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartUrl,
        url: Config.orders.orderPayment,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function orderComplete(orderRequest, accessToken) {
    const requestBody = {
        paymentIntentId: orderRequest.paymentIntentId
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.cartUrl,
        url: Config.orders.orderComplete,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function orderList(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartUrl,
        url: Config.orders.orderList,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function orderDetails(saleId, accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartUrl,
        url: Config.orders.orderDetails + saleId,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}