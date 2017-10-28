package com.parse.makemymosaic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Chunked extends AppCompatActivity {


    private ArrayList<String> imagesPathList;
    private final int PICK_IMAGE_MULTIPLE = 1;
    private ArrayList<Bitmap> arrrayImages;
    Bitmap bitmap;
    Bitmap bitmap1;
    ImageView imgs1, imgs2;
    int chunkNumbers = 2500;
    int scl = (int) Math.sqrt(chunkNumbers);
    ArrayList<Bitmap> chunkedImages;
    ArrayList<Bitmap> deployImgs = new ArrayList<>(chunkNumbers);
    ArrayList<Float> brightness = new ArrayList<>(chunkNumbers);
    ArrayList<Float> newBrightness = new ArrayList<>(chunkNumbers);
    Bitmap bit = null;
    Bitmap dest, mBitmap;
    int count1 = 256;
    String sumb;
    Toolbar toolbar;
    TextView save, sumbit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chunked);

        imgs2 = (ImageView) findViewById(R.id.imgs2);
        imgs1 = (ImageView) findViewById(R.id.imgs1);
        save = (TextView) findViewById(R.id.btnSaveImages);
        sumbit = (TextView) findViewById(R.id.btnSumbit);
        toolbar = (Toolbar) findViewById(R.id.actn);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(null);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        Uri myUri = Uri.parse(i.getStringExtra("image"));
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);

            resize(mBitmap, 1000, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

            imgs1.setImageBitmap(dest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int count = 0;
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_MULTIPLE) {
                imagesPathList = new ArrayList<>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");

                arrrayImages = new ArrayList<>(imagesPath.length);
                for (String anImagesPath : imagesPath) {
                    imagesPathList.add(anImagesPath);

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(anImagesPath, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;


                    int scaleFactor = Math.min(photoW / 50, photoH / 50);

                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;


                    bitmap = BitmapFactory.decodeFile(anImagesPath, bmOptions);
                    if (bitmap != null) {
                        Bitmap icon2 = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
                        arrrayImages.add(count, icon2);
                        count++;
                    }

                }
                for (int i = 0; i < arrrayImages.size(); i++) {
                    newBrightness.add(i, (float) Bright.getInstance().brightness(arrrayImages.get(i)));
                }
                Europa();

            }
        }

    }

    private void Europa() {

        int rows, cols;

        int chunkHeight, chunkWidth;

        chunkedImages = new ArrayList<>(chunkNumbers);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(dest, dest.getWidth(), dest.getHeight(), true);

        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = dest.getHeight() / rows;
        chunkWidth = dest.getWidth() / cols;

        int yCoord = 0;
        for (int x = 0; x < rows; x++) {
            int xCoord = 0;
            for (int y = 0; y < cols; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));

                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        for (int i = 0; i < chunkedImages.size(); i++) {
           brightness.add((float) Bright.getInstance().brightness(chunkedImages.get(i)));

        }
        new RemoteDataTask(chunkedImages).execute();
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {



        ArrayList<Bitmap> chunkImages;

        RemoteDataTask(ArrayList<Bitmap> chunkedImages) {
            this.chunkImages = chunkedImages;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{

                ParseQuery<ParseObject> d = new ParseQuery<ParseObject>("Splash");
                d.whereEqualTo("objectId","rD0P7W3Qf4");
                List<ParseObject> objects1 = d.find();

                for (final  ParseObject sandy:objects1){
                     sumb = "http://"+sandy.get("sumbit");

                }

            }catch (Exception e){

            }
            int chunkWidth = chunkImages.get(0).getWidth();
            int chunkHeight = chunkImages.get(0).getHeight();
            for (int i = 0; i < chunkNumbers; i++) {


                int count2 = count1;
                int bestIndex = 0;
                for (int z = 0; z < newBrightness.size(); z++) {
                    if (newBrightness.get(z) < brightness.get(i)) {

                        int difference = (int) (brightness.get(i) - newBrightness.get(z));
                        if (count2 > difference) {
                            count2 = difference;
                            bestIndex = z;

                        }

                    } else {
                        int difference = (int) (newBrightness.get(z) - brightness.get(i));
                        if (count2 > difference) {
                            count2 = difference;
                            bestIndex = z;

                        }
                    }
                }
                bit = scaleBitmap(arrrayImages.get(bestIndex), chunkWidth, chunkHeight);
                deployImgs.add(i, bit);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new RemoteDataTask2(deployImgs).execute();

        }
    }

    private class RemoteDataTask2 extends AsyncTask<Void, Void, Void> {
        ArrayList<Bitmap> imageChunks;

        RemoteDataTask2(ArrayList<Bitmap> imageChunks) {

            this.imageChunks = imageChunks;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            int chunkWidth = imageChunks.get(0).getWidth();
            int chunkHeight = imageChunks.get(0).getHeight();

            bitmap1 = Bitmap.createBitmap(chunkWidth * scl, chunkHeight * scl, Bitmap.Config.ARGB_8888);
            Bitmap water = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mmm), 150, 100, false);


            Canvas canvas = new Canvas(bitmap1);
            int count = 0;
            for (int rows = 0; rows < scl; rows++) {
                for (int cols = 0; cols < scl; cols++) {
                    canvas.drawBitmap(imageChunks.get(count), chunkWidth * cols, chunkHeight * rows, null);
                    count++;
                }
            }
            canvas.drawBitmap(water, 830, 870, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            imgs2.setVisibility(View.VISIBLE);

            imgs2.setImageBitmap(bitmap1);

            final boolean hasDrawable = (imgs2.getDrawable() != null);

            if (hasDrawable) {
                save.setVisibility(View.VISIBLE);
                sumbit.setVisibility(View.VISIBLE);
                imgs1.setVisibility(View.GONE);
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                Intent intent = new Intent(Chunked.this, CustomPhotoGalleryActivity.class);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
                break;
            case R.id.btnSaveImages:
                if (imagesPathList != null) {
                    Save save = new Save();
                    save.SaveImage(this, bitmap1);
                }
            case R.id.btnSumbit:
                Intent x = new Intent(Intent.ACTION_VIEW);
                x.setData(Uri.parse(sumb));
                startActivity(x);
                break;
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }


}
