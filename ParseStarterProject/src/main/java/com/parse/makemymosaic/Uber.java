package com.parse.makemymosaic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.parse.ParseUser;

public class Uber extends AppCompatActivity {

    Button getStrted;
    Switch rOrD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber);

        getStrted = (Button) findViewById(R.id.getStarted);
        rOrD = (Switch) findViewById(R.id.riderOrDriver);


        getStrted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String riderOrDriver = "rider";
                if (rOrD.isChecked()) {
                    riderOrDriver = "driver";
                }


                Intent i = new Intent(Uber.this,Login.class);
                i.putExtra("rod",riderOrDriver);
                startActivity(i);
                /*ParseUser.getCurrentUser().put("riderOrDriver",riderOrDriver);

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("sandeep", "Anonymus user logged in");
                            redirectUser();
                        } else {
                            Log.i("sandeep", "Anonymus user not logged in");
                        }
                    }
                });*/
            }
        });

   /*     if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.i("sand", "Anonymus logged in");
                    } else {
                        Log.i("sand", "Anonymus user not logged in");
                    }
                }
            });
        } else {
            if (ParseUser.getCurrentUser().get("riderOrDriver")!=null){
                redirectUser();
            }
        }*/


//        Log.i("sandeep12", String.valueOf(ParseUser.getCurrentUser().get("riderOrDriver")));

        rOrD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });
    }

    public  void redirectUser(){

        if (ParseUser.getCurrentUser().get("riderOrDriver").equals("rider")){
            startActivity(new Intent(Uber.this,UserLocation.class));
        }else {
            startActivity(new Intent(Uber.this,ViewRequests.class));
        }
    }
}
