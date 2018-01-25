package com.example.kiki.thehammer.helpers;

import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.model.User;
import com.example.kiki.thehammer.services.AuctionService;
import com.example.kiki.thehammer.services.BidService;
import com.example.kiki.thehammer.services.ItemService;
import com.example.kiki.thehammer.services.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lazar on 24/1/2018.
 */

public class DataInitHelper {

    public static void initDummyData(){
        ItemService itemService = new ItemService();
        UserService userService = new UserService();
        AuctionService auctionService = new AuctionService();
        BidService bidService = new BidService();

        List<Item> items = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Auction> auctions = new ArrayList<>();
        List<Bid> bids = new ArrayList<>();

        itemService.addItems(items);
        userService.addUsers(users);
        auctionService.addAuctions(auctions, users, items);
        bidService.addBids(bids, auctions, users);
    }

}
