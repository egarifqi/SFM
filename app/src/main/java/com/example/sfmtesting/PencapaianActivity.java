package com.example.sfmtesting;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.example.sfmtesting.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class PencapaianActivity extends AppCompatActivity {
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;
    TextView DisplayDateTime = null;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencapaian);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarpencapaian);
        setSupportActionBar(toolbar);
        DisplayDateTime = findViewById(R.id.tanggalsummaryvisit);

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
        Date = simpledateformat.format(calander.getTime());
        DisplayDateTime.setText(Date);

        TextView callTV = findViewById(R.id.call_statusvisit);
        TextView ecTV = findViewById(R.id.ec_statusvisit);
        TextView callplanTV = findViewById(R.id.call_planvisit);
        TextView notecTV = findViewById(R.id.not_ec_statusvisit);
        TextView penjualanTV = findViewById(R.id.penjualan_statusvisit);

        callTV.setText(String.valueOf(com.example.sfmtesting.StatusSR.totalCall));
        callplanTV.setText(String.valueOf(com.example.sfmtesting.StatusSR.callPlan));
        ecTV.setText(String.valueOf(com.example.sfmtesting.StatusSR.totalEC));
        notecTV.setText(String.valueOf(com.example.sfmtesting.StatusSR.totalNotEC));
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRP = NumberFormat.getCurrencyInstance(localeID);
        penjualanTV.setText(formatRP.format(com.example.sfmtesting.StatusSR.totalOrder));

    }


}
