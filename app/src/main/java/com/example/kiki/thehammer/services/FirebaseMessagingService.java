package com.example.kiki.thehammer.services;

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


    }

}
