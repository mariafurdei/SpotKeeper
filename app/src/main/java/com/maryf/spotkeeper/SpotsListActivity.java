package com.maryf.spotkeeper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import com.maryf.spotkeeper.fragments.FavouriteSpotsFragment;
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

        showSpotsListFragment(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "This spot" ));
        }

        return super.onOptionsItemSelected(item);
    }

    public void onNavItemFavSelected () {
        FavouriteSpotsFragment newFavSpotsFragment = new FavouriteSpotsFragment();
        replaceFragment(newFavSpotsFragment);
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, true);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_spots_list_content, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_spots) {
            showSpotsListFragment(true);
        } else if (id == R.id.nav_favourites) {
            onNavItemFavSelected();
        } else if (id == R.id.nav_addnewspot) {
            onAddNewSpotClick();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_spots_list);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSpotClick(Spot spot) {
        SpotDetailFragment fragmentDetail = SpotDetailFragment.newInstance(spot);
        replaceFragment(fragmentDetail);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
   }

    @Override
    public void onAddNewSpotClick() {
        NewSpotFragment newSpotFragment = new NewSpotFragment();
        replaceFragment(newSpotFragment);
    }

    @Override
    public void onCancelAddNewSpotClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    private void showSpotsListFragment(boolean addToBackStack) {
        SpotsListFragment fragmentList = new SpotsListFragment();
        replaceFragment(fragmentList, addToBackStack);
    }

    @Override
    public void onSaveNewSpotClick(Spot spot) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        ContentValues values = new ContentValues();
        values.put(SpotsContentProvider.COLUMN_SPOT_NAME, spot.getName());
        values.put(SpotsContentProvider.COLUMN_SPOT_ADDRESS, spot.getAddress());
        getContentResolver().insert(SpotsContentProvider.CONTENT_URI, values);
    }

    public void onFavButClick(Spot spot) {
        ContentValues values = new ContentValues();
        values.put(SpotsContentProvider.COLUMN_SPOT_NAME, spot.getName());
        values.put(SpotsContentProvider.COLUMN_SPOT_ADDRESS, spot.getAddress());
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
            favFlagBtn.setImageResource(R.mipmap.ic_fav_but_pressed);
        } else {
            favFlagBtn.setTag(0);
            favFlagBtn.setImageResource(R.mipmap.ic_fav_but_unpressed);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_spots_list);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); } else {
                super.onBackPressed();
            }
    }
}
