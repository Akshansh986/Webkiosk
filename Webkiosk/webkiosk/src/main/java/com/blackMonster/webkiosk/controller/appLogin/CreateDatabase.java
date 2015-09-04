package com.blackMonster.webkiosk.controller.appLogin;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendanceTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;

import java.util.List;

public class CreateDatabase {
	static final String TAG = "createDatabase";
	public static final int DONE = 1;
	public static final int ERROR = -52;

	private static String userName = null;				//Name of student.
	private static List<SubjectAttendance> subjectAttendances = null;

	public static int start(String colg, String enroll, String batch,
							CrawlerDelegate crawlerDelegate, Context context) {
		deleteOldDatabase(context);

		int result;
		try {
			scrapStudentAndSubjectInfo(crawlerDelegate, context);	//Get info from crawler.
			result = handleTimetable(colg, enroll, batch, context);

			if ( ! TimetableCreateRefresh.isError(result)) {
				initDatabase(context);
				if (result == TimetableCreateRefresh.TRANSFER_FOUND_DONE)		//U have to read timetable wiki to understand it.
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
		DbHelper.getInstance(context);		//It's important.
	}

	private static int handleTimetable(String colg, String enroll,
			String batch, Context context) {
		return TimetableCreateRefresh.createDatabase(subjectAttendances, colg, enroll, batch,
				context);
	}

	private static void scrapStudentAndSubjectInfo(CrawlerDelegate crawlerDelegate, Context context)
			throws Exception {
		userName = crawlerDelegate.getStudentName();
		subjectAttendances = crawlerDelegate.getSubjectAttendanceMain();
	}

	private static void deleteOldDatabase(Context context) {
		context.deleteDatabase(DbHelper.DB_NAME);
	}

	//Create detailed attendance table for each subject.
	private static void createTables(Context context) throws Exception {

		for (SubjectAttendance row : subjectAttendances) {
			new DetailedAttendanceTable(row.getSubjectCode(),
					row.isNotLab(),context).createTable();
		}
	}

	/*Every time "MY attendance" menu in Webkiosk website is updated 3-4 days after college starts.
	Meanwhile old sem data is present there.
	So to provide latest timetable at the very fist day of college, subject detail is fetched from "Pre reg"
	page and store in TempAtndOverview table.
	*/
	public static void createFillTempAtndOverviewFromPreregSub(CrawlerDelegate crawlerDelegate, Context context) {
		List<SubjectAttendance> preSubjectAttendance = getSubInfoFromPrereg(crawlerDelegate, context);
		List<SubjectAttendance> regSubjectAttendance = getSubInfoFromReg(crawlerDelegate, context);
		List<SubjectAttendance> subjectAttendance = combineSubInfo(preSubjectAttendance, regSubjectAttendance);
		
		if (subjectAttendance == null)
			return;
		TempAtndOverviewTable tempAtndOTable =new TempAtndOverviewTable(context);
		tempAtndOTable.dropTableifExist();
		tempAtndOTable.createTable(DbHelper.getInstance(context)
				.getWritableDatabase());
		for (SubjectAttendance row : subjectAttendance) {

			MySubjectAttendance mySubAtnd =	new MySubjectAttendance(row.getName(),row.getSubjectCode(),row.getOverall(),
					row.getLect(),row.getTute(),row.getPract(),row.isNotLab(),0);
			tempAtndOTable.insert(mySubAtnd);
		}
	}

	private static List<SubjectAttendance> combineSubInfo(List<SubjectAttendance> preSubjectLink,
													List<SubjectAttendance> regSubjectLink) {
		
		if (preSubjectLink==null && regSubjectLink==null) return null;
		if (preSubjectLink==null && regSubjectLink!=null) return regSubjectLink;
		if (preSubjectLink!=null && regSubjectLink==null) return preSubjectLink;
		
		for (SubjectAttendance x : preSubjectLink) {
			if (! regSubjectLink.contains(x)) regSubjectLink.add(x);
		}
		
		return regSubjectLink;
	}

	private static List<SubjectAttendance> getSubInfoFromReg(CrawlerDelegate crawlerDelegate, Context context) {

		try {
			return crawlerDelegate.getSubjectAttendanceFromSubRegistered();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<SubjectAttendance> getSubInfoFromPrereg(CrawlerDelegate crawlerDelegate, Context context) {
		try {
			return crawlerDelegate.getSubjectAttendanceFromPreReg();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    private static void createPreferences(Context context) {
		MainPrefs.setUserName(userName,context);
	}

}
