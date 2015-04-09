package com.blackMonster.webkiosk.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blackMonster.webkiosk.dateSheet.DSSPData;

public class DbHelper extends SQLiteOpenHelper {

	static final String TAG = "DbHelper";
	public static final String DB_NAME = "attendence.db";
	public static final int DB_VERSION = 2;

	private static DbHelper dInstance = null;
	private static Context context = null;

	public static DbHelper getInstance(Context cont) {
		if (dInstance == null) {
			dInstance = new DbHelper(cont.getApplicationContext());
			// Log.d(TAG, "getWritebledataase");
			dInstance.getWritableDatabase();
		}
		return dInstance;
	}

	private DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// Log.d(TAG, "constructor start");
		this.context = context;
		// Log.d(TAG, "DbHelper");
		// Log.d(TAG, "constructor end");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Log.d(TAG, "oncreate start");
		// Log.d(TAG, "createing tables sqlitOpenHelper");
		AttendenceData.getInstance(context).new SubjectLinkTable()
				.createTable(db);
		// Log.d(TAG, "subjectLink created");
		AttendenceData.getInstance(context).new AttendenceOverviewTable()
				.createTable(db);
		// Log.d(TAG, "ATndoverview created");

		// Log.d(TAG, "oncreate end");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion<2)
		DSSPData.createTable(db);

	}

	@Override
	public synchronized void close() {
		super.close();
		dInstance = null;
	}

	public static void shutDown() {
		if (dInstance != null)
			dInstance.close();
		dInstance = null;
	}

}