package com.maryf.spotkeeper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import com.maryf.spotkeeper.model.Spot;


/**
 * Created by maryf on 4/5/2017.
 */

public class SpotsListAdapter extends RecyclerView.Adapter<SpotsListAdapter.ViewHolder> {

    public interface SpotListAdapterListener {
        public void onSpotClick(Spot spot);
    }

    private Spot[] spots;
    private SpotListAdapterListener listener;

    public SpotsListAdapter(Spot[] spots, SpotListAdapterListener listener) {
        this.spots = spots;
        this.listener = listener;
    }

    @Override
    public SpotsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spot_list_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SpotsListAdapter.ViewHolder holder, final int position) {
        holder.mSpotName.setText(spots[position].getName());
        holder.mSpotAddress.setText(spots[position].getAddress());

        holder.mItemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SpotsListAdapter.this.listener.onSpotClick(spots[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spots.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSpotName;
        public TextView mSpotAddress;
        public View mItemView;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mSpotName = (TextView) itemLayoutView.findViewById(R.id.spot_name);
            mSpotAddress = (TextView) itemLayoutView.findViewById(R.id.spot_address);
            mItemView = itemLayoutView;
        }
    }
}

