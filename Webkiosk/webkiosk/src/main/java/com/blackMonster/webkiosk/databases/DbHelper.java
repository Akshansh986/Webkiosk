package com.blackMonster.webkiosk.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.DSSPData;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;

public class DbHelper extends SQLiteOpenHelper {

    static final String TAG = "DbHelper";
    public static final String DB_NAME = "attendence.db";
    public static final int DB_VERSION = 3;

    private static DbHelper dInstance = null;
    private static Context context = null;

    public static DbHelper getInstance(Context cont) {
        if (dInstance == null) {
            dInstance = new DbHelper(cont.getApplicationContext());
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
        new AttendenceOverviewTable(context).createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {

            case 1:
                DSSPData.createTable(db);

            case 2:
                removeSubjectLinkTable(db);
        }
    }

    private void removeSubjectLinkTable(SQLiteDatabase db) {

        String atndOverivewTable = new AttendenceOverviewTable(context).getTableName();

        db.execSQL(String.format("alter table %s add column %s integer",atndOverivewTable, AttendenceOverviewTable.C_NOT_LAB));

        Cursor cursor = db.rawQuery("select * from subjectLink",null);

        boolean existTempAtndData = new TempAtndOverviewTable(context).doesTableExist(db);
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