package com.blackMonster.webkiosk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sponsorpay.SponsorPay;
import com.sponsorpay.publisher.SponsorPayPublisher;
import com.sponsorpay.publisher.currency.SPCurrencyServerErrorResponse;
import com.sponsorpay.publisher.currency.SPCurrencyServerListener;
import com.sponsorpay.publisher.currency.SPCurrencyServerSuccesfulResponse;


public class StartupActivity extends BaseActivity{
	public static final String SponsorpayAppID = "21167";
	public static final String SponsorpaySecurityToken = "bea6e583d85634777194145a77526aa8";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (isStartupActivity(getClass(), this)) start();
		super.onCreate(savedInstanceState);
	}
	
	private void start() {
		handleLogout(getIntent(), this);
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
	
	
	SPCurrencyServerListener requestListener = new SPCurrencyServerListener() {

		@Override
		public void onSPCurrencyServerError(
				SPCurrencyServerErrorResponse response) {
			Log.d("SPCurrencyServerListener", "Request or Response Error: "
					+ response.getErrorType());
		}

		@Override
		public void onSPCurrencyDeltaReceived(
				SPCurrencyServerSuccesfulResponse response) {
			double coins = response.getDeltaOfCoins();
			PremiumManager.startUpdate(coins, getApplicationContext());
			Log.d("SPCurrencyServerListener",
					"Response From Currency Server. Delta of Coins: "
							+ String.valueOf(response.getDeltaOfCoins())
							+ ", Latest Transaction Id: "
							+ response.getLatestTransactionId());
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		SponsorPayPublisher.displayNotificationForSuccessfullCoinRequest(false);
		PremiumManager.updateDays(requestListener,this);
		
	}

}
