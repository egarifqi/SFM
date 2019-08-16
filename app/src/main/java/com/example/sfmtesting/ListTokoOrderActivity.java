package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class ListTokoOrderActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_toko_order);
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
        String namatoko = prefToko.getString("partner_name", "");
        TabLayout tabLayout = findViewById(R.id.taborderhist);

        tabLayout.addTab(tabLayout.newTab().setText("Order History"));
        DatabaseStoreOrder dbOrder = new DatabaseStoreOrder(ListTokoOrderActivity.this);
        ArrayList<StoreOrderList> listOrder = dbOrder.getAllProduk();
        for (int i = 0; i < listOrder.size(); i++) {
            Log.e("Database_" + i, listOrder.get(i).getNama_toko());
        }
        ListView produk = (ListView) findViewById(R.id.list_toko_order);
        ListViewAdapter adapter = new ListViewAdapter(ListTokoOrderActivity.this, listOrder);
        produk.setAdapter(adapter);

        pref = getApplicationContext().getSharedPreferences("SystemPref", 0);
        editor = pref.edit();
    }

    public class ViewHolderOrder implements Serializable {
        TextView product_ref;
        TextView product_name;
        TextView product_brand;
        LinearLayout product_layout;
    }

    public class ListViewAdapter extends BaseAdapter implements Serializable {
        Context c;
        ArrayList<StoreOrderList> orderedProducts;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<StoreOrderList> orderedProducts) {
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
                view = LayoutInflater.from(c).inflate(R.layout.model_row_toko_order, viewGroup, false);

                holder.product_layout = (LinearLayout) view.findViewById(R.id.toko_order_layout);
                holder.product_ref = (TextView) view.findViewById(R.id.toko_order_ref);
                holder.product_name = (TextView) view.findViewById(R.id.toko_order_nama);
                holder.product_brand = (TextView) view.findViewById(R.id.toko_order_brand);
                view.setTag(holder);
            } else {
                holder = (ViewHolderOrder) view.getTag();
                holder.product_ref.setText("");
                holder.product_name.setText("");
                holder.product_brand.setText("");
            }
//            if ((i+1) % 6 == 4 || (i+1) % 6 == 5 ||(i+1) % 6 == 0)

            final StoreOrderList op = (StoreOrderList) this.getItem(i);

//            String ref = pref.getString("ref", "");

//            if (!ref.equals(op.getReference())) {
            holder.product_ref.setText(op.getReference());
            holder.product_name.setText(op.getNama_toko());
            holder.product_brand.setText(op.getBrand_produk());
//            }
//            else {
//                holder.product_layout.setVisibility(View.GONE);
//            }

//            editor.putString("ref", op.getReference());
//            editor.commit();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ListTokoOrderActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle(op.getNama_toko());
                    dialog.setMessage("Apakah anda akan melihat produk-produk Brand "+op.getBrand_produk()+" yang diorder toko ini?");
                    dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt("do_id", op.getDo_id());
                            editor.commit();
                            Intent intent = new Intent(ListTokoOrderActivity.this, ListProdukDipesanActivity.class);
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

        public void refresh() {
            notifyDataSetChanged();
        }
    }
}

