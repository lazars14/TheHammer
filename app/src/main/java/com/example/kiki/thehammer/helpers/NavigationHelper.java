package com.example.kiki.thehammer.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.kiki.thehammer.R;

/**
 * Created by Kiki on 27-Dec-17.
 */

public class NavigationHelper {

    private Context appContext;
    private NavigationView navigationView;
    private TextView firstAndLastname;
    private TextView email;
    private TextView address;

    public NavigationHelper(Context applicationContext, NavigationView navigationView){
        this.appContext = applicationContext;
        this.navigationView = navigationView;
        getViewsForUserInfo();
        initUserInfo();
    }

    private void getViewsForUserInfo(){
        View headerView = navigationView.getHeaderView(0);
        // image to do
        firstAndLastname = headerView.findViewById(R.id.first_and_last_name);
        email = headerView.findViewById(R.id.email);
        address = headerView.findViewById(R.id.address);
    }

    public void initUserInfo(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);

        String firstAndLastnameText = preferences.getString("firstname", "John") + " " + preferences.getString("lastname", "Doe");

        firstAndLastname.setText(firstAndLastnameText);
        email.setText(preferences.getString("email", "johndoe@gmail.com"));
        address.setText(preferences.getString("address", "Boulevard 123"));
    }

    public void navigateTo(Class activity, AppCompatActivity currentActivity){
        Intent intent = new Intent(appContext, activity);
        currentActivity.startActivity(intent);
    }
}
