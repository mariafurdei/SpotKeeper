package com.maryf.spotkeeper.Database;

import android.content.Context;

/**
 * Created by maryf on 11/27/2017.
 */

public interface DatabaseHelperFactory {
    DatabaseHelper create(Context context);
}
