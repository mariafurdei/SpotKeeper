package com.maryf.spotkeeper.Database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;

import static com.maryf.spotkeeper.contentproviders.SpotsContentProvider.TABLE_SPOTSLIST;

/**
 * Created by maryf on 5/22/2017.
 */

public class SpotsListTable {
    // Database table
    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + SpotsContentProvider.TABLE_SPOTSLIST
            + "("
            + SpotsContentProvider.COLUMN_ID + " integer primary key autoincrement, "
            + SpotsContentProvider.COLUMN_SPOT_NAME + " text not null, "
            + SpotsContentProvider.COLUMN_SPOT_ADDRESS + " text not null, "
            + SpotsContentProvider.COLUMN_FAV_FL + " integer "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
        for (int i = 0; i < 5; i++) {
            database.execSQL("INSERT INTO " +
            SpotsContentProvider.TABLE_SPOTSLIST +
                    "(" + SpotsContentProvider.COLUMN_ID +
                    "," + SpotsContentProvider.COLUMN_SPOT_NAME +
                    "," + SpotsContentProvider.COLUMN_SPOT_ADDRESS +
                    "," + SpotsContentProvider.COLUMN_FAV_FL + ")" +
                    " VALUES( " +
                    i +
                    ", 'Spot name" + i + "'" +
                    ", 'Spot address" + i + "'" +
                    ", " + 0 +
                    ");"
            );
        }
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(SpotsListTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SPOTSLIST);
        onCreate(database);
    }
}
