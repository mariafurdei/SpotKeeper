package com.maryf.spotkeeper.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.model.Spot;

import java.io.IOException;
import java.util.List;


/**
 * Created by maryf on 4/10/2017.
 */

public class SpotDetailFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "SpotDetailFragment";
    private Location mLastKnownLocation;
    private static final float DEFAULT_ZOOM = 15;
    private static final LatLng mDefaultLocation = new LatLng (33.8121, 117.9190);

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    public interface SpotDetailFragmentListener {
        void onCloseDetailsClick();

        void onSaveSpot(Spot spot);

        void onFavDetBtnClick(Spot spot);
    }

    public SpotDetailFragmentListener listener;

    public static SpotDetailFragment newInstance(Spot spot) {
        SpotDetailFragment detailFragment = new SpotDetailFragment();

        Bundle spotParams = new Bundle();
        spotParams.putSerializable("Spot", spot);

        detailFragment.setArguments(spotParams);

        return detailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (SpotDetailFragmentListener) context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {

        final View rootView = inflater.inflate(R.layout.spot_details_fragment, container, false);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        Button butRetToSpotsList = (Button) rootView.findViewById(R.id.return_to_spots_list);
        butRetToSpotsList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onCloseDetailsClick();
            }
        });

        Bundle bundle = getArguments();

        TextView nameView = (TextView) rootView.findViewById(R.id.spot_name_detail);
        final TextView addressView = (TextView) rootView.findViewById(R.id.spot_address_detail);
        ImageButton favFlag = (ImageButton) rootView.findViewById(R.id.add_to_fav_but_det);

        final Spot spot = (Spot) bundle.getSerializable("Spot");
        if (spot != null) {
            nameView.setText(spot.getName());
            addressView.setText(spot.getAddress());
            if (spot.getFavFlag() == 1) {
                favFlag.setImageResource(R.mipmap.ic_fav_but_pressed);
                favFlag.setTag(1);
            } else {
                favFlag.setImageResource(R.mipmap.ic_fav_but_unpressed);
                favFlag.setTag(0);
            }
        } else {
            favFlag.setTag(0);
        }

        favFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFavDetBtnClick(spot);
            }
        });

        Button butUpdateSpot = (Button) rootView.findViewById(R.id.update_spots_list_btn);
        butUpdateSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText spotName = (EditText) rootView.findViewById(R.id.spot_name_detail);
                EditText spotAddress = (EditText) rootView.findViewById(R.id.spot_address_detail);
                Bundle bundle = getArguments();
                Spot originalSpot = (Spot) bundle.getSerializable("Spot");
                Spot spotDet;
                if (spot != null) {
                    spotDet = new Spot(originalSpot.getId(), spotName.getText().toString(), spotAddress.getText().toString(), 0);
                } else {
                    spotDet = new Spot(null, spotName.getText().toString(), spotAddress.getText().toString(), 0);
                }
                listener.onSaveSpot(spotDet);
            }
        });

        addressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!addressView.getText().toString().matches("")) {
                    addressToPosition(addressView.getText().toString());}
            }
        });

        setHasOptionsMenu(true);

      return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.nav_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_item_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            Bundle bundle = getArguments();
            final Spot spot = (Spot) bundle.getSerializable("Spot");
            String spotAddress = spot.getAddress();
            String spotName = spot.getName();

            sendIntent.putExtra(Intent.EXTRA_TEXT, spotName + ", " + spotAddress);

            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "This spot" ));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        Bundle bundle = getArguments();
        final Spot spot = (Spot) bundle.getSerializable("Spot");
        String spotAddress;

        if (spot != null) {
            spotAddress = spot.getAddress();

            updateLocationUI();

            addressToPosition(spotAddress);
        } else {

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    addNewMarker(latLng, positionToAddress(latLng.latitude, latLng.longitude));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getDeviceLocation();
                return true;
            }
        });
    }

    private void addressToPosition(String spotAddress){
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(spotAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            final double latitude = addresses.get(0).getLatitude();
            final double longitude = addresses.get(0).getLongitude();

            LatLng spotOnMap = new LatLng(latitude, longitude);
            addNewMarker(spotOnMap, spotAddress);
            setPanoramaStreetView(latitude, longitude);
        }
    }

    private String positionToAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        String address = (addresses != null && addresses.size() > 0)
                ? addresses.get(0).getAddressLine(0)
                : "Unknown";
        View rootView = getView();
        TextView addressView = (TextView) rootView.findViewById(R.id.spot_address_detail);
        TextView nameView = (TextView) rootView.findViewById(R.id.spot_name_detail);
        addressView.setText(address);
        nameView.setText("");
        setPanoramaStreetView(latitude, longitude);

        return address;
    }

    private void setPanoramaStreetView(final double latitude, final double longitude) {
        FragmentManager fm = getChildFragmentManager();
        SupportStreetViewPanoramaFragment sVpFragment = (SupportStreetViewPanoramaFragment) fm.findFragmentByTag("sVpFragment");
        if (sVpFragment == null) {
            sVpFragment = new SupportStreetViewPanoramaFragment();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.streetViewPanoramaFragmentContainer, sVpFragment, "sVpFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        sVpFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                StreetViewPanorama mPanorama = streetViewPanorama;
                mPanorama.setPosition(new LatLng(latitude, longitude));
            }
        });
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            LatLng spotOnMap = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            try {
                                addNewMarker(spotOnMap, positionToAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            try {
                                addNewMarker(mDefaultLocation, positionToAddress(mDefaultLocation.latitude,mDefaultLocation.longitude) );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void addNewMarker(LatLng spotOnMap, String markerTitle){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(spotOnMap).title(markerTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotOnMap,DEFAULT_ZOOM));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

}
