package com.example.kiki.thehammer.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.helpers.ValuePairViewHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuctionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final SimpleDateFormat format = new SimpleDateFormat("DD/mm/yyyy hh:mm");
    private View auction_info_view;
    private View owner_info_view;
    private double start_price;
    private double max_price;

    private NavigationHelper navHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    public void onResume(){
        super.onResume();

        navHelper.initUserInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_auction);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHelper = new NavigationHelper(getApplicationContext(), navigationView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int auction_id = bundle.getInt("auction_id");
            int item_id = bundle.getInt("item_id");

            View item_info_view = findViewById(R.id.item_info);
            TextView name = item_info_view.findViewById(R.id.name);
            TextView description = item_info_view.findViewById(R.id.description);
            // image
            name.setText(bundle.getString("item_name"));
            description.setText(bundle.getString("item_description"));

            auction_info_view = findViewById(R.id.auction_info);
            ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_price, "Start Price:", String.valueOf(bundle.getDouble("auction_start_price")));
            ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_date, "Start Date:", bundle.getString("auction_start_date"));
            ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.end_date, "End Date:", bundle.getString("auction_end_date"));

            start_price = bundle.getDouble("auction_start_price");

            Date endDate = DateHelper.stringToDate(bundle.getString("auction_end_date"));
            Date now = new Date();
            if(now.after(endDate)){
                // auction over, if user won it display owner info
                load_auction_winner(auction_id, item_id);
            } else {
                // auction still in progress, display current price
                load_current_price(auction_id);
            }
        }
    }

    private void load_auction_winner(int auction_id, int item_id){
        // to do
    }

    private void load_current_price(int auction_id){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(TheHammerContract.BidTable.CONTENT_URI,
                        new String[]{TheHammerContract.BidTable.BID_PRICE},
                        TheHammerContract.BidTable.BID_AUCTION_ID + " = ?",
                        new String[]{String.valueOf(integers[0])},
                        TheHammerContract.BidTable.BID_PRICE + " DESC LIMIT 1");
                max_price = start_price;
                if (cursor.moveToFirst()){
                    max_price = cursor.getDouble(0);
                }

                cursor.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                View current_price_view = auction_info_view.findViewById(R.id.current_price);
                current_price_view.setVisibility(View.VISIBLE);
                ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.current_price, "Current price:", String.valueOf(max_price));
            }
        };

        task.execute(auction_id);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.items) {
            navHelper.navigateTo(ItemsActivity.class, this);
        } else if (id == R.id.auctions) {
            finish();
        } else if (id == R.id.settings) {
            navHelper.navigateTo(SettingsActivity.class, this);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
