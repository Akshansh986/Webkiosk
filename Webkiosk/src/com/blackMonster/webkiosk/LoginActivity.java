package com.blackMonster.webkiosk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class LoginActivity extends ActionBarActivity implements
		OnItemSelectedListener {
	static final String TAG = "LoginActivity";
	AlertDialog dialog = null;
	String prefColg;
	boolean isRecreatingDatabase = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "oncreate");

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.theme)));
		getSupportActionBar().setLogo(
				getResources().getDrawable(R.drawable.ic_logo));

		setContentView(R.layout.activity_login);
		initSpinner();
	}

	private void startLogginIfRecreating() {
		isRecreatingDatabase = getIntent().getBooleanExtra(
				ServiceLoginRefresh.RECREATING_DATABASE, false);
		if (isRecreatingDatabase)
			startServiceLoginRefresh(MainPrefs.getColg(this),
					MainPrefs.getEnroll(this), MainPrefs.getPassword(this),
					MainPrefs.getBatch(this));

	}

	private void initSpinner() {
		// createSimpleSpinner(R.id.sem_select, R.array.prefs_sem);
		createSimpleSpinner(R.id.colg_select, R.array.prefs_colg_name);
	}

	private void createSimpleSpinner(int spinnerId, int arrayId) {
		Spinner spinner = (Spinner) findViewById(spinnerId);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, arrayId, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	public void ButtonLogin(View v) {
		((WebkioskApp) getApplication()).nullifyAllVariables();
		hideKeyboard();
		String enroll, pass, batch;

		// Log.d(TAG, "Button clicked");
		enroll = ((EditText) findViewById(R.id.enroll_num)).getEditableText()
				.toString().trim();
		pass = ((EditText) findViewById(R.id.password)).getEditableText()
				.toString().trim();
		batch = ((EditText) findViewById(R.id.batch_select)).getEditableText()
				.toString().trim().toUpperCase();

		if (!isValidDetails(enroll, pass, batch))
			return;

		if (!SiteConnection.isInternetAvailable(LoginActivity.this)) {
			// Log.d(TAG, getString(R.string.con_error));
			Toast.makeText(this, getString(R.string.con_error),
					Toast.LENGTH_SHORT).show();

		} else {

			/*
			 * Intent intent = new Intent(this, ServiceLoginRefresh.class);
			 * intent.putExtra(ServiceLoginRefresh.ENROLL_NO, enroll);
			 * intent.putExtra(ServiceLoginRefresh.PASSWORD, pass);
			 * intent.putExtra(ServiceLoginRefresh.FIRST_TIME_LOGIN, true);
			 * intent.putExtra(ServiceLoginRefresh.BATCH, batch);
			 * intent.putExtra(ServiceLoginRefresh.REFRESH_TYPE,
			 * ServiceLoginRefresh.MANUAL_REFRESH);
			 */
			startServiceLoginRefresh(prefColg, enroll, pass, batch);
		}

	}

	private void startServiceLoginRefresh(String colg, String enroll,
			String pass, String batch) {
		startService(ServiceLoginRefresh.getIntent(colg, enroll, pass, batch,
				ServiceLoginRefresh.MANUAL_REFRESH, true, this));
		dialog = createProgressDialog(R.string.logging_in);
		dialog.show();
	}

	private boolean isValidDetails(String enroll, String pass, String batch) {
		if (enroll.equals("") || pass.equals("") || batch.equals("")) {

			return false;
		} else
			return true;
	}

	/*
	 * private BroadcastReceiver broadcastTimetableloadResult = new
	 * BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) {
	 * //Log.d(TAG, "received : broadcastTimetableloadResult");
	 * 
	 * 
	 * int result; result = intent.getExtras().getInt(
	 * ServiceLoginRefresh.BROADCAST_TIMETABLE_LOAD);
	 * 
	 * 
	 * }
	 * 
	 * };
	 */
	private BroadcastReceiver broadcastLoginResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.d(TAG, "received : broadcastLoginResult");
			if (dialog != null)
				dialog.dismiss();
			dialog = null;
			int result;
			result = intent.getExtras().getInt(
					ServiceLoginRefresh.BROADCAST_LOGIN_RESULT);
			if (result == SiteConnection.LOGIN_DONE) {
				dialog = createProgressDialog(R.string.loading);
				dialog.show();
			} else {
				MyAlertDialog.checkDialog(LoginActivity.this);
			}

		}

	};

	void manageProgressDialog() {
		if (RefreshServicePrefs.isStatus(RefreshServicePrefs.LOGGING_IN, this)) {
			dialog = createProgressDialog(R.string.logging_in);
			dialog.show();
		} else if (RefreshServicePrefs.isStatus(
				RefreshServicePrefs.REFRESHING_O, this)) {
			dialog = createProgressDialog(R.string.loading);
			dialog.show();
		} else if (RefreshServicePrefs.isStatus(
				RefreshServicePrefs.REFRESHING_D, this)
				|| RefreshServicePrefs.getLastRefreshTime(this) != 0) {
			MainActivity.launchStartupActivity(this);
		}
	}

	private BroadcastReceiver broadcastDatabaseCreationResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.d(TAG, "received : broadcastDatabaseCreationResult");

			int result;
			result = intent.getExtras().getInt(
					ServiceLoginRefresh.BROADCAST_DATEBASE_CREATION_RESULT);
			if (result == CreateDatabase.ERROR || Timetable.isError(result)) {
				if (dialog != null)
					dialog.dismiss();
				dialog = null;
				MyAlertDialog.checkDialog(LoginActivity.this);
			}

		}

	};

	private BroadcastReceiver broadcastTempAttendenceResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.d(TAG, "received : broadcastTempAttendenceResult");

			if (dialog != null)
				dialog.dismiss();
			dialog = null;
			int result;
			result = intent.getExtras().getInt(
					ServiceLoginRefresh.BROADCAST_TEMP_ATND_RESULT);
			if (result == TempAtndData.ERROR) {
				MyAlertDialog.checkDialog(LoginActivity.this);
			} else {
				MainActivity.launchStartupActivity(getActivity());
			}

		}
	};

	private Activity getActivity() {
		return this;
	}

	@Override
	protected void onPause() {
		// Log.d(TAG, "onpause");
		// /LocalBroadcastManager.getInstance(this).unregisterReceiver(
		// / broadcastTimetableloadResult);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastLoginResult);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastDatabaseCreationResult);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastTempAttendenceResult);

		if (dialog != null) {
			dialog.dismiss();
			dialog = null;

		}
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Log.d(TAG, "onresume");
		// / LocalBroadcastManager.getInstance(this).registerReceiver(
		// / broadcastTimetableloadResult,
		// / new IntentFilter(ServiceLoginRefresh.BROADCAST_TIMETABLE_LOAD));
		LocalBroadcastManager.getInstance(this).registerReceiver(
				broadcastLoginResult,
				new IntentFilter(ServiceLoginRefresh.BROADCAST_LOGIN_RESULT));
		LocalBroadcastManager
				.getInstance(this)
				.registerReceiver(
						broadcastDatabaseCreationResult,
						new IntentFilter(
								ServiceLoginRefresh.BROADCAST_DATEBASE_CREATION_RESULT));
		LocalBroadcastManager
				.getInstance(this)
				.registerReceiver(
						broadcastTempAttendenceResult,
						new IntentFilter(
								ServiceLoginRefresh.BROADCAST_TEMP_ATND_RESULT));

		// Log.d(TAG, "resuming dialog");
		manageProgressDialog();
		MyAlertDialog.checkDialog(this);
		startLogginIfRecreating();

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

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		View focus = getCurrentFocus();
		if (focus != null)
			inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// / Log.d(TAG, "onitemselected");
		Spinner spinner = (Spinner) parent;

		if (spinner.getId() == R.id.colg_select) {
			prefColg = getResources().getStringArray(R.array.prefs_colg_code)[pos];
		}

		// Log.d(TAG, "Sem" + prefSem);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// /Log.d(TAG, "onnothingselected");
		prefColg = getResources().getStringArray(R.array.prefs_colg_code)[0];
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
