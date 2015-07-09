package com.blackMonster.webkiosk.refresher;

import android.content.Context;
import android.content.SharedPreferences;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.Attendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendenceTable;

import java.util.List;

public class UpdateAttendence {
    static final String TAG = "UpdateAttendence";
    public static final int DONE = 1;
    public static final int ERROR = -1;


    // return ERROR or no. of new data added;
    public static int start(CrawlerDelegate crawlerDelegate, Context context) {
        int result;

        try {
            fillAllAttendenceTable(crawlerDelegate, context);
            createPreferences(context);
            result = DONE;
        } catch (Exception e) {
            result = ERROR;
            e.printStackTrace();
        }
        return result;
    }

    private static void fillAllAttendenceTable(CrawlerDelegate crawlerDelegate, Context context)
            throws Exception {

        List<SubjectInfo> subjectInfoList = new AttendenceOverviewTable(context).getAllSubjectInfo();

        for (SubjectInfo subjectInfo : subjectInfoList) {
            List<Attendance> attendanceList = crawlerDelegate.getDetailedAttendance(subjectInfo.getSubjectCode());
            if (attendanceList != null) {
                int ltp;
                if (subjectInfo.getName().toLowerCase().contains("lab"))
                    ltp = 0;    //TODO complete jugad.
                else ltp = 1;

                fillSingleTable(subjectInfo.getSubjectCode(), attendanceList, ltp, context);

            }

        }

    }

    private static void fillSingleTable(String subCode, List<Attendance> attendanceList, int ltp, Context context) throws Exception {


        DetailedAttendenceTable detailedAttendence = new DetailedAttendenceTable(subCode, ltp, context);

        detailedAttendence.openWritebleDb();
        detailedAttendence.deleteAllRows();

        for (Attendance atnd : attendanceList) {

            detailedAttendence.insert(atnd.date, atnd.AttendenceBY,
                    atnd.status, atnd.ClassType, atnd.LTP);

        }
        detailedAttendence.closeWritebleDb();

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
