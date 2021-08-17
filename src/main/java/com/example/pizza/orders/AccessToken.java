package com.example.pizza.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Access Token response https://order-pizza-api.herokuapp.com/api/ui/#!/Auth/auth_login
public class AccessToken {

    @Expose
    @SerializedName("access_token")
    private String accessToken;

    public AccessToken() {}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

