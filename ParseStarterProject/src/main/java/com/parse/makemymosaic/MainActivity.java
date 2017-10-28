
package com.parse.makemymosaic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.CompressFormat.PNG;


public class MainActivity extends AppCompatActivity {

    private ImageView imgs;
    private TextView txt, nxt, uber;
    private static final int REQUEST_IMAGE_GET = 1;
    Uri fullPhotoUri;
    Toolbar toolbar;
    String url, imgsUrl,abtUs;
    CirclePageIndicator pageIndicator;
    RecyclerView hori;
    int count = 0;
    String t = "Reset";
    String x = "Choose a pic";
    String email;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bitmap dest;
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    private List<AutoImages> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgs = (ImageView) findViewById(R.id.imgs);
        txt = (TextView) findViewById(R.id.choosePic);
        nxt = (TextView) findViewById(R.id.nxt);
        toolbar = (Toolbar) findViewById(R.id.main_actionBar);
        uber = (TextView) findViewById(R.id.uber);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MakeMyMoasic");
        }

        uber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Uber.class));
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                imgs.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        new RemoteDataTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();

            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fullPhotoUri);

                resize(mBitmap, 900, 900);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void resize(Bitmap mBitmap, int maxWidth, int maxHeight) {

        if (maxHeight > 0 && maxWidth > 0) {
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();


            float xScale = (float) maxWidth / width;
            float yScale = (float) maxHeight / height;
            float scale = Math.max(xScale, yScale);

            float scaledWidth = scale * width;
            float scaledHeight = scale * height;

            float left = (maxWidth - scaledWidth) / 2;
            float top = (maxHeight - scaledHeight) / 2;

            RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

             dest = Bitmap.createBitmap(maxWidth, maxHeight, mBitmap.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(mBitmap, null, targetRect, null);

            imgs.setImageBitmap(dest);

            final boolean hasDrawable = (imgs.getDrawable() != null);

            if (hasDrawable) {
                txt.setText(t);
                nxt.setVisibility(View.VISIBLE);
            } else {

                txt.setText(x);

            }

            nxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new RemoteDataTask2().execute();


                }
            });


        } else {
            Log.i("sand", "nai hua");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();


            try {
                ParseQuery<ParseObject> q = new ParseQuery<>("Images");
                ParseQuery<ParseObject> d = new ParseQuery<ParseObject>("Splash");
                d.whereEqualTo("objectId","rD0P7W3Qf4");
                q.orderByAscending("rank");

                List<ParseObject> objects = q.find();
                List<ParseObject> objects1 = d.find();

                for (final  ParseObject sandy:objects1){
                     abtUs = "http://"+sandy.get("aboutUs");
                     email = (String) sandy.get("email");
                }

                for (final ParseObject images : objects) {

                    if (images != null) {
                        ParseFile ff = images.getParseFile("image");
                        if (ff != null) {
                            imgsUrl = ff.getUrl();
                        }
                        AutoImages ai = new AutoImages();
                        ai.setImages(imgsUrl);
                        ai.setImagesName((String) images.get("imageName"));
                        list.add(ai);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new ViewPagerAdapter(getApplicationContext(),list);
            viewPager.setAdapter(adapter);

            pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
            pageIndicator.setViewPager(viewPager);
            Log.i("sand", String.valueOf(list.size()));

            /*hori = (RecyclerView) findViewById(R.id.hori);
            hori.setNestedScrollingEnabled(false);
            LinearLayoutManager horiManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
            final HoriAdapter adapter = new HoriAdapter(MainActivity.this, list);
            hori.setLayoutManager(horiManager);
            hori.setItemAnimator(new DefaultItemAnimator());
            hori.setAdapter(adapter);*/


          /*  Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count <= adapter.getItemCount()) {
                                hori.smoothScrollToPosition(count);
                                count++;
                            } else {
                                count = 0;
                                hori.smoothScrollToPosition(count);
                            }
                        }
                    });
                }
            }, 500, 3000);*/

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String s = "\n           \n\n";
                s = s + url + "\n\n";
                i.putExtra(Intent.EXTRA_TEXT, s);
                startActivity(Intent.createChooser(i, "choose one"));

            case R.id.action_aboutUs:
                Intent x = new Intent(Intent.ACTION_VIEW);
                x.setData(Uri.parse(abtUs));
                startActivity(x);

            case R.id.action_feedback:
                Intent y = new Intent(Intent.ACTION_SEND);
                y.setData(Uri.parse("mailto:"));
                y.setType("text/plain");
                y.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                startActivity(y);

        }


        return super.onOptionsItemSelected(item);
    }

    private class RemoteDataTask2 extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            dest.compress(PNG, 50, stream1);

            byte[] image1 = stream1.toByteArray();

            final ParseFile file1 = new ParseFile( "sandeep.png", image1);

            file1.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){

                        ParseObject pd = ParseObject.createWithoutData("Images", "KvCbMClkFd");
                        pd.put("pImage1", file1);
                        pd.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.i("Parse", "Saved");
                                } else {
                                    Log.i("Parse1", e.toString());
                                }
                            }
                        });
                    }else {
                        Log.i("sand", e.toString());
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final boolean hasDrawable = (imgs.getDrawable() != null);
            if (!hasDrawable) {
                Toast.makeText(MainActivity.this, "Please Select An Image", Toast.LENGTH_LONG).show();
            }
            if (fullPhotoUri != null) {
                Intent in1 = new Intent(MainActivity.this, Chunked.class);
                in1.putExtra("image", fullPhotoUri.toString());
                startActivity(in1);
            } else {

                //    Toast.makeText(MainActivity.this,"Please select an image first",Toast.LENGTH_LONG).show();
            }
        }
    }
}
