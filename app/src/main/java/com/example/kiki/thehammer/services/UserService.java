package com.example.kiki.thehammer.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

/**
 * Created by Lazar on 22/1/2018.
 */

public class UserService {

    private static final String USERS_REFERENCE = "users";
    private DatabaseReference dbReference;

    public UserService(){
        this.dbReference = FirebaseDatabase.getInstance().getReference(USERS_REFERENCE);
    }

    public void addUsers(List<User> users){

        for(int i = 0; i < 10; i++){
            String id = dbReference.push().getKey();
            User user = new User(id, DummyData.user_firstnames[i] + " " + DummyData.user_lastnames[i], DummyData.user_firstnames[i] + DummyData.user_lastnames[i] + DummyData.email_end,
                    DummyData.default_password + i, DummyData.user_images[i], DummyData.default_address + i, DummyData.default_phone_num + i);

            dbReference.child(id).setValue(user);

            users.add(user);
        }

        String id = dbReference.push().getKey();
        User u = new User(id, DummyData.user_firstnames[10] + " " + DummyData.user_lastnames[10], DummyData.user_firstnames[10] + DummyData.user_lastnames[10] + DummyData.email_end, DummyData.default_password, DummyData.user_images[10], DummyData.default_address + " 123", DummyData.default_phone_num + "7");

        dbReference.child(id).setValue(u);
        users.add(u);
    }

    public String addUser(){
        String id = dbReference.push().getKey();
        User u = new User(id, DummyData.user_firstnames[10] + " " + DummyData.user_lastnames[10], DummyData.user_firstnames[10] + DummyData.user_lastnames[10] + DummyData.email_end,
                DummyData.default_password, DummyData.user_images[10], DummyData.default_address + " 123", DummyData.default_phone_num + "7");

        dbReference.child(id).setValue(u);

        return id;
    }

    public void updateUser(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String id = preferences.getString("user_id", DummyData.user_id);
        String firstname = preferences.getString("firstname", DummyData.user_firstnames[10]);
        String lastname = preferences.getString("lastname", DummyData.user_lastnames[10]);
        String email = preferences.getString("email", firstname + lastname + DummyData.email_end);
        String password = preferences.getString("password", DummyData.default_password + 10);
        String address = preferences.getString("address", DummyData.default_address + " 123");
        String phone = preferences.getString("phone", DummyData.default_phone_num + "7");

        User user = new User(id, firstname + " " + lastname, email, password, DummyData.user_images[10], address, phone);

        dbReference.child(id).setValue(user);
    }

    public Query getUserById(String userId){
        return dbReference.child(userId);
    }
}
