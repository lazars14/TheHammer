package com.example.kiki.thehammer.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.example.kiki.thehammer.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Lazar on 22/1/2018.
 */

public class ImageHelper {

    // 0 for item, 1 for user
    public static int[] fallback_images = new int[]{R.drawable.default_auction_item, R.drawable.default_user_image};

    public static void loadImage(String url, final Context context, final ImageView iv, final int fallbackImageId){
        Picasso.with(context).load(url).into(iv, new Callback(){
            @Override
            public void onSuccess() {}

            @Override
            public void onError() {

                Picasso.with(context).load(fallback_images[fallbackImageId]).into(iv);
            }
        });
    }

}
