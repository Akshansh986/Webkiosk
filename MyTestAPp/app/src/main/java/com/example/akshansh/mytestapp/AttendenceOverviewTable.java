package com.example.akshansh.mytestapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    public static final int SUBJECT_CHANGED = -101;
    public static final int ERROR = -102;
    public static final int DONE = -103;

    Context context;

    public AttendenceOverviewTable(Context context) {
        this.context = context;
    }

    public String getTableName() {
        return "attendanceOverview";
    }

    public void createTable(SQLiteDatabase db) {
        String sql = String
                .format("create table %s"
                                + "(%s text primary key, %s text, %s real, %s real, %s real, %s real, %s int, %s int)",
                        getTableName(), C_CODE, C_NAME, C_OVERALL,
                        C_LECTURE, C_TUTORIAL, C_PRACTICAL, C_IS_MODIFIED, C_NOT_LAB);
        db.execSQL(sql);

    }

   
}
