package com.maryf.spotkeeper;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.maryf.spotkeeper.SpotsListFragment.spots;

/**
 * Created by maryf on 4/10/2017.
 */

public class SpotDetailFragment extends Fragment {
    interface SpotDetailFragmentListener {
        void onCloseDetailsClick();
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

        View rootView = inflater.inflate(R.layout.spot_details_fragment, container, false);
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
        final Spot spot = (Spot) bundle.getSerializable("Spot");
        nameView.setText(spot.getName());
        addressView.setText(spot.getAddress());

        return rootView;
    }

}
