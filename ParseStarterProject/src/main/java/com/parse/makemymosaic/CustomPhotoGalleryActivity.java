package com.parse.makemymosaic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class CustomPhotoGalleryActivity extends AppCompatActivity {
    private GridView grdImages;
    private Button btnSelect;
    private TextView txt;
    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    int click = 0;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_photo_gallery);

        grdImages = (GridView) findViewById(R.id.grdImages);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        txt = (TextView) findViewById(R.id.itemSelected);

        SharedPreferences permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(CustomPhotoGalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CustomPhotoGalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CustomPhotoGalleryActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(CustomPhotoGalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomPhotoGalleryActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(CustomPhotoGalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.apply();


        } else {

            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            @SuppressWarnings("deprecation")
            Cursor imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
            this.count = imagecursor.getCount();
            this.arrPath = new String[this.count];
            ids = new int[count];
            this.thumbnailsselection = new boolean[this.count];

            for (int i = 0; i < this.count; i++) {
                imagecursor.moveToPosition(i);
                ids[i] = imagecursor.getInt(image_column_index);
                int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                arrPath[i] = imagecursor.getString(dataColumnIndex);
            }

            imageAdapter = new ImageAdapter();
            grdImages.setAdapter(imageAdapter);
            imagecursor.close();

            btnSelect.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    final int len = thumbnailsselection.length;
                    int cnt = 0;
                    String selectImages = "";
                    for (int i = 0; i < len; i++) {
                        if (thumbnailsselection[i]) {
                            cnt++;
                            selectImages = selectImages + arrPath[i] + "|";
                        }
                    }
                    if (cnt == 0) {
                        Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                    } else if (cnt >= 101) {
                        Toast.makeText(getApplicationContext(), "Maximum limit is 100", Toast.LENGTH_LONG).show();
                    } else {

                        Log.d("SelectedImages", String.valueOf(cnt));
                        Intent i = new Intent();
                        i.putExtra("data", selectImages);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    }
                }
            });
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void proceedAfterPermission() {
        Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
                final String orderBy = MediaStore.Images.Media._ID;
                @SuppressWarnings("deprecation")
                Cursor imagecursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
                int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
                this.count = imagecursor.getCount();
                this.arrPath = new String[this.count];
                ids = new int[count];
                this.thumbnailsselection = new boolean[this.count];

                for (int i = 0; i < this.count; i++) {
                    imagecursor.moveToPosition(i);
                    ids[i] = imagecursor.getInt(image_column_index);
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    arrPath[i] = imagecursor.getString(dataColumnIndex);
                }

                imageAdapter = new ImageAdapter();
                grdImages.setAdapter(imageAdapter);
                imagecursor.close();

                btnSelect.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        final int len = thumbnailsselection.length;
                        int cnt = 0;
                        String selectImages = "";
                        for (int i = 0; i < len; i++) {
                            if (thumbnailsselection[i]) {
                                cnt++;
                                selectImages = selectImages + arrPath[i] + "|";
                            }
                        }
                        if (cnt == 0) {
                            Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                        } else if (cnt >= 101) {
                            Toast.makeText(getApplicationContext(), "Maximum limit is 100", Toast.LENGTH_LONG).show();
                        } else {

                            Log.i("sand", selectImages);
                            Intent i = new Intent();
                            i.putExtra("data", selectImages);
                            setResult(Activity.RESULT_OK, i);
                            finish();
                        }
                    }
                });

                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(CustomPhotoGalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomPhotoGalleryActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(CustomPhotoGalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(CustomPhotoGalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(CustomPhotoGalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }


    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CustomPhotoGallery Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();


        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();


        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);
            holder.chkImage.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        if (click >= 1) {
                            click--;
                            txt.setText(String.valueOf(click));
                        }
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                        click++;
                        txt.setText(String.valueOf(click));
                    }
                }
            });
            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();

                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;

                        if (click >= 1) {
                            click--;
                            txt.setText(String.valueOf(click));
                        }


                    } else {
                        holder.chkImage.setChecked(true);
                        thumbnailsselection[id] = true;
                        click++;
                        txt.setText(String.valueOf(click));

                    }
                }
            });


            try {
                setBitmap(holder.imgThumb, ids[position]);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

}
