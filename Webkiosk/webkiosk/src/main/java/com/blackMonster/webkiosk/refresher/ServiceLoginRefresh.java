package com.blackMonster.webkiosk.refresher;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.notifications.NotificationManager;
import com.blackMonster.webkiosk.CreateDatabase;
import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.PremiumManager;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkiosk.TempAtndData;
import com.blackMonster.webkiosk.Timetable;
import com.blackMonster.webkiosk.WebkioskApp;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.dateSheet.DSSPManager;
import com.blackMonster.webkiosk.ui.LoginActivity;
import com.blackMonster.webkiosk.ui.LogoutActivity;
import com.blackMonster.webkiosk.ui.MyAlertDialog;

public class ServiceLoginRefresh extends IntentService {
    static final String TAG = "serviceLogin";
    public static final String REFRESH_TYPE = "refType";
    public static final int AUTO_REFRESH = 1;
    public static final int MANUAL_REFRESH = 2;
    public static final String FIRST_TIME_LOGIN = "fistTimeLogin";
    public static final String RECREATING_DATABASE = "recreateDatabase";

    //public static final String BROADCAST_TIMETALE_LOAD = "BROADCAST_TIMETABLE_LOAD";
    public static final String BROADCAST_LOGIN_RESULT = "BROADCAST_LOGIN_RESULT";
    public static final String BROADCAST_TEMP_ATND_RESULT = "TEMP_ATND_DATA_RESULT";
    public static final String BROADCAST_DATEBASE_CREATION_RESULT = "BROADCAST_DATEBASE_CREATION_RESULT";
    public static final String BROADCAST_UPDATE_ATTENDENCE_RESULT = "BROADCAST_UPDATE_ATTENDENCE_RESULT";

    String enroll, pass, batch, colg;
    int refreshType;
    boolean isFirstTimeLogin;

    public ServiceLoginRefresh() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RefreshServicePrefs.resetIfrunningFromLongTime(this);

        if (RefreshServicePrefs.isRunning(this))
            return;
        else {
            RefreshServicePrefs.setRefreshStartTimestamp(this);
            RefreshServicePrefs.setStatus(RefreshServicePrefs.LOGGING_IN, this);
        }
        initGlobalVariables(intent);


        new Thread() {
            public void run() {
                try {
                    strat();
                } catch (Exception e) {
                }
            }

        }.start();

    }

    private void strat() {

        ((WebkioskApp) getApplication()).resetSiteConnection();

        int result;

        boolean isSubjectChanged = false;
        ((WebkioskApp) getApplication()).connect = new SiteConnection(colg);
        M.log(TAG, "login");

        result = login();
        broadcastResult(BROADCAST_LOGIN_RESULT, result);
        if (result == SiteConnection.LOGIN_DONE) {
            M.log(TAG, "login done");
            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_O,
                    this);

            if (isFirstTimeLogin) {
                M.log(TAG, "first time login");

                result = CreateDatabase.start(colg, enroll, batch, this); // TempAtndData.storeData(this)
                // called
                // inside
                // this
                if (!isCreateDatabaseSuccessful(result))
                    return;
                saveFirstTimeloginPreference();
            } else {
                result = TempAtndData.storeData(this);
            }

            M.log(TAG, "temp atnd data result" + result);

            if (result == TempAtndData.ERROR) {
                broadcastResult(BROADCAST_TEMP_ATND_RESULT, result);
            } else if (result == AttendenceOverviewTable.SUBJECT_CHANGED) {
                isSubjectChanged = true;
            } else {
                RefreshServicePrefs.putRecentlyUpdatedTag(true, this);
                broadcastResult(BROADCAST_TEMP_ATND_RESULT, result);
                RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_D,
                        this);

                result = UpdateAttendence.start(this);

                broadcastResult(BROADCAST_UPDATE_ATTENDENCE_RESULT, result);
                if (result == UpdateAttendence.DONE)
                    ManageAlarmService();
                updateDatesheet();
            }

            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED, this);

        } else {
            M.log(TAG, " login error");

            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED, this);
        }
        ((WebkioskApp) getApplication()).resetSiteConnection();
        if (!isFirstTimeLogin) ServiceRefreshTimetable.runIfNotRunning(this);
        if (isSubjectChanged)
            recreateDatabase();

        NotificationManager.manageNotificaiton(this);
        M.log(TAG, "all done");
    }


    private void updateDatesheet() {
        M.log(TAG, "UpdateDatesheet");
        if (isFirstTimeLogin)
            DSSPManager.updateDataDontNotify(((WebkioskApp) getApplication()).connect, this);
        else
            DSSPManager.updateDataAndNotify(((WebkioskApp) getApplication()).connect, this);


    }

    private boolean isCreateDatabaseSuccessful(int result) {
        if (result == CreateDatabase.ERROR || Timetable.isError(result)
                ) {
            ///M.log(TAG, "create database error");
            broadcastResult(BROADCAST_DATEBASE_CREATION_RESULT, result);
            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED,
                    this);
            return false;
        }
        return true;
    }


    public void recreateDatabase() {
        resetPrefs();
        LogoutActivity.unallocateRecource(this);
        Timetable.deleteTimetableDb(this);
        LogoutActivity.deleteAttendence(this);
        // M.log(TAG, "cleared");
        if (isAutoRefresh()) {
            startServiceLoginRefresh();
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

        Editor ed = getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECREATING_DATABASE, true);
        getApplication().startActivity(intent);

    }

    private boolean isAutoRefresh() {
        return refreshType == AUTO_REFRESH;
    }

    private void startServiceLoginRefresh() {
        // M.log(TAG, "starting srvice login refresh");

        Intent intent = ServiceLoginRefresh.getIntent(MainPrefs.getColg(this),
                MainPrefs.getEnroll(this), MainPrefs.getPassword(this),
                MainPrefs.getBatch(this), AUTO_REFRESH,
                true, this);

        startService(intent);
    }

    private void ManageAlarmService() {
        if (isFirstTimeLogin)
            startService(new Intent(this, AlarmService.class).putExtra(
                    AlarmService.CALLER_TYPE, AlarmService.INSTALLATION_DONE));

    }

    private void saveFirstTimeloginPreference() {
        SharedPreferences settings = getSharedPreferences(
                MainActivity.PREFS_NAME, 0);
        Editor editor = settings.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.putString(MainPrefs.ENROLL_NO, enroll);
        editor.putString(MainPrefs.PASSWORD, pass);
        editor.putString(MainPrefs.BATCH, batch);
        //editor.putInt(MainPrefs.SEM, sem);
        editor.putString(AlarmService.PREF_AUTO_UPDATE_OVER, "anyNetwork");

        editor.putString(MainPrefs.COLG, colg);

        editor.commit();
        PremiumManager.setFirstTimeDone(getApplicationContext());
    }

    public void broadcastResult(String type, int result) {
        // M.log(TAG, type);

        if (refreshType == MANUAL_REFRESH) {
            // M.log(TAG, "manualrefresh --> is firstTimeLOgin : " +
            // isFirstTimeLogin);
            if (!isFirstTimeLogin
                    && type.equals(BROADCAST_LOGIN_RESULT)
                    && (result == SiteConnection.INVALID_PASS || result == SiteConnection.ACCOUNT_LOCKED)) {
                // M.log(TAG, "invalid pass or ac locked");
                RefreshServicePrefs.setPasswordOutdated(this);
            } else
                MyAlertDialog.saveDialogToPref(type, result, batch,
                        isFirstTimeLogin, this);
        } else {
            if (refreshType == AUTO_REFRESH) {
                if (type.equals(BROADCAST_LOGIN_RESULT)
                        && (result == SiteConnection.INVALID_PASS || result == SiteConnection.ACCOUNT_LOCKED)) {
                    RefreshServicePrefs.setPasswordOutdated(this);
                }
            }
        }

        Intent intent = new Intent(type).putExtra(type, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private int login() {
        // M.log(TAG, "logging in..");

        int response = ((WebkioskApp) getApplication()).connect.login(enroll,
                pass, this);
        // M.log(TAG, "res" + response);

        return response;

    }

    private void initGlobalVariables(Intent intent) {
        isFirstTimeLogin = intent.getExtras().getBoolean(FIRST_TIME_LOGIN);
        colg = intent.getExtras().getString(MainPrefs.COLG);
        enroll = intent.getExtras().getString(MainPrefs.ENROLL_NO);
        pass = intent.getExtras().getString(MainPrefs.PASSWORD);
        batch = intent.getExtras().getString(MainPrefs.BATCH);
        //sem = intent.getExtras().getInt(MainPrefs.SEM);
        refreshType = intent.getExtras().getInt(REFRESH_TYPE);

    }

    public static Intent getIntent(String colg, String enroll, String pass, String batch,
                                   int refreshType, boolean isFirstTimeLogin, Context context) {
        Intent intent = new Intent(context, ServiceLoginRefresh.class);
        intent.putExtra(MainPrefs.COLG, colg);
        intent.putExtra(MainPrefs.ENROLL_NO, enroll);
        intent.putExtra(MainPrefs.PASSWORD, pass);
        intent.putExtra(MainPrefs.BATCH, batch);
        //intent.putExtra(MainPrefs.SEM, sem);
        intent.putExtra(REFRESH_TYPE, refreshType);
        intent.putExtra(FIRST_TIME_LOGIN, isFirstTimeLogin);
        return intent;
    }

}
