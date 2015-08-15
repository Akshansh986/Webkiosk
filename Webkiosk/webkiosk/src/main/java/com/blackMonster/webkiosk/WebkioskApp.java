package com.blackMonster.webkiosk;

import android.app.Application;
import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

public class WebkioskApp extends Application {
    public static final int ATTENDENCE_GOOD = 80;
    public static final int ATTENDENCE_AVG = 70;
    public static final String ATND_NA = "NA";


    public static boolean canViewAttendance(Context context) {
        return  !(RefreshDBPrefs.getAvgAttendanceRefreshTimeStamp(context) == RefreshDBPrefs.DEFAULT_TIMESTAMP);
    }

    /**
     * Resets everything in app. Making app ready for fresh login.
     */
    public void nullifyAllVariables() {
        TimetableDbHelper.shutdown();
        MainPrefs.close();
        DbHelper.shutDown();
    }
}
