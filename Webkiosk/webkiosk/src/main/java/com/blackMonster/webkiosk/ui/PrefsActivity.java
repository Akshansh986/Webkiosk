package com.blackMonster.webkiosk.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.utils.NetworkUtils;
import com.blackMonster.webkioskApp.R;
import com.google.analytics.tracking.android.EasyTracker;

public class PrefsActivity extends android.preference.PreferenceActivity
		implements OnPreferenceClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceFile();
		addPreferencesFromResource(R.xml.preferences);

		((Preference) findPreference("logout"))
				.setOnPreferenceClickListener(this);
		((Preference) findPreference("reset_timetable"))
				.setOnPreferenceClickListener(this);
		((Preference) findPreference("pref_about"))
				.setOnPreferenceClickListener(this);
		((Preference) findPreference("pref_contact_us"))
		.setOnPreferenceClickListener(this);
		
		greyPreference pr = ((greyPreference) findPreference("account_info"));
		SharedPreferences settings = getSharedPreferences(
				MainPrefs.PREFS_NAME, 0);
		pr.setTitle(MainPrefs.getUserName(this));
		pr.setSummary(MainPrefs.getEnroll(this));

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {

			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.theme)));
			getActionBar().setTitle("Settings");
			getActionBar().setLogo(getResources().getDrawable(R.drawable.ic_logo));

		}
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			getActionBar().setDisplayHomeAsUpEnabled(true);

		}

	}

	private void setPreferenceFile() {
		PreferenceManager prefMgr = getPreferenceManager();
		prefMgr.setSharedPreferencesName(MainPrefs.PREFS_NAME);
		prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);
	}

	@Override
	public Intent getParentActivityIntent() {
		finish();
		return super.getParentActivityIntent();
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {

		/// Log.d("pref", "on Preference click");
		if (preference.getKey().equals("logout"))
			showLogoutDialog();
		else if (preference.getKey().equals("reset_timetable"))
			resetTimetable();
		else if (preference.getKey().equals("pref_about"))
			startActivity(new Intent(this, AboutActivity.class));
		else if (preference.getKey().equals("pref_contact_us"))
			startActivity(new Intent(this,ActivityCustomerSupport.class));
		return true;
	}

	private void showLogoutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(
				R.string.pref_logout_message));

		builder.setPositiveButton("Logout",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						startLogoutActivity();
					}

				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.show();

	}

	private void startLogoutActivity() {
		startActivity(new Intent(this, LogoutActivity.class));
		finish();
	}

	private void resetTimetable() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(
				R.string.pref_reset_timetable_message));

		builder.setPositiveButton("Restore",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						startTimetableReset();
					}

				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.show();

	}

	private void startTimetableReset() {
		if (NetworkUtils.isInternetAvailable(this)) {
			TimetableDbHelper.clearTimetable(this);
			new ResetTimetable().execute();

		} else
			showInternetNADialog();
	}

	private void showInternetNADialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.con_error));

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

					}

				});

		builder.show();

	}

	class ResetTimetable extends AsyncTask<Void, Void, Void> {
		AlertDialog dialog = createProgressDialog(R.string.loading_timetable);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			dialog = createProgressDialog(R.string.loading_timetable);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			TimetableCreateRefresh.deleteTimetableDb(getBaseContext());
			TimetableCreateRefresh.refresh(getBaseContext());
			//TimetableData.createDb(MainPrefs.getColg(getBaseContext()),
				//	MainPrefs.getSem(getBaseContext()),
					//MainPrefs.getBatch(getBaseContext()),
					//MainPrefs.getEnroll(getBaseContext()), getBaseContext());
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		}

	}

	private AlertDialog createProgressDialog(int msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View myView = getLayoutInflater().inflate(R.layout.login_progressbar,
				null);
		((TextView) myView.findViewById(R.id.login_dialog_msg)).setText(msg);

		builder.setView(myView);
		builder.setCancelable(false);

		return builder.create();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.

	}

}
