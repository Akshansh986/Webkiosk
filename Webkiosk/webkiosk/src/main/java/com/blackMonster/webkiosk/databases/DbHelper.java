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
			dInstance = new DbHelper(getApplicationContext());
			dInstance.getWritableDatabase();
		}
		return dInstance;
	}

	private DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		AttendenceData.getInstance(context).new SubjectLinkTable()
				.createTable(db);
		AttendenceData.getInstance(context).new AttendenceOverviewTable()
				.createTable(db);
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