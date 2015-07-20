package com.blackMonster.webkiosk.controller;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.DbHelper;
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
			scrapStudentAndSubjectInfo(crawlerDelegate, context);
			result = handleTimetable(colg, enroll, batch, context);

			if ( ! Timetable.isError(result)) {
				initDatabase(context);
				if (result == Timetable.TRANSFER_FOUND_DONE)
					createFillTempAtndOverviewFromPreregSub(crawlerDelegate, context);
				createTables(context);
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

	private static void scrapStudentAndSubjectInfo(CrawlerDelegate crawlerDelegate, Context context)
			throws Exception {
		userName = crawlerDelegate.getStudentName();
		subjectInfos = crawlerDelegate.getSubjectInfoMain();
	}

	private static void deleteOldDatabase(Context context) {
		context.deleteDatabase(DbHelper.DB_NAME);
	}

	// It loads subjectInfos table, create nd load attendenceOverviewTable
	// and create attendence table for each subject
	private static void createTables(Context context) throws Exception {

		for (SubjectInfo row : subjectInfos) {
			new DetailedAttendenceTable(row.getSubjectCode(),
					row.isNotLab(),context).createTable();
		}
		DSSPData.createTable(context);
	}

	public static void createFillTempAtndOverviewFromPreregSub(CrawlerDelegate crawlerDelegate, Context context) {
		///M.log(TAG, "createFillTempAtndOverviewFromPreregSub");

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

    private static void createPreferences(Context context) {
		///M.log(TAG, "creating database preferences");
		Editor editor = context
				.getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
		editor.putBoolean(HAS_DATABASE_CREATED, true);
		editor.putString(MainPrefs.USER_NAME, userName);
		editor.commit();

	}

}
