package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.refresher.SubjectChangedException;

import java.util.List;

public class UpdateAvgAtnd {
    public static final int ERROR = -100;

    public static int update(List<SubjectInfo> newSubjectInfos, Context context) throws SubjectChangedException {
        int numOfSubjectModified = 0;
        AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);

        try {
            if (atndO.isTableEmpty()) {
                doFirstRefresh(newSubjectInfos, context);
                return numOfSubjectModified;
            }

            int isModified;
            for (SubjectInfo newSubjectInfo : newSubjectInfos) {

                SubjectInfo oldSubjectInfo = atndO.getSubjectInfo(newSubjectInfo.getSubjectCode());
                if (oldSubjectInfo == null) throw new SubjectChangedException();

                if (oldSubjectInfo.getOverall() == newSubjectInfo.getOverall() && oldSubjectInfo.getLect() == newSubjectInfo.getLect() && oldSubjectInfo.getTute() == newSubjectInfo.getTute() && oldSubjectInfo.getPract() == newSubjectInfo.getPract())
                    isModified = 0;
                else {
                    isModified = 1;
                    ++numOfSubjectModified;
                }
                atndO.update(newSubjectInfo, isModified);
            }
            return numOfSubjectModified;
        } finally {
            RefreshServicePrefs.setAvgAttendanceTimestamp(context);
        }
    }

    private static void doFirstRefresh(List<SubjectInfo> newSubjectInfos, Context context) {
        AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);
        for (SubjectInfo newSubjectInfo : newSubjectInfos) {
            atndO.insert(newSubjectInfo, 0);
        }
    }

    public static int update(CrawlerDelegate crawlerDelegate, Context context) throws SubjectChangedException {
        int result;
        try {
            List<SubjectInfo> listt = crawlerDelegate.getSubjectInfoMain();
            result = update(listt, context);
        } catch (SubjectChangedException e) {
            throw e;
        } catch (Exception e) {
            result = ERROR;
            e.printStackTrace();
        }

        return result;
    }

}
