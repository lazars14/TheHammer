package com.example.kiki.thehammer.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.kiki.thehammer.helpers.DummyData;
import com.example.kiki.thehammer.model.Notification;
import com.example.kiki.thehammer.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lazar on 28/1/2018.
 */

public class NotificationService {

    private static final String NOTIFICATIONS_REFERENCE = "notifications";
    private DatabaseReference dbReference;
    private Context context;
    private SharedPreferences preferences;

    private static final String SEPARATOR = "|";
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public NotificationService(Context context){
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbReference = FirebaseDatabase.getInstance().getReference(NOTIFICATIONS_REFERENCE);
        client = new OkHttpClient();
    }

    public DatabaseReference getAllNotifications() {
        return dbReference;
    }

    public void registerUserWithToken(String id) {

        String token = FirebaseInstanceId.getInstance().getToken();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", id);
        editor.putString("notification_token", token);
        editor.apply();

        addNotification(id, token);
    }

    private void addNotification(String userId, String token){
        String id = dbReference.push().getKey();

        Notification notification = new Notification(id, userId, token);

        dbReference.child(id).setValue(notification);
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnap : dataSnapshot.getChildren()){
                    User user = userSnap.getValue(User.class);

                    if(user != null){
                        String token = FirebaseInstanceId.getInstance().getToken();

                        addNotification(user.getId(), token);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void sendNotification(final String token, final String message){
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("data", data);
                    root.put("to", token);

                    RequestBody body = RequestBody.create(JSON, root.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key=" + DummyData.LEGACY_SERVER_KEY)
                            .url(FCM_MESSAGE_URL)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    public String buildMessage(String item_id, String item_name, String item_description, String item_image) {

        StringBuilder value = new StringBuilder();
        value.append(item_id);
        value.append(SEPARATOR);
        value.append(item_name);
        value.append(SEPARATOR);
        value.append(item_description);
        value.append(SEPARATOR);
        value.append(item_image);

        return value.toString();
    }

}
