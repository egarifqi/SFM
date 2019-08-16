package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ListBarangDOActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;
    ListView myListView;
    ListViewAdapter adapter;
    ArrayList<listbarangDO> spacecrafts = new ArrayList<>();

    public class listbarangDO {
        /*
        INSTANCE FIELDS
        */
        private int id;
        private String name;
        private int doid;
        private String price;
        private String qtyfinal;
        private String kodeodoo;
        private String date;
        private String brand;
        private String qtyDO;
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

        public String getQtyfinal() {
            return qtyfinal;
        }

        public void setQtyfinal(String qtyfinal) {
            this.qtyfinal = qtyfinal;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getQtyDO() {
            return qtyDO;
        }

        public void setQtyDO(String qtyDO) {
            this.qtyDO = qtyDO;
        }

        public int getDoid() {
            return doid;
        }

        public void setDoid(int doid) {
            this.doid = doid;
        }

        public String getKodeodoo() {return kodeodoo;}

        public void setKodeodoo(String kodeodoo){this.kodeodoo = kodeodoo;}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang_do);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
        myListView = findViewById(R.id.list_produkDO);

        ArrayList<listbarangDO> listOrder = new ArrayList<>();
//        for (int i = 0; i < listOrder.size(); i++) {
//            Log.e("Database_" + i, listOrder.get(i).nama_produk);
//        }
        final int do_id = pref.getInt("id", 0);
        String url = "http://10.3.181.177:3000/v_delivery_order_line?do_id=eq." + do_id;
        Log.e("url barang", url);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        listbarangDO s;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jo = response.getJSONObject(i);
                                int do_id = jo.getInt("do_id");
                                String kode = jo.getString("default_code");
                                String name = jo.getString("product_name");
                                String ordered = jo.getString("qty_final");
                                String deliv = jo.getString("qty_do_nbm");
                                s = new listbarangDO();
                                s.setDoid(do_id);
                                s.setKodeodoo(kode);
                                s.setName(name);
                                s.setQtyfinal(ordered);
                                if (deliv.equals("null")){
                                    s.setQtyDO("0");
                                }else {
                                    s.setQtyDO(deliv);
                                }

                                listOrder.add(s);

//                                downloadedData.add(s);
//                                Log.e("hasil ", s.getKodeodoo() + " - " + s.getName()+ " - " +s.getQtyfinal() + " - " +s.getQtyDO());
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Log.e("errors ", e.getMessage());
                            Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    //ERROR
                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Log.e("error ", anError.getMessage());
                        Toast.makeText(getBaseContext(), "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        try {
            adapter = new ListViewAdapter(ListBarangDOActivity.this, listOrder);
            myListView.setAdapter(adapter);
            Log.e("sizerrr",""+adapter.getCount());
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
        Log.e("size ", ""+ listOrder.size());
        for (int i = 0; i < listOrder.size(); i++){
            Log.e("hasil_" + i, listOrder.get(i).getName());
        }
//        adapter = new ListViewAdapter(ListBarangDOActivity.this, listOrder);
//        myListView.setAdapter(adapter);
    }

    public class ViewHolderOrder implements Serializable {
        TextView product_code;
        TextView product_name;
        TextView product_qty_final;
        TextView product_qty_do;
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

    public class ListViewAdapter extends BaseAdapter implements Serializable {
        Context c;
        ArrayList<listbarangDO> orderedProducts;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<listbarangDO> orderedProducts) {
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
                view = LayoutInflater.from(c).inflate(R.layout.model_row_produk_do, viewGroup, false);

                holder.product_code = (TextView) view.findViewById(R.id.codecontain);
                holder.product_name = (TextView) view.findViewById(R.id.namecontain);
                holder.product_qty_final = (TextView) view.findViewById(R.id.stockcontain);
                holder.product_qty_do = (TextView) view.findViewById(R.id.qtycontain);
                view.setTag(holder);
            } else {
                holder = (ViewHolderOrder) view.getTag();
                holder.product_name.setText("");
                holder.product_code.setText("");
                holder.product_qty_final.setText("");
                holder.product_qty_do.setText("");
            }
            final listbarangDO op = (listbarangDO) this.getItem(i);

            holder.product_name.setText(op.getName());
            holder.product_code.setText(op.getKodeodoo());
            holder.product_qty_final.setText(op.getQtyfinal());
            holder.product_qty_do.setText(op.getQtyDO());

            return view;
        }

        public void refresh() {
            notifyDataSetChanged();
        }
    }

//    public class JSONDownloader {
//        //SAVE/RETRIEVE URLS
//        //INSTANCE FIELDS
//        private final Context c;
//
//        public JSONDownloader(Context c) {
//            this.c = c;
//        }
//
//        /*
//        Fetch JSON Data
//        */
//        public ArrayList<BarangDOActivity.barangDO> retrieve(final ListView mListView, final ProgressBar myProgressBar) {
//            final ArrayList<BarangDOActivity.barangDO> downloadedData = new ArrayList<>();
//            myProgressBar.setIndeterminate(true);
//            myProgressBar.setVisibility(View.VISIBLE);
//            pref = getSharedPreferences("MyPref", 0);
//            editor = pref.edit();
//            final String sales_id = pref.getString("sales_id", "");
//            Log.e("sales id", sales_id);
//            String url = "http://10.3.181.177:3000/v_delivery_order_line";
//            Log.e("url dalam",url);
//            AndroidNetworking.get(url)
//                    .setPriority(Priority.HIGH)
//                    .build()
//                    .getAsJSONArray(new JSONArrayRequestListener() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            JSONObject jo;
//                            BarangDOActivity.barangDO s;
//                            try {
//                                for (int i = 0; i < response.length(); i++) {
//                                    jo = response.getJSONObject(i);
//                                    int id = jo.getInt("id");
//                                    String name = jo.getString("product_name");
//                                    int do_id = jo.getInt("do_id");
//                                    String qtyFinal = jo.getString("qty_final");
//                                    String date = jo.getString("create_daate");
//                                    String brand = jo.getString("brand");
//                                    int kodeodoo = jo.getInt("default_code");
//                                    String price = jo.getString("unit_price");
//                                    String qty_do = jo.getString("qty_do_nbm");
//                                    s = new BarangDOActivity.barangDO();
//                                    s.setId(id);
//                                    s.setName(name);
//                                    s.setDoid(do_id);
//                                    s.setQtyfinal(qtyFinal);
//                                    s.setDate(date);
//                                    s.setBrand(brand);
//                                    s.setKodeodoo(kodeodoo);
//                                    s.setPrice(price);
//                                    s.setQtyDO(qty_do);
//
//                                    downloadedData.add(s);
//                                }
//                                myProgressBar.setVisibility(View.GONE);
//                            } catch (JSONException e) {
//                                myProgressBar.setVisibility(View.GONE);
//                                BarangDOActivity.barangDO sl;
////                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                Log.e("CANT PARSE JSON", e.getMessage());
//                                for (com.example.sfmtesting.TokoDalamRute tdr : listtoko){
//                                    sl = new BarangDOActivity.barangDO();
//                                    sl.setId(tdr.getId());
//                                    sl.setName(tdr.getNama());
//                                    sl.setDoid(do_id);
//                                    sl.setQtyfinal(qtyFinal);
//                                    sl.setDate(date);
//                                    sl.setBrand(brand);
//                                    sl.setKodeodoo(kodeodoo);
//                                    sl.setPrice(price);
//                                    sl.setQtyDO(qty_do);
//                                    downloadedData.add(sl);
//                                }
//
//                            }
//                        }
//
//                        //ERROR
//                        @Override
//                        public void onError(ANError anError) {
//                            anError.printStackTrace();
//                            myProgressBar.setVisibility(View.GONE);
////                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
//                            Log.e("Error", "Error : " + anError.getMessage());
//                            BarangDOActivity.barangDO sl;
//                            for (com.example.sfmtesting.TokoDalamRute tdr : listtoko){
//                                sl = new BarangDOActivity.barangDO();
//                                sl.setId(tdr.getId());
//                                sl.setStatus(tdr.getStatus());
//                                sl.setName(tdr.getKode());
//                                sl.setPropellant(tdr.getNama());
//                                sl.setSalesid(tdr.getSalesid());
//                                sl.setPartnerId(tdr.getPartnerId());
//                                sl.setFrekuensi(tdr.getFrekuensi());
//                                downloadedData.add(sl);
//                            }
//
//                        }
//                    });
////            StatusSR.callPlan = downloadedData.size();
//            return downloadedData;
//        }
//    }


}
