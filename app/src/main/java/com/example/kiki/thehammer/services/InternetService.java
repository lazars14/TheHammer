package com.example.kiki.thehammer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Lazar on 1/2/2018.
 */

public class InternetService extends BroadcastReceiver {

    public InternetService(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        // check if there is connection
        // then if there is, refresh data
    }
}
