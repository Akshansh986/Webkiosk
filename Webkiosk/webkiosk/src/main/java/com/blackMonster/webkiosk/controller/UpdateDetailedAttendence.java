package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
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

        List<SubjectInfo> subjectInfoList = new AttendenceOverviewTable(context).getAllSubjectInfo();

        for (SubjectInfo subjectInfo : subjectInfoList) {
            List<DetailedAttendance> detailedAttendanceList = crawlerDelegate.getDetailedAttendance(subjectInfo.getSubjectCode());
            if (detailedAttendanceList != null) {
                fillSingleTable(subjectInfo.getSubjectCode(), detailedAttendanceList, subjectInfo.isNotLab(), context);
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
