package com.example.kiki.thehammer.activities;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.helpers.ImageHelper;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.helpers.ValuePairViewHelper;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.User;
import com.example.kiki.thehammer.services.BidService;
import com.example.kiki.thehammer.services.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuctionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View auction_info_view;
    private View owner_info_view;
    private double start_price;
    private double max_price;
    private String auction_id;
    private String item_id;
    private Date end_date;

    private SharedPreferences preferences;

    private NavigationHelper navHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private static final Handler handler = new Handler();
    private final Runnable action = new Runnable() {
        @Override
        public void run() {
            navHelper.initUserInfo();
        }
    };

    @Override
    public void onResume(){
        super.onResume();

        handler.post(action);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_auction);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

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
            auction_id = bundle.getString("auction_id");
            item_id = bundle.getString("item_id");

            View item_info_view = findViewById(R.id.item_info);
            TextView name = item_info_view.findViewById(R.id.name);
            TextView description = item_info_view.findViewById(R.id.description);
            ImageView imageView = item_info_view.findViewById(R.id.image);
            name.setText(bundle.getString("item_name"));
            description.setText(bundle.getString("item_description"));
            ImageHelper.loadImage(bundle.getString("item_image"), getApplicationContext(), imageView, 0);

            auction_info_view = findViewById(R.id.auction_info);
            ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_price, "Start Price:", String.valueOf(bundle.getDouble("auction_start_price")));
            ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.start_date, "Start Date:", bundle.getString("auction_start_date"));
            ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.end_date, "End Date:", bundle.getString("auction_end_date"));

            end_date = DateHelper.stringToDate(bundle.getString("auction_end_date"));
            start_price = bundle.getDouble("auction_start_price");
            max_price = start_price;

            boolean auctionEnded = DateHelper.auctionEnded(end_date);
            if(auctionEnded){
                // auction over, if user won it display owner info
//                load_auction_winner(auction_id, item_id);
            } else {
                // auction still in progress, display current price
                load_current_price();
            }
        }
    }

    private void load_auction_winner(String auction_id, String item_id){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                BidService bidService = new BidService();
                Query query = bidService.getHighestBidQuery();

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Bid bid = dataSnapshot.getValue(Bid.class);

                        String user_id = preferences.getString("user_id", DummyData.user_id);

                        if(user_id.equals(bid.getUser().getId())){
                            UserService userService = new UserService();

                            Query userQuery = userService.getUserById(bid.getUser().getId());

                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);

                                    setOwnerInfo(user.getEmail());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return null;
            }
        };

        task.execute();
    }

    private void load_current_price(){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                BidService bidService = new BidService();
                DatabaseReference reference = bidService.getAllBidsDbReference();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot bidSnapshot : dataSnapshot.getChildren()){
                            Bid bid = bidSnapshot.getValue(Bid.class);
                            if(bid.getAuction().getId().equals(auction_id) && end_date.after(DateHelper.stringToDate(bid.getDateTime())) && bid.getPrice() > start_price){
                                max_price = bid.getPrice();
                            }
                        }

                        setCurrentPrice();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return null;
            }
        };

        task.execute();
    }

    private void setCurrentPrice(){
        View current_price_view = auction_info_view.findViewById(R.id.current_price);
        current_price_view.setVisibility(View.VISIBLE);
        ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.current_price, "Current price:", String.valueOf(max_price));
    }

    private void setOwnerInfo(String email){
        owner_info_view = auction_info_view.findViewById(R.id.owner_info);
        owner_info_view.setVisibility(View.VISIBLE);
        ValuePairViewHelper.setLabelValuePair(auction_info_view, R.id.owner_info, "Owner email: ", email);
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
