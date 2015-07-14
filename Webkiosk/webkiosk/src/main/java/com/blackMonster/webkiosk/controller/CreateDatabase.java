package com.blackMonster.webkiosk.controller;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.DSSPData;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendenceTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;

import java.util.List;

public class CreateDatabase {
	static final String TAG = "createDatabase";
	public static final int DONE = 1;
	public static final int ERROR = -52;
	public static final String HAS_DATABASE_CREATED = "hasdbcreated";

	private static String userName = null;
	private static List<SubjectInfo> subjectInfos = null;

	public static int start(String colg, String enroll, String batch,
							CrawlerDelegate crawlerDelegate, Context context) {
		deleteOldDatabase(context);

		int result;
		try {
			scrapStudentDetails(crawlerDelegate,context);
			result = handleTimetable(colg, enroll, batch, context);

			if (!Timetable.isError(result)) {
				initDatabase(context);
				if (result == Timetable.TRANSFER_FOUND_DONE)
					createTempAtndOverviewFromPreregSub(crawlerDelegate, context);
				createInitiliseTable(context);
				createPreferences(context);
				result = DONE;
			}

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
		return Timetable.createDatabase(subjectInfos, colg, enroll, batch,
				context);
	}

	private static void scrapStudentDetails(CrawlerDelegate crawlerDelegate, Context context)
			throws Exception {
	///	M.log(TAG, "fetchATndoverview");
//		student = new SubjectAndStudentDetailsMain(getWaPP(context).connect);
		userName = crawlerDelegate.getStudentName();
		subjectInfos = crawlerDelegate.getSubjectInfoMain();
	}

	private static void deleteOldDatabase(Context context) {
		if (context.deleteDatabase(DbHelper.DB_NAME)) {
		}
	///	M.log(TAG, "Database deleted");
	}

	// It loads subjectInfos table, create nd load attendenceOverviewTable
	// and create attendence table for each subject
	private static void createInitiliseTable(Context context) throws Exception {
		///M.log(TAG, "createInitiliseTable");

//		SubjectLinkTable subLnkTable = new SubjectLinkTable(context);
		AttendenceOverviewTable aoTable = new AttendenceOverviewTable(context);

		for (SubjectInfo row : subjectInfos) {
//			subLnkTable.insert(row.getSubjectCode(), row.getLink(), row.isNotLab());
			aoTable.insert(row, 0);

			DetailedAttendenceTable atndTable = new DetailedAttendenceTable(row.getSubjectCode(),
					row.isNotLab(),context);
			atndTable.createTable();

		}
		UpdateAvgAtnd.update(subjectInfos, context);
		DSSPData.createTable(context);
		
	}

	public static void createTempAtndOverviewFromPreregSub(CrawlerDelegate crawlerDelegate,Context context) {
		///M.log(TAG, "createTempAtndOverviewFromPreregSub");

		List<SubjectInfo> preSubjectInfo = getSubInfoFromPrereg(crawlerDelegate, context);
		List<SubjectInfo> regSubjectInfo = getSubInfoFromReg(crawlerDelegate, context);
		List<SubjectInfo> subjectInfo = combineSubInfo(preSubjectInfo, regSubjectInfo);
		
		if (subjectInfo == null)
			return;
		// AttendanceUtils.getInstance(context).new TempAtndOverviewTable()
		TempAtndOverviewTable tempAtndOTable =new TempAtndOverviewTable(context);
		tempAtndOTable.dropTableifExist();
		tempAtndOTable.createTable(DbHelper.getInstance(context)
				.getWritableDatabase());
		for (SubjectInfo row : subjectInfo) {
			tempAtndOTable.insert(row, 0);
		}
	}

	private static List<SubjectInfo> combineSubInfo(List<SubjectInfo> preSubjectLink,
													List<SubjectInfo> regSubjectLink) {
		
		if (preSubjectLink==null && regSubjectLink==null) return null;
		if (preSubjectLink==null && regSubjectLink!=null) return regSubjectLink;
		if (preSubjectLink!=null && regSubjectLink==null) return preSubjectLink;
		
		for (SubjectInfo x : preSubjectLink) {
			if (! regSubjectLink.contains(x)) regSubjectLink.add(x);
		}
		
		return regSubjectLink;
	}

	private static List<SubjectInfo> getSubInfoFromReg(CrawlerDelegate crawlerDelegate, Context context) {

		try {
			return crawlerDelegate.getSubjectInfoFromSubRegistered();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<SubjectInfo> getSubInfoFromPrereg(CrawlerDelegate crawlerDelegate, Context context) {
		try {
			return crawlerDelegate.getSubjectInfoFromPreReg();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
//
//	private static void getSiteConnection(Context context) {
//
//		if (getWaPP(context).connect == null) {
//			getWaPP(context).resetSiteConnection();
//			getWaPP(context).connect = new SiteLogin(
//					MainPrefs.getColg(context));
//			getWaPP(context).connect.login(MainPrefs.getEnroll(context),
//					MainPrefs.getPassword(context), context);
//
//		}
//
//	}

    private static void createPreferences(Context context) {
		///M.log(TAG, "creating database preferences");
		Editor editor = context
				.getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
		editor.putBoolean(HAS_DATABASE_CREATED, true);
		editor.putString(MainPrefs.USER_NAME, userName);
		editor.commit();

	}

}
