package com.blackMonster.webkiosk.databases.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.crawler.dateSheet.DS_SP;
import com.blackMonster.webkiosk.databases.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DateSheet and SeatingPlan table.
 */
public class DSSPTable {
    public static final String TABLE = "dateSSPlan";
    public static final String C_SHEET_CODE = "sheetCode";
    public static final String C_COURSE = "course";
    public static final String C_DATE = "date";
    public static final String C_TIME = "time";
    public static final String C_ROOM_NO = "roomNo";
    public static final String C_SEAT_NO = "seatNo";

    public static void createTable(SQLiteDatabase db) {

        String sql = String
                .format("create table %s"
                                + "(%s text , %s text, %s text , %s text, %s text , %s text, PRIMARY KEY (%s, %s) )",
                        DSSPTable.TABLE, DSSPTable.C_SHEET_CODE,
                        DSSPTable.C_COURSE, DSSPTable.C_DATE, DSSPTable.C_TIME,
                        DSSPTable.C_ROOM_NO, DSSPTable.C_SEAT_NO,
                        DSSPTable.C_DATE, DSSPTable.C_TIME);
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

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery("select DISTINCT " + C_SHEET_CODE
                + " from " + TABLE, null);
        List<String> sCodesList = new ArrayList<String>();
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); ++i) {
                sCodesList.add(cursor.getString(cursor
                        .getColumnIndex(C_SHEET_CODE)));
                cursor.moveToNext();

            }
            cursor.close();
        }
        return sCodesList;

    }

    /**
     * Get datesheet with seating plan of upcoming exam.
     * @param context
     * @return
     */
    public static List<DS_SP> getDS(Context context) {

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("select rowid _id,* from " + TABLE, null);  // Import if we want to use cursorAdapter.
        List<DS_SP> dsspList = new ArrayList<DS_SP>();
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
