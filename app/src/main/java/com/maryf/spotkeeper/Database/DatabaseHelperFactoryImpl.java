package com.maryf.spotkeeper.Database;

import android.content.Context;

/**
 * Created by maryf on 11/27/2017.
 */

public class DatabaseHelperFactoryImpl implements DatabaseHelperFactory {
    @Override
    public DatabaseHelper create(Context context) {
        return new DatabaseHelper(context);
    }
}
