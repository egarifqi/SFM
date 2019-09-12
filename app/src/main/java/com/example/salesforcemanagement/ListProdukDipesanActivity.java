package com.example.salesforcemanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class ListProdukDipesanActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    CekVisitID cekVisit = null;
    AmbilDOID ambilDOID = null;
    int maximumID = 0;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listprodukdipesan);
        pref = getApplicationContext().getSharedPreferences("SystemPref", 0);
        editor = pref.edit();
        int do_id = pref.getInt("do_id", 0);
        String do_name = pref.getString("ref", "");
        String visit_name = pref.getString("visit_name", "");
        DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(ListProdukDipesanActivity.this);
        ArrayList<OrderedProduct> listOrder = dbOrder.getAllProdukToko(do_name);
        for (int i = 0; i < listOrder.size(); i++) {
            Log.e("Database_" + i, listOrder.get(i).nama_produk);
        }
        DatabaseVisitHandler dbVisit = new DatabaseVisitHandler(ListProdukDipesanActivity.this);
        StoreVisitList visit = dbVisit.getProduk(visit_name);

        Log.e("ISI VISIT", visit.getPartner_ref() + " - " + visit.getUser_id() + " - " + visit.getSales_id()
                + " - " + visit.getPartner_id() + " - " + visit.getStart_time() + " - " + visit.getFinish_time()
                + " - " + visit.getLatitude() + " - " + visit.getLongitude() + " - " + visit.getReason()
                + " - " + visit.getReference() + " - " + visit.getState());
        ListView produk = (ListView) findViewById(R.id.list_produk);
        ListViewAdapter adapter = new ListViewAdapter(ListProdukDipesanActivity.this, listOrder);
        produk.setAdapter(adapter);

        Button kirim = findViewById(R.id.kirimulang);
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    boolean cek = false;

                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
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
                        } else if (level == 5) {
                            cek = true;
                        } else {
                            cek = false;
                        }
                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        int networkClass = getNetworkClass(getNetworkType(ListProdukDipesanActivity.this));
                        if (networkClass == 1) {
                            cek = false;
                        } else if (networkClass == 2) {
                            cek = true;
                        } else if (networkClass == 3) {
                            cek = true;
                        } else {
                            cek = false;
                        }
                    } else {
                        cek = false;
                    }

                    if (cek) {
                        try {
                            new DeleteLines(do_name).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ERROR_DELETE_LINES", e.getMessage());
                        }

                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ListProdukDipesanActivity.this);
                        dialog.setTitle("GAGAL KIRIM");
                        dialog.setMessage("Gagal mengirim ulang produk..\nKoneksi internet tidak memadai..");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ListProdukDipesanActivity.this);
                    dialog.setTitle("GAGAL KIRIM");
                    dialog.setMessage("Gagal mengirim ulang produk..\nTidak ada koneksi internet..");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });
    }

    public class ViewHolderOrder implements Serializable {
        TextView product_code;
        TextView product_name;
        TextView product_stock;
        TextView product_qty;
    }

    public class ListViewAdapter extends BaseAdapter implements Serializable {
        Context c;
        ArrayList<OrderedProduct> orderedProducts;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<OrderedProduct> orderedProducts) {
            this.c = c;
            this.orderedProducts = orderedProducts;
        }

        @Override
        public int getCount() {
            return orderedProducts.size();
        }

        @Override
        public Object getItem(int i) {
            return orderedProducts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolderOrder holder;
            holder = new ViewHolderOrder();
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.model_row_produk_order, viewGroup, false);

                holder.product_code = (TextView) view.findViewById(R.id.codecontain);
                holder.product_name = (TextView) view.findViewById(R.id.namecontain);
                holder.product_stock = (TextView) view.findViewById(R.id.stockcontain);
                holder.product_qty = (TextView) view.findViewById(R.id.qtycontain);
                view.setTag(holder);
            } else {
                holder = (ViewHolderOrder) view.getTag();
                holder.product_name.setText("");
                holder.product_code.setText("");
                holder.product_stock.setText("");
                holder.product_qty.setText("");
            }
            final OrderedProduct op = (OrderedProduct) this.getItem(i);

            holder.product_name.setText(op.getNama_produk());
            holder.product_code.setText(op.getKode_odoo());
            holder.product_stock.setText("" + op.getStock_produk());
            holder.product_qty.setText("" + op.getFinalorder_produk());

            return view;
        }

        public void refresh() {
            notifyDataSetChanged();
        }
    }

    ProgressDialog dialog;

    @SuppressLint("StaticFieldLeak")
    private class DeleteLines extends AsyncTask<String, Void, String> {

        private final String do_name;
        StringBuffer sb = new StringBuffer();

        DeleteLines(String do_name) {
            this.do_name = do_name;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ListProdukDipesanActivity.this);
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Sedang mengirim data orderan...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order_line?do_name=eq." + do_name);
                Log.e("URL_DELETE_LINES", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("DELETE");
                conn.setDoOutput(true);
                conn.setDoInput(true);

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
                } else {
                    Log.e("Response_Code1", String.valueOf(responseCode));
                    Log.e("Response_Message1", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String1", sb.toString());
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error: " + e.getMessage());
                return "Connection Error";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                new DeleteOrder(do_name).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR_DELETE_ORDER", e.getMessage());
            }
//            DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(ListProdukDipesanActivity.this);
//            ArrayList<OrderedProduct> listOrder = dbOrder.getAllProdukToko(do_);
//            for (int i = 0; i < listOrder.size(); i++){
//                new OrderLines(do_id, listOrder.get(i).getId_produk(), listOrder.get(i).getStock_produk(),
//                        listOrder.get(i).getFinalorder_produk(), listOrder.get(i).getBrand_produk(),
//                        listOrder.get(i).getKode_odoo(), listOrder.get(i).getKategori_produk(),
//                        Integer.parseInt(listOrder.get(i).getHarga_produk()), listOrder.get(i).getSgtorder_produk())
//                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteOrder extends AsyncTask<String, Void, String> {

        private final String do_name;
        StringBuffer sb = new StringBuffer();

        DeleteOrder(String do_name) {
            this.do_name = do_name;
        }
//        @Override
//        protected void onPreExecute() {
//            dialog = new ProgressDialog(ListProdukDipesanActivity.this);
//            dialog.setCancelable(false);
//            dialog.setIndeterminate(false);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setMessage("Sedang mengirim data orderan...");
//            dialog.show();
//        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order?reference=eq." + do_name);
                Log.e("URL_DELETE_ORDER", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("DELETE");
                conn.setDoOutput(true);
                conn.setDoInput(true);

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
                } else {
                    Log.e("Response_Code2", String.valueOf(responseCode));
                    Log.e("Response_Message2", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String2", sb.toString());
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error: " + e.getMessage());
                return "Connection Error";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pref = getApplicationContext().getSharedPreferences("SystemPref", 0);
            editor = pref.edit();
            String visit_name = pref.getString("visit_name", "");
            try {
                new DeleteVisit(visit_name).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR_DELETE_VISIT", e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteVisit extends AsyncTask<String, Void, String> {

        private final String visit_name;
        StringBuffer sb = new StringBuffer();

        DeleteVisit(String do_name) {
            this.visit_name = do_name;
        }
//        @Override
//        protected void onPreExecute() {
//            dialog = new ProgressDialog(ListProdukDipesanActivity.this);
//            dialog.setCancelable(false);
//            dialog.setIndeterminate(false);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setMessage("Sedang mengirim data orderan...");
//            dialog.show();
//        }

        @Override
        protected String doInBackground(String... strings) {

            DatabaseVisitHandler dbVisit = new DatabaseVisitHandler(ListProdukDipesanActivity.this);
            StoreVisitList visit = dbVisit.getProduk(visit_name);

            Log.e("ISI VISIT", visit.getPartner_ref() + " - " + visit.getUser_id() + " - " + visit.getSales_id()
                    + " - " + visit.getPartner_id() + " - " + visit.getStart_time() + " - " + visit.getFinish_time()
                    + " - " + visit.getLatitude() + " - " + visit.getLongitude() + " - " + visit.getReason()
                    + " - " + visit.getReference() + " - " + visit.getState());

            try {
                URL url = new URL("http://10.3.181.177:3000/visit?name=eq." + visit_name);
                Log.e("URL_DELETE_VISIT", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("DELETE");
                conn.setDoOutput(true);
                conn.setDoInput(true);

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
                    Log.e("DV NICE", "HELL YEAH");
                } else {
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error: " + e.getMessage());
                return "Connection Error";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            DatabaseVisitHandler dbVisit = new DatabaseVisitHandler(ListProdukDipesanActivity.this);
            StoreVisitList visit = dbVisit.getProduk(visit_name);

            Log.e("ISI VISIT DB", visit.getPartner_ref() + " - " + visit.getUser_id() + " - " + visit.getSales_id()
                    + " - " + visit.getPartner_id() + " - " + visit.getStart_time() + " - " + visit.getFinish_time()
                    + " - " + visit.getLatitude() + " - " + visit.getLongitude() + " - " + visit.getReason()
                    + " - " + visit.getReference() + " - " + visit.getState());

            new PostVisit(visit.getPartner_ref(), visit.getUser_id(), visit.getSales_id(),
                    visit.getPartner_id(), visit.getStart_time(), visit.getFinish_time(),
                    visit.getLatitude(), visit.getLongitude(), visit.getReason(),
                    visit.getReference(), visit.getState()).execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PostVisit extends AsyncTask<String, Void, String> {

        private final String partnerref;
        private final String userid;
        private final String salesid;
        private final String partnerid;
        private final String visittime;
        private final String outtime;
        private final String loclat;
        private final String loclong;
        private final String alasan;
        private final String ref;
        private final String state;
        int max;
        StringBuffer sb = new StringBuffer();

        PostVisit(String partnerref, String userid, String salesid, String partnerid,
                  String visittime, String outtime, String loclat, String loclong, String alasan,
                  String ref, String state) {
            this.partnerref = partnerref;
            this.userid = userid;
            this.salesid = salesid;
            this.partnerid = partnerid;
            this.loclat = loclat;
            this.loclong = loclong;
            this.visittime = visittime;
            this.outtime = outtime;
            this.alasan = alasan;
            this.ref = ref;
            this.state = state;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING PRODUCTS DATA");
            String json = "";

            try {
                URL url2 = new URL("http://10.3.181.177:3000/visit");
                JSONObject obj = new JSONObject();
                obj.put("partner_ref", partnerref);
                obj.put("user_id", userid);
                obj.put("partner_id", partnerid);
                obj.put("sales_id", salesid);
                obj.put("datang_time", visittime);
                obj.put("latitude", loclat);
                obj.put("longitude", loclong);
                obj.put("inroute", true);
                obj.put("selesai_time", outtime);
                obj.put("reason", alasan);
                obj.put("state", state);
                obj.put("name", ref);
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

//                    Calendar calander = Calendar.getInstance();
//                    SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
//                    String currentDate = simpledateformat.format(calander.getTime());
//
//                    String url = "http://10.3.181.177:3000/visit?user_id=eq." + userid + "&&write_date=gte." + currentDate;
//                    Log.e("URL_VISIT_ID", url);
//
//                    AndroidNetworking.get(url)
//                            .setPriority(Priority.HIGH)
//                            .build()
//                            .getAsJSONArray(new JSONArrayRequestListener() {
//                                @Override
//                                public void onResponse(JSONArray response) {
//                                    JSONObject jo;
//                                    max = 0;
//                                    try {
//                                        for (int i = 0; i < response.length(); i++) {
//                                            jo = response.getJSONObject(i);
//                                            int id = jo.getInt("id");
//                                            if (id > max) {
////                                        Log.e("visit_id_"+i, ""+id);
//                                                max = id;
//                                            }
//                                        }
//
//                                        editor.putInt("id", max);
//                                        editor.commit();
//                                        Log.e("id", "ID Visit: " + max);
//                                        Log.e("id2", "ID Visit2: " + pref.getInt("id", 0));
//
//                                    } catch (JSONException e) {
//                                        Log.e("CANT PARSE JSON", e.getMessage());
////                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                }
//
//                                //ERROR
//                                @Override
//                                public void onError(ANError anError) {
//                                    anError.printStackTrace();
//                                    Log.e("PARSING ERROR 1", "Error :" + anError.toString() + " - " + anError.getErrorBody());
////                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                    DatabaseStoreOrder dbOrder = new DatabaseStoreOrder(ListProdukDipesanActivity.this);
//                    String reference = pref.getString("ref", "");
//                    Log.e("Reference", reference);
//                    StoreOrderList order = dbOrder.getProduk(reference);
//
//                    Log.e("DO", order.getPartner_id() + " - " + order.getUser_id() + " - " +
//                            order.getSales_id() + " - " + order.getPartner_id() + " - " + order.getReference()
//                            + " - " + order.getTotal_amount() + " - " + order.getTotal_item() + " - " + order.getTotal_sku()
//                            + " - " + order.getNama_toko() + " - " + order.getBrand_produk() + " - " + order.getNote());
//                    int do_id = pref.getInt("id", max);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.e("DELAY_ERROR", e.getMessage());
//                    }
//                    try {
//                        new SendOrder(order.getPartner_ref(), Integer.parseInt(order.getUser_id()),
//                                Integer.parseInt(order.getSales_id()), Integer.parseInt(order.getPartner_id()),
//                                order.getReference(), "draft", order.getTotal_amount(), order.getTotal_item(),
//                                order.getTotal_sku(), order.getNama_toko(), max, order.getBrand_produk(),
//                                order.getNote(), order.getVisit_ref()).execute();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("ERROR_SEND_ORDER", e.getMessage());
//                        Log.e("ERROR_SEND_ORDER2", e.getLocalizedMessage());
//                        Log.e("ERROR_SEND_ORDER3", e.toString());
//                    }
                    return json;
                } else {
                    Log.e("Response_Code4", String.valueOf(responseCode));
                    Log.e("Response_Message4", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String4", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return json;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("DELAY_ERROR", e.getMessage());
            }
            try {
                int user_id = Integer.parseInt(userid);
                cekVisit = (CekVisitID) new  CekVisitID("http://10.3.181.177:3000/visit?user_id=eq." + userid,
                        partnerref, userid, salesid, partnerid, visittime, outtime, loclong, loclat, ref).execute();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR_VISIT_ID", e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CekVisitID extends AsyncTask<String, Void, String> {
        private final String url;
        private final String partnerref;
        private final String userid;
        private final String salesid;
        private final String partnerid;
        private final String visittime;
        private final String outtime;
        private final String loclat;
        private final String loclong;
        private final String ref;
        int max;
        boolean visID = false;
        String alasan, selesai;

        CekVisitID(String url, String partnerref, String userid, String salesid, String partnerid,
                   String visittime, String outtime, String loclong, String loclat, String ref){
            this.url = url;
            this.partnerref = partnerref;
            this.partnerid = partnerid;
            this.userid = userid;
            this.salesid = salesid;
            this.visittime = visittime;
            this.outtime = outtime;
            this.loclong = loclong;
            this.loclat = loclat;
            this.ref = ref;
        }

        @Override
        protected String doInBackground(String... strings) {
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = simpledateformat.format(calander.getTime());

            String url = "http://10.3.181.177:3000/visit?user_id=eq." + userid + "&&write_date=gte." + currentDate +"&&order=id.desc";
            Log.e("URL_VISIT_ID", url);

            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            max = 0;
                            try {
                                jo = response.getJSONObject(0);
                                max = jo.getInt("id");
//                                for (int i = 0; i < response.length(); i++) {
//                                    jo = response.getJSONObject(i);
//                                    int id = jo.getInt("id");
//                                    if (id > max) {
////                                        Log.e("visit_id_"+i, ""+id);
//                                        max = id;
//                                    }
//                                }

                                editor.putInt("id", max);
                                editor.commit();
                                Log.e("id", "ID Visit: " + max);
                                Log.e("id2", "ID Visit2: " + pref.getInt("id", 0));
                                visID =true;

                            } catch (JSONException e) {
                                Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR 1", "Error :" + anError.toString() + " - " + anError.getErrorBody());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            return "NICE";
        }

        @Override
        protected void onPostExecute(String s) {
//            if (visID){
            cekVisit.cancel(true);
            DatabaseStoreOrder dbOrder = new DatabaseStoreOrder(ListProdukDipesanActivity.this);
            String reference = pref.getString("ref", "");
            Log.e("Reference", reference);
            StoreOrderList order = dbOrder.getProduk(reference);

            Log.e("DO", order.getPartner_id() + " - " + order.getUser_id() + " - " +
                    order.getSales_id() + " - " + order.getPartner_id() + " - " + order.getReference()
                    + " - " + order.getTotal_amount() + " - " + order.getTotal_item() + " - " + order.getTotal_sku()
                    + " - " + order.getNama_toko() + " - " + order.getBrand_produk() + " - " + order.getNote());
            int do_id = pref.getInt("id", max);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("DELAY_ERROR", e.getMessage());
            }
            try {
                new SendOrder(order.getPartner_ref(), Integer.parseInt(order.getUser_id()),
                        Integer.parseInt(order.getSales_id()), Integer.parseInt(order.getPartner_id()),
                        order.getReference(), "draft", order.getTotal_amount(), order.getTotal_item(),
                        order.getTotal_sku(), order.getNama_toko(), max, order.getBrand_produk(),
                        order.getNote(), order.getVisit_ref()).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR_SEND_ORDER", e.getMessage());
                Log.e("ERROR_SEND_ORDER2", e.getLocalizedMessage());
                Log.e("ERROR_SEND_ORDER3", e.toString());
            }

//            }
//            else {
//                Log.e("ULANG", "Cek visit ID lagi");
//                try {
//                    try {
//                        cekVisit = (CekVisitID) new  CekVisitID("http://10.3.181.177:3000/visit?user_id=eq." + userid,
//                                partnerref, userid, salesid, partnerid, visittime, outtime, loclong, loclat, ref).execute();
//                    } catch (Exception e) {
//                        AlertDialog.Builder dialog = new AlertDialog.Builder(ListProdukDipesanActivity.this);
//                        dialog.setTitle("PENGIRIMAN GAGAL!");
//                        dialog.setMessage("Koneksi internet terdeteksi namun tidak memadai untuk mengirim data orderan...\n Pastikan anda terhubung dengan koneksi internet yang kencang...");
//                        dialog.setCancelable(true);
//                        dialog.show();
//                    }
//                } catch (Exception e){
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(ListProdukDipesanActivity.this);
//                    dialog.setTitle("PENGIRIMAN GAGAL!");
//                    dialog.setMessage("Koneksi internet terdeteksi namun tidak memadai untuk mengirim data orderan...\n Pastikan anda terhubung dengan koneksi internet yang kencang...");
//                    dialog.setCancelable(true);
//                    dialog.show();
//                }
//            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SendOrder extends AsyncTask<String, Void, String> {

        private final String partnerref;
        private final int userid;
        private final int salesid;
        private final int partnerid;
        private final String reference;
        private final String partner_name;
        private final String brand;
        private final int visitid;
        private final int total_amount;
        private final int total_qty;
        private final int total_sku;
        private final String notes;
        private final String visit_ref;
        int max;
        StringBuffer sb = new StringBuffer();

        SendOrder(String partnerref, int userid, int salesid, int partnerid, String reference,
                  String state, int total_amount, int total_qty, int total_sku, String partner_name,
                  int visitid, String brand, String notes, String visit_ref) {
            this.partnerref = partnerref;
            this.userid = userid;
            this.salesid = salesid;
            this.partnerid = partnerid;
            this.total_amount = total_amount;
            this.total_qty = total_qty;
            this.reference = reference;
            this.total_sku = total_sku;
            this.partner_name = partner_name;
            this.visitid = visitid;
            this.brand = brand;
            this.notes = notes;
            this.visit_ref = visit_ref;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING ORDER DATA");
            String json = "";
            String link = "http://10.3.181.177:3000/delivery_order";
//            String visit_ref = pref.getString("visit_ref", "null");

            try {
                URL url2 = new URL("http://10.3.181.177:3000/delivery_order");
                JSONObject obj = new JSONObject();
                boolean ec = pref.getBoolean("ec", true);
//                obj.put("id", id);
                obj.put("partner_ref", partnerref);
                obj.put("partner_name", partner_name);
                obj.put("create_uid", userid);
                obj.put("partner_id", partnerid);
                obj.put("sales_id", salesid);
                obj.put("reference", reference);
                obj.put("state", "draft");
                obj.put("total_sku", total_sku);

                obj.put("total_amount", total_amount);
                obj.put("total_qty", total_qty);

                obj.put("note", notes);
                obj.put("visit_id", visitid);
                obj.put("brand", brand);
                obj.put("visit_name", visit_ref);
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

                    Calendar calander = Calendar.getInstance();
                    SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = simpledateformat.format(calander.getTime());

                    String url = "http://10.3.181.177:3000/delivery_order?create_uid=eq." + userid + "&&write_date=gte." + currentDate;
                    Log.e("URL_POST_DO", url);
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
//                                        Log.e("do_id_" + i, "" + id);
                                                max = id;
                                            }
                                        }

                                        Log.e("id", "ID Visit: " + max);
                                        editor.putInt("doid", max);
                                        editor.commit();
                                        Log.e("id2", "ID Visit2: " + pref.getInt("doid", 0));

                                        DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(ListProdukDipesanActivity.this);

                                        ArrayList<OrderedProduct> order = dbOrder.getAllProdukToko(reference);
                                        for (int i = 0; i < order.size(); i++) {
                                            Log.e("PRODUCTS_" + i, order.get(i).getKode_odoo() + " - " +
                                                    order.get(i).getNama_produk() + " - " + order.get(i).getStock_produk()
                                                    + " - " + order.get(i).getFinalorder_produk());
                                        }
                                        int do_id = pref.getInt("doid", max);
                                        Log.e("do_id", "" + do_id + " - " + max);
                                        try {
                                            Log.e("SEND_DO_LINES", "LALALALA");
                                            new OrderLines(max, order.size(), order).execute();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("ERROR_SEND_ORDER", e.getMessage());
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
                                    Log.e("PARSING ERROR 2", "Error :" + anError.toString() + " - " + anError.getErrorBody());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                    return json;
                } else {
                    Log.e("Response_Code5", String.valueOf(responseCode));
                    Log.e("Response_Message5", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String5", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error");
                return "Connection Error";
            }


        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("DELAY_ERROR", e.getMessage());
            }
            Calendar calander = Calendar.getInstance();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = simpledateformat.format(calander.getTime());
            try {
                ambilDOID = new AmbilDOID("http://10.3.181.177:3000/delivery_order?create_uid=eq." + userid + "&&write_date=gte." + currentDate, brand, reference);
                ambilDOID.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e){
                e.printStackTrace();
                Log.e("ErrorAmbilDO", e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AmbilDOID extends AsyncTask<String, Void, String> {

        private final String url;
        private final String brand;
        private final String reference;
        StringBuffer sb = new StringBuffer();

        AmbilDOID(String url, String brand, String reference) {
            this.url = url;
            this.brand = brand;
            this.reference = reference;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "FETCHING DO ID FROM " + brand);

            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            maximumID = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");
                                    if (id > maximumID) {
//                                        Log.e("do_id_" + i, "" + id);
                                        maximumID = id;
                                    }
                                }

                                Log.e("id", "ID Visit: " + maximumID);
                                editor.putInt("doid", maximumID);
                                editor.commit();
                                Log.e("id2", "ID Visit2: " + pref.getInt("doid", 0));
                                check = true;

                            } catch (JSONException e) {
                                Log.e("CANT PARSE JSON", e.getMessage());
//                                Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("PARSING ERROR 2", "Error :" + anError.toString() + " - " + anError.getErrorBody());
//                            Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            return url;

        }

        @Override
        protected void onPostExecute(String s) {


            if (check) {
                ambilDOID.cancel(true);
                DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(ListProdukDipesanActivity.this);

                ArrayList<OrderedProduct> order = dbOrder.getAllProdukToko(reference);
                for (int i = 0; i < order.size(); i++) {
                    Log.e("PRODUCTS_" + i, order.get(i).getKode_odoo() + " - " +
                            order.get(i).getNama_produk() + " - " + order.get(i).getStock_produk()
                            + " - " + order.get(i).getFinalorder_produk());
                }
                int do_id = pref.getInt("doid", maximumID);
                Log.e("do_id", "" + do_id + " - " + maximumID);
                try {
                    Log.e("SEND_DO_LINES", "LALALALA");
                    new OrderLines(maximumID, order.size(), order).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR_SEND_ORDER", e.getMessage());
                }

            } else {
                ambilDOID = new AmbilDOID(url, brand, reference);
                ambilDOID.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            super.onPostExecute(s);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OrderLines extends AsyncTask<String, Void, String> {

        private final int do_id;
        private final int size;
        private final ArrayList<OrderedProduct> oplist;
        StringBuffer sb = new StringBuffer();

        OrderLines(int do_id, int size, ArrayList<OrderedProduct> oplist) {
            this.do_id = do_id;
            this.size = size;
            this.oplist = oplist;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING PRODUCTS DATA");
            String json = "";

            try {
                URL url = new URL("http://10.3.181.177:3000/delivery_order_line");

                ArrayList<JSONObject> jo = new ArrayList<JSONObject>();
                StringBuilder loop = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("do_id", do_id);
                    obj.put("product_id", oplist.get(i).getId_produk());
                    obj.put("qty_final", oplist.get(i).getFinalorder_produk());

                    obj.put("qty_stock", oplist.get(i).getStock_produk());
                    obj.put("brand", oplist.get(i).getBrand_produk());
                    obj.put("unit_price", oplist.get(i).getHarga_produk());
                    obj.put("default_code", oplist.get(i).getKode_odoo());
                    obj.put("category_id", oplist.get(i).getKategori_produk());
                    obj.put("qty_order", oplist.get(i).getSgtorder_produk());
                    obj.put("direct", false);
                    obj.put("do_name", oplist.get(i).getReference());
                    obj.put("qty_do_nbm", 0);
                    jo.add(obj);
                    loop.append(obj.toString());
                    if (i < size - 1) {
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
                Log.e("PRODUK", "[" + loop.toString() + "]");

                writer.write("[" + loop.toString() + "]");
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
                    Log.e("Response_Code6", String.valueOf(responseCode));
                    Log.e("Response_Message6", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String6", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error: " + e.getMessage());
                return "Connection Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Toast.makeText(ListProdukDipesanActivity.this, "Produk-produk sudah terkirim ulang", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder popup = new AlertDialog.Builder(ListProdukDipesanActivity.this);
            popup.setTitle("BERHASIL");
            popup.setMessage("Pengiriman ulang produk-produk telah berhasil..\nTenang! Jadinya tetap 1 faktur kok!");
            popup.setCancelable(false);
            popup.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ListProdukDipesanActivity.this, ListTokoOrderActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            popup.show();
            Log.e("Terkirim_ulang", oplist.get(0).getReference());
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
        } catch (Exception ignored) {
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
                return 1;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ListProdukDipesanActivity.this, ListTokoOrderActivity.class);
        startActivity(intent);
    }
}
