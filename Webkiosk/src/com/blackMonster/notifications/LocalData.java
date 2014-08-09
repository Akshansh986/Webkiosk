package com.blackMonster.notifications;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalData {
	private static final String PREFS_NAME = "nofification";
	private static final String TITLE = "title";
	private static final String Link = "link";
	private static final String SHOW_ALERT = "showAlert";

	
	
	private static SharedPreferences prefs=null;
	

	private static void initPrefInstance(Context context) {
		if (prefs == null) prefs = context.getSharedPreferences(PREFS_NAME, 0);
	}
	public static SharedPreferences getSharedPreference(Context context) {
		initPrefInstance(context);
		return prefs;
	}
	
	
	public static Notificaton getNotification(Context context) {
		initPrefInstance(context);
		Notificaton notificaton = new Notificaton();
		notificaton.title = 	prefs.getString(TITLE, "NA");
		notificaton.link = prefs.getString(Link, "NA");
		return notificaton;
	}
	
	public static void setNotification(Notificaton notificaton, Context context) {
		initPrefInstance(context);
		prefs.edit().putString(TITLE, notificaton.title).commit();
		prefs.edit().putString(Link, notificaton.link).commit();
	}
	
	public static boolean isShowNotificationAlert(Context context) {
		initPrefInstance(context);
		return prefs.getBoolean(SHOW_ALERT, false);
	
	}
	public static void setNotificationAlertDone(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(SHOW_ALERT, false).commit();
	}
	
	public static void setNotificationAlertNotDone(Context context) {
		initPrefInstance(context);
		prefs.edit().putBoolean(SHOW_ALERT, true).commit();
	}
	
	
	
	
	
}
