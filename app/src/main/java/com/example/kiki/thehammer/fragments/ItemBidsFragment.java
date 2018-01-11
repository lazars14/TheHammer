package com.example.kiki.thehammer.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.BidsAdapter;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemBidsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private BidsAdapter adapter;
    private List<Bid> bids = new ArrayList<>();

    private int item_id;
    private int auction_id;
    private Date end_date;
    private double start_price;
    private String[] cents;
    private final SimpleDateFormat format = new SimpleDateFormat("DD/MM/yyyy hh:mm");

    public ItemBidsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_bids_fragment, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            item_id = bundle.getInt("id");
        }

        recyclerView = v.findViewById(R.id.recycler_view);
        load_data_from_content_provider(0);

        gridLayoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new BidsAdapter(getContext(), bids);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == bids.size()-1){
                    load_data_from_content_provider(bids.get(bids.size() - 1).getId());
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(), "Bid successfull", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder add_bid_dialog_builder = new AlertDialog.Builder(
                        getContext());

                // set title
                add_bid_dialog_builder.setTitle("Enter your bid");
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_bid, null);

                final NumberPicker euro_picker = (NumberPicker) dialogView.findViewById(R.id.euro_picker);
                double min_price;
                if(bids.size() > 0) min_price = bids.get(0).getPrice();
                else min_price = start_price - 1;

                int min_price_int = Integer.parseInt(String.valueOf(Math.round(min_price)));

                euro_picker.setMinValue(min_price_int);
                euro_picker.setMaxValue(min_price_int + 1000);

                final NumberPicker cent_picker = (NumberPicker) dialogView.findViewById(R.id.cent_picker);

                if(cents == null){
                    // ovako ili da ucitam kad ucitavam fragment
                    cents = new String[100];
                    for(int i = 0; i < 100; i++){
                        if(i < 10) cents[i] = "0" + i;
                        else cents[i] = String.valueOf(i);
                    }
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
                                Date now = new Date();
                                if(now.after(end_date)){
                                    // auction over
                                    Toast.makeText(getContext(), "Bid failed - auction over", Toast.LENGTH_SHORT).show();
                                } else {
                                    // auction still in progress, bid successfull
                                    double euros = euro_picker.getValue();
                                    double cents = cent_picker.getValue();
                                    double euro_cents = euros + cents/100;
                                    boolean valid = true;
                                    // dodavanje na firebase
                                    if(valid) Toast.makeText(getContext(), "Bid successfull", Toast.LENGTH_SHORT).show();
                                    else Toast.makeText(getContext(), "Bid failed - server error", Toast.LENGTH_SHORT).show();
                                    // trebao bi da uzmem error sa servera da ispisem tacno koji je
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
        });

        return v;
    }

    private void load_data_from_content_provider(int id){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                ContentResolver resolver = getActivity().getContentResolver();
                Cursor auction_cursor = resolver.query(TheHammerContract.AuctionTable.CONTENT_URI,
                        new String[]{TheHammerContract.AuctionTable.AUCTION_ID,
                                TheHammerContract.AuctionTable.AUCTION_END_DATE,
                                TheHammerContract.AuctionTable.AUCTION_START_PRICE},
                        TheHammerContract.AuctionTable.AUCTION_ITEM_ID + " = ?",
                        new String[]{String.valueOf(item_id)},
                        null);
                if (auction_cursor.moveToFirst()){
                    auction_id = auction_cursor.getInt(0);
                    try {
                        end_date = format.parse(auction_cursor.getString(1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    start_price = auction_cursor.getDouble(2);
                }
                auction_cursor.close();

                String selection = TheHammerContract.BidTable.BID_AUCTION_ID + " = ? AND " + TheHammerContract.BidTable.BID_ID + " BETWEEN ? AND ?";
                String[] selectionArgs = new String[]{ String.valueOf(auction_id), String.valueOf(integers[0] + 1), String.valueOf(integers[0] + 6)};
                Cursor cursor =
                        resolver.query(TheHammerContract.BidTable.CONTENT_URI,
                                null,
                                selection,
                                selectionArgs,
                                TheHammerContract.BidTable.BID_PRICE + " DESC");
                if (cursor.moveToFirst()) {
                    do {
                        int bid_id = cursor.getInt(0);
                        double price = cursor.getDouble(1);
                        String date_time_string = cursor.getString(2);
                        Date date = null;
                        User user = null;
                        try {
                            date = format.parse(date_time_string);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        int user_id = cursor.getInt(2);

                        Cursor user_cursor =
                                resolver.query(TheHammerContract.UserTable.CONTENT_URI,
                                        new String[]{TheHammerContract.UserTable.USER_NAME,
                                                    TheHammerContract.UserTable.USER_PICTURE},
                                        TheHammerContract.UserTable.USER_ID + " = ?",
                                        new String[]{String.valueOf(user_id)},
                                        null);
                        if (user_cursor.moveToFirst()){
                            String user_name = user_cursor.getString(0);
                            String user_picture = user_cursor.getString(1);
                            user = new User(user_id, user_name, user_picture);
                        }
                        Bid bid = new Bid(bid_id, price, date, user);
                        bids.add(bid);

                        user_cursor.close();

                    } while (cursor.moveToNext());

                    cursor.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
        };

        task.execute(id);
    }
}
