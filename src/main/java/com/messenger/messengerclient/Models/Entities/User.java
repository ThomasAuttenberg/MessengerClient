package com.messenger.messengerclient.Models.Entities;

public class User {
    private String authToken;
    private String userName;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getAuthToken() {
        return authToken;
    }
}
