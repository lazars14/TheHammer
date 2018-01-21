package com.example.kiki.thehammer.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Lazar on 6/7/2017.
 */

public class TheHammerContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper;

    private static final int USER = 100;
    private static final int USER_ID = 101;

    private static final int AUCTION = 200;
    private static final int AUCTION_ID = 201;

    private static final int BID = 300;
    private static final int BID_ID = 301;

    private static final int ITEM = 400;
    private static final int ITEM_ID = 401;

    private static final UriMatcher URI_MATCHER = createUriMatcher();

    private static UriMatcher createUriMatcher() {

        final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = TheHammerContract.CONTENT_AUTHORITY;

        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.UserTable.PATH_USER, USER);
        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.UserTable.PATH_USER + "/#", USER_ID);

        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.AuctionTable.PATH_AUCTION, AUCTION);
        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.AuctionTable.PATH_AUCTION + "/#", AUCTION_ID);

        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.BidTable.PATH_BID, BID);
        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.BidTable.PATH_BID + "/#", BID_ID);

        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.ItemTable.PATH_ITEM, ITEM);
        URI_MATCHER.addURI(AUTHORITY, TheHammerContract.ItemTable.PATH_ITEM + "/#", ITEM_ID);

        return URI_MATCHER;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch ((URI_MATCHER.match(uri))) {
            case USER:
                return TheHammerContract.UserTable.CONTENT_TYPE_MULTIPLE_ITEMS;
            case USER_ID:
                return TheHammerContract.UserTable.CONTENT_TYPE_SINGLE_ITEM;
            case AUCTION:
                return TheHammerContract.AuctionTable.CONTENT_TYPE_MULTIPLE_ITEMS;
            case AUCTION_ID:
                return TheHammerContract.AuctionTable.CONTENT_TYPE_SINGLE_ITEM;
            case BID:
                return TheHammerContract.BidTable.CONTENT_TYPE_MULTIPLE_ITEMS;
            case BID_ID:
                return TheHammerContract.BidTable.CONTENT_TYPE_SINGLE_ITEM;
            case ITEM:
                return TheHammerContract.ItemTable.CONTENT_TYPE_MULTIPLE_ITEMS;
            case ITEM_ID:
                return TheHammerContract.ItemTable.CONTENT_TYPE_SINGLE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = URI_MATCHER.match(uri);
        long rowId;

        switch (uriType) {
            case USER:
                rowId = db.insertOrThrow(TheHammerContract.UserTable.USER, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(TheHammerContract.UserTable.CONTENT_URI, rowId);
            case AUCTION:
                rowId = db.insertOrThrow(TheHammerContract.AuctionTable.AUCTION, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(TheHammerContract.AuctionTable.CONTENT_URI, rowId);
            case BID:
                rowId = db.insertOrThrow(TheHammerContract.BidTable.BID, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(TheHammerContract.BidTable.CONTENT_URI, rowId);
            case ITEM:
                rowId = db.insertOrThrow(TheHammerContract.BidTable.BID, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(TheHammerContract.ItemTable.CONTENT_URI, rowId);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int uriType = URI_MATCHER.match(uri);

        int updateCount;

        switch (uriType) {
            case USER:
                updateCount = db.update(TheHammerContract.UserTable.USER, values, selection, selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                updateCount = db.update(TheHammerContract.UserTable.USER,
                        values,
                        TheHammerContract.UserTable.USER_ID + " =" + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            case AUCTION:
                updateCount = db.update(TheHammerContract.AuctionTable.AUCTION, values, selection, selectionArgs);
                break;
            case AUCTION_ID:
                id = uri.getLastPathSegment();
                updateCount = db.update(TheHammerContract.AuctionTable.AUCTION,
                        values,
                        TheHammerContract.AuctionTable.AUCTION_ID + " =" + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            case BID:
                updateCount = db.update(TheHammerContract.BidTable.BID, values, selection, selectionArgs);
                break;
            case BID_ID:
                id = uri.getLastPathSegment();
                updateCount = db.update(TheHammerContract.BidTable.BID,
                        values,
                        TheHammerContract.BidTable.BID_ID + " =" + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            case ITEM:
                updateCount = db.update(TheHammerContract.ItemTable.ITEM, values, selection, selectionArgs);
                break;
            case ITEM_ID:
                id = uri.getLastPathSegment();
                updateCount = db.update(TheHammerContract.ItemTable.ITEM,
                        values,
                        TheHammerContract.ItemTable.ITEM_ID + " =" + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int uriType = URI_MATCHER.match(uri);

        int deleteCount;

        switch (uriType) {
            case USER:
                deleteCount = db.delete(TheHammerContract.UserTable.USER, selection, selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                deleteCount = db.delete(
                        TheHammerContract.UserTable.USER,
                        TheHammerContract.UserTable.USER_ID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            case AUCTION:
                deleteCount = db.delete(TheHammerContract.AuctionTable.AUCTION, selection, selectionArgs);
                break;
            case AUCTION_ID:
                id = uri.getLastPathSegment();
                deleteCount = db.delete(
                        TheHammerContract.AuctionTable.AUCTION,
                        TheHammerContract.AuctionTable.AUCTION_ID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            case BID:
                deleteCount = db.delete(TheHammerContract.BidTable.BID, selection, selectionArgs);
                break;
            case BID_ID:
                id = uri.getLastPathSegment();
                deleteCount = db.delete(
                        TheHammerContract.BidTable.BID,
                        TheHammerContract.BidTable.BID_ID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            case ITEM:
                deleteCount = db.delete(TheHammerContract.ItemTable.ITEM, selection, selectionArgs);
                break;
            case ITEM_ID:
                id = uri.getLastPathSegment();
                deleteCount = db.delete(
                        TheHammerContract.ItemTable.ITEM,
                        TheHammerContract.ItemTable.ITEM_ID + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), // append selection to query if selection is not empty
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Record id
        String id;

        // Match Uri pattern
        int uriType = URI_MATCHER.match(uri);

        switch (uriType) {
            case USER:
                // Set the table name
                queryBuilder.setTables(TheHammerContract.UserTable.USER);
                break;
            case USER_ID:
                queryBuilder.setTables(TheHammerContract.UserTable.USER);
                selection = TheHammerContract.UserTable.USER_ID + " = ? ";
                id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                break;
            case AUCTION:
                queryBuilder.setTables(TheHammerContract.AuctionTable.AUCTION);
                break;
            case AUCTION_ID:
                queryBuilder.setTables(TheHammerContract.AuctionTable.AUCTION);
                selection = TheHammerContract.AuctionTable.AUCTION_ID + " = ? ";
                id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                break;
            case BID:
                queryBuilder.setTables(TheHammerContract.BidTable.BID);
                break;
            case BID_ID:
                queryBuilder.setTables(TheHammerContract.BidTable.BID);
                selection = TheHammerContract.BidTable.BID_ID + " = ? ";
                id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                break;
            case ITEM:
                queryBuilder.setTables(TheHammerContract.ItemTable.ITEM);
                break;
            case ITEM_ID:
                queryBuilder.setTables(TheHammerContract.ItemTable.ITEM);
                selection = TheHammerContract.ItemTable.ITEM_ID + " = ? ";
                id = uri.getLastPathSegment();
                selectionArgs = new String[] {id};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
}
