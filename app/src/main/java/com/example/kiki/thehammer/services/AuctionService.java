package com.example.kiki.thehammer.services;

import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

/**
 * Created by Lazar on 22/1/2018.
 */

public class AuctionService {

    private static final String AUCTIONS_REFERENCE = "auctions";
    public static final Query ALL_AUCTIONS_QUERY = FirebaseDatabase.getInstance().getReference(AUCTIONS_REFERENCE);
    private DatabaseReference dbReference;

    public AuctionService(){
        this.dbReference = FirebaseDatabase.getInstance().getReference(AUCTIONS_REFERENCE);
    }

    public void addAuctions(List<Auction> auctions, List<User> users, List<Item> items) {
        for(int i = 0; i < 10; i++){
            String id = dbReference.push().getKey();
            Auction auction = new Auction(id, DummyData.auction_default_start_price, DummyData.getDummyDate(1, 1, 1),
                    DummyData.getDummyDate(2, 4, 1), new User(users.get(i).getId()), new Item(items.get(i).getId()));

            dbReference.child(id).setValue(auction);

            auctions.add(auction);
        }
    }

    public Query getAuctionById(String auction_id){
        return dbReference.child(auction_id);
    }
}
