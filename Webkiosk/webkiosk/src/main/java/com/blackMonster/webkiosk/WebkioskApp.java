package com.blackMonster.webkiosk;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

import java.util.ArrayList;
import java.util.List;

public class WebkioskApp extends Application {


	@Override
	public void onCreate() {
		super.onCreate();

	}


	/**
	 * Resets everything in app. Making app ready for fresh login.
	 */
	public void nullifyAllVariables() {
		TimetableDbHelper.nullifyInstance();
		MainPrefs.close();
		DbHelper.shutDown();
	}

	public static WebkioskApp getWaPP(Context context) {
		WebkioskApp a = null;
		if (context instanceof Activity)
			a = ((WebkioskApp) ((Activity) context).getApplication());
		else if (context instanceof Service)
			a = ((WebkioskApp) ((Service) context).getApplication());
		else
			a = ((WebkioskApp) context.getApplicationContext());
		return a;
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
