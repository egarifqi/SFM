package com.example.salesforcemanagement;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static android.Manifest.permission.CAMERA;

public class ReportWinningAtStoreActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView wasBackButton;
    ImageView wasCameraButton;
    ImageView wasDeleteButton;
    ImageView wasSaveButton;
    ImageView wasPhotoTaken;
    ImageView wasCantSaveButton;
    EditText wasDeskripsiGambar;
    GridView wasGridPhoto;
    WasImageAdapter wasImageAdapter;
    RelativeLayout wasLoadingLayout;
    LinearLayout wasContentUpload;

    SharedPreferences tokoPref;
    String namaToko;
    Uri imageUri;
    Random rand;
    Object imageurl;
    ArrayList<Bitmap> wasPhotoList;
    String fileId;
    com.google.api.services.drive.Drive mDriveService;
    DriveServiceHelper mDriveServiceHelper;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CAMERA = 4;
    private static final int REQUEST_CODE_SIGN_IN = 5;
    private static final String FOLDER_ID = "1R4oZPF9iTuuS_IePKaZ71bqAWqlH-74N";

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
        wasDeskripsiGambar = findViewById(R.id.was_edittext_deskripsi_gambar);
        wasContentUpload = findViewById(R.id.was_layout_content_upload);

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

        googleSignIn();

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
            wasLoadingLayout.setVisibility(View.VISIBLE);
            wasCameraButton.setEnabled(false);
            for(int i=0; i<wasPhotoList.size();i++){
                saveFileToDrive(wasPhotoList.get(i));
            }
            wasLoadingLayout.setVisibility(View.INVISIBLE);
            wasCameraButton.setEnabled(true);
            Log.i(TAG, "Image successfully saved.");
            wasPhotoList.clear();
            wasGridPhoto.setAdapter(wasImageAdapter);
            wasPhotoTaken.setVisibility(View.INVISIBLE);
            wasSaveButton.setVisibility(View.GONE);
            wasCantSaveButton.setVisibility(View.VISIBLE);
            wasDeskripsiGambar.setText("");
            wasContentUpload.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Gambar berhasil diupload!", Toast.LENGTH_SHORT).show();
        }

        if (view == wasCantSaveButton){
            Toast.makeText(getApplicationContext(), "Silahkan ambil foto terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileToDrive(Bitmap image)  {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");
            mDriveServiceHelper.createFile(image,"WinningAtStore_"+namaToko+"_"+rand.nextInt(999999)+".jpg", FOLDER_ID, wasDeskripsiGambar.getText().toString())
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
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
        Bitmap mBitmapToSave;
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
                        wasContentUpload.setVisibility(View.VISIBLE);
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
