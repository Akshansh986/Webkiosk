package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.controller.UpdateAvgAtnd;
import com.blackMonster.webkiosk.databases.AttendanceUtils;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.TimetableData;
import com.blackMonster.webkiosk.refresher.ServiceLoginRefresh;
import com.blackMonster.webkioskApp.R;

public class AtndOverviewActivity extends StartupActivity implements
		OnItemClickListener {
	public String TAG = "AtndOverviewActivity";
	public static final int ATTENDENCE_GOOD = 80;
	public static final int ATTENDENCE_AVG = 70;
	public static final String ATND_NA = "NA";
	
	Cursor cursor;
	private AttendenceOverviewTable atndOTable;
	CustomCursorAdapter cursorAdapter;
	
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(getResources().getString(R.string.action_title_attendence));
		setActionBarSubtitle();

		ListView listView = new ListView(this);
		atndOTable = new AttendenceOverviewTable(this);
		cursor = atndOTable.getData();
		if (cursor != null) {
			cursorAdapter = new CustomCursorAdapter(this, cursor);
			listView.setAdapter(cursorAdapter);
			listView.setOnItemClickListener(this);
			activityContent.addView(listView);
			
		}
			//Log.d(TAG, "cursor done");
		

	}
	
	public void setActionBarSubtitle() {
		putTimestampInSubtitle(RefreshServicePrefs.getAtndOverviewTimeStamp(this));
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		atndOTable.close();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
	//	Log.d(TAG, "pos : " + position);
		cursor.moveToFirst();
		cursor.move(position);
		String code = cursor.getString(cursor
				.getColumnIndex(AttendenceOverviewTable.C_CODE));
		String subName = cursor.getString(cursor
				.getColumnIndex(AttendenceOverviewTable.C_NAME));
		Intent intent = new Intent(this, DetailedAtndActivity.class);
		intent.putExtra(DetailedAtndActivity.SUB_CODE, code);
		intent.putExtra(DetailedAtndActivity.SUB_NAME, subName);
		startActivity(intent);

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
			View retView = inflater.inflate(R.layout.attendence_overview_row,
					parent, false);

			return retView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// here we are setting our data
			// that means, take the data from the cursor and put it in views
			// TextView subName =
			if (cursor == null) return;
			String subName = cursor.getString(cursor
					.getColumnIndex(AttendenceOverviewTable.C_NAME));
			String subCode = cursor.getString(cursor
					.getColumnIndex(AttendenceOverviewTable.C_CODE));
			setTextView(R.id.atndo_Sub_name, subName, view);
			

			int pbProgress;

			if (AttendanceUtils.isLab(subCode, context)) {
				pbProgress = cursor.getInt(cursor
						.getColumnIndex(AttendenceOverviewTable.C_PRACTICAL));
				//Log.d(TAG,"progress" +  pbProgress);
				((TextView) view.findViewById(R.id.atndo_lect)).setVisibility(View.GONE);
				((TextView) view.findViewById(R.id.atndo_tute)).setVisibility(View.GONE);
			} else {
				pbProgress = cursor.getInt(cursor
						.getColumnIndex(AttendenceOverviewTable.C_OVERALL));

				setTextView(R.id.atndo_lect, "L : " + atndToString(cursor.getInt(cursor
						.getColumnIndex(AttendenceOverviewTable.C_LECTURE))), view);
				setTextView(R.id.atndo_tute, "T : " + atndToString(cursor.getInt(cursor
						.getColumnIndex(AttendenceOverviewTable.C_TUTORIAL))), view);

			}

			setTextView(R.id.atndo_overall_attendence, atndToString(pbProgress), view);
			if (pbProgress == -1)
				pbProgress = 0;
			ProgressBar pbar = ((ProgressBar) view.findViewById(R.id.atndo_progressBar));
			Rect bounds = pbar.getProgressDrawable().getBounds(); //Save the drawable bound
			setProgressBarColor(pbar, pbProgress, AtndOverviewActivity.this);
			pbar.setProgress(1);
			pbar.setProgress(pbProgress);
			pbar.getProgressDrawable().setBounds(bounds);
			if (TimetableData.showRecentUpdatedTag(AtndOverviewActivity.this) &&
					cursor.getInt(cursor.getColumnIndex(AttendenceOverviewTable.C_IS_MODIFIED)) == 1) 
				((TextView) view.findViewById(R.id.atndo_updated_tag)).setVisibility(View.VISIBLE); 
			else
				((TextView) view.findViewById(R.id.atndo_updated_tag)).setVisibility(View.INVISIBLE);
		}

		

		private String atndToString(int x) {
			if (x == -1)
				return ATND_NA;
			else
				return x + "%";
		}

		private void setTextView(int id, String text, View view) {
			if (text == null) text = ATND_NA;
			TextView tview = ((TextView) view.findViewById(id));
			tview.setText(text);
			tview.setVisibility(View.VISIBLE);
		}

	}

	public static void setProgressBarColor(ProgressBar pb, Integer attendence,
			Context context) {
		if (attendence == null || attendence < 0) attendence = 0;

		if (attendence >= ATTENDENCE_GOOD)
			pb.setProgressDrawable(context.getResources().getDrawable(
					R.drawable.custom_progressbar_green));
		else if (attendence >= ATTENDENCE_AVG)
			pb.setProgressDrawable(context.getResources().getDrawable(
					R.drawable.custom_progressbar_orange));
		else
			pb.setProgressDrawable(context.getResources().getDrawable(
					R.drawable.custom_progressbar_red));

	}


	@Override
	public void registerReceivers() {
	//	Log.d(TAG, "subclass register receiver");
		if (! isReceiverRegistered) {
		//	Log.d(TAG, "registered tempatnd");

			LocalBroadcastManager.getInstance(this).registerReceiver(
					broadcastTempAtndResult,
					new IntentFilter(ServiceLoginRefresh.BROADCAST_TEMP_ATND_RESULT));
		}
			
		super.registerReceivers();
	}
	
	@Override
	public void unregisterIfRegistered() {
		//Log.d(TAG, "subclass unregister receiver");
		if (isReceiverRegistered) {
		//	Log.d(TAG, "unregistered tempatnd");

			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					broadcastTempAtndResult);
		}
		super.unregisterIfRegistered();
	}
	
	BroadcastReceiver broadcastTempAtndResult = new BroadcastReceiver() {
		
	
		@Override
		public void onReceive(Context context, Intent intent) {
			
		//	Log.d(TAG, "received : BroadcastTempAtndResult");
			
			unanimateRefreshButton();
			updateUI();
			
			int result = intent.getExtras().getInt(
					ServiceLoginRefresh.BROADCAST_TEMP_ATND_RESULT);
			
			if (result == UpdateAvgAtnd.ERROR) {
				MyAlertDialog.checkDialog(AtndOverviewActivity.this);
			} else {
				makeToast(result);
				
			}
			
			
		}


		private void makeToast(int result) {
			Toast.makeText(
					AtndOverviewActivity.this,result + " " +
					getString(R.string.temp_attendence_updated),
					Toast.LENGTH_LONG).show();			
		}
	};
	
	private void updateUI() {
		setActionBarSubtitle();
		cursor.close();
		cursor  = atndOTable.getData();
		cursorAdapter.changeCursor(cursor);			
	}
	
	

	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterIfRegistered();
		MyAlertDialog.dismissIfPresent();
		unanimateRefreshButton();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RefreshServicePrefs.resetIfrunningFromLongTime(this);
		updateUI();
		MyAlertDialog.checkDialog(this);
		
		if (RefreshServicePrefs.isStatus(RefreshServicePrefs.LOGGING_IN, this) 
				|| RefreshServicePrefs.isStatus(RefreshServicePrefs.REFRESHING_O, this)) {
			animateRefreshButton();
			registerReceivers();
		}
		
		
	}
	
}
