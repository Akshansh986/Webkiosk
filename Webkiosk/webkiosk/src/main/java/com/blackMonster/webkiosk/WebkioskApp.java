package com.blackMonster.webkiosk;

import android.app.Application;
import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

public class WebkioskApp extends Application {
    public static boolean canViewAttendance(Context context) {
        return  !(RefreshServicePrefs.getAvgAttendanceTimeStamp(context) == RefreshServicePrefs.DEFAULT_TIMESTAMP);
    }

    /**
     * Resets everything in app. Making app ready for fresh login.
     */
    public void nullifyAllVariables() {
        TimetableDbHelper.nullifyInstance();
        MainPrefs.close();
        DbHelper.shutDown();
    }
}
