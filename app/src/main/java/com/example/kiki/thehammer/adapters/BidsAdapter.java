package com.example.kiki.thehammer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.model.Bid;

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
        holder.bid = bids.get(position);
        holder.price.setText(String.valueOf(holder.bid.getPrice()));
        holder.date_time.setText(DateHelper.format.format(holder.bid.getDateTime()));
        holder.user_name.setText(holder.bid.getUser().getName());
    }

    @Override
    public int getItemCount() {
        return bids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Bid bid;
        private ImageView image;
        private TextView price;
        private TextView date_time;
        private TextView user_name;

        public ViewHolder(View itemView) {
            super(itemView);
            //image = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            date_time = itemView.findViewById(R.id.date_time);
            user_name = itemView.findViewById(R.id.user_name);
        }
    }
}
