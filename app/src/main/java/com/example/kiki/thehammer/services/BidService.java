package com.example.kiki.thehammer.services;

import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by Lazar on 22/1/2018.
 */

public class BidService {
    private static final String BIDS_REFERENCE = "bids";
    private DatabaseReference dbReference;

    public BidService(){
        this.dbReference = FirebaseDatabase.getInstance().getReference(BIDS_REFERENCE);
    }

    public void addBids(List<Bid> bids, List<Auction> auctions, List<User> users){

        for(int i = 0; i < 10; i++){
            String id = dbReference.push().getKey();
            Bid bid = new Bid(id, DummyData.bid_default_price + i, DummyData.getDummyDate(1, 2, 1), new Auction(auctions.get(auctions.size() - (i + 1)).getId()),
                    new User(users.get(auctions.size() - (i + 1)).getId()));

            dbReference.child(id).setValue(bid);

            bids.add(bid);
        }
    }

    public void addBid(double price, Date date, Auction auction, User user){
        String id = dbReference.push().getKey();
        Bid newBid = new Bid(id, price, date, auction, user);

        dbReference.child(id).setValue(newBid);
    }

    public DatabaseReference getAllBidsDbReference(){
        return dbReference;
    }

    public Query getHighestBidQuery(){
        return dbReference.orderByChild("price");
    }
}
