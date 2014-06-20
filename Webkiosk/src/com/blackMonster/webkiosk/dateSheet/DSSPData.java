package com.blackMonster.webkiosk.dateSheet;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.DbHelper;
import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.dateSheet.DSSPFetch.DS_SP;

public class DSSPData {
	public static final String TABLE = "dateSSPlan";
	public static final String C_SHEET_CODE = "sheetCode";
	public static final String C_COURSE = "course";
	public static final String C_DATE = "date";
	public static final String C_TIME = "time";
	public static final String C_ROOM_NO = "roomNo";
	public static final String C_SEAT_NO = "seatNo";

	public static void createTable(Context context) {

		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
		createTable(db);
		db.close();
	}

	public static void createTable(SQLiteDatabase db) {

		String sql = String
				.format("create table %s"
						+ "(%s text , %s text, %s text , %s text, %s text , %s text, PRIMARY KEY (%s, %s) )",
						DSSPData.TABLE, DSSPData.C_SHEET_CODE,
						DSSPData.C_COURSE, DSSPData.C_DATE, DSSPData.C_TIME,
						DSSPData.C_ROOM_NO, DSSPData.C_SEAT_NO,
						DSSPData.C_DATE, DSSPData.C_TIME);

		M.log(TABLE, "onCreate with SQL : " + sql);

		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public static void insert(List<DS_SP> dsspList, Context context) {
		if (dsspList == null)
			return;
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

		for (DS_SP dssp : dsspList) {
			ContentValues values = new ContentValues();
			values.put(C_SHEET_CODE, dssp.sheetCode);
			values.put(C_COURSE, dssp.course);
			values.put(C_DATE, dssp.date);
			values.put(C_TIME, dssp.time);
			values.put(C_ROOM_NO, dssp.roomNo);
			values.put(C_SEAT_NO, dssp.seatNo);

			db.insert(TABLE, null, values);
		}
	}

	public static List<String> getSheetCodes(Context context) {
		M.log(TABLE, "getsheetcode");

		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		M.log(TABLE, "getsheetcode1");
		// Cursor cursor = db.rawQuery("select rowid _id,* from " + TABLE
		// + " ORDER BY " + "_id" + " DESC", null);

		Cursor cursor = db.rawQuery("select DISTINCT " + C_SHEET_CODE
				+ " from " + TABLE, null);
		List<String> sCodesList = new ArrayList<String>();
		if (cursor != null) {
			M.log(TABLE, "getsheetcode2");
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); ++i) {
				sCodesList.add(cursor.getString(cursor
						.getColumnIndex(C_SHEET_CODE)));
				cursor.moveToNext();

			}
			M.log(TABLE, "getsheetcode");
			cursor.close();
		}
		M.log(TABLE, "getsheetcode5");
		return sCodesList;

	}

	public static Cursor getDSfromSheetCode(String sheetCode, Context context) {
		M.log(TABLE, "getdsfromsheetcode");

		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		M.log(TABLE, "getdsfromsheetcode1");
		Cursor cursor = db.rawQuery("select rowid _id,* from " + TABLE
				+ " WHERE " + C_SHEET_CODE + " = \"" + sheetCode + "\"", null);
		M.log(TABLE, "getdsfromsheetcode2");
		// Cursor cursor = db.query(TABLE, null, C_SHEET_CODE + "='" + sheetCode
		// + "'", null, null, null, null);
		if (cursor != null) {
			M.log(TABLE, "getdsfromsheetcode3");
			cursor.moveToFirst();
			M.log(TABLE, "getdsfromsheetcode4");
		}
		M.log(TABLE, "getdsfromsheetcode5");
		return cursor;
	}

	public static List<DS_SP> getDS(Context context) {

		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
		Cursor cursor = db.rawQuery("select rowid _id,* from " + TABLE, null);
		List<DS_SP> dsspList = new ArrayList<DSSPFetch.DS_SP>();
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); ++i) {
				dsspList.add(new DS_SP(cursor.getString(cursor
						.getColumnIndex(C_SHEET_CODE)), cursor.getString(cursor
						.getColumnIndex(C_COURSE)), cursor.getString(cursor
						.getColumnIndex(C_DATE)), cursor.getString(cursor
						.getColumnIndex(C_TIME)), cursor.getString(cursor
						.getColumnIndex(C_ROOM_NO)), cursor.getString(cursor
						.getColumnIndex(C_SEAT_NO))));

				cursor.moveToNext();

			}

		}
		return dsspList;
	}

	public static void clearTable(Context context) {
		DbHelper.getInstance(context).getWritableDatabase()
				.execSQL("delete from " + TABLE);
	}

}
