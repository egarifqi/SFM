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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static android.Manifest.permission.CAMERA;

public class ReportCompetitorActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView rcBackButton;
    ImageView rcCameraButton;
    ImageView rcDeleteButton;
    ImageView rcSaveButton;
    ImageView rcPhotoTaken;
    ImageView rcCantSaveButton;
    EditText rcDeskripsiGambar;
    GridView rcGridPhoto;
    RcImageAdapter rcImageAdapter;
    RelativeLayout rcLoadingLayout;
    LinearLayout rcContentUpload;

    SharedPreferences tokoPref;
    String namaToko;
    Uri imageUri;
    Random rand;
    Object imageurl;
    ArrayList<Bitmap> rcPhotoList;
    com.google.api.services.drive.Drive mDriveService;
    DriveServiceHelper mDriveServiceHelper;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CAMERA = 4;
    private static final int REQUEST_CODE_SIGN_IN = 5;
    private static final String FOLDER_ID = "1OzH-LxH2BnhbqvrDX6blGjHLpjyiqx6A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_competitor);

        rcBackButton = findViewById(R.id.rc_back_button);
        rcCameraButton = findViewById(R.id.rc_camera_button);
//        rcDeleteButton = findViewById(R.id.rc_delete_button);
        rcSaveButton = findViewById(R.id.rc_save_button);
        rcCantSaveButton = findViewById(R.id.rc_cant_save_button);
        rcPhotoTaken = findViewById(R.id.rc_photo_taken);
        rcGridPhoto = findViewById(R.id.rc_grid_photo);
        rcLoadingLayout = findViewById(R.id.rc_loading_layout);
        rcDeskripsiGambar = findViewById(R.id.rc_edittext_deskripsi_gambar);
        rcContentUpload = findViewById(R.id.rc_layout_content_upload);

        tokoPref = getApplicationContext().getSharedPreferences("TokoPref",0);
        namaToko = tokoPref.getString("partner_name","");
        rand = new Random();
        rcPhotoList = new ArrayList<Bitmap>();
        rcImageAdapter = new RcImageAdapter(this,rcPhotoList);
        rcGridPhoto.setAdapter(rcImageAdapter);

        googleSignIn();

        rcBackButton.setOnClickListener(this);
        rcCameraButton.setOnClickListener(this);
//        rcDeleteButton.setOnClickListener(this);
        rcSaveButton.setOnClickListener(this);
        rcCantSaveButton.setOnClickListener(this);

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

        rcPhotoList.clear();
        rcSaveButton.setVisibility(View.GONE);
        rcCantSaveButton.setVisibility(View.VISIBLE);
    }

    private void googleSignIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        if (view == rcBackButton){
            onBackPressed();
        }

        if (view == rcCameraButton){
            ContentValues values = new ContentValues();
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 99);
        }

        if (view == rcDeleteButton){

        }

        if (view == rcSaveButton){
            rcLoadingLayout.setVisibility(View.VISIBLE);
            rcCameraButton.setEnabled(false);
            for(int i=0; i<rcPhotoList.size();i++){
                saveFileToDrive(rcPhotoList.get(i));
            }
            rcLoadingLayout.setVisibility(View.INVISIBLE);
            rcCameraButton.setEnabled(true);
            Log.i(TAG, "Image successfully saved.");
            rcPhotoList.clear();
            rcGridPhoto.setAdapter(rcImageAdapter);
            rcPhotoTaken.setVisibility(View.INVISIBLE);
            rcSaveButton.setVisibility(View.GONE);
            rcCantSaveButton.setVisibility(View.VISIBLE);
            rcDeskripsiGambar.setText("");
            rcContentUpload.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Gambar berhasil diupload!", Toast.LENGTH_SHORT).show();
        }

        if (view == rcCantSaveButton){
            Toast.makeText(getApplicationContext(), "Silahkan ambil foto terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileToDrive(Bitmap bitmap) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");
            mDriveServiceHelper.createFile(bitmap,"ReportCompetitor_"+namaToko+"_"+rand.nextInt(999999)+".jpg", FOLDER_ID, rcDeskripsiGambar.getText().toString())
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
        }
    }

    private boolean checkPermissionCamera() {
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
        new androidx.appcompat.app.AlertDialog.Builder(ReportCompetitorActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap mBitmapToSave;
        switch (requestCode) {
            case 99:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        mBitmapToSave = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        rcPhotoTaken.setVisibility(View.VISIBLE);
                        rcPhotoTaken.setImageBitmap(mBitmapToSave);
                        rcPhotoList.add(mBitmapToSave);
                        rcCantSaveButton.setVisibility(View.GONE);
                        rcSaveButton.setVisibility(View.VISIBLE);
                        rcGridPhoto.setAdapter(rcImageAdapter);
                        rcContentUpload.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    handleSignInResult(data);
                }
                break;
        }
    }

    private void handleSignInResult(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    mDriveService =
                            new com.google.api.services.drive.Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    mDriveServiceHelper = new DriveServiceHelper(mDriveService);
                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }

    public class RcImageAdapter extends BaseAdapter {

        Context context;
        ArrayList<Bitmap> bitmaps;

        public RcImageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
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
