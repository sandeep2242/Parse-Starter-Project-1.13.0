package com.parse.makemymosaic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Login extends AppCompatActivity {

    String rod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText user = (EditText) findViewById(R.id.username);
        final EditText pass = (EditText) findViewById(R.id.password);
        TextView sub = (TextView) findViewById(R.id.sumbit);
        TextView login = (TextView) findViewById(R.id.login);
        TextView list = (TextView) findViewById(R.id.list);


        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
            Log.i("sandeep", "Not null");
        }

        final Intent i = getIntent();
        rod = i.getStringExtra("rod");

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString();
                String password = pass.getText().toString();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.put("riderOrDriver", rod);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            if (rod.equals("rider")) {
                                startActivity(new Intent(Login.this, UserLocation.class));
                            } else if (rod.equals("driver")) {
                                startActivity(new Intent(Login.this, ViewRequests.class));
                            }
                        }
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString();
                String password = pass.getText().toString();
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps(username, password);
                } else {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                if (rod.equals("rider")) {
                                    startActivity(new Intent(Login.this, Destination.class));// UserLocation
                                } else if (rod.equals("driver")) {
                                    startActivity(new Intent(Login.this, ViewRequests.class));// ViewRequests
                                }
                            } else {
                                Log.i("sandeep", e.getMessage());
                            }
                        }
                    });
                }


            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Destination.class));
            }
        });


    }

    private void buildAlertMessageNoGps(final String username, final String password) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    if (rod.equals("rider")) {
                                        startActivity(new Intent(Login.this, UserLocation.class));
                                    } else if (rod.equals("driver")) {
                                        startActivity(new Intent(Login.this, ViewRequests.class));
                                    }
                                } else {
                                    Log.i("sandeep", e.getMessage());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(Login.this, "Please enable GPS ", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
