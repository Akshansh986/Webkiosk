package com.blackMonster.webkiosk.refresher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.Timetable;
import com.blackMonster.webkiosk.ui.LoginActivity;
import com.blackMonster.webkiosk.ui.LogoutActivity;

/**
 * Created by akshansh on 17/07/15.
 */
public class ServiceRefresh extends IntentService {
    public static final String TAG = "ServiceRefresh";
    public static final String RECREATING_DATABASE = "recreateDatabase";

    int refreshType;

    public ServiceRefresh() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        refreshType = intent.getExtras().getInt(RefreshDB.REFRESH_TYPE);

        RefreshDB refreshDB = new RefreshDB(refreshType, this);

        try {
            refreshDB.refresh();
        } catch (SubjectChangedException e) {
            recreateDatabase();
        }
    }


    public void recreateDatabase() {
        resetPrefs();
        LogoutActivity.unallocateRecource(this);
        Timetable.deleteTimetableDb(this);
        LogoutActivity.deleteAttendence(this);
        // M.log(TAG, "cleared");
        if (isAutoRefresh()) {
            startServiceLogin();
        } else
            startLoginActivity();

    }


    private void resetPrefs() {

        String autoUpdateOver = getSharedPreferences(MainActivity.PREFS_NAME, 0)
                .getString(AlarmService.PREF_AUTO_UPDATE_OVER, "anyNetwork");
        String batch = MainPrefs.getBatch(this);
        String colg = MainPrefs.getColg(this);
        String userName = MainPrefs.getUserName(this);
        String enroll = MainPrefs.getEnroll(this);
        String pass = MainPrefs.getPassword(this);
        String fileName = MainPrefs.getOnlineTimetableFileName(this);
        MainPrefs.close();
        getSharedPreferences(MainActivity.PREFS_NAME, 0).edit().clear()
                .commit();

        SharedPreferences.Editor ed = getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
        ed.putString(AlarmService.PREF_AUTO_UPDATE_OVER, autoUpdateOver);
        ed.putString(MainPrefs.BATCH, batch);
        ed.putString(MainPrefs.COLG, colg);
        ed.putString(MainPrefs.USER_NAME, userName);
        ed.putString(MainPrefs.ENROLL_NO, enroll);
        ed.putString(MainPrefs.PASSWORD, pass);
        ed.putBoolean(MainPrefs.IS_FIRST_TIME, false);
        ed.commit();
        MainPrefs.setOnlineTimetableFileName(this, fileName);
        // M.log(TAG, "SEM : " + MainPrefs.getSem(this));

    }


    private void startLoginActivity() {
        // M.log(TAG, "calling loginActivity");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECREATING_DATABASE, true);
        getApplication().startActivity(intent);

    }

    private boolean isAutoRefresh() {
        return refreshType == RefreshDB.AUTO_REFRESH;
    }

    private void startServiceLogin() {
        // M.log(TAG, "starting srvice login refresh");

        Intent intent = ServiceLogin.getIntent(MainPrefs.getColg(this),
                MainPrefs.getEnroll(this), MainPrefs.getPassword(this),
                MainPrefs.getBatch(this), this);

        startService(intent);
    }


    public static Intent getIntent(int refreshType, Context context) {
        Intent intent = new Intent(context, ServiceRefresh.class);
        intent.putExtra(RefreshDB.REFRESH_TYPE, refreshType);
        return intent;
    }
}
