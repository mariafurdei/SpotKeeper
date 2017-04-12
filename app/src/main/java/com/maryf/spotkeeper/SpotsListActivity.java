package com.maryf.spotkeeper;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SpotsListActivity extends AppCompatActivity implements
        SpotsListFragment.SpotsListFragmentListener,
        SpotDetailFragment.SpotDetailFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots_list);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SpotsListFragment fragmentList = new SpotsListFragment(this);
        fragmentTransaction.add(R.id.activity_spots_list, fragmentList);
        fragmentTransaction.commit();
    }

    @Override
    public void onSpotClick(Spot spot) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SpotDetailFragment fragmentDetail = SpotDetailFragment.newInstance(spot, this);
        fragmentTransaction.replace(R.id.activity_spots_list, fragmentDetail);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCloseDetailsClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SpotsListFragment fragmentList = new SpotsListFragment(this);
        fragmentTransaction.replace(R.id.activity_spots_list, fragmentList);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

//create fragment, place fragment inside the activity. Fragment holds a list of places
