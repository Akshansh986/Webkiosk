package com.blackMonster.webkiosk.databases.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.databases.DbHelper;

/**
* Created by akshansh on 19/04/15.
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
