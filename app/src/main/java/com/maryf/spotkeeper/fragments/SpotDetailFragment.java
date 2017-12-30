package com.maryf.spotkeeper.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.SpotsListActivity;
import com.maryf.spotkeeper.model.Spot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by maryf on 4/10/2017.
 */

public class SpotDetailFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationChangeListener {

    private GoogleMap mMap;
    private MapView mapView;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    ////****
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);
        Location loc = null;
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc = null;
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        View rootView = getView();
        EditText spotAddress = (EditText) rootView.findViewById(R.id.spot_address_detail);
        if (loc != null) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude, 1);

                String address = (addresses != null && addresses.size() > 0)
                        ? addresses.get(0).getAddressLine(0)
                        : "Unknown";

                spotAddress.setText(address);

            } catch (IOException e) {
                e.printStackTrace();
            }} else {
            spotAddress.setText("");
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationChange(Location location) {
        System.out.println("");
    }
    ////****

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
        Button butRetToSpotsList = (Button) rootView.findViewById(R.id.return_to_spots_list);
        butRetToSpotsList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onCloseDetailsClick();
            }
        });

        Bundle bundle = getArguments();

        TextView nameView = (TextView) rootView.findViewById(R.id.spot_name_detail);
        TextView addressView = (TextView) rootView.findViewById(R.id.spot_address_detail);
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

        Button btnUseCurLoc = (Button) rootView.findViewById(R.id.btnUseCurrentLocation);
        btnUseCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///***
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);
                isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                permissionsToRequest = findUnAskedPermissions(permissions);

                if (!isGPS && !isNetwork) {
                    //Log.d(TAG, "Connection off");
                    showSettingsAlert();
                    getLastLocation();
                } else {
                    Log.d(TAG, "Connection on");
                    //check permissions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (permissionsToRequest.size() > 0) {
                            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                            Log.d(TAG, "Permission requests");
                            canGetLocation = false;
                        }
                    }
                    getLocation();
                }
                ///***
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationChangeListener(this);

        Geocoder geocoder = new Geocoder(getActivity());

        Bundle bundle = getArguments();
        final Spot spot = (Spot) bundle.getSerializable("Spot");
        String spotAddress;
        if (spot != null) {
            spotAddress = spot.getAddress();
        } else {
            spotAddress = "Sunnyvale, CA";
        }

        try {
            List<Address> addresses = geocoder.getFromLocationName(spotAddress, 1);
            if (addresses.size() > 0) {
                final double latitude = addresses.get(0).getLatitude();
                final double longitude = addresses.get(0).getLongitude();

                LatLng spotOnMap = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(spotOnMap).title("Marker in" + spotAddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotOnMap,15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

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
        } catch (IOException e) {
            e.printStackTrace();
        }

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d("Map", "Map clicked");
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("New spot"));

                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                mMap.getCameraPosition().target.latitude,
                                mMap.getCameraPosition().target.longitude, 1);

                        String address = (addresses != null && addresses.size() > 0)
                                ? addresses.get(0).getAddressLine(0)
                                : "Unknown";

                        View rootView = getView();
                        TextView addressView = (TextView) rootView.findViewById(R.id.spot_address_detail);
                        addressView.setText(address);

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
                                mPanorama.setPosition(new LatLng(
                                        mMap.getCameraPosition().target.latitude,
                                        mMap.getCameraPosition().target.longitude));
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
}
