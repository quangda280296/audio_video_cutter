package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class LibraryViewPager extends FragmentStatePagerAdapter {

    private static int NUM_ITEMS = 2;
    private static String[] TITLE_ITEMS = {"Musics", "Videos"};

    public LibraryViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MusicFragment.newInstance();
            case 1:
                return VideoFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TITLE_ITEMS[position];
    }
}
