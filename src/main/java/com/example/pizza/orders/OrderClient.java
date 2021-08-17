package com.example.pizza.orders;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import feign.Feign;
import feign.FeignException;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.pizza.config.OrderConstraints;
import com.example.pizza.config.PizzeriaAPI;
import com.example.pizza.orders.exceptions.*;

/*
    Wrapper for Pizzeria API https://order-pizza-api.herokuapp.com/api/ui/#/
    Initializes Feign Client, handles authentication.
    Ensures order constraints.
    Throws HTTP response errors.
 */
public class OrderClient {

    private final Logger log = LoggerFactory.getLogger(OrderClient.class);

    private final PizzeriaClient pizzeriaClient;
    private String accessToken = "";
    private final Auth auth = new Auth(PizzeriaAPI.username, PizzeriaAPI.password);

    public OrderClient() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        this.pizzeriaClient = Feign.builder()
                .decoder(new GsonDecoder(gson))
                .encoder(new GsonEncoder(gson))
                .target(PizzeriaClient.class, PizzeriaClient.baseUrl);
    }

    // Refresh access token.
    public void auth() throws AuthenticationException {
        try {
            accessToken = pizzeriaClient.auth(auth).getAccessToken();
        } catch (FeignException e) {
            log.error("Client POST auth: " + e.toString());
            throw new AuthenticationException();
        }
    }

    // Throw according HTTP error response based on violated constraint.
    public void assertOrderConstraints(Order order) throws InvalidFlavorException, InvalidCrustException, InvalidSizeException {
        if (!Arrays.asList(OrderConstraints.flavor).contains(order.getFlavor()))
            throw new InvalidFlavorException(order.getFlavor());
        if (!Arrays.asList(OrderConstraints.crust).contains(order.getCrust()))
            throw new InvalidCrustException(order.getCrust());
        if (!Arrays.asList(OrderConstraints.size).contains(order.getSize()))
            throw new InvalidSizeException(order.getSize());
    }

    public List<Order> getOrders() throws GetOrdersException {
        try {
            return pizzeriaClient.getOrders();
        } catch (FeignException e) {
            log.error("Client GET orders: " + e.toString());
            throw new GetOrdersException();
        }
    }

    public void deleteOrder(Long id) {
        try {
            pizzeriaClient.delete(id);
        } catch (FeignException e) {
            if (e.status() == 404) {
                // Returns 404 when trying to delete non existing order.
                // We ignore this as we always return 204 from our end.
            } else {
                log.error("Client DELETE orders: " + e.toString());
                throw new DeleteOrderException(id);
            }
        }
    }

    // Get next available table number.
    public int getNextTableNumber() {
        // Minimal value.
        int tableNumber = OrderConstraints.minTableNumber;
        // Get all orders and iterate table numbers to get highest taken number.
        List<Order> orders = getOrders();
        for (Order order : orders) {
            tableNumber = Math.max(order.getTableNumber(), tableNumber);
        }
        // Get next available number and reset on overflow.
        tableNumber++;
        if (tableNumber < 0) tableNumber = OrderConstraints.minTableNumber;
        return tableNumber;
    }

    // Post order and retry on failed authentication.
    public Order postOrderWithAuth(Order order) throws PostOrderException {
        try {
            // Try to post first time.
            return pizzeriaClient.postOrder(accessToken, order);
        } catch (FeignException exception) {
            // If token expired (401) or token is invalid (422).
            if (exception.status() == 401 || exception.status() == 422) {
                // Refresh token and try to post again.
                auth();
                try {
                    return pizzeriaClient.postOrder(accessToken, order);
                } catch (FeignException retryException) {
                    log.error("Client POST orders: " + retryException.toString());
                    throw new PostOrderException();
                }
            }
            throw exception;
        }
    }

}
