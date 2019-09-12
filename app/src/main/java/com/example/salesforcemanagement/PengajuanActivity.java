package com.example.salesforcemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class PengajuanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan);
        RelativeLayout branding = findViewById(R.id.layout_branding);
        RelativeLayout listing = findViewById(R.id.layout_listing);
        RelativeLayout sewacounter = findViewById(R.id.layout_sewacounter);
        RelativeLayout merchandising = findViewById(R.id.layout_merchandising);

        branding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PengajuanActivity.this, BrandingActivity.class);
                startActivity(intent);
            }
        });
        listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PengajuanActivity.this, ListingFeeActivity.class);
                startActivity(intent);
            }
        });
        sewacounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PengajuanActivity.this, RentCounterActivity.class);
                startActivity(intent);
            }
        });
        merchandising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PengajuanActivity.this, MerchandisingActivity.class);
                startActivity(intent);
            }
        });
    }
}
