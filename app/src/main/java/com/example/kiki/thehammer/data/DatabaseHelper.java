package com.example.kiki.thehammer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lazar on 6/7/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = TheHammerContract.DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String USER = TheHammerContract.UserTable.USER;
    private static final String USER_ID = TheHammerContract.UserTable.USER_ID; // int
    private static final String USER_NAME = TheHammerContract.UserTable.USER_NAME;
    private static final String USER_EMAIL = TheHammerContract.UserTable.USER_EMAIL;
    private static final String USER_PASSWORD = TheHammerContract.UserTable.USER_PASSWORD;
    private static final String USER_PICTURE = TheHammerContract.UserTable.USER_PICTURE;
    private static final String USER_ADDRESS = TheHammerContract.UserTable.USER_ADDRESS; // Address
    private static final String USER_PHONE = TheHammerContract.UserTable.USER_PHONE;

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + USER + " (" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_NAME + " TEXT, " +
            USER_EMAIL + " TEXT, " +
            USER_PASSWORD + " TEXT, " +
            USER_PICTURE + " TEXT, " +
            USER_ADDRESS + " TEXT, " +
            USER_PHONE + " TEXT);";



    private static final String AUCTION = TheHammerContract.AuctionTable.AUCTION;
    private static final String AUCTION_ID = TheHammerContract.AuctionTable.AUCTION_ID; // int
    private static final String AUCTION_START_PRICE = TheHammerContract.AuctionTable.AUCTION_START_PRICE;
    private static final String AUCTION_START_DATE = TheHammerContract.AuctionTable.AUCTION_START_DATE;
    private static final String AUCTION_END_DATE = TheHammerContract.AuctionTable.AUCTION_END_DATE;
    private static final String AUCTION_USER_ID = TheHammerContract.AuctionTable.AUCTION_USER_ID;
    private static final String AUCTION_ITEM_ID = TheHammerContract.AuctionTable.AUCTION_ITEM_ID;

    private static final String CREATE_TABLE_AUCTION = "CREATE TABLE " + AUCTION + " (" + AUCTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            AUCTION_START_PRICE + " REAL, " +
            AUCTION_START_DATE + " TEXT, " +
            AUCTION_END_DATE + " TEXT, " +
            AUCTION_USER_ID + " INTEGER, " +
            AUCTION_ITEM_ID + " INTEGER);";



    private static final String BID = TheHammerContract.BidTable.BID;
    private static final String BID_ID = TheHammerContract.BidTable.BID_ID; // int
    private static final String BID_PRICE = TheHammerContract.BidTable.BID_PRICE;
    private static final String BID_DATE_TIME = TheHammerContract.BidTable.BID_DATE_TIME;
    private static final String BID_AUCTION_ID = TheHammerContract.BidTable.BID_AUCTION_ID;
    private static final String BID_USER_ID = TheHammerContract.BidTable.BID_USER_ID;

    private static final String CREATE_TABLE_BID = "CREATE TABLE " + BID + " (" + BID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BID_PRICE + " REAL, " +
            BID_DATE_TIME + " TEXT, " +
            BID_AUCTION_ID + " INTEGER, " +
            BID_USER_ID + " INTEGER);";



    private static final String ITEM = TheHammerContract.ItemTable.ITEM;
    private static final String ITEM_ID = TheHammerContract.ItemTable.ITEM_ID; // int
    private static final String ITEM_NAME = TheHammerContract.ItemTable.ITEM_NAME;
    private static final String ITEM_DESCRIPTION = TheHammerContract.ItemTable.ITEM_DESCRIPTION;
    private static final String ITEM_PICTURE = TheHammerContract.ItemTable.ITEM_PICTURE;
    private static final String ITEM_SOLD = TheHammerContract.ItemTable.ITEM_SOLD;

    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " + ITEM + " (" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ITEM_NAME + " TEXT, " +
            ITEM_DESCRIPTION + " TEXT, " +
            ITEM_PICTURE + " TEXT, " +
            ITEM_SOLD + " INTEGER);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_AUCTION);
        db.execSQL(CREATE_TABLE_BID);

        ContentValues contentValues = new ContentValues();

        insertUsers(contentValues, db);
        insertItems(contentValues, db);
        insertAuctions(contentValues, db);
        insertBids(contentValues, db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database updated", "to another version");
    }

    private void insertUsers(ContentValues contentValues, SQLiteDatabase db) {
        for(int i = 0; i < 30; i++){
            contentValues.put(USER_NAME, "name" + i);
            contentValues.put(USER_EMAIL, "email" + i);
            contentValues.put(USER_PASSWORD, "password" + i);
            contentValues.put(USER_PICTURE, "user_picture" + i);
            contentValues.put(USER_ADDRESS, "address" + i);
            contentValues.put(USER_PHONE, "phone" + i);

            db.insert(USER, null, contentValues);

            contentValues.clear();
        }
    }

    private void insertItems(ContentValues contentValues, SQLiteDatabase db) {
        for(int i = 0; i < 30; i++){
            contentValues.put(ITEM_NAME, "item" + i);
            contentValues.put(ITEM_DESCRIPTION, "description" + i);
            contentValues.put(ITEM_PICTURE, "item_picture" + i);
            contentValues.put(ITEM_SOLD, 0);

            db.insert(ITEM, null, contentValues);

            contentValues.clear();
        }
    }

    private void insertAuctions(ContentValues contentValues, SQLiteDatabase db) {
        // date format for sqlLite "YYYY-MM-DD HH:MM:SS.SSS"
        for(int i = 0; i < 30; i++){
            contentValues.put(AUCTION_START_PRICE, 1000);
            contentValues.put(AUCTION_START_DATE, "2017-06-01 00:00:00.000");
            contentValues.put(AUCTION_END_DATE, "2017-06-21 00:00:00.000");
            contentValues.put(AUCTION_USER_ID, 0);
            contentValues.put(AUCTION_ITEM_ID, i + 1);

            db.insert(AUCTION, null, contentValues);

            contentValues.clear();
        }
    }

    private void insertBids(ContentValues contentValues, SQLiteDatabase db) {
        for(int i = 0; i < 30; i++){
            contentValues.put(BID_PRICE, 1000);
            contentValues.put(BID_DATE_TIME, "2017-06-03 00:00:00.000");
            contentValues.put(BID_AUCTION_ID, i + 1);
            contentValues.put(BID_USER_ID, i + 1);

            db.insert(BID, null, contentValues);

            contentValues.clear();
        }
    }

}
