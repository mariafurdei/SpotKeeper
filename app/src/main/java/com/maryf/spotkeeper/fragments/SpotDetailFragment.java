package com.maryf.spotkeeper.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private Marker marker;

    public interface SpotDetailFragmentListener {
        void onCloseDetailsClick();

        void onUpdateSpot(Spot spot);

        void onFavDetBtnClick(Spot spot);

        void onMapClick();
    }

    public SpotDetailFragmentListener listener;

    public SpotDetailFragment(SpotDetailFragmentListener listener) {
        this.listener = listener;
    }

    public static SpotDetailFragment newInstance(Spot spot, SpotDetailFragmentListener spotsListActivity) {
        SpotDetailFragment detailFragment = new SpotDetailFragment(spotsListActivity);

        Bundle spotParams = new Bundle();
        spotParams.putSerializable("Spot", spot);

        detailFragment.setArguments(spotParams);

        return detailFragment;
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
        nameView.setText(spot.getName());
        addressView.setText(spot.getAddress());
        if (spot.getFavFlag() == 1) {
            favFlag.setImageResource(R.mipmap.button_pressed);
            favFlag.setTag(1);
        } else {
            favFlag.setImageResource(R.mipmap.button_normal);
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
                Spot spot = new Spot(originalSpot.getId(), spotName.getText().toString(), spotAddress.getText().toString(), 0);
                listener.onUpdateSpot(spot);
            }
        });

        // Inflate the layout for this fragment
        inflater.inflate(R.layout.spot_details_fragment, container, false);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        Geocoder geocoder = new Geocoder(getActivity());

        Bundle bundle = getArguments();
        final Spot spot = (Spot) bundle.getSerializable("Spot");
        String spotAddress = spot.getAddress();
        try {
            List<Address> addresses = geocoder.getFromLocationName(spotAddress, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();

                LatLng spotOnMap = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(spotOnMap).title("Marker in" + spotAddress));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotOnMap,15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("Map","Map clicked");
                //marker.remove();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("New marker"));
                // position = mMap.getCameraPosition().target;

                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            mMap.getCameraPosition().target.latitude,
                            mMap.getCameraPosition().target.longitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    System.out.println(address);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
