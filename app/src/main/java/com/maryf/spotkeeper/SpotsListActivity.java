package com.maryf.spotkeeper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;
import com.maryf.spotkeeper.fragments.NewSpotFragment;
import com.maryf.spotkeeper.fragments.SpotDetailFragment;
import com.maryf.spotkeeper.fragments.SpotsListFragment;
import com.maryf.spotkeeper.model.Spot;

public class SpotsListActivity extends AppCompatActivity implements
        SpotsListFragment.SpotsListFragmentListener,
        SpotDetailFragment.SpotDetailFragmentListener,
        NewSpotFragment.NewSpotFragmentListener,
        NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_spots_list);

        //This class provides a handy way to tie together the functionality of DrawerLayout
        // and the framework ActionBar to implement the recommended design for navigation drawers.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         showSpotsListFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_spots_list);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_spots) {
            showSpotsListFragment();
        } else if (id == R.id.nav_favourites) {
            System.out.println("nav_favourites");
        } else if (id == R.id.nav_addnewspot) {
            onAddNewSpotClick();
        } else if (id == R.id.nav_gallery) {
            System.out.println("nav_gallery");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_spots_list);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSpotClick(Spot spot) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SpotDetailFragment fragmentDetail = SpotDetailFragment.newInstance(spot, this);
        fragmentTransaction.replace(R.id.activity_spots_list_content, fragmentDetail);
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
    public void onUpdateSpot(final Spot spot) {
        ContentValues values = new ContentValues();
        ImageButton favFlagBtn = (ImageButton) findViewById(R.id.add_to_fav_but_det);
        values.put(SpotsContentProvider.COLUMN_SPOT_NAME, spot.getName());
        values.put(SpotsContentProvider.COLUMN_SPOT_ADDRESS, spot.getAddress());
        if ((Integer) favFlagBtn.getTag() == 0) {
            values.put(SpotsContentProvider.COLUMN_FAV_FL, 0);
        } else {
            values.put(SpotsContentProvider.COLUMN_FAV_FL, 1);
        }

        getContentResolver().update(
                ContentUris.withAppendedId(SpotsContentProvider.CONTENT_URI, spot.getId()),
                values,
                null,
                null);
        showSpotsListFragment();
   }

    @Override
    public void onAddNewSpotClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NewSpotFragment newSpotFragment = new NewSpotFragment(this);
        fragmentTransaction.replace(R.id.activity_spots_list_content, newSpotFragment);
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
        fragmentTransaction.replace(R.id.activity_spots_list_content, fragmentList);
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
    }

    public void onFavButClick(Spot spot) {
        ContentValues values = new ContentValues();
        values.put(SpotsContentProvider.COLUMN_SPOT_NAME, spot.getName());
        values.put(SpotsContentProvider.COLUMN_SPOT_ADDRESS, spot.getAddress());
        //System.out.println(String.valueOf(spot.getFavFlag()));
        if (spot.getFavFlag() == 0) {
            values.put(String.valueOf(SpotsContentProvider.COLUMN_FAV_FL),1);
        } else
        {
            values.put(String.valueOf(SpotsContentProvider.COLUMN_FAV_FL),0);
        }

        getContentResolver().update(
                ContentUris.withAppendedId(SpotsContentProvider.CONTENT_URI, spot.getId()),
                values,
                null,
                null);
    }

    public void onFavDetBtnClick(Spot spot) {
        ImageButton favFlagBtn = (ImageButton) findViewById(R.id.add_to_fav_but_det);
        if ((Integer) favFlagBtn.getTag() == 0) {
            favFlagBtn.setTag(1);
            favFlagBtn.setImageResource(R.mipmap.button_pressed);
        } else {
            favFlagBtn.setTag(0);
            favFlagBtn.setImageResource(R.mipmap.button_normal);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_spots_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.removeDrawerListener(toggle);
    }
}
