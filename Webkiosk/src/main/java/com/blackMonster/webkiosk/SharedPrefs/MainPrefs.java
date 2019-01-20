package com.blackMonster.webkiosk.SharedPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.crawler.CrawlerUtils;
import com.blackMonster.webkiosk.ui.TimetableActivity;

public class MainPrefs {
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final String ENROLL_NO = "enroll";
    public static final String PASSWORD = "pass";
    public static final String DOB = "dob";
    public static final String BATCH = "batch";
    public static final String COLG = "colg";
    public static final String USER_NAME = "userName";        //Name of student.
    public static final String IS_FIRST_TIME = "isFirstTime";
    private static final String STARTUP_ACTIVITY_NAME = "startupActivityName";
    private static final String ONLINE_TIMETABLE_FILE_NAME = "onlineTimetableFileName";
    public static final String IS_TIMETABLE_MODIFIED = "isTimetableModified";  //True if user modifies timetable at any time in lifetime.

    public static final String SAVE_DIALOG_MSG = "dialog";
    public static final String SAVE_DIALOG_DEFAULT_MSG = "NA";


    private static SharedPreferences prefs = null;

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

    public static String getDOB(Context context) {
        initPrefInstance(context);
        return prefs.getString(DOB, "123");
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

    /**
     * get message of saved error dialog.
     *
     * @param context
     * @return
     */
    public static String getSavedDialogMsg(Context context) {
        initPrefInstance(context);
        return prefs.getString(SAVE_DIALOG_MSG, SAVE_DIALOG_DEFAULT_MSG);
    }

    public static String getStartupActivityName(Context context) {
        initPrefInstance(context);
        return prefs.getString(STARTUP_ACTIVITY_NAME, TimetableActivity.class.getSimpleName());
    }

    /**
     * FileName of timetable as stored on server.
     *
     * @param context
     * @return
     */
    public static String getOnlineTimetableFileName(Context context) {
        initPrefInstance(context);
        return prefs.getString(ONLINE_TIMETABLE_FILE_NAME, "NULL");
    }


    /**
     * Used in showing help menu at first login.
     *
     * @param context
     * @return true, if app is viewed first time after logging in.
     */
    public static boolean isFirstTime(Context context) {
        initPrefInstance(context);
        return prefs.getBoolean(IS_FIRST_TIME, true);
    }

    public static boolean isTimetableModified(Context context) {
        initPrefInstance(context);
        return prefs.getBoolean(IS_TIMETABLE_MODIFIED, false);
    }

    //UserName == StudentName
    public static void setUserName(String userName, Context context) {
        initPrefInstance(context);
        prefs.edit().putString(USER_NAME, userName).commit();
    }

    public static void setPassword(String password, Context context) {
        initPrefInstance(context);
        prefs.edit().putString(PASSWORD, password).commit();
    }

    public static void setDOB(String dob, Context context) {
        initPrefInstance(context);
        prefs.edit().putString(DOB, dob).commit();
    }

    /**
     * set meessage of dialog to be saved.
     * @param msg
     * @param context
     */
    public static void setSaveDialogMsg(String msg, Context context) {
        initPrefInstance(context);
        prefs.edit().putString(SAVE_DIALOG_MSG, msg).commit();
    }

    /**
     * set that first refresh of database is over.
     * @param context
     */
    public static void setFirstTimeOver(Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(IS_FIRST_TIME, false).commit();
    }

    public static void storeStartupActivity(Context context, String name) {
        initPrefInstance(context);
        prefs.edit().putString(STARTUP_ACTIVITY_NAME, name).commit();
    }

    /**
     * Filename of timetable on server.
     * @param context
     * @param fileName
     */
    public static void setOnlineTimetableFileName(Context context, String fileName) {
        initPrefInstance(context);
        prefs.edit().putString(ONLINE_TIMETABLE_FILE_NAME, fileName).commit();
    }

    /**
     * set if timetable if timetable is manually modified by user or not.
     * @param context
     */
    public static void setTimetableModified(Context context) {
        initPrefInstance(context);
        prefs.edit().putBoolean(IS_TIMETABLE_MODIFIED, true).commit();
    }

    public static void close() {
        prefs = null;
    }

}
