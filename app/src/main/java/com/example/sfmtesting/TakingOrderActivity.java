package com.example.sfmtesting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.sfmtesting.R;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class TakingOrderActivity extends AppCompatActivity {

    static int qtysum = 0;
    static int maximumIDW = 0;
    static int maximumIDMO = 0;
    static int maximumIDE = 0;
    static int maximumIDP = 0;
    static int kali = 0;
    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;
    SendRequest mAuthTask = null;
    SendOrder sendOrder = null;
    AmbilDOIDW ambilDOIDW = null;
    AmbilDOIDM ambilDOIDM = null;
    AmbilDOIDE ambilDOIDE = null;
    AmbilDOIDP ambilDOIDP = null;
    ImageView signImage;
    Button signatureButton;
    boolean check1 = false;
    boolean check2 = false;
    boolean check3 = false;
    boolean check4 = false;
    boolean check = false;
    boolean checkW = false;
    boolean checkM = false;
    boolean checkE = false;
    boolean checkP = false;
    boolean kosong = false;
    boolean visID = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_order);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();

        boolean dalrut = prefToko.getBoolean("dalamrute", StatusToko.rute);

        Log.e("SIZE", "Wardah : "+Global.kode.size()+", MakeOver : "+Globalmo.kode.size()+", Emina : "+Globalemina.kode.size()+", Putri : "+Globalputri.kode.size());
        String namatoko = prefToko.getString("partner_name", "");

//        TabLayout tabLayout = findViewById(R.id.tab_layouttakingorder);
//
//        tabLayout.addTab(tabLayout.newTab().setText(namatoko));

        LinearLayout wardah = findViewById(R.id.wardahtakingorder);
        LinearLayout makeover = findViewById(R.id.makeovertakingorder);
        LinearLayout emina = findViewById(R.id.eminatakingorder);
        LinearLayout putri = findViewById(R.id.putritakingorder);
        Button simpan = findViewById(R.id.button_simpantakingorder);

        signImage = findViewById(R.id.imageView1);
        String image_path = getIntent().getStringExtra("imagePath");
        Bitmap bitmap = BitmapFactory.decodeFile(image_path);
        signImage.setImageBitmap(bitmap);
        signatureButton = findViewById(R.id.getSign);
        EditText alasanoutroute = findViewById(R.id.alasanluar);
        EditText namattd = findViewById(R.id.nama_ttd);
        alasanoutroute.setText(prefToko.getString("alasanout", ""));
        namattd.setText(prefToko.getString("namattd", ""));
        LinearLayout layoutalasan = findViewById(R.id.alasanluarrute);


        if (dalrut){
            layoutalasan.setVisibility(View.GONE);
        } else {
            layoutalasan.setVisibility(View.VISIBLE);
            editorToko.putString("alasanout", alasanoutroute.getText().toString());
            editorToko.putString("namattd", namattd.getText().toString());
            editorToko.commit();
        }

        signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorToko.putString("alasanout", alasanoutroute.getText().toString());
                editorToko.putString("namattd", namattd.getText().toString());
                editorToko.commit();
                Intent i = new Intent(TakingOrderActivity.this, SignatureActivity.class);
                startActivity(i);
            }
        });

        wardah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorToko.putString("alasanout", alasanoutroute.getText().toString());
                editorToko.putString("namattd", namattd.getText().toString());
                editorToko.commit();
                Intent intent = new Intent(TakingOrderActivity.this, HistoricalSalesWardahActivity.class);
                startActivity(intent);
            }
        });
        makeover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorToko.putString("alasanout", alasanoutroute.getText().toString());
                editorToko.putString("namattd", namattd.getText().toString());
                editorToko.commit();
                Intent intent = new Intent(TakingOrderActivity.this, HistoricalSalesMOActivity.class);
                startActivity(intent);
            }
        });
        emina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorToko.putString("alasanout", alasanoutroute.getText().toString());
                editorToko.putString("namattd", namattd.getText().toString());
                editorToko.commit();
                Intent intent = new Intent(TakingOrderActivity.this, HistoricalSalesEminaActivity.class);
                startActivity(intent);
            }
        });
        putri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorToko.putString("alasanout", alasanoutroute.getText().toString());
                editorToko.putString("namattd", namattd.getText().toString());
                editorToko.commit();
                Intent intent = new Intent(TakingOrderActivity.this, HistoricalSalesPutriActivity.class);
                startActivity(intent);
            }
        });
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()){

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
                        int networkClass = getNetworkClass(getNetworkType(TakingOrderActivity.this));
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
                        editorToko.putString("alasanout", alasanoutroute.getText().toString());
                        editorToko.putString("namattd", namattd.getText().toString());
                        editorToko.commit();
                        Log.e("alasan", prefToko.getString("alasanout", "aduh"));
                        String status = "cek";
//                            = prefToko.getString("alasan", "");
                        final int[] do_id = {0};

                        int sum_qty = 0;
                        if (Global.qty.size() > 0) {
                            for (int i = 0; i < Global.qty.size(); i++) {
                                sum_qty += Integer.parseInt(Global.qty.get(i));
                            }
                        }
                        if (Globalmo.qty.size() > 0) {
                            for (int i = 0; i < Globalmo.qty.size(); i++) {
                                sum_qty += Integer.parseInt(Globalmo.qty.get(i));
                            }
                        }
                        if (Globalemina.qty.size() > 0) {
                            for (int i = 0; i < Globalemina.qty.size(); i++) {
                                sum_qty += Integer.parseInt(Globalemina.qty.get(i));
                            }
                        }
                        if (Globalputri.qty.size() > 0) {
                            for (int i = 0; i < Globalputri.qty.size(); i++) {
                                sum_qty += Integer.parseInt(Globalputri.qty.get(i));
                            }
                        }

                        Log.e("Total_Order", "" + sum_qty);
                        qtysum = sum_qty;
                        if (qtysum<=0){
                            editorToko.putBoolean("ec", false);
                            editorToko.commit();
                        }

                        if ((Global.kode.size() <= 0 && Globalmo.kode.size() <= 0 && Globalemina.kode.size() <= 0 && Globalputri.kode.size() <= 0)) {
                            Intent intentkosong = new Intent(TakingOrderActivity.this, ReasonNotECActivitty.class);
                            Toast.makeText(getBaseContext(), "Anda tidak memesan produk sama sekali...\nToko ini dianggap Visit Not EC", Toast.LENGTH_SHORT).show();
                            startActivity(intentkosong);
                        } else {

                            boolean dalamRute = prefToko.getBoolean("dalamrute", StatusToko.rute);
                            boolean luarRute = prefToko.getBoolean("luarrute", !StatusToko.rute);
                            boolean ec = prefToko.getBoolean("ec", true);

                            if (dalamRute && !luarRute) {
                                StatusSR.dalamRute += 1;
                                if (ec){
                                    StatusSR.ECdalamRute += 1;
                                    editor.putInt("ecdr", StatusSR.ECdalamRute);
                                    editor.commit();
                                } else {
                                    StatusSR.totalNotEC += 1;
                                    editor.putInt("necdr", StatusSR.totalNotEC);
                                    editor.commit();
                                }

                                editor.putInt("calldr", StatusSR.dalamRute);
                                editor.commit();

                                status = "";
                                Log.e("Dalam Rute", status);
                            } else if (!dalamRute && luarRute) {
                                status = alasanoutroute.getText().toString();
                                StatusSR.luarRute += 1;
                                if (ec) {
                                    StatusSR.ECluarRute += 1;
                                    editor.putInt("eclr", StatusSR.ECluarRute);
                                    editor.commit();
                                }
                                editor.putInt("calllr", StatusSR.luarRute);
                                editor.commit();
                                Log.e("LuarRute", status);
                            }
                            Log.e("Rute", "dalam rute : " + dalamRute + ", luar rute : " + luarRute + ", alasan : " + status);
                            StatusSR.totalCall += 1;
                            StatusSR.totalEC += 1;
                            StatusToko.totalsku.add(StatusToko.skuwardah + StatusToko.skuputri + StatusToko.skumake + StatusToko.skuemina);
                            StatusToko.totalqty.add(StatusToko.qtywardah + StatusToko.qtyputri + StatusToko.qtymake + StatusToko.qtyemina);
                            StatusToko.totalamount.add(StatusSR.wardahAch + StatusSR.makeoverAch + StatusSR.eminaAch + StatusSR.putriAch);
                            StatusSR.totalOrder += StatusSR.wardahAch + StatusSR.makeoverAch + StatusSR.eminaAch + StatusSR.putriAch;
                            editor.putInt("ordertotal", StatusSR.totalOrder);
                            editor.commit();
                            StatusSR.subtotal = 0;
                            StatusSR.wardahAch = 0;
                            StatusSR.makeoverAch = 0;
                            StatusSR.eminaAch = 0;
                            StatusSR.putriAch = 0;

                            com.example.sfmtesting.StatusToko.kodetoko.add(prefToko.getString("partner_name", ""));
                            com.example.sfmtesting.StatusToko.namatoko.add(prefToko.getString("ref", ""));


                            if (!ec || qtysum <= 0){
                                StatusToko.statuskunjungan.add("2");
                            } else {
                                StatusToko.statuskunjungan.add("3");
                            }


                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = simpleDateFormat.format(calendar.getTime());
                            StatusToko.waktuselesai.add(currentTime);
                            editorToko.putString("waktu_selesai", currentTime);
                            editorToko.apply();

                            int visitid = prefToko.getInt("id", 0);
                            String partner_ref = prefToko.getString("ref", "null");
                            String user_id = pref.getString("user_id", "0");
                            String sales_id = pref.getString("sales_id", "0");
                            final String partner_id = prefToko.getString("partner_id", "0");
                            String partner_name = prefToko.getString("partner_name", "null");
                            String date = pref.getString("write_date", "null");
                            String waktu_datang = prefToko.getString("waktu_mulai", "null");
                            String waktu_keluar = prefToko.getString("waktu_selesai", "null");
                            String latitude = prefToko.getString("latitude", "null");
                            String longitude = prefToko.getString("longitude", "null");


                            mAuthTask = new SendRequest(partner_ref, user_id, sales_id, partner_id, date, waktu_datang,
                                    waktu_keluar, latitude, longitude, visitid, status, partner_name);
                            mAuthTask.execute();
                        }
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(TakingOrderActivity.this);
                        dialog.setTitle("KONEKSI INTERNET TIDAK MEMADAI");
                        dialog.setMessage("Koneksi internet terdeteksi namun tidak memadai untuk mengirim data orderan...\n Pastikan anda terhubung dengan koneksi internet yang kencang...");
                        dialog.setCancelable(true);
                        dialog.show();
                    }

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TakingOrderActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle("TIDAK ADA KONEKSI INTERNET");
                    dialog.setMessage("Anda tidak terhubung ke internet!!\nPastikan anda mendapatkan sinyal internet yang memadai sebelum memilih tombol KIRIM...");
                    dialog.show();
                }


            }
        });

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

    ProgressDialog dialog;

    @SuppressLint("StaticFieldLeak")
    private class SendRequest extends AsyncTask<String, Void, String> {

        private final String partnerref;
        private final String userid;
        private final String salesid;
        private final String partnerid;
        private final String todaydate;
        private final String visittime;
        private final String outtime;
        private final String loclat;
        private final String loclong;
        private final int visitid;
        private final String alasan;
        private final String partnername;
        StringBuffer sb = new StringBuffer();



        SendRequest(String partnerref, String userid, String salesid, String partnerid,
                    String todaydate, String visittime, String outtime, String loclat,
                    String loclong, int visitid, String alasan, String partnername) {
            this.partnerref = partnerref;
            this.userid = userid;
            this.salesid = salesid;
            this.partnerid = partnerid;
            this.todaydate = todaydate;
            this.loclat = loclat;
            this.loclong = loclong;
            this.visitid = visitid;
            this.visittime = visittime;
            this.outtime = outtime;
            this.alasan = alasan;
            this.partnername = partnername;
        }

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            Log.e("KIRIM DATA", "STARTING");
            dialog = new ProgressDialog(TakingOrderActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang mengirim data orderan...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            if (Global.kode.size() <= 0 && Globalmo.kode.size() <= 0  && Globalemina.kode.size() <= 0 && Globalputri.kode.size() <= 0){
                kosong = true;
            }

            if (kosong){
                Intent intent = new Intent(TakingOrderActivity.this, ReasonNotECActivitty.class);
                startActivity(intent);
            }

            String json = "";
            Log.e("KIRIM DATA", "UPDATING VISIT DATA");

//            int id = pref.getInt("id", 0);
            int idvisit = visitid;
            try {
                Log.e("Visit ID", "id : " + idvisit);

                boolean online = prefToko.getBoolean("online", true);
                Log.e("Status Internet", ""+online);
                if (online){
                    URL url = new URL("http://10.3.181.177:3000/visit?id=eq." + idvisit);
                    JSONObject obj = new JSONObject();
                    String alasannotec = prefToko.getString("alasannotec", "Not EC: Stok Masih Ada");
                    boolean ec = prefToko.getBoolean("ec", true);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    obj.put("selesai_time", outtime);
                    if (ec) {
                        obj.put("reason", alasan);
                        obj.put("state", "3");
                    } else {
                        obj.put("reason", alasannotec);
                        obj.put("state", "2");
                    }
                    Log.e("Bentuk JSON1", obj.toString());


                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("PATCH");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(getPostDataString(obj));
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.e("Status Internet", ""+online);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                            responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        in.close();
                        Log.e("Buffer", sb.toString());
                        return json;
                    } else {
                        Log.e("Response_Code1", String.valueOf(responseCode));
                        Log.e("Response_Message1", String.valueOf(conn.getResponseMessage()));
                        Log.e("Returned_String1", sb.toString());
                        return "false : " + responseCode;
                    }
                } else{
                    URL url2 = new URL("http://10.3.181.177:3000/visit");
                    String alasannotec = prefToko.getString("alasannotec", "Not EC: Stok Masih Ada");
                    boolean ec = prefToko.getBoolean("ec", true);
                    JSONObject obj = new JSONObject();
                    obj.put("partner_ref", partnerref);
                    obj.put("user_id", userid);
                    obj.put("partner_id", partnerid);
                    obj.put("sales_id", salesid);
                    obj.put("datang_time", visittime);
//                    obj.put("id", visitid);
                    obj.put("latitude", loclat);
                    obj.put("longitude", loclong);
                    obj.put("inroute", true);
                    obj.put("selesai_time", outtime);
                    if (ec) {
                        obj.put("reason", "");
                        obj.put("state", "3");
                    } else {
                        obj.put("reason", alasannotec);
                        obj.put("state", "2");
                    }
                    Log.e("Bentuk JSON1", obj.toString());
                    HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
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
                    Log.e("Status Internet", ""+online);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                            responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        in.close();
                        Log.e("Buffer", sb.toString());
                        return json;
                    } else {
                        Log.e("Response_Code1", String.valueOf(responseCode));
                        Log.e("Response_Message1", String.valueOf(conn.getResponseMessage()));
                        Log.e("Returned_String1", sb.toString());
                        return "false : " + responseCode;
                    }
                }


            } catch (Exception e) {
                Log.e("Error1", "Connection Error");
                return "Connection Error";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("KIRIM DATA", "FINISHING VISIT DATA");
            new CekVisitID("http://10.3.181.177:3000/visit?user_id=eq." + userid,
                    partnerref, userid, salesid, partnerid, partnername, visitid, visittime, outtime, loclong, loclat).execute();

            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SendOrder extends AsyncTask<String, Void, String> {

        private final String partnerref;
        private final int userid;
        private final int salesid;
        private final int partnerid;
        private final String reference;
        private final String state;
        private final String partner_name;
        private final String brand;
        private final int visitid;
        private final int total_amount;
        private final int total_qty;
        private final int total_sku;
        private final String notes;
        StringBuffer sb = new StringBuffer();

        SendOrder(String partnerref, int userid, int salesid, int partnerid, String reference,
                  String state, int total_amount, int total_qty, int total_sku, String partner_name,
                  int visitid, String brand, String notes) {
            this.partnerref = partnerref;
            this.userid = userid;
            this.salesid = salesid;
            this.partnerid = partnerid;
            this.total_amount = total_amount;
            this.total_qty = total_qty;
            this.reference = reference;
            this.state = state;
            this.total_sku = total_sku;
            this.partner_name = partner_name;
            this.visitid = visitid;
            this.brand = brand;
            this.notes = notes;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING ORDER DATA");
            String json = "";
            String link = "http://10.3.181.177:3000/delivery_order";

            try {
                URL url2 = new URL("http://10.3.181.177:3000/delivery_order");
                JSONObject obj = new JSONObject();
                boolean ec = prefToko.getBoolean("ec", true);
//                obj.put("id", id);
                obj.put("partner_ref", partnerref);
                obj.put("partner_name", partner_name);
                obj.put("create_uid", userid);
                obj.put("partner_id", partnerid);
                obj.put("sales_id", salesid);
                obj.put("reference", reference);
                obj.put("state", state);
                obj.put("total_sku", total_sku);
                if (ec){
                    obj.put("total_amount", total_amount);
                    obj.put("total_qty", total_qty);
                } else {
                    obj.put("total_amount", 0);
                    obj.put("total_qty", 0);
                }
                obj.put("note", notes);
                obj.put("visit_id", visitid);
                obj.put("brand", brand);
                Log.e("Bentuk JSON DO", obj.toString());

                editor.putString("partner", partner_name);

                HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
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

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Buffer", sb.toString());


                    return json;
                } else {
                    Log.e("Response_Code2", String.valueOf(responseCode));
                    Log.e("Response_Message2", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String2", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("KIRIM DATA", "FINISHING ORDER DATA");

            if (brand.equals(Global.brand)){
                ambilDOIDW = new AmbilDOIDW("http://10.3.181.177:3000/delivery_order?brand=eq."+brand, brand);
                ambilDOIDW.execute();
            }
            if (brand.equals(Globalmo.brand)){
                ambilDOIDM = new AmbilDOIDM("http://10.3.181.177:3000/delivery_order?brand=eq.brand:Make%20Over", brand);
                ambilDOIDM.execute();
            }
            if (brand.equals(Globalemina.brand)){
                ambilDOIDE = new AmbilDOIDE("http://10.3.181.177:3000/delivery_order?brand=eq."+brand, brand);
                ambilDOIDE.execute();
            }
            if (brand.equals(Globalputri.brand)){
                ambilDOIDP = new AmbilDOIDP("http://10.3.181.177:3000/delivery_order?brand=eq."+brand, brand);
                ambilDOIDP.execute();
            }

            super.onPostExecute(s);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class OrderLinesWardah extends AsyncTask<String, Void, String> {

        private final int do_id;
        StringBuffer sb = new StringBuffer();

        OrderLinesWardah(int do_id) {
            this.do_id = do_id;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING WARDAH PRODUCTS DATA");
            String json = "";

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order_line");

                ArrayList<JSONObject> jo = new ArrayList<JSONObject>();
                StringBuilder loop = new StringBuilder();

                boolean ec = prefToko.getBoolean("ec", true);

                for (int i = 0; i < Global.nama.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("do_id", do_id - 1);
                    obj.put("product_id", Global.id_produk.get(i));
                    if (ec) {
                        obj.put("qty_final", Global.qty.get(i));
                    } else {
                        obj.put("qty_final", 0);
                    }
                    obj.put("qty_stock", Global.stock.get(i));
                    obj.put("brand", Global.brand);
                    obj.put("unit_price", Global.harga.get(i));
                    obj.put("default_code", Global.kode.get(i));
                    obj.put("category_id", Global.kategori.get(i));
                    obj.put("qty_order", Global.sgtorder.get(i));
                    obj.put("direct", true);
                    jo.add(obj);
                    loop.append(obj.toString());
                    if (i<Global.nama.size()-1){
                        loop.append(", ");
                    }
                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                Log.e("PRODUK", "["+loop.toString()+"]");

                writer.write("["+loop.toString()+"]");
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Buffer", sb.toString());
                    return json;
                } else {
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            if (check1 && check2 && check3 && check4) {

                Log.e("KIRIM DATA", "FINISHING SENDING WARDAH PRODUCTS");
                new CekComplete(do_id).execute();

            }
            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OrderLinesMakeOver extends AsyncTask<String, Void, String> {

        private final int do_id;
        StringBuffer sb = new StringBuffer();

        OrderLinesMakeOver(int do_id) {
            this.do_id = do_id;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING MAKE OVER PRODUCTS DATA");
            String json = "";

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order_line");

                ArrayList<JSONObject> jo = new ArrayList<JSONObject>();
                StringBuilder loop = new StringBuilder();

                boolean ec = prefToko.getBoolean("ec", true);

                for (int i = 0; i < Globalmo.nama.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("do_id", do_id - 1);
                    obj.put("product_id", Globalmo.id_produk.get(i));
                    if (ec) {
                        obj.put("qty_final", Globalmo.qty.get(i));
                    } else {
                        obj.put("qty_final", 0);
                    }
                    obj.put("qty_stock", Globalmo.stock.get(i));
                    obj.put("brand", Globalmo.brand);
                    obj.put("unit_price", Globalmo.harga.get(i));
                    obj.put("default_code", Globalmo.kode.get(i));
                    obj.put("category_id", Globalmo.kategori.get(i));
                    obj.put("qty_order", Globalmo.sgtorder.get(i));
                    obj.put("direct", true);
                    jo.add(obj);
                    loop.append(obj.toString());
                    if (i<Globalmo.nama.size()-1){
                        loop.append(", ");
                    }
                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                Log.e("PRODUK", "["+loop.toString()+"]");

                writer.write("["+loop.toString()+"]");
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Buffer", sb.toString());
                    return json;
                } else {
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (check1 && check2 && check3 && check4) {

                Log.e("KIRIM DATA", "FINISHING SENDING MAKE OVER PRODUCTS");
                new CekComplete(do_id).execute();

            }
            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OrderLinesEmina extends AsyncTask<String, Void, String> {

        private final int do_id;
        StringBuffer sb = new StringBuffer();

        OrderLinesEmina(int do_id) {
            this.do_id = do_id;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING EMINA PRODUCTS DATA");
            String json = "";

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order_line");

                ArrayList<JSONObject> jo = new ArrayList<JSONObject>();
                StringBuilder loop = new StringBuilder();

                boolean ec = prefToko.getBoolean("ec", true);

                for (int i = 0; i < Globalemina.nama.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("do_id", do_id - 1);
                    obj.put("product_id", Globalemina.id_produk.get(i));
                    if (ec) {
                        obj.put("qty_final", Globalemina.qty.get(i));
                    } else {
                        obj.put("qty_final", 0);
                    }
                    obj.put("qty_stock", Globalemina.stock.get(i));
                    obj.put("brand", Globalemina.brand);
                    obj.put("unit_price", Globalemina.harga.get(i));
                    obj.put("default_code", Globalemina.kode.get(i));
                    obj.put("category_id", Globalemina.kategori.get(i));
                    obj.put("qty_order", Globalemina.sgtorder.get(i));
                    obj.put("direct", true);
                    jo.add(obj);
                    loop.append(obj.toString());
                    if (i<Globalemina.nama.size()-1){
                        loop.append(", ");
                    }
                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                Log.e("PRODUK", "["+loop.toString()+"]");

                writer.write("["+loop.toString()+"]");
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Buffer", sb.toString());
                    return json;
                } else {
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (check1 && check2 && check3 && check4) {

                Log.e("KIRIM DATA", "FINISHING SENDING EMINA PRODUCTS");
                new CekComplete(do_id).execute();

            }
            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OrderLinesPutri extends AsyncTask<String, Void, String> {

        private final int do_id;
        StringBuffer sb = new StringBuffer();

        OrderLinesPutri(int do_id) {
            this.do_id = do_id;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING PUTRI PRODUCTS DATA");
            String json = "";

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order_line");

                ArrayList<JSONObject> jo = new ArrayList<JSONObject>();
                StringBuilder loop = new StringBuilder();

                boolean ec = prefToko.getBoolean("ec", true);

                for (int i = 0; i < Globalputri.nama.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("do_id", do_id - 1);
                    obj.put("product_id", Globalputri.id_produk.get(i));
                    if (ec) {
                        obj.put("qty_final", Globalputri.qty.get(i));
                    } else {
                        obj.put("qty_final", 0);
                    }
                    obj.put("qty_stock", Globalputri.stock.get(i));
                    obj.put("brand", Globalputri.brand);
                    obj.put("unit_price", Globalputri.harga.get(i));
                    obj.put("default_code", Globalputri.kode.get(i));
                    obj.put("category_id", Globalputri.kategori.get(i));
                    obj.put("qty_order", Globalputri.sgtorder.get(i));
                    obj.put("direct", true);
                    jo.add(obj);
                    loop.append(obj.toString());
                    if (i<Globalputri.nama.size()-1){
                        loop.append(", ");
                    }
                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                Log.e("PRODUK", "["+loop.toString()+"]");

                writer.write("["+loop.toString()+"]");
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Buffer", sb.toString());
                    return json;
                } else {
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (check1 && check2 && check3 && check4) {

                Log.e("KIRIM DATA", "FINISHING SENDING WARDAH PRODUCTS");
                new CekComplete(do_id).execute();

            }
            super.onPostExecute(s);
        }
    }

    private class CekComplete extends AsyncTask<String,Void, String> {

        private final int do_id;

        CekComplete(int do_id){
            this.do_id=do_id;
        }

        @Override
        protected String doInBackground(String... strings) {
            boolean ec = prefToko.getBoolean("ec", true);
            StatusToko.clearTotalToko();
            Global.clearProduct();
            Globalputri.clearProduct();
            Globalmo.clearProduct();
            Globalemina.clearProduct();

            AndroidNetworking.get("http://10.3.181.177:3000/delivery_order?do_id=eq."+do_id)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;

                            boolean complete = false;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    boolean status = jo.getBoolean("complete");
                                    Log.e("Complete", ""+status);

                                    if (status) {
                                        complete = true;
                                    }
                                }

                                if (complete) {
                                    editor.putBoolean("berhasil", true);
                                    editor.commit();
                                } else {
                                    editor.putBoolean("berhasil", false);
                                    editor.commit();
                                }


                            } catch (JSONException e) {
                                Log.e("JSON Parsing Error", e.getMessage());
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Error", anError.getMessage());
                        }
                    });


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();

            editor.putBoolean("odoo", true);
            editor.commit();

            kali = 0;

            editorToko.clear();
            editorToko.commit();

            Intent intent = new Intent(TakingOrderActivity.this, KunjunganActivity.class);
            startActivity(intent);
            finish();
            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDOIDW extends AsyncTask<String, Void, String> {

        private final String url;
        private final String brand;
        StringBuffer sb = new StringBuffer();

        AmbilDOIDW(String url, String brand) {
            this.url = url;
            this.brand = brand;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "FETCHING DO ID FROM " + brand);

            if (Global.kode.size() <= 0 && Globalmo.kode.size() <= 0  && Globalemina.kode.size() <= 0 && Globalputri.kode.size() <= 0){
                kosong = true;
            }

            kali++;
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;

                            int max = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");

                                    if (id > max) {
                                        max = id;
                                        maximumIDW = id;
                                    }
                                }

                                Log.e("id1", "\n\nID Delivery Order1 W (max): " + max + ", brand: " + brand);
                                Log.e("id2", "\n\nID Delivery Order2 W (maximumID): " + maximumIDW+ ", brand: " + brand);
                                StatusToko.do_id = max;
                                editorToko.putInt("do_idW", maximumIDW);
                                editorToko.commit();
                                checkW = true;


                            } catch (JSONException e) {
                                Log.e("JSON Parsing Error", e.getMessage());
                                ambilDOIDW = new AmbilDOIDW(url, brand);
                                ambilDOIDW.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Error", anError.getMessage());
                        }
                    });

            return url;

        }

        @Override
        protected void onPostExecute(String s) {


            if (checkW) {
                Log.e("id", "\n\nID Delivery Order W dibawah (pref): " + prefToko.getInt("do_idW", 0));
                Log.e("id2", "\n\nID Delivery Order2 W dibawah (status): " + StatusToko.do_id);
                Log.e("id3", "\n\nID Delivery Order3 W dibawah (maximumID): " + maximumIDW);

                String referencew = prefToko.getString("referencew", "");
                String partnerid = prefToko.getString("partner_id", "");
                String namatoko = prefToko.getString("partner_name", "");

                DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(TakingOrderActivity.this);
                ArrayList<OrderedProduct> op = new ArrayList<OrderedProduct>();
                DatabaseStoreOrder dbStoreOrder = new DatabaseStoreOrder(TakingOrderActivity.this);

                if (Global.nama.size()>0){

                    for (int i = 0; i < Global.nama.size(); i++ ){
                        op.add(new OrderedProduct(Global.id_produk.get(i), Global.kode.get(i),
                                Global.nama.get(i), Global.harga.get(i), Integer.parseInt(Global.stock.get(i)),
                                Integer.parseInt(Global.sgtorder.get(i)), Integer.parseInt(Global.qty.get(i)),
                                Global.kategori.get(i), Global.brand, partnerid, referencew,
                                prefToko.getInt("do_idW", maximumIDW), namatoko));
                    }
                    try {
                        dbOrder.addAllProduk(op, op.size());
                        dbStoreOrder.addProduk(new StoreOrderList(prefToko.getInt("do_idW", maximumIDW),
                                partnerid, namatoko, referencew, "Wardah"));
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e("SQLite_Error_W", e.getMessage());
                    }
                }
                if (Global.kode.size() > 0) {
                    Log.e("KIRIM DATA", "SENDING WARDAH PRODUCTS DATA");
                    new OrderLinesWardah(prefToko.getInt("do_idW", maximumIDW)+1).execute();
//
//                    }
                    check1 = true;
                } else {
                    check1 = true;
                }

            } else {
                ambilDOIDW = new AmbilDOIDW(url, brand);
                ambilDOIDW.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDOIDM extends AsyncTask<String, Void, String> {

        private final String url;
        private final String brand;
        StringBuffer sb = new StringBuffer();

        AmbilDOIDM(String url, String brand) {
            this.url = url;
            this.brand = brand;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "FETCHING DO ID FROM " + brand);

            if (Global.kode.size() <= 0 && Globalmo.kode.size() <= 0  && Globalemina.kode.size() <= 0 && Globalputri.kode.size() <= 0){
                kosong = true;
            }

            kali++;
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;

                            int max = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");

                                    if (id > max) {
                                        max = id;
                                        maximumIDMO = id;
                                    }
                                }

                                Log.e("id1", "\n\nID Delivery Order1 M (max): " + max + ", brand: " + brand);
                                Log.e("id2", "\n\nID Delivery Order2 M (maximumID): " + maximumIDMO+ ", brand: " + brand);
                                StatusToko.do_id = max;
                                editorToko.putInt("do_idM", maximumIDMO);
                                editorToko.commit();
                                checkM = true;


                            } catch (JSONException e) {
                                Log.e("JSON Parsing Error", e.getMessage());
                                ambilDOIDM = new AmbilDOIDM(url, brand);
                                ambilDOIDM.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Error", anError.getMessage());
                        }
                    });

            return url;

        }

        @Override
        protected void onPostExecute(String s) {


            if (checkM) {
                Log.e("id", "\n\nID Delivery Order M dibawah (pref): " + prefToko.getInt("do_idM", 0));
                Log.e("id2", "\n\nID Delivery Order2 M dibawah (status): " + StatusToko.do_id);
                Log.e("id3", "\n\nID Delivery Order3 M dibawah (maximumID): " + maximumIDMO);
                String referencem = prefToko.getString("referencem", "");
                String partnerid = prefToko.getString("partner_id", "");
                String namatoko = prefToko.getString("partner_name", "");

                DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(TakingOrderActivity.this);
                ArrayList<OrderedProduct> op = new ArrayList<OrderedProduct>();
                DatabaseStoreOrder dbStoreOrder = new DatabaseStoreOrder(TakingOrderActivity.this);

                if (Globalmo.nama.size()>0){

                    for (int i = 0; i < Globalmo.nama.size(); i++ ){
                        op.add(new OrderedProduct(Globalmo.id_produk.get(i), Globalmo.kode.get(i),
                                Globalmo.nama.get(i), Globalmo.harga.get(i), Integer.parseInt(Globalmo.stock.get(i)),
                                Integer.parseInt(Globalmo.sgtorder.get(i)), Integer.parseInt(Globalmo.qty.get(i)),
                                Globalmo.kategori.get(i), Globalmo.brand, partnerid, referencem,
                                prefToko.getInt("do_idM", maximumIDMO), namatoko));
                    }
                    try {
                        dbOrder.addAllProduk(op, op.size());
                        dbStoreOrder.addProduk(new StoreOrderList(prefToko.getInt("do_idM", maximumIDMO),
                                partnerid, namatoko, referencem, "Make Over"));
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e("SQLite_Error_M", e.getMessage());
                    }
                }

                if (Globalmo.kode.size() > 0) {
                    Log.e("KIRIM DATA", "SENDING MAKE OVER PRODUCTS DATA");
                    check2 = true;
                } else {
                    check2 = true;
                }

            } else {
                ambilDOIDM = new AmbilDOIDM(url, brand);
                ambilDOIDM.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDOIDE extends AsyncTask<String, Void, String> {

        private final String url;
        private final String brand;
        StringBuffer sb = new StringBuffer();

        AmbilDOIDE(String url, String brand) {
            this.url = url;
            this.brand = brand;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "FETCHING DO ID FROM " + brand);

            if (Global.kode.size() <= 0 && Globalmo.kode.size() <= 0  && Globalemina.kode.size() <= 0 && Globalputri.kode.size() <= 0){
                kosong = true;
            }

            kali++;
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;

                            int max = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");

                                    if (id > max) {
                                        max = id;
                                        maximumIDE = id;
                                    }
                                }

                                Log.e("id1", "\n\nID Delivery Order1 E (max): " + max + ", brand: " + brand);
                                Log.e("id2", "\n\nID Delivery Order2 E (maximumID): " + maximumIDE+ ", brand: " + brand);
                                StatusToko.do_id = max;
                                editorToko.putInt("do_idE", maximumIDE);
                                editorToko.commit();
                                checkE = true;


                            } catch (JSONException e) {
                                Log.e("JSON Parsing Error", e.getMessage());
                                ambilDOIDE = new AmbilDOIDE(url, brand);
                                ambilDOIDE.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Error", anError.getMessage());
                        }
                    });

            return url;

        }

        @Override
        protected void onPostExecute(String s) {


            if (checkE) {
                Log.e("id", "\n\nID Delivery Order E dibawah (pref): " + prefToko.getInt("do_idE", 0));
                Log.e("id2", "\n\nID Delivery Order2 E dibawah (status): " + StatusToko.do_id);
                Log.e("id3", "\n\nID Delivery Order3 E dibawah (maximumID): " + maximumIDE);

                String referencee = prefToko.getString("referencee", "");
                String partnerid = prefToko.getString("partner_id", "");
                String namatoko = prefToko.getString("partner_name", "");

                DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(TakingOrderActivity.this);
                ArrayList<OrderedProduct> op = new ArrayList<OrderedProduct>();
                DatabaseStoreOrder dbStoreOrder = new DatabaseStoreOrder(TakingOrderActivity.this);

                if (Globalemina.nama.size()>0){

                    for (int i = 0; i < Globalemina.nama.size(); i++ ){
                        op.add(new OrderedProduct(Globalemina.id_produk.get(i), Globalemina.kode.get(i),
                                Globalemina.nama.get(i), Globalemina.harga.get(i), Integer.parseInt(Globalemina.stock.get(i)),
                                Integer.parseInt(Globalemina.sgtorder.get(i)), Integer.parseInt(Globalemina.qty.get(i)),
                                Globalemina.kategori.get(i), Globalemina.brand, partnerid, referencee,
                                prefToko.getInt("do_idE", maximumIDE), namatoko));
                    }
                    try {
                        dbOrder.addAllProduk(op, op.size());
                        dbStoreOrder.addProduk(new StoreOrderList(prefToko.getInt("do_idE", maximumIDMO),
                                partnerid, namatoko, referencee, "Emina"));
//                            }
//                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e("SQLite_Error_E", e.getMessage());
                    }
                }

                if (Globalemina.kode.size() > 0) {
                    Log.e("KIRIM DATA", "SENDING EMINA PRODUCTS DATA");
                    new OrderLinesEmina(prefToko.getInt("do_idE", maximumIDE)+1).execute();
                    check3 = true;
                } else {
                    check3 = true;
                }

            } else {
                ambilDOIDE = new AmbilDOIDE(url, brand);
                ambilDOIDE.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDOIDP extends AsyncTask<String, Void, String> {

        private final String url;
        private final String brand;

        AmbilDOIDP(String url, String brand) {
            this.url = url;
            this.brand = brand;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "FETCHING DO ID FROM " + brand);

            if (Global.kode.size() <= 0 && Globalmo.kode.size() <= 0  && Globalemina.kode.size() <= 0 && Globalputri.kode.size() <= 0){
                kosong = true;
            }

            kali++;
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;

                            int max = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");

                                    if (id > max) {
                                        max = id;
                                        maximumIDP = id;
                                    }
                                }

                                Log.e("id1", "\n\nID Delivery Order1 P (max): " + max + ", brand: " + brand);
                                Log.e("id2", "\n\nID Delivery Order2 P (maximumID): " + maximumIDP+ ", brand: " + brand);
                                StatusToko.do_id = max;
                                editorToko.putInt("do_idP", maximumIDP);
                                editorToko.commit();
                                checkP = true;


                            } catch (JSONException e) {
                                Log.e("JSON Parsing Error", e.getMessage());
                                ambilDOIDP = new AmbilDOIDP(url, brand);
                                ambilDOIDP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Error", anError.getMessage());
                        }
                    });

            return url;

        }

        @Override
        protected void onPostExecute(String s) {


            if (checkP) {
                Log.e("id", "\n\nID Delivery Order P dibawah (pref): " + prefToko.getInt("do_id", 0));
                Log.e("id2", "\n\nID Delivery Order2 P dibawah (status): " + StatusToko.do_id);
                Log.e("id3", "\n\nID Delivery Order3 P dibawah (maximumID): " + maximumIDP);

                String referencep = prefToko.getString("referencep", "");
                String partnerid = prefToko.getString("partner_id", "");
                String namatoko = prefToko.getString("partner_name", "");

                DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(TakingOrderActivity.this);
                ArrayList<OrderedProduct> op = new ArrayList<OrderedProduct>();
                DatabaseStoreOrder dbStoreOrder = new DatabaseStoreOrder(TakingOrderActivity.this);

                if (Globalputri.nama.size()>0){

                    for (int i = 0; i < Globalputri.nama.size(); i++ ){
                        op.add(new OrderedProduct(Globalputri.id_produk.get(i), Globalputri.kode.get(i),
                                Globalputri.nama.get(i), Globalputri.harga.get(i), Integer.parseInt(Globalputri.stock.get(i)),
                                Integer.parseInt(Globalputri.sgtorder.get(i)), Integer.parseInt(Globalputri.qty.get(i)),
                                Globalputri.kategori.get(i), Globalputri.brand, partnerid, referencep,
                                prefToko.getInt("do_idP", maximumIDP), namatoko));
                    }
                    try {
                        dbOrder.addAllProduk(op, op.size());
                        dbStoreOrder.addProduk(new StoreOrderList(prefToko.getInt("do_idP", maximumIDMO),
                                partnerid, namatoko, referencep, "Putri"));
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e("SQLite_Error", e.getMessage());
                    }
                }

                if (Globalputri.nama.size() > 0 && Globalputri.nama != null) {
                    Log.e("KIRIM DATA", "SENDING PUTRI PRODUCTS DATA");
                    new OrderLinesPutri(prefToko.getInt("do_idP", maximumIDP)+1).execute();
                    check4 = true;
                } else {
                    check4 = true;
                }
            } else {
                ambilDOIDP = new AmbilDOIDP(url, brand);
                ambilDOIDP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CekVisitID extends AsyncTask<String, Void, String> {
        private final String url;
        private final String partnerref;
        private final String userid;
        private final String salesid;
        private final String partnerid;
        private final int visitid;
        private final String partnername;
        private final String visittime;
        private final String outtime;
        private final String loclat;
        private final String loclong;
        int max;
        String alasan, selesai;

        CekVisitID(String url, String partnerref, String userid, String salesid, String partnerid,
                   String partnername, int visitid, String visittime, String outtime, String loclong, String loclat){
            this.url = url;
            this.partnername = partnername;
            this.visitid = visitid;
            this.partnerref = partnerref;
            this.partnerid = partnerid;
            this.userid = userid;
            this.salesid = salesid;
            this.visittime = visittime;
            this.outtime = outtime;
            this.loclong = loclong;
            this.loclat = loclat;
        }

        @Override
        protected String doInBackground(String... strings) {
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            max = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");
                                    if (id > max) {
                                        max = id;
                                        alasan = jo.getString("reason");
                                        selesai = jo.getString("selesai_time");
                                    }
                                }
                                editorToko.putInt("id", max);
                                editorToko.putString("reason", alasan);
                                editorToko.putString("waktu_selesai", selesai);

                                editorToko.commit();
                                Log.e("id", "ID Visit: " + max);
                                Log.e("id2", "ID Visit2: " + prefToko.getInt("id", 0));
                                Log.e("alasan", alasan);
                                Log.e("selesai", selesai);

                                visID = true;
                            } catch (JSONException e) {
                                Log.e("CANT PARSE JSON", e.getMessage());
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR", "Error :"+anError.getMessage());
                        }
                    });
            return "NICE";
        }

        @Override
        protected void onPostExecute(String s) {
            if (visID){
                Calendar calander = Calendar.getInstance();
                SimpleDateFormat simpledateformat1 = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat simpledateformat2 = new SimpleDateFormat("ddMMyy");
                SimpleDateFormat simpledateformat3 = new SimpleDateFormat("HHmmss");
                String Date1 = simpledateformat1.format(calander.getTime());
                String Date2 = simpledateformat2.format(calander.getTime());
                String Date3 = simpledateformat3.format(calander.getTime());
                Log.e("TANGGAL1", Date1);
                Log.e("TANGGAL2", Date2);
                Log.e("TANGGAL3", Date3);

                final String referencewardah = "SFM/" + partnerid + "-W/" + Date2 + "/" + Date3;
                final String referencemo = "SFM/" + partnerid + "-MO/" + Date2 + "/" + Date3;
                final String referenceemina = "SFM/" + partnerid + "-E/" + Date2 + "/" + Date3;
                final String referenceputri = "SFM/" + partnerid + "-P/" + Date2 + "/" + Date3;
                Log.e("date", referencewardah);

                editorToko.putString("referencew", referencewardah);
                editorToko.putString("referencem", referencemo);
                editorToko.putString("referencee", referenceemina);
                editorToko.putString("referencep", referenceputri);
                editorToko.commit();

                String state = "draft";

                if (Global.kode.size()>0){
                    sendOrder = new SendOrder(partnerref, Integer.parseInt(userid), Integer.parseInt(salesid),
                            Integer.parseInt(partnerid), referencewardah, state, Global.totalorder,
                            Global.totalitem, Global.totalsku, partnername, visitid, Global.brand, Global.notes);
                    sendOrder.execute();
                } else{
                    check1 = true;
                }
                if (Globalmo.kode.size()>0){
                    sendOrder = new SendOrder(partnerref, Integer.parseInt(userid), Integer.parseInt(salesid),
                            Integer.parseInt(partnerid), referencemo, state, Globalmo.totalorder,
                            Globalmo.totalitem, Globalmo.totalsku, partnername, visitid, Globalmo.brand, Globalmo.notes);
                    sendOrder.execute();
                } else{
                    check2 = true;
                }
                if (Globalemina.kode.size()>0){
                    sendOrder = new SendOrder(partnerref, Integer.parseInt(userid), Integer.parseInt(salesid),
                            Integer.parseInt(partnerid), referenceemina, state, Globalemina.totalorder,
                            Globalemina.totalitem, Globalemina.totalsku, partnername, visitid, Globalemina.brand, Globalemina.notes);
                    sendOrder.execute();
                } else{
                    check3 = true;
                }
                if (Globalputri.kode.size()>0){
                    sendOrder = new SendOrder(partnerref, Integer.parseInt(userid), Integer.parseInt(salesid),
                            Integer.parseInt(partnerid), referenceputri, state, Globalputri.totalorder,
                            Globalputri.totalitem, Globalputri.totalsku, partnername, visitid, Globalputri.brand, Globalputri.notes);
                    sendOrder.execute();
                } else{
                    check4 = true;
                }


            } else {
                Log.e("ULANG", "Cek visit ID lagi");
                mAuthTask = new SendRequest(partnerref,userid,salesid,partnerid,"",visittime,outtime, loclat,loclong, visitid, alasan, partnername);
                mAuthTask.execute();
            }
        }
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

            case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return 1;

            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:

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
