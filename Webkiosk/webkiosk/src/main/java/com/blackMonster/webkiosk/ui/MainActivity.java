package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RefreshDBPrefs.resetIfrunningFromLongTime(this);

        if (UIUtils.canViewAttendance(this)) {
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
