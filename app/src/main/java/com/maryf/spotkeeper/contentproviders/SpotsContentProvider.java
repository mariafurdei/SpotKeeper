package com.maryf.spotkeeper.contentproviders;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.maryf.spotkeeper.Database.DatabaseHelper;
import com.maryf.spotkeeper.Database.SpotsListTable;
import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.model.Spot;

import java.util.ArrayList;
import java.util.Arrays;

public class SpotsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.maryf.spotkeeper.contentproviders.SpotsContentProvider";
    private static final String BASE_PATH = "spots";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);
    public static final String COLUMN_SPOT_NAME = "spotName";
    public static final String COLUMN_SPOT_ADDRESS = "spotAddress";
    public static final String TABLE_SPOTSLIST = "spotslist";
    public static final String COLUMN_ID = "_id";

    public SpotsContentProvider() {
    }

    private DatabaseHelper database;
    // used for the UriMacher
    private static final int SPOTSLISTS = 10;
    private static final int SPOTSLIST_ID = 20;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/spots";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SPOTSLISTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SPOTSLIST_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Spot newSpot = new Spot(values.getAsString(COLUMN_SPOT_NAME), values.getAsString(COLUMN_SPOT_ADDRESS));

        return uri;
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(TABLE_SPOTSLIST);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SPOTSLISTS:
                break;
            case SPOTSLIST_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private void checkColumns(String[] projection) {
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
