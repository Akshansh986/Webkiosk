package com.blackMonster.webkiosk;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.ui.TimetableActivity;

public class MainPrefs {
	public static final String ENROLL_NO = "enroll";
	public static final String PASSWORD = "pass";
	public static final String BATCH = "batch";
	public static final String SEM = "sem";
	public static final String COLG = "colg";
	public static final String USER_NAME = "userName";
	public static final String IS_FIRST_TIME = "isFirstTime";
	private static final String STARTUP_ACTIVITY_NAME = "startupActivityName";
	private static final String ONLINE_TIMETABLE_FILE_NAME = "onlineTimetableFileName";
	
	public static final int DEFAULT_SEM = -1;

	
	private static SharedPreferences prefs=null;

	private static void initPrefInstance(Context context) {
		if (prefs == null) prefs = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
	}
	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
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
	
	public static int getSem(Context context) {
		initPrefInstance(context);
		return prefs.getInt(SEM, DEFAULT_SEM);
	}
	
	public static int getSem(SharedPreferences settings) {
		return settings.getInt(SEM, DEFAULT_SEM);
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
	


	public static boolean isFirstTime(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(IS_FIRST_TIME, true);
	}
	
	public static void setFirstTimeOver(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(IS_FIRST_TIME, false).commit();
	}
	
	public static void setDefaultSem(Context context) {
		initPrefInstance(context);
		prefs.edit().putInt(SEM, DEFAULT_SEM).commit();
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
