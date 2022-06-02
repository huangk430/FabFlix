import Config from "backend/config.json";
import Axios from "axios";


export async function insertCart(insertRequest, accessToken) {
    const requestBody = {
        movieId: insertRequest.movieId,
        quantity: insertRequest.quantity
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.cartUrl,
        url: Config.cart.insertCart,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function retrieveCart(queryParams, accessToken) {

    const options = {
        method: "GET",
        baseURL: Config.cartUrl,
        url: Config.cart.retrieveCart,
        params: queryParams,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function cartUpdate(cartRequest, accessToken) {
    const requestBody = {
        movieId: cartRequest.movieId,
        quantity: cartRequest.quantity
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.cartUrl,
        url: Config.cart.updateCart,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function cartDelete(deleteRequest, accessToken) {
    const requestBody = {
        movieId: deleteRequest.movieId
    }

    const options = {
        method: "DELETE", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.cartUrl,
        url: Config.cart.deleteCart + deleteRequest.movieId,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function cartClear(accessToken) {

    const options = {
        method: "POST",
        baseURL: Config.cartUrl,
        url: Config.cart.clearCart,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}