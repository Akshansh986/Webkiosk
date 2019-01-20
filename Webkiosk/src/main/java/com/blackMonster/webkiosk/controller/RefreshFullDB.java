package com.blackMonster.webkiosk.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.notifications.NotificationManager;
import com.blackMonster.webkiosk.ui.Dialog.RefreshDbErrorDialogStore;
import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.controller.updateAtnd.SubjectChangedException;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateAvgAtnd;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateDetailedAttendance;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.services.AutoRefreshAlarmService;
import com.blackMonster.webkiosk.services.ServiceRefreshTimetable;

public class RefreshFullDB {
    static final String TAG = "serviceLogin";
    public static final String REFRESH_TYPE = "refType";
    public static final int AUTO_REFRESH = 1;       //Refresh automatically done by app.
    public static final int MANUAL_REFRESH = 2;     //Type of refresh when user press refresh button.
    public static final int ERROR = -1;
    public static final int OK = 1;
    String enroll, pass, batch, colg, dob;
    int refreshType;        //Auto refresh or manual refresh.
    Context context;

    /**
     * @param refreshType AUTO_REFRESH 0R MANUAL_REFRESH
     * @param context
     */
    public RefreshFullDB(int refreshType, Context context) {
        this.enroll = MainPrefs.getEnroll(context);
        this.pass = MainPrefs.getPassword(context);
        this.batch = MainPrefs.getBatch(context);
        this.colg = MainPrefs.getColg(context);
        this.dob = MainPrefs.getDOB(context);
        this.refreshType = refreshType;
        this.context = context;
    }

    public int refresh() throws SubjectChangedException {
        return refresh(null);
    }


    public int refresh(CrawlerDelegate crawlerDelegate) throws SubjectChangedException {
        M.log(TAG, "refresh started");

        RefreshDBPrefs.resetIfrunningFromLongTime(context);
        if (RefreshDBPrefs.isRunning(context)) return OK;

        RefreshDBPrefs.setRefreshStartTimestamp(context);


        int result;

        try {

            if (crawlerDelegate == null) {
                RefreshDBPrefs.setStatus(RefreshStatus.LOGGING_IN, context);

                crawlerDelegate = new CrawlerDelegate(context);
                result = crawlerDelegate.login(colg, enroll, pass, dob);
                M.log(TAG, "login done result  " + result);

                broadcastResult(RefreshBroadcasts.BROADCAST_LOGIN_RESULT, result);

                if (result != LoginStatus.LOGIN_DONE) return ERROR;
                M.log(TAG, "login done");
            }

            RefreshDBPrefs.setStatus(RefreshStatus.REFRESHING_O,
                    context);
            result = UpdateAvgAtnd.update(crawlerDelegate, context);
            M.log(TAG, "UpdateAvgAtnd result" + result);
            broadcastResult(RefreshBroadcasts.BROADCAST_UPDATE_AVG_ATND_RESULT, result);
            if (result == UpdateAvgAtnd.ERROR) return ERROR;

            RefreshDBPrefs.setRecentlyUpdatedTagVisibility(true, context); //"Recently updated" is marked on subject with changed attendance.


            RefreshDBPrefs.setStatus(RefreshStatus.REFRESHING_D,
                    context);
            result = UpdateDetailedAttendance.start(crawlerDelegate, context);
            broadcastResult(RefreshBroadcasts.BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT, result);
            if (result == UpdateDetailedAttendance.ERROR) return ERROR;

            manageAlarmService();       //For auto refresh.

            RefreshDBPrefs.setStatus(RefreshStatus.REFRESHING_DATESHEET,
                    context);
            updateDatesheet(crawlerDelegate);  //Broadcast and error checking is not done for datesheet as it is not that necessary.


            RefreshDBPrefs.setRefreshEndTimestamp(context);
            return OK;

        } finally {

            RefreshDBPrefs.setStatus(RefreshStatus.STOPPED, context);
            RefreshDBPrefs.setFirstRefreshOver(context);    //Indented to call only when it is first refresh. But calling it every time doesn't harm.
            crawlerDelegate.reset();

            ServiceRefreshTimetable.runIfNotRunning(context); //Start refresh timetable.
            NotificationManager.manageNotificaiton(context);
        }


    }


    private void updateDatesheet(CrawlerDelegate crawlerDelegate) {
        M.log(TAG, "UpdateDatesheet");
        if (RefreshDBPrefs.isFirstRefresh(context))
            DSSPManager.updateDataDontNotify(crawlerDelegate, context);
        else
            DSSPManager.updateDataAndNotify(crawlerDelegate, context);
    }


    private void manageAlarmService() {
        if (RefreshDBPrefs.isFirstRefresh(context))
            context.startService(new Intent(context, AutoRefreshAlarmService.class).putExtra(
                    AutoRefreshAlarmService.CALLER_TYPE, AutoRefreshAlarmService.INSTALLATION_DONE));
    }

    //Broadcast result of every step of DB initialization, so that UI elements can act accordingly.
    private void broadcastResult(String  type, int result) {

        if (type.equals(RefreshBroadcasts.BROADCAST_LOGIN_RESULT)
                && (result == LoginStatus.INVALID_PASS || result == LoginStatus.ACCOUNT_LOCKED)) {
            RefreshDBPrefs.setPasswordOutdated(context);
        } else if (refreshType == MANUAL_REFRESH) {
            //Not done in case of Auto refresh because we won't like to show a error dialog( ex. connectivity lost)
            // for refresh of which user is not even aware of.
            RefreshDbErrorDialogStore.store(type, result, context);
        }

        Intent intent = new Intent(type).putExtra(type, result);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
