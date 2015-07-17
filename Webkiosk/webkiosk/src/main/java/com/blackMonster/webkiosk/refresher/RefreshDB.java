package com.blackMonster.webkiosk.refresher;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.notifications.NotificationManager;
import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.controller.DSSPManager;
import com.blackMonster.webkiosk.controller.UpdateAvgAtnd;
import com.blackMonster.webkiosk.controller.UpdateDetailedAttendence;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.ui.AlertDialogHandler;

public class RefreshDB {
    static final String TAG = "serviceLogin";
    public static final String REFRESH_TYPE = "refType";
    public static final int AUTO_REFRESH = 1;
    public static final int MANUAL_REFRESH = 2;

    public static final int ERROR = -1;
    public static final int OK = 1;
    public static final int SUBJECT_CHANGED = 2;



    public static final String FIRST_TIME_LOGIN = "fistTimeLogin";

    //public static final String BROADCAST_TIMETALE_LOAD = "BROADCAST_TIMETABLE_LOAD";
    public static final String BROADCAST_LOGIN_RESULT = "BROADCAST_LOGIN_RESULT";
    public static final String BROADCAST_UPDATE_ATND_RESULT = "TEMP_ATND_DATA_RESULT";
    public static final String BROADCAST_UPDATE_ATTENDENCE_RESULT = "BROADCAST_UPDATE_ATTENDENCE_RESULT";

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

    public int refresh() {
        return refresh(null);
    }


    public int refresh(CrawlerDelegate crawlerDelegate) {
        M.log(TAG, "refresh started");

        RefreshServicePrefs.resetIfrunningFromLongTime(context);
        if (RefreshServicePrefs.isRunning(context)) return OK;

        RefreshServicePrefs.setRefreshStartTimestamp(context);



        int result;

        try {

            if (crawlerDelegate == null) {
                RefreshServicePrefs.setStatus(RefreshServicePrefs.LOGGING_IN, context);

                crawlerDelegate = new CrawlerDelegate(context);
                result =  crawlerDelegate.login(colg, enroll, pass);
                M.log(TAG, "login done result  " + result);

                broadcastResult(BROADCAST_LOGIN_RESULT, result);

                if (result != LoginStatus.LOGIN_DONE) return ERROR;
                M.log(TAG, "login done");
            }

            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_O,
                    context);

            result = UpdateAvgAtnd.update(crawlerDelegate, context);

            M.log(TAG, "UpdateAvgAtnd result" + result);


            if (result == AttendenceOverviewTable.SUBJECT_CHANGED) {
                return SUBJECT_CHANGED;
            }
            broadcastResult(BROADCAST_UPDATE_ATND_RESULT, result);

            if (result == UpdateAvgAtnd.ERROR) return ERROR;

            RefreshServicePrefs.putRecentlyUpdatedTag(true, context);

            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_D,
                    context);
            result = UpdateDetailedAttendence.start(crawlerDelegate, context);

            broadcastResult(BROADCAST_UPDATE_ATTENDENCE_RESULT, result);
            if (result == UpdateDetailedAttendence.DONE)
                manageAlarmService();

            RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_DATESHEET,
                    context);
            updateDatesheet(crawlerDelegate);

//
//
//
//            if (result == UpdateAvgAtnd.ERROR) {
//                broadcastResult(BROADCAST_UPDATE_ATND_RESULT, result);
//            } else if (result == AttendenceOverviewTable.SUBJECT_CHANGED) {
//                isSubjectChanged = true;
//            } else {
//                RefreshServicePrefs.putRecentlyUpdatedTag(true, context);
//                broadcastResult(BROADCAST_UPDATE_ATND_RESULT, result);
//                RefreshServicePrefs.setStatus(RefreshServicePrefs.REFRESHING_D,
//                        context);
//
//                result = UpdateDetailedAttendence.start(crawlerDelegate, context);
//
//                broadcastResult(BROADCAST_UPDATE_ATTENDENCE_RESULT, result);
//                if (result == UpdateDetailedAttendence.DONE)
//                    manageAlarmService();
//                updateDatesheet(crawlerDelegate);
//            }
        } finally {

            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED, context);
            RefreshServicePrefs.setFirstRefreshOver(context);
            crawlerDelegate.reset();

//            if (!isFirstTimeLogin) ServiceRefreshTimetable.runIfNotRunning(context);
            ServiceRefreshTimetable.runIfNotRunning(context); //TODO check it's effects

//            if (isSubjectChanged) {
//                recreateDatabase();
//                return;
//            }

            NotificationManager.manageNotificaiton(context);
            M.log(TAG, "all done");
        }
        return OK;

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
