package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sfmtesting.scan.ScanreturwardahActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ReturWardahActivity extends AppCompatActivity {
    final Spacecraft kumpulanorder = new Spacecraft();
    final ArrayList<Spacecraft> order = new ArrayList<Spacecraft>();
    final ArrayList<Integer> orderedID = new ArrayList<Integer>();
    final ArrayList<String> orderedkode = new ArrayList<String>(); //kodemo
    final ArrayList<String> orderedname = new ArrayList<String>(); //namamo
    final ArrayList<String> orderedprice = new ArrayList<String>(); //hargamo
    final ArrayList<String> orderedstock = new ArrayList<String>(); //stockmo
    final ArrayList<String> orderedqty = new ArrayList<String>(); //qtymo
    final ArrayList<String> orderedcategory = new ArrayList<String>();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<Spacecraft> spacecrafts = new ArrayList<Spacecraft>();
    ListView myListView;
    ImageView scanreturwardah;
    public static SearchView mySearchView;
    ListViewAdapter adapter;
    private ArrayList<String> stock1 = new ArrayList<String>();
    private ArrayList<String> qty1 = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur_wardah);
        myListView = findViewById(R.id.mListReturWardah);
        final ProgressBar myProgressBar = findViewById(R.id.myProgressBarRetursWardah);
        scanreturwardah = findViewById(R.id.barcodeReturwardah);
        scanreturwardah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReturWardahActivity.this, ScanreturwardahActivity.class);
                startActivity(intent);
            }
        });
        mySearchView = findViewById(R.id.mySearchViewReturWardah
        );
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

        spacecrafts = new JSONDownloader(this).retrieve(myListView, myProgressBar);
        adapter = new ListViewAdapter(this, spacecrafts);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(ReturWardahActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.form_retur, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Input Retur");

                TextView formKode = null;
                final TextView formNama;
                final TextView formHarga;
                final TextView formPcs, formws;
                final EditText formStock, formQty;

                formKode = dialogView.findViewById(R.id.kode_odoo_form);
                formNama = dialogView.findViewById(R.id.nama_produk_form);
                formHarga = dialogView.findViewById(R.id.harga_form);
                formQty = dialogView.findViewById(R.id.qty_form1);
                formPcs = dialogView.findViewById(R.id.pcs_produk_form);

                final Spacecraft coba = (Spacecraft) adapter.getItem(position);

                String konsta = pref.getString("const", "2");
                final int konst = Integer.parseInt(konsta);

                formKode.setText(coba.getKodeodoo());
                formNama.setText(coba.getNamaproduk());
                formHarga.setText(coba.getPrice());
                formPcs.setText(coba.getKoli());

                final TextView finalFormKode = formKode;
                final int[] count = new int[1];
                dialog.setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mQty = formQty.getText().toString();
                        count[0] = 0;
                        if (mQty.isEmpty()){
                            Toast.makeText(getBaseContext(), "Mohon jangan kosongkan quantity retur", Toast.LENGTH_SHORT).show();
                        } else {

                            orderedID.add(coba.getId());
                            orderedkode.add(finalFormKode.getText().toString());
                            orderedname.add(formNama.getText().toString());
                            orderedprice.add(formHarga.getText().toString());
                            orderedqty.add(formQty.getText().toString());
                            orderedcategory.add(coba.getCategory());

                            kumpulanorder.setId(orderedID.get(count[0]));
                            kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                            kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                            kumpulanorder.setPrice(orderedprice.get(count[0]));
                            kumpulanorder.setQty(orderedqty.get(count[0]));
                            kumpulanorder.setCategory(orderedcategory.get(count[0]));

                            order.add(count[0], kumpulanorder);

                            count[0]++;

                            coba.setQty(formQty.getText().toString());

                            TextView qty = view.findViewById(R.id.qtyhist);

                            qty.setText(spacecrafts.get(position).getQty());

//                            Toast.makeText(getActivity(), "order:" + formNama.getText().toString() + "stockmo " + coba.getStock() + " qtymo " + coba.getQty(), Toast.LENGTH_LONG).show();


                            boolean check = false;
                            boolean add = true;

                            for (int x = 0; x < Global.kode.size(); x++) {
                                if (finalFormKode.getText().toString().equals(Global.kode.get(x))) {
                                    check = true;
                                }
                                if (check) {
                                    Global.id_produk.set(x, coba.getId());
                                    Global.kode.set(x, finalFormKode.getText().toString());
                                    Global.nama.set(x, formNama.getText().toString());
                                    Global.harga.set(x, formHarga.getText().toString());
                                    Global.qty.set(x, formQty.getText().toString());
                                    Global.kategori.set(x, coba.getCategory());
                                    Global.sgtorder.set(x,"0");
                                    check = false;
                                    add = false;
                                }

                            }
                            if (add) {
                                Global.produk.add(Global.produkCount, kumpulanorder);
                                Global.id_produk.add(coba.getId());
                                Global.kode.add(finalFormKode.getText().toString());
                                Global.nama.add(formNama.getText().toString());
                                Global.harga.add(formHarga.getText().toString());
                                Global.qty.add(formQty.getText().toString());
                                Global.kategori.add(coba.getCategory());
                                Global.sgtorder.add("0");
                                Global.produkCount++;
                            }

                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

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
        });

        Button orderbutton = findViewById(R.id.buttonReturWardah);
        orderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("ID", orderedID);
                bundle.putStringArrayList("kodemo", orderedkode);
                bundle.putStringArrayList("namamo", orderedname);
                bundle.putStringArrayList("hargamo", orderedprice);
                bundle.putStringArrayList("kuantitas", orderedqty);
                Intent intent = new Intent(ReturWardahActivity.this.getBaseContext(), RingkasanWardahActivity.class);
                intent.putExtra("listorder", bundle);
                startActivity(intent);
            }
        });
    }

    static class FilterHelper extends Filter implements Serializable {
        ArrayList<Spacecraft> currentList;
        ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<Spacecraft> currentList, ListViewAdapter adapter, Context c) {
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
                ArrayList<Spacecraft> foundFilters = new ArrayList<>();
                Spacecraft spacecraft = null;
//ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    spacecraft = currentList.get(i);
//SEARCH
                    if (spacecraft.getKodeodoo().toUpperCase().contains(constraint) ||
                            spacecraft.getNamaproduk().toUpperCase().contains(constraint) ||
                            spacecraft.getBarcode().toUpperCase().contains(constraint))  {
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
            adapter.setSpacecrafts((ArrayList<Spacecraft>) filterResults.values);
            adapter.refresh();
        }
    }

    /*
    Our custom adapter class
    */
    public class ListViewAdapter extends BaseAdapter implements Filterable, Serializable {
        Context c;
        ArrayList<Spacecraft> spacecrafts;
        public ArrayList<Spacecraft> currentList;
        FilterHelper filterHelper;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
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
                view = LayoutInflater.from(c).inflate(R.layout.model_row_retur, viewGroup, false);
                holder.cardView = (CardView) view.findViewById(R.id.cardview);
                holder.product_odoo = (TextView) view.findViewById(R.id.odoo_hist);
                holder.product_name = (TextView) view.findViewById(R.id.nama_hist);
                holder.product_price = (TextView) view.findViewById(R.id.harga_hist);
                holder.product_qty = (TextView) view.findViewById(R.id.qtyhist);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
                holder.product_odoo.setText("");
                holder.product_name.setText("");
                holder.product_price.setText("");
                holder.product_qty.setText("");
            }
//            if ((i+1) % 6 == 4 || (i+1) % 6 == 5 ||(i+1) % 6 == 0)
            if (i % 2 == 0)
            {
                holder.cardView.setBackgroundColor(Color.rgb(240, 240, 240));
            } else {
                holder.cardView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            final Spacecraft s = (Spacecraft) this.getItem(i);
            holder.product_odoo.setText(s.getKodeodoo());
            holder.product_name.setText(s.getNamaproduk());
            holder.product_price.setText(s.getPrice());
            holder.product_qty.setText(s.getQty());

            return view;
        }

        public void setSpacecrafts(ArrayList<Spacecraft> filteredSpacecrafts) {
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
        public ArrayList<Spacecraft> retrieve(final ListView mLpositiveistView, final ProgressBar myProgressBar) {
            final ArrayList<Spacecraft> downloadedData = new ArrayList<>();
            final DatabaseMHSHandler dbEBP = new DatabaseMHSHandler(getBaseContext());

            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);
            pref = getSharedPreferences("TokoPref", 0);
            editor = pref.edit();
            final String partnerid = pref.getString("partner_id", "0");
            final ArrayList<Spacecraft> listEBP = dbEBP.getAllProdukToko(partnerid, "brand:Wardah");
            for (int i=0; i<listEBP.size(); i++){
//                sc = dbEBP.getProduk(i);
//                if (sc != null && sc.getBrand().contains("Wardah") && sc.getPartner_id().equals(partnerid)){
//                    listEBP.add(sc);
////                    Log.e("LIST NPD", listEBP.get(i).getKodeodoo() + " - " +listEBP.get(i).getNamaproduk() + " - " +listEBP.get(i).getBrand()+ " - " +listEBP.get(i).getPartner_id());
//
//                }
////                listEBP.add(dbEBP.getProdukToko(i, partnerid, "brand:Wardah"));
                Log.e("LIST MHS", listEBP.get(i).getKodeodoo() + " - " +listEBP.get(i).getNamaproduk() + " - " +listEBP.get(i).getBrand()+ " - " +listEBP.get(i).getPartner_id());
            }
            Log.e("SIZE LIST MHS", ""+listEBP.size());
            final String customer = pref.getString("ref", "");
//            String barcode = pref.getString("barcode", "");
            String url = "https://sfa-api.pti-cosmetics.com/v_product_all?brand=ilike.*wardah&partner_ref=eq." + customer;
            Log.e("url", url);
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            Spacecraft s;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("id");
                                    String name = jo.getString("default_code");
                                    String propellant = jo.getString("name");
                                    String barcode = jo.getString("barcode");
                                    String price = jo.getString("price");
                                    String cat = jo.getString("category");
                                    String unit = jo.getString("unit");
//                                    String imageURL=jo.getString("imageurl");
                                    s = new Spacecraft();
                                    s.setId(id);
                                    s.setKoli(unit);
                                    s.setBarcode(barcode);
                                    s.setCategory(cat);
                                    s.setKodeodoo(name);
                                    s.setNamaproduk(propellant);
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
                                Spacecraft EBP;
                                for (Spacecraft produk : listEBP){
                                    Log.e("ID", ""+produk.getId()+", Kode: "+produk.getKodeodoo()+", ");
//                                    if ((produk.getBrand().equals("brand:Make Over")) && (produk.getPartner_id().equals(partnerid))){

                                    Log.e("MHS OFFLINE", "MAKE OVER");
                                    EBP = new Spacecraft();
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
                            Spacecraft EBP;
                            for (Spacecraft produk : listEBP){
                                Log.e("ID", ""+produk.getId()+", Kode: "+produk.getKodeodoo()+", ");
//                                if ((produk.getBrand().equals("brand:Make Over")) && (produk.getPartner_id().equals(partnerid))){

                                Log.e("MHS OFFLINE", "MAKE OVER");
                                EBP = new Spacecraft();
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
