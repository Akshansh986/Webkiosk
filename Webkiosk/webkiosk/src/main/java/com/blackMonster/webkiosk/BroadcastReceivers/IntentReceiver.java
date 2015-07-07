package com.blackMonster.webkiosk.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.blackMonster.webkiosk.CreateDatabase;
import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.refresher.AlarmService;

public class IntentReceiver extends BroadcastReceiver {
	static final String TAG = "receiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		M.log(TAG, "Received");
		if (context.getSharedPreferences(MainActivity.PREFS_NAME, 0).getBoolean(CreateDatabase.HAS_DATABASE_CREATED, false)) {
			
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				//M.log(TAG, "Boot completed");
				Intent intention = new Intent(context,AlarmService.class);
				intention.putExtra(AlarmService.CALLER_TYPE, AlarmService.BOOT_COMPLETE);
				context.startService(intention);
			}
			else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				//M.log(TAG, "network confing changed");
				if (isDataPackAvailable(context) || isWifiAvailable(context)) {
					//M.log(TAG, "connected or connecting");
					Intent intention = new Intent(context,AlarmService.class);
					intention.putExtra(AlarmService.CALLER_TYPE, AlarmService.CONNECTIVITY_CHANGE);
					context.startService(intention);
				}
				
			}
			
		}
		
	}
	
	private boolean isDataPackAvailable(Context context) {
		NetworkInfo networkInfo = ( (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) ).getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnectedOrConnecting() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	private boolean isWifiAvailable(Context context) {
		NetworkInfo networkInfo = ( (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) ).getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnectedOrConnecting() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
		}

}
