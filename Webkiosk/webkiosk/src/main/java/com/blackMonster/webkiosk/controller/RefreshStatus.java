package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkioskApp.R;


/**
 * Enum for phases that refresher goes while refreshing full database.
 */
public enum RefreshStatus {
    STOPPED,
    LOGGING_IN,
    REFRESHING_AVG_ATND,
    REFRESHING_D,       //refreshing detailed attendance.
    REFRESHING_DATESHEET,
    CREATING_DB;

    public static RefreshStatus getEnumFromString(String name) {
        return RefreshStatus.valueOf(name);
    }

    public String getString() {
        return name();
    }

    /**
     * Returns messages that can be shown in UI for each phase of refresh.
     * @param context
     * @return
     */
    public static String getStatusMessage(Context context) {
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
