package com.example.kiki.thehammer.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.ItemsAdapter;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.helpers.FilterHelper;
import com.example.kiki.thehammer.helpers.InternetHelper;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.services.InternetService;
import com.example.kiki.thehammer.services.ItemService;
import com.example.kiki.thehammer.services.NotificationService;
import com.example.kiki.thehammer.services.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ItemsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ItemsAdapter adapter;
    private List<Item> items = new ArrayList<>();
    private UserService userService;

    private NavigationHelper navHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Spinner spinner;
    private EditText filter_text;

    private static final Handler handler = new Handler();
    private final Runnable action = new Runnable() {
        @Override
        public void run() {
            navHelper.checkIfPrefChanged();
            setRecyclerView();
        }
    };

    private FilterHelper filterHelper;

    private InternetService internetService = new InternetService(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                if(!activity_paused){
                    boolean noConnectivity = intent.getBooleanExtra(
                            ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                    if (!noConnectivity) {
                        load_items_from_firebase();
                        handler.post(action);
                    } else {
                        Toast.makeText(getApplicationContext(), DummyData.TURN_ON_INTERNET, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private boolean activity_paused;

    @Override
    public void onResume(){
        super.onResume();

        activity_paused = false;
        handler.post(action);
    }

    @Override
    protected void onPause() {
        super.onPause();

        activity_paused = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_items);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHelper = new NavigationHelper(getApplicationContext(), navigationView);
        setSpinnerData();

        recyclerView = findViewById(R.id.recycler_view);

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        setRecyclerView();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetService, filter);

        if(InternetHelper.isNetworkAvailable(this)){
            load_items_from_firebase();
            checkIfRegistered();
        } else {
            Toast.makeText(this, DummyData.TURN_ON_INTERNET_DATA, Toast.LENGTH_SHORT).show();
        }

    }

    private void checkIfRegistered(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString("notification_token", DummyData.NOT_REGISTERED).equals(DummyData.NOT_REGISTERED)){
            userService = new UserService();
            String id = userService.addUser();

            NotificationService notificationService = new NotificationService(getApplicationContext());
            notificationService.registerUserWithToken(id);
        }
    }

    private void load_items_from_firebase() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {

                Query reference = ItemService.ALL_ITEMS_QUERY;

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                            Item item = itemSnapshot.getValue(Item.class);

                            if(!checkIfExists(item.getId())){
                                items.add(item);

                                adapter.notifyDataSetChanged();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
            }
        };

        task.execute();
    }

    private boolean checkIfExists(String itemId){
        for(Item i : items){
            if(i.getId().equals(itemId)){
                return true;
            }
        }

        return false;
    }

    private void setSpinnerData(){
        View filter_view = findViewById(R.id.items_filter);
        spinner = filter_view.findViewById(R.id.spinner);

        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("name");
        spinnerArray.add("description");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        filter_text = filter_view.findViewById(R.id.filter_text);
        filter_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    sleep(500);
                    if(filter_text.getText().toString().equals("")){
                        setRecyclerView();
                    } else {
                        filterHelper = new FilterHelper(spinner.getSelectedItem().toString(), filter_text.getEditableText().toString(), items, recyclerView, getApplicationContext());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
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

        } else if (id == R.id.auctions) {
            navHelper.navigateTo(AuctionsActivity.class, this);
        } else if (id == R.id.settings) {
            navHelper.navigateTo(SettingsActivity.class, this);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setRecyclerView(){
        adapter = new ItemsAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

}
