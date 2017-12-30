package com.example.kiki.thehammer.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemAuctionFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemAuctionFragment() {
        // Required empty public constructor
    }

    public static ItemAuctionFragment newInstance(String desc){
        ItemAuctionFragment fragment = new ItemAuctionFragment();

        Bundle arguments = new Bundle();
//        arguments.putString("ITEM_NAME", auction.getItem().getName());
//        arguments.putString("ITEM_DESCRIPTION", auction.getItem().getDescription());
//        arguments.putString("ITEM_PICTURE", auction.getItem().getPicture());
//
//        arguments.putDouble("AUCTION_START_PRICE", auction.getStartPrice());
//        arguments.putString("AUCTION_START_DATE", auction.getStartDate().toString());
//        arguments.putString("AUCTION_END_DATE", auction.getEndDate().toString());
//
//        arguments.putString("AUCTION_USER_NAME", auction.getUser().getName());
//        arguments.putString("AUCTION_USER_PICTURE", auction.getUser().getPicture());
        arguments.putString("ITEM_DESCRIPTION", desc);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.item_auction_fragment, container);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
//            String item_name = bundle.getString("ITEM_NAME");
            String item_description = bundle.getString("ITEM_DESCRIPTION");
//            String item_picture = bundle.getString("ITEM_PICTURE");
//
            Log.d("item_description", item_description);
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
//            double auction_start_price = bundle.getDouble("AUCTION_START_PRICE");
//            Date auction_start_date = null; Date auction_end_date = null;
//
//            try
//            {
//                auction_start_date = dateFormat.parse(bundle.getString("AUCTION_START_DATE"));
//                auction_end_date = dateFormat.parse(bundle.getString("AUCTION_END_DATE"));
//            }
//            catch (ParseException e)
//            {
//                e.printStackTrace();
//            }
//
//            String auction_user_name = bundle.getString("AUCTION_USER_NAME");
//            String auction_user_picture = bundle.getString("AUCTION_USER_PICTURE");
        }


        return v;
    }

}
