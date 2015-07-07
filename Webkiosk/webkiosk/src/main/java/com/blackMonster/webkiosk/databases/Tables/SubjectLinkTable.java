package com.blackMonster.webkiosk.databases.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.CreateDatabase;
import com.blackMonster.webkiosk.crawler.subjectDetails.SubjectAndStudentDetailsMain;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.crawler.SubjectLink;

import java.util.List;

/**
* Contains link for detailed attendance of each subject.
*/
public class SubjectLinkTable {
    public static final String TABLE = "subjectLink";
    public static final String C_CODE = "code";
    public static final String C_LINK = "link";
    public static final String C_LTP = "LTP";  //0 if subject is Lab, 1 otherwise.
    SQLiteDatabase db;


    Context context;
    public SubjectLinkTable(Context context) {
        this.context = context;
    }


    public void createTable(SQLiteDatabase db) {
        String sql = String.format("create table %s"
                + "(%s TEXT primary key, %s TEXT, %s INTEGER)", TABLE,
                C_CODE, C_LINK, C_LTP);

        db.execSQL(sql);
    }

    public void insert(String code, String link, int LTP) {
        db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(C_CODE, code);
        values.put(C_LINK, link);
        values.put(C_LTP, LTP);
        db.insertWithOnConflict(TABLE, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
        // db.close();
    }

    public void update(SQLiteDatabase database, String code, String link, int LTP) {
        ContentValues values = new ContentValues();

        values.put(C_LINK, link);
        values.put(C_LTP, LTP);

        database.update(TABLE, values, C_CODE + "='" + code + "'", null);

    }

    public class Reader {
        Cursor cursor;

        public Reader() {
            db = DbHelper.getInstance(context).getReadableDatabase();
            cursor = db.query(TABLE, null, null, null, null, null, null); // SELECT
                                                                            // *
                                                                            // FROM
                                                                            // STATUS
                                                                            // ORDERBY
                                                                            // C_CREATED_AT
                                                                            // DESCENDING
        }

        public SubjectLink read() {
            if (!cursor.move(1))
                return null;
            SubjectLink sublnk = new SubjectLink();
            sublnk.setCode(cursor.getString(cursor.getColumnIndex(C_CODE)));
            sublnk.setLink(cursor.getString(cursor.getColumnIndex(C_LINK)));
            sublnk.setLTP(cursor.getInt(cursor.getColumnIndex(C_LTP)));
            ///Log.d(TAG, sublnk.toString());
            return sublnk;
        }

        public void close() {
            cursor.close();
            // db.close();
        }
    }

    public void refreshLinksAndLTP() {

        if (hasNullLinkOrLTP()) {
            ///Log.e(TAG, "has null link true");
            SQLiteDatabase database;
            database = DbHelper.getInstance(context).getWritableDatabase();
            SubjectAndStudentDetailsMain student;

            try {
                student = new SubjectAndStudentDetailsMain(
                        CreateDatabase.getWaPP(context).connect);
                List<SubjectLink> listt = student.getSubjectURL();
                for (SubjectLink row : listt) {
                    update(database, row.getCode(), row.getLink(), row.getLTP());
                }
                // Log.d(TAG, "links refreshed");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // database.close();
        }

    }

    private boolean hasNullLinkOrLTP() {
        boolean hasNullLinkOrLTP = false;
        Reader reader = new Reader();

        while (true) {
            SubjectLink row = reader.read();
            if (row == null)
                break;
            if (row.getLink() == null || row.getLTP() == -1) {
                hasNullLinkOrLTP = true;
                break;
            }
        }
        reader.close();
        return hasNullLinkOrLTP;
    }

    public int getLTP(String subCode) {
        db = DbHelper.getInstance(context).getReadableDatabase();
        //if (! doesTableExist(db)) return null;
        int result;
        String[] columns;
        columns = new String[1];
        columns[0] = C_LTP;


        Cursor cursor = db.query(TABLE, columns, C_CODE + "='" + subCode
                + "'", null, null, null, null);

        if (cursor == null)
            result = -1;
        else {
            if (cursor.getCount() == 0) {
                result = -1;
                // Log.e(TAG, "Subject Changed");
            } else {
                cursor.moveToFirst();
                result = cursor.getInt(cursor
                        .getColumnIndex(C_LTP));
            }
        }
        return result;

    }

}
