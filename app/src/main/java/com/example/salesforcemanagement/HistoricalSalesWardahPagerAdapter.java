package com.example.salesforcemanagement;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricalSalesWardahPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public HistoricalSalesWardahPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new AdditionalWardahFragment();
            case 2:
                return new com.example.salesforcemanagement.NPDWardahFragment();
            case 0:
                return new com.example.salesforcemanagement.ListHistWardahFragment();
            case 3:
                return new OthersWardahFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
