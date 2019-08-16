package com.example.sfmtesting;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class KunjunganPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public KunjunganPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DalamRuteFragment();
            case 1:
                return new com.example.sfmtesting.LuarRuteFragment();
            case 2:
                return new com.example.sfmtesting.TokoBaruFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
