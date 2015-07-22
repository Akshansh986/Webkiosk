package com.blackMonster.webkiosk.Timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;

import java.util.ArrayList;
import java.util.List;

public class TimetableData {
    public static final String C_DAY = "day";
    public static final String COLUMN_PREFIX = "c";  //Columns have name like "c9", "c13" etc for 9hr and 13hr class respectively.

    public static final String PRACTICAL_SECOND_CLASS = "same";
    public static final char ALIAS_LECTURE = 'L';
    public static final char ALIAS_TUTORIAL = 'T';
    public static final char ALIAS_PRACTICAL = 'P';

    public static final int CLASS_START_TIME = 9;
    public static final int CLASS_END_TIME = 17;
    private static final String TAG = "TimetableData";

    public static int createDb(String colg, String fileName, String batch,
                               String enroll, Context context) {
        int result;
        // / M.log(TAG, "createdb");
        List<String> timetableDataList = new ArrayList<String>();
        result = TimetableFetch.getDataBundle(colg, fileName, batch,
                timetableDataList, context);

        if (result == TimetableFetch.DONE) {
            execueSQLCommands(colg, enroll, fileName, batch, context,
                    timetableDataList);
        }
        return result;

    }

    private static void execueSQLCommands(String colg, String enroll,
                                          String fileName, String batch, Context context,
                                          List<String> timetableDataList) {
        try {
            for (String command : timetableDataList) {
                // M.log(TAG, command.substring(0, command.length() - 1));
                SQLiteDatabase db = TimetableDbHelper
                        .getInstanceAndCreateTable(colg, enroll, fileName,
                                batch, context).getWritableDatabase();

                db.execSQL(command.substring(0, command.length() - 1));

            }
            MainPrefs.setOnlineTimetableFileName(context, fileName);
        } catch (SQLException e) {
            // M.log(TAG, "create timetable table exception");
            e.printStackTrace();
        }

    }


    public static String getTableName(Context context) {
        return MainPrefs.getBatch(context); //Batch name is used as timetable table name.
    }

    public static List<SingleClass> getDayWiseClass(int day, Context context) throws Exception {
        List<SingleClass> list = new ArrayList<SingleClass>();

        SQLiteDatabase db = TimetableDbHelper
                .getReadableDatabaseifExist(context);
        if (db == null) {
            // M.log(TAG, "timetable db not available");
            return list;
        }
        Cursor timetablecursor = db.query(getTableName(context), null, C_DAY + "='" + day
                + "'", null, null, null, null);

        if (timetablecursor == null)
            return null;

        timetablecursor.moveToFirst();
        int columnCount = timetablecursor.getColumnCount();
        String tmp;

        AttendenceOverviewTable atndOverviewTable = new AttendenceOverviewTable(context);

        Cursor atndOverviewTableCursor = atndOverviewTable.getData();

        TempAtndOverviewTable tempAtndOTable = new TempAtndOverviewTable(context);

        Cursor tempAtndOCursor = tempAtndOTable.getData();

        for (int i = 1; i < columnCount; ++i) {
            if (timetablecursor.isNull(i))
                continue;
            tmp = timetablecursor.getString(i);
            if (tmp.equals(PRACTICAL_SECOND_CLASS))
                continue;
            String[] sub;
            if (tmp.contains("#")) {
                sub = tmp.split("#");
                for (int p = 0; p < sub.length; ++p) {
                    String[] parts = sub[p].split("-");
                    SingleClass sc = new SingleClass(parts[0].charAt(0),
                            parts[1], parts[2], parts[3], i,
                            atndOverviewTableCursor, tempAtndOCursor);
                    if (sc.isSubjectFound())
                        list.add(sc);
                }
            } else {
                String[] parts = tmp.split("-");
                SingleClass sc = new SingleClass(parts[0].charAt(0),
                        parts[1], parts[2], parts[3], i,
                        atndOverviewTableCursor, tempAtndOCursor);
                if (sc.isSubjectFound())
                    list.add(sc);
            }

        }
        closeCursor(timetablecursor);
        closeCursor(tempAtndOCursor);
        closeCursor(atndOverviewTableCursor);
        return list;

    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null)
            cursor.close();
    }

    /**
     * Replaces "same" with null;
     *
     * @param currentDay  Use Calender.Monday etc.
     * @param currentTime {9,10....17}
     * @return Raw timetable string.
     */
    public static String getRawData(int currentDay, int currentTime,
                                    Context context) {
        SQLiteDatabase db = TimetableDbHelper
                .getReadableDatabaseifExist(context);
        if (db == null) return null;

        String[] columnName = {COLUMN_PREFIX + currentTime};
        String result;
        Cursor cursor = db.query(getTableName(context), columnName, C_DAY + "='" + currentDay
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

    public static void deleteClass(int day, int time, Context context) {
        insertRawData(day, time, null, context);
    }


    public static boolean addNewClass(ClassInfo classInfo,
                                      Context context) {
        boolean result;
        if (isCellEmpty(classInfo, context)) {
            insertRawData(classInfo.getDay(), classInfo.getTime(),
                    createRawData(classInfo),
                     context);

            result = true;
        } else
            result = false;

        return result;
    }

    private static boolean isCellEmpty(ClassInfo classInfo, Context context) {

        String rawData;

        if (classInfo.getTime() > CLASS_START_TIME) {
            rawData = getMyClass(classInfo.getDay(), classInfo.getTime() - 1,context);
            if (rawData != null && isOfTwoHr(rawData))
                return false;
        }

        rawData = getMyClass(classInfo.getDay(), classInfo.getTime(), context);

        if (rawData != null) return false;

        boolean isNewOfTwoHr = TimetableUtils.isOfTwoHr(classInfo.getClassType(), classInfo.getSubCode());
        if (isNewOfTwoHr) {
            if (classInfo.getTime() + 1 > CLASS_END_TIME) return false;
            rawData = getMyClass(classInfo.getDay(), classInfo.getTime() + 1, context);
            if (rawData != null)
                return false;
        }

        return true;
    }


    private static String getMyClass(String rawData, Context context) {
        if (rawData == null || rawData.equals("")) {
            M.log(TAG, "rawDAta null");
            return null;
        }
        List<SubjectInfo> subCodeList = new AttendenceOverviewTable(context)
                .getAllSubjectInfo();

        String singleRaw[];
        if (rawData.contains("#")) {
            singleRaw = rawData.split("#");

            for (int p = 0; p < singleRaw.length; ++p) {
                String subCode = singleRaw[p].split("-")[1];
                for (SubjectInfo listItem : subCodeList)
                    if (listItem.getSubjectCode().contains(subCode))
                        return singleRaw[p];
            }
        } else {

            String subCode = rawData.split("-")[1];

            for (SubjectInfo listItem : subCodeList)
                if (listItem.getSubjectCode().contains(subCode))
                    return rawData;
        }
        return null;
    }

    private static String getMyClass(int day, int time, Context context) {
        return getMyClass(getRawData(day, time, context), context);
    }

    private static boolean isOfTwoHr(String rawData) {
        char classType = rawData.split("-")[0].charAt(0);
        String subCode = rawData.split("-")[1];
        return TimetableUtils.isOfTwoHr(classType, subCode);
    }

    private static String createRawData(ClassInfo classInfo) {
        return classInfo.getClassType() + "-" + classInfo.getSubCode() +
                "-" + classInfo.getVenue() + "-" + classInfo.getFaculty();
    }

}
