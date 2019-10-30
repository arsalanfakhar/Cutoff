package com.example.techtik.cuttoff.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.techtik.cuttoff.Fragments.ComfortFragment;
import com.example.techtik.cuttoff.Fragments.ContactListFragment;
import com.example.techtik.cuttoff.Fragments.HomeFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:

                return new HomeFragment();

            case 1:

                return new ComfortFragment();

            case 2:

                return new ContactListFragment();


            default:

                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
