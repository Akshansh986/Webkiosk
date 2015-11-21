package com.blackMonster.webkiosk.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.blackMonster.webkiosk.WebkioskApp;
import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.services.AutoRefreshAlarmService;
import com.blackMonster.webkiosk.utils.NetworkUtils;

/**
 * Receives boot complete and network connectivity change.
 */
public class SystemBroadcastReceiver extends BroadcastReceiver {
	static final String TAG = "receiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		M.log(TAG, "Received");
		if (WebkioskApp.isAppLogined(context)) {
			
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				Intent intention = new Intent(context,AutoRefreshAlarmService.class);
				intention.putExtra(AutoRefreshAlarmService.CALLER_TYPE, AutoRefreshAlarmService.BOOT_COMPLETE);
				context.startService(intention);
			}
			else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (NetworkUtils.isDataPackAvailable(context) || NetworkUtils.isWifiAvailable(context)) {
					Intent intention = new Intent(context,AutoRefreshAlarmService.class);
					intention.putExtra(AutoRefreshAlarmService.CALLER_TYPE, AutoRefreshAlarmService.CONNECTIVITY_CHANGE);
					context.startService(intention);
				}
			}
		}
	}
	


}
