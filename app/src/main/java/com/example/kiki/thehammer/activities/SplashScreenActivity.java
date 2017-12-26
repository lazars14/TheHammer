package com.example.kiki.thehammer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kiki.thehammer.R;

import static java.lang.Thread.sleep;

public class SplashScreenActivity extends AppCompatActivity {

    private int visibilityTime;
    private boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        visibilityTime = preferences.getInt("splash_screen_visibility", 2000);
        visible = preferences.getBoolean("splash_screen_show", true);

        Thread thread = new Thread(){
            @Override
            public void run() {
                checkIfVisible();
                setSplashScreenVisibility(visibilityTime);
                Intent intent = new Intent(getApplicationContext(), ItemsActivity.class);
                startActivity(intent);
            }
        };
        thread.start();
    }

    public void setSplashScreenVisibility(int visibleInvisible)
    {
        try {
            sleep(visibleInvisible);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkIfVisible(){
        if(visible){
            setContentView(R.layout.splash_screen_activity);
        } else {
            visibilityTime = 0;
        }
    }
}