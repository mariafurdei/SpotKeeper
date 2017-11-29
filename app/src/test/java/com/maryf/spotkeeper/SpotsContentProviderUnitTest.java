package com.maryf.spotkeeper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;

import com.maryf.spotkeeper.Database.DatabaseHelper;
import com.maryf.spotkeeper.Database.DatabaseHelperFactory;
import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;
import com.maryf.spotkeeper.contentproviders.UriMatcherFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class SpotsContentProviderUnitTest {

    private static final String SPOT_NAME = "Sunnyvale";
    private static final String SPOT_ADDRESS = "El Camino";

    @Mock
    ContentValues mSpot;

    @Mock
    DatabaseHelper mDatabaseHelper;

    @Mock
    UriMatcher mUriMatcher;

    @Mock
    SQLiteDatabase mSQLiteDatabase;

    @Mock
    Context mContext;

    @Mock
    ContentResolver mContentResolver;

    @Test
    public void testInsertSpot() throws Exception {
        when(mSpot.getAsString(SpotsContentProvider.COLUMN_SPOT_NAME))
                .thenReturn(SPOT_NAME);
        when(mSpot.getAsString(SpotsContentProvider.COLUMN_SPOT_ADDRESS))
                .thenReturn(SPOT_ADDRESS);
        assertEquals(SPOT_NAME, mSpot.getAsString(SpotsContentProvider.COLUMN_SPOT_NAME));
        assertEquals(SPOT_ADDRESS, mSpot.getAsString(SpotsContentProvider.COLUMN_SPOT_ADDRESS));

        when(mUriMatcher.match(SpotsContentProvider.CONTENT_URI))
                .thenReturn(SpotsContentProvider.SPOTSLISTS);

        when(mDatabaseHelper.getWritableDatabase()).thenReturn(mSQLiteDatabase);

        SpotsContentProvider contentProvider = new SpotsContentProvider(new DatabaseHelperFactory() {
            @Override
            public DatabaseHelper create(Context context) {
                return mDatabaseHelper;
            }
        }, new UriMatcherFactory() {
            @Override
            public UriMatcher create() {
                return mUriMatcher;
            }
        });

        contentProvider = spy(contentProvider);
        when(contentProvider.getContext()).thenReturn(mContext);
        when(mContext.getContentResolver()).thenReturn(mContentResolver);

        contentProvider.onCreate();
        contentProvider.insert(SpotsContentProvider.CONTENT_URI, mSpot);

        verify(mSQLiteDatabase, times(1)).insert(SpotsContentProvider.TABLE_SPOTSLIST, null, mSpot);
    }
}