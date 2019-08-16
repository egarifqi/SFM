package com.example.sfmtesting;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.sfmtesting.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class VisitActivity extends AppCompatActivity {

    SharedPreferences pref;
    LocationManager locationManager;
    SharedPreferences.Editor editor;
    TextView DisplayDateTime;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
//                case R.id.navigation_pengajuan:
//                    selectedFragment = new PengajuanFragment();
//                    break;
//                case R.id.navigation_account:
//                    selectedFragment = new AccountFragment();
//                    break;

            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    public VisitActivity() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        isStoragePermissionGranted();
        getLocation();

        calander = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date = sdf.format(calander.getTime());
        Log.e("SELESAI LOGIN", Date);

        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbardashboard);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menambahkan icon menu pada toolbar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final String sales_id = pref.getString("sales_id", "");
        int id = item.getItemId();

        if (id == R.id.Logout) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);

            if (StatusSR.dalamRute + StatusSR.totalNotCall >= StatusSR.callPlan) {

                dialog.setCancelable(true);
                dialog.setTitle("LOGOUT");
                dialog.setMessage("Apakah Anda yakin? Anda tidak bisa lagi melihat pencapaian hari ini jika logout");
                dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        LoggingOut loggingOut = new LoggingOut();
                        loggingOut.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });

                dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            } else {
//                Toast.makeText(getBaseContext(), "Masih ada toko yang belum dikunjungi atau tertunda, silakan kunjungi terlebih dahulu atau beri alasan mengapa tidak mengunjungi", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(VisitActivity.this, KunjunganActivity.class);
//                startActivity(intent);
                dialog.setMessage("Masih ada toko yang belum dikunjungi atau masih tertunda.\nApakah anda akan melakukan kunjungan ke toko-toko tersebut?");
                dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(VisitActivity.this, KunjunganActivity.class);
                        startActivity(intent);
                    }
                });

                dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(VisitActivity.this, KunjunganYangBelumActivity.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    private class LoggingOut extends AsyncTask<String, Void, String>{

        final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(getBaseContext());
        final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(getBaseContext());
        final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(getBaseContext());
        final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(getBaseContext());
        final ArrayList<TokoDalamRute> tdr = dbtoko.getAllToko();

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(VisitActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang logout...\nMohon tunggu sebentar...");
            dialog.show();
            Log.e("LOGOUT STATUS", "STARTING...");
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            StatusSR.clearAll();
            StatusToko.clearToko();
            Global.clearProduct();
            Globalemina.clearProduct();
            Globalmo.clearProduct();
            Globalputri.clearProduct();
            TokoBelumDikunjungi.clearBelumDikunjungi();
            Log.e("LOGOUT STATUS", "DELETING LOCAL VARIABLE");
            dbtoko.deleteAll();
            dbEBP.deleteAll();
            dbMHS.deleteAll();
            dbNPDP.deleteAll();
            editor.clear();
            editor.commit();


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            Log.e("LOGOUT STATUS", "DONE");
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder popup = new AlertDialog.Builder(VisitActivity.this);
            popup.setTitle("Log Out");
            popup.setMessage("Log out berhasil!");
            popup.setCancelable(false);
            popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(VisitActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            popup.show();
//            super.onPostExecute(s);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) VisitActivity.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
//                        Toast.makeText(getActivity(), "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    Log.e("Location", "Lat : " + location.getLatitude());
                    Log.e("Location", "Long : " + location.getLongitude());

                    editor.putString("latitude", String.valueOf(location.getLatitude()));
                    editor.putString("longitude", String.valueOf(location.getLongitude()));
                    editor.commit();

                    try {
                        Geocoder geocoder = new Geocoder(VisitActivity.this, Locale.getDefault());
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
//                        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
                    Log.e("I&GPS", "Please enable GPS and Internet");
                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
