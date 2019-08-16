package com.example.sfmtesting;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricalSalesEminaPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public HistoricalSalesEminaPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AdditionalEminaFragment();
            case 1:
                return new com.example.sfmtesting.NPDEminaFragment();
//            case 2: return new PromoEminaFragment();
            case 2:
                return new com.example.sfmtesting.ListHistEminaFragment();
            case 3:
                return new OthersEminaFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
