package com.maryf.spotkeeper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;
import com.maryf.spotkeeper.fragments.NewSpotFragment;
import com.maryf.spotkeeper.fragments.SpotDetailFragment;
import com.maryf.spotkeeper.fragments.SpotsListFragment;
import com.maryf.spotkeeper.model.Spot;

public class SpotsListActivity extends AppCompatActivity implements
        SpotsListFragment.SpotsListFragmentListener,
        SpotDetailFragment.SpotDetailFragmentListener,
        NewSpotFragment.NewSpotFragmentListener {

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

    public void onSpotLongClick(final Spot spot, View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(SpotsListActivity.this, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.spots_list_popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                getContentResolver().delete(
                        ContentUris.withAppendedId(SpotsContentProvider.CONTENT_URI, spot.getId()),
                        null,
                        null);
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    @Override
    public void onCloseDetailsClick() {
        showSpotsListFragment();
    }

    @Override
    public void onAddNewSpotClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NewSpotFragment newSpotFragment = new NewSpotFragment(this);
        fragmentTransaction.replace(R.id.activity_spots_list, newSpotFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCancelAddNewSpotClick() {
        showSpotsListFragment();
    }

    private void showSpotsListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SpotsListFragment fragmentList = new SpotsListFragment(this);
        fragmentTransaction.replace(R.id.activity_spots_list, fragmentList);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveNewSpotClick(Spot spot) {
        showSpotsListFragment();
        ContentValues values = new ContentValues();
        values.put(SpotsContentProvider.COLUMN_SPOT_NAME, spot.getName());
        values.put(SpotsContentProvider.COLUMN_SPOT_ADDRESS, spot.getAddress());
        getContentResolver().insert(SpotsContentProvider.CONTENT_URI, values);
        System.out.println("name: "+spot.getName()+"    address: "+spot.getAddress());
    }


}
