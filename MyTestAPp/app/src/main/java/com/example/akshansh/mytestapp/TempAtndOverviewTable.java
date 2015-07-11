package com.example.akshansh.mytestapp;

import android.content.Context;

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

}
