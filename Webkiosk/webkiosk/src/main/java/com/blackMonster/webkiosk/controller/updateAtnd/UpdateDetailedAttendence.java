package com.blackMonster.webkiosk.controller.updateAtnd;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendenceTable;

import java.util.List;

public class UpdateDetailedAttendence {
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

        List<MySubjectAttendance> subjectAttendanceList = new AttendenceOverviewTable(context).getAllSubjectAttendance();

        for (SubjectAttendance subjectAttendance : subjectAttendanceList) {
            List<DetailedAttendance> detailedAttendanceList = crawlerDelegate.getDetailedAttendance(subjectAttendance.getSubjectCode());
            if (detailedAttendanceList != null) {
                fillSingleTable(subjectAttendance.getSubjectCode(), detailedAttendanceList, subjectAttendance.isNotLab(), context);
//                Log.d(TAG,subjectInfo.getName() + "  notLab " + subjectInfo.isNotLab());
            }

        }

    }

    private static void fillSingleTable(String subCode, List<DetailedAttendance> detailedAttendanceList, int isNotLab, Context context) throws Exception {
        DetailedAttendenceTable detailedAttendenceTable = new DetailedAttendenceTable(subCode, isNotLab, context);

        detailedAttendenceTable.deleteAllRows();
        detailedAttendenceTable.insert(detailedAttendanceList);
    }

    private static void createPreferences(Context context) {
        RefreshServicePrefs.setDetailedAtndTimestamp(context);
        RefreshServicePrefs.setPasswordUptoDate(context);  //TODO check it
    }

}
