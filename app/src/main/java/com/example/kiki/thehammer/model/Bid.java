package com.example.kiki.thehammer.model;

import java.util.Date;

/**
 * Created by Lazar on 6/4/2017.
 */

public class Bid {

    private String id;
    private double price;
    private String dateTime;
    private Auction auction;
    private User user;

    public Bid(){}

    public Bid(String id, double price, String dateTime, Auction auction, User user) {
        this.id = id;
        this.price = price;
        this.dateTime = dateTime;
        this.auction = auction;
        this.user = user;
    }

    public Bid(String id, double price, String dateTime, User user){
        this.id = id;
        this.price = price;
        this.dateTime = dateTime;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
