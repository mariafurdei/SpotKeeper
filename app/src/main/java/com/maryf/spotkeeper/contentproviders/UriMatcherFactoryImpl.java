package com.maryf.spotkeeper.contentproviders;

import android.content.UriMatcher;

/**
 * Created by maryf on 11/27/2017.
 */

public class UriMatcherFactoryImpl implements UriMatcherFactory {
    @Override
    public UriMatcher create() {
        return new UriMatcher(UriMatcher.NO_MATCH);
    }
}
