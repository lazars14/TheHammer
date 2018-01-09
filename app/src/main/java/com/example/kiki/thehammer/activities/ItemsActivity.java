package com.example.kiki.thehammer.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.example.kiki.thehammer.data.TheHammerContract;
import com.example.kiki.thehammer.helpers.NavigationHelper;
import com.example.kiki.thehammer.model.Item;

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

    @Override
    public void onResume(){
        super.onResume();
        //refreshItems();
    }

    private void refreshItems(){
        items = new ArrayList<>();
        load_data_from_content_provider(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activity_items);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHelper = new NavigationHelper(getApplicationContext(), navigationView);
        setSpinnerData();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        load_data_from_content_provider(0);

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new ItemsAdapter(this, items);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == items.size()-1){
                    load_data_from_content_provider(items.get(items.size() - 1).getId());
                }

            }
        });
    }

    private void load_data_from_content_provider(int id) {

        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                String selection = TheHammerContract.ItemTable.ITEM_ID + " BETWEEN ? AND ? AND " + TheHammerContract.ItemTable.ITEM_SOLD + " = 0";
                String[] selectionArgs = new String[]{ String.valueOf(integers[0] + 1), String.valueOf(integers[0] + 6)};
                ContentResolver resolver = getContentResolver();
                Cursor cursor =
                        resolver.query(TheHammerContract.ItemTable.CONTENT_URI,
                                null,
                                selection,
                                 selectionArgs,
                                null);
                if (cursor.moveToFirst()) {
                    do {
                        int itemId = cursor.getInt(0);
                        String name = cursor.getString(1);
                        String description = cursor.getString(2);
                        String image = cursor.getString(3);

                        Item item = new Item(itemId, name, description, image);
                        items.add(item);

                    } while (cursor.moveToNext());
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
        View filter_view = (View) findViewById(R.id.items_filter);
        spinner = (Spinner) filter_view.findViewById(R.id.spinner);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("name");
        spinnerArray.add("description");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        filter_text = (EditText) filter_view.findViewById(R.id.filter_text);
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
        // getMenuInflater().inflate(R.menu.item, menu);
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
        String selected = spinner.getSelectedItem().toString();
        String text = filter_text.getEditableText().toString();
        if (selected.equals("name")) {

        } else if(selected.equals("description")){

        }
    }
}
