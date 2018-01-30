package com.example.kiki.thehammer.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.helpers.ImageHelper;
import com.example.kiki.thehammer.helpers.ValuePairViewHelper;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.services.AuctionService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ItemAuctionFragment extends Fragment {

    private View auction_info_view;
    private String item_id;

    public ItemAuctionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_auction_fragment, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            String item_image = bundle.getString("image");
            View item_info_view = v.findViewById(R.id.item_info);
            TextView name = item_info_view.findViewById(R.id.name);
            TextView description = item_info_view.findViewById(R.id.description);
            ImageView imageView = item_info_view.findViewById(R.id.image);

            name.setText(bundle.getString("name"));
            description.setText(bundle.getString("description"));
            ImageHelper.loadImage(item_image, getContext(), imageView, 0);
            auction_info_view = v.findViewById(R.id.auction_info);

            item_id = bundle.getString("id");
            load_auction_info();
        }

        return v;
    }

    private void load_auction_info() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                Query query = AuctionService.ALL_AUCTIONS_QUERY;
                final Date now = new Date();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot auctionSnapshot : dataSnapshot.getChildren()){
                            Auction auction = auctionSnapshot.getValue(Auction.class);

                            if(auction != null){
                                if(auction.getItem().getId().equals(item_id) && auction.getEndDate().after(now)){
                                    setAuctionInfo(auction);
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

    private void setAuctionInfo(Auction auction){
        ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_price, "Start Price:", String.valueOf(auction.getStartPrice()));
        ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_date, "Start Date:", DateHelper.dateToString(auction.getStartDate()));
        ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.end_date, "End Date:", DateHelper.dateToString(auction.getEndDate()));
    }
}
