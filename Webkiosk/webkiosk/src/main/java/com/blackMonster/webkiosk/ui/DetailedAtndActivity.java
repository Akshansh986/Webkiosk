package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.controller.RefreshDB;
import com.blackMonster.webkiosk.controller.UpdateDetailedAttendence;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendenceTable;
import com.blackMonster.webkioskApp.R;

public class DetailedAtndActivity extends BaseActivity {
    public static final String TAG = "DetailedAtndActivity";
    public static final String SUB_CODE = "subcode";
    public static final String SUB_NAME = "subname";
    String subName;
    String code;

    CustomCursorAdapter cursorAdapter;

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
        Cursor cursor = new DetailedAttendenceTable(
                code, 0, this).getData();
        // if (cursor != null)
        // Log.d("act", "cursor done");
        ListView listView = new ListView(this);
        cursorAdapter = new CustomCursorAdapter(this, cursor);
        listView.setAdapter(cursorAdapter);
        activityContent.removeAllViews();
        activityContent.addView(listView);

    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(subName);
        setActionBarSubtitle();
        openDrawerWithIcon(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setActionBarSubtitle() {
        putTimestampInSubtitle(RefreshServicePrefs.getDetailedAtndTimeStamp(this));
    }

    public class CustomCursorAdapter extends CursorAdapter {

        public CustomCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // when the view will be created for first time,
            // we need to tell the adapters, how each item will look
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View retView = inflater.inflate(R.layout.detailed_attendence_row,
                    parent, false);

            return retView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // here we are setting our data
            // that means, take the data from the cursor and put it in views

            // setLT(view, cursor);

            if (cursor.getInt(cursor
                    .getColumnIndex(DetailedAttendenceTable.C_STATUS)) == 1) {
                ((TextView) view.findViewById(R.id.detailed_atnd_leftbar))
                        .setBackgroundColor(Color.rgb(77, 184, 73));
                ((RelativeLayout) view.findViewById(R.id.detailed_atnd_root))
                        .setBackgroundColor(Color.rgb(249, 249, 249)); // 229,229,229

            } else {
                ((TextView) view.findViewById(R.id.detailed_atnd_leftbar))
                        .setBackgroundColor(Color.rgb(231, 70, 62));
                ((RelativeLayout) view.findViewById(R.id.detailed_atnd_root))
                        .setBackgroundColor(Color.rgb(252, 233, 232));

            }

            ((TextView) view.findViewById(R.id.detailed_atnd_date))
                    .setText(cursor.getString(cursor
                            .getColumnIndex(DetailedAttendenceTable.C_DATE)));
            ((TextView) view.findViewById(R.id.detailed_atnd_teacher))
                    .setText(cursor.getString(cursor
                            .getColumnIndex(DetailedAttendenceTable.C_ATTENDENCE_BY)));

        }

		/*
         * private void setTuteMarker(View view, Cursor cursor, int color) { if
		 * (!isLab) if (cursor.getString(
		 * cursor.getColumnIndex(DetailedAttendenceTable.C_LTP))
		 * .equals("Tutorial")){ ((TextView) view
		 * .findViewById(R.id.detailed_atnd_tute_marker
		 * )).setVisibility(View.VISIBLE); ((TextView) view
		 * .findViewById(R.id.detailed_atnd_tute_marker))
		 * .setBackgroundColor(color); } else ((TextView) view
		 * .findViewById(R.id
		 * .detailed_atnd_tute_marker)).setVisibility(View.INVISIBLE);
		 * 
		 * }
		 */

    }

    @Override
    public void registerReceivers() {
        // Log.d(TAG, "subclass register receiver");
        if (!isReceiverRegistered) {
            // Log.d(TAG, "registered tempatnd");

            LocalBroadcastManager
                    .getInstance(this)
                    .registerReceiver(
                            broadcastUpdateAtndResult,
                            new IntentFilter(
                                    RefreshDB.BROADCAST_UPDATE_ATTENDENCE_RESULT));
        }

        super.registerReceivers();
    }

    @Override
    public void unregisterIfRegistered() {
        // Log.d(TAG, "subclass unregister receiver");
        if (isReceiverRegistered) {
            // Log.d(TAG, "unregistered tempatnd");

            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    broadcastUpdateAtndResult);
        }
        super.unregisterIfRegistered();
    }

    BroadcastReceiver broadcastUpdateAtndResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Log.d(TAG, "received : broadcastUpdateAtndResult");

            unanimateRefreshButton();
            updateUI();

            int result = intent.getExtras().getInt(
                    RefreshDB.BROADCAST_UPDATE_ATTENDENCE_RESULT);

            if (result == UpdateDetailedAttendence.ERROR) {
                AlertDialogHandler.checkDialog(DetailedAtndActivity.this);
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

    private void updateUI() {
        setActionBarSubtitle();
        cursorAdapter
                .changeCursor(new DetailedAttendenceTable(
                        code, 0, this).getData());
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterIfRegistered();
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
                || RefreshServicePrefs.isStatus(RefreshServicePrefs.REFRESHING_O, this)
                || RefreshServicePrefs.isStatus(RefreshServicePrefs.REFRESHING_D, this)) {
            animateRefreshButton();
            registerReceivers();
        }

    }
}
