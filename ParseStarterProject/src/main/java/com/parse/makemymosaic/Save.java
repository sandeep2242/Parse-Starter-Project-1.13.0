package com.parse.makemymosaic;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Lenovo on 07-03-2017.
 */


public class Save {
    private Context This;
    private String nameOfFolder = "/Mosaic";
    private  String nameOfFile = "myImage";


    public  void  SaveImage(Context context , Bitmap bitmap){
        This = context;
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+nameOfFolder;
        String CurrentDateAndTime = getCurrentDateAndTime();
        File dir = new File(filePath);

        if (!dir.exists()){
            dir.mkdirs();
        }

        File file =  new File(dir,nameOfFile +CurrentDateAndTime +".jpg");
        try {

            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
            fOut.flush();
            fOut.close();
            IfFileCreatedThenAvailable(file);
            AbleToSave();

        }catch (Exception e){
            UnableToSave();
            e.printStackTrace();
        }

    }

    private void UnableToSave() {
        Toast.makeText(This,"Please Try Again",Toast.LENGTH_LONG).show();
    }

    private void AbleToSave() {
        Toast.makeText(This,"Image Saved to gallery",Toast.LENGTH_LONG).show();
    }

    private void IfFileCreatedThenAvailable(File file) {

        MediaScannerConnection.scanFile(This,
                new String[]{file.toString()}, null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String s, Uri uri) {

                        Log.i("ExternalStorage","Scanned" +s+ ":");
                        Log.i("ExternalStorage","uri" +uri);

                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.N)
    private String getCurrentDateAndTime() {
       Calendar c = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String formattedDate  = sf.format(c.getTime());

        return formattedDate;
    }
}
