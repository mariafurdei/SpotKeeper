package com.maryf.spotkeeper.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.maryf.spotkeeper.Database.DatabaseHelper;
import com.maryf.spotkeeper.Database.DatabaseHelperFactory;
import com.maryf.spotkeeper.Database.DatabaseHelperFactoryImpl;

public class SpotsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.maryf.spotkeeper.contentproviders.SpotsContentProvider";
    private static final String BASE_PATH = "spots";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);
    public static final String COLUMN_SPOT_NAME = "spotName";
    public static final String COLUMN_SPOT_ADDRESS = "spotAddress";
    public static final String TABLE_SPOTSLIST = "spotslist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FAV_FL = "favFlag" ;

    private DatabaseHelperFactory mDatabaseHelperFactory;

    private UriMatcherFactory mUriMatcherFactory;

    public SpotsContentProvider() {
        mDatabaseHelperFactory = new DatabaseHelperFactoryImpl();
        mUriMatcherFactory = new UriMatcherFactoryImpl();
    }

    public SpotsContentProvider(DatabaseHelperFactory databaseHelperFactory, UriMatcherFactory uriMatcherFactory) {
        mDatabaseHelperFactory = databaseHelperFactory;
        mUriMatcherFactory = uriMatcherFactory;
    }

    private static DatabaseHelper database;
    // used for the UriMacher
    public static final int SPOTSLISTS = 10;
    public static final int SPOTSLIST_ID = 20;

    private static UriMatcher sURIMatcher;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SPOTSLISTS:
                rowsDeleted = sqlDB.delete(TABLE_SPOTSLIST, selection, selectionArgs);
                break;
            case SPOTSLIST_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            TABLE_SPOTSLIST,
                            COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            TABLE_SPOTSLIST,
                            COLUMN_ID + "=" + id
                            + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case SPOTSLISTS:
                id = sqlDB.insert(TABLE_SPOTSLIST, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public boolean onCreate() {
        database = mDatabaseHelperFactory.create(getContext());
        sURIMatcher = mUriMatcherFactory.create();
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SPOTSLISTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SPOTSLIST_ID);
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
        //throw new UnsupportedOperationException("Not yet implemented");
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SPOTSLISTS:
                rowsUpdated = sqlDB.update(
                        TABLE_SPOTSLIST,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SPOTSLIST_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(
                            TABLE_SPOTSLIST,
                            values,
                            COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(
                            TABLE_SPOTSLIST,
                            values,
                            COLUMN_ID + "=" + id
                            + " and "
                            + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
