package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReturBrandActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur_brand);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = getApplicationContext().getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
        TextView storename = findViewById(R.id.nameRetur);
        storename.setText(prefToko.getString("partner_name", "0"));
//        Log.e("nama toko : ", storename.getText());
        LinearLayout wardah = findViewById(R.id.returwardah);
        LinearLayout emina = findViewById(R.id.returemina);
        LinearLayout makeover = findViewById(R.id.returmakeover);
        LinearLayout putri = findViewById(R.id.returputri);
        Button simpan = findViewById(R.id.button_retur);

        wardah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturBrandActivity.this, ReturWardahActivity.class);
                startActivity(intent);
            }
        });
        emina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturBrandActivity.this, ReturEminaActivity.class);
                startActivity(intent);
            }
        });
        makeover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturBrandActivity.this, ReturMOActivity.class);
                startActivity(intent);
            }
        });
        putri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturBrandActivity.this, ReturPutriActivity.class);
                startActivity(intent);
            }
        });
    }
}
