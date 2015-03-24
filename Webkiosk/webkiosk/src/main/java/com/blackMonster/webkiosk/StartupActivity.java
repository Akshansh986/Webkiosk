package com.blackMonster.webkiosk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class StartupActivity extends BaseActivity{
	
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
	
	
	public static Class<?> getStartupActivity(Context context) {
		if (MainPrefs.getStartupActivityName(context).equals(TimetableActivity.class.getSimpleName())){
			///Log.d("startup", "timetable");
			return TimetableActivity.class;
		}
		else{
			///Log.d("startup", "atndo");
			return AtndOverviewActivity.class;
		}
		
	}
	
	public static void setStartupActivity(Context context) {
		if (TimetableDataHelper.databaseExists(context)) {
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
		if (!RefreshServicePrefs.isPasswordUptoDate(activity))
			MyAlertDialog.showChangePasswordDialog(activity);
	}
	
	private static void handleLogout(Intent inputIntent,Activity activity) {
		// Log.d(TAG, "handling logout");
		boolean finish = inputIntent.getBooleanExtra(LogoutActivity.FINISH,
				false);
		if (finish) {
			// Log.d(TAG, "logging out through timetable");
			Intent intent = new Intent(activity, LoginActivity.class);
			intent.putExtra(
					ServiceLoginRefresh.RECREATING_DATABASE,
					inputIntent.getBooleanExtra(
							ServiceLoginRefresh.RECREATING_DATABASE, false));
			activity.startActivity(intent);
			activity.finish();
			return;
		}
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

}
