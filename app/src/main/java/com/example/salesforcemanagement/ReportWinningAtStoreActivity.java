package com.example.salesforcemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import static android.Manifest.permission.CAMERA;

public class ReportWinningAtStoreActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageView wasBackButton;
    ImageView wasCameraButton;
    ImageView wasDeleteButton;
    ImageView wasSaveButton;
    ImageView wasPhotoTaken;
    ImageView wasCantSaveButton;
    GridView wasGridPhoto;
    WasImageAdapter wasImageAdapter;
    RelativeLayout wasLoadingLayout;

    SharedPreferences tokoPref;
    String namaToko;
    Uri imageUri;
    Random rand;
    Object imageurl;
    ArrayList<Bitmap> wasPhotoList;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_CAMERA = 4;

    private GoogleApiClient mGoogleApiClient;
    private Bitmap mBitmapToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_winning_at_store);

        wasBackButton = findViewById(R.id.was_back_button);
        wasCameraButton = findViewById(R.id.was_camera_button);
//        wasDeleteButton = findViewById(R.id.was_delete_button);
        wasSaveButton = findViewById(R.id.was_save_button);
        wasCantSaveButton = findViewById(R.id.was_cant_save_button);
        wasPhotoTaken = findViewById(R.id.was_photo_taken);
        wasGridPhoto = findViewById(R.id.was_grid_photo);
        wasLoadingLayout = findViewById(R.id.was_loading_layout);

        tokoPref = getApplicationContext().getSharedPreferences("TokoPref",0);
        namaToko = tokoPref.getString("partner_name","");
        rand = new Random();
        wasPhotoList = new ArrayList<Bitmap>();
        wasImageAdapter = new WasImageAdapter(this,wasPhotoList);
        wasGridPhoto.setAdapter(wasImageAdapter);

        wasBackButton.setOnClickListener(this);
        wasCameraButton.setOnClickListener(this);
//        wasDeleteButton.setOnClickListener(this);
        wasSaveButton.setOnClickListener(this);
        wasCantSaveButton.setOnClickListener(this);

        int currentApiVersion = Build.VERSION.SDK_INT;
        Log.d("PERMISSION CAMERA","Check build version");
        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            Log.d("PERMISSION CAMERA","Current api > build");
            if(checkPermissionCamera())
            {
                Log.d("PERMISSION CAMERA","Permission already granted");
//                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d("PERMISSION CAMERA","Requesting permission camera");
                requestPermissionCamera();
            }
        }

        wasPhotoList.clear();
        wasSaveButton.setVisibility(View.GONE);
        wasCantSaveButton.setVisibility(View.VISIBLE);
    }

    private void saveFileToDrive(Bitmap image) {
        Log.i(TAG, "Creating new contents.");
        wasLoadingLayout.setVisibility(View.VISIBLE);
        wasCameraButton.setEnabled(false);
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        Log.i(TAG, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("WinningAtStore_"+namaToko+"_"+rand.nextInt(999999)+".png").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);

                        wasLoadingLayout.setVisibility(View.INVISIBLE);
                        wasCameraButton.setEnabled(true);

                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
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
            startActivityForResult(intent, 99);
        }

        if (view == wasDeleteButton){

        }

        if (view == wasSaveButton){
            for(int i=0; i<wasPhotoList.size();i++){
                saveFileToDrive(wasPhotoList.get(i));
            }
        }

        if (view == wasCantSaveButton){
            Toast.makeText(getApplicationContext(), "Silahkan ambil foto terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissionCamera()
    {
        Log.d("PERMISSION CAMERA","Checking permission camera");
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionCamera()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                Log.d("PERMISSION CAMERA","Granting permission result");
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
//                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
//                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        (dialog, which) -> {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA},
                                                        REQUEST_CAMERA);
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(ReportWinningAtStoreActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 99:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        mBitmapToSave = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        wasPhotoTaken.setVisibility(View.VISIBLE);
                        wasPhotoTaken.setImageBitmap(mBitmapToSave);
                        wasPhotoList.add(mBitmapToSave);
                        wasCantSaveButton.setVisibility(View.GONE);
                        wasSaveButton.setVisibility(View.VISIBLE);
                        wasGridPhoto.setAdapter(wasImageAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_CODE_CAPTURE_IMAGE:
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
                }
                break;

            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    wasPhotoList.clear();
                    wasGridPhoto.setAdapter(wasImageAdapter);
                    wasPhotoTaken.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Gambar berhasil diupload!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "API client connected.");
//        if (mBitmapToSave == null) {
//            // This activity has no UI of its own. Just start the camera.
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
//                    REQUEST_CODE_CAPTURE_IMAGE);
//            return;
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
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
