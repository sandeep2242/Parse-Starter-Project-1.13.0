package com.parse.makemymosaic;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ViewRiderLocation extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    Intent i;
    TextView acceptRqst;
    private ArrayList<LatLng> points; //added
    Polyline line; //added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rider_location);
        acceptRqst = (TextView) findViewById(R.id.acceptRqst);
        i = getIntent();
        Log.i("dhamiji",i.getDoubleExtra("userLat", 0)+" "+i.getDoubleExtra("userLng", 0));

        points = new ArrayList<LatLng>(); //added userLng
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        acceptRqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
                query.whereEqualTo("requesterUserName",i.getStringExtra("userNames"));
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                          if (objects.size()>0){
                              for (ParseObject object:objects){

                                  object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                                  object.saveInBackground(new SaveCallback() {
                                      @Override
                                      public void done(ParseException e) {
                                          if (e==null){
                                              Intent x = new Intent(Intent.ACTION_VIEW,
                                                      Uri.parse("https://maps.google.com/maps?daddr=" +i.getDoubleExtra("userLat",0)+ ","+i.getDoubleExtra("userLng",0)));

                                              startActivity(x);
                                          }
                                      }
                                  });


                              }
                          }

                        } else {

                        }

                    }
                });
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RelativeLayout mapLyout = (RelativeLayout) findViewById(R.id.mapLayout);
        mapLyout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                ArrayList<Marker> markers = new ArrayList<>();

                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(i.getDoubleExtra("lat", 0), i.getDoubleExtra("lng", 0))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Rider Location")));

                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(i.getDoubleExtra("userLat", 0), i.getDoubleExtra("userLng", 0))).title("User Location")));

                for (Marker marker : markers) {

                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 50;

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
                mMap.animateCamera(cu);
            }
        });




    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = i.getDoubleExtra("lat", 0);
        double longitude = i.getDoubleExtra("lng", 0);
        LatLng latLng = new LatLng(latitude, longitude); //you already have this

        double latitude2 = i.getDoubleExtra("userLat", 0);
        double longitude2 = i.getDoubleExtra("userLng", 0);
        LatLng latLng2 = new LatLng(latitude2, longitude2);

        PolylineOptions options = new PolylineOptions().add(latLng).add(latLng2).width(5).color(Color.BLUE).geodesic(true);
        mMap.addPolyline(options);

        Log.i("points", String.valueOf(latLng));
       // redrawLine(); //added
    }

    private void redrawLine(){

        mMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }


        line = mMap.addPolyline(options); //add Polyline
    }


}
