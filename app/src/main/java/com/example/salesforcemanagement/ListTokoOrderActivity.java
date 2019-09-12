package com.example.salesforcemanagement;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        DatabaseStoreOrder dbOrder = new DatabaseStoreOrder(ListTokoOrderActivity.this);
        ArrayList<StoreOrderList> listOrder = dbOrder.getAllProduk();
//        for (int i = 0; i < listOrder.size(); i++) {
//            Log.e("Database_" + i, listOrder.get(i).getNama_toko());
//        }
        ListView produk = (ListView) findViewById(R.id.list_toko_order);
        ListViewAdapter adapter = new ListViewAdapter(ListTokoOrderActivity.this, listOrder);
        produk.setAdapter(adapter);

        for (int i = 0; i < listOrder.size(); i++) {
//            Log.e("Database_" + i, listOrder.get(i).getNama_toko());
            int do_id = listOrder.get(i).getDo_id();
            String do_name = listOrder.get(i).getReference();
            int finalI = i;
//            Log.e("doid", ""+do_id);
            AndroidNetworking.get("http://10.3.181.177:3000/delivery_order?reference=eq." + do_name)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;

//                            boolean complete = false;
                            try {
                                Log.e("LINK", "http://10.3.181.177:3000/delivery_order?reference=eq." + do_name);
//                                for (int j = 0; j < response.length(); j++) {
                                jo = response.getJSONObject(0);
                                boolean status = jo.getBoolean("complete");
//                                    Log.e("Complete", status+" - "+listOrder.get(finalI).getNama_toko());
                                listOrder.get(finalI).setComplete(status);
//                                    Log.e("Hasil", listOrder.get(finalI).getComplete()+"!!");
                                adapter.notifyDataSetChanged();
//                                }

                            } catch (JSONException e) {
                                Log.e("JSON Parsing Error", e.getMessage());
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Log.e("Error", ""+anError.getErrorDetail());
                        }
                    });
        }

        pref = getApplicationContext().getSharedPreferences("SystemPref", 0);
        editor = pref.edit();

        Button refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
                adapter.refresh();
            }
        });
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
//            if (i % 2 == 0)
//            {
//                holder.product_layout.setBackgroundColor(Color.rgb(245, 245, 245));
//            } else {
//                holder.product_layout.setBackgroundColor(Color.rgb(255, 255, 255));
//            }
            final StoreOrderList op = (StoreOrderList) this.getItem(i);

//            String ref = pref.getString("ref", "");

//            if (!ref.equals(op.getReference())) {
            holder.product_ref.setText(op.getReference());
            holder.product_name.setText(op.getNama_toko());
            holder.product_brand.setText(op.getBrand_produk());

            if (op.getComplete()){
                holder.product_layout.setBackgroundColor(Color.CYAN);
            } else {
                holder.product_layout.setBackgroundColor(Color.LTGRAY);
            }
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
                            editor.putString("visit_name", op.getVisit_ref());
                            editor.putInt("do_id", op.getDo_id());
                            editor.putString("ref", op.getReference());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ListTokoOrderActivity.this, VisitActivity.class);
        startActivity(intent);
    }
}

