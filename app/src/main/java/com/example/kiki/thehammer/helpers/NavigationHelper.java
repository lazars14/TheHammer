package com.example.kiki.thehammer.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.services.UserService;

/**
 * Created by Kiki on 27-Dec-17.
 */

public class NavigationHelper {

    private Context appContext;
    private NavigationView navigationView;
    private ImageView image;
    private TextView firstAndLastname;
    private TextView email;
    private TextView address;

    private String not_changed = "not_changed";

    public NavigationHelper(Context applicationContext, NavigationView navigationView){
        this.appContext = applicationContext;
        this.navigationView = navigationView;
        getViewsForUserInfo();
        initUserInfo(PreferenceManager.getDefaultSharedPreferences(appContext));
    }

    private void getViewsForUserInfo(){
        View headerView = navigationView.getHeaderView(0);
        image = headerView.findViewById(R.id.imageView);
        firstAndLastname = headerView.findViewById(R.id.first_and_last_name);
        email = headerView.findViewById(R.id.email);
        address = headerView.findViewById(R.id.address);
    }

    public void initUserInfo(SharedPreferences preferences){
        String firstAndLastnameText = preferences.getString("firstname", "John") + " " + preferences.getString("lastname", "Doe");

        String url = preferences.getString("image", not_changed);
        if(url.equals(not_changed)){
            url = String.valueOf(R.drawable.default_user_image);
        }
        ImageHelper.loadImage(url, appContext, image, 1);

        firstAndLastname.setText(firstAndLastnameText);
        email.setText(preferences.getString("email", "johndoe@gmail.com"));
        address.setText(preferences.getString("address", "Boulevard 123"));
    }

    public void navigateTo(Class activity, AppCompatActivity currentActivity){
        Intent intent = new Intent(appContext, activity);
        currentActivity.startActivity(intent);
    }

    public void checkIfPrefChanged(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        boolean somethingChanged = preferences.getBoolean("something_changed", false);
        if(somethingChanged) {
            UserService userService = new UserService();
            userService.updateUser(appContext);
            initUserInfo(preferences);
        }
    }
}
