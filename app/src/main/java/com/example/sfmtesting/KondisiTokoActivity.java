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

public class KondisiTokoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView titleNamaToko;
    RelativeLayout layoutPengajuan;
    RelativeLayout layoutKondisiShelving;
    RelativeLayout layoutReportCompetitor;

    SharedPreferences tokoPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kondisi_toko);

        titleNamaToko = findViewById(R.id.nama_toko);
        layoutPengajuan = findViewById(R.id.layout_pengajuan);
        layoutKondisiShelving = findViewById(R.id.layout_kondisi_shelving);
        layoutReportCompetitor = findViewById(R.id.layout_report_competitor);

        tokoPref = getApplicationContext().getSharedPreferences("TokoPref",0);
        String namaToko = tokoPref.getString("partner_name","");
        titleNamaToko.setText(namaToko);

        layoutReportCompetitor.setOnClickListener(this);
        layoutKondisiShelving.setOnClickListener(this);
        layoutPengajuan.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == layoutKondisiShelving){
            Toast.makeText(KondisiTokoActivity.this, "Kondisi Shelving ....", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(KondisiTokoActivity.this, ReportKondisiShelvingActivity.class));
        }

        if(view == layoutReportCompetitor){
            Toast.makeText(KondisiTokoActivity.this, "Report Competitor ....", Toast.LENGTH_SHORT).show();
        }

        if(view == layoutPengajuan){
            Toast.makeText(KondisiTokoActivity.this, "Pengajuan ....", Toast.LENGTH_SHORT).show();
        }
    }
}
