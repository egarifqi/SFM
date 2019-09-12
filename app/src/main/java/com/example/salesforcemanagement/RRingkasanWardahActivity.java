package com.example.salesforcemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RRingkasanWardahActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    ArrayList<com.example.salesforcemanagement.Spacecraft> order = new ArrayList<com.example.salesforcemanagement.Spacecraft>();
    ListViewAdapter adapter;
    private GestureOverlayView gestureOverlayView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rringkasan_wardah);
        SharedPreferences pref;
        SharedPreferences.Editor editor;

        //ADD -> NAMA TOKO
        pref = this.getSharedPreferences("TokoPref", 0);
        editor = pref.edit();
        final String nama = pref.getString("partner_name", "");
        String catatan = pref.getString("noteswardah", "");

        ListView listView = (ListView)findViewById(R.id.mListViewRingkasanWardahr);

        for (int k = 0; k < Global.kode.size(); k++) {
            com.example.salesforcemanagement.Spacecraft coba = new com.example.salesforcemanagement.Spacecraft();
            coba.setId(Global.id_produk.get(k));
            coba.setKodeodoo(Global.kode.get(k));
            coba.setNamaproduk(Global.nama.get(k));
            coba.setPrice(Global.harga.get(k));
//            coba.setStock(Global.stock.get(k));
            coba.setQty(Global.qty.get(k));
            coba.setAlasan(Global.alasan.get(k));
            coba.setCategory(Global.kategori.get(k));

            order.add(coba);
        }

        Global.id_produk.clear();
        Global.kode.clear();
        Global.nama.clear();
        Global.harga.clear();
//        Global.stock.clear();
        Global.qty.clear();
        Global.alasan.clear();
        Global.kategori.clear();

        adapter = new ListViewAdapter(order, getBaseContext());

        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getCount(); i++) {
            Log.e("Adapter_" + i, adapter.orderan.get(i).namaproduk + adapter.orderan.get(i).price);
        }
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RRingkasanWardahActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.form_retur, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Input Order");

                final TextView formKode;
                final TextView formNama;
                final TextView formHarga;
                final EditText formQty;
//                final Spinner formAlasan;
                Spinner formAlasan = (Spinner) dialogView.findViewById(R.id.reason_form1);
                ArrayAdapter<String> adapterretur = new ArrayAdapter<String>(RRingkasanWardahActivity.this, android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.list_alasanretur));
                adapterretur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                formAlasan.setAdapter(adapterretur);
                formKode = dialogView.findViewById(R.id.hkode_odoo_form);
                formNama = dialogView.findViewById(R.id.hnama_produk_form);
                formHarga = dialogView.findViewById(R.id.hharga_form);
//                formAlasan = dialogView.findViewById(R.id.alasan_form);
                formQty = dialogView.findViewById(R.id.qty_form);

                final com.example.salesforcemanagement.Spacecraft coba = (com.example.salesforcemanagement.Spacecraft) adapter.getItem(position);

                formKode.setText(coba.getKodeodoo());
                formNama.setText(coba.getNamaproduk());
                formHarga.setText(coba.getPrice());
//                formAlasan.setText(coba.getAlasan());
                formQty.setText(coba.getQty());

                dialog.setPositiveButton("Ganti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mQty = formQty.getText().toString();

                        order.get(position).setQty(mQty);

                        adapter.notifyDataSetChanged();
                        listView.invalidateViews();
                        listView.refreshDrawableState();

                        int sku = adapter.getCount();
                        int item = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int itemi = Integer.parseInt(order.get(i).getQty());
                            item += itemi;
                        }
                        int ordering = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int orderingi = Integer.parseInt(String.valueOf(Integer.parseInt(order.get(i).getPrice()) * Integer.parseInt(order.get(i).getQty())));
                            ordering += orderingi;
                        }

                        Log.i("TOTAL", "Order: " + ordering);
                        Log.i("QTY", "Item: " + item);
                        Log.i("SKU", "SKU: " + sku);

                        TextView totalsku = findViewById(R.id.totalskur);
                        totalsku.setText(String.valueOf(sku));
                        com.example.salesforcemanagement.StatusToko.skuwardah = sku;
                        Global.totalsku = sku;

                        TextView totalitem = findViewById(R.id.totalitemr);
                        totalitem.setText(String.valueOf(item));
                        com.example.salesforcemanagement.StatusToko.qtywardah = item;
                        Global.totalitem = item;

                        TextView totalorder = findViewById(R.id.totalorderr);
                        totalorder.setText(String.valueOf(ordering));
                        com.example.salesforcemanagement.StatusSR.wardahAch = ordering;
                        Global.totalorder = ordering;
                    }
                });

                dialog.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order.remove(position);

                        adapter.notifyDataSetChanged();
                        listView.invalidateViews();
                        listView.refreshDrawableState();

                        int sku = adapter.getCount();
                        int item = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int itemi = Integer.parseInt(order.get(i).getQty());
                            item += itemi;
                        }
                        int ordering = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int orderingi = Integer.parseInt(String.valueOf(Integer.parseInt(order.get(i).getPrice()) * Integer.parseInt(order.get(i).getQty())));
                            ordering += orderingi;
                        }

                        Log.i("TOTAL", "Order: " + ordering);
                        Log.i("QTY", "Item: " + item);
                        Log.i("SKU", "SKU: " + sku);

                        TextView totalsku = findViewById(R.id.totalskur);
                        totalsku.setText(String.valueOf(sku));
                        com.example.salesforcemanagement.StatusToko.skuwardah = sku;
                        Global.totalsku = sku;

                        TextView totalitem = findViewById(R.id.totalitemr);
                        totalitem.setText(String.valueOf(item));
                        com.example.salesforcemanagement.StatusToko.qtywardah = item;
                        Global.totalitem = item;

                        TextView totalorder = findViewById(R.id.totalorderr);
                        totalorder.setText(String.valueOf(ordering));
                        com.example.salesforcemanagement.StatusSR.wardahAch = ordering;
                        Global.totalorder = ordering;
                    }
                });

                dialog.show();
            }
        });

        int sku = adapter.getCount();
        int item = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            int itemi = Integer.parseInt(order.get(i).getQty());
            item += itemi;
        }
        int ordering = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            int orderingi = Integer.parseInt(String.valueOf(Integer.parseInt(order.get(i).getPrice()) * Integer.parseInt(order.get(i).getQty())));
            ordering += orderingi;
        }

        Log.i("TOTAL", "Order: " + ordering);
        Log.i("QTY", "Item: " + item);
        Log.i("SKU", "SKU: " + sku);

        TextView totalsku = findViewById(R.id.totalskur);
        totalsku.setText(String.valueOf(sku));
        com.example.salesforcemanagement.StatusToko.skuwardah = sku;
        Global.totalsku = sku;

        TextView totalitem = findViewById(R.id.totalitemr);
        totalitem.setText(String.valueOf(item));
        com.example.salesforcemanagement.StatusToko.qtywardah = item;
        Global.totalitem = item;

        TextView totalorder = findViewById(R.id.totalorderr);
        totalorder.setText(String.valueOf(ordering));
        com.example.salesforcemanagement.StatusSR.wardahAch = ordering;
        Global.totalorder = ordering;

        FloatingActionButton add = findViewById(R.id.fabadd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RRingkasanWardahActivity.this, ReturWardahActivity.class);
                startActivity(intent);
            }
        });
//        EditText notes = findViewById(R.id.noteswardah);
//        notes.setText(catatan);
        FloatingActionButton simpan = findViewById(R.id.fabsend);
        int finalOrdering = ordering;
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(RRingkasanWardahActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.form_konfirmasi, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Konfirmasi");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.example.salesforcemanagement.StatusToko.sku += Integer.parseInt(totalsku.getText().toString());
                        com.example.salesforcemanagement.StatusToko.itemqty += Integer.parseInt(totalitem.getText().toString());
                        com.example.salesforcemanagement.StatusSR.subtotal += finalOrdering;

                        for (int g = 0; g < order.size(); g++){
                            Global.id_produk.add(order.get(g).getId());
                            Global.kode.add(order.get(g).getKodeodoo());
                            Global.nama.add(order.get(g).getNamaproduk());
                            Global.harga.add(order.get(g).getPrice());
                            Global.alasan.add(order.get(g).getAlasan());
                            Global.qty.add(order.get(g).getQty());
                            if (order.get(g).getCategory().equals("null") || order.get(g).getCategory() == null){
                                Global.kategori.add("20");
                            } else {
                                Global.kategori.add(order.get(g).getCategory());
                            }
                            Log.e("Produk_"+g, Global.id_produk.get(g)+ " - "+Global.kategori.get(g)+" - "+Global.kode.get(g)+" - "+Global.nama.get(g)+", Alasan : "+ Global.alasan.get(g)+", Qty : "+Global.qty.get(g));
                        }

                        Log.e("SIZE", ""+Global.kode.size());
//                        Global.notes = notes.getText().toString();
//                        editor.putString("noteswardah", Global.notes);
//                        editor.commit();
                        Intent intent = new Intent(RRingkasanWardahActivity.this, com.example.salesforcemanagement.ReturBrandActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.setNegativeButton("Belum", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        for (int g = 0; g < order.size(); g++){
            Global.id_produk.add(order.get(g).getId());
            Global.kode.add(order.get(g).getKodeodoo());
            Global.nama.add(order.get(g).getNamaproduk());
            Global.harga.add(order.get(g).getPrice());
            Global.alasan.add(order.get(g).getAlasan());
            Global.qty.add(order.get(g).getQty());
            if (order.get(g).getCategory().equals("null") || order.get(g).getCategory() == null){
                Global.kategori.add("20");
            } else {
                Global.kategori.add(order.get(g).getCategory());
            }
            Log.e("Produk_"+g, Global.id_produk.get(g)+ " - "+Global.kategori.get(g)+" - "+Global.kode.get(g)+" - "+Global.nama.get(g)+", Alasan : "+ Global.alasan.get(g)+", Qty : "+Global.qty.get(g));
        }
        super.onBackPressed();
    }

    //Signature


    public class ListViewAdapter extends ArrayAdapter<Spacecraft> {

        Context mContext;
        private ArrayList<Spacecraft> orderan;

        public ListViewAdapter(ArrayList<com.example.salesforcemanagement.Spacecraft> ordered, Context context) {
            super(context, R.layout.model_row_summary, ordered);
            this.orderan = ordered;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return orderan.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            com.example.salesforcemanagement.Spacecraft listOrder = getItem(position);
            ListViewAdapter.ViewHolder viewHolder;

            final View result;

            if (convertView == null) {
                viewHolder = new ListViewAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.model_row_rsummary, parent, false);
                viewHolder.txtkode = convertView.findViewById(R.id.odoo_ringkasanrw);
                viewHolder.txtnama = convertView.findViewById(R.id.nama_ringkasanrw);
                viewHolder.txtqty = convertView.findViewById(R.id.qty_ringkasanrw);
                viewHolder.txttotal = convertView.findViewById(R.id.total_ringkasanrw);
                viewHolder.txtalasan = convertView.findViewById(R.id.alasan_ringkasanrw);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ListViewAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.txtkode.setText(listOrder.getKodeodoo());
            viewHolder.txtnama.setText(listOrder.getNamaproduk());
            viewHolder.txtqty.setText(listOrder.getQty());
            String qty = viewHolder.txtqty.getText().toString();
            String harga = listOrder.getPrice();
            int intqty = Integer.parseInt(qty);
            int intharga = Integer.parseInt(harga);
            int sub = intharga * intqty;
            viewHolder.txttotal.setText(String.valueOf(sub));
            viewHolder.txtalasan.setText(listOrder.getAlasan());

            return convertView;
        }

        private class ViewHolder {
            TextView txtkode, txtnama, txtqty, txttotal, txtalasan;
        }
    }
//Signature


}
