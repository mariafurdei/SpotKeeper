package com.maryf.spotkeeper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.maryf.spotkeeper.R;
import com.maryf.spotkeeper.model.Spot;

/**
 * Created by maryf on 4/19/2017.
 */

public class NewSpotFragment extends Fragment {

    public interface NewSpotFragmentListener {
        void onCancelAddNewSpotClick();
        void onSaveNewSpotClick(Spot spot);
    }

    public NewSpotFragmentListener listener;

    public NewSpotFragment() {}

    public NewSpotFragment(NewSpotFragmentListener listener) {
        this.listener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {

        final View rootView = inflater.inflate(R.layout.new_spot, container, false);

        Button cancelAddNewSpotBtn = (Button) rootView.findViewById(R.id.CancelAddNewSpotBtn);
        cancelAddNewSpotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancelAddNewSpotClick();
            }
        });

        Button saveNewSpotBtn = (Button) rootView.findViewById(R.id.SaveNewSpotBtn);
        saveNewSpotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText spotName = (EditText) rootView.findViewById(R.id.new_spot_name_edit_text);
                EditText spotAddress = (EditText) rootView.findViewById(R.id.new_spot_address_edit_text);
                Spot spot = new Spot(null, spotName.getText().toString(), spotAddress.getText().toString(), 0);
                listener.onSaveNewSpotClick(spot);
            }
        });

        return rootView;
    }
}
