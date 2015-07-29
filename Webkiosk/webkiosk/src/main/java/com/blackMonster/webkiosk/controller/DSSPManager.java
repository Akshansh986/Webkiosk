package com.blackMonster.webkiosk.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.blackMonster.webkiosk.ui.MainActivity;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.dateSheet.DS_SP;
import com.blackMonster.webkiosk.databases.Tables.DSSPTable;
import com.blackMonster.webkiosk.ui.ActivityDateSheet;
import com.blackMonster.webkioskApp.R;

import java.util.List;

public class DSSPManager {

	public static void updateDataDontNotify(CrawlerDelegate crawlerDelegate,
			Context context) {

		List<DS_SP> dssp = null;
		try {
			dssp = crawlerDelegate.getDateSheetSeatingPlan();
			DSSPTable.clearTable(context);
			DSSPTable.insert(dssp, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateDataAndNotify(CrawlerDelegate crawlerDelegate,
			Context context) {
		List<String> oldScCodes = DSSPTable.getSheetCodes(context);
		updateDataDontNotify(crawlerDelegate, context);
		List<String> newScCodes = DSSPTable.getSheetCodes(context);
		boolean res = isDSUpdated(oldScCodes, newScCodes);
		if (res)
			notifyUser(context);
	}

	private static void notifyUser(Context context) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Datesheet arrived")
				.setContentText("Touch to view datesheet");
		mBuilder.setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, ActivityDateSheet.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(94, mBuilder.build());
	}

	private static boolean isDSUpdated(List<String> oldScCodes,
			List<String> newScCodes) {
		for (String newCode : newScCodes) {
			if (!oldScCodes.contains(newCode))
				return true;
		}
		return false;
	}

}
