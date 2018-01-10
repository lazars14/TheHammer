package com.example.kiki.thehammer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.activities.AuctionActivity;
import com.example.kiki.thehammer.activities.ItemActivity;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Item;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Kiki on 10-Jan-18.
 */

public class AuctionsAdapter extends RecyclerView.Adapter<AuctionsAdapter.ViewHolder> {

    private Context context;
    private List<Auction> auctions;

    public AuctionsAdapter(Context c, List<Auction> a){
        context = c;
        auctions = a;
    }

    @Override
    public AuctionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View auctionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_auction,parent,false);

        return new AuctionsAdapter.ViewHolder(auctionView);
    }

    @Override
    public void onBindViewHolder(AuctionsAdapter.ViewHolder holder, int position) {
        // set image
//        holder.item_name.setText(auctions.get(position).getItem().getName());
//        holder.item_description.setText(auctions.get(position).getItem().getDescription());
//        holder.start_price.setText(String.valueOf(auctions.get(position).getStartPrice()));
//        holder.auction = auctions.get(position);
        holder.auction = auctions.get(position);
        holder.item_name.setText(holder.auction.getItem().getName());
        holder.item_description.setText(holder.auction.getItem().getDescription());
        holder.start_price.setText("Start: " + holder.auction.getStartPrice());
        holder.end_date.setText("End: " + DateFormat.getDateTimeInstance().format(auctions.get(position).getEndDate()));
    }

    @Override
    public int getItemCount() {
        return auctions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Auction auction;
        private ImageView item_image;
        private TextView item_name;
        private TextView item_description;
        private TextView start_price;
        private TextView end_date;

        public ViewHolder(View auctionView) {
            super(auctionView);
            //item_image = (TextView) itemView.findViewById(R.id.item_image);
            item_name = (TextView) auctionView.findViewById(R.id.item_name);
            item_description = (TextView) auctionView.findViewById(R.id.item_description);
            start_price = (TextView) auctionView.findViewById(R.id.start_price);
            end_date = (TextView) auctionView.findViewById(R.id.end_date);
            auctionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AuctionActivity.class);
                    intent.putExtra("id", auction.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
