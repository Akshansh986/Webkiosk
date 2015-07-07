package com.blackMonster.webkiosk.databases.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.crawler.SubjectLink;

import java.util.ArrayList;
import java.util.List;

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
                                + "(%s text primary key, %s text, %s real, %s real, %s real, %s real, %s int)",
                        getTableName(), C_CODE, C_NAME, C_OVERALL,
                        C_LECTURE, C_TUTORIAL, C_PRACTICAL, C_IS_MODIFIED);
        db.execSQL(sql);

    }

    public void insert(SubjectLink subDetail, int isModified) {
        db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(C_CODE, subDetail.getCode());
        values.put(C_NAME, subDetail.getName());

        values.put(C_OVERALL, subDetail.getOverall());
        values.put(C_LECTURE, subDetail.getLect());
        values.put(C_TUTORIAL, subDetail.getTute());
        values.put(C_PRACTICAL, subDetail.getPract());
        values.put(C_IS_MODIFIED, isModified);

        db.insertWithOnConflict(getTableName(), null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void update(SubjectLink subDetail, int isModified) {
        db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(C_OVERALL, subDetail.getOverall());
        values.put(C_LECTURE, subDetail.getLect());
        values.put(C_TUTORIAL, subDetail.getTute());
        values.put(C_PRACTICAL, subDetail.getPract());
        values.put(C_IS_MODIFIED, isModified);


        db.update(getTableName(), values, C_CODE + "='" + subDetail.getCode()
                + "'", null);
    }

    Cursor cursor;

    public Cursor getData(String code, int isLTP) {
        db = DbHelper.getInstance(context).getReadableDatabase();
        if (! doesTableExist(db)) return null;

        String[] columns;

        if (isLTP == 1) {
            columns = new String[3];
            columns[0] = C_OVERALL;
            columns[1] = C_LECTURE;
            columns[2] = C_TUTORIAL;
        } else {
            columns = new String[1];
            columns[0] = C_PRACTICAL;
        }

        cursor = db.query(getTableName(), columns, C_CODE + "='" + code
                + "'", null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getData() {
        db = DbHelper.getInstance(context).getReadableDatabase();
        if (! doesTableExist(db)) return null;

        cursor = db.rawQuery("select rowid _id,* from " + getTableName()
                + " ORDER BY " + C_OVERALL + " DESC", null);

        return cursor;
    }

    public List<SubjectLink> getAllSubjectLink() {
        ///Log.d(TAG, "getallsubjectLink");
        Cursor cursor = getData();
        List<SubjectLink> list= new ArrayList<SubjectLink>();;
        if (cursor == null)
            return null;
        else {
            cursor.moveToFirst();
            while (true) {
                SubjectLink subAtnd = new SubjectLink();

                subAtnd.setOverall(cursor.getInt(cursor
                        .getColumnIndex(C_OVERALL)));


                subAtnd.setLect(cursor.getInt(cursor
                        .getColumnIndex(C_LECTURE)));
                subAtnd.setTute(cursor.getInt(cursor
                        .getColumnIndex(C_TUTORIAL)));
                subAtnd.setPract(cursor.getInt(cursor
                        .getColumnIndex(C_PRACTICAL)));
                subAtnd.setCode(cursor.getString(cursor
                        .getColumnIndex(C_CODE)));
                subAtnd.setName(cursor.getString(cursor
                        .getColumnIndex(C_NAME)));
                list.add(subAtnd);
                if (! cursor.moveToNext()) break;
            }
        }
        cursor.close();
        return list;
    }

    public int getSubjectLink(SubjectLink subAtnd, String code) {
        int result;
        SQLiteDatabase db = DbHelper.getInstance(context)
                .getReadableDatabase();


        String[] columns;

        columns = new String[4];
        columns[0] = C_OVERALL;
        columns[1] = C_LECTURE;
        columns[2] = C_TUTORIAL;

        columns[3] = C_PRACTICAL;

        cursor = db.query(getTableName(), columns, C_CODE + "='" + code
                + "'", null, null, null, null);

        if (cursor == null)
            result = ERROR;
        else {
            if (cursor.getCount() == 0) {
                result = SUBJECT_CHANGED;
                // Log.e(TAG, "Subject Changed");
            } else {
                cursor.moveToFirst();
                subAtnd.setOverall(cursor.getInt(cursor
                        .getColumnIndex(C_OVERALL)));
                subAtnd.setLect(cursor.getInt(cursor
                        .getColumnIndex(C_LECTURE)));
                subAtnd.setTute(cursor.getInt(cursor
                        .getColumnIndex(C_TUTORIAL)));
                subAtnd.setPract(cursor.getInt(cursor
                        .getColumnIndex(C_PRACTICAL)));
                result = DONE;
            }
        }

        return result;

    }

     public boolean doesTableExist(SQLiteDatabase db) {

         Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+getTableName()+"'", null);

         if(cursor!=null) {
                if(cursor.getCount()>0) {
                                    cursor.close();
                    return true;
                }
                            cursor.close();
            }
            return false;
     }

    public void close() {
        if (cursor != null)
            cursor.close();
        // if (db != null) db.close();
    }

}
