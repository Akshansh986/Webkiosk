package com.blackMonster.webkiosk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.FetchPreRegSub;
import com.blackMonster.webkiosk.crawler.FetchSubReg;
import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkiosk.crawler.StudentDetails;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendenceTable;
import com.blackMonster.webkiosk.databases.Tables.SubjectLinkTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;
import com.blackMonster.webkiosk.dateSheet.DSSPData;
import com.blackMonster.webkiosk.model.SubjectLink;

import java.util.List;

public class CreateDatabase {
	static final String TAG = "createDatabase";
	public static final int DONE = 1;
	public static final int ERROR = -52;
	public static final String HAS_DATABASE_CREATED = "hasdbcreated";

	private static String userName = null;
	private static List<SubjectLink> subjectLink = null;
	private static StudentDetails student = null;

	public static int start(String colg, String enroll, String batch,
			Context context) {
		deleteOldDatabase(context);

		int result;
		try {
			scrapStudentDetails(context);
			result = handleTimetable(colg, enroll, batch, context);

			if (!Timetable.isError(result)) {
				initDatabase(context);
				if (result == Timetable.TRANSFER_FOUND_DONE)
					createTempAtndOverviewFromPreregSub(context);
				createInitiliseTable(context);
				createPreferences(context);
				result = DONE;
			}

			student.close();
		} catch (Exception e) {
			result = ERROR;
			e.printStackTrace();
		}

		return result;
	}

	private static void initDatabase(Context context) {

		DbHelper.getInstance(context);

	}

	private static int handleTimetable(String colg, String enroll,
			String batch, Context context) {
		return Timetable.createDatabase(subjectLink, colg, enroll, batch,
				context);
	}

	private static void scrapStudentDetails(Context context)
			throws Exception {
	///	M.log(TAG, "fetchATndoverview");
		student = new StudentDetails(getWaPP(context).connect);
		userName = student.getName();
		subjectLink = student.getSubjectURL();
	}

	private static void deleteOldDatabase(Context context) {
		if (context.deleteDatabase(DbHelper.DB_NAME)) {
		}
	///	M.log(TAG, "Database deleted");
	}

	// It loads subjectLink table, create nd load attendenceOverviewTable
	// and create attendence table for each subject
	private static void createInitiliseTable(Context context) throws Exception {
		///M.log(TAG, "createInitiliseTable");

		SubjectLinkTable subLnkTable = new SubjectLinkTable(context);
		AttendenceOverviewTable aoTable = new AttendenceOverviewTable(context);

		for (SubjectLink row : subjectLink) {
			subLnkTable.insert(row.getCode(), row.getLink(), row.getLTP());
			aoTable.insert(row, 0);

			DetailedAttendenceTable atndTable = new DetailedAttendenceTable(row.getCode(),
					row.getLTP(),context);
			atndTable.createTable();

		}
		TempAtndData.storeData(subjectLink, context);
		DSSPData.createTable(context);
		
	}

	public static void createTempAtndOverviewFromPreregSub(Context context) {
		///M.log(TAG, "createTempAtndOverviewFromPreregSub");

		List<SubjectLink> preSubjectLink = getSubLinkFromPrereg(context);
		List<SubjectLink> regSubjectLink = getSubLinkFromReg(context);
		List<SubjectLink> subjectLink = combineSubLink(preSubjectLink, regSubjectLink);
		
		if (subjectLink == null)
			return;
		// AttendanceUtils.getInstance(context).new TempAtndOverviewTable()
		TempAtndOverviewTable tempAtndOTable =new TempAtndOverviewTable(context);
		tempAtndOTable.dropTableifExist();
		tempAtndOTable.createTable(DbHelper.getInstance(context)
				.getWritableDatabase());
		for (SubjectLink row : subjectLink) {
			tempAtndOTable.insert(row, 0);
		}
	}

	private static List<SubjectLink> combineSubLink(List<SubjectLink> preSubjectLink,
			List<SubjectLink> regSubjectLink) {
		
		if (preSubjectLink==null && regSubjectLink==null) return null;
		if (preSubjectLink==null && regSubjectLink!=null) return regSubjectLink;
		if (preSubjectLink!=null && regSubjectLink==null) return preSubjectLink;
		
		for (SubjectLink x : preSubjectLink) {
			if (! regSubjectLink.contains(x)) regSubjectLink.add(x);
		}
		
		return regSubjectLink;
	}

	private static List<SubjectLink> getSubLinkFromReg(Context context) {
		FetchSubReg nameCode = null;
		
		List<SubjectLink> list;
		try {
			getSiteConnection(context);
			nameCode = new FetchSubReg(getWaPP(context).connect);
			list = nameCode.getSubjectURL();
			nameCode.close();
		} catch (Exception e) {
			list = null;
			e.printStackTrace();

		}
		return list;
	}

	private static List<SubjectLink> getSubLinkFromPrereg(Context context) {
		FetchPreRegSub nameCode = null;
		
		List<SubjectLink> list;
		try {
			getSiteConnection(context);
			nameCode = new FetchPreRegSub(getWaPP(context).connect);
			list = nameCode.getSubjectURL();
			nameCode.close();
		} catch (Exception e) {
			list = null;
			e.printStackTrace();

		}
		return list;

	}

	private static void getSiteConnection(Context context) {

		if (getWaPP(context).connect == null) {
			getWaPP(context).resetSiteConnection();
			getWaPP(context).connect = new SiteConnection(
					MainPrefs.getColg(context));
			getWaPP(context).connect.login(MainPrefs.getEnroll(context),
					MainPrefs.getPassword(context), context);

		}

	}

	public static WebkioskApp getWaPP(Context context) {
		WebkioskApp a = null;
		if (context instanceof Activity)
			a = ((WebkioskApp) ((Activity) context).getApplication());
		else if (context instanceof Service)
			a = ((WebkioskApp) ((Service) context).getApplication());
		else
			a = ((WebkioskApp) context.getApplicationContext());
		return a;
	}

	private static void createPreferences(Context context) {
		///M.log(TAG, "creating database preferences");
		Editor editor = context
				.getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
		editor.putBoolean(HAS_DATABASE_CREATED, true);
		editor.putString(MainPrefs.USER_NAME, userName);
		editor.commit();

	}

}
