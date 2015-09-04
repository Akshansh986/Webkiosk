package com.blackMonster.webkiosk.controller.updateAtnd;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.DetailedAttendanceTable;

import java.util.List;

public class UpdateDetailedAttendance {
    static final String TAG = "UpdateAttendence";
    public static final int DONE = 1;
    public static final int ERROR = -1;



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

    //updates detailed attendance of all subjects
    private static void fillAllAttendenceTable(CrawlerDelegate crawlerDelegate, Context context)
            throws Exception {

        List<MySubjectAttendance> subjectAttendanceList = new AttendenceOverviewTable(context).getAllSubjectAttendance();

        for (SubjectAttendance subjectAttendance : subjectAttendanceList) {
            List<DetailedAttendance> detailedAttendanceList = crawlerDelegate.getDetailedAttendance(subjectAttendance.getSubjectCode());
            if (detailedAttendanceList != null) {
                fillSingleTable(subjectAttendance.getSubjectCode(), detailedAttendanceList, subjectAttendance.isNotLab(), context);
            }

        }

    }

    //updates detailed attendance of single subject.
    private static void fillSingleTable(String subCode, List<DetailedAttendance> detailedAttendanceList, int isNotLab, Context context) throws Exception {
        DetailedAttendanceTable detailedAttendanceTable = new DetailedAttendanceTable(subCode, isNotLab, context);

        detailedAttendanceTable.deleteAllRows();
        detailedAttendanceTable.insert(detailedAttendanceList);
    }

    private static void createPreferences(Context context) {
        RefreshDBPrefs.setDetailedAtndRefreshTimestamp(context);
        RefreshDBPrefs.setPasswordUptoDate(context);  //TODO check it
    }

}
