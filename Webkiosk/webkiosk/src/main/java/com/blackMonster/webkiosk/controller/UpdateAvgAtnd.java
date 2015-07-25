package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;

import java.util.List;

public class UpdateAvgAtnd {
    public static final int ERROR = -100;

    public static int update(List<SubjectAttendance> newSubjectAttendances, Context context) throws SubjectChangedException {
        int numOfSubjectModified = 0;
        AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);

        try {
            if (atndO.isTableEmpty()) {
                doFirstRefresh(newSubjectAttendances, context);
                return numOfSubjectModified;
            }

            int isModified;
            for (SubjectAttendance newSubjectAttendance : newSubjectAttendances) {

                SubjectAttendance oldSubjectAttendance = atndO.getSubjectAttendance(newSubjectAttendance.getSubjectCode());
                if (oldSubjectAttendance == null) throw new SubjectChangedException();

                if (oldSubjectAttendance.getOverall() == newSubjectAttendance.getOverall() && oldSubjectAttendance.getLect() == newSubjectAttendance.getLect() && oldSubjectAttendance.getTute() == newSubjectAttendance.getTute() && oldSubjectAttendance.getPract() == newSubjectAttendance.getPract())
                    isModified = 0;
                else {
                    isModified = 1;
                    ++numOfSubjectModified;
                }
                atndO.update(toMySubAtnd(newSubjectAttendance, isModified));
            }
            return numOfSubjectModified;
        } finally {
            RefreshServicePrefs.setAvgAttendanceTimestamp(context);
        }
    }



    private static void doFirstRefresh(List<SubjectAttendance> newSubjectAttendances, Context context) {
        AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);
        for (SubjectAttendance newSubjectAttendance : newSubjectAttendances) {
            atndO.insert(toMySubAtnd(newSubjectAttendance,0));
        }
    }

    public static int update(CrawlerDelegate crawlerDelegate, Context context) throws SubjectChangedException {
        int result;
        try {
            List<SubjectAttendance> listt = crawlerDelegate.getSubjectAttendanceMain();
            result = update(listt, context);
        } catch (SubjectChangedException e) {
            throw e;
        } catch (Exception e) {
            result = ERROR;
            e.printStackTrace();
        }

        return result;
    }

    private static MySubjectAttendance toMySubAtnd(SubjectAttendance subAtnd, int isModified) {
        return new MySubjectAttendance(subAtnd.getName(), subAtnd.getSubjectCode(), subAtnd.getOverall(),
                subAtnd.getLect(), subAtnd.getTute(), subAtnd.getPract(), subAtnd.isNotLab(), isModified);
    }

}
