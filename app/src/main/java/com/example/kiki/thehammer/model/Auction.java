package com.example.kiki.thehammer.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Lazar on 6/4/2017.
 */

public class Auction {

    private int id;
    public double startPrice;
    public Date startDate;
    public Date endDate;
    public User user;
    public Item item;
    public List<Bid> bids;

    public Auction(int id, double startPrice, Date startDate, Date endDate, User user, Item item, List<Bid> bids) {
        this.id = id;
        this.startPrice = startPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.item = item;
        this.bids = bids;
    }

    public Auction(int id, double startPrice, Date endDate, Item item){
        this.id = id;
        this.startPrice = startPrice;
        this.endDate = endDate;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }
}
