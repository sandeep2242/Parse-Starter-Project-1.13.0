package com.parse.makemymosaic;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Destination extends AppCompatActivity {

    private Spinner from;
    private Spinner to;

    String Form="", To="";
    TextView fromText, toText,next;
    List<String> des = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        from = (Spinner) findViewById(R.id.from);
        to = (Spinner) findViewById(R.id.to);
        fromText = (TextView) findViewById(R.id.fromText);
        toText = (TextView) findViewById(R.id.toText);
        next = (TextView) findViewById(R.id.next);


        new DestinationTask().execute();
    }

    private class DestinationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ParseQuery<ParseObject> query = new ParseQuery<>("Destination");

                List<ParseObject> object1 = query.find();

                for (final ParseObject country : object1) {
                    // Locate images in flag column
                    des = country.getList("des");

                }

            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<String> adapter = new ArrayAdapter(Destination.this, android.R.layout.simple_spinner_item, des) {
                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


                    View view = super.getDropDownView(position, convertView, parent);

                    TextView tv = (TextView) view;
                    tv.setPadding(30, 30, 30, 30);
                    tv.setTextColor(Color.parseColor("#000000"));



                    // Set the Text color
                    try {
                        if (des.get(position).equals("none")) {
                            tv.setText("none");
                            tv.setBackgroundResource(R.color.white);
                            tv.setTextColor(Color.parseColor("#fff"));
                        }


                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        llp.setMargins(50, 50, 50, 50); // llp.setMargins(left, top, right, bottom);
                        tv.setLayoutParams(llp);
                        tv.setBackgroundColor(Color.parseColor(des.get(position).toString()));

                    } catch (Exception e) {
                        Log.i("sand", String.valueOf(e));
                        tv.setText(des.get(position).toString());
                    }
                    return view;
                }
            };
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            from.setAdapter(adapter);
            to.setAdapter(adapter);

            from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0) {
                        fromText.setText("From");
                        Form = "";
                    }else {
                        Form = parent.getItemAtPosition(position).toString();
                        if (To.equals(Form)){
                            Form = "";
                            Toast.makeText(Destination.this,"source and destination cannot be same",Toast.LENGTH_LONG).show();
                            fromText.setText("From");
                        }else {
                            fromText.setText(parent.getItemAtPosition(position).toString());
                        }

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        toText.setText("To");
                        To ="";
                    }else {
                        To = parent.getItemAtPosition(position).toString();
                        if (To.equals(Form)){
                            To ="";
                            Toast.makeText(Destination.this,"source and destination cannot be same",Toast.LENGTH_LONG).show();
                            toText.setText("To");
                        }else {
                            toText.setText(parent.getItemAtPosition(position).toString());
                        }

                    }


                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!To.isEmpty()&& !Form.isEmpty()){
                        Intent i = new Intent(Destination.this,SelectCars.class);
                        i.putExtra("srcDes",Form+To);
                        startActivity(i);
                    }

                }
            });

        }
    }
}
