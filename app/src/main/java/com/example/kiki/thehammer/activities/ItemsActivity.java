package com.example.kiki.thehammer.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.ItemsAdapter;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.DataInitHelper;
import com.example.kiki.thehammer.helpers.DateHelper;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.helpers.FilterHelper;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.model.User;
import com.example.kiki.thehammer.services.AuctionService;
import com.example.kiki.thehammer.services.BidService;
import com.example.kiki.thehammer.services.ItemService;
import com.example.kiki.thehammer.services.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ItemsAdapter adapter;
    private List<Item> items = new ArrayList<>();

    private NavigationHelper navHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Spinner spinner;
    private EditText filter_text;

    private static final Handler handler = new Handler();
    private final Runnable action = new Runnable() {
        @Override
        public void run() {
            // refresh data
            navHelper.initUserInfo();
            setRecyclerView();
        }
    };

    private DatabaseReference ref;

    private FilterHelper filterHelper;

    @Override
    public void onResume(){
        super.onResume();

        handler.post(action);
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
        load_items_from_firebase();
//        DataInitHelper.initDummyData();
    }

    private void load_items_from_firebase() {

        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
//                Query reference = FirebaseDatabase.getInstance().getReference("items").orderByChild("name").equalTo(false, "sold").limitToFirst(3).startAt(0);

                Query reference = ItemService.ALL_ITEMS_QUERY;

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                            Item item = itemSnapshot.getValue(Item.class);

                            items.add(item);

                            adapter.notifyDataSetChanged();
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

    public void setSpinnerData(){
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
            navHelper.navigateTo(AuctionsActivity.class, this);
        } else if (id == R.id.settings) {
            navHelper.navigateTo(SettingsActivity.class, this);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {
        if(filter_text.getText().toString().equals("")){
            setRecyclerView();
        } else {
            filterHelper = new FilterHelper(spinner.getSelectedItem().toString(), filter_text.getEditableText().toString(), items, recyclerView, this);
        }

    }

    public void setRecyclerView(){
        adapter = new ItemsAdapter(this, items);
        recyclerView.setAdapter(adapter);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == items.size()-1){
//                    load_data_from_content_provider(items.get(items.size() - 1).getId());
//                }
//
//            }
//        });
    }


}
