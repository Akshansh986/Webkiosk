package com.blackMonster.webkiosk.databases.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshansh on 19/04/15.
 */
public class AttendenceOverviewTable {
    public static final String C_CODE = "code";
    public static final String C_NAME = "name";
    public static final String C_OVERALL = "overall";
    public static final String C_LECTURE = "lecture";
    public static final String C_TUTORIAL = "tutorial";
    public static final String C_PRACTICAL = "practical";
    public static final String C_IS_MODIFIED = "isModified";
    public static final String C_NOT_LAB = "notLab";

    SQLiteDatabase db;


    Context context;

    public AttendenceOverviewTable(Context context) {
        this.context = context;
    }

    public String getTableName() {
        return "attendanceOverview";  //See TempAtndOverivewTable.
    }

    public void createTable(SQLiteDatabase db) {
        String sql = String
                .format("create table %s"
                                + "(%s text primary key, %s text, %s real, %s real, %s real, %s real, %s int, %s int)",
                        getTableName(), C_CODE, C_NAME, C_OVERALL,
                        C_LECTURE, C_TUTORIAL, C_PRACTICAL, C_IS_MODIFIED, C_NOT_LAB);
        db.execSQL(sql);

    }

    public void insert(MySubjectAttendance subAtnd) {
        db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_CODE, subAtnd.getSubjectCode());
        values.put(C_NAME, subAtnd.getName());

        values.put(C_OVERALL, subAtnd.getOverall());
        values.put(C_LECTURE, subAtnd.getLect());
        values.put(C_TUTORIAL, subAtnd.getTute());
        values.put(C_PRACTICAL, subAtnd.getPract());
        values.put(C_IS_MODIFIED, subAtnd.isModified());
        values.put(C_NOT_LAB, subAtnd.isNotLab());

        db.insertWithOnConflict(getTableName(), null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void update(MySubjectAttendance subAtnd) {
        db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(C_OVERALL, subAtnd.getOverall());
        values.put(C_LECTURE, subAtnd.getLect());
        values.put(C_TUTORIAL, subAtnd.getTute());
        values.put(C_PRACTICAL, subAtnd.getPract());
        values.put(C_IS_MODIFIED, subAtnd.isModified());
        values.put(C_NOT_LAB, subAtnd.isNotLab());


        db.update(getTableName(), values, C_CODE + "='" + subAtnd.getSubjectCode()
                + "'", null);
    }


    public Cursor getData() {
        db = DbHelper.getInstance(context).getReadableDatabase();
        if (!doesTableExist(db)) return null;

        Cursor cursor = db.rawQuery("select rowid _id,* from " + getTableName()
                + " ORDER BY " + C_OVERALL + " DESC", null);

        return cursor;
    }

    public List<MySubjectAttendance> getAllSubjectAttendance() {

        Cursor cursor = getData();
        if (cursor == null) return null;

        List<MySubjectAttendance> list = new ArrayList<MySubjectAttendance>();

        cursor.moveToFirst();
        while (true) {
            MySubjectAttendance subAtnd = new MySubjectAttendance(cursor.getString(cursor
                    .getColumnIndex(C_NAME)),cursor.getString(cursor
                    .getColumnIndex(C_CODE)),cursor.getInt(cursor
                    .getColumnIndex(C_OVERALL)),cursor.getInt(cursor
                    .getColumnIndex(C_LECTURE)),cursor.getInt(cursor
                    .getColumnIndex(C_TUTORIAL)),cursor.getInt(cursor
                    .getColumnIndex(C_PRACTICAL)),cursor.getInt(cursor.
                    getColumnIndex(C_NOT_LAB)),cursor.getInt(cursor
                    .getColumnIndex(C_IS_MODIFIED))
                    );

            list.add(subAtnd);
            if (!cursor.moveToNext()) break;
        }

        cursor.close();
        return list;
    }


    public MySubjectAttendance getSubjectAttendance(String code) {
        SQLiteDatabase db = DbHelper.getInstance(context)
                .getReadableDatabase();
        if (!doesTableExist(db)) return null;
        // "Like" statement is used because it may be possible that full subject code is stored in table and code provided to function is half subject code.
        String query = "Select * from " + getTableName() + " where " + C_CODE + " like '%" + code + "%'";

        Cursor cursor = db.rawQuery(query,null);
        if (cursor == null) return null;

        try {
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();

            MySubjectAttendance subAtnd = new MySubjectAttendance(cursor.getString(cursor
                    .getColumnIndex(C_NAME)),cursor.getString(cursor
                    .getColumnIndex(C_CODE)),cursor.getInt(cursor
                    .getColumnIndex(C_OVERALL)),cursor.getInt(cursor
                    .getColumnIndex(C_LECTURE)),cursor.getInt(cursor
                    .getColumnIndex(C_TUTORIAL)),cursor.getInt(cursor
                    .getColumnIndex(C_PRACTICAL)),cursor.getInt(cursor.
                    getColumnIndex(C_NOT_LAB)),cursor.getInt(cursor
                    .getColumnIndex(C_IS_MODIFIED))
            );
            return subAtnd;

        } finally {
            cursor.close();
        }
    }

    public int isNotLab(String subCode) {
        db = DbHelper.getInstance(context).getReadableDatabase();
        if (!doesTableExist(db)) return -1;

        int result;
        String[] columns;
        columns = new String[1];
        columns[0] = C_NOT_LAB;


        Cursor cursor = db.query(getTableName(), columns, C_CODE + "='" + subCode
                + "'", null, null, null, null);

        if (cursor == null) return -1;

        try {
            if (cursor.getCount() == 0) return -1;

            cursor.moveToFirst();
            result = cursor.getInt(cursor
                    .getColumnIndex(C_NOT_LAB));
            return result;
        } finally {
            cursor.close();
        }
    }

    public boolean doesTableExist(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + getTableName() + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean isTableEmpty() {
        if (!doesTableExist(DbHelper.getInstance(context).getReadableDatabase())) return true;

        Cursor cursor = getData();
        if (cursor != null) return cursor.getCount() == 0;

        return true;
    }


}
