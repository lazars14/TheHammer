package com.example.kiki.thehammer.services;

import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.Item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

/**
 * Created by Lazar on 22/1/2018.
 */

public class ItemService {

    private static final String ITEMS_REFERENCE = "items";
    public static final Query ALL_ITEMS_QUERY = FirebaseDatabase.getInstance().getReference(ITEMS_REFERENCE).orderByChild("sold").equalTo(false);
    private DatabaseReference dbReference;

    public ItemService(){
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

}
