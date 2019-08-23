package com.example.salesforcemanagement;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import me.xdrop.fuzzywuzzy.FuzzySearch;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalEminaFragment extends Fragment {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    final com.example.salesforcemanagement.Spacecraft kumpulanorder = new com.example.salesforcemanagement.Spacecraft();
    final ArrayList<com.example.salesforcemanagement.Spacecraft> order = new ArrayList<>();
    public static SearchView mySearchView;
    int fuzzyscore = 75;

    final ArrayList<Integer> orderedID = new ArrayList<>();
    final ArrayList<String> orderedkode = new ArrayList<>(); //kodemo
    final ArrayList<String> orderedname = new ArrayList<>(); //namamo
    final ArrayList<String> orderedprice = new ArrayList<>(); //hargamo
    final ArrayList<String> orderedstock = new ArrayList<>(); //stockmo
    final ArrayList<String> orderedqty = new ArrayList<>(); //qtymo

    /*
     Our data object
     */
    static class FilterHelper extends Filter implements Serializable {
        ArrayList<com.example.salesforcemanagement.Spacecraft> currentList;
        ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<com.example.salesforcemanagement.Spacecraft> currentList, ListViewAdapter adapter, Context c) {
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
                ArrayList<com.example.salesforcemanagement.Spacecraft> foundFilters = new ArrayList<>();
                com.example.salesforcemanagement.Spacecraft spacecraft;
//ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    spacecraft = currentList.get(i);
//SEARCH
                    if (spacecraft.getFuzzyMatchStatus().toUpperCase().contains(constraint)) {
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
            adapter.setSpacecrafts((ArrayList<com.example.salesforcemanagement.Spacecraft>) filterResults.values);
            adapter.refresh();
        }
    }


    /*
    Our custom adapter class
    */
    public class ListViewAdapter extends BaseAdapter implements Filterable, Serializable {
        Context c;
        ArrayList<com.example.salesforcemanagement.Spacecraft> spacecrafts;
        public ArrayList<com.example.salesforcemanagement.Spacecraft> currentList;
        FilterHelper filterHelper;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<com.example.salesforcemanagement.Spacecraft> spacecrafts) {
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            holder = new ViewHolder();
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.model_row_hist, viewGroup, false);
                holder.cardView = view.findViewById(R.id.cardview);
                holder.product_odoo = view.findViewById(R.id.odoo_hist);
                holder.product_name = view.findViewById(R.id.nama_hist);
                holder.product_price = view.findViewById(R.id.harga_hist);
                holder.product_ws = view.findViewById(R.id.order_BA_hist);
                holder.product_stock = view.findViewById(R.id.stock_hist);
                holder.product_qty = view.findViewById(R.id.qtyhist);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
                holder.product_odoo.setText("");
                holder.product_name.setText("");
                holder.product_price.setText("");
                holder.product_ws.setText("");
                holder.product_stock.setText("");
                holder.product_qty.setText("");
            }
            if (i % 2 == 0) {
                holder.cardView.setBackgroundColor(Color.rgb(240, 240, 240));
            } else {
                holder.cardView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            final com.example.salesforcemanagement.Spacecraft s = (com.example.salesforcemanagement.Spacecraft) this.getItem(i);
            holder.product_odoo.setText(s.getKodeodoo());
            holder.product_name.setText(s.getNamaproduk());
            holder.product_price.setText(s.getPrice());
            holder.product_ws.setText("" + s.getWeeklySales());
            holder.product_stock.setText(s.getStock());
            holder.product_qty.setText(s.getQty());

            return view;
        }

        public void setSpacecrafts(ArrayList<com.example.salesforcemanagement.Spacecraft> filteredSpacecrafts) {
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
    }


    public class JSONDownloader implements Serializable {

        private final Context c;

        public JSONDownloader(Context c) {
            this.c = c;
        }

        /*
        Fetch JSON Data
        */
        public ArrayList<com.example.salesforcemanagement.Spacecraft> retrieve(final ListView mLpositiveistView, final ProgressBar myProgressBar) {
            final ArrayList<com.example.salesforcemanagement.Spacecraft> downloadedData = new ArrayList<>();
            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);
            final com.example.salesforcemanagement.DatabaseMHSHandler dbEBP = new com.example.salesforcemanagement.DatabaseMHSHandler(getContext());

            pref = Objects.requireNonNull(getActivity()).getSharedPreferences("TokoPref", 0);
            editor = pref.edit();
            final String customer = pref.getString("ref", "");
            final String partnerid = pref.getString("partner_id", "0");
            final ArrayList<com.example.salesforcemanagement.Spacecraft> listEBP = dbEBP.getAllProdukToko(partnerid, "brand:Emina");
            for (int i = 0; i < listEBP.size(); i++) {
                Log.e("LIST MHS", listEBP.get(i).getKodeodoo() + " - " + listEBP.get(i).getNamaproduk() + " - " + listEBP.get(i).getBrand() + " - " + listEBP.get(i).getPartner_id());
            }
            Log.e("SIZE LIST MHS", "" + listEBP.size());
            String url = "https://sfa-api.pti-cosmetics.com/v_product_mhs?brand=ilike.*emina&partner_ref=ilike.*" + customer;
            Log.e("url", url);
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            com.example.salesforcemanagement.Spacecraft s;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("product_id");
                                    String name = jo.getString("default_code");
                                    String propellant = jo.getString("name");
                                    String barcode = jo.getString("barcode");
                                    String price = jo.getString("price");
                                    String unit = jo.getString("unit");
                                    String cat = jo.getString("category");
                                    int ws = jo.getInt("weekly_qty");
                                    s = new com.example.salesforcemanagement.Spacecraft();
                                    s.setId(id);
                                    s.setKoli(unit);
                                    s.setBarcode(barcode);
                                    s.setCategory(cat);
                                    s.setKodeodoo(name);
                                    s.setNamaproduk(propellant);
                                    s.setWeeklySales(ws);
                                    s.setPrice(price);
                                    s.setStock("0");
                                    s.setQty("0");
                                    downloadedData.add(s);
                                }
                                myProgressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                myProgressBar.setVisibility(View.GONE);
                                Log.e("CANT PARSE JSON", e.getMessage());
                                com.example.salesforcemanagement.Spacecraft EBP;
                                for (com.example.salesforcemanagement.Spacecraft produk : listEBP) {
                                    Log.e("ID", "" + produk.getId() + ", Kode: " + produk.getKodeodoo() + ", ");
                                    Log.e("MHS OFFLINE", "EMINA");
                                    EBP = new com.example.salesforcemanagement.Spacecraft();
                                    EBP.setId(produk.getId());
                                    EBP.setKodeodoo(produk.getKodeodoo());
                                    EBP.setNamaproduk(produk.getNamaproduk());
                                    EBP.setCategory(produk.getCategory());
                                    EBP.setPrice(produk.getPrice());
                                    EBP.setBarcode(produk.getBarcode());
                                    EBP.setWeeklySales(produk.getWeeklySales());
                                    EBP.setStock(produk.getStock());
                                    EBP.setQty(produk.getQty());
                                    EBP.setKoli(produk.getKoli());
                                    downloadedData.add(EBP);
                                }
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);
                            Log.e("Error", anError.getMessage());
                            com.example.salesforcemanagement.Spacecraft EBP;
                            for (com.example.salesforcemanagement.Spacecraft produk : listEBP) {
                                Log.e("ID", "" + produk.getId() + ", Kode: " + produk.getKodeodoo() + ", ");
                                Log.e("MHS OFFLINE", "EMINA");
                                EBP = new com.example.salesforcemanagement.Spacecraft();
                                EBP.setId(produk.getId());
                                EBP.setKodeodoo(produk.getKodeodoo());
                                EBP.setNamaproduk(produk.getNamaproduk());
                                EBP.setCategory(produk.getCategory());
                                EBP.setPrice(produk.getPrice());
                                EBP.setBarcode(produk.getBarcode());
                                EBP.setWeeklySales(produk.getWeeklySales());
                                EBP.setStock(produk.getStock());
                                EBP.setQty(produk.getQty());
                                EBP.setKoli(produk.getKoli());
                                downloadedData.add(EBP);
                            }
                        }
                    });
            return downloadedData;
        }
    }

    ArrayList<com.example.salesforcemanagement.Spacecraft> spacecrafts = new ArrayList<>();
    ListView myListView;
    ListViewAdapter adapter;

    //    @NonNull
    @Override

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_additional_emina, container, false);
        myListView = view.findViewById(R.id.mListAdditionalEmina);
        final ProgressBar myProgressBar = view.findViewById(R.id.myProgressBarAdditionalEmina);
        ImageView scanmhsemina = view.findViewById(R.id.barcodemhsemina);
        scanmhsemina.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), com.example.salesforcemanagement.scan.ScanmhseminaActivity.class);
            startActivity(intent);
        });
        mySearchView = view.findViewById(R.id.mySearchViewAdditionalEmina);
        mySearchView.setIconified(true);
        mySearchView.setOnSearchClickListener(view12 -> {
        });
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                for (int i = 0; i < spacecrafts.size(); i++) {
                    Log.d("FUZZY RATIO " + s + " : " + spacecrafts.get(i).getNamaproduk(), "" + FuzzySearch.partialRatio(s, spacecrafts.get(i).getNamaproduk()));
                    if (s.length() == 0) {
                        spacecrafts.get(i).setFuzzyMatchStatus("fuzzymatched");
                    } else {
                        if (FuzzySearch.partialRatio(s.toLowerCase(), spacecrafts.get(i).getNamaproduk().toLowerCase() + " " + spacecrafts.get(i).getKodeodoo() + " " + spacecrafts.get(i).getBarcode()) > fuzzyscore) {
                            spacecrafts.get(i).setFuzzyMatchStatus("fuzzymatched");
                        } else {
                            spacecrafts.get(i).setFuzzyMatchStatus("fuzzynotmatched");
                        }
                    }
                }
                adapter.getFilter().filter("fuzzymatched");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                for (int i = 0; i < spacecrafts.size(); i++) {
                    Log.d("FUZZY RATIO " + query + " : " + spacecrafts.get(i).getNamaproduk(), "" + FuzzySearch.partialRatio(query, spacecrafts.get(i).getNamaproduk()));
                    if (query.length() == 0) {
                        spacecrafts.get(i).setFuzzyMatchStatus("fuzzymatched");
                    } else {
                        if (FuzzySearch.partialRatio(query.toLowerCase(), spacecrafts.get(i).getNamaproduk().toLowerCase() + " " + spacecrafts.get(i).getKodeodoo() + " " + spacecrafts.get(i).getBarcode()) > fuzzyscore) {
                            spacecrafts.get(i).setFuzzyMatchStatus("fuzzymatched");
                        } else {
                            spacecrafts.get(i).setFuzzyMatchStatus("fuzzynotmatched");
                        }
                    }
                }
                adapter.getFilter().filter("fuzzymatched");
                return false;
            }
        });

        spacecrafts = new JSONDownloader(getActivity()).retrieve(myListView, myProgressBar);
        adapter = new ListViewAdapter(getActivity(), spacecrafts);
        String txt = "";
        String all = "";
        for (int j = 0; j < spacecrafts.size(); j++) {
            txt = spacecrafts.get(j).namaproduk + "\n";
            all = all + txt;

        }
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        myListView.setOnItemClickListener((parent, view13, position, id) -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.form_takingorder, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);
            dialog.setTitle("Input Order");

            TextView formKode;
            final TextView formNama;
            final TextView formHarga;
            final TextView formPcs, formws;
            final EditText formStock, formQty;

            formKode = dialogView.findViewById(R.id.kode_odoo_form);
            formNama = dialogView.findViewById(R.id.nama_produk_form);
            formws = dialogView.findViewById(R.id.ws_form);
            formHarga = dialogView.findViewById(R.id.harga_form);
            formStock = dialogView.findViewById(R.id.stock_form);
            formQty = dialogView.findViewById(R.id.qty_form);
            formPcs = dialogView.findViewById(R.id.pcs_produk_form);

            final Spacecraft coba = (Spacecraft) adapter.getItem(position);

            String konsta = pref.getString("const", "2");
            final int konst = Integer.parseInt(Objects.requireNonNull(konsta));

            formKode.setText(coba.getKodeodoo());
            formNama.setText(coba.getNamaproduk());
            formws.setText("" + coba.getWeeklySales());
            formHarga.setText(coba.getPrice());
            formPcs.setText(coba.getKoli());
            formStock.setHint(coba.getStock());
            String stockformawal = formStock.getText().toString();
            if (!stockformawal.isEmpty()) {
                int intstockformawal = Integer.parseInt(stockformawal);
                int qtyformawal = konst * coba.getWeeklySales() - intstockformawal;
                if (qtyformawal >= 0) {
                    formQty.setHint(String.valueOf(qtyformawal));
                } else {
                    formQty.setHint("0");
                }
            } else {
                formQty.setHint("0");
            }

            formStock.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    String stockform = formStock.getText().toString();
                    if (!stockform.isEmpty()) {
                        int intstockform = Integer.parseInt(stockform);
                        int qtyform = konst * coba.getWeeklySales() - intstockform;
                        if (qtyform >= 0) {
                            formQty.setHint(String.valueOf(qtyform));
                        } else {
                            formQty.setHint("0");
                        }
                    } else {
                        formQty.setHint("0");
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String stockform = formStock.getText().toString();
                    if (!stockform.isEmpty()) {
                        int intstockform = Integer.parseInt(stockform);
                        int qtyform = konst * coba.getWeeklySales() - intstockform;
                        if (qtyform >= 0) {
                            formQty.setHint(String.valueOf(qtyform));
                        } else {
                            formQty.setHint("0");
                        }
                    } else {
                        formQty.setHint("0");
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            final TextView finalFormKode = formKode;
            final int[] count = new int[1];
            dialog.setPositiveButton("Order", (dialog1, which) -> {
                String mStock = formStock.getText().toString();
                String mQty = formQty.getText().toString();
                count[0] = 0;
                if (mStock.isEmpty() && mQty.isEmpty()) {
                    Toast.makeText(getContext(), "Mohon jangan kosongkan stock dan quantity order", Toast.LENGTH_SHORT).show();
                } else if (mStock.isEmpty() && !mQty.isEmpty()) {
                    orderedID.add(coba.getId());
                    orderedkode.add(finalFormKode.getText().toString());
                    orderedname.add(formNama.getText().toString());
                    orderedprice.add(formHarga.getText().toString());
//                            int stockform = 0;
                    orderedstock.add("0");
                    orderedqty.add(formQty.getText().toString());

                    kumpulanorder.setId(orderedID.get(count[0]));
                    kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                    kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                    kumpulanorder.setPrice(orderedprice.get(count[0]));
                    kumpulanorder.setStock(orderedstock.get(count[0]));
                    kumpulanorder.setQty(orderedqty.get(count[0]));

                    order.add(count[0], kumpulanorder);

                    count[0]++;

                    coba.setStock("0");
                    coba.setQty(formQty.getText().toString());

                    TextView stock = view13.findViewById(R.id.stock_hist);
                    TextView qty = view13.findViewById(R.id.qtyhist);

                    stock.setText(spacecrafts.get(position).getStock());
                    qty.setText(spacecrafts.get(position).getQty());



                    boolean check = false;
                    boolean add = true;

                    for (int x = 0; x < Globalemina.kode.size(); x++) {
                        if (finalFormKode.getText().toString().equals(Globalemina.kode.get(x))) {
                            check = true;
                        }
                        if (check) {
                            Globalemina.id_produk.set(x, coba.getId());
                            Globalemina.kode.set(x, finalFormKode.getText().toString());
                            Globalemina.nama.set(x, formNama.getText().toString());
                            Globalemina.harga.set(x, formHarga.getText().toString());
                            Globalemina.stock.set(x, "0");
                            Globalemina.sgtorder.set(x, "0");
                            Globalemina.qty.set(x, formQty.getText().toString());
                            Globalemina.kategori.set(x, coba.getCategory());
                            check = false;
                            add = false;
                        }

                    }
                    if (add) {
                        Globalemina.produk.add(Globalemina.produkCount, kumpulanorder);
                        Globalemina.id_produk.add(coba.getId());
                        Globalemina.kode.add(finalFormKode.getText().toString());
                        Globalemina.nama.add(formNama.getText().toString());
                        Globalemina.harga.add(formHarga.getText().toString());
                        Globalemina.stock.add("0");
                        Globalemina.sgtorder.add("0");
                        Globalemina.qty.add(formQty.getText().toString());
                        Globalemina.kategori.add(coba.getCategory());
                        Globalemina.produkCount++;
                    }

                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();
                } else if (!mStock.isEmpty() && mQty.isEmpty()) {

                    orderedID.add(coba.getId());
                    orderedkode.add(finalFormKode.getText().toString());
                    orderedname.add(formNama.getText().toString());
                    orderedprice.add(formHarga.getText().toString());
                    orderedstock.add(formStock.getText().toString());
                    String stockform = formStock.getText().toString();
                    int intstockform = Integer.parseInt(stockform);
                    int qtyform = konst * coba.getWeeklySales() - intstockform;
                    if (qtyform < 0) {
                        qtyform = 0;
                    }
                    orderedqty.add(String.valueOf(qtyform));

                    kumpulanorder.setId(orderedID.get(count[0]));
                    kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                    kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                    kumpulanorder.setPrice(orderedprice.get(count[0]));
                    kumpulanorder.setStock(orderedstock.get(count[0]));
                    kumpulanorder.setQty(orderedqty.get(count[0]));

                    order.add(count[0], kumpulanorder);

                    count[0]++;


                    coba.setStock(formStock.getText().toString());
                    coba.setQty(String.valueOf(qtyform));

                    TextView stock = view13.findViewById(R.id.stock_hist);
                    TextView qty = view13.findViewById(R.id.qtyhist);

                    stock.setText(spacecrafts.get(position).getStock());
                    qty.setText(spacecrafts.get(position).getQty());

//                            Toast.makeText(getActivity(), "stock " + coba.getStock() + " qty " + coba.getQty(), Toast.LENGTH_LONG).show();

                    boolean check = false;
                    boolean add = true;

                    for (int x = 0; x < Globalemina.kode.size(); x++) {
                        if (finalFormKode.getText().toString().equals(Globalemina.kode.get(x))) {
                            check = true;
                        }
                        if (check) {
                            Globalemina.id_produk.set(x, coba.getId());
                            Globalemina.kode.set(x, finalFormKode.getText().toString());
                            Globalemina.nama.set(x, formNama.getText().toString());
                            Globalemina.harga.set(x, formHarga.getText().toString());
                            Globalemina.stock.set(x, formStock.getText().toString());
                            Globalemina.qty.set(x, String.valueOf(qtyform));
                            Globalemina.sgtorder.set(x, "0");
                            Globalemina.kategori.set(x, coba.getCategory());
                            check = false;
                            add = false;
                        }

                    }
                    if (add) {
                        Globalemina.id_produk.add(coba.getId());
                        Globalemina.produk.add(Globalemina.produkCount, kumpulanorder);
                        Globalemina.kode.add(finalFormKode.getText().toString());
                        Globalemina.nama.add(formNama.getText().toString());
                        Globalemina.harga.add(formHarga.getText().toString());
                        Globalemina.stock.add(formStock.getText().toString());
                        Globalemina.qty.add(String.valueOf(qtyform));
                        Globalemina.kategori.add(coba.getCategory());
                        Globalemina.sgtorder.add("0");
                        Globalemina.produkCount++;
                    }


                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();

                } else {

                    orderedID.add(coba.getId());
                    orderedkode.add(finalFormKode.getText().toString());
                    orderedname.add(formNama.getText().toString());
                    orderedprice.add(formHarga.getText().toString());
                    orderedstock.add(formStock.getText().toString());
                    orderedqty.add(formQty.getText().toString());

                    kumpulanorder.setId(orderedID.get(count[0]));
                    kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                    kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                    kumpulanorder.setPrice(orderedprice.get(count[0]));
                    kumpulanorder.setStock(orderedstock.get(count[0]));
                    kumpulanorder.setQty(orderedqty.get(count[0]));

                    order.add(count[0], kumpulanorder);

                    count[0]++;

                    coba.setStock(formStock.getText().toString());
                    coba.setQty(formQty.getText().toString());

                    TextView stock = view13.findViewById(R.id.stock_hist);
                    TextView qty = view13.findViewById(R.id.qtyhist);

                    stock.setText(spacecrafts.get(position).getStock());
                    qty.setText(spacecrafts.get(position).getQty());

//                            Toast.makeText(getActivity(), "order:" + formNama.getText().toString() + "stockmo " + coba.getStock() + " qtymo " + coba.getQty(), Toast.LENGTH_LONG).show();


                    boolean check = false;
                    boolean add = true;

                    for (int x = 0; x < Globalemina.kode.size(); x++) {
                        if (finalFormKode.getText().toString().equals(Globalemina.kode.get(x))) {
                            check = true;
                        }
                        if (check) {
                            Globalemina.id_produk.set(x, coba.getId());
                            Globalemina.kode.set(x, finalFormKode.getText().toString());
                            Globalemina.nama.set(x, formNama.getText().toString());
                            Globalemina.harga.set(x, formHarga.getText().toString());
                            Globalemina.stock.set(x, formStock.getText().toString());
                            Globalemina.qty.set(x, formQty.getText().toString());
                            Globalemina.kategori.set(x, coba.getCategory());
                            Globalemina.sgtorder.set(x, "0");
                            check = false;
                            add = false;
                        }

                    }
                    if (add) {
                        Globalemina.produk.add(Globalemina.produkCount, kumpulanorder);
                        Globalemina.id_produk.add(coba.getId());
                        Globalemina.kode.add(finalFormKode.getText().toString());
                        Globalemina.nama.add(formNama.getText().toString());
                        Globalemina.harga.add(formHarga.getText().toString());
                        Globalemina.stock.add(formStock.getText().toString());
                        Globalemina.qty.add(formQty.getText().toString());
                        Globalemina.kategori.add(coba.getCategory());
                        Globalemina.sgtorder.add("0");
                        Globalemina.produkCount++;
                    }

                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();

                }

            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        });

        Button orderbutton = view.findViewById(R.id.order_buttonAdditionalEmina);
        orderbutton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList("ID", orderedID);
            bundle.putStringArrayList("kodemo", orderedkode);
            bundle.putStringArrayList("namamo", orderedname);
            bundle.putStringArrayList("hargamo", orderedprice);
            bundle.putStringArrayList("stok", orderedstock);
            bundle.putStringArrayList("kuantitas", orderedqty);
            Intent intent = new Intent(getActivity().getBaseContext(), RingkasanEminaActivity.class);
            intent.putExtra("listorder", bundle);
            startActivity(intent);
        });

        return view;
    }

}
