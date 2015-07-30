package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.WebkioskApp;
import com.crittercism.app.Crittercism;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RefreshDBPrefs.resetIfrunningFromLongTime(this);

        try {
            Crittercism.initialize(getApplicationContext(), "53eb5a1683fb796b50000004");
        } catch (RuntimeException e) {
            M.log(TAG, e.getLocalizedMessage());
        }


        if (WebkioskApp.canViewAttendance(this)) {
            launchStartupActivity(this);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();

    }


    public static void launchStartupActivity(Context context) {
        StartupActivity.setStartupActivity(context);
        context.startActivity(new Intent(context, StartupActivity
                .getStartupActivity(context)));
    }

}
