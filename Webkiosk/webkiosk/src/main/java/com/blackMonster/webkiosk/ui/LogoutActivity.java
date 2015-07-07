package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.Timetable;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDataHelper;
import com.blackMonster.webkiosk.refresher.AlarmService;
import com.google.analytics.tracking.android.EasyTracker;

public class LogoutActivity extends Activity {
public static final String TAG = "LogoutActivity";
public static final String FINISH = "finish";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		unallocateRecource(this);
		logoutTimetable(this);
		deleteAttendence(this);
		AlarmService.cancelAlarm(this);
		deletePrefs(this);
		
			
		
		Intent intent = new Intent(this, StartupActivity.getStartupActivity(this));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(FINISH, true);
		startActivity(intent);
		finish();

	}

	public static void unallocateRecource(Context context) {
		DbHelper.getInstance(context).close();
		TimetableDataHelper.close(context);
	}

	public static void deletePrefs(Context context) {
		String startupAct = MainPrefs.getStartupActivityName(context);
		SharedPreferences sharedPrefs = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		Editor editor = sharedPrefs.edit();
		editor.clear();
		editor.commit();
		MainPrefs.storeStartupActivity(context, startupAct);
		//Log.d(TAG, "prefs cleared");
		
	}

	public static void deleteAttendence(Context context) {
		DbHelper.shutDown();
		if (context.deleteDatabase(DbHelper.DB_NAME)) {}
		//Log.d(TAG, "Attendence cleared");
		//else
		//	Log.d(TAG, "unable to clear attendence");
			

	}

	

	public static void logoutTimetable(Context context) {
		if (context.getSharedPreferences(MainActivity.PREFS_NAME, 0).getBoolean(ModifyTimetableDialog.IS_MODIFIED, false)) return;
		else
		{
			Timetable.deleteTimetableDb(context);


		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		 EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.

	}
	

}
