package com.maryf.spotkeeper;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import com.maryf.spotkeeper.contentproviders.SpotsContentProvider;
import com.maryf.spotkeeper.model.Spot;


/**
 * Created by maryf on 4/5/2017.
 */

public class SpotsListAdapter extends RecyclerView.Adapter<SpotsListAdapter.ViewHolder> {

    public interface SpotListAdapterListener {
        void onSpotClick(Spot spot);
        void onSpotLongClick(Spot spot, View v);
    }

    private SpotListAdapterListener listener;
    private Cursor cursor;

    public SpotsListAdapter(SpotListAdapterListener listener) {
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
        cursor.moveToPosition(position);
        final Spot spot = new Spot(
                cursor.getLong(cursor.getColumnIndex(SpotsContentProvider.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(SpotsContentProvider.COLUMN_SPOT_NAME)),
                cursor.getString(cursor.getColumnIndex(SpotsContentProvider.COLUMN_SPOT_ADDRESS))
        );

        holder.mSpotName.setText(spot.getName());
        holder.mSpotAddress.setText(spot.getAddress());

        holder.mItemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SpotsListAdapter.this.listener.onSpotClick(spot);
            }
        });

        holder.mItemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                SpotsListAdapter.this.listener.onSpotLongClick(spot, v);
                return true;
            }

        });
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
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

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}

