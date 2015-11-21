package com.blackMonster.webkiosk.databases.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.databases.DbHelper;

/**
* Table containing details of subjects fetched from "Pre reg subjects" of webkiosk website.
* As no attendance details is present on website, attendance is kept -1 here.
*/
public class TempAtndOverviewTable extends AttendenceOverviewTable {

    public TempAtndOverviewTable(Context context) {
        super(context);
    }

    @Override
    public String getTableName() {
        return "tempAtndOverview";
    }

    public void dropTableifExist() {
        SQLiteDatabase db = DbHelper.getInstance(context)
                .getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
    }


}
