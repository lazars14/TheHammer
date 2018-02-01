package com.example.kiki.thehammer.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.BidsAdapter;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.helpers.InternetHelper;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.Notification;
import com.example.kiki.thehammer.model.User;
import com.example.kiki.thehammer.services.AuctionService;
import com.example.kiki.thehammer.services.BidService;
import com.example.kiki.thehammer.services.NotificationService;
import com.example.kiki.thehammer.services.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemBidsFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    private FloatingActionButton fab;
    private BidsAdapter adapter;
    private List<Bid> bids = new ArrayList<>();
    private AuctionService auctionService = new AuctionService();
    private BidService bidService = new BidService();
    private UserService userService = new UserService();

    private String item_id, item_name, item_description, item_image;
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
            item_name = bundle.getString("name");
            item_description = bundle.getString("description");
            item_image = bundle.getString("image");
        }

        recyclerView = v.findViewById(R.id.recycler_view);

        adapter = new BidsAdapter(getContext(), bids);
        recyclerView.setAdapter(adapter);

        // reverse the list to show highest bid on top
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        if(InternetHelper.isNetworkAvailable(getContext())){
            load_bids_from_firebase();
        } else {
            Toast.makeText(getContext(), DummyData.TURN_ON_INTERNET_DATA, Toast.LENGTH_SHORT).show();
        }


        return v;
    }

    private void load_bids_from_firebase(){
        final Date now = new Date();

        @SuppressLint("StaticFieldLeak") AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                Query query = auctionService.ALL_AUCTIONS_QUERY;
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot auctionSnapshot : dataSnapshot.getChildren()){
                            Auction auction = auctionSnapshot.getValue(Auction.class);

                            if(auction != null){
                                if(auction.getItem().getId().equals(item_id) && auction.getEndDate().after(now)){
                                    auction_id = auction.getId();
                                    end_date = auction.getEndDate();
                                    start_price = auction.getStartPrice();

                                    Query bidQuery = bidService.getAllBidsDbReference().orderByChild("price");

                                    bidQuery.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot bidSnapshot : dataSnapshot.getChildren()){
                                                final Bid bid = bidSnapshot.getValue(Bid.class);

                                                if(bid != null){
                                                    if(bid.getAuction().getId().equals(auction_id)){

                                                        Query userQuery = userService.getUserById(bid.getUser().getId());

                                                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                User user = dataSnapshot.getValue(User.class);
                                                                bid.setUser(user);

                                                                if(!bids.contains(bid)){
                                                                    bids.add(bid);
                                                                    adapter.notifyDataSetChanged();

                                                                    mLayoutManager.scrollToPosition(bids.size() - 1);
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } else Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
            }
        };

        task.execute();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder add_bid_dialog_builder = new AlertDialog.Builder(
                getContext());

        add_bid_dialog_builder.setTitle("Enter your bid");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_bid, null);

        final NumberPicker euro_picker = dialogView.findViewById(R.id.euro_picker);
        double min_price;
        if(bids.size() > 0) min_price = bids.get(bids.size() - 1).getPrice();
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

        double cents_from_price = min_price - (int) min_price;
        double value = Math.round(cents_from_price*100.0)/100.0;
        int index = (int) (value / 0.01);

        cent_picker.setValue(index + 1);

        add_bid_dialog_builder.setView(dialogView);

        // set dialog message
        add_bid_dialog_builder
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        boolean auctionEnded = DateHelper.auctionEnded(end_date);
                        if(auctionEnded){
                            Toast.makeText(getContext(), "Bid failed - auction over", Toast.LENGTH_SHORT).show();
                        } else {
                            double euros = euro_picker.getValue();
                            double cents = cent_picker.getValue();
                            final double euro_cents = euros + cents/100;

                            if(euro_cents > bids.get(bids.size() - 1).getPrice()){
                                final String last_bid_user_id = bids.get(bids.size() - 1).getUser().getId();

                                if(InternetHelper.isNetworkAvailable(getContext())){
                                    final NotificationService notificationService = new NotificationService(getContext());
                                    DatabaseReference dbReference = notificationService.getAllNotifications();

                                    dbReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot notificationSnap : dataSnapshot.getChildren()){
                                                Notification notification = notificationSnap.getValue(Notification.class);

                                                if(notification != null){
                                                    if(notification.getUserId().equals(last_bid_user_id)){
                                                        BidService bidService = new BidService();
                                                        bidService.addBid(euro_cents, new Date(), new Auction(auction_id), new User(last_bid_user_id));

                                                        notificationService.sendNotification(notificationService.buildMessage(item_id, item_name, item_description, item_image));
                                                        Toast.makeText(getContext(), "Bid successfull", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Turn on internet to post bid!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Bid failed - not bigger than last bid", Toast.LENGTH_SHORT).show();
                            }

                        }
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
}
