package com.blackMonster.webkiosk.SharedPrefs;

/**
 * Created by akshansh on 30/07/15.
 */
public enum RefreshStatus {
    STOPPED("STOPPED","Stopped"),
    LOGGING_IN("LOGIN_RESULT",), REFRESHING_O, REFRESHING_D, REFRESHING_DATESHEET, CREATING_DB;

    String broadcastString, msg;

    RefreshStatus(String broadcastString, String msg) {
        this.broadcastString = broadcastString;
        this.msg = msg;
    }
}
