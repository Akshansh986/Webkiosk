package com.example.akshansh.mytestapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by akshansh on 11/07/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    static final String TAG = "DbHelper";
    public static final String DB_NAME = "attendence.db";
    public static final int DB_VERSION = 3;

    private  Context context = null;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d(TAG, " " + doesTableExist(db));


//            new AttendenceOverviewTable(context).createTable(db);
        db.execSQL("create table attendanceOverview (subCode text)");



//        Log.d(TAG, " " + doesTableExist(db));


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
removeSubjectLinkTable(db);
    }



    public boolean doesTableExist(SQLiteDatabase db) {
        Cursor cur = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='?'", new String[]{"attendanceOverivew"});
        return cur.getCount() != 0;
    }


    private void removeSubjectLinkTable(SQLiteDatabase db) {

        String atndOverivewTable = new AttendenceOverviewTable(context).getTableName();

        db.execSQL(String.format("alter table %s add column %s integer",atndOverivewTable, AttendenceOverviewTable.C_NOT_LAB));

        Cursor cursor = db.rawQuery("select * from subjectLink",null);

        boolean existTempAtndData = false;
        String tempAtndOverviewTable = new TempAtndOverviewTable(context).getTableName();

        if (cursor.moveToFirst()) {
            do {
                String subCode = cursor.getString(cursor.getColumnIndex("code"));
                String notLab = cursor.getInt(cursor.getColumnIndex("LTP")) + "";

                db.execSQL(String.format("update %s set %s  = %s where code = '%s'",
                        atndOverivewTable, AttendenceOverviewTable.C_NOT_LAB, notLab, subCode));

                if (existTempAtndData)
                    db.execSQL(String.format("update %s set %s  = %s where code = '%s'",
                            tempAtndOverviewTable, TempAtndOverviewTable.C_NOT_LAB, notLab, subCode));


            } while (cursor.moveToNext());
        }
        cursor.close();

        db.execSQL("DROP TABLE IF EXISTS subjectLink");
    }
}
