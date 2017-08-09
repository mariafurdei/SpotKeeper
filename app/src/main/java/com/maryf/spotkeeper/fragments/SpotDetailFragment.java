package com.maryf.spotkeeper.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.model.Spot;

/**
 * Created by maryf on 4/10/2017.
 */

public class SpotDetailFragment extends Fragment {
    public interface SpotDetailFragmentListener {
        void onCloseDetailsClick();
        void onUpdateSpot(Spot spot);
        void onFavDetBtnClick(Spot spot);
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

        return rootView;
    }
}
