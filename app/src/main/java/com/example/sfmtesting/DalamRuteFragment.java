package com.example.sfmtesting;

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
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class DalamRuteFragment extends Fragment implements LocationListener {

    SharedPreferences pref;
    SharedPreferences prefToko;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorToko;
    LocationManager locationManager;
    ArrayList<SpacecraftLuar> spacecrafts = new ArrayList<>();
    ListView myListView;
    ListViewAdapter adapter;
    Boolean online;

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(getActivity(), "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
        Log.e("Location Changed", "Lat : "+location.getLatitude() +", Long : "+location.getLongitude());
        editor.putString("latitude", String.valueOf(location.getLatitude()));
        editor.putString("longitude", String.valueOf(location.getLongitude()));
        editor.commit();

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        } catch (Exception e) {
            Log.e("Geocoder Error", e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
        Log.e("I&GPS", "Please enable GPS and Internet");
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dalam_rute, container, false);
        myListView = view.findViewById(R.id.mListtokodalam);
        final ProgressBar myProgressBar = view.findViewById(R.id.myProgressBartokodalam);
        SearchView mySearchView = view.findViewById(R.id.mySearchViewtokodalam);


        mySearchView.setIconified(true);
        mySearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        Log.e("Status Kunjungan", "Dalam Rute:\nCall : " + com.example.sfmtesting.StatusSR.dalamRute + "\nEC : "
                + com.example.sfmtesting.StatusSR.ECdalamRute + "\n\nLuar Rute:\nCall : " + com.example.sfmtesting.StatusSR.luarRute + "\nEC : "
                + com.example.sfmtesting.StatusSR.ECluarRute);

        spacecrafts = new JSONDownloader(getActivity()).retrieve(myListView, myProgressBar);
        adapter = new ListViewAdapter(getActivity(), spacecrafts);
        myListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        SharedPreferences pref;
        pref = getActivity().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getActivity().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();

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

    /*
     Our data object
     */
    static class FilterHelper extends Filter {
        ArrayList<SpacecraftLuar> currentList;
        ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<SpacecraftLuar> currentList, ListViewAdapter adapter, Context c) {
            this.currentList = currentList;
            this.adapter = adapter;
            this.c = c;
        }

        /*-
        - Perform actual filtering.
        */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
//CHANGE TO UPPER
                constraint = constraint.toString().toUpperCase();
//HOLD FILTERS WE FIND
                ArrayList<SpacecraftLuar> foundFilters = new ArrayList<>();
                SpacecraftLuar spacecraft = null;
//ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    spacecraft = currentList.get(i);
//SEARCH
                    if (spacecraft.getPropellant().toUpperCase().contains(constraint)) {
//ADD IF FOUND
                        foundFilters.add(spacecraft);
                    }
                }
//SET RESULTS TO FILTER LIST
                filterResults.count = foundFilters.size();
                filterResults.values = foundFilters;
            } else {
//NO ITEM FOUND.LIST REMAINS INTACT
                filterResults.count = currentList.size();
                filterResults.values = currentList;
            }
//RETURN RESULTS
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.setSpacecrafts((ArrayList<SpacecraftLuar>) filterResults.values);
            adapter.refresh();
        }
    }

    static class ViewHolder implements Serializable {
        TextView txtName;
        TextView txtPropellant;
        CardView cardView;
        TextView txtStatus;
        TextView txtFrekuensi;
    }

    public class SpacecraftLuar {
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
        private String namafrekuensi;
        private String status;
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

        public String getnamaFrekuensi() {
            return namafrekuensi;
        }

        public void setnamaFrekuensi(String namafrekuensi) {
            this.namafrekuensi = namafrekuensi;
        }

        public int getTechnologyExists() {
            return technologyExists;
        }

        public void setTechnologyExists(int technologyExists) {
            this.technologyExists = technologyExists;
        }

        public String getStatus() {return status;}

        public void setStatus(String status){this.status = status;}

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

    public class ListViewAdapter extends BaseAdapter implements Filterable {

        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0);
        SharedPreferences prefToko = getActivity().getSharedPreferences("TokoPref", 0);
        public ArrayList<SpacecraftLuar> currentList;
        StringBuffer sb = new StringBuffer();
        Context c;
        ArrayList<SpacecraftLuar> spacecrafts;
        FilterHelper filterHelper;
        Dialog dialog;
        private SendRequest mAuthTask = null;

        public ListViewAdapter(Context c, ArrayList<SpacecraftLuar> spacecrafts) {
            this.c = c;
            this.spacecrafts = spacecrafts;
            this.currentList = spacecrafts;
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
                view = LayoutInflater.from(c).inflate(R.layout.model_row_rute_dalam, viewGroup, false);
                viewHolder.txtName = view.findViewById(R.id.id_customer);
                viewHolder.txtPropellant = view.findViewById(R.id.nama_toko);
                viewHolder.cardView = view.findViewById(R.id.dalamrute);
                viewHolder.txtStatus = view.findViewById(R.id.status_toko);
                viewHolder.txtFrekuensi = view.findViewById(R.id.nama_frekuensi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
                viewHolder.txtName.setText("");
                viewHolder.txtPropellant.setText("");
                viewHolder.cardView.setBackgroundColor(Color.WHITE);
                viewHolder.txtStatus.setText("");
                viewHolder.txtPropellant.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtName.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtFrekuensi.setTypeface(null, Typeface.NORMAL);
            }

            final ConstraintLayout constraintLayout = view.findViewById(R.id.layoutdalamrute);
            final SpacecraftLuar s = (SpacecraftLuar) this.getItem(i);
            viewHolder.txtName.setText(s.getName());
            viewHolder.txtFrekuensi.setText(s.getnamaFrekuensi());
            viewHolder.txtPropellant.setText(s.getPropellant());

            if (s.getStatus().equals("0")){
                viewHolder.cardView.setBackgroundColor(Color.rgb(219, 93, 93));
                viewHolder.txtStatus.setText("TIDAK VISIT");
                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);
            } else if (s.getStatus().equals("1")){
                viewHolder.cardView.setBackgroundColor(Color.rgb(226, 222, 90));
                viewHolder.txtStatus.setText("TUNDA");
                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);
            } else if (s.getStatus().equals("2")){
                viewHolder.cardView.setBackgroundColor(Color.rgb(58, 139, 207));
                viewHolder.txtStatus.setText("VISIT NOT EC");
                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);
            } else if (s.getStatus().equals("3")){
                viewHolder.cardView.setBackgroundColor(Color.rgb(61, 168, 109));
                viewHolder.txtStatus.setText("VISIT EC");
                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);
            }


            if (com.example.sfmtesting.StatusToko.namatoko != null) {
                for (int k = 0; k < com.example.sfmtesting.StatusToko.namatoko.size(); k++) {
                    if (spacecrafts.get(i).getPropellant().equals(com.example.sfmtesting.StatusToko.namatoko.get(k))) {
                        if (!com.example.sfmtesting.StatusToko.statuskunjungan.isEmpty()) {
                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("0")) {
                                //NOT VISIT - merah
                                viewHolder.cardView.setBackgroundColor(Color.rgb(219, 93, 93));
                                viewHolder.txtStatus.setText("TIDAK VISIT");
                                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);

                            }

                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("1")) {
                                viewHolder.cardView.setBackgroundColor(Color.rgb(226, 222, 90));
                                viewHolder.txtStatus.setText("TUNDA");
                                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);


                            }
                            //VISIT NOT EC - biru
                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("2")) {
                                viewHolder.cardView.setBackgroundColor(Color.rgb(58, 139, 207));
                                viewHolder.txtStatus.setText("VISIT NOT EC");
                                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);


                            }

                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("3")) {
                                //VISIT EC hijau
                                viewHolder.cardView.setBackgroundColor(Color.rgb(61, 168, 109));
                                viewHolder.txtStatus.setText("VISIT EC");
                                viewHolder.txtStatus.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtName.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtPropellant.setTypeface(null, Typeface.BOLD);
                                viewHolder.txtFrekuensi.setTypeface(null, Typeface.BOLD);

                            }
                        }

                    }
                }
            }

            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getLocation();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.form_kunjungan, null);
//                    dialog.setView(dialogView);
                    dialog.setCancelable(true);
                    dialog.setTitle(s.getPropellant());
                    dialog.setMessage("Apakah anda akan mengunjungi toko " + s.getPropellant() + "?");

                    dialog.setNegativeButton("TIDAK VISIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(getActivity(), com.example.sfmtesting.ReasonNotVisitActivity.class);

                            if (com.example.sfmtesting.StatusToko.namatoko != null) {
                                for (int k = 0; k < com.example.sfmtesting.StatusToko.namatoko.size(); k++) {
                                    if (spacecrafts.get(i).getPropellant().equals(com.example.sfmtesting.StatusToko.namatoko.get(k))) {
                                        if (!com.example.sfmtesting.StatusToko.statuskunjungan.isEmpty()) {

                                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("3")) {
                                                //VISIT NOT EC hijau
                                                com.example.sfmtesting.StatusSR.totalEC -= 1;
                                                com.example.sfmtesting.StatusSR.totalCall -= 1;
                                            }
                                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("2")) {
                                                //VISIT EC hijau
                                                com.example.sfmtesting.StatusSR.totalNotEC -= 1;
                                                com.example.sfmtesting.StatusSR.totalCall -= 1;
                                            }
                                        }

                                    }
                                }
                            }


                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            editorToko.putString("ref", s.getName());
                            editorToko.putString("partner_name", s.getPropellant());
                            editorToko.putString("partner_id", s.getPartnerId());
                            editorToko.putString("freukensi_name", s.getnamaFrekuensi());
                            editorToko.putString("const", s.getFrekuensi());
                            editorToko.commit();

//                            com.example.sfmtesting.StatusToko.kodetoko.add(s.getName());
//                            com.example.sfmtesting.StatusToko.namatoko.add(s.getPropellant());
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = simpleDateFormat.format(calendar.getTime());
                            Log.e("waktu", currentTime);
                            com.example.sfmtesting.StatusToko.waktumulai.add(currentTime);

                            editorToko.putString("waktu_mulai", currentTime);

                            editorToko.commit();

                            String partner_ref = prefToko.getString("ref", "null");
                            String user_id = pref.getString("user_id", "null");
                            String sales_id = pref.getString("sales_id", "null");
                            String partner_id = prefToko.getString("partner_id", "null");
                            String date = pref.getString("write_date", "");
                            String waktu_datang = prefToko.getString("waktu_mulai", "");
                            String latitude = pref.getString("latitude", "");
                            String longitude = pref.getString("longitude", "");
                            Boolean inroute = true;
                            Boolean visit = false;
                            final String id = partner_ref + date + sales_id + com.example.sfmtesting.StatusSR.id_inc;
                            com.example.sfmtesting.StatusSR.id_inc += 1;

                            editorToko.putBoolean("visit", visit);
                            editorToko.commit();
                            mAuthTask = new SendRequest(partner_ref, user_id, sales_id, partner_id, date, waktu_datang, latitude, longitude, id, inroute);
                            mAuthTask.execute();

                        }
                    });

                    dialog.setPositiveButton("VISIT", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            if (com.example.sfmtesting.StatusToko.namatoko != null) {
                                for (int k = 0; k < com.example.sfmtesting.StatusToko.namatoko.size(); k++) {
                                    if (spacecrafts.get(i).getPropellant().equals(com.example.sfmtesting.StatusToko.namatoko.get(k))) {
                                        if (!com.example.sfmtesting.StatusToko.statuskunjungan.isEmpty()) {

                                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("3")) {
                                                //VISIT NOT EC hijau
                                                com.example.sfmtesting.StatusSR.totalEC -= 1;
                                                com.example.sfmtesting.StatusSR.totalCall -= 1;
                                                com.example.sfmtesting.StatusSR.dalamRute -=1;
                                                com.example.sfmtesting.StatusSR.ECdalamRute -=1;
                                            }
                                            if (com.example.sfmtesting.StatusToko.statuskunjungan.get(k).equals("2")) {
                                                //VISIT EC hijau
                                                com.example.sfmtesting.StatusSR.totalNotEC -= 1;
                                                com.example.sfmtesting.StatusSR.totalCall -= 1;
                                                com.example.sfmtesting.StatusSR.dalamRute -= 1;
                                            }
                                        }

                                    }
                                }
                            }

                            boolean dalamRute = true;
                            boolean luarRute = false;
                            com.example.sfmtesting.StatusToko.rute = true;

                            final ArrayList<Integer> al_id = new ArrayList<Integer>();

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            editorToko.putBoolean("dalamrute", dalamRute);
                            editorToko.putBoolean("luarrute", luarRute);
                            editorToko.putString("ref", s.getName());
                            editorToko.putString("partner_name", s.getPropellant());
                            editorToko.putString("partner_id", s.getPartnerId());
                            editorToko.putString("const", s.getFrekuensi());
                            editorToko.putString("alasan", "");
                            editorToko.commit();
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentTime = simpleDateFormat.format(calendar.getTime());
                            Log.e("waktu", currentTime);
                            com.example.sfmtesting.StatusToko.waktumulai.add(currentTime);

                            editorToko.putString("waktu_mulai", currentTime);
                            editorToko.putString("waktu_selesai", "");

                            editorToko.commit();

                            String partner_ref = prefToko.getString("ref", "null");
                            String user_id = pref.getString("user_id", "null");
                            String sales_id = pref.getString("sales_id", "null");
                            String partner_id = prefToko.getString("partner_id", "null");
                            String date = pref.getString("write_date", "");
                            String waktu_datang = prefToko.getString("waktu_mulai", "");
                            String latitude = pref.getString("latitude", "");
                            String longitude = pref.getString("longitude", "");
                            Boolean inroute = true;
                            boolean visit = true;
                            final String id = partner_ref + date + sales_id + com.example.sfmtesting.StatusSR.id_inc;
                            com.example.sfmtesting.StatusSR.id_inc += 1;

                            editorToko.putBoolean("visit", visit);
                            editorToko.commit();
                            mAuthTask = new SendRequest(partner_ref, user_id, sales_id, partner_id, date, waktu_datang, latitude, longitude, id, inroute);
                            mAuthTask.execute();

                        }
                    });

                    dialog.setNeutralButton("CEK BARANG DO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editorToko.putString("ref", s.getName());
                            editorToko.putString("partner_name", s.getPropellant());
                            editorToko.putString("partner_id", s.getPartnerId());
                            editorToko.putString("freukensi_name", s.getnamaFrekuensi());
                            editorToko.putString("const", s.getFrekuensi());
                            editorToko.commit();
                            Intent intent = new Intent(getActivity(), BarangDOActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            });
            return view;
        }

        public void setSpacecrafts(ArrayList<SpacecraftLuar> filteredSpacecrafts) {
            this.spacecrafts = filteredSpacecrafts;
        }

        @Override
        public Filter getFilter() {
            if (filterHelper == null) {
                filterHelper = new FilterHelper(currentList, this, c);
            }
            return filterHelper;
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        void getLocation() {
            try {
                locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
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
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

        @SuppressLint("StaticFieldLeak")
        private class SendRequest extends AsyncTask<String, Void, String> {

            private final String partnerref;
            private final String userid;
            private final String salesid;
            private final String partnerid;
            private final String todaydate;
            private final String visittime;
            private final String loclat;
            private final String loclong;
            private final String visitid;
            private final Boolean inroute;

            SendRequest(String partnerref, String userid, String salesid, String partnerid,
                        String todaydate, String visittime, String loclat, String loclong,
                        String visitid, Boolean inroute) {
                this.partnerref = partnerref;
                this.userid = userid;
                this.salesid = salesid;
                this.partnerid = partnerid;
                this.todaydate = todaydate;
                this.loclat = loclat;
                this.loclong = loclong;
                this.visitid = visitid;
                this.visittime = visittime;
                this.inroute = inroute;
            }


            @Override
            protected String doInBackground(String... strings) {
                String json = "";

                try {
                    URL url = new URL("http://10.3.181.177:3000/visit");
                    JSONObject obj = new JSONObject();
                    obj.put("partner_ref", partnerref);
                    obj.put("user_id", userid);
                    obj.put("partner_id", partnerid);
                    obj.put("sales_id", salesid);
                    obj.put("datang_time", visittime);
//                    obj.put("id", visitid);
                    obj.put("latitude", loclat);
                    obj.put("longitude", loclong);
                    obj.put("inroute", inroute);
                    obj.put("selesai_time", "");
                    obj.put("reason", "");
//                    obj.put("check")
//                    obj.put("date", todaydate);
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
                        online = true;
                        editorToko.putBoolean("online", online);
                        editorToko.commit();
                        return json;
                    } else {
                        Log.e("Response_Code", String.valueOf(responseCode));
                        Log.e("Response_Message", String.valueOf(conn.getResponseMessage()));
                        Log.e("Returned_String", sb.toString());
                        online = true;
                        editorToko.putBoolean("online", online);
                        editorToko.commit();
                        return "false : " + responseCode;
                    }
                } catch (Exception e) {
                    Log.e("Error", "Connection Error");
                    online = false;
                    editorToko.putBoolean("online", online);
                    editorToko.commit();
                    return "Connection Error";
                }

            }

            @Override
            protected void onPostExecute(String s) {
                String user_id = pref.getString("user_id", "null");
                String url = "http://10.3.181.177:3000/visit?user_id=eq." + user_id;
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
//                                        al_id.add(id);
                                        if (id > max) {
                                            max = id;
                                        }
                                    }
                                    editorToko.putInt("id", max);

                                    editorToko.commit();
                                    Log.e("id", "ID Visit: " + max);
                                    Log.e("id2", "ID Visit2: " + prefToko.getInt("id", 0));
//                                    Log.e("id3", "ID Visit3: " + al_id.get(al_id.size() - 1));
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

                boolean visit = prefToko.getBoolean("visit", true);
                if (visit){
                    Intent intent = new Intent(getActivity(), com.example.sfmtesting.TokoActivity.class);
                    startActivity(intent);
//                    dialog.dismiss();
                } else{
                    Intent intent = new Intent(getActivity(), com.example.sfmtesting.ReasonNotVisitActivity.class);

                    startActivity(intent);
//                    dialog.dismiss();
                }

            }
        }
    }

    /*
    Our HTTP Client
    */
    public class JSONDownloader {
        //SAVE/RETRIEVE URLS
        //INSTANCE FIELDS
        private final Context c;

        public JSONDownloader(Context c) {
            this.c = c;
        }

        /*
        Fetch JSON Data
        */
        public ArrayList<SpacecraftLuar> retrieve(final ListView mListView, final ProgressBar myProgressBar) {
            final ArrayList<SpacecraftLuar> downloadedData = new ArrayList<>();
            final com.example.sfmtesting.DatabaseTokoHandler dbtoko = new com.example.sfmtesting.DatabaseTokoHandler(getContext());
            final ArrayList<com.example.sfmtesting.TokoDalamRute> listtoko = dbtoko.getAllToko();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);
            pref = getActivity().getSharedPreferences("MyPref", 0);
            editor = pref.edit();
            final String sales_id = pref.getString("sales_id", "");
            Log.e("sales id", sales_id);
            String url = "http://10.3.181.177:3000/v_partner_inroute?dc_name=ilike.*DC%Jakarta&sales_id=eq." + sales_id;
            Log.e("url dalam",url);
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            SpacecraftLuar s;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");
                                    String name = jo.getString("ref");
                                    String propellant = jo.getString("partner_name");
                                    String salesid = jo.getString("sales_id");
                                    String partnerid = jo.getString("partner_id");
                                    String konst = jo.getString("const");
                                    String status = jo.getString("visit_state");
                                    String frekuensi = jo.getString("freukensi_name");
                                    s = new SpacecraftLuar();
                                    s.setId(id);
                                    s.setName(name);
                                    s.setPropellant(propellant);
                                    s.setStatus(status);
                                    s.setSalesid(salesid);
                                    s.setPartnerId(partnerid);
                                    s.setFrekuensi(konst);
                                    s.setnamaFrekuensi(frekuensi);

                                    downloadedData.add(s);
                                }
                                myProgressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                myProgressBar.setVisibility(View.GONE);
                                SpacecraftLuar sl;
//                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("CANT PARSE JSON", e.getMessage());
                                for (com.example.sfmtesting.TokoDalamRute tdr : listtoko){
                                    sl = new SpacecraftLuar();
                                    sl.setId(tdr.getId());
                                    sl.setStatus(tdr.getStatus());
                                    sl.setName(tdr.getKode());
                                    sl.setPropellant(tdr.getNama());
                                    sl.setSalesid(tdr.getSalesid());
                                    sl.setPartnerId(tdr.getPartnerId());
                                    sl.setFrekuensi(tdr.getFrekuensi());
                                    sl.setStatus(tdr.getStatus());
                                    downloadedData.add(sl);
                                }

                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);
//                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Error", "Error : " + anError.getMessage());
                            SpacecraftLuar sl;
                            for (com.example.sfmtesting.TokoDalamRute tdr : listtoko){
                                sl = new SpacecraftLuar();
                                sl.setId(tdr.getId());
                                sl.setStatus(tdr.getStatus());
                                sl.setName(tdr.getKode());
                                sl.setPropellant(tdr.getNama());
                                sl.setSalesid(tdr.getSalesid());
                                sl.setPartnerId(tdr.getPartnerId());
                                sl.setFrekuensi(tdr.getFrekuensi());
                                downloadedData.add(sl);
                            }

                        }
                    });
//            StatusSR.callPlan = downloadedData.size();
            return downloadedData;
        }
    }

}