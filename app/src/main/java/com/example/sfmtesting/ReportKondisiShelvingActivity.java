package com.example.sfmtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sfmtesting.R;

public class ReportKondisiShelvingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView titleNamaToko;
    RelativeLayout layoutWinningAtStore;
    RelativeLayout layoutDoubleImplementation;

    SharedPreferences tokoPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_kondisi_shelving);

        titleNamaToko = findViewById(R.id.nama_toko);
        layoutWinningAtStore = findViewById(R.id.layout_winning_at_store);
        layoutDoubleImplementation = findViewById(R.id.layout_double_implementation);

        tokoPref = getApplicationContext().getSharedPreferences("TokoPref",0);
        String namaToko = tokoPref.getString("partner_name","");
        titleNamaToko.setText(namaToko);

        layoutWinningAtStore.setOnClickListener(this);
        layoutDoubleImplementation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == layoutDoubleImplementation){
            Toast.makeText(ReportKondisiShelvingActivity.this, "Double Implementation...", Toast.LENGTH_SHORT).show();
        }

        if (view == layoutWinningAtStore){
            startActivity(new Intent(ReportKondisiShelvingActivity.this, ReportWinningAtStoreActivity.class));
        }

    }
}
