package com.blackMonster.webkiosk;

import android.app.Application;
import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

public class WebkioskApp extends Application {
    /**
     * If user has logined to app or not.
     * @param context
     * @return
     */
    public static boolean isAppLogined(Context context) {
        return  !(RefreshDBPrefs.getAvgAttendanceRefreshTimeStamp(context) == RefreshDBPrefs.DEFAULT_TIMESTAMP);
    }

    /**
     * Closes opened databases and sharedPrefs. Making app ready for fresh login.
     */
    public void nullifyAllVariables() {
        TimetableDbHelper.shutdown();
        MainPrefs.close();
        DbHelper.shutDown();
    }
}
