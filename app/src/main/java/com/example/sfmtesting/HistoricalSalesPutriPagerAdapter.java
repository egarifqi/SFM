package com.example.sfmtesting;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricalSalesPutriPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public HistoricalSalesPutriPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AdditionalPutriFragment();
            case 1:
                return new com.example.sfmtesting.NPDPutriFragment();
            case 2:
                return new com.example.sfmtesting.ListHistPutriFragment();
            case 3:
                return new OthersPutriFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
