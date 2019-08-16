package com.example.sfmtesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.tabs.TabLayout;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class BarangDOActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;
    ArrayList<barangDO> spacecrafts = new ArrayList<barangDO>();
    ListViewAdapter adapter;
    ListView myListView;


    public class barangDO {
        /*
        INSTANCE FIELDS
        */
        private int id;
        private Date tanggal;
        private String reference;
        private String brand;
        /*
        GETTERS AND SETTERS
        */


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Date getTanggal() {
            return tanggal;
        }

        public void setTanggal(Date tanggal) {
            this.tanggal = tanggal;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_do);
        myListView = findViewById(R.id.list_brand_DO);

        TabLayout tabLayout = findViewById(R.id.tabbrandDO);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
        String customer = prefToko.getString("partner_name", "");
        tabLayout.addTab(tabLayout.newTab().setText("BARANG DO : "+customer));

        ArrayList<barangDO> downloadedData = new ArrayList<barangDO>();
//        adapter = new ListViewAdapter(downloadedData);
//        myListView.setAdapter(adapter);

        final String sales_id = pref.getString("sales_id", "");
        final String partner_ref = prefToko.getString("ref", "0");
        Log.e("sales id", sales_id);
        String url = "http://10.3.181.177:3000/delivery_order?partner_ref=eq." + partner_ref;
        Log.e("url", url);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        barangDO s;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jo = response.getJSONObject(i);
                                int id = jo.getInt("id");
                                String name = jo.getString("write_date");
                                String propellant = jo.getString("reference");
                                String salesid = jo.getString("brand");
                                Date date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSZ").parse(name);
                                s = new barangDO();
                                s.setId(id);
                                s.setTanggal(date);
                                s.setReference(propellant);
                                s.setBrand(salesid);

                                downloadedData.add(s);
                                Log.e("hasil ", s.getId() + " - " + s.getReference()+ " - " +s.getTanggal() + " - " +s.getBrand());
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Log.e("errors ", e.getMessage());
                            Toast.makeText(getBaseContext(), "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
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
            adapter = new ListViewAdapter(BarangDOActivity.this, downloadedData);
            myListView.setAdapter(adapter);
//            Log.e("sizerrr",""+adapter.getCount());
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }


        Log.e("size ", ""+ downloadedData.size());
        for (int i = 0; i < downloadedData.size(); i++){
            Log.e("hasil_" + i, downloadedData.get(i).getReference());
        }
    }

    static class ViewHolder implements Serializable {
        TextView txtTanggal;
        TextView txtReference;
        TextView txtBrand;
        CardView cardView;
    }

    public class ListViewAdapter extends BaseAdapter {
        public ArrayList<barangDO> currentList;
        Context c;
        ArrayList<barangDO> spacecrafts;

        public ListViewAdapter(Context c, ArrayList<barangDO> spacecrafts) {
            this.spacecrafts = spacecrafts;
            this.c = c;
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
//                view = View.inflate(BarangDOActivity.this, R.layout.model_row_brand_do, viewGroup);
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.model_row_brand_do, viewGroup, false);
                viewHolder.txtTanggal = view.findViewById(R.id.toko_brandDO_tanggal);
                viewHolder.txtReference = view.findViewById(R.id.toko_brandDO_ref);
                viewHolder.txtBrand = view.findViewById(R.id.toko_brandDO);
                viewHolder.cardView = view.findViewById(R.id.cvBrandDO);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                viewHolder.txtTanggal.setText("");
                viewHolder.txtReference.setText("");
                viewHolder.txtBrand.setText("");
                viewHolder.cardView.setBackgroundColor(Color.WHITE);
            }

//            final ConstraintLayout constraintLayout = view.findViewById(R.id.layoutdalamrute);
            final barangDO s = (barangDO) getItem(i);
            Locale localeID = new Locale("in", "ID");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", localeID);
            Log.e("cek_"+i, s.getReference());
            viewHolder.txtTanggal.setText(sdf.format(s.getTanggal()));
            viewHolder.txtReference.setText(s.getReference());
            viewHolder.txtBrand.setText(s.getBrand());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BarangDOActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle(sdf.format(s.getTanggal()));
                    dialog.setMessage("Apakah anda akan melihat produk-produk Brand "+s.getBrand()+" yang diorder toko ini?");
                    dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt("id", s.getId());
                            editor.commit();
                            Intent intent = new Intent(BarangDOActivity.this, ListBarangDOActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            return view;
        }

        public void setSpacecrafts(ArrayList<barangDO> filteredSpacecrafts) {
            this.spacecrafts = filteredSpacecrafts;
        }

        public void refresh() {
            notifyDataSetChanged();
        }

    }
}