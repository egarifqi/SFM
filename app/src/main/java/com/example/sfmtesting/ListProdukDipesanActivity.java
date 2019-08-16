package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class ListProdukDipesanActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listprodukdipesan);
        pref = getApplicationContext().getSharedPreferences("SystemPref", 0);
        editor = pref.edit();
        int do_id = pref.getInt("do_id", 0);
        DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(ListProdukDipesanActivity.this);
        ArrayList<OrderedProduct> listOrder = dbOrder.getAllProdukToko(do_id);
        for (int i = 0; i < listOrder.size(); i++) {
            Log.e("Database_" + i, listOrder.get(i).nama_produk);
        }
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
                        if (networkClass == 1)
                            cek = false;
                        else if (networkClass == 2)
                            cek = true;
                        else if (networkClass == 3)
                            cek = true;
                        else
                            cek = false;
                    } else
                        cek = false;

                    if (cek) {
                        new DeleteLines(do_id).execute();
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
        ConstraintLayout product_layout;
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
                holder.product_layout = (ConstraintLayout) view.findViewById(R.id.header_list);
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
            if (i % 2 == 0)
            {
                holder.product_layout.setBackgroundColor(Color.rgb(245, 245, 245));
            } else {
                holder.product_layout.setBackgroundColor(Color.rgb(255, 255, 255));
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

    private class DeleteLines extends AsyncTask<String, Void, String>{

        private final int do_id;
        StringBuffer sb = new StringBuffer();

        DeleteLines(int do_id){
            this.do_id = do_id;
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
                URL url = new URL("https://sfa-api.pti-cosmetics.com/delivery_order_line?do_id=eq."+do_id);
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
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                }
            } catch (Exception e){
                Log.e("Error", "Connection Error: " +e.getMessage());
                return "Connection Error";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            DatabaseOrderHandler dbOrder = new DatabaseOrderHandler(ListProdukDipesanActivity.this);
            ArrayList<OrderedProduct> listOrder = dbOrder.getAllProdukToko(do_id);
            for (int i = 0; i < listOrder.size(); i++){
                new OrderLines(do_id, listOrder.get(i).getId_produk(), listOrder.get(i).getStock_produk(),
                        listOrder.get(i).getFinalorder_produk(), listOrder.get(i).getBrand_produk(),
                        listOrder.get(i).getKode_odoo(), listOrder.get(i).getKategori_produk(),
                        Integer.parseInt(listOrder.get(i).getHarga_produk()), listOrder.get(i).getSgtorder_produk())
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OrderLines extends AsyncTask<String, Void, String> {

        private final int do_id;
        private final int product_id;
        private final int qty_stock;
        private final int qty_order;
        //        private final String date;
        private final String brand;
        private final String default_code;
        private final String category;
        private final int unit_price;
        private final int sgt_order;
        StringBuffer sb = new StringBuffer();

        OrderLines(int do_id, int product_id, int qty_stock, int qty_order, String brand,
                   String default_code, String category, int unit_price, int sgt_order) {
            this.do_id = do_id;
            this.product_id = product_id;
            this.qty_stock = qty_stock;
            this.qty_order = qty_order;
            this.default_code = default_code;
            this.category = category;
//            this.date = date;
            this.brand = brand;
            this.unit_price = unit_price;
            this.sgt_order = sgt_order;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.e("KIRIM DATA", "SENDING PRODUCTS DATA");
            String json = "";

            try {
                URL url = new URL("https://sfa-api.pti-cosmetics.com/delivery_order_line");

                JSONObject obj = new JSONObject();


                obj.put("do_id", do_id);
                obj.put("product_id", product_id);

                obj.put("qty_final", qty_order);

                obj.put("qty_stock", qty_stock);
                obj.put("brand", brand);
                obj.put("unit_price", unit_price);
                obj.put("default_code", default_code);
                obj.put("category_id", category);
                obj.put("qty_order", sgt_order);
                obj.put("direct", false);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Prefer", "resolution=merge-duplicates");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                Log.e("PRODUK", obj.toString());

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
                    Log.e("Response_Code3", String.valueOf(responseCode));
                    Log.e("Response_Message3", String.valueOf(conn.getResponseMessage()));
                    Log.e("Returned_String3", sb.toString());
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                Log.e("Error", "Connection Error: " +e.getMessage());
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
            Log.e("Terkirim_ulang", default_code);
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
}
