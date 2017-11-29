package com.maryf.spotkeeper.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;

public class FavouriteSpotsFragment extends SpotsListFragment {

    public FavouriteSpotsFragment(SpotsListFragmentListener listener) {
        super(listener);//!!read about
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] arg = {"1"};
        return new CursorLoader(getActivity(), SpotsContentProvider.CONTENT_URI,
                CONTACTS_SUMMARY_PROJECTION, "favFlag=?", arg, null);
    }

}
