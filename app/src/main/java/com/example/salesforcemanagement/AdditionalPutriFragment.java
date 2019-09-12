package com.example.salesforcemanagement;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.salesforcemanagement.scan.ScanmhsputriActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import me.xdrop.fuzzywuzzy.FuzzySearch;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdditionalPutriFragment extends Fragment {
    final com.example.salesforcemanagement.Spacecraft kumpulanorder = new com.example.salesforcemanagement.Spacecraft();
    final ArrayList<com.example.salesforcemanagement.Spacecraft> order = new ArrayList<>();
    final ArrayList<Integer> orderedID = new ArrayList<>();
    final ArrayList<String> orderedkode = new ArrayList<>(); //kodemo
    final ArrayList<String> orderedname = new ArrayList<>(); //namamo
    final ArrayList<String> orderedprice = new ArrayList<>(); //hargamo
    final ArrayList<String> orderedstock = new ArrayList<>(); //stockmo
    final ArrayList<String> orderedqty = new ArrayList<>(); //qtymo
    final ArrayList<String> orderedcategory = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<com.example.salesforcemanagement.Spacecraft> spacecrafts = new ArrayList<com.example.salesforcemanagement.Spacecraft>();
    ImageView scanmhsputri;
    public static SearchView mySearchView;
    ListView myListView;
    ListViewAdapter adapter;
    private ArrayList<String> stock1 = new ArrayList<String>();
    private ArrayList<String> qty1 = new ArrayList<String>();
    int fuzzyscore = 75;

    Boolean barcodeInit = false;
    int stateSearching = 3;
    public int lengthStringBarcode;

    @Override

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_additional_putri, container, false);
        myListView = view.findViewById(R.id.mListAdditionalPutri);
        final ProgressBar myProgressBar = view.findViewById(R.id.myProgressBarAdditionalPutri);
        ImageView scanmhsputri = view.findViewById(R.id.barcodemhsputri);
        scanmhsputri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanmhsputriActivity.class);
                startActivity(intent);
            }
        });
        mySearchView = view.findViewById(R.id.mySearchViewAdditionalPutri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mySearchView.setIconified(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mySearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
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

                    if(s.length() == 0){
                        barcodeInit = false;
                        stateSearching = 3;
                        adapter.setFilterHelperState(stateSearching);
                    }

                    if(s.length() == lengthStringBarcode){
                        stateSearching = 1;
                        adapter.setFilterHelperState(stateSearching);
                        lengthStringBarcode = 0;
                    }
                    else if(isInteger(s)){
                        stateSearching = 2;
                        adapter.setFilterHelperState(stateSearching);
                    }
                    else {
                        stateSearching = 3;
                        adapter.setFilterHelperState(stateSearching);
                    }

                    switch (stateSearching){
                        case 1:
                            adapter.getFilter().filter(s);
                            barcodeInit = false;
                            break;


                        case 2:
                            adapter.getFilter().filter(s);
                            break;

                        case 3:
                            adapter.getFilter().filter("fuzzymatched");
                            break;
                    }
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

                    if(query.length() == 0){
                        barcodeInit = false;
                        stateSearching = 3;
                        adapter.setFilterHelperState(stateSearching);
                    }

                    if(barcodeInit){
                        if(query.length() > 0){
                            lengthStringBarcode = query.length();
                            Log.d("DEBUG SEARCHING", "query barcode");
                            stateSearching = 1;
                            adapter.setFilterHelperState(stateSearching);
                        }
                    }
                    else if(isInteger(query)){
                        Log.d("DEBUG SEARCHING", "query integer");
                        stateSearching = 2;
                        adapter.setFilterHelperState(stateSearching);
                    }
                    else {
                        Log.d("DEBUG SEARCHING", "query text");
                        stateSearching = 3;
                        adapter.setFilterHelperState(stateSearching);
                    }

                    switch (stateSearching){
                        case 1:
                            adapter.getFilter().filter(query);
                            barcodeInit = false;
                            break;


                        case 2:
                            adapter.getFilter().filter(query);
                            break;

                        case 3:
                            adapter.getFilter().filter("fuzzymatched");
                            break;
                    }
                    return false;
                }
            });
        }

        spacecrafts = new JSONDownloader(getActivity()).retrieve(myListView, myProgressBar);
        adapter = new ListViewAdapter(getActivity(), spacecrafts);
        String txt = "";
        String all = "";
        for (int j = 0; j < spacecrafts.size(); j++) {
            txt = spacecrafts.get(j).namaproduk + "\n";
            all = all + txt;

        }
//        Toast.makeText(getActivity(), "Order: \n" + all, Toast.LENGTH_LONG).show();
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
//                Toast.makeText(getActivity(), "data muncul", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.form_takingorder, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Input Order");

                TextView formKode = null;
                final TextView formNama;
                final TextView formHarga, formPcs, formws, formStockBA, formQtyBA;
                final EditText formStock, formQty;

                LinearLayout llstocknonBA = dialogView.findViewById(R.id.layoutstocknonba);
                LinearLayout llstockBA = dialogView.findViewById(R.id.layoutstockba);
                LinearLayout llorderBA = dialogView.findViewById(R.id.layoutorderba);

                formKode = dialogView.findViewById(R.id.kode_odoo_form);
                formNama = dialogView.findViewById(R.id.nama_produk_form);
                formHarga = dialogView.findViewById(R.id.harga_form);
                formPcs = dialogView.findViewById(R.id.pcs_produk_form);
                formws = dialogView.findViewById(R.id.ws_form);
                formStock = dialogView.findViewById(R.id.stock_form);
                formQty = dialogView.findViewById(R.id.qty_form);
                formStockBA = dialogView.findViewById(R.id.stock_form_ba);
                formQtyBA = dialogView.findViewById(R.id.suggest_form);

                final Spacecraft coba = (Spacecraft) adapter.getItem(position);
                Log.e("Produk Terpilih", coba.getKodeodoo() + " - " + coba.getNamaproduk() + " - STK: " + coba.getStock() + ", QTY: " + coba.getQty());

                String konsta = pref.getString("const", "2");
                boolean is_BA = pref.getBoolean("isBa", false);
                Log.e("TOKO BA1", "harusnya " + is_BA);
                final int konst = Integer.parseInt(konsta);

                formKode.setText(coba.getKodeodoo());
                formNama.setText(coba.getNamaproduk());
                formHarga.setText(coba.getPrice());
                formPcs.setText(coba.getKoli());
                formws.setText("" + coba.getWeeklySales());
                if (is_BA) {
                    Toast.makeText(getContext(), "harusnya toko BA", Toast.LENGTH_SHORT).show();
                    llstocknonBA.setVisibility(View.GONE);
                    formStockBA.setText(coba.getStock());
                    formQtyBA.setText(coba.getQty());
                    formQty.setHint(coba.getQty());
                    TextView finalFormKode1 = formKode;

                    dialog.setPositiveButton("Order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            coba.setQty(formQty.getText().toString());

                            TextView qty = view.findViewById(R.id.qtyhist);

                            qty.setText(spacecrafts.get(position).getQty());
                            boolean check = false;
                            boolean add = true;

                            for (int x = 0; x < Globalputri.kode.size(); x++) {
                                if (finalFormKode1.getText().toString().equals(Globalputri.kode.get(x))) {
                                    check = true;
                                }
                                if (check) {
                                    Globalputri.id_produk.set(x, coba.getId());
                                    Globalputri.kode.set(x, finalFormKode1.getText().toString());
                                    Globalputri.nama.set(x, formNama.getText().toString());
                                    Globalputri.harga.set(x, formHarga.getText().toString());
                                    Globalputri.stock.set(x, formStockBA.getText().toString());
                                    Globalputri.qty.set(x, formQtyBA.getText().toString());
                                    Globalputri.kategori.set(x, coba.getCategory());
                                    Globalputri.sgtorder.set(x, formQtyBA.getText().toString());
                                    check = false;
                                    add = false;
                                }

                            }
                            if (add) {
                                Globalputri.id_produk.add(coba.getId());
                                Globalputri.produk.add(Globalputri.produkCount, kumpulanorder);
                                Globalputri.kode.add(finalFormKode1.getText().toString());
                                Globalputri.nama.add(formNama.getText().toString());
                                Globalputri.harga.add(formHarga.getText().toString());
                                Globalputri.stock.add(formStockBA.getText().toString());
                                Globalputri.qty.add(formQtyBA.getText().toString());
                                Globalputri.kategori.add(coba.getCategory());
                                Globalputri.sgtorder.add(formQtyBA.getText().toString());
                                Globalputri.produkCount++;
                            }


                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {
                    llorderBA.setVisibility(View.GONE);
                    llstockBA.setVisibility(View.GONE);
                    formStock.setHint(coba.getStock());
                    String stockformawal = formStock.getText().toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
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
                    }
                    formStock.setHint(coba.getStock());
                    stockformawal = formStock.getText().toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
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
                    }

                    formStock.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            String stockform = formStock.getText().toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
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
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String stockform = formStock.getText().toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
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

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    final TextView finalFormKode = formKode;
                    final int[] count = new int[1];
                    dialog.setPositiveButton("Order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String mStock = formStock.getText().toString();
                            String mQty = formQty.getText().toString();
                            count[0] = 0;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
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
                                    orderedcategory.add(coba.getCategory());

                                    kumpulanorder.setId(orderedID.get(count[0]));
                                    kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                                    kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                                    kumpulanorder.setPrice(orderedprice.get(count[0]));
                                    kumpulanorder.setStock(orderedstock.get(count[0]));
                                    kumpulanorder.setQty(orderedqty.get(count[0]));
                                    kumpulanorder.setCategory(orderedcategory.get(count[0]));

                                    order.add(count[0], kumpulanorder);

                                    count[0]++;

                                    coba.setStock("0");
                                    coba.setQty(formQty.getText().toString());

                                    TextView stock = view.findViewById(R.id.stock_hist);
                                    TextView qty = view.findViewById(R.id.qtyhist);

                                    stock.setText(spacecrafts.get(position).getStock());
                                    qty.setText(spacecrafts.get(position).getQty());

    //                            Toast.makeText(getActivity(), "order:" + formNama.getText().toString() + "stockmo " + coba.getStock() + " qtymo " + coba.getQty(), Toast.LENGTH_LONG).show();


                                    boolean check = false;
                                    boolean add = true;

                                    for (int x = 0; x < Globalputri.kode.size(); x++) {
                                        if (finalFormKode.getText().toString().equals(Globalputri.kode.get(x))) {
                                            check = true;
                                        }
                                        if (check) {
                                            Globalputri.id_produk.set(x, coba.getId());
                                            Globalputri.kode.set(x, finalFormKode.getText().toString());
                                            Globalputri.nama.set(x, formNama.getText().toString());
                                            Globalputri.harga.set(x, formHarga.getText().toString());
                                            Globalputri.stock.set(x, "0");
                                            Globalputri.qty.set(x, formQty.getText().toString());
                                            Globalputri.kategori.set(x, coba.getCategory());
                                            Globalputri.sgtorder.set(x, "0");
                                            check = false;
                                            add = false;
                                        }

                                    }
                                    if (add) {
                                        Globalputri.produk.add(Globalputri.produkCount, kumpulanorder);
                                        Globalputri.id_produk.add(coba.getId());
                                        Globalputri.kode.add(finalFormKode.getText().toString());
                                        Globalputri.nama.add(formNama.getText().toString());
                                        Globalputri.harga.add(formHarga.getText().toString());
                                        Globalputri.stock.add("0");
                                        Globalputri.sgtorder.add("0");
                                        Globalputri.qty.add(formQty.getText().toString());
                                        Globalputri.kategori.add(coba.getCategory());
                                        Globalputri.produkCount++;
                                    }

                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
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
                                    orderedcategory.add(coba.getCategory());

                                    kumpulanorder.setId(orderedID.get(count[0]));
                                    kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                                    kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                                    kumpulanorder.setPrice(orderedprice.get(count[0]));
                                    kumpulanorder.setStock(orderedstock.get(count[0]));
                                    kumpulanorder.setQty(orderedqty.get(count[0]));
                                    kumpulanorder.setCategory(orderedcategory.get(count[0]));

                                    order.add(count[0], kumpulanorder);

                                    count[0]++;


                                    coba.setStock(formStock.getText().toString());
                                    coba.setQty(String.valueOf(qtyform));

                                    TextView stock = view.findViewById(R.id.stock_hist);
                                    TextView qty = view.findViewById(R.id.qtyhist);

                                    stock.setText(spacecrafts.get(position).getStock());
                                    qty.setText(spacecrafts.get(position).getQty());

    //                            Toast.makeText(getActivity(), "stock " + coba.getStock() + " qty " + coba.getQty(), Toast.LENGTH_LONG).show();

                                    boolean check = false;
                                    boolean add = true;

                                    for (int x = 0; x < Globalputri.kode.size(); x++) {
                                        if (finalFormKode.getText().toString().equals(Globalputri.kode.get(x))) {
                                            check = true;
                                        }
                                        if (check) {
                                            Globalputri.id_produk.set(x, coba.getId());
                                            Globalputri.kode.set(x, finalFormKode.getText().toString());
                                            Globalputri.nama.set(x, formNama.getText().toString());
                                            Globalputri.harga.set(x, formHarga.getText().toString());
                                            Globalputri.stock.set(x, formStock.getText().toString());
                                            Globalputri.qty.set(x, String.valueOf(qtyform));
                                            Globalputri.kategori.set(x, coba.getCategory());
                                            Globalputri.sgtorder.set(x, "0");
                                            check = false;
                                            add = false;
                                        }

                                    }
                                    if (add) {
                                        Globalputri.id_produk.add(coba.getId());
                                        Globalputri.produk.add(Globalputri.produkCount, kumpulanorder);
                                        Globalputri.kode.add(finalFormKode.getText().toString());
                                        Globalputri.nama.add(formNama.getText().toString());
                                        Globalputri.harga.add(formHarga.getText().toString());
                                        Globalputri.stock.add(formStock.getText().toString());
                                        Globalputri.qty.add(String.valueOf(qtyform));
                                        Globalputri.kategori.add(coba.getCategory());
                                        Globalputri.sgtorder.add("0");
                                        Globalputri.produkCount++;
                                    }


                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();

                                } else {

                                    orderedID.add(coba.getId());
                                    orderedkode.add(finalFormKode.getText().toString());
                                    orderedname.add(formNama.getText().toString());
                                    orderedprice.add(formHarga.getText().toString());
                                    orderedstock.add(formStock.getText().toString());
                                    orderedqty.add(formQty.getText().toString());
                                    orderedcategory.add(coba.getCategory());

                                    kumpulanorder.setId(orderedID.get(count[0]));
                                    kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                                    kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                                    kumpulanorder.setPrice(orderedprice.get(count[0]));
                                    kumpulanorder.setStock(orderedstock.get(count[0]));
                                    kumpulanorder.setQty(orderedqty.get(count[0]));
                                    kumpulanorder.setCategory(orderedcategory.get(count[0]));

                                    order.add(count[0], kumpulanorder);

                                    count[0]++;

                                    coba.setStock(formStock.getText().toString());
                                    coba.setQty(formQty.getText().toString());

                                    TextView stock = view.findViewById(R.id.stock_hist);
                                    TextView qty = view.findViewById(R.id.qtyhist);

                                    stock.setText(spacecrafts.get(position).getStock());
                                    qty.setText(spacecrafts.get(position).getQty());

    //                            Toast.makeText(getActivity(), "order:" + formNama.getText().toString() + "stockmo " + coba.getStock() + " qtymo " + coba.getQty(), Toast.LENGTH_LONG).show();


                                    boolean check = false;
                                    boolean add = true;

                                    for (int x = 0; x < Globalputri.kode.size(); x++) {
                                        if (finalFormKode.getText().toString().equals(Globalputri.kode.get(x))) {
                                            check = true;
                                        }
                                        if (check) {
                                            Globalputri.id_produk.set(x, coba.getId());
                                            Globalputri.kode.set(x, finalFormKode.getText().toString());
                                            Globalputri.nama.set(x, formNama.getText().toString());
                                            Globalputri.harga.set(x, formHarga.getText().toString());
                                            Globalputri.stock.set(x, formStock.getText().toString());
                                            Globalputri.qty.set(x, formQty.getText().toString());
                                            Globalputri.kategori.set(x, coba.getCategory());
                                            Globalputri.sgtorder.set(x, "0");
                                            check = false;
                                            add = false;
                                        }

                                    }
                                    if (add) {
                                        Globalputri.produk.add(Globalputri.produkCount, kumpulanorder);
                                        Globalputri.id_produk.add(coba.getId());
                                        Globalputri.kode.add(finalFormKode.getText().toString());
                                        Globalputri.nama.add(formNama.getText().toString());
                                        Globalputri.harga.add(formHarga.getText().toString());
                                        Globalputri.stock.add(formStock.getText().toString());
                                        Globalputri.qty.add(formQty.getText().toString());
                                        Globalputri.kategori.add(coba.getCategory());
                                        Globalputri.sgtorder.add("0");
                                        Globalputri.produkCount++;
                                    }

                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                            }

                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });

        Button orderbutton = view.findViewById(R.id.order_buttonAdditionalPutri);
        orderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("ID", orderedID);
                bundle.putStringArrayList("kodemo", orderedkode);
                bundle.putStringArrayList("namamo", orderedname);
                bundle.putStringArrayList("hargamo", orderedprice);
                bundle.putStringArrayList("stok", orderedstock);
                bundle.putStringArrayList("kuantitas", orderedqty);
                Intent intent = new Intent(getActivity().getBaseContext(), RingkasanPutriActivity.class);
                intent.putExtra("listorder", bundle);
                startActivity(intent);
            }
        });

//        Button checkbutton = (Button) view.findViewById(R.id.check_buttonAdditionalWardah);
//        checkbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String all = "";
//                String txt = "";
//                for (int j = 0; j < orderedID.size(); j++){
//                    txt = "\n" + orderedname.get(j) + ",\n stk: " + orderedstock.get(j) + ",\t qtymo: " + orderedqty.get(j) + "price: " +orderedprice.get(j)+ "\n";
//                    all = all + txt;
//                }
//                Toast.makeText(getActivity(), "Order: \n" + all, Toast.LENGTH_LONG).show();
//            }
//        });

        return view;
    }

    private boolean isInteger(String s) {
        Log.d("DEBUG SEARCHING", "query string : "+s);
        try{
            int testInt = Integer.parseInt(s);
            Log.d("DEBUG SEARCHING", "query int : "+testInt);
        } catch(NumberFormatException nfe) {
            Log.d("DEBUG SEARCHING", "not integer");
            return false;
        }
        Log.d("DEBUG SEARCHING", "integer");
        return true;
    }

    /*
     Our data object
     */
    static class FilterHelper extends Filter implements Serializable {
        ArrayList<com.example.salesforcemanagement.Spacecraft> currentList;
        ListViewAdapter adapter;
        Context c;
        int stateSearch;

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
                com.example.salesforcemanagement.Spacecraft spacecraft = null;
//ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    spacecraft = currentList.get(i);
//SEARCH
                    switch (stateSearch) {
                        case 1:
                            Log.d("DEBUG SEARCHING", "query state barcode : " + constraint);
                            if (spacecraft.getBarcode().toUpperCase().contains(constraint)) {
                                foundFilters.add(spacecraft);
                            }
                            break;

                        case 2:
                            Log.d("DEBUG SEARCHING", "query state kode odoo : " + constraint);
                            if (spacecraft.getKodeodoo().toUpperCase().contains(constraint)) {
                                foundFilters.add(spacecraft);
                            }
                            break;

                        case 3:
                            if (spacecraft.getFuzzyMatchStatus().toUpperCase().contains(constraint)) {
                                //ADD IF FOUND
                                Log.d("DEBUG SEARCHING", "query state text : " + constraint);
                                foundFilters.add(spacecraft);
                            }
                            break;
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

        public void setStateSearch(int state) {
            Log.d("DEBUG SEARCHING", "state FilterHelper : "+state);
            stateSearch = state;
        }
    }

//    static class ViewHolder implements Serializable {
//        TextView product_odoo;
//        TextView product_name;
//        TextView product_price;
//        TextView product_ws;
//        TextView product_stock;
//        TextView product_qty;
//    }

    /*
    Our custom adapter class
    */
    public class ListViewAdapter extends BaseAdapter implements Filterable, Serializable {
        public ArrayList<com.example.salesforcemanagement.Spacecraft> currentList;
        Context c;
        ArrayList<com.example.salesforcemanagement.Spacecraft> spacecrafts;
        FilterHelper filterHelper;
        Dialog dialog;
        int state;

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
            if ((i + 1) % 6 == 4 || (i + 1) % 6 == 5 || (i + 1) % 6 == 0)
//            if (i % 2 == 0)
            {
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

        public void setFilterHelperState(int stateSearching) {
            this.getFilter();
            Log.d("DEBUG SEARCHING", "state ListViewAdapter: "+state);
            filterHelper.setStateSearch(state);
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

            pref = getActivity().getSharedPreferences("TokoPref", 0);
            editor = pref.edit();
            final String customer = pref.getString("ref", "");
            final String partnerid = pref.getString("partner_id", "0");
            final ArrayList<com.example.salesforcemanagement.Spacecraft> listEBP = dbEBP.getAllProdukToko(partnerid, "brand:Putri");
            for (int i = 0; i < listEBP.size(); i++) {
//                sc = dbEBP.getProduk(i);
//                if (sc != null && sc.getBrand().contains("Wardah") && sc.getPartner_id().equals(partnerid)){
//                    listEBP.add(sc);
////                    Log.e("LIST NPD", listEBP.get(i).getKodeodoo() + " - " +listEBP.get(i).getNamaproduk() + " - " +listEBP.get(i).getBrand()+ " - " +listEBP.get(i).getPartner_id());
//
//                }
////                listEBP.add(dbEBP.getProdukToko(i, partnerid, "brand:Wardah"));
                Log.e("LIST MHS", listEBP.get(i).getKodeodoo() + " - " + listEBP.get(i).getNamaproduk() + " - " + listEBP.get(i).getBrand() + " - " + listEBP.get(i).getPartner_id());
            }
            Log.e("SIZE LIST MHS", "" + listEBP.size());
            String url = "https://sfa-api.pti-cosmetics.com/v_product_mhs?brand=ilike.*putri&partner_ref=ilike.*" + customer;
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
                                    String cat = jo.getString("category");
                                    int ws = jo.getInt("weekly_qty");
                                    String unit = jo.getString("unit");
//                                    String imageURL=jo.getString("imageurl");
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
//                                    s.setImageURL(imageURL);
//                                    s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);
                                    downloadedData.add(s);
                                }
                                myProgressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                myProgressBar.setVisibility(View.GONE);
//                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("CANT PARSE JSON", e.getMessage());
                                com.example.salesforcemanagement.Spacecraft EBP;
                                for (com.example.salesforcemanagement.Spacecraft produk : listEBP) {
                                    Log.e("ID", "" + produk.getId() + ", Kode: " + produk.getKodeodoo() + ", ");
//                                    if ((produk.getBrand().equals("brand:Make Over")) && (produk.getPartner_id().equals(partnerid))){

                                    Log.e("MHS OFFLINE", "MAKE OVER");
                                    EBP = new com.example.salesforcemanagement.Spacecraft();
                                    EBP.setId(produk.getId());
                                    EBP.setKoli(produk.getKoli());
                                    EBP.setKodeodoo(produk.getKodeodoo());
                                    EBP.setNamaproduk(produk.getNamaproduk());
                                    EBP.setCategory(produk.getCategory());
                                    EBP.setPrice(produk.getPrice());
                                    EBP.setBarcode(produk.getBarcode());
                                    EBP.setWeeklySales(produk.getWeeklySales());
                                    EBP.setStock(produk.getStock());
                                    EBP.setQty(produk.getQty());
                                    downloadedData.add(EBP);

//                                    if (String.valueOf(produk.getPartner_id()) == partnerid){
//                                        downloadedData.add(EBP);
//                                        Log.e("DATA OFFLINE", "ADDED");
//                                    }

//                                    } else {
//                                        Log.e("MHS OFFLINE", "NOT ADDED");
//                                    }
                                }
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);
//                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Error", anError.getMessage());
                            com.example.salesforcemanagement.Spacecraft EBP;
                            for (com.example.salesforcemanagement.Spacecraft produk : listEBP) {
                                Log.e("ID", "" + produk.getId() + ", Kode: " + produk.getKodeodoo() + ", ");
//                                if ((produk.getBrand().equals("brand:Make Over")) && (produk.getPartner_id().equals(partnerid))){

                                Log.e("MHS OFFLINE", "MAKE OVER");
                                EBP = new com.example.salesforcemanagement.Spacecraft();
                                EBP.setId(produk.getId());
                                EBP.setKoli(produk.getKoli());
                                EBP.setKodeodoo(produk.getKodeodoo());
                                EBP.setNamaproduk(produk.getNamaproduk());
                                EBP.setCategory(produk.getCategory());
                                EBP.setPrice(produk.getPrice());
                                EBP.setBarcode(produk.getBarcode());
                                EBP.setWeeklySales(produk.getWeeklySales());
                                EBP.setStock(produk.getStock());
                                EBP.setQty(produk.getQty());
                                downloadedData.add(EBP);

//                                    if (String.valueOf(produk.getPartner_id()) == partnerid){
//                                        downloadedData.add(EBP);
//                                        Log.e("DATA OFFLINE", "ADDED");
//                                    }

//                                } else {
//                                    Log.e("MHS OFFLINE", "NOT ADDED");
//                                }
                            }
                        }
                    });
            return downloadedData;
        }
    }

}
