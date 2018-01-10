package com.example.kiki.thehammer.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private final SimpleDateFormat format = new SimpleDateFormat("DD/mm/yyyy hh:mm");

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

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
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

        return v;
    }

    private void load_data_from_content_provider(int id){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                ContentResolver resolver = getActivity().getContentResolver();
                Cursor auction_cursor = resolver.query(TheHammerContract.AuctionTable.CONTENT_URI,
                        new String[]{TheHammerContract.AuctionTable.AUCTION_ID},
                        TheHammerContract.AuctionTable.AUCTION_ITEM_ID + " = ?",
                        new String[]{String.valueOf(item_id)},
                        null);
                if (auction_cursor.moveToFirst()){
                    auction_id = auction_cursor.getInt(0);
                }

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

                    } while (cursor.moveToNext());
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
