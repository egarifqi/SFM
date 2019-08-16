package com.example.sfmtesting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.sfmtesting.R;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class ReasonNotVisitActivity extends AppCompatActivity {
    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;
    private Button sendalasannotvisit;
    private Spinner spinnernotvisit;

    private SendRequest mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reason_not_visit);
        TabLayout tabLayout = findViewById(R.id.tab_layoutreasonnotvisit);

        tabLayout.addTab(tabLayout.newTab().setText("Alasan Tidak Mengunjungi Toko"));
        sendalasannotvisit = findViewById(R.id.save_gambarnotvisit);
        spinnernotvisit = findViewById(R.id.spinner_notvisit);

        pref = getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();


        sendalasannotvisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()){
                    final String status;

                    status = spinnernotvisit.getSelectedItem().toString();
                    Log.e("alasan", status);

                    Intent intent = new Intent(ReasonNotVisitActivity.this, KunjunganActivity.class);

                    com.example.sfmtesting.StatusToko.kodetoko.add(prefToko.getString("partner_name", ""));
                    com.example.sfmtesting.StatusToko.namatoko.add(prefToko.getString("ref", ""));
                    com.example.sfmtesting.StatusToko.statuskunjungan.add("0");
                    com.example.sfmtesting.StatusToko.totalamount.add(0);
                    com.example.sfmtesting.StatusToko.totalqty.add(0);
                    com.example.sfmtesting.StatusToko.totalsku.add(0);
//                Log.e("TOTAL", "Order : " + StatusToko.totalamount.get(StatusToko.getCount() - 1)
//                        + ",\n Qty : " + StatusToko.totalqty.get(StatusToko.getCount() - 1) +
//                        ",\n SKU : " + StatusToko.totalsku.get(StatusToko.getCount() - 1));
                    com.example.sfmtesting.StatusSR.subtotal = 0;
                    com.example.sfmtesting.StatusSR.totalNotCall += 1;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = simpleDateFormat.format(calendar.getTime());
                    com.example.sfmtesting.StatusToko.waktuselesai.add(currentTime);
                    editorToko.putString("waktu_selesai", currentTime);
                    editorToko.commit();

                    int visitid = prefToko.getInt("id", 0);
                    String partner_ref = prefToko.getString("ref", "null");
                    String user_id = pref.getString("user_id", "null");
                    String sales_id = pref.getString("sales_id", "null");
                    String partner_id = prefToko.getString("partner_id", "null");
                    String date = pref.getString("write_date", "-");
                    String waktu_datang = prefToko.getString("waktu_mulai", "-");
                    String waktu_keluar = prefToko.getString("waktu_selesai", "-");
                    String latitude = prefToko.getString("latitude", "-");
                    String longitude = prefToko.getString("longitude", "-");

                    editor.putBoolean("odoo", false);
                    editor.commit();

                    mAuthTask = new SendRequest(partner_ref, user_id, sales_id, partner_id, date, waktu_datang, waktu_keluar, latitude, longitude, visitid, status);
                    mAuthTask.execute();

                    startActivity(intent);
                    finish();
                } else {
//                    Toast.makeText(getBaseContext(), "Anda tidak terhubung ke internet!!\nPastikan anda mendapatkan sinyal internet yang memadai sebelum memilih tombol KIRIM...", Toast.LENGTH_LONG).show();

                    AlertDialog.Builder dialog = new AlertDialog.Builder(ReasonNotVisitActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle("TIDAK ADA KONEKSI INTERNET");
                    dialog.setMessage("Anda tidak terhubung ke internet!!\nPastikan anda mendapatkan sinyal internet yang memadai sebelum memilih tombol KIRIM...");
                    dialog.show();
                }

            }
        });
    }

    public void pilihAlasan(View view) {
        TextView alasan = findViewById(R.id.tampil_alasannotvisit);
        Spinner spinner = findViewById(R.id.spinner_notvisit);
        String alasanTunda = String.valueOf(spinner.getSelectedItem());
        alasan.setText(alasanTunda);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent backintent = new Intent(ReasonActivity.this, KunjunganActivity.class);
//        StatusToko.statuskunjungan.add("0");
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-mm-dd HH:mm:ss");
//        String currentTime = simpleDateFormat.format(calendar.getTime());
//        StatusToko.waktuselesai.add(currentTime);
//        editor.putString("selesai_time", currentTime);
//        editor.commit();
//
//
//
//        startActivity(backintent);
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
        StringBuffer sb = new StringBuffer();

        SendRequest(String partnerref, String userid, String salesid, String partnerid,
                    String todaydate, String visittime, String outtime, String loclat,
                    String loclong, int visitid, String alasan) {
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
        }


        @Override
        protected String doInBackground(String... strings) {
            String json = "";

//            int id = pref.getInt("id", 0);
            int idvisit = visitid;

            try {
                boolean online = prefToko.getBoolean("online", true);
                Log.e("Status Internet", ""+online);
                if (online){
                    URL url = new URL("http://10.3.181.177:3000/visit?id=eq." + idvisit);
                    Log.e("ID Visit 4", "" + idvisit);
                    JSONObject obj = new JSONObject();
//                obj.put("id", id);
//                obj.put("partner_ref", partnerref);
//                obj.put("user_id", userid);
//                obj.put("partner_id", partnerid);
//                obj.put("sales_id", salesid);
                    obj.put("datang_time", visittime);
                    obj.put("state", "0");
                    obj.put("selesai_time", "");
////                obj.put("id", visitid);
                    obj.put("latitude", "");
                    obj.put("longitude", "");
                    obj.put("reason", alasan);
                    Log.e("Bentuk JSON1", obj.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                    if (responseCode == HttpURLConnection.HTTP_OK) {
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
                        Log.e("Response_Code", String.valueOf(responseCode));
                        Log.e("Response_Message", String.valueOf(conn.getResponseMessage()));
                        Log.e("Returned_String", sb.toString());
                        return "false : " + responseCode;
                    }
                } else {
                    URL url2 = new URL("http://10.3.181.177:3000/visit");
                    JSONObject obj = new JSONObject();
                    obj.put("partner_ref", partnerref);
                    obj.put("user_id", userid);
                    obj.put("partner_id", partnerid);
                    obj.put("sales_id", salesid);
                    obj.put("datang_time", visittime);
                    obj.put("state", "0");
//                    obj.put("id", visitid);
                    obj.put("latitude", loclat);
                    obj.put("longitude", loclong);
                    obj.put("inroute", true);
                    obj.put("selesai_time", outtime);
                    obj.put("reason", "");
//                    obj.put("check")
//                    obj.put("date", todaydate);
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
                        mAuthTask = new SendRequest(partnerref,userid,salesid,partnerid,todaydate,visittime,outtime,loclat,loclong,visitid,alasan);
                        mAuthTask.execute();
                        return "false : " + responseCode;
                    }
                }

            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }

        }
    }
}
