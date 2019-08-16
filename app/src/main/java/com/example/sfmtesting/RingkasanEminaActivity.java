package com.example.sfmtesting;


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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sfmtesting.R;

import java.util.ArrayList;


public class RingkasanEminaActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    ArrayList<com.example.sfmtesting.Spacecraft> order = new ArrayList<com.example.sfmtesting.Spacecraft>();
    ListViewAdapter adapter;
    private GestureOverlayView gestureOverlayView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringkasan_emina);

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarhistemina);
        setSupportActionBar(toolbar);
        //ADD -> NAMA TOKO
        pref = this.getSharedPreferences("TokoPref", 0);
        editor = pref.edit();
        final String nama = pref.getString("partner_name", "");
        String catatan = pref.getString("notesemina", "");

        final ListView listView = findViewById(R.id.mlistRingkasanEmina);

        for (int k = 0; k < Globalemina.kode.size(); k++) {
            com.example.sfmtesting.Spacecraft coba = new com.example.sfmtesting.Spacecraft();
            coba.setId(Globalemina.id_produk.get(k));
            coba.setKodeodoo(Globalemina.kode.get(k));
            coba.setNamaproduk(Globalemina.nama.get(k));
            coba.setPrice(Globalemina.harga.get(k));
            coba.setStock(Globalemina.stock.get(k));
            coba.setQty(Globalemina.qty.get(k));
            coba.setCategory(Globalemina.kategori.get(k));

            order.add(coba);
        }

        Globalemina.id_produk.clear();
        Globalemina.kode.clear();
        Globalemina.nama.clear();
        Globalemina.harga.clear();
        Globalemina.stock.clear();
        Globalemina.qty.clear();
        Globalemina.kategori.clear();

        adapter = new ListViewAdapter(order, getBaseContext());

        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getCount(); i++) {
            Log.e("Adapter_" + i, adapter.orderan.get(i).namaproduk + adapter.orderan.get(i).price);
        }
        adapter.notifyDataSetChanged();
//            Toast.makeText(this, "Order: \n" + all + "\n" + adapter.getCount(), Toast.LENGTH_LONG).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RingkasanEminaActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.form_takingorder, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Input Order");

                final TextView formKode;
                final TextView formNama;
                final TextView formHarga;
                final EditText formStock, formQty;

                formKode = dialogView.findViewById(R.id.kode_odoo_form);
                formNama = dialogView.findViewById(R.id.nama_produk_form);
                formHarga = dialogView.findViewById(R.id.harga_form);
                formStock = dialogView.findViewById(R.id.stock_form);
                formQty = dialogView.findViewById(R.id.qty_form);

                final com.example.sfmtesting.Spacecraft coba = (com.example.sfmtesting.Spacecraft) adapter.getItem(position);

                formKode.setText(coba.getKodeodoo());
                formNama.setText(coba.getNamaproduk());
                formHarga.setText(coba.getPrice());
                formStock.setText(coba.getStock());
                formQty.setText(coba.getQty());

                dialog.setPositiveButton("Ganti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mStock = formStock.getText().toString();
                        String mQty = formQty.getText().toString();

                        order.get(position).setStock(mStock);
                        order.get(position).setQty(mQty);

                        adapter.notifyDataSetChanged();
                        listView.invalidateViews();
                        listView.refreshDrawableState();

                        int skug = adapter.getCount();
                        Log.e("sku", "" + skug);
                        int itemg = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int itemig = Integer.parseInt(order.get(i).getQty());
                            itemg += itemig;
                        }
                        Log.e("item", "" + itemg);
                        int orderingg = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int orderingig = Integer.parseInt(String.valueOf(Integer.parseInt(order.get(i).getPrice()) * Integer.parseInt(order.get(i).getQty())));
                            orderingg += orderingig;
                        }
                        Log.e("ordering", "" + orderingg);

                        TextView totalsku = findViewById(R.id.totalskuemina);
                        totalsku.setText(String.valueOf(skug));
                        com.example.sfmtesting.StatusToko.skuemina = skug;
                        Globalemina.totalsku = skug;

                        TextView totalitem = findViewById(R.id.totalitememina);
                        totalitem.setText(String.valueOf(itemg));
                        com.example.sfmtesting.StatusToko.qtyemina = itemg;
                        Globalemina.totalitem = itemg;

                        TextView totalorder = findViewById(R.id.totalorderemina);
                        totalorder.setText(String.valueOf(orderingg));
                        com.example.sfmtesting.StatusSR.eminaAch = orderingg;
                        Globalemina.totalorder = orderingg;
                    }
                });
                dialog.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order.remove(position);

                        adapter.notifyDataSetChanged();
                        listView.invalidateViews();
                        listView.refreshDrawableState();

                        int skuh = adapter.getCount();
                        Log.e("sku", "" + skuh);
                        int itemh = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int itemih = Integer.parseInt(order.get(i).getQty());
                            itemh += itemih;
                        }
                        Log.e("item", "" + itemh);
                        int orderingh = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            int orderingih = Integer.parseInt(String.valueOf(Integer.parseInt(order.get(i).getPrice()) * Integer.parseInt(order.get(i).getQty())));
                            orderingh += orderingih;
                        }
                        Log.e("ordering", "" + orderingh);
                        TextView totalsku = findViewById(R.id.totalskuemina);
                        totalsku.setText(String.valueOf(skuh));
                        com.example.sfmtesting.StatusToko.skuemina = skuh;
                        Globalemina.totalsku = skuh;

                        TextView totalitem = findViewById(R.id.totalitememina);
                        totalitem.setText(String.valueOf(itemh));
                        com.example.sfmtesting.StatusToko.qtyemina = itemh;
                        Globalemina.totalitem = itemh;

                        TextView totalorder = findViewById(R.id.totalorderemina);
                        totalorder.setText(String.valueOf(orderingh));
                        com.example.sfmtesting.StatusSR.eminaAch = orderingh;
                        Globalemina.totalorder = orderingh;
                    }
                });

                dialog.show();
            }
        });

        int sku = adapter.getCount();
        Log.e("sku", "" + sku);
        int item = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            int itemi = Integer.parseInt(order.get(i).getQty());
            item += itemi;
        }
        Log.e("item", "" + item);
        int ordering = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            int orderingi = Integer.parseInt(String.valueOf(Integer.parseInt(order.get(i).getPrice()) * Integer.parseInt(order.get(i).getQty())));
            ordering += orderingi;
        }
        Log.e("ordering", "" + ordering);

        TextView totalsku = findViewById(R.id.totalskuemina);
        totalsku.setText(String.valueOf(sku));
        com.example.sfmtesting.StatusToko.skuemina = sku;
        Globalemina.totalsku = sku;

        TextView totalitem = findViewById(R.id.totalitememina);
        totalitem.setText(String.valueOf(item));
        com.example.sfmtesting.StatusToko.qtyemina = item;
        Globalemina.totalitem = item;

        TextView totalorder = findViewById(R.id.totalorderemina);
        totalorder.setText(String.valueOf(ordering));
        com.example.sfmtesting.StatusSR.eminaAch = ordering;
        Globalemina.totalorder = ordering;

        EditText notes = findViewById(R.id.notesemina);
        notes.setText(catatan);
        Button simpan = findViewById(R.id.simpanorderemina);
        int finalOrdering = ordering;
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RingkasanEminaActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.form_konfirmasi, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Konfirmasi");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.example.sfmtesting.StatusToko.sku += Integer.parseInt(totalsku.getText().toString());
                        com.example.sfmtesting.StatusToko.itemqty += Integer.parseInt(totalitem.getText().toString());
                        com.example.sfmtesting.StatusSR.subtotal += finalOrdering;

                        for (int g = 0; g < order.size(); g++){
                            Globalemina.id_produk.add(order.get(g).getId());
                            Globalemina.kode.add(order.get(g).getKodeodoo());
                            Globalemina.nama.add(order.get(g).getNamaproduk());
                            Globalemina.harga.add(order.get(g).getPrice());
                            Globalemina.stock.add(order.get(g).getStock());
                            Globalemina.qty.add(order.get(g).getQty());
                            if (order.get(g).getCategory().equals("null") || order.get(g).getCategory() == null){
                                Globalemina.kategori.add("20");
                            } else {
                                Globalemina.kategori.add(order.get(g).getCategory());
                            }
                            Log.e("Produk_"+g, Globalemina.id_produk.get(g)+ " - "+Globalemina.kategori.get(g)+" - "+Globalemina.kode.get(g)+" - "+Globalemina.nama.get(g)+", Stok : "+ Globalemina.stock.get(g)+", Qty : "+Globalemina.qty.get(g));
                        }
//                Saveorder search = new Saveorder(RingkasanWardahActivity.this, -1, token, subject_meeting,
//                        tanggal2, start, end,pic_meeting2,contact_pic2,weekly);
                        Globalemina.notes = notes.getText().toString();
                        editor.putString("notesemina", Globalemina.notes);
                        editor.commit();
                        Log.e("SIZE", ""+Globalemina.kode.size());
                        Intent intent = new Intent(RingkasanEminaActivity.this, com.example.sfmtesting.TakingOrderActivity.class);
                        startActivity(intent);
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
        for (int g = 0; g < order.size(); g++) {
            Globalemina.id_produk.add(order.get(g).getId());
            Globalemina.kode.add(order.get(g).getKodeodoo());
            Globalemina.nama.add(order.get(g).getNamaproduk());
            Globalemina.harga.add(order.get(g).getPrice());
            Globalemina.stock.add(order.get(g).getStock());
            Globalemina.qty.add(order.get(g).getQty());
            if (order.get(g).getCategory().equals("null") || order.get(g).getCategory() == null){
                Globalemina.kategori.add("20");
            } else {
                Globalemina.kategori.add(order.get(g).getCategory());
            }
            Log.e("Produk_" + g, Globalemina.id_produk.get(g) + " - " + Globalemina.kategori.get(g) + " - " + Globalemina.kode.get(g) + " - " + Globalemina.nama.get(g) + ", Stok : " + Globalemina.stock.get(g) + ", Qty : " + Globalemina.qty.get(g));
        }
        super.onBackPressed();
    }

    public class ListViewAdapter extends ArrayAdapter<com.example.sfmtesting.Spacecraft> {

        Context mContext;
        private ArrayList<com.example.sfmtesting.Spacecraft> orderan;

        public ListViewAdapter(ArrayList<com.example.sfmtesting.Spacecraft> ordered, Context context) {
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
            com.example.sfmtesting.Spacecraft listOrder = getItem(position);
            ViewHolder viewHolder;

            final View result;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.model_row_summary, parent, false);
                viewHolder.txtkode = convertView.findViewById(R.id.odoo_ringkasan);
                viewHolder.txtnama = convertView.findViewById(R.id.nama_ringkasan);
                viewHolder.txtharga = convertView.findViewById(R.id.harga_ringkasan);
                viewHolder.txtqty = convertView.findViewById(R.id.qty_ringkasan);
                viewHolder.txttotal = convertView.findViewById(R.id.jumlah_ringkasan);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.txtkode.setText(listOrder.getKodeodoo());
            viewHolder.txtnama.setText(listOrder.getNamaproduk());
            viewHolder.txtharga.setText(listOrder.getPrice());
            viewHolder.txtqty.setText(listOrder.getQty());
            String harga = viewHolder.txtharga.getText().toString();
            String qty = viewHolder.txtqty.getText().toString();
            int intharga = Integer.parseInt(harga);
            int intqty = Integer.parseInt(qty);
            int sub = intharga * intqty;
            viewHolder.txttotal.setText(String.valueOf(sub));

            return convertView;
        }

        private class ViewHolder {
            TextView txtkode, txtnama, txtharga, txtqty, txttotal;
        }
    }
//Signature


}
