package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.services.AutoRefreshAlarmService;
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
        AutoRefreshAlarmService.cancelAlarm(this);
        deletePrefs(this);


        Intent intent = new Intent(this, StartupActivity.getStartupActivity(this));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(FINISH, true);
        startActivity(intent);
        finish();

    }

    public static void unallocateRecource(Context context) {
        DbHelper.shutDown();
        TimetableDbHelper.shutdown();
    }

    public static void deletePrefs(Context context) {
        String startupAct = MainPrefs.getStartupActivityName(context);
        SharedPreferences sharedPrefs = context.getSharedPreferences(MainPrefs.PREFS_NAME, 0);
        Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
        MainPrefs.storeStartupActivity(context, startupAct);

    }

    public static void deleteAttendence(Context context) {
        DbHelper.shutDown();
        context.deleteDatabase(DbHelper.DB_NAME);

    }


    public static void logoutTimetable(Context context) {
        if (MainPrefs.isTimetableModified(context)) return; //Don't delete timetable if it is manually modified by user. Will reuse it again if logined with same enroll no and batch.
        else {
            TimetableCreateRefresh.deleteTimetableDb(context); //Timetable not modified by user, safe to delete it.
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Google analytics.
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Google analytics.

    }


}
