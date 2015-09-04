package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.widget.ListView;
import android.widget.Toast;

import com.blackMonster.webkiosk.controller.RefreshBroadcasts;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.controller.RefreshStatus;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateDetailedAttendance;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendanceTable;
import com.blackMonster.webkiosk.ui.Dialog.RefreshDbErrorDialogStore;
import com.blackMonster.webkiosk.ui.adapters.DetailedAttendanceAdapter;
import com.blackMonster.webkioskApp.R;

public class DetailedAtndActivity extends BaseActivity {
    public static final String TAG = "DetailedAtndActivity";
    public static final String SUB_CODE = "subcode";
    public static final String SUB_NAME = "subname";
    private String subName;
    private String code;

    private DetailedAttendanceAdapter adapter;

    //Called when attempt to update detailed attendance is done.
    BroadcastReceiver broadcastUpdateDetailedAtndResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            unanimateRefreshButton();
            updateUI();

            int result = intent.getExtras().getInt(
                    RefreshBroadcasts.BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT);

            if (result == UpdateDetailedAttendance.ERROR) {
                RefreshDbErrorDialogStore.showDialogIfPresent(DetailedAtndActivity.this);
            } else {
                makeToast();
            }
        }

        private void makeToast() {
            Toast.makeText(DetailedAtndActivity.this,
                    getString(R.string.detailed_attendence_updated),
                    Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = getIntent().getExtras();
        code = extra.getString(SUB_CODE);
        subName = extra.getString(SUB_NAME);

        initActionBar();
        showListView();

    }

    private void showListView() {
        Cursor cursor = new DetailedAttendanceTable(
                code, 0, this).getData();
        ListView listView = new ListView(this);
        adapter = new DetailedAttendanceAdapter(this, cursor);
        listView.setAdapter(adapter);
        activityContent.removeAllViews();
        activityContent.addView(listView); //Adding list view to empty container of base activity.
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(subName);
        setActionBarSubtitle();
        openDrawerWithIcon(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setActionBarSubtitle() {
        putTimestampInSubtitle(RefreshDBPrefs.getDetailedAtndRefreshTimeStamp(this));
    }

    @Override
    public void registerReceivers() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager
                    .getInstance(this)
                    .registerReceiver(
                            broadcastUpdateDetailedAtndResult,
                            new IntentFilter(
                                    RefreshBroadcasts.BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT));
        }

        super.registerReceivers();      //Register receivers defined in super class.
    }

    @Override
    public void unregisterReceivers() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    broadcastUpdateDetailedAtndResult);
        }
        super.unregisterReceivers();   //Unregister receivers registered in super class.
    }

    private void updateUI() {
        setActionBarSubtitle();
        adapter.changeCursor(new DetailedAttendanceTable(
                        code, 0, this).getData());
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceivers();
        RefreshDbErrorDialogStore.dismissIfPresent();
        unanimateRefreshButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshDBPrefs.resetIfrunningFromLongTime(this);
        updateUI();
        RefreshDbErrorDialogStore.showDialogIfPresent(this);

        //Only show refresh animation till refreshing detailed attendance.
        if (RefreshDBPrefs.isStatus(RefreshStatus.LOGGING_IN, this)
                || RefreshDBPrefs.isStatus(RefreshStatus.REFRESHING_O, this)
                || RefreshDBPrefs.isStatus(RefreshStatus.REFRESHING_D, this)) {
            animateRefreshButton();
            registerReceivers();
        }

    }
}
