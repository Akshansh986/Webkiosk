package com.blackMonster.webkiosk.databases;

import android.content.Context;

import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;


//close reader object;
public class AttendanceUtils {

    public static boolean isLab(String subCode, Context context) {

        if (new AttendenceOverviewTable(context).isNotLab(subCode) == 0)
            return true;
        else {
            TempAtndOverviewTable tao = new TempAtndOverviewTable(context);
            if (tao.doesTableExist(DbHelper.getInstance(context).getReadableDatabase()))
                return tao.isNotLab(subCode) == 0;
            else return false;
        }
    }

}
