package com.example.kiki.thehammer.fragments;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.BidsAdapter;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.User;
import com.example.kiki.thehammer.services.AuctionService;
import com.example.kiki.thehammer.services.BidService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemBidsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FloatingActionButton fab;
    private BidsAdapter adapter;
    private List<Bid> bids = new ArrayList<>();
    private AuctionService auctionService = new AuctionService();
    private BidService bidService = new BidService();

    private String item_id;
    private String auction_id;
    private Date end_date;
    private double start_price;
    private String[] cents;

    public ItemBidsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_bids_fragment, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            item_id = bundle.getString("id");
        }

        recyclerView = v.findViewById(R.id.recycler_view);

        gridLayoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new BidsAdapter(getContext(), bids);
        recyclerView.setAdapter(adapter);

        fab = v.findViewById(R.id.fab);

        load_bids_from_firebase();

        return v;
    }

    private void load_bids_from_firebase(){
        final Date now = new Date();

        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
//                Query query = auctionService.ALL_AUCTIONS_QUERY;
//                query.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot auctionSnapshot : dataSnapshot.getChildren()){
//                            Auction auction = auctionSnapshot.getValue(Auction.class);
//                            if(auction.getItem().getId().equals(item_id) && DateHelper.stringToDate(auction.getEndDate()).after(now)){
//                                auction_id = auction.getId();
//
//                                Query bidQuery = bidService.getAllBidsDbReference().orderByChild("price");
//
//                                bidQuery.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot bidSnapshot : dataSnapshot.getChildren()){
//                                            Bid bid = bidSnapshot.getValue(Bid.class);
//
//                                            bids.add(bid);
//                                            adapter.notifyDataSetChanged();
//                                        }
//
//                                        setOnClickListenerForButton();
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                Query query = auctionService.ALL_AUCTIONS_QUERY;
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot auctionSnapshot : dataSnapshot.getChildren()){
                            Auction auction = auctionSnapshot.getValue(Auction.class);
                            if(auction.getItem().getId().equals(item_id)){
                                auction_id = auction.getId();
                                end_date = DateHelper.stringToDate(auction.getEndDate());

                                Query bidQuery = bidService.getAllBidsDbReference().orderByChild("price");

                                bidQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot bidSnapshot : dataSnapshot.getChildren()){
                                            Bid bid = bidSnapshot.getValue(Bid.class);

                                            if(bid.getAuction().getId().equals(auction_id)){
                                                bids.add(bid);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        setOnClickListenerForButton();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return null;
            }
        };

        task.execute();
    }

    private void setOnClickListenerForButton(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder add_bid_dialog_builder = new AlertDialog.Builder(
                        getContext());

                // set title
                add_bid_dialog_builder.setTitle("Enter your bid");
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_bid, null);

                final NumberPicker euro_picker = dialogView.findViewById(R.id.euro_picker);
                double min_price;
                if(bids.size() > 0) min_price = bids.get(0).getPrice();
                else min_price = start_price - 1;

                int min_price_int = Integer.parseInt(String.valueOf(Math.round(min_price)));

                euro_picker.setMinValue(min_price_int);
                euro_picker.setMaxValue(min_price_int + 1000);

                final NumberPicker cent_picker = dialogView.findViewById(R.id.cent_picker);

                cents = new String[100];
                for(int i = 0; i < 100; i++){
                    if(i < 10) cents[i] = "0" + i;
                    else cents[i] = String.valueOf(i);
                }

                cent_picker.setDisplayedValues(cents);
                cent_picker.setMinValue(0);
                cent_picker.setMaxValue(99);
                // to do
                cent_picker.setValue(0);

                add_bid_dialog_builder.setView(dialogView);

                // set dialog message
                add_bid_dialog_builder
                        .setCancelable(false)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
//                                boolean auctionEnded = DateHelper.auctionEnded(end_date);
//                                if(auctionEnded){
////                                    Toast.makeText(getContext(), "Bid failed - auction over", Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getContext(), DateHelper.dateToString(end_date), Toast.LENGTH_LONG).show();
//                                } else {
//                                    double euros = euro_picker.getValue();
//                                    double cents = cent_picker.getValue();
//                                    double euro_cents = euros + cents/100;
//                                    boolean valid = true;
//
//                                    BidService bidService = new BidService();
//                                    bidService.addBid(euro_cents, DateHelper.dateToString(new Date()), new Auction(auction_id), new User(DummyData.user_id));
//
//                                    if(valid) Toast.makeText(getContext(), "Bid successfull", Toast.LENGTH_SHORT).show();
//                                    else Toast.makeText(getContext(), "Bid failed - server error", Toast.LENGTH_SHORT).show();
//                                }
                                double euros = euro_picker.getValue();
                                double cents = cent_picker.getValue();
                                double euro_cents = euros + cents/100;

                                BidService bidService = new BidService();
                                bidService.addBid(euro_cents, DateHelper.dateToString(new Date()), new Auction(auction_id), new User(DummyData.user_id));
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog add_bid_dialog = add_bid_dialog_builder.create();

                // show it
                add_bid_dialog.show();
            }
        });
    }
}
