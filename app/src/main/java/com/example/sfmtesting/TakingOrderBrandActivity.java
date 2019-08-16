package com.example.sfmtesting;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.sfmtesting.R;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TakingOrderBrandActivity extends AppCompatActivity {


    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_order_brand);

        isStoragePermissionGranted();
        TextView storename = findViewById(R.id.nameToko);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();

        storename.setText(prefToko.getString("partner_name", "0"));

        LinearLayout wardah = findViewById(R.id.wardah);
        LinearLayout makeover = findViewById(R.id.makeover);
        LinearLayout emina = findViewById(R.id.emina);
        LinearLayout putri = findViewById(R.id.putri);
        Button selesai = findViewById(R.id.button_simpan);
//        Button tunda = findViewById(R.id.button_tunda);

        wardah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String brand;
//                brand = v.getResources().getResourceName(v.getId());
//                Log.e("tes", brand);
                Intent intent = new Intent(TakingOrderBrandActivity.this, HistoricalSalesWardahActivity.class);
                startActivity(intent);
            }
        });
        makeover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakingOrderBrandActivity.this, HistoricalSalesMOActivity.class);
                startActivity(intent);
            }
        });
        emina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakingOrderBrandActivity.this, HistoricalSalesEminaActivity.class);
                startActivity(intent);
            }
        });
        putri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakingOrderBrandActivity.this, HistoricalSalesPutriActivity.class);
                startActivity(intent);
            }
        });
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TakingOrderBrandActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.form_notec, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Informasi");

                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TakingOrderBrandActivity.this, TakingOrderBrandActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TakingOrderBrandActivity.this, ReasonNotECActivitty.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
//        tunda.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(TakingOrderBrandActivity.this);
//                LayoutInflater inflater = getLayoutInflater();
//                View dialogView = inflater.inflate(R.layout.form_tunda, null);
//                dialog.setView(dialogView);
//                dialog.setCancelable(true);
//                dialog.setTitle("Informasi");
//
//                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(TakingOrderBrandActivity.this, TakingOrderBrandActivity.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//                    }
//                });
//                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(TakingOrderBrandActivity.this, ReasonActivity.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }
}
