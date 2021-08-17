package com.example.pizza.orders;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import org.springframework.web.bind.annotation.PathVariable;

import com.example.pizza.config.PizzeriaAPI;

import java.util.List;

/*
    Interface for Feign Client for Pizzeria API https://order-pizza-api.herokuapp.com/api/ui/#/
 */
public interface PizzeriaClient {

    String baseUrl = PizzeriaAPI.url;

    @RequestLine("GET /orders")
    @Headers({"Accept: application/json"})
    List<Order> getOrders();

    @RequestLine("POST /auth")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    AccessToken auth(Auth auth);

    @RequestLine("POST /orders")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Authorization: Bearer {token}"
    })
    Order postOrder(@Param("token") String token, Order order);

    @RequestLine("DELETE /orders/{id}")
    @Headers({"Accept: application/json"})
    Object delete(@PathVariable Long id);
}
