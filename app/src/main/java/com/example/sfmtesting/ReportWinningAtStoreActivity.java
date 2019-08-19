package com.example.sfmtesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class ReportWinningAtStoreActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView wasBackButton;
    ImageView wasCameraButton;
    ImageView wasDeleteButton;
    ImageView wasSaveButton;
    ImageView wasPhotoTaken;
    GridView wasGridPhoto;
    WasImageAdapter wasImageAdapter;

    SharedPreferences tokoPref;
    String namaToko;
    Uri imageUri;
    Random rand;
    Bitmap thumbnail;
    Object imageurl;
    ArrayList<Bitmap> wasPhotoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_winning_at_store);

        wasBackButton = findViewById(R.id.was_back_button);
        wasCameraButton = findViewById(R.id.was_camera_button);
        wasDeleteButton = findViewById(R.id.was_delete_button);
        wasSaveButton = findViewById(R.id.was_save_button);
        wasPhotoTaken = findViewById(R.id.was_photo_taken);
        wasGridPhoto = findViewById(R.id.was_grid_photo);

        tokoPref = getApplicationContext().getSharedPreferences("TokoPref",0);
        namaToko = tokoPref.getString("partner_name","");
        rand = new Random();
        wasPhotoList = new ArrayList<Bitmap>();
        wasImageAdapter = new WasImageAdapter(this,wasPhotoList);
        wasGridPhoto.setAdapter(wasImageAdapter);

        wasBackButton.setOnClickListener(this);
        wasCameraButton.setOnClickListener(this);
        wasDeleteButton.setOnClickListener(this);
        wasSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == wasBackButton){
            onBackPressed();
        }

        if (view == wasCameraButton){
            ContentValues values = new ContentValues();
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 0);
        }

        if (view == wasDeleteButton){

        }

        if (view == wasSaveButton){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {

            case 0:
                if (requestCode == 0)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
//                            thumbnail = (Bitmap)data.getExtras().get("data");
                            thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            wasPhotoTaken.setImageBitmap(thumbnail);
                            wasPhotoList.add(thumbnail);
                            wasGridPhoto.setAdapter(wasImageAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
    }

    public class WasImageAdapter extends BaseAdapter {

        Context context;
        ArrayList<Bitmap> bitmaps;

        public WasImageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
            this.context = context;
            this.bitmaps = bitmapList;
        }

        @Override
        public int getCount() {
            return this.bitmaps.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView image;

            if(view == null){
                image = new ImageView(this.context);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setLayoutParams(new GridView.LayoutParams(290,290));
            } else {
                image = (ImageView) view;
            }

            image.setImageBitmap(this.bitmaps.get(i));
            return image;
        }
    }
}
