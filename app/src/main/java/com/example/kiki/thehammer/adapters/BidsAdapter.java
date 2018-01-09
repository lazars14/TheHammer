package com.example.kiki.thehammer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.activities.ItemActivity;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.Item;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Kiki on 08-Jan-18.
 */

public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.ViewHolder> {

    private Context context;
    private List<Bid> bids;

    public BidsAdapter(Context con, List<Bid> bidsList){
        context = con;
        bids = bidsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View bidView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_bid,parent,false);

        return new ViewHolder(bidView);
    }

    @Override
    public void onBindViewHolder(BidsAdapter.ViewHolder holder, int position) {
        // holder.image. set image (get from bid - User object in bid)
        holder.price.setText(String.valueOf(bids.get(position).getPrice()));
        holder.date_time.setText(DateFormat.getDateTimeInstance().format(bids.get(position).getDateTime()));
        holder.user_name.setText(bids.get(position).getUser().getName());
    }

    @Override
    public int getItemCount() {
        return bids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView price;
        private TextView date_time;
        private TextView user_name;

        public ViewHolder(View itemView) {
            super(itemView);
            //image = (ImageView) itemView.findViewById(R.id.description);
            price = (TextView) itemView.findViewById(R.id.price);
            date_time = (TextView) itemView.findViewById(R.id.date_time);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
        }
    }
}
