package com.restart.earthquake;


import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.restart.earthquake.utilities.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

class EarthQuakeAdapter extends RecyclerView.Adapter<EarthQuakeAdapter.EarthQuakeAdapterViewHolder> {
    private final ListItemLongClickListener mOnListItemLongClickListener;
    private final ListItemClickListener mOnListItemClickListener;
    private List<EarthQuake> mDataSet;
    private Activity mActivity;

    /**
     * Initialize adapter by bring some data to create onClick listeners and gain access to resources
     *
     * @param activity     reference to the main activity
     * @param listener     for tapping on recyclerview
     * @param listenerLong for long tapping on recyclerview
     */
    EarthQuakeAdapter(Activity activity, ListItemClickListener listener, ListItemLongClickListener listenerLong) {
        mActivity = activity;
        mOnListItemClickListener = listener;
        mOnListItemLongClickListener = listenerLong;
    }

    interface ListItemClickListener {
        void onListItemClick(int index);
    }

    interface ListItemLongClickListener {
        void onListLongItemClick(int index);
    }

    /**
     * Managing our main recyclerview
     */
    class EarthQuakeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView mMagnitude;
        private final TextView mDate;
        private final TextView mAddress;
        private final TextView mDepth;
        private final TextView mSource;
        private final TextView mID;

        /**
         * Setup the basic layout of a row in the recycler view. Create both the click and long click listeners.
         */
        EarthQuakeAdapterViewHolder(View view) {
            super(view);
            mMagnitude = (TextView) view.findViewById(R.id.magnitudeText);
            mDate = (TextView) view.findViewById(R.id.date);
            mAddress = (TextView) view.findViewById(R.id.address);
            mDepth = (TextView) view.findViewById(R.id.depth);
            mSource = (TextView) view.findViewById(R.id.source);
            mID = (TextView) view.findViewById(R.id.id);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mOnListItemLongClickListener.onListLongItemClick(getAdapterPosition());
            return true;
        }
    }

    /**
     * Inflate the layout so we can access each of the individual views
     *
     * @param parent
     * @param viewType
     * @return view to use and find our widgets
     */
    @Override
    public EarthQuakeAdapter.EarthQuakeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earth_quake_row, parent, false);
        return new EarthQuakeAdapterViewHolder(view);
    }

    /**
     * On a bind, setup the properties of the ViewHolder by also knowing which position it current
     * rests in
     *
     * @param holder   incoming attached ViewHolder
     * @param position position it is in the adapter
     */
    @Override
    public void onBindViewHolder(EarthQuakeAdapter.EarthQuakeAdapterViewHolder holder, int position) {
        EarthQuake current = mDataSet.get(position);
        String dateTime = current.getDateTime();
        String address = current.getAddress();
        double magnitude = current.getMagnitude();
        double depth = current.getDepth();

        /* Set the background based on the earth quake strength */
        if (magnitude > 8) {
            holder.mMagnitude.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.magnitudeDanger));
        } else if (magnitude > 4) {
            holder.mMagnitude.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.magnitudeWarning));
        } else {
            holder.mMagnitude.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.magnitudeSafe));
        }

        /* Set the depth and its color based on its strength. Categorized into three different levels.
        1. Shallow: 0 to 70 km deep
        2. Intermediate: 70 to 300 km deep
        3. Deep: 300 to 700 km deep
        For more info: https://earthquake.usgs.gov/learn/topics/determining_depth.php */
        if (depth > 300) {
            holder.mDepth.setText("Deep");
            holder.mDepth.setTextColor(ContextCompat.getColor(mActivity, R.color.magnitudeDanger));
        } else if (depth > 70) {
            holder.mDepth.setText("Intermediate");
            holder.mDepth.setTextColor(ContextCompat.getColor(mActivity, R.color.magnitudeWarning));
        } else {
            holder.mDepth.setText("Shallow");
            holder.mDepth.setTextColor(ContextCompat.getColor(mActivity, R.color.magnitudeSafe));
        }

        /* Simply set the magnitude, we previously set its background */
        holder.mMagnitude.setText(String.valueOf(magnitude));

        /* Parse the date to a more readable version. One that shows how long before the earth quake occurred */
        try {
            Date time = DateUtils.parseDate(dateTime);
            holder.mDate.setText(DateUtils.getDateDifference(time.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* If no address was found by Google reverse geocoding, then just display lat & lng */
        if (address != null && !"".equals(address)) {
            holder.mAddress.setText(current.getAddress());
        } else {
            holder.mAddress.setText(current.getLat() + ", " + current.getLng());
        }

        holder.mSource.setText("src: " + current.getSrc());
        holder.mID.setText("id: " + current.getEqID());
    }

    /**
     * Recycler view size is based on the size of our data list
     *
     * @return size of recyclerview
     */
    @Override
    public int getItemCount() {
        if (mDataSet == null) return 0;
        return mDataSet.size();
    }

    /**
     * Set the data list of recyclerview to a new one
     *
     * @param dataSet the new data list
     */
    void setDataSet(List<EarthQuake> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }
}
