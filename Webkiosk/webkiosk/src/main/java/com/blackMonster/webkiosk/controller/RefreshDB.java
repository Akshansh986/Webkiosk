package com.blackMonster.webkiosk.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.notifications.NotificationManager;
import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.services.AlarmService;
import com.blackMonster.webkiosk.services.ServiceRefreshTimetable;
import com.blackMonster.webkiosk.ui.AlertDialogHandler;

public class RefreshDB {
    static final String TAG = "serviceLogin";
    public static final String REFRESH_TYPE = "refType";
    public static final int AUTO_REFRESH = 1;
    public static final int MANUAL_REFRESH = 2;

    public static final int ERROR = -1;
    public static final int OK = 1;


    public static final String BROADCAST_LOGIN_RESULT = "BROADCAST_LOGIN_RESULT";
    public static final String BROADCAST_UPDATE_AVG_ATND_RESULT = "UPDATE_AVG_ATND_RESULT";
    public static final String BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT = "UPDATE_DETAILED_ATTENDENCE_RESULT";

    String enroll, pass, batch, colg;
    int refreshType;
    Context context;


    public RefreshDB(int refreshType, Context context) {
        this.enroll = MainPrefs.getEnroll(context);
        this.pass = MainPrefs.getPassword(context);
        this.batch = MainPrefs.getBatch(context);
        this.colg = MainPrefs.getColg(context);
        this.refreshType = refreshType;
        this.context = context;
    }

    public int refresh() throws SubjectChangedException {
        return refresh(null);
    }


    public int refresh(CrawlerDelegate crawlerDelegate) throws SubjectChangedException {
        M.log(TAG, "refresh started");

        RefreshServicePrefs.resetIfrunningFromLongTime(context);
        if (RefreshServicePrefs.isRunning(context)) return OK;

        RefreshServicePrefs.setRefreshStartTimestamp(context);


        int result;

        try {

            if (crawlerDelegate == null) {
                RefreshServicePrefs.setStatus(RefreshServicePrefs.LOGGING_IN, context);

                crawlerDelegate = new CrawlerDelegate(context);
                result = crawlerDelegate.login(colg, enroll, pass);
                M.log(TAG, "login done result  " + result);

                broadcastResult(BROADCAST_LOGIN_RESULT, result);

                if (result != LoginStatus.LOGIN_DONE) return ERROR;
                M.log(TAG, "login done");
            }

            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_O,
                    context);
            result = UpdateAvgAtnd.update(crawlerDelegate, context);
            M.log(TAG, "UpdateAvgAtnd result" + result);
            broadcastResult(BROADCAST_UPDATE_AVG_ATND_RESULT, result);
            if (result == UpdateAvgAtnd.ERROR) return ERROR;

            RefreshServicePrefs.setRecentlyUpdatedTagVisibility(true, context); //"Recently updated" is marked on subject with changed attendance.


            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_D,
                    context);
            result = UpdateDetailedAttendence.start(crawlerDelegate, context);
            broadcastResult(BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT, result);
            if (result == UpdateDetailedAttendence.ERROR) return ERROR;

            manageAlarmService();

            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_DATESHEET,
                    context);
            updateDatesheet(crawlerDelegate);


            RefreshServicePrefs.setRefreshEndTimestamp(context);
            return OK;

        } finally {

            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED, context);
            RefreshServicePrefs.setFirstRefreshOver(context);
            crawlerDelegate.reset();

//            if (!isFirstTimeLogin) ServiceRefreshTimetable.runIfNotRunning(context);

            //These things has nothing to do with webkiosk servers.
            ServiceRefreshTimetable.runIfNotRunning(context); //TODO check it's effects
            NotificationManager.manageNotificaiton(context);
        }


    }


    private void updateDatesheet(CrawlerDelegate crawlerDelegate) {
        M.log(TAG, "UpdateDatesheet");
        if (RefreshServicePrefs.isFirstRefresh(context))
            DSSPManager.updateDataDontNotify(crawlerDelegate, context);
        else
            DSSPManager.updateDataAndNotify(crawlerDelegate, context);
    }


    private void manageAlarmService() {
        if (RefreshServicePrefs.isFirstRefresh(context))
            context.startService(new Intent(context, AlarmService.class).putExtra(
                    AlarmService.CALLER_TYPE, AlarmService.INSTALLATION_DONE));

    }


    private void broadcastResult(String type, int result) {


        if (type.equals(BROADCAST_LOGIN_RESULT)
                && (result == LoginStatus.INVALID_PASS || result == LoginStatus.ACCOUNT_LOCKED)) {
            RefreshServicePrefs.setPasswordOutdated(context);
        } else if (refreshType == MANUAL_REFRESH) {
            AlertDialogHandler.saveDialogToPref(type, result, batch,
                    false, context);
        }

//        if (refreshType == MANUAL_REFRESH) {
//            if (type.equals(BROADCAST_LOGIN_RESULT)
//                    && (result == LoginStatus.INVALID_PASS || result == LoginStatus.ACCOUNT_LOCKED)) {
//                RefreshServicePrefs.setPasswordOutdated(context);
//            } else
//                AlertDialogHandler.saveDialogToPref(type, result, batch,
//                        false, context);
//        } else {
//            if (refreshType == AUTO_REFRESH) {
//                if (type.equals(BROADCAST_LOGIN_RESULT)
//                        && (result == LoginStatus.INVALID_PASS || result == LoginStatus.ACCOUNT_LOCKED)) {
//                    RefreshServicePrefs.setPasswordOutdated(context);
//                }
//            }

        Intent intent = new Intent(type).putExtra(type, result);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
