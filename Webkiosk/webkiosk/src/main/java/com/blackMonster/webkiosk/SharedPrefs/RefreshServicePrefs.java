package com.blackMonster.webkiosk.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkioskApp.R;

public class RefreshServicePrefs {

	public static final String REFRESH_SERVICE_STATUS = "refSSt";
	public static final int STOPPED = 0;
	public static final int LOGGING_IN = 1;
	public static final int REFRESHING_O = 2;
	public static final int REFRESHING_D = 3;
	public static final int REFRESHING_DATESHEET = 4;

	static final int RECENTLY_UPDATED_TIME_LAG = 300000;

	public static final String LAST_UPDATED = "lastUpdate";
	public static final String REFRESH_START_TIMESTAMP = "refreshStartTime";
	public static final String PASSWORD_UPTO_DATE = "passUptoDate";
	public static final String WIFI_ZONE_END_RANDOMIZE_TIME = "wifiZoneEndRandomizeTime";
	private static final String ATND_OVERVIEW_TIMESTAMP = "ATND_OVERVIEW_TIMESTAMP";
	public static final String SHOW_RECENTLY_UPDATED_TAG = "showRecentlyUpdatedTag";
	private static final String IS_FIRST_REFRESH = "isFirstRefresh";

	static SharedPreferences prefs = null;

	private static void initPrefInstance(Context context) {
		if (prefs == null)
			prefs = context.getApplicationContext().getSharedPreferences(MainActivity.PREFS_NAME, 0);
	}

	public static void setStatus(int st, Context context) {
		initPrefInstance(context);
		prefs.edit().putInt(REFRESH_SERVICE_STATUS, st).commit();
	}

	public static int getStatus(Context context) {
		initPrefInstance(context);
		return prefs.getInt(REFRESH_SERVICE_STATUS, STOPPED);
	}


	//TODO move this to appropriate place.
	public static String getStatusMessage(Context context){

		switch (getStatus(context)){

			case  REFRESHING_D :
				return 	context.getString(R.string.refreshing_detailed_atnd);
			case REFRESHING_DATESHEET:
				return context.getString(R.string.refreshing_datesheet);
			default:
				return 	context.getString(R.string.refresh_in_progress);




		}


	}

	public static boolean isRunning(Context context) {
		return (getStatus(context) != STOPPED);
	}

	public static boolean isRefreshingAtndOverview(Context context) {
		return (getStatus(context) == LOGGING_IN || getStatus(context) == REFRESHING_O);
	}

	public static long getLastRefreshTime(Context context) {
		initPrefInstance(context);
		return prefs.getLong(LAST_UPDATED, 0);
	}

	public static boolean isRecentlyUpdated(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(SHOW_RECENTLY_UPDATED_TAG, false);
	}
	
	public static void putRecentlyUpdatedTag(boolean value, Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(SHOW_RECENTLY_UPDATED_TAG, value).commit();
	}

	public static boolean isStatus(int st, Context context) {
		initPrefInstance(context);
		return getStatus(context) == st;
	}

	public static void setRefreshStartTimestamp(Context context) {
		initPrefInstance(context);
		
		prefs.edit()
				.putLong(REFRESH_START_TIMESTAMP, System.currentTimeMillis())
				.commit();
	}

	private static long getRefreshStartTimeStamp(Context context) {
		initPrefInstance(context);
		return prefs.getLong(REFRESH_START_TIMESTAMP, 0);
	}

	private static boolean isRunningFromLongTime(Context context) {
		initPrefInstance(context);
		if (isRunning(context)
				&& System.currentTimeMillis() > (getRefreshStartTimeStamp(context) + 300000))
			return true;
		else
			return false;
	}

	public static void resetIfrunningFromLongTime(Context context) {
		initPrefInstance(context);

		if (isRunningFromLongTime(context)) {
			setStatus(STOPPED, context);
		}
	}

	public static void setPasswordOutdated(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(PASSWORD_UPTO_DATE, false).commit();
	}

	public static void setPasswordUptoDate(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(PASSWORD_UPTO_DATE, true).commit();
	}

	
	public static boolean isPasswordUptoDate(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(PASSWORD_UPTO_DATE, true);
	}
	
	public static boolean canAutoRefresh(Context context) {
		return isPasswordUptoDate(context);
	}

	public static long getWifiZoneEndRandomizeTime(Context context) {
		initPrefInstance(context);
		return prefs.getLong(WIFI_ZONE_END_RANDOMIZE_TIME,
				System.currentTimeMillis());
	}

	public static void setWifiZoneEndRandomizeTime(long time, Context context) {
		initPrefInstance(context);
		prefs.edit().putLong(WIFI_ZONE_END_RANDOMIZE_TIME, time).commit();
	}

	public static void setAtndOverviewTimestamp(Context context) {
		initPrefInstance(context);
		prefs.edit()
				.putLong(ATND_OVERVIEW_TIMESTAMP, System.currentTimeMillis())
				.commit();
	}

	public static long getAtndOverviewTimeStamp(Context context) {
		initPrefInstance(context);
		return prefs.getLong(ATND_OVERVIEW_TIMESTAMP, 0);
	}

	public static boolean isFirstRefresh(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(IS_FIRST_REFRESH, true);
	}

	public static void setFirstRefreshOver(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(IS_FIRST_REFRESH, false).commit();
	}
	

}