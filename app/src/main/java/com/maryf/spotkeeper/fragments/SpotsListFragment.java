package com.maryf.spotkeeper.fragments;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageButton;

import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;
import com.maryf.spotkeeper.model.Spot;
import com.maryf.spotkeeper.SpotsListAdapter;


/**
 * Created by maryf on 4/5/2017.
 */

public class SpotsListFragment extends Fragment implements
        SpotsListAdapter.SpotListAdapterListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String[] CONTACTS_SUMMARY_PROJECTION = {
            SpotsContentProvider.COLUMN_ID,
            SpotsContentProvider.COLUMN_SPOT_NAME,
            SpotsContentProvider.COLUMN_SPOT_ADDRESS,
            SpotsContentProvider.COLUMN_FAV_FL};

    private SpotsListAdapter spotsListAdapter;

    public SpotsListFragment() {}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SpotsContentProvider.CONTENT_URI,
                CONTACTS_SUMMARY_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        spotsListAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        spotsListAdapter.setCursor(null);
    }

    public interface SpotsListFragmentListener {
        void onSpotClick(Spot spot);
        void onAddNewSpotClick();
        void onSpotLongClick(Spot spot, View v);
        void onFavButClick(Spot spot);
    }

    private SpotsListFragmentListener listener;

    public SpotsListFragment(SpotsListFragmentListener listener) {
        this.listener = listener;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spots_list_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.spots_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        spotsListAdapter = new SpotsListAdapter(this);
        recyclerView.setAdapter(spotsListAdapter);

        FloatingActionButton addNewSpotBtn = (FloatingActionButton) rootView.findViewById(R.id.AddNewSpotBtn);
        addNewSpotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddNewSpotClick();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSpotClick(Spot spot) {
        listener.onSpotClick(spot);
    }

    public void onSpotLongClick(Spot spot, View v) {
        listener.onSpotLongClick(spot, v);
    }

    public void onFavButClick(Spot spot) {
        listener.onFavButClick(spot);
    }
}
