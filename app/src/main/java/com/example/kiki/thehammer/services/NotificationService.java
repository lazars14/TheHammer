package com.example.kiki.thehammer.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Lazar on 28/1/2018.
 */

public class NotificationService {

    private static final String NOTIFICATIONS_REFERENCE = "notifications";
    private DatabaseReference dbReference;
    private Context context;
    private SharedPreferences preferences;

    public NotificationService(Context context, SharedPreferences preferences){
        this.context = context;
        this.preferences = preferences;
        dbReference = FirebaseDatabase.getInstance().getReference(NOTIFICATIONS_REFERENCE);
    }


    public void registerUser(String id) {

        String token = registerToken();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", id);
        editor.putString("notification_token", token);
        editor.commit();
    }

    public String registerToken(){
        String token = FirebaseInstanceId.getInstance().getToken();

        // register user

        return token;
    }

}
