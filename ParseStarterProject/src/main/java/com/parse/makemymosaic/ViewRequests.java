package com.parse.makemymosaic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.parse.FindCallback;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

// booked seats

public class ViewRequests extends AppCompatActivity implements LocationListener {


    ArrayList<String> listViewContent;
    ArrayList<String> usernames;
    ArrayList<Double> lat;
    ArrayList<Double> lng;
    ArrayAdapter arrayAdapter;
    LocationManager locationManager;
    String provider;
    Location location;
    ParseGeoPoint userLocation;
    LocationCallback callback;
    Object dataTransfer[] = new Object[2];
    String url;
    GetDirectionsData getDirectionsData;
    ParseGeoPoint requesterLocation;
    ParseGeoPoint requesterSecondLocation;
    RecyclerView recyclerView;
    private List<Rqst_G_S> list = new ArrayList<>();
    int newSize = 0, oldSize = 0;
    private int mInterval = 5000;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        recyclerView = (RecyclerView) findViewById(R.id.viewRqst);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
            return;
        }

        callback = new LocationCallback() {
            @Override
            public void done(ParseGeoPoint geoPoint, ParseException e) {

            }
        };
        locationManager.requestLocationUpdates(provider, 400, 1, this);

        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            //updateLocation();
            onLocationChanged(location);
        }
        time();

    }

    private void time() {
        int timeMiliseconds = 10000; // in milliseconds = 2seconds
        int tickTime = 1000; // in milliseconds - 1 second - we trigger onUpdate in intervals of this time
        new BataTime(timeMiliseconds, tickTime).start(new BataTimeCallback() {
            @Override
            public void onUpdate(int elapsed) {
                Log.i("sec", "On update called...time elapsed = " + elapsed);
            }

            @Override
            public void onComplete() {
                time();
                new ViewRqstTask(location).execute();

            }
        });
    }

    private String getDirectionsUrl(Location location) {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + location.getLatitude() + "," + location.getLongitude());
        googleDirectionsUrl.append("&destination=" + requesterLocation.getLatitude() + "," + requesterLocation.getLongitude());
        googleDirectionsUrl.append("&key=" + "AIzaSyApUV0esfjnRGRGDw0O1kqgPRDpRe5UANk");   //  MyApp2


        return googleDirectionsUrl.toString();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        //  locationManager.removeUpdates(this);

    }


    @Override
    public void onLocationChanged(Location location) {

        this.location = location;
        new ViewRqstTask(location).execute();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private float distance(double currentlatitude, double currentlongitude, double originLat, double originLon) {

        float[] results = new float[1];
        Location.distanceBetween(currentlatitude, currentlongitude, originLat, originLon, results);
        float distanceInMeters = results[0];

        return distanceInMeters;
    }

    private class ViewRqstTask extends AsyncTask<Void, Void, Void> {
        Location location;

        public ViewRqstTask(Location location) {
            this.location = location;
        }

        @Override
        protected Void doInBackground(Void... params) {

            userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            if (list != null) {
                list.clear();
            }
            if (requesterSecondLocation != null) {
                double dis = distance(userLocation.getLatitude(), userLocation.getLongitude(), requesterSecondLocation.getLatitude(), requesterSecondLocation.getLongitude());
                if (dis >= 1) {
                    ParseUser.getCurrentUser().put("location", userLocation);     //todo driver location
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseRelation<ParseObject> p = ParseUser.getCurrentUser().getRelation("cabsRelation");
                                ParseQuery p2 = p.getQuery();
                                List<ParseObject> oc = null;
                                try {
                                    oc = p2.find();

                                    for (ParseObject country2 : oc) {

                                        if (country2 != null) {
                                            if (listViewContent != null) {
                                                country2.put("bookedSeats", listViewContent.size());   // booked seats
                                            } else {
                                                country2.put("bookedSeats", 0);
                                            }
                                            country2.saveInBackground();

                                        }
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                Log.i("sandeepD", e.toString());
                            }
                        }
                    });
                    requesterSecondLocation = userLocation;
                }
            } else {
                requesterSecondLocation = userLocation;
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
            query.whereEqualTo("driverName", ParseUser.getCurrentUser().getUsername());
            query.whereNear("requesterLocation", userLocation);
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {

                        if (objects.size() > 0) {

                            for (ParseObject object : objects) {

                                /*if (object.get("driverUsername") == null) {*/

                                requesterLocation = object.getParseGeoPoint("requesterLocation");
                                LatLng from = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                                LatLng to = new LatLng(requesterLocation.getLatitude(), requesterLocation.getLongitude());

                                dataTransfer = new Object[3];
                                url = getDirectionsUrl(location);
                                getDirectionsData = new GetDirectionsData();
                                dataTransfer[0] = null;
                                dataTransfer[1] = url;
                                dataTransfer[2] = new LatLng(requesterLocation.getLatitude(), requesterLocation.getLongitude());

                                getDirectionsData.execute(dataTransfer);
                                //Double distanceInKm = userLocation.distanceInKilometersTo(object.getParseGeoPoint("requesterLocation"));
                                Double dis = SphericalUtil.computeDistanceBetween(from, to);
                                Double disOneDp = (double) Math.round(dis * 10) / 10;

                                Rqst_G_S rqstGS = new Rqst_G_S();
                                rqstGS.setLat(object.getParseGeoPoint("requesterLocation").getLatitude());
                                rqstGS.setLng(object.getParseGeoPoint("requesterLocation").getLongitude());
                                rqstGS.setUsernames(object.getString("requesterUserName"));
                                rqstGS.setLocation(location);
                                rqstGS.setListViewContent(String.valueOf(disOneDp) + " Km");
                                list.add(rqstGS);
                            }
                            newSize = list.size();
                        }

                    } else {
                        Log.i("sandeep", e.toString());
                    }

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (newSize > oldSize) {
                Log.i("size", String.valueOf(newSize + " " + oldSize));
                recyclerView.setNestedScrollingEnabled(false);
                VR_Adapter load = new VR_Adapter(ViewRequests.this, list, location);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ViewRequests.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(load);
                oldSize = newSize;
                newSize = 0;
            }else {
                if (oldSize>newSize){
                    oldSize = newSize;
                }
            }
        }
    }
}
