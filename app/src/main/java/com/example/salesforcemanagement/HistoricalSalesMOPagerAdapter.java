package com.example.salesforcemanagement;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricalSalesMOPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public HistoricalSalesMOPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new AdditionalMOFragment();
            case 2:
                return new com.example.salesforcemanagement.NPDMOFragment();
//            case 2: return new PromoMOFragment();
            case 0:
                return new com.example.salesforcemanagement.ListHistMOFragment();
            case 3:
                return new OthersMOFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
