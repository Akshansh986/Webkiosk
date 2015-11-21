package com.blackMonster.webkiosk.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.utils.NetworkUtils;

public class ServiceRefreshTimetable extends IntentService {
    public static boolean RUNNING_STATUS = false;
    public static final String TAG = "serviceRefreshTimetable";

    public ServiceRefreshTimetable() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        M.log("ServiceRefreshTimetable", "onHandleIntent");
        startExecution();
    }

    private void startExecution() {
        if (!NetworkUtils.isInternetAvailable(this))
            return;
        RUNNING_STATUS = true;
        TimetableCreateRefresh.refresh(this);
        RUNNING_STATUS = false;
    }

    public static void runIfNotRunning(Context context) {
        if (RUNNING_STATUS == false) {
            Intent intent = new Intent(context.getApplicationContext(),
                    ServiceRefreshTimetable.class);
            context.getApplicationContext().startService(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RUNNING_STATUS = false;
    }

}
