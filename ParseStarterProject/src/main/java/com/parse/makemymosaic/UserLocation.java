package com.parse.makemymosaic;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//add requested column to parse user

public class UserLocation extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    TextView rqstUber, cancelRqst, otp, close;
    Boolean requestActive = false;
    Location location;
    String driverUsername = "", cabId = "",driverName;
    ParseGeoPoint driverLocation = new ParseGeoPoint(0, 0);
    ParseGeoPoint driverChangedLocation = new ParseGeoPoint(0, 0);
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    Object dataTransfer[] = new Object[2];
    String url;
    GetDirectionsData getDirectionsData;
    Handler handler = new Handler();
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i = 0, randomNo;
    LinearLayout rqstRel, rqstRel2;
    RelativeLayout rqstRel1;
    Random r = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        rqstUber = (TextView) findViewById(R.id.rqstUber);
        cancelRqst = (TextView) findViewById(R.id.cancelRqst);
        otp = (TextView) findViewById(R.id.otp);
        close = (TextView) findViewById(R.id.close);
        rqstRel = (LinearLayout) findViewById(R.id.rqstRel);
        rqstRel1 = (RelativeLayout) findViewById(R.id.rqstRel1);
        rqstRel2 = (LinearLayout) findViewById(R.id.rqstRel2);


        Intent x = getIntent();
        cabId = x.getStringExtra("cabId");
        driverName = x.getStringExtra("driverName");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rqstUber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rqstRel.setVisibility(View.VISIBLE);
                rqstRel1.setVisibility(View.VISIBLE);

                if (!requestActive) {
                    mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
                    mProgressBar.setProgress(i);
                    mCountDownTimer = new CountDownTimer(120000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                            i++;
                            mProgressBar.setProgress((int) i * 100 / (120000 / 1000));

                            if (!driverUsername.equals("")) {
                                randomNo = r.nextInt((100000 - 10000) + 1) + 10000;
                                rqstRel1.setVisibility(View.GONE);
                                rqstRel2.setVisibility(View.VISIBLE);
                                rqstUber.setVisibility(View.GONE);
                                mCountDownTimer.cancel();
                            }

                        }

                        @Override
                        public void onFinish() {
                            //Do what you want
                            i++;
                            mProgressBar.setProgress(100);
                            removeObjectFromDatabase();
                            finish();
                        }
                    };
                    mCountDownTimer.start();
                    ParseObject request = new ParseObject("Requests");
                    request.put("requesterUserName", ParseUser.getCurrentUser().getUsername());
                    request.put("driverName",driverName);

                    ParseACL parseACL = new ParseACL();
                    parseACL.setPublicWriteAccess(true);
                    parseACL.setPublicReadAccess(true);
                    request.setACL(parseACL);

                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                rqstUber.setText("Show OTP");
                                requestActive = true;
                                updateLocation(location);
                                rqstUber.setVisibility(View.GONE);


                            } else {
                                Log.i("sand", "Uber not Requested");
                            }
                        }
                    });
                } else {
                    rqstRel1.setVisibility(View.GONE);
                    rqstRel2.setVisibility(View.VISIBLE);
                }

            }
        });

        cancelRqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeObjectFromDatabase();
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rqstRel.setVisibility(View.GONE);    //hiding new Rel layout
                rqstUber.setVisibility(View.VISIBLE);
                rqstUber.setText("Show OTP");        // text view below map
            }
        });


    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void removeObjectFromDatabase() {
        requestActive = false;

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");

        query.whereEqualTo("requesterUserName", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            rqstRel.setVisibility(View.GONE);    //hiding new Rel layout
                            object.deleteInBackground();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener((GoogleMap.OnMarkerDragListener) this);
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    protected synchronized void buildGoogleApiClient() {

        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    private void updateLocation(final Location location) {

        if (ParseUser.getCurrentUser() != null) {

            if (driverUsername.equals("")) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location"));

            }
            if (requestActive) {

                if (!driverUsername.equals("")) {

                    ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                    userParseQuery.whereEqualTo("username", driverUsername);
                    userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {

                            if (e == null) {

                                if (objects.size() > 0) {

                                    for (ParseObject object : objects) {
                                        driverLocation = object.getParseGeoPoint("location");
                                    }

                                    if (driverChangedLocation != null) {
                                        double dis = distance(driverChangedLocation.getLatitude(), driverChangedLocation.getLongitude(), driverLocation.getLatitude(), driverLocation.getLongitude());
                                        if (dis >= 1) {
                                            dataTransfer = new Object[3];
                                            url = getDirectionsUrl(location);
                                            getDirectionsData = new GetDirectionsData();
                                            dataTransfer[0] = mMap;
                                            dataTransfer[1] = url;
                                            dataTransfer[2] = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());

                                            getDirectionsData.execute(dataTransfer);

                                            driverChangedLocation = driverLocation;
                                        } else {
                                            Log.i("driverLocation2", String.valueOf(dis));

                                        }
                                    } else {
                                        driverChangedLocation = driverLocation;
                                    }

                                }

                            }

                        }
                    });

                    if (driverLocation.getLatitude() != 0 && driverLocation.getLongitude() != 0) {


                        Double distanceInKm = driverLocation.distanceInKilometersTo(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                        Double disOneDp = (double) Math.round(distanceInKm * 10) / 10;

                        //uberMsg.setText("Your driver is " + disOneDp + " km away");

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        ArrayList<Marker> markers = new ArrayList<>();

                        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Driver Location")));

                        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("User Location")));


                        for (Marker marker : markers) {

                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();

                        int padding = 50;

                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);

                    }

                }
                final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Requests");

                query.whereEqualTo("requesterUserName", ParseUser.getCurrentUser().getUsername());

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {
                                for (ParseObject object : objects) {
                                    object.put("requesterLocation", userLocation);
                                    int parseRandomNo = object.getInt("otp");
                                    if (parseRandomNo != 0) {       // for checking if otp exists bcoz updatelocation method will update after 10sec
                                        object.put("otp", parseRandomNo);
                                        otp.setText(String.valueOf(parseRandomNo));
                                    } else {
                                        ParseUser.getCurrentUser().put("requested", true);
                                        ParseUser.getCurrentUser().put("cabId", cabId);// // TODO: 26-10-2017  add cab id
                                        ParseUser.getCurrentUser().saveInBackground();          //add requested column to parse user
                                        otp.setText(String.valueOf(randomNo));
                                        object.put("otp", randomNo);    // if not exists then fresh random no will b updated to parse
                                    }

                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.i("mt app", "done");
                                            } else {
                                                Log.i("mt app", e.toString());
                                            }
                                        }
                                    });

                                }
                            }
                        }

                    }
                });

                if (getDirectionsData != null) {
                    String duration = getDirectionsData.duration;
                    if (duration != null) {
                        Log.i("duration", duration);
                    }
                }


            } else {
                refreshName();
            }

            // reverseTimer(5);

            final Runnable r = new Runnable() {
                public void run() {
                    Log.i("sec", "sec");
                    if (driverUsername.equals("")) {
                        refreshName();
                        updateLocation(location);
                    } else {
                        updateLocation(location);
                    }
                }
            };

            handler.postDelayed(r, 10000);
        }
    }

    private String getDirectionsUrl(Location location) {
        Log.i("sandeepDHami", driverLocation.getLatitude() + "," + driverLocation.getLongitude() + "," + location.getLatitude() + "," + location.getLongitude());
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + location.getLatitude() + "," + location.getLongitude());
        googleDirectionsUrl.append("&destination=" + driverLocation.getLatitude() + "," + driverLocation.getLongitude());
        googleDirectionsUrl.append("&key=" + "AIzaSyApUV0esfjnRGRGDw0O1kqgPRDpRe5UANk");   //  MyApp2


        return googleDirectionsUrl.toString();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyAUB9z9kpCr9bjVpNUHi4DOMbD-exTseHg");


        Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    private void refreshName() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        if (ParseUser.getCurrentUser() != null) {
            query.whereEqualTo("requesterUserName", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {

                        if (objects.size() > 0) {           // check whether requester has requested cab

                            for (ParseObject object : objects) {

                                requestActive = true;

                                rqstUber.setText("Show OTP");

                                if (object.get("driverUsername") != null) {

                                    driverUsername = object.getString("driverUsername");
                                    //  rqstUber.setVisibility(View.INVISIBLE);
                                    Log.i("sandeepD", driverUsername);
                                } else {
                                    Log.i("sandeep", "nai hua");
                                }

                            }
                        }

                    }
                }
            });
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void onLocationChanged(Location location) {


        mMap.clear();
        this.location = location;

        Location currLoc = location;
        if (currLoc.hasAccuracy()) {
            // Accuracy is in rage of 20 meters, stop listening we have a fix
            if (currLoc.getAccuracy() < 30) {
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }
        }
        updateLocation(location);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    private float distance(double currentlatitude, double currentlongitude, double originLat, double originLon) {

        float[] results = new float[1];
        Location.distanceBetween(currentlatitude, currentlongitude, originLat, originLon, results);
        float distanceInMeters = results[0];

        return distanceInMeters;
    }
}
