package com.blackMonster.webkiosk.refresher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.PremiumManager;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.controller.CreateDatabase;
import com.blackMonster.webkiosk.Timetable.TimetableHandler;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.ui.AlertDialogHandler;

/**
 * Created by akshansh on 16/07/15.
 */
public class InitDB {


    public static final String BROADCAST_DATEBASE_CREATION_RESULT = "BROADCAST_DATEBASE_CREATION_RESULT";
    private static final String TAG = "InitDB";

    String enroll, pass, batch, colg;
    Context context;

    CrawlerDelegate crawlerDelegate;


    public InitDB(String enroll, String pass, String batch, String colg, Context context) {
        this.enroll = enroll;
        this.pass = pass;
        this.batch = batch;
        this.colg = colg;
        this.context = context;
    }

    /*
     * TEMPLATE:
     *
     * RefreshServicePrefs.setStatus(..);
     * do work....
     * broadcastResult(..);
     * Error handling
     *
     * RefreshServicePrefs.setStatus(..);
     *      .
     *      .
     */

    public boolean start() {
        int result;

        try {
            RefreshServicePrefs.setStatus(RefreshServicePrefs.LOGGING_IN, context);
            crawlerDelegate = new CrawlerDelegate(context);
            result = crawlerDelegate.login(colg, enroll, pass);
            broadcastResult(RefreshDB.BROADCAST_LOGIN_RESULT, result);

            if (result != LoginStatus.LOGIN_DONE) return false;
            M.log(TAG, "login done");

            RefreshServicePrefs.setStatus(RefreshServicePrefs.CREATING_DB, context);
            result = CreateDatabase.start(colg, enroll, batch, crawlerDelegate, context);
            broadcastResult(BROADCAST_DATEBASE_CREATION_RESULT, result);

            if (isCreateDatabaseSuccessful(result)) {
                saveFirstTimeloginPreference();
                return true;
            } else return false;

        } finally {
            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED,
                    context);
        }

    }

    public CrawlerDelegate getCrawlerDelegate() {
        return crawlerDelegate;
    }

    private void saveFirstTimeloginPreference() {
        SharedPreferences settings = context.getSharedPreferences(
                MainPrefs.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean("hasLoggedIn", true);
        editor.putString(MainPrefs.ENROLL_NO, enroll);
        editor.putString(MainPrefs.PASSWORD, pass);
        editor.putString(MainPrefs.BATCH, batch);
        //editor.putInt(MainPrefs.SEM, sem);
        editor.putString(AlarmService.PREF_AUTO_UPDATE_OVER, "anyNetwork");

        editor.putString(MainPrefs.COLG, colg);

        editor.commit();
        PremiumManager.setFirstTimeDone(context.getApplicationContext());
    }

    private void broadcastResult(String type, int result) {
        AlertDialogHandler.saveDialogToPref(type, result, batch,
                true, context);

        Intent intent = new Intent(type).putExtra(type, result);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private boolean isCreateDatabaseSuccessful(int result) {
        return !(result == CreateDatabase.ERROR || TimetableHandler.isError(result));
//                ) {
//            ///M.log(TAG, "create database error");
//            broadcastResult(BROADCAST_DATEBASE_CREATION_RESULT, result);
//            RefreshServicePrefs.setStatus(RefreshServicePrefs.STOPPED,
//                    context);
//            return false;
//        }
//        return true;
    }
}

