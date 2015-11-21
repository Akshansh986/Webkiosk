package com.blackMonster.webkiosk.controller.Timetable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.controller.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by akshansh on 22/07/15.
 */
public class TimetableDelegate {
    public static final String TAG = "TimetableDataHandler";


    public static List<ClassTime> getDayWiseTimetable(int day, Context context) {
        List<ClassTime> list = new ArrayList<ClassTime>();

        SQLiteDatabase db = TimetableDbHelper
                .getReadableDatabaseifExist(context);
        if (db == null) return null;

        Cursor timetablecursor = db.query(TimetableTable.getTableName(context), null, TimetableTable.C_DAY + "='" + day
                + "'", null, null, null, null);

        if (timetablecursor == null)
            return null;

        timetablecursor.moveToFirst();
        int columnCount = timetablecursor.getColumnCount();
        String tmp;

        for (int i = 1; i < columnCount; ++i) {
            if (timetablecursor.isNull(i))
                continue;
            tmp = timetablecursor.getString(i);
            if (tmp.equals(TimetableTable.PRACTICAL_SECOND_CLASS))
                continue;

            String[] sub;
            sub = tmp.split("#");

            M.log(TAG, tmp + "         " + Arrays.toString(sub));

            for (int p = 0; p < sub.length; ++p) {
                String[] parts = sub[p].split("-");
                ClassTime classTime = new ClassTime(parts[0].charAt(0),
                        parts[1], parts[2],
                        getTimeFromColumnName(timetablecursor.getColumnName(i)), parts[3], day);
                list.add(classTime);
            }

        }
        timetablecursor.close();
        return list;

    }


    public static void deleteClass(int day, int time, Context context) {
        TimetableTable.insertRawData(day, time, null, context);
    }


    public static boolean addNewClass(ClassTime classTime,
                                      Context context) {
        return AddNewClass.addNewClass(classTime, context);
    }


    /**
     * Columns have name in form "c9","c11"..., here numbers are timing of class.
     *
     * @param s
     * @return integer from supplied string.
     */
    private static int getTimeFromColumnName(String s) {
        return Integer.parseInt(s.replaceAll("\\D", ""));
    }


}
