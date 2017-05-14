package com.maryf.spotkeeper.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.model.Spot;

import java.util.ArrayList;
import java.util.Arrays;

public class SpotsContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.maryf.spotkeeper.contentproviders.SpotsContentProvider");
    public static final String COLUMN_SPOT_NAME = "spotName";
    public static final String COLUMN_SPOT_ADDRESS = "spotAddress";

    ArrayList<Spot> spots = new ArrayList<Spot>();

    public SpotsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        Spot newSpot = new Spot(values.getAsString(COLUMN_SPOT_NAME), values.getAsString(COLUMN_SPOT_ADDRESS));
        //spots.add(values);
        return uri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        for (int i = 0; i < 5; i++) {
            spots.add(i, new Spot("Spot name " + i, "Spot address " + i));
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        String[] namesCol = new String[] {COLUMN_SPOT_NAME, COLUMN_SPOT_ADDRESS};
        MatrixCursor matrixCursor = new MatrixCursor(namesCol);
        for (int i = 0; i < spots.size(); i++) {
            MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
            newRow.add(COLUMN_SPOT_NAME, spots.get(i).getName());
            newRow.add(COLUMN_SPOT_ADDRESS, spots.get(i).getAddress());
        }

        return matrixCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
