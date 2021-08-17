package com.example.pizza.orders;

import com.google.gson.annotations.Expose;

// Authentication body https://order-pizza-api.herokuapp.com/api/ui/#!/Auth/auth_login
public class Auth {

    @Expose
    private String username;
    @Expose
    private String password;

    public Auth() {}

    public Auth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
