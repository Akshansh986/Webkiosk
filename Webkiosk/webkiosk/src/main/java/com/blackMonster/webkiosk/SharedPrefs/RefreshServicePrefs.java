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
    public static final int CREATING_DB = 5;




    static final int RECENTLY_UPDATED_TIME_LAG = 300000;
    //Name value is out of sync, would be great if someone could fix it.
    public static final String DETAILED_ATND_TIMESTAMP = "lastUpdate";
    public static final String REFRESH_START_TIMESTAMP = "refreshStartTime";
    public static final String PASSWORD_UPTO_DATE = "passUptoDate";
    public static final String WIFI_ZONE_END_RANDOMIZE_TIME = "wifiZoneEndRandomizeTime";
    private static final String AVG_ATND_TIMESTAMP = "ATND_OVERVIEW_TIMESTAMP";
    public static final String SHOW_RECENTLY_UPDATED_TAG = "showRecentlyUpdatedTag";
    private static final String IS_FIRST_REFRESH = "isFirstRefresh";
    private static final String REFRESH_END_TIMESTAMP = "refreshEndTime";

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


    public static boolean isStatus(int st, Context context) {
        initPrefInstance(context);
        return getStatus(context) == st;
    }


    //TODO move this to appropriate place.
    public static String getStatusMessage(Context context) {

        switch (getStatus(context)) {

            case REFRESHING_D:
                return context.getString(R.string.refreshing_detailed_atnd);
            case REFRESHING_DATESHEET:
                return context.getString(R.string.refreshing_datesheet);
            default:
                return context.getString(R.string.refresh_in_progress);


        }


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


    public static void setAvgAttendanceTimestamp(Context context) {
        initPrefInstance(context);
        prefs.edit()
                .putLong(AVG_ATND_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    public static long getAvgAttendanceTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(AVG_ATND_TIMESTAMP, 0);
    }


    public static void setDetailedAtndTimestamp(Context context) {
        initPrefInstance(context);
        prefs.edit()
                .putLong(DETAILED_ATND_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    public static long getDetailedAtndTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(DETAILED_ATND_TIMESTAMP, 0);
    }

    public static void setRefreshEndTimestamp(Context context) {
        initPrefInstance(context);

        prefs.edit()
                .putLong(REFRESH_END_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    public static long getRefreshEndTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(REFRESH_END_TIMESTAMP, 0);
    }

    public static void setRecentlyUpdatedTagVisibility(boolean value, Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(SHOW_RECENTLY_UPDATED_TAG, value).commit();
    }

    public static boolean getRecentlyUpdatedTagVisibility(Context context) {
        initPrefInstance(context);
        return prefs.getBoolean(SHOW_RECENTLY_UPDATED_TAG, false);
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


    public static boolean isFirstRefresh(Context context) {
        initPrefInstance(context);
        return prefs.getBoolean(IS_FIRST_REFRESH, true);
    }

    public static void setFirstRefreshOver(Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(IS_FIRST_REFRESH, false).commit();
    }

    public static boolean isRunning(Context context) {
        return (getStatus(context) != STOPPED);
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
}