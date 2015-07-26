package com.blackMonster.webkiosk.databases.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;
import com.blackMonster.webkiosk.databases.DbHelper;

import java.util.List;


/**
 * Table containing attendance detail of a particular subject.
 * i.e 5 tables of this type will be created if sem has 5 subjects.
 */
public class DetailedAttendenceTable {
    public static final String C_DATE = "date";
    public static final String C_ATTENDENCE_BY = "attendenceBy";
    public static final String C_STATUS = "status";     //Attendance i.e present or absent.
    public static final String C_CLASS_TYPE = "classType";
    public static final String C_LTP = "LTP";          // type of class. i.e "LECTURE","TUTORIAL" OR "PRACTICAL"

    String TABLE;  //Name of table. It is dynamic because separate table for each subject is created.
    int isNotLab; //TODO convert it to boolean and explain its concept here.
    SQLiteDatabase db;
    Context context;

    public DetailedAttendenceTable(String tableName, int isNotLab, Context context) {
        TABLE = tableName;
        this.isNotLab = isNotLab;
        this.context = context;
    }

    public void createTable() {
        SQLiteDatabase db = DbHelper.getInstance(context)
                .getWritableDatabase();
        String sql;
        sql = String
                .format("create table %s"
                                + "(%s text, %s text, %s INTEGER, %s text, %s text, PRIMARY KEY (%s, %s, %s, %s) )",
                        TABLE, C_DATE, C_ATTENDENCE_BY, C_STATUS,
                        C_CLASS_TYPE, C_LTP, C_DATE, C_ATTENDENCE_BY,
                        C_CLASS_TYPE, C_LTP);
        db.execSQL(sql);
    }

    public void insert(List<DetailedAttendance> detailedAttendanceList) {

        db = DbHelper.getInstance(context).getWritableDatabase();
        db.beginTransaction();


        try {
            for (DetailedAttendance detailedAttendance : detailedAttendanceList) {
                ContentValues values = new ContentValues();

                values.put(C_DATE, detailedAttendance.date);
                values.put(C_ATTENDENCE_BY, detailedAttendance.AttendenceBY);
                values.put(C_STATUS, detailedAttendance.status);
                values.put(C_CLASS_TYPE, detailedAttendance.ClassType);
                if (isNotLab == 1)
                    values.put(C_LTP, detailedAttendance.LTP);

                db.insert(TABLE, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public Cursor getData() {
        db = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("select rowid _id,* from " + TABLE
                + " ORDER BY " + "_id" + " DESC", null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public void deleteAllRows() {
        db = DbHelper.getInstance(context).getWritableDatabase();
        db.delete(TABLE, null, null);
    }

}
