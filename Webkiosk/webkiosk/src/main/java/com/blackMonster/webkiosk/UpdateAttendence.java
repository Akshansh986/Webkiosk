package com.blackMonster.webkiosk;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.FetchDetailedAttendence;
import com.blackMonster.webkiosk.crawler.FetchDetailedAttendence.Attendence;
import com.blackMonster.webkiosk.databases.AttendenceData;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendenceTable;
import com.blackMonster.webkiosk.databases.Tables.SubjectLinkTable;
import com.blackMonster.webkiosk.databases.Tables.SubjectLinkTable.Reader;
import com.blackMonster.webkiosk.model.SubjectLink;

public class UpdateAttendence {
	static final String TAG = "UpdateAttendence";
	public static final int DONE = 1;
	public static final int ERROR = -1;

	// return ERROR or no. of new data added;
	public static int start(Context context) {
		int result;

		try {
			fillAllAttendenceTable(context);
			createPreferences(context);
			result = DONE;
		} catch (Exception e) {
			result = ERROR;
			e.printStackTrace();
		}
		return result;
	}

	private static void fillAllAttendenceTable(Context context)
			throws Exception {
		// int newAttendenceCount=0;
		SubjectLinkTable subLnkTable = AttendenceData.getInstance(context).new SubjectLinkTable();
		subLnkTable.refreshLinksAndLTP();

		Reader reader = subLnkTable.new Reader();

		while (true) {
			SubjectLink row = reader.read();
			if (row == null)
				break;

			if (row.link != null) {
				// Log.d(TAG, row.link);
				fillSingleTable(row.code, row.link, row.LTP, context);
			}

		}
		reader.close();

	}

	private static void fillSingleTable(String code, String link, int LTP,
			Context context) throws Exception {
		// Log.d(TAG, "single client");
		DetailedAttendenceTable detailedAttendence = AttendenceData
				.getInstance(context).new DetailedAttendenceTable(code, LTP);

		FetchDetailedAttendence loadAttendence = new FetchDetailedAttendence(
				CreateDatabase.getWaPP(context).connect,
				link, LTP, 0);

		detailedAttendence.openWritebleDb();
		detailedAttendence.deleteAllRows();
		while (true) {
			Attendence atnd = loadAttendence.getAttendence();
			if (atnd == null)
				break;
			detailedAttendence.insert(atnd.date, atnd.AttendenceBY,
					atnd.status, atnd.ClassType, atnd.LTP);

		}
		detailedAttendence.closeWritebleDb();
		loadAttendence.close();

	}

	private static void createPreferences(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				MainActivity.PREFS_NAME, 0);
		settings.edit()
				.putLong(RefreshServicePrefs.LAST_UPDATED,
						System.currentTimeMillis()).commit();
		RefreshServicePrefs.setPasswordUptoDate(context);
	}

}
