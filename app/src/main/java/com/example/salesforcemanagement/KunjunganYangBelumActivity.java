package com.example.salesforcemanagement;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.salesforcemanagement.R;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class KunjunganYangBelumActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;
    ArrayList<Spacecraftnotvisittall> spacecrafts = new ArrayList<>();
    ListView myListView;
    ListViewAdapter adapter;
    private Button sendalasannotvisit;
    private Spinner spinnernotvisit;
    private ListViewAdapter.SendRequest mAuthTask = null;

    public static String getPostDataString(JSONObject params) throws Exception {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kunjungan_yang_belum);
        TabLayout tabLayout = findViewById(R.id.tab_layoutreasonnotvisitall);

        tabLayout.addTab(tabLayout.newTab().setText("Alasan Tidak Mengunjungi Toko"));
//        final ProgressBar myProgressBar = findViewById(R.id.myProgressBarkunjungan);
        sendalasannotvisit = findViewById(R.id.save_gambarnotvisitall);
        spinnernotvisit = findViewById(R.id.spinner_notvisitall);

        pref = getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
        myListView = findViewById(R.id.listtokoall);
        final ArrayList<Spacecraftnotvisittall> downloadedData = new ArrayList<Spacecraftnotvisittall>();
        adapter = new ListViewAdapter(downloadedData);
        myListView.setAdapter(adapter);


//            myProgressBar.setIndeterminate(true);
//            myProgressBar.setVisibility(View.VISIBLE);

        final String sales_id = pref.getString("sales_id", "");
        Log.e("sales id", sales_id);
        String url = "https://sfa-api.pti-cosmetics.com/v_partner_inroute?dc_name=ilike.*DC%Jakarta&sales_id=eq." + sales_id;
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        Spacecraftnotvisittall s;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jo = response.getJSONObject(i);
                                int id = jo.getInt("id");
                                String name = jo.getString("ref");
                                String propellant = jo.getString("partner_name");
                                String salesid = jo.getString("sales_id");
                                String partnerid = jo.getString("partner_id");
                                String konst = jo.getString("const");
                                s = new Spacecraftnotvisittall();
                                s.setId(id);
                                s.setName(name);
                                s.setPropellant(propellant);
                                s.setSalesid(salesid);
                                s.setPartnerId(partnerid);
                                s.setFrekuensi(konst);
                                com.example.salesforcemanagement.TokoBelumDikunjungi.idtoko.add(id);
                                com.example.salesforcemanagement.TokoBelumDikunjungi.partneridtoko.add(partnerid);
                                com.example.salesforcemanagement.TokoBelumDikunjungi.salesidtoko.add(salesid);
                                com.example.salesforcemanagement.TokoBelumDikunjungi.namatoko.add(propellant);
                                com.example.salesforcemanagement.TokoBelumDikunjungi.kodetoko.add(name);

                                if (com.example.salesforcemanagement.StatusToko.statuskunjungan != null) {
                                    for (int y = 0; y < com.example.salesforcemanagement.StatusToko.statuskunjungan.size(); y++) {
                                        if (!com.example.salesforcemanagement.StatusToko.statuskunjungan.get(y).equals("1") && com.example.salesforcemanagement.StatusToko.kodetoko.get(y).equals(name)) {
                                            Log.e("TokoBelumDikunjungi_" + y, com.example.salesforcemanagement.StatusToko.namatoko.get(y));
                                            for (int z = 0; z < com.example.salesforcemanagement.TokoBelumDikunjungi.idtoko.size(); z++) {
                                                if (com.example.salesforcemanagement.StatusToko.namatoko.get(y).equals(com.example.salesforcemanagement.TokoBelumDikunjungi.namatoko.get(z))) {
                                                    com.example.salesforcemanagement.TokoBelumDikunjungi.idtoko.remove(z);
                                                    com.example.salesforcemanagement.TokoBelumDikunjungi.partneridtoko.remove(z);
                                                    com.example.salesforcemanagement.TokoBelumDikunjungi.salesidtoko.remove(z);
                                                    com.example.salesforcemanagement.TokoBelumDikunjungi.namatoko.remove(z);
                                                    com.example.salesforcemanagement.TokoBelumDikunjungi.kodetoko.remove(z);
                                                }
                                            }
                                        }
                                    }
                                }

                                downloadedData.add(s);
                                adapter.notifyDataSetChanged();
                            }
//                                myProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
//                                myProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    //ERROR
                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
//                            myProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

//        spacecrafts = new JSONDownloader(this).retrieve(myListView);
//        Log.e("Listview", myListView.toString());

        sendalasannotvisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()){
                    final String status;

                    status = spinnernotvisit.getSelectedItem().toString();
                    Log.e("alasan", status);


                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = simpleDateFormat.format(calendar.getTime());
                    Log.e("waktu", currentTime);
                    com.example.salesforcemanagement.StatusToko.waktuselesai.add(currentTime);
                    editorToko.putString("waktu_selesai", currentTime);
                    editorToko.commit();

                    String user_id = pref.getString("user_id", "null");
                    String sales_id = pref.getString("sales_id", "null");
                    String date = pref.getString("write_date", "-");
                    for (int i = 0; i < com.example.salesforcemanagement.TokoBelumDikunjungi.idtoko.size(); i++) {
                        mAuthTask = new ListViewAdapter.SendRequest(com.example.salesforcemanagement.TokoBelumDikunjungi.kodetoko.get(i), user_id, sales_id,
                                com.example.salesforcemanagement.TokoBelumDikunjungi.partneridtoko.get(i), currentTime, currentTime, "",
                                "", "", 0, status);
                        mAuthTask.execute();
                    }
                    Log.e("Jumlah List1", ""+myListView.getCount());
                    Log.e("Jumlah List2", ""+adapter.getCount());
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                    LoggingOut loggingOut = new LoggingOut();
                    loggingOut.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                    final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(getBaseContext());
//                    final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(getBaseContext());
//                    final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(getBaseContext());
//                    final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(getBaseContext());
//                    final ArrayList<TokoDalamRute> tdr = dbtoko.getAllToko();
//                    final ArrayList<Spacecraft> listEBP = dbEBP.getAllProduk();
//                    final ArrayList<Spacecraft> listMHS = dbMHS.getAllProduk();
//                    final ArrayList<Spacecraft> listNPDP = dbNPDP.getAllProduk();
//                    Intent intent = new Intent(KunjunganYangBelumActivity.this, MainActivity.class);
//
//                    StatusSR.clearAll();
//                    StatusToko.clearToko();
//                    GlobalWardah.clearProduct();
//                    Global.clearProduct();
//                    Globalemina.clearProduct();
//                    Globalmo.clearProduct();
//                    Globalputri.clearProduct();
//                    TokoBelumDikunjungi.clearBelumDikunjungi();
//                    for (TokoDalamRute toko : tdr){
//                        dbtoko.deleteContact(toko);
//                    }
//                    for (Spacecraft EBP : listEBP){
//                        dbEBP.deleteProduk(EBP);
//                    }
//                    for (Spacecraft MHS : listMHS){
//                        dbMHS.deleteProduk(MHS);
//                    }
//                    for (Spacecraft NPD : listNPDP){
//                        dbNPDP.deleteProduk(NPD);
//                    }
//                    editor.clear();
//                    editor.commit();

//                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Anda tidak terhubung ke internet!!\nPastikan anda mendapatkan sinyal internet yang memadai sebelum memilih tombol KIRIM...", Toast.LENGTH_LONG).show();

//                    AlertDialog.Builder dialog = new AlertDialog.Builder(getBaseContext());
//                    dialog.setCancelable(true);
//                    dialog.setTitle("TIDAK ADA KONEKSI INTERNET");
//                    dialog.setMessage("Anda tidak terhubung ke internet!!\nPastikan anda mendapatkan sinyal internet yang memadai sebelum memilih tombol KIRIM...");
//                    dialog.show();
                }


            }
        });


    }

    public void pilihAlasan(View view) {
        TextView alasan = findViewById(R.id.tampil_alasannotvisitall);
        Spinner spinner = findViewById(R.id.spinner_notvisitall);
        String alasanTunda = String.valueOf(spinner.getSelectedItem());
        alasan.setText(alasanTunda);
    }

//    @Override
//    public void onBackPressed() {
//    }

    static class ViewHolder implements Serializable {
        TextView txtName;
        TextView txtPropellant;
        CardView cardView;
    }

    public static class ListViewAdapter extends BaseAdapter implements Filterable {
        public ArrayList<Spacecraftnotvisittall> currentList;
        Context c;
        ArrayList<Spacecraftnotvisittall> spacecrafts;

        public ListViewAdapter(ArrayList<Spacecraftnotvisittall> spacecrafts) {
            super();
            this.spacecrafts = spacecrafts;
        }

        @Override
        public int getCount() {
            return spacecrafts.size();
        }

        @Override
        public Object getItem(int i) {
            return spacecrafts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            ViewHolder viewHolder = new ViewHolder();
            StringBuffer sb = new StringBuffer();
            if (view == null) {
//                view = View.inflate(c,R.layout.model_row_rute_dalam, viewGroup);
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_row_rute_dalam, viewGroup, false);
                viewHolder.txtName = view.findViewById(R.id.id_customer);
                viewHolder.txtPropellant = view.findViewById(R.id.nama_toko);
                viewHolder.cardView = view.findViewById(R.id.dalamrute);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                viewHolder.txtName.setText("");
                viewHolder.txtPropellant.setText("");
                viewHolder.cardView.setBackgroundColor(Color.WHITE);
                viewHolder.txtPropellant.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtName.setTypeface(null, Typeface.NORMAL);
            }

            final ConstraintLayout constraintLayout = view.findViewById(R.id.layoutdalamrute);
            final Spacecraftnotvisittall s = (Spacecraftnotvisittall) this.getItem(i);
            viewHolder.txtName.setText(s.getName());

            viewHolder.txtPropellant.setText(s.getPropellant());


            if (com.example.salesforcemanagement.StatusToko.namatoko != null) {
                for (int k = 0; k < com.example.salesforcemanagement.StatusToko.namatoko.size(); k++) {
                    if (spacecrafts.get(i).getPropellant().equals(com.example.salesforcemanagement.StatusToko.namatoko.get(k))) {
                        if (!com.example.salesforcemanagement.StatusToko.statuskunjungan.isEmpty()) {
                            if (com.example.salesforcemanagement.StatusToko.statuskunjungan.get(k).equals("0")) {
                                //NOT VISIT - merah
                                viewHolder.cardView.setBackgroundColor(Color.rgb(219, 93, 93));
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                            }

                            // TUNDA - KUNING
                            if (com.example.salesforcemanagement.StatusToko.statuskunjungan.get(k).equals("1")) {
                                viewHolder.cardView.setBackgroundColor(Color.rgb(226, 222, 90));
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                //VISIT NOT EC - biru
                            }

                            if (com.example.salesforcemanagement.StatusToko.statuskunjungan.get(k).equals("2")) {
                                viewHolder.cardView.setBackgroundColor(Color.rgb(58, 139, 207));
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);

                            }

                            if (com.example.salesforcemanagement.StatusToko.statuskunjungan.get(k).equals("3")) {
                                //VISIT EC hijau
                                viewHolder.cardView.setBackgroundColor(Color.rgb(61, 168, 109));
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);

                            }
                        }

                    }
                }
            }
            return view;
        }

        public void setSpacecrafts(ArrayList<Spacecraftnotvisittall> filteredSpacecrafts) {
            this.spacecrafts = filteredSpacecrafts;
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        @Override
        public Filter getFilter() {
            return null;
        }

        @SuppressLint("StaticFieldLeak")
        private static class SendRequest extends AsyncTask<String, Void, String> {

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
                    URL url = new URL("https://sfa-api.pti-cosmetics.com/visit");
                    Log.e("ID Visit 4", "" + idvisit);
                    JSONObject obj = new JSONObject();
//                obj.put("id", id);
                    obj.put("partner_ref", partnerref);
                    obj.put("user_id", userid);
                    obj.put("partner_id", partnerid);
                    obj.put("sales_id", salesid);
                    obj.put("datang_time", visittime);
                    obj.put("state", "0");
                    obj.put("selesai_time", "");
////                obj.put("id", visitid);
                    obj.put("latitude", "");
                    obj.put("longitude", "");
                    obj.put("reason", alasan);
                    obj.put("inroute", true);
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
                } catch (Exception e) {
                    Log.e("Error", "Connection Error");
                    return "Connection Error";
                }

            }
        }

    }

    public class Spacecraftnotvisittall {
        /*
        INSTANCE FIELDS
        */
        private int id;
        private String name;
        private String propellant;
        private String price;
        private String salesid;
        private String partnerid;
        //        private String imageURL;
        private int technologyExists;
        private String frekuensi;
        /*
        GETTERS AND SETTERS
        */


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPropellant() {
            return propellant;
        }

        public void setPropellant(String propellant) {
            this.propellant = propellant;
        }

        public String getSalesid() {
            return salesid;
        }

        public void setSalesid(String salesid) {
            this.salesid = salesid;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getFrekuensi() {
            return frekuensi;
        }

        public void setFrekuensi(String frekuensi) {
            this.frekuensi = frekuensi;
        }

        public int getTechnologyExists() {
            return technologyExists;
        }

        public void setTechnologyExists(int technologyExists) {
            this.technologyExists = technologyExists;
        }

        /*
        TOSTRING
        */
        @Override
        public String toString() {
            return name;
        }

        public String getPartnerId() {
            return partnerid;
        }

        public void setPartnerId(String partnerid) {
            this.partnerid = partnerid;
        }
    }

    private class LoggingOut extends AsyncTask<String, Void, String>{

        final DatabaseTokoHandler dbtoko = new DatabaseTokoHandler(getBaseContext());
        final DatabaseProdukEBPHandler dbEBP = new DatabaseProdukEBPHandler(getBaseContext());
        final DatabaseMHSHandler dbMHS = new DatabaseMHSHandler(getBaseContext());
        final DatabaseNPDPromoHandler dbNPDP = new DatabaseNPDPromoHandler(getBaseContext());
//        final ArrayList<TokoDalamRute> tdr = dbtoko.getAllToko();
//        final ArrayList<Spacecraft> listEBP = dbEBP.getAllProduk();
//        final ArrayList<Spacecraft> listMHS = dbMHS.getAllProduk();
//        final ArrayList<Spacecraft> listNPDP = dbNPDP.getAllProduk();

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog =  new ProgressDialog(KunjunganYangBelumActivity.this);
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

            com.example.salesforcemanagement.StatusSR.clearAll();
            com.example.salesforcemanagement.StatusToko.clearToko();
            Global.clearProduct();
            Globalemina.clearProduct();
            Globalmo.clearProduct();
            Globalputri.clearProduct();
            com.example.salesforcemanagement.TokoBelumDikunjungi.clearBelumDikunjungi();
            Log.e("LOGOUT STATUS", "DELETING LOCAL VARIABLE");
            dbtoko.deleteAll();
            dbEBP.deleteAll();
            dbMHS.deleteAll();
            dbNPDP.deleteAll();
//            for (TokoDalamRute toko : tdr){
//                dbtoko.deleteContact(toko);
//                Log.e("LOGOUT STATUS", "DELETING DATABASE TOKO");
//            }
//            for (Spacecraft EBP : listEBP){
//                dbEBP.deleteProduk(EBP);
//                Log.e("LOGOUT STATUS", "DELETING DATABASE EBP");
//            }
//            for (Spacecraft MHS : listMHS){
//                dbMHS.deleteProduk(MHS);
//                Log.e("LOGOUT STATUS", "DELETING DATABASE MHS");
//            }
//            for (Spacecraft NPD : listNPDP){
//                dbNPDP.deleteProduk(NPD);
//                Log.e("LOGOUT STATUS", "DELETING DATABASE NPD & PROMO");
//            }
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

            androidx.appcompat.app.AlertDialog.Builder popup = new AlertDialog.Builder(KunjunganYangBelumActivity.this);
            popup.setTitle("Log Out");
            popup.setMessage("Log out berhasil!! WOW! AMAZING!!");
            popup.setCancelable(false);
            popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(KunjunganYangBelumActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            popup.show();
//            super.onPostExecute(s);
        }
    }
}