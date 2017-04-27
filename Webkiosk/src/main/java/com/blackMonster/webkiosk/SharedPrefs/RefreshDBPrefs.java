package com.blackMonster.webkiosk.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.controller.RefreshStatus;

/**
 * Current status of database refresher and refresh related other details is stored here.
 */
public class RefreshDBPrefs {

    public static final String REFRESH_SERVICE_STATUS = "refSStatus";


    //Name value is out of sync, would be great if someone could fix it.
    public static final String DETAILED_ATND_TIMESTAMP = "lastUpdate";
    public static final String REFRESH_START_TIMESTAMP = "refreshStartTime";
    public static final String PASSWORD_UPTO_DATE = "passUptoDate";
    public static final String WIFI_ZONE_END_RANDOMIZE_TIME = "wifiZoneEndRandomizeTime";
    private static final String AVG_ATND_TIMESTAMP = "ATND_OVERVIEW_TIMESTAMP";
    public static final String SHOW_RECENTLY_UPDATED_TAG = "showRecentlyUpdatedTag";
    private static final String IS_FIRST_REFRESH = "isFirstRefresh";
    private static final String REFRESH_END_TIMESTAMP = "refreshEndTime";

    public static final int DEFAULT_TIMESTAMP = 0;

    static SharedPreferences prefs = null;

    //Singleton init preference.
    private static void initPrefInstance(Context context) {
        if (prefs == null)
            prefs = context.getApplicationContext().getSharedPreferences(MainPrefs.PREFS_NAME, 0);
    }

    /**
     * Refresher has to use setStatus(..) before doing anything so that other part of app has info what refresher is currently doing.
     * @param status    Current status of refresh
     * @param context   Context
     */
    public static void setStatus(RefreshStatus status, Context context) {
        initPrefInstance(context);
        prefs.edit().putString(REFRESH_SERVICE_STATUS, status.getString()).commit();
    }

    /**
     * Return current status of Refresher.
     * @param context
     * @return RefreshStatus
     */
    public static RefreshStatus getStatus(Context context) {
        initPrefInstance(context);
        String str =  prefs.getString(REFRESH_SERVICE_STATUS, RefreshStatus.STOPPED.getString());
        return   RefreshStatus.getEnumFromString(str);
    }

    /**
     * Checks current status of refresh with provided status.
     */
    public static boolean isStatus(RefreshStatus status, Context context) {
        initPrefInstance(context);
        return getStatus(context) == status;
    }

    /**
     * Sets time when refresh starts.
     * @param context
     */
    public static void setRefreshStartTimestamp(Context context) {
        initPrefInstance(context);

        prefs.edit()
                .putLong(REFRESH_START_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    private static long getRefreshStartTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(REFRESH_START_TIMESTAMP, DEFAULT_TIMESTAMP);
    }

    /**
     * Sets time when average attendance update is done.
     * @param context
     */
    public static void setAvgAttendanceRefreshTimestamp(Context context) {
        initPrefInstance(context);
        prefs.edit()
                .putLong(AVG_ATND_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    /**
     * Gets last time when average attendance was updated.
     * @param context
     * @return
     */
    public static long getAvgAttendanceRefreshTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(AVG_ATND_TIMESTAMP, DEFAULT_TIMESTAMP);
    }

    /**
     * Sets time when detailed attendance update is done.
     * @param context
     */
    public static void setDetailedAtndRefreshTimestamp(Context context) {
        initPrefInstance(context);
        prefs.edit()
                .putLong(DETAILED_ATND_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    /**
     * Gets last time when detailed attendance was updated.
     * @param context
     * @return
     */
    public static long getDetailedAtndRefreshTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(DETAILED_ATND_TIMESTAMP, DEFAULT_TIMESTAMP);
    }

    /**
     * Sets time when full refresh is complete.
     * @param context
     */
    public static void setRefreshEndTimestamp(Context context) {
        initPrefInstance(context);

        prefs.edit()
                .putLong(REFRESH_END_TIMESTAMP, System.currentTimeMillis())
                .commit();
    }

    /**
     * Gets end time of last refresh.
     * @param context
     * @return
     */
    public static long getRefreshEndTimeStamp(Context context) {
        initPrefInstance(context);
        return prefs.getLong(REFRESH_END_TIMESTAMP, DEFAULT_TIMESTAMP);
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

    /**
     * Sets that first time database refresh of app is done.
     * @param context
     */
    public static void setFirstRefreshOver(Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(IS_FIRST_REFRESH, false).commit();
    }

    /**
     * Returns if refresh is running or not.
     * @param context
     * @return
     */
    public static boolean isRunning(Context context) {
        return (getStatus(context) != RefreshStatus.STOPPED);
    }

    private static boolean isRunningFromLongTime(Context context) {
        initPrefInstance(context);
        if (isRunning(context)
                && System.currentTimeMillis() > (getRefreshStartTimeStamp(context) + 300000))
            return true;
        else
            return false;
    }

    /**
     * Resets status of refresh if it is running from more than 5 minutes.
     * @param context
     */
    public static void resetIfrunningFromLongTime(Context context) {
        initPrefInstance(context);

        if (isRunningFromLongTime(context)) {
            setStatus(RefreshStatus.STOPPED, context);
        }
    }
}