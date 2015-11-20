package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.services.ServiceRefreshAll;
import com.blackMonster.webkiosk.ui.Dialog.ChangePasswordDialog;
import com.blackMonster.webkiosk.utils.AppRater;

/**
 * Any activity capable of launching at app launch extends StartupActivity(Except loginActivity)
 * Example : TimetableActivity is to be opened if timetable is available otherwise AtndOverviewActivity will be opened, so
 * both extends StartupActivity.
 */
public class StartupActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (isStartupActivity(getClass(), this)) start();
		super.onCreate(savedInstanceState);
	}
	
	private void start() {
		handleLogout(getIntent(), this);
		AppRater.app_launched(this);
		showDialogIfPasswordChanged(this);
	}

	/**
	 * Returns activity to be launched at app startup.
	 */
	public static Class<?> getStartupActivity(Context context) {
		if (MainPrefs.getStartupActivityName(context).equals(TimetableActivity.class.getSimpleName())){
			return TimetableActivity.class;
		}
		else{
			return AtndOverviewActivity.class;
		}
		
	}

	/**
	 * Set activity to be launched at app startup.
     *
     * Note : It's pretty lame to first store and then return startupActivity with "getStartupActivity", but I don't
     * know the reason why I had done it in first place.
     * Combining both functions might introduce bugs, so I am leaving it as it is.
	 */
	public static void setStartupActivity(Context context) {
		if (TimetableDbHelper.databaseExists(context)) {
			MainPrefs.storeStartupActivity(context, TimetableActivity.class.getSimpleName());
		}
		else
			MainPrefs.storeStartupActivity(context, AtndOverviewActivity.class.getSimpleName());
	}
	
	public static boolean isStartupActivity(Class<?> act,Context context) {
		if (act==getStartupActivity(context)) return true;
		else
			return false;
	}

	private static void showDialogIfPasswordChanged(Activity activity) {
		if (!RefreshDBPrefs.isPasswordUptoDate(activity))
			ChangePasswordDialog.show(activity);
	}


	/*
    When sem changes, every subject is changed. So all database has to be recreated.
    It's somewhat like we are doing fresh login.
    This function call login activity with required parameters.
     */
	private static void handleLogout(Intent inputIntent,Activity activity) {
		boolean finish = inputIntent.getBooleanExtra(LogoutActivity.FINISH,
				false);
		if (finish) {
			Intent intent = new Intent(activity, LoginActivity.class);
			intent.putExtra(
					ServiceRefreshAll.RECREATING_DATABASE,
					inputIntent.getBooleanExtra(
							ServiceRefreshAll.RECREATING_DATABASE, false));
			activity.startActivity(intent);
			activity.finish();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RefreshDBPrefs.setRecentlyUpdatedTagVisibility(false, this);
	}

}
