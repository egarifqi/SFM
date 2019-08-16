package com.example.sfmtesting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sfmtesting.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements LocationListener {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String postUrl;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar[] calendar = {Calendar.getInstance()};
    String currentTime = simpleDateFormat.format(calendar[0].getTime());
    StringBuffer sb = new StringBuffer();

    ArrayList<String> username = new ArrayList<String>();
    ArrayList<String> password = new ArrayList<String>();
    ArrayList<String> sales_id = new ArrayList<String>();
    ArrayList<String> id = new ArrayList<String>();
    ArrayList<String> dc_id = new ArrayList<String>();
    ArrayList<String> dc_name = new ArrayList<String>();
    ArrayList<String> salesname = new ArrayList<String>();

//    ArrayList<TokoDalamRute> tokoDalamRutes = new ArrayList<TokoDalamRute>();
//    ArrayList<Spacecraft> listEBP = new ArrayList<Spacecraft>();
//    ArrayList<Spacecraft> listMHS = new ArrayList<Spacecraft>();
//    ArrayList<Spacecraft> listNPD = new ArrayList<Spacecraft>();

    LocationManager locationManager;

    String useracc;
    String passacc;
    String salesacc;
    String idacc;
    String dcacc;
    String dcnameacc;
    String nameacc;

    ProgressDialog dialog;

    private EditText mEmailView;
    private EditText mPasswordView;

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        getLocation();

        mEmailView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);

        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    login();
                }
                return false;
            }
        });

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        postUrl = pref.getString("MyPref", "");

        String login_url = "https://sfa-api.pti-cosmetics.com/v_user";
        AndroidNetworking.get(login_url).setPriority(Priority.HIGH).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jo;
                Log.e("Array", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        jo = response.getJSONObject(i);
                        String uname = jo.getString("username");
                        String pword = jo.getString("password");
                        String salesid = jo.getString("sales_id");
                        String userid = jo.getString("id");
                        String dcid = jo.getString("dc_id");
                        String dcname = jo.getString("partner_name");
                        String sales_name = jo.getString("sales_name");

                        Log.e("LIST", uname + " - Sales ID: " + salesid + " - User ID: " + userid + " - Nama: " + sales_name);

                        username.add(uname);
                        password.add(pword);
                        sales_id.add(salesid);
                        id.add(userid);
                        dc_id.add(dcid);
                        dc_name.add(dcname);
                        salesname.add(sales_name);
                    }
                } catch (JSONException e) {
                    Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            //ERROR
            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("PARSING ERROR", "Error :"+anError.getMessage());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        Button loginbutton = findViewById(R.id.buttonlogin);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {

            boolean cek = false;

            if(netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                int numberOfLevels = 5;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                if (level == 2) {
                    cek = false;
                } else if (level == 3) {
                    cek = true;
                } else if (level == 4) {
                    cek = true;
                } else if(level == 5 ) {
                    cek = true;
                }else {
                    cek = false;
                }
            }else if(netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int networkClass = getNetworkClass(getNetworkType(MainActivity.this));
                if(networkClass == 1)
                    cek = false;
                else if(networkClass == 2 )
                    cek = true;
                else if(networkClass == 3 )
                    cek = true;
                else
                    cek = true;
            }else
                cek = false;

            if (cek) {
                calendar[0] = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String date = sdf.format(calendar[0].getTime());
                Log.e("MULAI LOGIN", date);
                boolean cancel = false;
                View focusview = null;
                String emailET = mEmailView.getText().toString();
                String pwET = mPasswordView.getText().toString();
                boolean same = false;
                getLocation();

                if (emailET.isEmpty()) {
                    mEmailView.setError("Tolong isi kolom ini");
                    focusview = mEmailView;
                    cancel = true;
                } else {
                    if (pwET.isEmpty()) {
                        mPasswordView.setError("Tolong isi kolom ini");
                        focusview = mPasswordView;
                        cancel = true;
                    } else {
                        pwET = md5(pwET);
                    }
                }

                if (cancel) {
                    focusview.requestFocus();
                } else {
                    for (int j = 0; j < username.size(); j++) {
                        if (emailET.equals(username.get(j)) && pwET.equals(password.get(j))) {
                            same = true;
                            salesacc = sales_id.get(j);
                            useracc = username.get(j);
                            passacc = password.get(j);
                            idacc = id.get(j);
                            dcacc = dc_id.get(j);
                            dcnameacc = dc_name.get(j);
                            nameacc = salesname.get(j);
                            String latitude = pref.getString("latitude", "0");
                            String longitude = pref.getString("longitude", "0");
                            editor.putString("sales_id", salesacc);
                            editor.putString("username", useracc);
                            editor.putString("user_id", idacc);
                            editor.putString("dc_id", dcacc);
                            editor.putString("dc_name", dcnameacc);
                            editor.putString("sales_name", nameacc);
                            editor.commit();
                            final String salesidacc = pref.getString("sales_id", "");
                            com.example.sfmtesting.StatusSR.namaSR = nameacc;
                            com.example.sfmtesting.StatusSR.idSR = salesacc;
//                            Toast.makeText(getBaseContext(), "sales id" + salesidacc, Toast.LENGTH_LONG).show();
                            Log.e("salesnya ya", salesidacc);

                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                            if (SDK_INT > 8) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                //your codes here

                                try {
                                    URL url = new URL("http://10.3.181.177:3000/login_presence");
                                    JSONObject obj = new JSONObject();
                                    obj.put("user_id", idacc);
                                    obj.put("username", useracc);
                                    obj.put("latitude", latitude);
                                    obj.put("langitude", longitude);
                                    obj.put("login_date", currentTime);
                                    Log.e("Bentuk JSON1", obj.toString());
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setReadTimeout(15000);
                                    conn.setConnectTimeout(15000);
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    OutputStream os = conn.getOutputStream();
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                                    writer.write(getPostDataString(obj));
                                    writer.flush();
                                    writer.close();
                                    os.close();
//                                        Log.e("Bentuk JSON2", obj.toString());

                                    int responseCode = conn.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                                            Log.e("Bentuk JSON2.5", obj.toString());
                                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                        String line = "";
                                        while ((line = in.readLine()) != null) {
                                            sb.append(line);
                                            break;
                                        }
                                        in.close();
//                                            Log.e("Bentuk JSON3", obj.toString());
                                        Log.e("Buffer", sb.toString());

                                    } else {
                                        Log.e("Response_Code", String.valueOf(responseCode));
                                        Log.e("Response_Message", String.valueOf(conn.getResponseMessage()));
                                        Log.e("Returned_String", sb.toString());
//                                            Log.e("Bentuk JSON4", obj.toString());

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("Error", "Connection Error : " + e.getMessage());

                                }

                            }

                            AmbilData ambilData = new AmbilData();
                            ambilData.execute();


                        } else {
//                            Toast.makeText(getBaseContext(), "Username belum terdaftar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.e("sales id", salesacc + "username" + useracc + "password" + passacc + "dc id" + dcacc);

//                    Toast.makeText(getBaseContext(), "Sales id :" + salesacc + "\n" +
//                            "Username :" + useracc + "\n" +
//                            "Password :" + passacc + "\n" +
//                            "Email :" + emailacc, Toast.LENGTH_LONG).show();
                }
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Gagal Log In");
                dialog.setMessage("Koneksi internet terdeteksi namun tidak memadai untuk mengambil data harian...\n Pastikan anda terhubung dengan koneksi internet yang kencang...");
                dialog.setCancelable(true);
                dialog.show();
            }

        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Gagal Log In");
            dialog.setMessage("Tidak ada koneksi internet...\nPastikan anda terhubung dengan jaringan internet...");
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.e("Location Changed", "Lat : "+location.getLatitude() +", Long : "+location.getLongitude());
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.commit();

//        try {
//            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//        } catch (Exception e) {
//            Log.e("Geocoder Error", e.getMessage());
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("I&GPS", "Please enable GPS and Internet");
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang mengambil data harian...");
            dialog.show();
            Log.e("LOGIN STATUS", "STARTING...");
//            super.onPreExecute();
        }

        protected String doInBackground(String... strings) {


//            final ArrayList<Spacecraft> downloadedData = new ArrayList<>();
            final com.example.sfmtesting.DatabaseTokoHandler dbtoko = new com.example.sfmtesting.DatabaseTokoHandler(getBaseContext());

            Log.e("sales id", salesacc);
            String url = "https://sfa-api.pti-cosmetics.com/v_partner_inroute?sales_id=eq." + salesacc;
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
//                            Spacecraft s = new Spacecraft();
                            com.example.sfmtesting.TokoDalamRute tdr = new com.example.sfmtesting.TokoDalamRute();
                            try {
                                Log.e("LOGIN STATUS", "FETCHING PARTNER DATA...");
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");
                                    String name = jo.getString("ref");
                                    String propellant = jo.getString("partner_name");
                                    String salesid = jo.getString("sales_id");
                                    String partnerid = jo.getString("partner_id");
                                    String konst = jo.getString("const");
                                    String status = jo.getString("visit_state");
//                                    s = new Spacecraft();
//                                    s.setId(id);
//                                    s.setKodeodoo(name);
//                                    s.setNamaproduk(propellant);

                                    tdr.setId(id);
                                    tdr.setNama(propellant);
                                    tdr.setKode(name);
                                    tdr.setFrekuensi(konst);
                                    tdr.setPartnerId(partnerid);
                                    tdr.setSalesid(salesid);
                                    tdr.setStatus(status);

//                                    tokoDalamRutes.add(tdr);

                                    dbtoko.addToko(tdr);

//                                    downloadedData.add(s);
                                }
                                com.example.sfmtesting.StatusSR.callPlan = response.length();
                                editor.putInt("datamcp", response.length());
                                editor.commit();


//                                dbtoko.addAllProduk(tokoDalamRutes, response.length());

                                Log.e("TOTAL TOKO", ""+dbtoko.getContactsCount());
//                                Toast.makeText(getBaseContext(), "Data ada " + StatusSR.callPlan, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR", "Error :"+anError.getMessage());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();

            Log.e("LOGIN STATUS", "DONE FETCHING PARTNER DATA!");

            AmbilDataEBP ambilDataEBP = new AmbilDataEBP();
            ambilDataEBP.execute();

//            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDataEBP extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang mengambil data harian...");
            dialog.show();
//            Log.e("LOGIN STATUS", "STARTING...");
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final com.example.sfmtesting.DatabaseProdukEBPHandler dbEBP = new com.example.sfmtesting.DatabaseProdukEBPHandler(getBaseContext());

            Log.e("sales id", salesacc);
            String url2 = "https://sfa-api.pti-cosmetics.com/v_product_ebp?sales_id=eq."+salesacc;
            AndroidNetworking.get(url2)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            com.example.sfmtesting.Spacecraft s = new com.example.sfmtesting.Spacecraft();
                            Log.e("LOGIN STATUS", "FETCHING EBP DATA...");
                            try {
                                dbEBP.addAllProduk(response, response.length());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("SIZE_EBP", "SIZE = " + dbEBP.getProductCount());
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR", "Error :"+anError.getMessage());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.e("LOGIN STATUS", "DONE FETCHING EBP DATA!");
            AmbilDataNPD ambilDataNPD = new AmbilDataNPD();
            ambilDataNPD.execute();
//            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDataNPD extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang mengambil data harian...");
            dialog.show();
//            Log.e("LOGIN STATUS", "STARTING...");
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final com.example.sfmtesting.DatabaseNPDPromoHandler dbNPDP = new com.example.sfmtesting.DatabaseNPDPromoHandler(getBaseContext());

            Log.e("sales id", salesacc);
            String url3 = "https://sfa-api.pti-cosmetics.com/v_product_npd_promo?sales_id=eq."+salesacc;
            AndroidNetworking.get(url3)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            com.example.sfmtesting.Spacecraft s = new com.example.sfmtesting.Spacecraft();
                            Log.e("LOGIN STATUS", "FETCHING NPD & PROMO DATA...");
                            try {
                                dbNPDP.addAllProduk(response, response.length());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("SIZE_NPD_PROMO", "SIZE = " + dbNPDP.getProductCount());
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR", "Error :"+anError.getMessage());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.e("LOGIN STATUS", "DONE FETCHING NPD DATA!");
            AmbilDataMHS ambilDataMHS = new AmbilDataMHS();
            ambilDataMHS.execute();
//            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDataMHS extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang mengambil data harian...");
            dialog.show();
//            Log.e("LOGIN STATUS", "STARTING...");
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final com.example.sfmtesting.DatabaseMHSHandler dbMHS = new com.example.sfmtesting.DatabaseMHSHandler(getBaseContext());

            Log.e("sales id", salesacc);
            String url4 = "https://sfa-api.pti-cosmetics.com/v_product_mhs?sales_id=eq."+salesacc;
            AndroidNetworking.get(url4)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            com.example.sfmtesting.Spacecraft s = new com.example.sfmtesting.Spacecraft();
                            Log.e("LOGIN STATUS", "FETCHING MHS DATA...");
                            try {
                                dbMHS.addAllProduk(response, response.length());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("SIZE_EBP", "SIZE = " + dbMHS.getProductCount());
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR", "Error :"+anError.getMessage());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.e("LOGIN STATUS", "DONE FETCHING MHS DATA!");

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(MainActivity.this, com.example.sfmtesting.VisitActivity.class);
            startActivity(intent);
//            super.onPostExecute(s);
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
//                        Toast.makeText(getActivity(), "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
//                    Log.e("Location", "Lat : " + location.getLatitude());
//                    Log.e("Location", "Long : " + location.getLongitude());

                    editor.putString("latitude", String.valueOf(location.getLatitude()));
                    editor.putString("longitude", String.valueOf(location.getLongitude()));
                    editor.commit();

//                    try {
//                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//                    } catch (Exception e) {
//
//                    }
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

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    public int getNetworkClass(int networkType) {
        try {
            return getNetworkClassReflect(networkType);
        }catch (Exception ignored) {
        }

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16: // TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return 1;

            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:

                return 2;

            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18: // TelephonyManager.NETWORK_TYPE_IWLAN:
                return 3;
            default:
                return 0;
        }
    }

    private int getNetworkClassReflect(int networkType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getNetworkClass = TelephonyManager.class.getDeclaredMethod("getNetworkClass", int.class);
        if (!getNetworkClass.isAccessible()) {
            getNetworkClass.setAccessible(true);
        }
        return (Integer) getNetworkClass.invoke(null, networkType);
    }

    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
    }

}