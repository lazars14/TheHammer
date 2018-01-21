package com.example.kiki.thehammer.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.kiki.thehammer.adapters.AuctionsAdapter;
import com.example.kiki.thehammer.adapters.ItemsAdapter;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lazar on 16/1/2018.
 */

public class FilterHelper {

    private String criteria;
    private String value;
    private List<Item> items;
    private RecyclerView recyclerView;
    private Context context;

    private List<Auction> auctions;
    private boolean ended;

    public FilterHelper(String criteria, String value, List<Item> items, RecyclerView recyclerView, Context context) {
        this.criteria = criteria;
        this.value = value;
        this.items = items;
        this.recyclerView = recyclerView;
        this.context = context;

        filterItems();
    }

    public FilterHelper(String criteria, String value, List<Auction> auctions, boolean ended, RecyclerView recyclerView, Context context) {
        this.criteria = criteria;
        this.value = value;
        this.auctions = auctions;
        this.recyclerView = recyclerView;
        this.context = context;
        this.ended = ended;

        filterAuctions();
    }

    private void filterItems(){
        List<Item> filteredList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            if(checkIfItemValid(i)){
                filteredList.add(items.get(i));
            }
        }

        ItemsAdapter adapter = new ItemsAdapter(context, filteredList);
        recyclerView.setAdapter(adapter);
    }

    private boolean checkIfItemValid(int i){
        boolean valid;
        valid = getComparingValueItem(i).contains(value);

        return valid;
    }

    private String getComparingValueItem(int i){
        return (criteria.equals("name")) ? items.get(i).getName() : items.get(i).getDescription();
    }

    private void filterAuctions(){
        List<Auction> filteredList = new ArrayList<>();

        for (int i = 0; i < auctions.size(); i++){
            if(checkIfAuctionValid(i)){
                filteredList.add(auctions.get(i));
            }
        }

        AuctionsAdapter adapter = new AuctionsAdapter(context, filteredList);
        recyclerView.setAdapter(adapter);
    }

    private boolean checkIfAuctionValid(int i){
        boolean valid;
        if(!criteria.equals("status")){
            valid = getComparingValueAuction(i).contains(value);
        } else {
            valid = checkIfAuctionEnded(i);
        }

        return valid;
    }

    private String getComparingValueAuction(int i){
        return (criteria.equals("name")) ? auctions.get(i).getItem().getName() : auctions.get(i).getItem().getDescription();
    }

    private boolean checkIfAuctionEnded(int i){
        boolean valid;
        Date now = new Date();
        if(ended){
            valid = now.after(auctions.get(i).getEndDate());
        }
        else {
            valid = auctions.get(i).getEndDate().after(now);
        }

        return valid;
    }

}
