package com.blackMonster.webkiosk.databases.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

import java.util.List;

/**
 * You have to read timetable wiki to completelyl understand how it works.
 */
public class TimetableTable {
    public static final String C_DAY = "day";
    public static final String COLUMN_PREFIX = "c";  //Columns have name like "c9", "c13" etc for 9hr and 13hr class respectively.

    public static final String PRACTICAL_SECOND_CLASS = "same";//Every 2hr class has this written in it's second hour in database.
    public static final char ALIAS_LECTURE = 'L';
    public static final char ALIAS_TUTORIAL = 'T';
    public static final char ALIAS_PRACTICAL = 'P';

    public static final int CLASS_START_TIME = 9;
    public static final int CLASS_END_TIME = 17;
    private static final String TAG = "TimetableData";

    public static void createDb(String colg, String fileName, String batch,
                               String enroll, List<String> timetableDataList, Context context) {
            execueSQLCommands(colg, enroll, fileName, batch, context,
                    timetableDataList);     //list of sql commands as fetched from server.
    }

    private static void execueSQLCommands(String colg, String enroll,
                                          String fileName, String batch, Context context,
                                          List<String> timetableDataList) {
        try {
            for (String command : timetableDataList) {
                SQLiteDatabase db = TimetableDbHelper
                        .getInstanceAndCreateTable(colg, enroll, fileName,
                                batch, context).getWritableDatabase();

                db.execSQL(command.substring(0, command.length() - 1));
            }
            MainPrefs.setOnlineTimetableFileName(context, fileName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static String getTableName(Context context) {
        return MainPrefs.getBatch(context); //Batch name is used as timetable table name.
    }

    public static void insertRawData(int day, int time, String rawData,
                                     Context context) {
        SQLiteDatabase db = TimetableDbHelper
                .getWritableDatabaseifExist(context);
        if (db == null) return;
        ContentValues values = new ContentValues();
        String columnName = COLUMN_PREFIX + time;
        values.put(columnName, rawData);
        db.update(getTableName(context), values, C_DAY + "='" + day + "'", null);
    }
    /**
     * Replaces "same" with null;
     *
     * @param day  Use Calender.Monday etc.
     * @param time {9,10....17}
     * @return Raw timetable string.
     */
    public static String getRawData(int day, int time,
                                    Context context) {
        SQLiteDatabase db = TimetableDbHelper
                .getReadableDatabaseifExist(context);
        if (db == null) return null;

        String[] columnName = {COLUMN_PREFIX + time};
        String result;
        Cursor cursor = db.query(getTableName(context), columnName, C_DAY + "='" + day
                + "'", null, null, null, null);

        if (cursor == null) return null;

        cursor.moveToFirst();
        if (cursor.isNull(cursor.getColumnIndex(columnName[0])))
            result = null;
        else {
            result = cursor.getString(cursor.getColumnIndex(columnName[0]))
                    .trim();
            M.log(TAG, "init : " + result);
            result = filterSame(result);
            M.log(TAG, "same filtered : " + result);
            if (result.equals("")) result = null;
        }
        cursor.close();

        return result;
    }


    /**
     * Read wiki to understand timetable structure.
     * It just removes "same".
     */
    private static String filterSame(String str) {
        str = str.replaceAll(PRACTICAL_SECOND_CLASS + "#", "");
        str = str.replaceAll("#" + PRACTICAL_SECOND_CLASS, "");
        str = str.replaceAll(PRACTICAL_SECOND_CLASS, "");
        return str;
    }

}
