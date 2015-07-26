package com.blackMonster.webkiosk.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.blackMonster.webkiosk.ui.TimetableListFragment;
import com.blackMonster.webkioskApp.R;

import java.util.Calendar;

/**
 * Created by akshansh on 26/07/15.
 */
//TODO understand and document it.
public class TimetablePageAdapter extends FragmentStatePagerAdapter implements
        ViewPager.OnPageChangeListener {

    public static final int PAGER_CHILD_COUNT = 100;    //Hack to create circular view pager effect.
    private static final int WORKING_DAYS_IN_WEEK = 6;
    private Context context;


    ViewPager viewPager;
    String[] daysOfWeek = context.getResources().getStringArray(
            R.array.days_of_week);

    public TimetablePageAdapter(Context context, FragmentManager fm, ViewPager viewPager) {
        super(fm);
        this.context = context;
        this.viewPager = viewPager;

    }

    @Override
    public Fragment getItem(int i) {

        i = i % WORKING_DAYS_IN_WEEK;   //This creates cycle effect.
        Fragment fragment = new TimetableListFragment();
        Bundle args = new Bundle();
        args.putInt(TimetableListFragment.ARG_DAY, i + 2); // "i+2" because monday is 2 in Calender.Monday
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        TimetableListFragment fragment = (TimetableListFragment) object;

        if (fragment.CURRENT_DAY == viewPager.getCurrentItem()
                % WORKING_DAYS_IN_WEEK + Calendar.MONDAY) {
            try {
                fragment.updateThisFragment();
            } catch (Exception e) {
                e.printStackTrace();
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }

        return POSITION_NONE;
    }

    @Override
    public int getCount() {

        return PAGER_CHILD_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        position = position % WORKING_DAYS_IN_WEEK;
        switch (position) {
            case 0:
                return daysOfWeek[0];
            case 1:
                return daysOfWeek[1];
            case 2:
                return daysOfWeek[2];
            case 3:
                return daysOfWeek[3];
            case 4:
                return daysOfWeek[4];
            case 5:
                return daysOfWeek[5];
            default:
                return context.getString(R.string.unknown);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position,
                               Object object) {
        super.setPrimaryItem(container, 3, object);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {

        if (position == 0) {
            viewPager.setCurrentItem(PAGER_CHILD_COUNT - 2, false);
        } else if (position == PAGER_CHILD_COUNT - 1) {
            viewPager.setCurrentItem(1, false);
        }
    }
}
