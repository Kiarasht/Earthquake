package com.restart.earthquake;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.restart.earthquake.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Main activity that holds the recyclerview and a google map fragment
 */
public class ScrollingActivity extends Activity implements EarthQuakeAdapter.ListItemClickListener,
        EarthQuakeAdapter.ListItemLongClickListener, OnMapReadyCallback {

    private final static String TAG = ".ScrollingActivity";
    private final static String GEO_NAMES_URL = "http://api.geonames.org/earthquakesJSON?formatted=" +
            "true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=mkoppelman";

    private ProgressBar mProgress;
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private EarthQuakeAdapter mAdapter;
    private ArrayList<EarthQuake> mDataSet;
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private boolean mMapReset;

    /**
     * Set up variables and start an async task to process a network call in the background
     * <p>
     * Restore any data that was previously saved so we don't make the user wait or put too much
     * pressure on the API services.
     *
     * @param savedInstanceState get data back from the bundle if this isn't a first time open
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        mActivity = this;
        mProgress = (ProgressBar) findViewById(R.id.httpWait);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.quakesRecyclerList);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new EarthQuakeAdapter(this, this, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            mDataSet = new ArrayList<>();
            mAdapter.setDataSet(mDataSet);
            mRecyclerView.setAdapter(mAdapter);
            try {
                Uri earthQuakesUri = Uri.parse(GEO_NAMES_URL);
                URL earthQuakesURL = new URL(earthQuakesUri.toString());
                new EarthQuakesAsyncTask().execute(earthQuakesURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            mDataSet = savedInstanceState.getParcelableArrayList("mDataSet");
            mAdapter.setDataSet(mDataSet);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mProgress.setVisibility(View.GONE);
            mMapReset = true;
        }
    }

    /**
     * Save our list of data that we worked to hard to request, parse, and display so that on
     * a configuration change it would still stay with us. Wouldn't be just such a shame to lose
     * a list of such beauty?
     *
     * @param outState Save it in the bundle, we will grab it back onRestoreInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) { // TODO: Current layout sizes, and current googleMap position should also be saved.
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mDataSet", mDataSet);
    }

    /**
     * If an earth quake in the recyclerview was clicked, animate the google maps and move to the
     * location of that earth quake.
     *
     * @param index position of the adapter that was clicked
     */
    @Override
    public void onListItemClick(int index) {
        if (mGoogleMap != null) {
            EarthQuake currentEarthQuake = mDataSet.get(index);
            LatLng newPosition = new LatLng(currentEarthQuake.getLat(), currentEarthQuake.getLng());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 10), 3000, null);
        }
    }

    /**
     * If an earth quake in the recyclerview was long clicked, animate the google maps and move to the
     * location of that earth quake. Different from just a click in that google maps takes the whole
     * screen now and will go back to its original size by pressing phones back button.
     *
     * @param index position of the adapter that was clicked
     */
    @Override
    public void onListLongItemClick(int index) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        mMapFragment.getView().setLayoutParams(param);

        onListItemClick(index);
    }

    /**
     * Initial map position when google map is ready and to save the googleMap variable for later use.
     *
     * @param googleMap incoming ready to be used googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng bestPlaceEver = new LatLng(45.513734, -122.680087);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bestPlaceEver, 17));

        if (mMapReset) {
            onMapDraw();
        }
    }

    /**
     * Draw custom markers on the map when the map is ready and we grabbed our data or a
     * configuration changed occurred.
     *
     * Data on the map has different colors based on the severity of the earth quake.
     */
    private void onMapDraw() {
        for (EarthQuake aEarthQuake : mDataSet) {
            LatLng newPosition = new LatLng(aEarthQuake.getLat(), aEarthQuake.getLng());

            /* Custom marker */
            mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_danger))
                    .anchor(0.5f, 0.5f) // Anchors the marker on the bottom left
                    .position(newPosition));

            double magnitude = aEarthQuake.getMagnitude();
            int stroke, fill;

            /* Circles color depends on the magnitude of an earth quake. */
            if (magnitude > 8) {
                stroke = ContextCompat.getColor(mActivity, R.color.strokeCircleDanger);
                fill = ContextCompat.getColor(mActivity, R.color.fillCircleDanger);
            } else if (magnitude > 4) {
                stroke = ContextCompat.getColor(mActivity, R.color.strokeCircleWarning);
                fill = ContextCompat.getColor(mActivity, R.color.fillCircleWarning);
            } else {
                stroke = ContextCompat.getColor(mActivity, R.color.strokeCircleSafe);
                fill = ContextCompat.getColor(mActivity, R.color.fillCircleSafe);
            }

            /* Circles size depends slightly on the magnitude of an earth quake. */
            mGoogleMap.addCircle(new CircleOptions()
                    .center(newPosition)
                    .radius(1000 * aEarthQuake.getMagnitude())
                    .strokeColor(stroke)
                    .fillColor(fill));
        }
    }

    /**
     * Override the onBackPressed button to first make the map fragment go back to its original size
     * before allowing the user to exit the app.
     */
    @Override
    public void onBackPressed() {
        ViewGroup.LayoutParams layoutParams = mMapFragment.getView().getLayoutParams();

        if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.25f
            );
            mMapFragment.getView().setLayoutParams(param);
            return;
        }

        finish();
    }

    /**
     * AsyncTask that will make a single call to geonames.org to grab the JSON of earth quakes, and
     * another one for each earth quake found.
     * <p>
     * It will use the lat and lng of geonames.org and Google reverse geocoding API to find an address
     * from that lat and lng. If no address was found (possible if earth quake was at an ocean)
     * then the lat and lng will be printed.
     */
    private class EarthQuakesAsyncTask extends AsyncTask<URL, Void, Void> {

        /**
         * Start the background process which includes the network call and the parsing of resulted
         * JSON. More network calls will be make for each earth quake to find their respective addresses.
         *
         * @param params Incoming URL to open an http connection with
         * @return N/A
         */
        @Override
        protected Void doInBackground(URL... params) {
            try {
                String resultsJSON = NetworkUtils.HttpResponse(params[0]);
                JSONObject earthQuakesJSON = new JSONObject(resultsJSON);
                JSONArray earthQuakes = earthQuakesJSON.getJSONArray("earthquakes");

                for (int i = 0; i < earthQuakes.length(); ++i) {
                    JSONObject oneEarthQuake = earthQuakes.getJSONObject(i);
                    String dateTime = oneEarthQuake.getString("datetime");
                    String src = oneEarthQuake.getString("src");
                    String eqid = oneEarthQuake.getString("eqid");
                    double magnitude = oneEarthQuake.getDouble("magnitude");
                    double depth = oneEarthQuake.getDouble("depth");
                    double lat = oneEarthQuake.getDouble("lat");
                    double lng = oneEarthQuake.getDouble("lng");

                    /* Url to find each earth quake address. Typically api key shouldn't be in the main program
                    specially if it ends up being open source */
                    final String urlAddress = "https://maps.googleapis.com/maps/api/geocode/json?" +
                            "latlng=" + lat + "," + lng + "&" +
                            "key=" + "AIzaSyAmjoXtBCPEokzGQZrUnOnmSWFWdwWueo4";

                    String resultsAddress = NetworkUtils.HttpResponse(new URL(Uri.parse(urlAddress).toString()));
                    JSONObject addressJSON = new JSONObject(resultsAddress);
                    String address = null;

                    /* Check if we found an address from a single earth quake that was just processed */
                    if (addressJSON.getString("status").equals("OK")) {
                        try {
                            JSONArray addressArray = addressJSON.getJSONArray("results");
                            address = addressArray.getJSONObject(0).getString("formatted_address");
                        } catch (JSONException e) {
                            address = null;
                        }
                    }

                    /* Finish the process by creating a new earth quake */
                    EarthQuake newEarthQuake = new EarthQuake(dateTime, src, eqid, magnitude, depth, address, lat, lng);
                    mDataSet.add(newEarthQuake);
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable in grabbing data");
                e.printStackTrace();
            } catch (SecurityException e) {
                Log.e(TAG, "Permission to access internet was denied");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, "Unable in parsing data");
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Manipulate the map using the data we just received from doInBackground.
         *
         * @param ignored N/A
         */
        @Override
        protected void onPostExecute(Void ignored) {
            onMapDraw();
            mAdapter.notifyDataSetChanged();
            mProgress.setVisibility(View.GONE);
        }
    }
}
