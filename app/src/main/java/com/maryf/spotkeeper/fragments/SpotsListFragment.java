package com.maryf.spotkeeper.fragments;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;

import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.model.Spot;
import com.maryf.spotkeeper.SpotsListAdapter;


/**
 * Created by maryf on 4/5/2017.
 */

public class SpotsListFragment extends Fragment implements SpotsListAdapter.SpotListAdapterListener {

    public interface SpotsListFragmentListener {
        void onSpotClick(Spot spot);
        void onAddNewSpotClick();
    }

    //final - нельзя переопределить
    static final Spot[] spots = new Spot[] {
            new Spot("Sunnyvale", "5 Street5" +
                    ""),
            new Spot("MV", "1 Street1"),
            new Spot("SF", "2 Street2"),
            new Spot("SJ", "3 Street3"),
            new Spot("PA", "4 Street4")

    };

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

        final SpotsListAdapter spotsListAdapter = new SpotsListAdapter(spots, this);
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
    public void onSpotClick(Spot spot) {
        this.listener.onSpotClick(spot);
    }
}
