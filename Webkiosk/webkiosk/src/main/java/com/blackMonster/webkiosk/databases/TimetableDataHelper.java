package com.blackMonster.webkiosk.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blackMonster.webkiosk.MainPrefs;

public class TimetableDataHelper extends SQLiteOpenHelper {
	public static final String TAG = "TimetableDataHelper";
	public static final int DB_VERSION = 1;
	public static final String C_DAY = "day";

	private static TimetableDataHelper dInstance = null;

	private TimetableDataHelper(Context context, String dbName) {
		super(context, dbName, null, DB_VERSION);
	///	Log.d(TAG, dbName);
	}

	public static TimetableDataHelper getInstanceAndCreateTable(String colg,
			String enroll, String onlineFileName, String batch, Context cont) {
		if (dInstance == null) {
			dInstance = new TimetableDataHelper(cont.getApplicationContext(),
					getDbName(colg, enroll, batch, onlineFileName));
			// Log.d(TAG, "getWritebledataase");
			dInstance.getWritableDatabase();
		}
		return dInstance;
	}

	private static void initDinstance(Context cont) {
		if (dInstance == null) {
			if (databaseExists(cont)) {
				dInstance = new TimetableDataHelper(
						cont.getApplicationContext(), getDbName(
								MainPrefs.getColg(cont),
								MainPrefs.getEnroll(cont),
								MainPrefs.getBatch(cont),
								MainPrefs.getOnlineTimetableFileName(cont)));
			}
		}
	}

	public static SQLiteDatabase getReadableDatabaseifExist(Context context) {
		initDinstance(context);
		if (dInstance != null)
			return dInstance.getReadableDatabase();
		else
			return null;
	}

	public static SQLiteDatabase getWritableDatabaseifExist(Context context) {
		initDinstance(context);
		if (dInstance != null)
			return dInstance.getWritableDatabase();
		else
			return null;
	}

	public static String getDbName(String colg, String enroll, String batch,
			String onlineFileName) {
		// Log.d(TAG, colg + batch + enroll + sem + ".db");
		return colg + batch + enroll + onlineFileName + ".db";

	}

	public static String getDbNameThroughPrefs(Context context) {

		return getDbName(MainPrefs.getColg(context),
				MainPrefs.getEnroll(context), MainPrefs.getBatch(context),
				MainPrefs.getOnlineTimetableFileName(context));

	}

	public static boolean databaseExists(String colg, String enroll,
			String batch, String onlineFileName, Context context) {
		return context.getDatabasePath(getDbName(colg, enroll, batch, onlineFileName))
				.exists();
	}

	public static boolean databaseExists(Context context) {
		return context.getDatabasePath(getDbNameThroughPrefs(context)).exists();
	}

	public static void clearTimetable(Context context) {
		if (databaseExists(context))
			getWritableDatabaseifExist(context).execSQL(
					"DROP TABLE IF EXISTS " + MainPrefs.getBatch(context));

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	

	public static void close(Context context) {
		getInstanceAndCreateTable(MainPrefs.getColg(context),
				MainPrefs.getEnroll(context), MainPrefs.getOnlineTimetableFileName(context),
				MainPrefs.getBatch(context), context).close();
		dInstance = null;
	}

	public static void nullifyInstance() {
		if (dInstance != null)
			dInstance.close();
		dInstance = null;
	}

}