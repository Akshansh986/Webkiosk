package com.blackMonster.webkiosk;

import android.app.Application;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

public class WebkioskApp extends Application {
     /**
     * Resets everything in app. Making app ready for fresh login.
     */
    public void nullifyAllVariables() {
        TimetableDbHelper.nullifyInstance();
        MainPrefs.close();
        DbHelper.shutDown();
    }
}
