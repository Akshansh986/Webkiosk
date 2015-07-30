package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateAvgAtnd;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.controller.RefreshFullDB;
import com.blackMonster.webkiosk.ui.adapters.TimetablePageAdapter;
import com.blackMonster.webkioskApp.R;

import java.util.Calendar;

public class TimetableActivity extends StartupActivity {
    public String TAG = "TimetableActivity";
    ViewPager mViewPager = null;

    private BroadcastReceiver broadcastUpdateAvgAtndResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            unanimateRefreshButton();

            int result = intent.getExtras().getInt(
                    RefreshFullDB.BROADCAST_UPDATE_AVG_ATND_RESULT);

            if (result == UpdateAvgAtnd.ERROR) {
                AlertDialogHandler.checkDialog(TimetableActivity.this);
            } else {
                makeToast(result);
                updateUI();
            }
        }

        private void makeToast(int result) {
            Toast.makeText(TimetableActivity.this,
                    result + " " + getString(R.string.temp_attendence_updated),
                    Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Received when new class is manually added to timetable.
     * It's only registered when user taps add new class from option menu and unregistered after adding new class.
     */
    private BroadcastReceiver broadcastAddClassDialog = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mViewPager.getAdapter().notifyDataSetChanged();
                LocalBroadcastManager.getInstance(getBaseContext())
                        .unregisterReceiver(broadcastAddClassDialog);
                broadcastAddClassDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_title_timetable));

        getLayoutInflater().inflate(R.layout.swipe_view, activityContent); //adding swipe view to empty container of base activity.

        ((PagerTabStrip) findViewById(R.id.pager_title_strip))
                .setTabIndicatorColor(getResources().getColor(R.color.theme));

        mViewPager = (ViewPager) findViewById(R.id.timetable_pager);

        mViewPager.setAdapter(new TimetablePageAdapter(this, getSupportFragmentManager(),
                mViewPager));

        mViewPager.setCurrentItem(54 + getDay() - Calendar.MONDAY);

        showOverLayIfFirstTime();

    }

    /**
     * Help menu shown on first login.
     */
    private void showOverLayIfFirstTime() {

        if (MainPrefs.isFirstTime(this)
                && StartupActivity.isStartupActivity(getClass(), this)) {
            getSupportActionBar().hide();
            final FrameLayout mFrame = (FrameLayout) findViewById(R.id.base_frame);
            final View tutView = LayoutInflater.from(getBaseContext()).inflate(
                    R.layout.help_overlay, null);
            mFrame.addView(tutView);
            tutView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    MainPrefs.setFirstTimeOver(getBaseContext());
                    mFrame.removeView(tutView);
                    getSupportActionBar().show();
                    return false;

                }

            });

        }

    }

    /**
     * Get current day of week.
     */
    private int getDay() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY)
            day = Calendar.MONDAY;    //To show monday timetable on sunday.
        return day;
    }

    @Override
    public void registerReceivers() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    broadcastUpdateAvgAtndResult,
                    new IntentFilter(
                            RefreshFullDB.BROADCAST_UPDATE_AVG_ATND_RESULT));
        }

        super.registerReceivers();
    }

    @Override
    public void unregisterReceivers() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    broadcastUpdateAvgAtndResult);
        }
        super.unregisterReceivers();
    }

    private void updateUI() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void inflateOnCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (TimetableDbHelper.databaseExists(this))
            inflater.inflate(R.menu.optionsmenu_timetable, menu); //Show add class to timetable option.
        else
            inflater.inflate(R.menu.mainmenu, menu);    //Hide add class to timetable option.
    }

    @Override
    boolean switchForOnOptionsItemSelected(int itemId) {

        //Shows  "Add class to timetable"  in options menu.
        switch (itemId) {

            case R.id.action_add_to_timetable:

                LocalBroadcastManager.getInstance(this).registerReceiver(
                        broadcastAddClassDialog,
                        new IntentFilter(AddClassDialog.BROADCAST_ADD_CLASS_DIALOG));

                DialogFragment dialogFragment = new AddClassDialog();

                dialogFragment.show(getSupportFragmentManager(), "timetable");
                return true;
            default:
                return super.switchForOnOptionsItemSelected(itemId); //For others option present in base activity.
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceivers();
        AlertDialogHandler.dismissIfPresent();
        unanimateRefreshButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshDBPrefs.resetIfrunningFromLongTime(this);
        updateUI();
        AlertDialogHandler.checkDialog(this);

        if (RefreshDBPrefs.isStatus(RefreshDBPrefs.LOGGING_IN, this)
                || RefreshDBPrefs.isStatus(
                RefreshDBPrefs.REFRESHING_O, this)) {
            animateRefreshButton();
            registerReceivers();
        }

    }


}
