package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.controller.RefreshDB;
import com.blackMonster.webkiosk.controller.UpdateAvgAtnd;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.ui.adapters.AtndOverviewAdapter;
import com.blackMonster.webkioskApp.R;

public class AtndOverviewActivity extends StartupActivity implements
        OnItemClickListener {
    public String TAG = "AtndOverviewActivity";

    private Cursor cursor = null;
    private AtndOverviewAdapter adapter;
    private AttendenceOverviewTable atndOTable;

    private BroadcastReceiver broadcastUpdateAvgAtndResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            unanimateRefreshButton();
            updateUI();

            int result = intent.getExtras().getInt(
                    RefreshDB.BROADCAST_UPDATE_AVG_ATND_RESULT);

            if (result == UpdateAvgAtnd.ERROR) {
                AlertDialogHandler.checkDialog(AtndOverviewActivity.this);
            } else {
                makeToast(result);
            }
        }

        private void makeToast(int result) {
            Toast.makeText(
                    AtndOverviewActivity.this, result + " " +
                            getString(R.string.temp_attendence_updated),
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_title_attendence));
        setActionBarSubtitle();

        ListView listView = new ListView(this);
        atndOTable = new AttendenceOverviewTable(this);
        cursor = atndOTable.getData();
        if (cursor != null) {
            adapter = new AtndOverviewAdapter(this, this, cursor);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            activityContent.addView(listView);  //Adding list view to empty container of base activity.

        }

    }

    public void setActionBarSubtitle() {
        putTimestampInSubtitle(RefreshServicePrefs.getAvgAttendanceTimeStamp(this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long id) {
        cursor.moveToPosition(position);
        String code = cursor.getString(cursor
                .getColumnIndex(AttendenceOverviewTable.C_CODE));
        String subName = cursor.getString(cursor
                .getColumnIndex(AttendenceOverviewTable.C_NAME));
        Intent intent = new Intent(this, DetailedAtndActivity.class);
        intent.putExtra(DetailedAtndActivity.SUB_CODE, code);
        intent.putExtra(DetailedAtndActivity.SUB_NAME, subName);
        startActivity(intent);
    }

    @Override
    public void registerReceivers() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    broadcastUpdateAvgAtndResult,
                    new IntentFilter(RefreshDB.BROADCAST_UPDATE_AVG_ATND_RESULT));
        }
        super.registerReceivers(); //Register receivers defined in super class.
    }

    @Override
    public void unregisterReceivers() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    broadcastUpdateAvgAtndResult);
        }
        super.unregisterReceivers(); //Unregister receivers registered in super class.
    }

    private void updateUI() {
        setActionBarSubtitle();
        cursor.close();
        cursor = atndOTable.getData();
        adapter.changeCursor(cursor);
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
        RefreshServicePrefs.resetIfrunningFromLongTime(this);
        updateUI();
        AlertDialogHandler.checkDialog(this);

        if (RefreshServicePrefs.isStatus(RefreshServicePrefs.LOGGING_IN, this)
                || RefreshServicePrefs.isStatus(RefreshServicePrefs.REFRESHING_O, this)) {
            animateRefreshButton();
            registerReceivers();
        }
    }

}
