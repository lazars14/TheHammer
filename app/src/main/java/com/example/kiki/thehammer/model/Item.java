package com.example.kiki.thehammer.model;

import java.util.List;

/**
 * Created by Lazar on 6/4/2017.
 */

public class Item {

    private long id;
    public String name;
    public String description;
    public String picture;
    public boolean sold;
    public List<Auction> auctions;

    public Item(long id, String name, String description, String picture, boolean sold, List<Auction> auctions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.sold = sold;
        this.auctions = auctions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(List<Auction> auctions) {
        this.auctions = auctions;
    }
}
