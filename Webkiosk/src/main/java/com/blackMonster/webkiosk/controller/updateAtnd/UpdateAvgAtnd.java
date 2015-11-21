package com.blackMonster.webkiosk.controller.updateAtnd;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;

import java.util.List;

public class UpdateAvgAtnd {
    public static final int ERROR = -100;

    /**
     * Updates Avg attendance to local db.
     * @param newSubjectAttendances
     * @param context
     * @return Number of subject whose attendance is modified
     * @throws SubjectChangedException
     */
    public static int update(List<SubjectAttendance> newSubjectAttendances, Context context) throws SubjectChangedException {
        int numOfSubjectModified = 0;
        AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);

        try {
            if (atndO.isTableEmpty()) {
                doFirstRefresh(newSubjectAttendances, context); //update is run for first time.
                return numOfSubjectModified;
            }

            int isModified;                 //tracks if attendance of a subject is modified or not.
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
            RefreshDBPrefs.setAvgAttendanceRefreshTimestamp(context);
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

    //convrets SubjectAttendance to MySubjectAttendance.
    private static MySubjectAttendance toMySubAtnd(SubjectAttendance subAtnd, int isModified) {
        return new MySubjectAttendance(subAtnd.getName(), subAtnd.getSubjectCode(), subAtnd.getOverall(),
                subAtnd.getLect(), subAtnd.getTute(), subAtnd.getPract(), subAtnd.isNotLab(), isModified);
    }

}
