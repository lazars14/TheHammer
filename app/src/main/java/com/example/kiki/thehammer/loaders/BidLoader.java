package com.example.kiki.thehammer.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.example.kiki.thehammer.data.TheHammerContentProvider;
import com.example.kiki.thehammer.data.TheHammerContract;

/**
 * Created by Lazar on 16/6/2017.
 */

public class BidLoader extends CursorLoader {
    private TheHammerContentProvider provider;

    public BidLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor loadInBackground() {
        provider = new TheHammerContentProvider();

        return provider.query(TheHammerContract.BidTable.CONTENT_URI, null, null, null, null);
    }
}
