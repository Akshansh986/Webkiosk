package com.blackMonster.webkiosk.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.controller.RefreshFullDB;
import com.blackMonster.webkiosk.controller.updateAtnd.SubjectChangedException;
import com.blackMonster.webkiosk.ui.LoginActivity;
import com.blackMonster.webkiosk.ui.LogoutActivity;

/**
 * Service to refresh full database.
 */
public class ServiceRefreshAll extends IntentService {
    public static final String TAG = "ServiceRefresh";
    public static final String RECREATING_DATABASE = "recreateDatabase";

    int refreshType;    //Manual refresh or auto refresh.

    public ServiceRefreshAll() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        refreshType = intent.getExtras().getInt(RefreshFullDB.REFRESH_TYPE);

        RefreshFullDB refreshFullDB = new RefreshFullDB(refreshType, this);

        try {
            refreshFullDB.refresh();
        } catch (SubjectChangedException e) {
            recreateDatabase(); //Sem has changed, recreate complete database.
        }
    }

    /**
     * Completely recreate all database.
     */
    public void recreateDatabase() {
        resetPrefs();
        LogoutActivity.unallocateRecource(this);
        TimetableCreateRefresh.deleteTimetableDb(this);
        LogoutActivity.deleteAttendence(this);
        if (isAutoRefresh()) {
            startServiceLogin();    //No need to show UI.
        } else
            startLoginActivity(); //Need to show UI.

    }


    private void resetPrefs() {

        String autoUpdateOver = getSharedPreferences(MainPrefs.PREFS_NAME, 0)
                .getString(AutoRefreshAlarmService.PREF_AUTO_UPDATE_OVER, "anyNetwork");
        String batch = MainPrefs.getBatch(this);
        String colg = MainPrefs.getColg(this);
        String userName = MainPrefs.getUserName(this);
        String enroll = MainPrefs.getEnroll(this);
        String pass = MainPrefs.getPassword(this);
        String fileName = MainPrefs.getOnlineTimetableFileName(this);
        MainPrefs.close();
        getSharedPreferences(MainPrefs.PREFS_NAME, 0).edit().clear()
                .commit();

        SharedPreferences.Editor ed = getSharedPreferences(MainPrefs.PREFS_NAME, 0).edit();
        ed.putString(AutoRefreshAlarmService.PREF_AUTO_UPDATE_OVER, autoUpdateOver);
        ed.putString(MainPrefs.BATCH, batch);
        ed.putString(MainPrefs.COLG, colg);
        ed.putString(MainPrefs.USER_NAME, userName);
        ed.putString(MainPrefs.ENROLL_NO, enroll);
        ed.putString(MainPrefs.PASSWORD, pass);
        ed.putBoolean(MainPrefs.IS_FIRST_TIME, false);
        ed.commit();
        MainPrefs.setOnlineTimetableFileName(this, fileName);

    }

    //Recreate full database using the flow from actual login flow.
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECREATING_DATABASE, true);
        getApplication().startActivity(intent);

    }

    private boolean isAutoRefresh() {
        return refreshType == RefreshFullDB.AUTO_REFRESH;
    }

    private void startServiceLogin() {
        Intent intent = ServiceAppLogin.getIntent(MainPrefs.getColg(this),
                MainPrefs.getEnroll(this), MainPrefs.getPassword(this),
                MainPrefs.getBatch(this), this);
        startService(intent);
    }

    /**
     * Anyone starting this service is expected to pass intent returned by this function.
     */
    public static Intent getIntent(int refreshType, Context context) {
        Intent intent = new Intent(context, ServiceRefreshAll.class);
        intent.putExtra(RefreshFullDB.REFRESH_TYPE, refreshType);
        return intent;
    }
}
