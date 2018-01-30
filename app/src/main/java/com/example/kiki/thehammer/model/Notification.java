package com.example.kiki.thehammer.model;

/**
 * Created by Lazar on 28/1/2018.
 */

public class Notification {

    private String id;
    private String userId;
    private String token;

    public Notification(){}

    public Notification(String id, String userId, String token) {
        this.id = id;
        this.userId = userId;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
