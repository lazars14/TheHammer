package com.example.kiki.thehammer.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by Lazar on 6/7/2017.
 */

public class TheHammerContract implements BaseColumns {

    public static final String DATABASE_NAME = "the_hammer.db";

    public static final String CONTENT_AUTHORITY = "com.example.kiki.TheHammer.TheHammerContentProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class UserTable {

        public static final String PATH_USER = "user";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE_MULTIPLE_ITEMS = "vnd.android.cursor.dir/android.sqlite.provider.users";

        public static final String CONTENT_TYPE_SINGLE_ITEM = "vnd.android.cursor.item/android.sqlite.provider.user";

        public static final String USER = "user";

        public static final String USER_ID = BaseColumns._ID; // int
        public static final String USER_NAME = "username";
        public static final String USER_EMAIL = "email";
        public static final String USER_PASSWORD = "password";
        public static final String USER_PICTURE = "picture";
        public static final String USER_ADDRESS = "address";
        public static final String USER_PHONE = "phone";

        public static final String[] PROJECTION = new String[]{
           /* 0 */ UserTable.USER_ID,
           /* 1 */ UserTable.USER_NAME,
           /* 2 */ UserTable.USER_EMAIL,
           /* 3 */ UserTable.USER_PASSWORD,
           /* 4 */ UserTable.USER_PICTURE,
           /* 5 */ UserTable.USER_ADDRESS,
           /* 6 */ UserTable.USER_PHONE
        };
    }

    public static final class AuctionTable {

        public static final String PATH_AUCTION = "auction";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AUCTION).build();

        public static final String CONTENT_TYPE_MULTIPLE_ITEMS = "vnd.android.cursor.dir/android.sqlite.provider.auctions";

        public static final String CONTENT_TYPE_SINGLE_ITEM = "vnd.android.cursor.item/android.sqlite.provider.auction";

        public static final String AUCTION = "auction";

        public static final String AUCTION_ID = BaseColumns._ID; // int
        public static final String AUCTION_START_PRICE = "startPrice";
        public static final String AUCTION_START_DATE = "startDate";
        public static final String AUCTION_END_DATE = "endDate";
        public static final String AUCTION_USER_ID = "userId";
        public static final String AUCTION_ITEM_ID = "itemId";

        public static final String[] PROJECTION = new String[]{
           /* 0 */ AuctionTable.AUCTION_ID,
           /* 1 */ AuctionTable.AUCTION_START_PRICE,
           /* 2 */ AuctionTable.AUCTION_START_DATE,
           /* 3 */ AuctionTable.AUCTION_END_DATE,
           /* 4 */ AuctionTable.AUCTION_USER_ID,
           /* 5 */ AuctionTable.AUCTION_ITEM_ID
        };
    }

    public static final class BidTable {

        public static final String PATH_BID = "bid";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BID).build();

        public static final String CONTENT_TYPE_MULTIPLE_ITEMS = "vnd.android.cursor.dir/android.sqlite.provider.bids";

        public static final String CONTENT_TYPE_SINGLE_ITEM = "vnd.android.cursor.item/android.sqlite.provider.bid";

        public static final String BID = "bid";

        public static final String BID_ID = BaseColumns._ID; // int
        public static final String BID_PRICE = "price";
        public static final String BID_DATE_TIME = "dateTime";
        public static final String BID_AUCTION_ID = "auctionId";
        public static final String BID_USER_ID = "userId";

        public static final String[] PROJECTION = new String[]{
           /* 0 */ BidTable.BID_ID,
           /* 1 */ BidTable.BID_PRICE,
           /* 2 */ BidTable.BID_DATE_TIME,
           /* 3 */ BidTable.BID_AUCTION_ID,
           /* 4 */ BidTable.BID_USER_ID
        };
    }

    public static final class ItemTable {

        public static final String PATH_ITEM = "item";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String CONTENT_TYPE_MULTIPLE_ITEMS = "vnd.android.cursor.dir/android.sqlite.provider.items";

        public static final String CONTENT_TYPE_SINGLE_ITEM = "vnd.android.cursor.item/android.sqlite.provider.item";

        public static final String ITEM = "item";

        public static final String ITEM_ID = BaseColumns._ID; // int
        public static final String ITEM_NAME = "name";
        public static final String ITEM_DESCRIPTION = "description";
        public static final String ITEM_PICTURE = "picture";
        public static final String ITEM_SOLD = "sold";

        public static final String[] PROJECTION = new String[]{
           /* 0 */ ItemTable.ITEM_ID,
           /* 1 */ ItemTable.ITEM_NAME,
           /* 2 */ ItemTable.ITEM_DESCRIPTION,
           /* 3 */ ItemTable.ITEM_PICTURE,
           /* 4 */ ItemTable.ITEM_SOLD
        };
    }

}
