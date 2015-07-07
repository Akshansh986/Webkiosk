package com.blackMonster.webkiosk.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.TimetableFetch;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;
import com.blackMonster.webkiosk.model.SubjectLink;

import java.util.ArrayList;
import java.util.List;

public class TimetableData {
	public static final String C_DAY = "day";

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
				SQLiteDatabase db = TimetableDataHelper
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

	public static List<SingleClass> getDayWiseClass(int day, String batchTable,
			Context context) throws Exception {
		List<SingleClass> list = new ArrayList<SingleClass>();

		SQLiteDatabase db = TimetableDataHelper
				.getReadableDatabaseifExist(context);
		if (db == null) {
			// M.log(TAG, "timetable db not available");
			return list;
		}
		Cursor timetablecursor = db.query(batchTable, null, C_DAY + "='" + day
				+ "'", null, null, null, null);

		if (timetablecursor == null)
			return null;

		timetablecursor.moveToFirst();
		int columnCount = timetablecursor.getColumnCount();
		String tmp;

		AttendenceOverviewTable atndOverviewTable = AttendenceData
				.getInstance(context).new AttendenceOverviewTable();

		Cursor atndOverviewTableCursor = atndOverviewTable.getData();

		TempAtndOverviewTable tempAtndOTable = AttendenceData
				.getInstance(context).new TempAtndOverviewTable();

		Cursor tempAtndOCursor = tempAtndOTable.getData();
		//if (tempAtndOCursor == null)
			// / M.log(TAG, "temp atnd data is null");

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
		atndOverviewTable.close();
		// db.close();
		return list;

	}

	private static void closeCursor(Cursor cursor) {
		if (cursor != null)
			cursor.close();
		else
			cursor = null;
	}

	public static boolean showRecentUpdatedTag(Context context) {
		boolean result;

		if (RefreshServicePrefs.isRecentlyUpdated(context)
				|| RefreshServicePrefs.isStatus(
						RefreshServicePrefs.REFRESHING_D, context))
			result = true;

		else
			result = false;
		return result;
	}

	public static String getFormattedTime(int time) {
		if (time < 12) {
			return time + " AM";

		}
		if (time == 12) {
			return time + " NOON";

		}
		return (time - 12) + " PM";

	}

	public static String getRawData(int currentDay, int currentTime,
			String table, Context context) {
		SQLiteDatabase db = TimetableDataHelper
				.getReadableDatabaseifExist(context);
		if (db == null)
			return null;
		String[] columnName = { "c" + currentTime };
		String result;
		Cursor cursor = db.query(table, columnName, C_DAY + "='" + currentDay
				+ "'", null, null, null, null);

		if (cursor == null)
			result = null;
		else {
			cursor.moveToFirst();
			if (cursor.isNull(cursor.getColumnIndex(columnName[0])))
				result = null;
			else{
				result = cursor.getString(cursor.getColumnIndex(columnName[0]))
						.trim();
				M.log(TAG, "init : " + result);
				result = filterSame(result);
				M.log(TAG, "same filtered : " + result);
				if (result.equals("")) result = null;
				
			}
		}

		if (cursor != null)
			cursor.close();
		// if (db != null)
		// db.close();
		// M.log(TAG, "result" + result);
		return result;
	}

	private static String filterSame(String str) {
		str = str.replaceAll(PRACTICAL_SECOND_CLASS + "#", "");
		str = str.replaceAll("#"+ PRACTICAL_SECOND_CLASS, "");
		str = str.replaceAll(PRACTICAL_SECOND_CLASS, "");
		return str;
	}
	
	public static void insertRawData(int day, int time, String rawData,
			String table, Context context) {

		SQLiteDatabase db = TimetableDataHelper
				.getWritableDatabaseifExist(context);
		if (db==null) return;
		ContentValues values = new ContentValues();
		String columnName = "c" + time;
		values.put(columnName, rawData);
		db.update(table, values, C_DAY + "='" + day + "'", null);
	}
	
	public static void deleteClass(int day, int time, Context context) {
		insertRawData(day, time, null, MainPrefs.getBatch(context), context);
	}
	
	
	
	
	public static boolean addNewClass(int day, int time, char classType,
			String subCode, String venue, String teacherCodes, String table,
			Context context) {
		boolean result;
		if (isCellEmpty(day, time, classType, subCode, table, context)) {
			insertRawData(day, time,
					createRawData(classType, subCode, venue, teacherCodes),
					table, context);
			
			result = true;
		} else
			result = false;
		
		return result;
	}
	
	private static boolean isCellEmpty(int day, int time, char classType,
			String subCode, String table, Context context) {

		String rawData;
		
		if (time > CLASS_START_TIME){ 
			rawData = getMyClass(day, time-1, table, context);
			if (rawData != null && isOfTwoHr(rawData,context)) 
				return false;
		}
		
		rawData = getMyClass(day, time, table, context);
		
		if (rawData != null ) return false;
		
		boolean isNewOfTwoHr = isOfTwoHr(classType, subCode);
		if (isNewOfTwoHr) {
			if (time + 1  > CLASS_END_TIME) return false;
			rawData = getMyClass(day, time+1, table, context);
			if (rawData != null) 
				return false;
		}
		
		return true;

/*
		String rawData = getRawData(day, time, table, context);

		if (!isNewOfTwoHr) {
			result = !isMyClass(rawData, context);
			// result = rawData == null;
		} else {
			// result = rawData == null;
			result = !isMyClass(rawData, context);
			boolean resultNext;
			if (time + 1 <= CLASS_END_TIME)
				resultNext = getRawData(day, time + 1, table, context) == null;
			else
				resultNext = false;
			result = result && resultNext;
		}

		return result;*/

	}

	
	private static String getMyClass(String rawData, Context context) {
		if (rawData == null || rawData.equals("")) {
			M.log(TAG, "rawDAta null");
			return null;
		}
		List<SubjectLink> subCodeList = AttendenceData.getInstance(context).new AttendenceOverviewTable()
				.getAllSubjectLink();

		String singleRaw[];
		if (rawData.contains("#")) {
			singleRaw = rawData.split("#");

			for (int p = 0; p < singleRaw.length; ++p) {
				String subCode = singleRaw[p].split("-")[1];
				for (SubjectLink listItem : subCodeList)
					if (listItem.code.contains(subCode))
						return singleRaw[p];
			}
		} else {
			
			String subCode = rawData.split("-")[1];

			for (SubjectLink listItem : subCodeList)
				if (listItem.code.contains(subCode))
					return rawData;
		}
		return null;
	}
	
	private static String getMyClass(int day , int time, String table, Context context) {
		return getMyClass(getRawData(day, time, table, context), context);
	}
	
	public static boolean isOfTwoHr(char classType, String subCode) {
		boolean result = false;
		if (classType == TimetableData.ALIAS_PRACTICAL)
			result = true;
		else if (classType == TimetableData.ALIAS_TUTORIAL
				&& (subCode.equals("PD111") || subCode.equals("PD211")))
			result = true;
		return result;
	}
	
	private static boolean isOfTwoHr(String rawData, Context context) {
		char classType = rawData.split("-")[0].charAt(0);
		String subCode = rawData.split("-")[1];
		return isOfTwoHr(classType, subCode);
	}
	
	public static String createRawData(char type, String subCode, String venue,
			String teacherCodes) {
		return type + "-" + subCode + "-" + venue + "-" + teacherCodes;
	}

	
	
	
	
	
/*
	public static void swapClass(int currentDay, int currentTime, int newDay,
			int newTime, String table, Context context) {
		String tmp = getRawData(currentDay, currentTime, table, context);

		SQLiteDatabase db = TimetableDataHelper
				.getWritableDatabaseifExist(context);
		if (db == null)
			return;
		updateRawData(currentDay, currentTime,
				getRawData(newDay, newTime, table, context), table, context, db);
		updateRawData(newDay, newTime, tmp, table, context, db);
		// db.close();
	}

	public static void updateRawData(int day, int time, String rawData,
			String table, Context context, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		String columnName = "c" + time;
		rawData = getCancantenatedRawData(
				getRawData(day, time, table, context), rawData);
		values.put(columnName, rawData);
		db.update(table, values, C_DAY + "='" + day + "'", null);
	}

	private static String getCancantenatedRawData(String oldData, String newData) {
		if (newData == null) return null;	//for deleting 
		if (oldData == null)
			return newData;
		else
			return newData + "#" + oldData;

	}

	public static void updateRawData(int day, int time, String rawData,
			String table, Context context) {
		SQLiteDatabase db = TimetableDataHelper
				.getWritableDatabaseifExist(context);
		if (db == null)
			return;
		updateRawData(day, time, rawData, table, context, db);
		// db.close();
	}

	public static boolean insertData(int day, int time, char classType,
			String subCode, String venue, String teacherCodes, String table,
			Context context) {
		boolean result;
		if (isCellEmpty(day, time, classType, subCode, table, context)) {
			updateRawData(day, time,
					createRawData(classType, subCode, venue, teacherCodes),
					table, context);
			if (isOfTwoHr(classType, subCode))
				updateRawData(day, time + 1, PRACTICAL_SECOND_CLASS, table,
						context);
			result = true;
		} else
			result = false;
		return result;
	}

	private static boolean isCellEmpty(int day, int time, char classType,
			String subCode, String table, Context context) {
		boolean result = false;
		boolean isNewOfTwoHr = isOfTwoHr(classType, subCode);

		String rawData = getRawData(day, time, table, context);

		if (!isNewOfTwoHr) {
			result = !isMyClass(rawData, context);
			// result = rawData == null;
		} else {
			// result = rawData == null;
			result = !isMyClass(rawData, context);
			boolean resultNext;
			if (time + 1 <= CLASS_END_TIME)
				resultNext = getRawData(day, time + 1, table, context) == null;
			else
				resultNext = false;
			result = result && resultNext;
		}

		return result;

	}

	private static boolean isMyClass(String rawData, Context context) {
		if (rawData == null) {
			M.log(TAG, "rawDAta null");
			return false;
		}
		List<SubjectLink> subCodeList = AttendenceData.getInstance(context).new AttendenceOverviewTable()
				.getAllSubjectLink();

		String singleRaw[];
		if (rawData.contains("#")) {
			singleRaw = rawData.split("#");

			for (int p = 0; p < singleRaw.length; ++p) {
				String subCode = singleRaw[p].split("-")[1];
				for (SubjectLink listItem : subCodeList)
					if (listItem.code.contains(subCode))
						return true;
			}
		} else {
			String subCode = rawData.split("-")[1];

			for (SubjectLink listItem : subCodeList)
				if (listItem.code.contains(subCode))
					return true;
		}
		return false;
	}

	public static void deleteClass(int day, int time, Context context) {
		updateRawData(day, time, null, MainPrefs.getBatch(context), context);

		if (time < CLASS_END_TIME) {

			String nextData = TimetableData.getRawData(day, time + 1,
					MainPrefs.getBatch(context), context);

			if (nextData != null) {

				if (nextData.equals(TimetableData.PRACTICAL_SECOND_CLASS))
					updateRawData(day, time + 1, null,
							MainPrefs.getBatch(context), context);

			}
		}
	}

	public static String createRawData(char type, String subCode, String venue,
			String teacherCodes) {
		return type + "-" + subCode + "-" + venue + "-" + teacherCodes;
	}

	public static boolean isOfTwoHr(char classType, String subCode) {
		boolean result = false;
		if (classType == TimetableData.ALIAS_PRACTICAL)
			result = true;
		else if (classType == TimetableData.ALIAS_TUTORIAL
				&& (subCode.equals("PD111") || subCode.equals("PD211")))
			result = true;
		return result;
	}
*/

}
