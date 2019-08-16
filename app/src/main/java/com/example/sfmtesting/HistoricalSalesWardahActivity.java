package com.example.sfmtesting;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.sfmtesting.R;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


public class HistoricalSalesWardahActivity extends AppCompatActivity {

    SharedPreferences pref, prefToko;
    SharedPreferences.Editor editor, editorToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_sales_wardah);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarhistwardah);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pref = this.getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        prefToko = this.getSharedPreferences("TokoPref", 0);
        editorToko = prefToko.edit();
//        final String customer = pref.getString("customer_id", "");
        final String nama = prefToko.getString("partner_name", "");
//        toolbar.setTitle();
        toolbar.setSubtitle(nama);

        TabLayout tabLayout = findViewById(R.id.tab_layouthistoricalsales);

        tabLayout.addTab(tabLayout.newTab().setText("MHS"));
        tabLayout.addTab(tabLayout.newTab().setText("NPD & PROMO"));
        tabLayout.addTab(tabLayout.newTab().setText("EBP"));
        tabLayout.addTab(tabLayout.newTab().setText("OTHERS"));
//        tabLayout.addTab(tabLayout.newTab().setText("ADDITIONAL ORDER"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pagerhistoricalsales);
        final com.example.sfmtesting.HistoricalSalesWardahPagerAdapter adapter = new com.example.sfmtesting.HistoricalSalesWardahPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}
