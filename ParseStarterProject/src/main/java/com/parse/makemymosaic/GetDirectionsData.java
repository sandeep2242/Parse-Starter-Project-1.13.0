package com.parse.makemymosaic;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lenovo on 20-10-2017.
 */

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadUrl downloadURL = new DownloadUrl();
        try {
            googleDirectionsData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;


    }

    @Override
    protected void onPostExecute(String s) {
        try {
            HashMap<String, String> distanceList = null;    // for duration and distance
            DataParser dataParser1 = new DataParser();
            distanceList = dataParser1.parseDistance(s);

            duration = distanceList.get("duration");
            distance = distanceList.get("distance");

            if (mMap != null)
                mMap.clear();
            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(latLng);
            markerOptions.draggable(true);
            markerOptions.title(duration);
            markerOptions.title(distance);

            Log.i("sandeep", distance + " " + duration);
            if (mMap != null) {
                mMap.addMarker(markerOptions);

                String[] directionsList;

                if (s != null) {
                    DataParser dataParser = new DataParser();
                    directionsList = dataParser.parseDirections(s);

                    displayDirections(directionsList);
                }
            }
        } catch (Exception e) {
            Log.i("error", e.toString());
        }

    }


    private void displayDirections(String[] directionsList) {

        for (String aDirectionsList : directionsList) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(aDirectionsList));

            mMap.addPolyline(options);
        }
    }
}
