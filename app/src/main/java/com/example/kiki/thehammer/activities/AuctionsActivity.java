package com.example.kiki.thehammer.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.AuctionsAdapter;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.helpers.FilterHelper;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Bid;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.services.AuctionService;
import com.example.kiki.thehammer.services.BidService;
import com.example.kiki.thehammer.services.ItemService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AuctionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AuctionsAdapter adapter;
    private List<Auction> auctions = new ArrayList<>();

    private NavigationHelper navHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Spinner spinner;
    private EditText filter_text;
    private CheckBox filter_status;

    private FilterHelper filterHelper;

    private SharedPreferences preferences;
    private String user_id;

    private AuctionService auctionService = new AuctionService();
    private ItemService itemService = new ItemService();

    private static final Handler handler = new Handler();
    private final Runnable action = new Runnable() {
        @Override
        public void run() {
            // refresh data
            navHelper.checkIfPrefChanged();
            setRecyclerView();
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
        setContentView(R.layout.activity_auctions);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        handler.post(action);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_auctions);

        recyclerView = findViewById(R.id.recycler_view);

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        setRecyclerView();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHelper = new NavigationHelper(getApplicationContext(), navigationView);
        setSpinnerData();

        setRecyclerView();
        user_id = preferences.getString("user_id", DummyData.user_id);
        load_auctions_from_content_provider();
    }

    private void load_auctions_from_content_provider(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {
                BidService bidService = new BidService();
                Query query = bidService.getAllBidsDbReference();

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot bidSnapshot : dataSnapshot.getChildren()){
                            Bid bid = bidSnapshot.getValue(Bid.class);

                            if(bid != null){
                                if(bid.getUser().getId().equals(user_id)){
                                    Query q = auctionService.getAuctionById(bid.getAuction().getId());

                                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final Auction auction = dataSnapshot.getValue(Auction.class);

                                            if(auction != null){
                                                if(!auctions.contains(auction)){

                                                    Query itemQuery = itemService.getReferenceForItemById(auction.getItem().getId());

                                                    itemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Item item = dataSnapshot.getValue(Item.class);
                                                            auction.setItem(item);

                                                            auctions.add(auction);
                                                            adapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            Toast.makeText(getApplicationContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else Toast.makeText(getApplicationContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else Toast.makeText(getApplicationContext(), DummyData.FAILED_TO_LOAD_DATA, Toast.LENGTH_SHORT).show();
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

    public void setSpinnerData(){
        View filter_view = findViewById(R.id.auctions_filter);
        spinner = filter_view.findViewById(R.id.spinner);

        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("name");
        spinnerArray.add("description");
        spinnerArray.add("status");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spinner.getSelectedItem().toString();
                if(selectedItem.equals("status")){
                    filter_status.setEnabled(true);
                }else {
                    filter_status.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filter_text = filter_view.findViewById(R.id.filter_text);
        filter_status = filter_view.findViewById(R.id.auction_status);
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
            filterHelper = new FilterHelper(spinner.getSelectedItem().toString(), filter_text.getEditableText().toString(), auctions, filter_status.isChecked(), recyclerView, this);
        }
    }

    public void setRecyclerView(){
        adapter = new AuctionsAdapter(this, auctions);
        recyclerView.setAdapter(adapter);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == auctions.size()-1){
//                    load_data_from_content_provider(auctions.get(auctions.size() - 1).getId());
//                }
//
//            }
//        });
    }
}
