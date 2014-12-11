package com.blackMonster.webkiosk;

import android.content.Context;
import android.content.SharedPreferences;

import com.sponsorpay.publisher.SponsorPayPublisher;
import com.sponsorpay.publisher.currency.SPCurrencyServerListener;

public class PremiumManager {

	public static final String SponsorpayAppID = "21167";
	public static final String SponsorpaySecurityToken = "bea6e583d85634777194145a77526aa8";
	
	
	public static final String PREFS_NAME = "Adprefs";
	public static final String START_DAY = "StartDay";
	public static final String END_DAY = "EndDay";
	public static final String SHOW_FIRST_TIME = "ShowfirstTime";

	public static double MILLISEC_IN_DAY = 86400000.0;

	private static SharedPreferences prefs = null;

	private static void initPrefInstance(Context context) {
		if (prefs == null)
			prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}

	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
	}

	public static int getDaysLeft(Context context) {
		return 100;
//		long curr = System.currentTimeMillis();
//
//		if (curr > getStartTime(context) && curr < getEndTime(context))
//			return (int) ((getEndTime(context) - curr) / MILLISEC_IN_DAY);
//		else
//			return 0;

	}

	public static boolean isPermiumUser(Context context) {
		return true;
//		M.log("premium ", " start " + getStartTime(context) / MILLISEC_IN_DAY
//				+ " end " + getEndTime(context) / MILLISEC_IN_DAY);
//		if (getDaysLeft(context) > 0)
//			return true;
//		else
//			return false;
	}

	public static void updateDays(SPCurrencyServerListener requestListener,
			Context context) {
		M.log("PremiumUser", "UpdateDays");
		try {
			SponsorPayPublisher.requestNewCoins(context.getApplicationContext(),
					requestListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// SponsorPayPublisher.displayNotificationForSuccessfullCoinRequest(false);

	}

	public static boolean startUpdate(double coins, Context context) {
		if (coins == 0)
			return false;

		long timeEarned = (long) (coins * PremiumManager.MILLISEC_IN_DAY);
		long curr = System.currentTimeMillis();
		
		if (curr> getStartTime(context)
				&& curr < getEndTime(context))
			setEndTime(getEndTime(context) + timeEarned, context);
		else {
			setStartTime(curr, context);
			setEndTime(curr + timeEarned, context);
		}
		return true;

	}

	public static long getStartTime(Context context) {
		return getSharedPreference(context).getLong(START_DAY, 0);
	}

	public static long getEndTime(Context context) {
		return getSharedPreference(context).getLong(END_DAY, 0);
	}

	private static void setStartTime(long startTime, Context context) {
		getSharedPreference(context).edit().putLong(START_DAY, startTime)
				.commit();
	}

	private static void setEndTime(long endTime, Context context) {
		getSharedPreference(context).edit().putLong(END_DAY, endTime).commit();
	}

	public static boolean showFirstTime(Context context) {
		return getSharedPreference(context).getBoolean(SHOW_FIRST_TIME, true);
	}

	public static void setFirstTimeDone(Context context) {
		getSharedPreference(context).edit().putBoolean(SHOW_FIRST_TIME, false)
				.commit();
	}
}
