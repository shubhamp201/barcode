package com.example.barcode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Domain.RootScreenFragment;

/**
 * Created by Luke on 10/09/2014.
 */
public class RootScreenPagerAdapter extends FragmentPagerAdapter
{
    public RootScreenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new RootScreenFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(RootScreenFragment.ARG_OBJECT, i + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
