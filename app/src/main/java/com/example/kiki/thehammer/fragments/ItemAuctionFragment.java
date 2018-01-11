package com.example.kiki.thehammer.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.ValuePairViewHelper;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Item;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.kiki.thehammer.helpers.ValuePairViewHelper.setLabelValuePair;

public class ItemAuctionFragment extends Fragment {

    private View auction_info_view;
    private int item_id;
    private String start_price, start_date, end_date;

    public ItemAuctionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_auction_fragment, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            // String item_image = bundle.getString("image");
            View item_info_view = v.findViewById(R.id.item_info);
            TextView name = (TextView) item_info_view.findViewById(R.id.name);
            TextView description = (TextView) item_info_view.findViewById(R.id.description);
            name.setText(bundle.getString("name"));
            description.setText(bundle.getString("description"));

            auction_info_view = v.findViewById(R.id.auction_info);

            item_id = bundle.getInt("id");
            load_auction_info();
        }

        return v;
    }

    private void load_auction_info() {
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                ContentResolver resolver = getActivity().getContentResolver();
                Cursor auction_cursor = resolver.query(TheHammerContract.AuctionTable.CONTENT_URI,
                        new String[]{TheHammerContract.AuctionTable.AUCTION_START_PRICE,
                                    TheHammerContract.AuctionTable.AUCTION_START_DATE,
                                    TheHammerContract.AuctionTable.AUCTION_END_DATE},
                        TheHammerContract.AuctionTable.AUCTION_ITEM_ID + " = ?",
                        new String[]{String.valueOf(item_id)},
                        null);
                if (auction_cursor.moveToFirst()){
                    start_price = String.valueOf(auction_cursor.getDouble(0));
                    start_date = auction_cursor.getString(1);
                    end_date = auction_cursor.getString(2);
//                    ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_price, "Start Price:", String.valueOf(auction_cursor.getDouble(0)));
//                    ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_date, "Start Date:", auction_cursor.getString(1));
//                    ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.end_date, "End Date:", auction_cursor.getString(2));
                }
                auction_cursor.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_price, "Start Price:", start_price);
                ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_date, "Start Date:", start_date);
                ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.end_date, "End Date:", end_date);
            }
        };

        task.execute();
    }
}
