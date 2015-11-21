package com.blackMonster.webkiosk.crawler;

import android.content.Context;

import com.blackMonster.webkioskApp.R;

/**
 * Created by akshansh on 08/07/15.
 */
public class LoginStatus {
    public static final int LOGIN_DONE = 1;
    public static final int CONN_ERROR = 2;
    public static final int INVALID_PASS = 3;
    public static final int INVALID_ENROLL = 4;
    public static final int ACCOUNT_LOCKED = 5;
    public static final int UNKNOWN_ERROR = 6;

    public static  String responseToString(Context context,int response) {
        switch (response) {

        case INVALID_PASS:
                return context.getString(R.string.invalid_pass);

        case INVALID_ENROLL:
            return context.getString(R.string.invalid_enroll);

        case CONN_ERROR:
            return context.getString(R.string.con_error);
        case ACCOUNT_LOCKED :
                return context.getString(R.string.webkiosk_account_locked_at_first_login);

        case UNKNOWN_ERROR:
            return context.getString(R.string.unknown_error);

        default:
            return null;

        }
    }
}
