package com.example.kiki.thehammer.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.kiki.thehammer.R;
import com.example.kiki.thehammer.adapters.AuctionsAdapter;
import com.example.kiki.thehammer.adapters.ItemsAdapter;
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.model.Auction;
import com.example.kiki.thehammer.model.Item;
import com.example.kiki.thehammer.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuctionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AuctionsAdapter adapter;
    private List<Auction> auctions = new ArrayList<>();
    private final SimpleDateFormat format = new SimpleDateFormat("DD/mm/yyyy hh:mm");

    private NavigationHelper navHelper;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Spinner spinner;
    private EditText filter_text;
    private CheckBox filter_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_auctions);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        load_data_from_content_provider(0);

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new AuctionsAdapter(this, auctions);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == auctions.size()-1){
                    load_data_from_content_provider(auctions.get(auctions.size() - 1).getId());
                }

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHelper = new NavigationHelper(getApplicationContext(), navigationView);
        setSpinnerData();
    }

    private void load_data_from_content_provider(int id){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                String[] projection = new String[]{TheHammerContract.AuctionTable.AUCTION_ID,
                                                    TheHammerContract.AuctionTable.AUCTION_START_PRICE,
                                                    TheHammerContract.AuctionTable.AUCTION_START_DATE,
                                                    TheHammerContract.AuctionTable.AUCTION_END_DATE,
                                                    TheHammerContract.AuctionTable.AUCTION_ITEM_ID};
                String selection = TheHammerContract.ItemTable.ITEM_ID + " BETWEEN ? AND ?";
                String[] selectionArgs = new String[]{ String.valueOf(integers[0] + 1), String.valueOf(integers[0] + 6)};
                ContentResolver resolver = getContentResolver();
                Cursor cursor =
                        resolver.query(TheHammerContract.AuctionTable.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                null);
                if (cursor.moveToFirst()) {
                    do {
                        int auction_id = cursor.getInt(0);
                        Double start_price = cursor.getDouble(1);
                        String start_date_str = cursor.getString(2);
                        String end_date_str = cursor.getString(3);
                        int item_id = cursor.getInt(4);

                        Date start_date = null;
                        Date end_date = null;
                        Item item = null;
                        try {
                            start_date = format.parse(start_date_str);
                            end_date = format.parse(end_date_str);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Cursor item_cursor =
                                resolver.query(TheHammerContract.ItemTable.CONTENT_URI,
                                        new String[]{TheHammerContract.ItemTable.PROJECTION[1],
                                                TheHammerContract.ItemTable.PROJECTION[2],
                                                TheHammerContract.ItemTable.PROJECTION[3]},
                                        TheHammerContract.ItemTable.ITEM_ID + " = ?",
                                        new String[]{String.valueOf(item_id)},
                                        null);
                        if(item_cursor.moveToFirst()){
                            String item_name = cursor.getString(0);
                            String item_description = cursor.getString(1);
                            String item_picture = cursor.getString(2);

                            item = new Item(item_id, item_name, item_description, item_picture);
                        }
                        Auction auction = new Auction(auction_id, start_price, start_date, end_date, item);
                        auctions.add(auction);

                        item_cursor.close();

                    } while (cursor.moveToNext());

                    cursor.close();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
        };

        task.execute(id);
    }

    public void setSpinnerData(){
        View filter_view = (View) findViewById(R.id.auctions_filter);
        spinner = (Spinner) filter_view.findViewById(R.id.spinner);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("name");
        spinnerArray.add("description");
        spinnerArray.add("status");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
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

//        spinner.setOnItemClickListener();

        filter_text = (EditText) filter_view.findViewById(R.id.filter_text);
        filter_status = (CheckBox) filter_view.findViewById(R.id.auction_status);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.auctions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
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

        } else if (id == R.id.settings) {
            navHelper.navigateTo(SettingsActivity.class, this);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        String selected = spinner.getSelectedItem().toString();
        String text = filter_text.getEditableText().toString();

        if (selected.equals("name")) {

        } else if(selected.equals("description")){

        } else if(selected.equals("status")){
            boolean status = filter_status.isChecked();
        }
    }
}
