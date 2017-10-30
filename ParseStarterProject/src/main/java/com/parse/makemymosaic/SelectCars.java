package com.parse.makemymosaic;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SelectCars extends AppCompatActivity {

    LinearLayout micro, sedan, hatch, suv;
    String type;
    String imgUrl;
    private List<Cabs_G_S> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cars);

        micro = (LinearLayout) findViewById(R.id.microImg);
        sedan = (LinearLayout) findViewById(R.id.sedanImg);
        hatch = (LinearLayout) findViewById(R.id.hatchImg);
        suv = (LinearLayout) findViewById(R.id.suvImg);

        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "micro";
            }
        });
        sedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "sedan";
            }
        });
        hatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "hatch";
            }
        });
        suv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "suv";
            }
        });

        new SelectCarsTask().execute();

    }

    private class SelectCarsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            ParseQuery<ParseObject> query = new ParseQuery<>("Cabs");
            query.whereEqualTo("online", true);
            List<ParseObject> object1 = null;

            try {
                object1 = query.find();
                for (final ParseObject country : object1) {


                    ParseFile pf = country.getParseFile("cabImage");
                    if (pf != null) {
                        imgUrl = pf.getUrl();
                    }
                    Log.i("names",country.getList("seats").toString());
                    Cabs_G_S cabsGS = new Cabs_G_S();
                    cabsGS.setCabImages(imgUrl);
                    cabsGS.setCabName(country.getString("cabName"));
                    cabsGS.setDriverName(country.getString("driverName"));
                    cabsGS.setPrice(country.getString("price"));
                    cabsGS.setBookedSeats(country.getInt("bookedSeats"));
                    cabsGS.setTotalSeats(country.getInt("totalNoOfSeats"));
                    cabsGS.setDiscount(country.getInt("discount"));
                    cabsGS.setDriverLocation(country.getParseGeoPoint("driverLocation"));
                    cabsGS.setObjId(country.getObjectId());
                    list.add(cabsGS);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cabsRecy);
            recyclerView.setNestedScrollingEnabled(false);
            JobsAdapter load = new JobsAdapter(SelectCars.this,list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(SelectCars.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(load);


        }
    }
}
