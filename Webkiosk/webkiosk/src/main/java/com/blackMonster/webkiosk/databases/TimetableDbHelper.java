package com.blackMonster.webkiosk.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;

/**
 * Singleton database helper for Timetable.
 */
public class TimetableDbHelper extends SQLiteOpenHelper {
	public static final String TAG = "TimetableDataHelper";
	public static final int DB_VERSION = 1;

	private static TimetableDbHelper dInstance = null;

	private TimetableDbHelper(Context context, String dbName) {
		super(context, dbName, null, DB_VERSION);
	}

	public static TimetableDbHelper getInstanceAndCreateTable(String colg,
			String enroll, String onlineFileName, String batch, Context cont) {
		if (dInstance == null) {
			dInstance = new TimetableDbHelper(cont.getApplicationContext(),
					getDbName(colg, enroll, batch, onlineFileName));
			dInstance.getWritableDatabase();
		}
		return dInstance;
	}

	private static void initDinstance(Context cont) {
		if (dInstance == null) {
			if (databaseExists(cont)) {		//checks if timetable is available or not.
				dInstance = new TimetableDbHelper(
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

	/**
	 * Name of timetable database.
	 * @param colg
	 * @param enroll
	 * @param batch
	 * @param onlineFileName
	 * @return
	 */
	public static String getDbName(String colg, String enroll, String batch,
			String onlineFileName) {
		return colg + batch + enroll + onlineFileName + ".db";

	}

	/**
	 * Name of timetable database, details fetched from shared prefs.
	 * @param context
	 * @return
	 */
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
					"DROP TABLE IF EXISTS " + TimetableTable.getTableName(context));

	}

	public static void shutdown() {
		if (dInstance != null)
			dInstance.close();
		dInstance = null;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}