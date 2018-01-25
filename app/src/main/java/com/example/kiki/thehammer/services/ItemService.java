package com.example.kiki.thehammer.services;

import android.support.v7.widget.RecyclerView;

import com.example.kiki.thehammer.adapters.ItemsAdapter;
import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Lazar on 22/1/2018.
 */

public class ItemService {

    private static final String ITEMS_REFERENCE = "items";
    public static final Query ALL_ITEMS_QUERY = FirebaseDatabase.getInstance().getReference(ITEMS_REFERENCE).orderByChild("sold").equalTo(false);
    private DatabaseReference dbReference;
    private Item item = null;

    public ItemService(){
        // needed only here, because it must be set before using any instance
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        this.dbReference = FirebaseDatabase.getInstance().getReference(ITEMS_REFERENCE);
    }

    public void addItems(List<Item> items){

        for(int i = 0; i < 10; i++){
            String id = dbReference.push().getKey();
            Item item = new Item(id, DummyData.itemNames[i], DummyData.itemDescriptions[i], DummyData.item_images[i]);

            dbReference.child(id).setValue(item);

            items.add(item);
        }
    }

    public DatabaseReference getReferenceForItemById(String itemId){
        return dbReference.child(itemId);
    }

    // get all items here?

}
