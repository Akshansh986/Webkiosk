package com.blackMonster.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationManager {
	public static final int NOTIFICATION_ADDED = 0;
	public static final int NOTIFICAITON_REMOVED = 1;
	public static final int NOTIFICATION_CHANGED = 2;
	public static final int NOTIFICATION_NO_CHANGE = 3;

	public static final String BROADCAST_NOTIFICATION_UPDATE_RESULT = "notificationUpdate";
	public static final String TAG = "NotificationManager";
	
	private static boolean isRunning=false;

	public static void manageNotificaiton(Context context) {
		if (isRunning) return;
		isRunning = true;
		
		try {
			Notificaton newNf = Server.getNotification(context);
			Log.d(TAG, newNf.link + " " + newNf.title);
			Notificaton oldNf = LocalData.getNotification(context);
			int result = compare(oldNf, newNf);
			updateLocalData(result,newNf,context);
			broadcastResult(result, context);   //at time of app login receiver in not registered.

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		isRunning = false;
	}

	private static void updateLocalData(int result, Notificaton newNf,
			Context context) {
		LocalData.setNotification(newNf, context);
		updateNotificationAlert(result,context);		
	}

	private static void updateNotificationAlert(int result, Context context) {
		if (result == NOTIFICATION_ADDED) {
			LocalData.setNotificationAlertNotDone(context);
		} else if (result == NotificationManager.NOTIFICAITON_REMOVED) {
			LocalData.setNotificationAlertDone(context);
		} else if (result == NotificationManager.NOTIFICATION_CHANGED) {
			LocalData.setNotificationAlertNotDone(context);

		}

	}

	public static boolean isNotificationAvailable(Context context) {
		return !LocalData.getNotification(context).isEmpty();
	}

	private static void broadcastResult(int result, Context context) {
		Intent intent = new Intent(BROADCAST_NOTIFICATION_UPDATE_RESULT)
				.putExtra(BROADCAST_NOTIFICATION_UPDATE_RESULT, result);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

	}

	private static int compare(Notificaton oldNf, Notificaton newNf) {
		if (oldNf.isEmpty() && !newNf.isEmpty())
			return NOTIFICATION_ADDED;
		if (!oldNf.isEmpty() && newNf.isEmpty())
			return NOTIFICAITON_REMOVED;
		if (oldNf.equals(newNf))
			return NOTIFICATION_NO_CHANGE;
		if (!oldNf.isEmpty() && !newNf.isEmpty() && !oldNf.equals(newNf))
			return NOTIFICATION_CHANGED;
		return -1;
	}

}
