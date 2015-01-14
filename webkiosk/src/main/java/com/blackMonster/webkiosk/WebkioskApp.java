package com.blackMonster.webkiosk;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class WebkioskApp extends Application {
	SiteConnection connect = null;

	@Override
	public void onCreate() {
		super.onCreate();

	}

	public void resetSiteConnection() {
		if (connect != null) {
			connect.close();
			connect = null;
		}
	}

	public void nullifyAllVariables() {
		TimetableDataHelper.nullifyInstance();
		MainPrefs.close();
		DbHelper.shutDown();
		//resetSiteConnection();
	}

	public class BroadcastStore {
		public static final String IS_FROM_STORE = "isfromstore";
		List<Intent> bList = new ArrayList<Intent>();

		public void store(Intent intent) {
			bList.add(intent);
		}

		public void remove(Intent intent) {

			if (bList.remove(intent)){}
				//Log.d("webkioskApp", "broadcast removed");
		}

		public void broadcastAll(Context context) {
			for (Intent intent : bList) {
				intent.putExtra(BroadcastStore.IS_FROM_STORE, true);
				LocalBroadcastManager.getInstance(context)
						.sendBroadcast(intent);

			}
		}

	}

	public class SingleBroadcast {
		Intent intent;
		String type;

		public SingleBroadcast(Intent i, String t) {
			intent = i;
			type = t;
		}
	}

}
