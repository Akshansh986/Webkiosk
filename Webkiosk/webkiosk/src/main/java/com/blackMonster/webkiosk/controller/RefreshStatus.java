package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkioskApp.R;


/**
 * Created by akshansh on 30/07/15.
 */
public enum RefreshStatus {
    STOPPED,
    LOGGING_IN,
    REFRESHING_O,
    REFRESHING_D,
    REFRESHING_DATESHEET,
    CREATING_DB;

    public static RefreshStatus getEnumFromString(String name) {
        return RefreshStatus.valueOf(name);
    }

    public String getString() {
        return name();
    }


    public static String getStatusMessage(Context context) {
        STOPPED.name();
        switch (RefreshDBPrefs.getStatus(context)) {

            case REFRESHING_D:
                return context.getString(R.string.refreshing_detailed_atnd);
            case REFRESHING_DATESHEET:
                return context.getString(R.string.refreshing_datesheet);
            default:
                return context.getString(R.string.refresh_in_progress);
        }
    }

}
