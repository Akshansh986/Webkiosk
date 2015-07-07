package com.blackMonster.webkiosk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDataHelper;

import java.util.ArrayList;
import java.util.List;

public class WebkioskApp extends Application {
	public SiteConnection connect = null;

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

	/**
	 * Resets everything in app. Making app ready for fresh login.
	 */
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
