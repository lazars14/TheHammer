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
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.ImageHelper;
import com.example.kiki.thehammer.model.Auction;

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
        holder.auction = auctions.get(position);
        ImageHelper.loadImage(holder.auction.getItem().getPicture(), context, holder.item_image, 0);
        holder.item_name.setText(holder.auction.getItem().getName());
        if(holder.auction.getItem().getDescription().length() > 22) holder.item_description.setText(holder.auction.getItem().getDescription().substring(0, 20) + "...");
        else holder.item_description.setText(holder.auction.getItem().getDescription());
        holder.start_price.setText("Start price: " + holder.auction.getStartPrice());
        holder.end_date.setText("End: " + DateHelper.calculateRemainingTime(holder.auction.getEndDate()));
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
            item_image = itemView.findViewById(R.id.image);
            item_name = auctionView.findViewById(R.id.item_name);
            item_description = auctionView.findViewById(R.id.item_description);
            start_price = auctionView.findViewById(R.id.start_price);
            end_date = auctionView.findViewById(R.id.end_date);
            auctionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AuctionActivity.class);
                    intent.putExtra("auction_id", auction.getId());
                    intent.putExtra("auction_start_price", auction.getStartPrice());
                    intent.putExtra("auction_start_date", auction.getStartDate().getTime());
                    intent.putExtra("auction_end_date", auction.getEndDate().getTime());
                    intent.putExtra("item_id", auction.getItem().getId());
                    intent.putExtra("item_name", auction.getItem().getName());
                    intent.putExtra("item_description", auction.getItem().getDescription());
                    intent.putExtra("item_image", auction.getItem().getPicture());
                    context.startActivity(intent);
                }
            });
        }
    }
}
