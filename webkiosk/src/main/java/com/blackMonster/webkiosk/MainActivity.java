package com.blackMonster.webkiosk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blackMonster.webkiosk.dateSheet.ActivityPremium;
import com.blackMonster.webkioskApp.R;
import com.crittercism.app.Crittercism;
import com.sponsorpay.SponsorPay;

//-103
public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	SharedPreferences settings;
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RefreshServicePrefs.resetIfrunningFromLongTime(this);

		///M.log(TAG, "onCreate");
		
		try {
			Crittercism.initialize(getApplicationContext(), "53eb5a1683fb796b50000004");
			SponsorPay.start(PremiumManager.SponsorpayAppID, null, PremiumManager.SponsorpaySecurityToken, this);
	    } catch (RuntimeException e){
	        M.log(TAG, e.getLocalizedMessage());
	    }  

		settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);

		if (settings.getBoolean(CreateDatabase.HAS_DATABASE_CREATED, false)) {
			///M.log(TAG, "loggedIN");
			upgradeAndStartActivity(settings);
		} else {
			///M.log(TAG, "notLoggedin");
			settings.edit().putBoolean("hasLoggedIn", false).commit();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}

	}

	private void upgradeAndStartActivity(SharedPreferences settings) {
		///M.log(TAG, "sem : " + MainPrefs.getSem(settings));

		if (MainPrefs.getSem(settings) != MainPrefs.DEFAULT_SEM) {
			setContentView(R.layout.main_activity);
			if (SiteConnection.isInternetAvailable(this)) {
				new Upgradation().execute(this);
			} else
				showInternetNADialog();
			//return true;
		} else
			launchStartupActivity(this);

	}

	class Upgradation extends AsyncTask<Context, Void, Void> {
		Context context;
		AlertDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = createProgressDialog("Upgrading...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Context... params) {
			context = params[0];
			context.deleteDatabase(getDbNameThroughPrefs(context));
			Timetable.handleChangesRefresh(context);
			MainPrefs.setDefaultSem(context);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			launchStartupActivity((Activity) context);
		}
		
		public  String getDbName(String enroll, String batch, int sem) {
			return batch + enroll + sem + ".db";
			
		}

		public  String getDbNameThroughPrefs(Context context) {
				
				return getDbName(
						MainPrefs.getEnroll(context),
						MainPrefs.getBatch(context),
						MainPrefs.getSem(context));

			}

	}

	private AlertDialog createProgressDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View myView = getLayoutInflater().inflate(R.layout.login_progressbar,
				null);
		((TextView) myView.findViewById(R.id.login_dialog_msg)).setText(msg);

		builder.setView(myView);
		builder.setCancelable(false);

		return builder.create();
	}

	private void showInternetNADialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});

		builder.setMessage(R.string.internet_na_upgradation);
		builder.create().show();
	}

	public static void launchStartupActivity(Activity activity) {
		if (PremiumManager.showFirstTime(activity.getApplicationContext())) {
			activity.startActivity(new Intent(activity, ActivityPremium.class));
			PremiumManager.setFirstTimeDone(activity.getApplicationContext());
		}
		else
		{
		StartupActivity.setStartupActivity(activity);
		activity.startActivity(new Intent(activity, StartupActivity
				.getStartupActivity(activity)));
		}
		activity.finish();
	}

}
