package com.example.barcode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Domain.ScanScreenFragment;
import com.Domain.RootScreenFragment;

/**
 * Created by Luke on 10/09/2014.
 */
public class RootScreenPagerAdapter extends FragmentPagerAdapter {
    public RootScreenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return new RootScreenFragment();

            default:
                // The other sections of the app are dummy placeholders.
                Fragment fragment = new ScanScreenFragment();
                Bundle args = new Bundle();
                args.putInt(ScanScreenFragment.ARG_SECTION_NUMBER, i + 1);
                fragment.setArguments(args);
                return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Section " + (position + 1);
    }

}
