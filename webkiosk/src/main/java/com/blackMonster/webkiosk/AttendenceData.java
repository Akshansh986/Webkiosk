package com.blackMonster.webkiosk;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.StudentDetails.SubjectLink;

//close reader object;
public class AttendenceData {
	static final String TAG = "AttendenceData";
	public static final String DB_NAME = "attendence.db";

	public static final int DB_VERSION = 1;
	public static final String SUB_CODE = "subcode";
	public static final String SUB_NAME = "subname";

	Context context;

	private static AttendenceData atndDataInstance = null;

	public static AttendenceData getInstance(Context context) {
		if (atndDataInstance == null) {
			atndDataInstance = new AttendenceData(
					context.getApplicationContext());
		}
		return atndDataInstance;
	}

	private AttendenceData(Context context) {
		// Log.d(TAG, "readched to attendencedata");
		this.context = context;

	}

	public void close() {
		context = null;
		atndDataInstance = null;
	}

	public static boolean isLab(String subCode, Context context) {
		return (AttendenceData.getInstance(context).new SubjectLinkTable().getLTP(subCode)) == 0;
		//return subName.toUpperCase().contains("LAB");
	}

	public class SubjectLinkTable {
		public static final String TABLE = "subjectLink";
		public static final String C_CODE = "code";
		public static final String C_LINK = "link";
		public static final String C_LTP = "LTP";
		SQLiteDatabase db;

		public void createTable(SQLiteDatabase db) {
			String sql = String.format("create table %s"
					+ "(%s TEXT primary key, %s TEXT, %s INTEGER)", TABLE,
					C_CODE, C_LINK, C_LTP);

			// Log.d(TAG,"createTable subjectLink : " + sql);
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
				sublnk.code = cursor.getString(cursor.getColumnIndex(C_CODE));
				sublnk.link = cursor.getString(cursor.getColumnIndex(C_LINK));
				sublnk.LTP = cursor.getInt(cursor.getColumnIndex(C_LTP));
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
				StudentDetails student;

				try {
					student = new StudentDetails(
							CreateDatabase.getWaPP(context).connect);
					List<SubjectLink> listt = student.getSubjectURL();
					for (SubjectLink row : listt) {
						update(database, row.code, row.link, row.LTP);
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
				if (row.link == null || row.LTP == -1) {
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

	public class AttendenceOverviewTable {
		// public static final String TABLE = "attendanceOverview";
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

		public String getTableName() {
			return "attendanceOverview";
		}

		public void createTable(SQLiteDatabase db) {
			String sql = String
					.format("create table %s"
							+ "(%s text primary key, %s text, %s real, %s real, %s real, %s real, %s int)",
							getTableName(), C_CODE, C_NAME, C_OVERALL,
							C_LECTURE, C_TUTORIAL, C_PRACTICAL, C_IS_MODIFIED);
			// Log.d(TAG,"createTable AttendenceOverview : " + sql);
			db.execSQL(sql);

		}

		public void insert(SubjectLink subDetail, int isModified) {
			db = DbHelper.getInstance(context).getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(C_CODE, subDetail.code);
			values.put(C_NAME, subDetail.name);
			values.put(C_OVERALL, subDetail.overall);
			values.put(C_LECTURE, subDetail.lect);
			values.put(C_TUTORIAL, subDetail.tute);
			values.put(C_PRACTICAL, subDetail.pract);
			values.put(C_IS_MODIFIED, isModified);

			db.insertWithOnConflict(getTableName(), null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			// db.close();
		}

		public void update(SubjectLink subDetail, int isModified) {
			db = DbHelper.getInstance(context).getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(C_OVERALL, subDetail.overall);
			values.put(C_LECTURE, subDetail.lect);
			values.put(C_TUTORIAL, subDetail.tute);
			values.put(C_PRACTICAL, subDetail.pract);
			values.put(C_IS_MODIFIED, isModified);

			db.update(getTableName(), values, C_CODE + "='" + subDetail.code
					+ "'", null);
			// db.close();
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

		public  List<SubjectLink> getAllSubjectLink() {
			///Log.d(TAG, "getallsubjectLink");
			Cursor cursor = getData();
			List<SubjectLink> list= new ArrayList<SubjectLink>();;
			if (cursor == null)
				return null;
			else {
				cursor.moveToFirst();
				while (true) {
					SubjectLink subAtnd = new SubjectLink();
					subAtnd.overall = cursor.getInt(cursor
							.getColumnIndex(C_OVERALL));
					subAtnd.lect = cursor.getInt(cursor
							.getColumnIndex(C_LECTURE));
					subAtnd.tute = cursor.getInt(cursor
							.getColumnIndex(C_TUTORIAL));
					subAtnd.pract = cursor.getInt(cursor
							.getColumnIndex(C_PRACTICAL));
					subAtnd.code = cursor.getString(cursor
							.getColumnIndex(C_CODE));
					subAtnd.name = cursor.getString(cursor
							.getColumnIndex(C_NAME));
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
					subAtnd.overall = cursor.getInt(cursor
							.getColumnIndex(C_OVERALL));
					subAtnd.lect = cursor.getInt(cursor
							.getColumnIndex(C_LECTURE));
					subAtnd.tute = cursor.getInt(cursor
							.getColumnIndex(C_TUTORIAL));
					subAtnd.pract = cursor.getInt(cursor
							.getColumnIndex(C_PRACTICAL));
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

	public class TempAtndOverviewTable extends AttendenceOverviewTable {
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

	public class DetailedAttendenceTable {
		public static final String C_SNO = "SNo";
		public static final String C_DATE = "date";
		public static final String C_ATTENDENCE_BY = "attendenceBy";
		public static final String C_STATUS = "status";
		public static final String C_CLASS_TYPE = "classType";
		public static final String C_LTP = "LTP";
		String TABLE;
		int isLTP;
		SQLiteDatabase db;

		public DetailedAttendenceTable(String Table, int LTP) {
			TABLE = Table;
			isLTP = LTP;

		}

		public void createTable() {
			SQLiteDatabase db = DbHelper.getInstance(context)
					.getWritableDatabase();
			String sql;
			//if (isLTP == 1) {
				sql = String
						.format("create table %s"
								+ "(%s text, %s text, %s INTEGER, %s text, %s text, PRIMARY KEY (%s, %s, %s, %s) )",
								TABLE, C_DATE, C_ATTENDENCE_BY, C_STATUS,
								C_CLASS_TYPE, C_LTP, C_DATE, C_ATTENDENCE_BY,
								C_CLASS_TYPE, C_LTP);
			/*} else {
				sql = String
						.format("create table %s"
								+ "(%s text primary key, %s text, %s INTEGER, %s text)",
								TABLE, C_DATE, C_ATTENDENCE_BY, C_STATUS,
								C_CLASS_TYPE);
			}*/

			try {
				db.execSQL(sql);
			} catch (SQLException e) {
				// Log.e(TAG,
				// "DetailedAttendence table creation error or table already exist");

			}
			db.close();
		}

		public void insert(String date, String attendenceBY, int status,
				String classType, String LTP) {

			ContentValues values = new ContentValues();

			values.put(C_DATE, date);
			values.put(C_ATTENDENCE_BY, attendenceBY);
			values.put(C_STATUS, status);
			values.put(C_CLASS_TYPE, classType);
			if (isLTP == 1)
				values.put(C_LTP, LTP);

			long a = db.insert(TABLE, null, values);
			// Log.d(TAG, "insert attendence result " + a);

		}

		Cursor cursor;

		public Cursor getData() {
			db = DbHelper.getInstance(context).getReadableDatabase();

			cursor = db.rawQuery("select rowid _id,* from " + TABLE
					+ " ORDER BY " + "_id" + " DESC", null);
			if (cursor != null)
				cursor.moveToFirst();
			return cursor;
		}

		public void close() {
			cursor.close();
			// db.close();
		}

		public void openWritebleDb() {
			db = DbHelper.getInstance(context).getWritableDatabase();
		}

		public void deleteAllRows() {
			db.delete(TABLE, null, null);

		}

		public void closeWritebleDb() {
			// if (db != null) db.close();
		}

	}

}
