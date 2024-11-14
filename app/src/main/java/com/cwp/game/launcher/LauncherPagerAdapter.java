

package com.cwp.game.launcher;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LauncherPagerAdapter extends FragmentPagerAdapter {
    public LauncherPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return LauncherPageFragment.NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        return LauncherPageFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return LauncherPageFragment.getPageTitle(position);
    }
}
