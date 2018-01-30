package com.example.kiki.thehammer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.activities.ItemActivity;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Lazar on 28/1/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message) {
        Intent i = new Intent(this,ItemActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String[] itemInfo = message.split("\\|");

        i.putExtra("id", itemInfo[0]);
        i.putExtra("name", itemInfo[1]);
        i.putExtra("description", itemInfo[2]);
        i.putExtra("image", itemInfo[3]);

        i.putExtra("bids", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("The Hammer")
                .setContentText("Your bid for item " + itemInfo[1] + " has been passed!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }

}
