package com.example.sfmtesting;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.sfmtesting.R;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

public class KunjunganActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LocationManager locationManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.sfa.ACTION_UPDATE_NOTIFICATION";
    private NotificationReceiver mReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kunjungan);
        pref = getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        boolean notif = pref.getBoolean("odoo", false);
        if (notif){
            createNotificationChannel();
            sendNotification();
            registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
            editor.putBoolean("odoo", false);
            editor.commit();
        }
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TabLayout tabLayout = findViewById(R.id.tab_layoutkunjungan);

        tabLayout.addTab(tabLayout.newTab().setText("DALAM RUTE"));
        tabLayout.addTab(tabLayout.newTab().setText("LUAR RUTE"));
//        tabLayout.addTab(tabLayout.newTab().setText("TOKO BARU"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pagerkunjungan);
        final com.example.sfmtesting.KunjunganPagerAdapter adapter = new com.example.sfmtesting.KunjunganPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        isStoragePermissionGranted();

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menambahkan icon menu pada toolbar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.Logout) {

            if (com.example.sfmtesting.StatusSR.dalamRute + com.example.sfmtesting.StatusSR.totalNotCall >= com.example.sfmtesting.StatusSR.callPlan) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
                final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(getBaseContext());
                final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(getBaseContext());
                final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(getBaseContext());
                final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(getBaseContext());

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
                dialog.setCancelable(true);
                dialog.setTitle("LOGOUT");
                dialog.setMessage("Masih ada toko yang belum dikunjungi atau masih tertunda.\nApakah anda akan melakukan kunjungan ke toko-toko tersebut?");
                dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getBaseContext(), "Masih ada toko yang belum dikunjungi atau tertunda, silakan kunjungi terlebih dahulu atau beri alasan mengapa tidak mengunjungi", Toast.LENGTH_LONG).show();

                    }
                });

                dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(KunjunganActivity.this, com.example.sfmtesting.KunjunganYangBelumActivity.class);
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
        super.onBackPressed();
        Intent intent = new Intent(KunjunganActivity.this, com.example.sfmtesting.VisitActivity.class);
        startActivity(intent);
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) KunjunganActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
                        Geocoder geocoder = new Geocoder(KunjunganActivity.this, Locale.getDefault());
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

    private class LoggingOut extends AsyncTask<String, Void, String> {

        final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(getBaseContext());
        final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(getBaseContext());
        final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(getBaseContext());
        final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(getBaseContext());

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(KunjunganActivity.this);
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

            com.example.sfmtesting.StatusSR.clearAll();
            com.example.sfmtesting.StatusToko.clearToko();
            Global.clearProduct();
            Globalemina.clearProduct();
            Globalmo.clearProduct();
            Globalputri.clearProduct();
            com.example.sfmtesting.TokoBelumDikunjungi.clearBelumDikunjungi();
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

            AlertDialog.Builder popup = new AlertDialog.Builder(KunjunganActivity.this);
            popup.setTitle("Log Out");
            popup.setMessage("Log out berhasil!! WOW! AMAZING!!");
            popup.setCancelable(false);
            popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(KunjunganActivity.this, MainActivity.class);
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

    public void sendNotification(){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.data);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        notifyBuilder.addAction(R.drawable.parama, "Update Notification", updatePendingIntent);
    }

    public void createNotificationChannel() {
        mNotifyManager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, KunjunganActivity.class);
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.thanks);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle("Terkirim!"))
                        .setContentTitle("Orderan telah terkirim")
                        .setContentText("Orderan untuk " + pref.getString("partner", "toko ini") + " telah masuk ke sistem ODOO!")
                        .setSmallIcon(R.drawable.parama)
//                        .setContentIntent(notificationPendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH).setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    public void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.data);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }

    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean isUpdateEnabled, Boolean isCancelEnabled) {
//        button_notify.setEnabled(isNotifyEnabled);
//        button_update.setEnabled(isUpdateEnabled);
//        button_cancel.setEnabled(isCancelEnabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
