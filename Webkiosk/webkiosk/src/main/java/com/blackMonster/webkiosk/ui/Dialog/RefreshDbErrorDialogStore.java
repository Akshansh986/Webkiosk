package com.blackMonster.webkiosk.ui.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.RefreshBroadcasts;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.controller.appLogin.CreateDatabase;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateAvgAtnd;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateDetailedAttendence;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.controller.appLogin.InitDB;
import com.blackMonster.webkioskApp.R;

/**
 * Error messages dialog handler.
 *
 * Complete dialog handling is done manually. Ex what happens when app minimizes and resumes.
 * Every error dialog to be displayed is first saved here. If activity is online it is displayed instantaneously
 * and cleared from here. If not, then onResume()  of most of acivity checks and display if any dialog is present here.
 *
 */
public class RefreshDbErrorDialogStore {


    static AlertDialog myDialog = null;


    public static void showDialogIfPresent(Context context) {
        String dialogMsg = MainPrefs.getSavedDialogMsg(context);

        if (!dialogMsg.equals(MainPrefs.SAVE_DIALOG_DEFAULT_MSG)) {
            myDialog = createAlertDialog(dialogMsg, context);
            myDialog.show();
        }
    }

    public static void dismissIfPresent() {
        if (myDialog != null) {
            myDialog.dismiss();
            myDialog = null;
        }
    }

    private static AlertDialog createAlertDialog(String msg,
                                                final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myDialog = null;
                        MainPrefs.setSaveDialogMsg(MainPrefs.SAVE_DIALOG_DEFAULT_MSG, context);
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                myDialog = null;
                MainPrefs.setSaveDialogMsg(MainPrefs.SAVE_DIALOG_DEFAULT_MSG, context);
            }
        });

        builder.setMessage(msg);
        return builder.create();

    }

    public static void store(String type, int result, Context context) {

        if (type.equals(RefreshBroadcasts.BROADCAST_LOGIN_RESULT)) {
            if (result != LoginStatus.LOGIN_DONE)
                addToPrefs(LoginStatus.responseToString(context, result), context);
        } else if (type.equals(RefreshBroadcasts.BROADCAST_UPDATE_AVG_ATND_RESULT)) {
            if (result == UpdateAvgAtnd.ERROR)
                addToPrefs(context.getString(R.string.attendence_update_error),
                        context);
        } else if (type
                .equals(InitDB.BROADCAST_DATEBASE_CREATION_RESULT)) {
            if (result == CreateDatabase.ERROR)
                addToPrefs(context.getString(R.string.database_creation_error),
                        context);
            else if (result == TimetableCreateRefresh.ERROR_BATCH_UNAVAILABLE)
                addToPrefs(context.getString(R.string.invalid_batch), context);
            else if (result == TimetableCreateRefresh.ERROR_CONNECTION)
                addToPrefs(
                        context.getString(R.string.error_timetableserver_connection),
                        context);
            else if (result == TimetableCreateRefresh.ERROR_UNKNOWN)
                addToPrefs(context.getString(R.string.unknown_error), context);
        } else if (type
                .equals(RefreshBroadcasts.BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT)) {
            if (result == UpdateDetailedAttendence.ERROR)
                addToPrefs(context.getString(R.string.attendence_update_error),
                        context);
        }

    }

    private static void addToPrefs(String msg, Context context) {
        MainPrefs.setSaveDialogMsg(msg, context);
    }

}
