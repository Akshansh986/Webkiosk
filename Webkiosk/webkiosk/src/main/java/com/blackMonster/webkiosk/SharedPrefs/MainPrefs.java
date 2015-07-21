package com.blackMonster.webkiosk.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.ui.TimetableActivity;

public class MainPrefs {
	public static final String PREFS_NAME = "MyPrefsFile";

	public static final String ENROLL_NO = "enroll";
	public static final String PASSWORD = "pass";
	public static final String BATCH = "batch";
	public static final String SEM = "sem";
	public static final String COLG = "colg";
	public static final String USER_NAME = "userName";
	public static final String IS_FIRST_TIME = "isFirstTime";
	private static final String STARTUP_ACTIVITY_NAME = "startupActivityName";
	private static final String ONLINE_TIMETABLE_FILE_NAME = "onlineTimetableFileName";
	

	private static SharedPreferences prefs=null;

	private static void initPrefInstance(Context context) {
		if (prefs == null) prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}

	public static String getEnroll(Context context) {
		initPrefInstance(context);
		return prefs.getString(ENROLL_NO, "123");
	}
	
	public static String getPassword(Context context) {
		initPrefInstance(context);
		return prefs.getString(PASSWORD, "123");
	}
	
	public static String getBatch(Context context) {
		initPrefInstance(context);
		return prefs.getString(BATCH, "123");
	}
	

	public static String getColg(Context context) {
		initPrefInstance(context);
		return prefs.getString(COLG, "JIIT");
	}
	
	public static String getUserName(Context context) {
		initPrefInstance(context);
		return prefs.getString(USER_NAME, "NA");
	}
	
	public static String getStartupActivityName(Context context) {
		initPrefInstance(context);
		return prefs.getString(STARTUP_ACTIVITY_NAME, TimetableActivity.class.getSimpleName());
	}
	
	public static String getOnlineTimetableFileName(Context context) {
		initPrefInstance(context);
		return prefs.getString(ONLINE_TIMETABLE_FILE_NAME, "NULL");
	}


	/**
	 * Used in showing help menu at first login.
	 * @param context
	 * @return true, if app is viewed first time after logging in.
	 */
	public static boolean isFirstTime(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(IS_FIRST_TIME, true);
	}
	//UserName == StudentName
	public static void setUserName(String userName,Context context) {
		initPrefInstance(context);
		prefs.edit().putString(USER_NAME, userName).commit();
	}

	public static void setFirstTimeOver(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(IS_FIRST_TIME, false).commit();
	}

	public static void storeStartupActivity(Context context,String name) {
		initPrefInstance(context);
		prefs.edit().putString(STARTUP_ACTIVITY_NAME, name).commit();
	}

	public static void setOnlineTimetableFileName(Context context,String fileName) {
		initPrefInstance(context);
		prefs.edit().putString(ONLINE_TIMETABLE_FILE_NAME, fileName).commit();
	}
	
	public static void close() {
		prefs = null;
	}

}
