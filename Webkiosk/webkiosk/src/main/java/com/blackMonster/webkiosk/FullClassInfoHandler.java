package com.blackMonster.webkiosk;

import android.content.Context;

import com.blackMonster.webkiosk.Timetable.TimetableDelegate;
import com.blackMonster.webkiosk.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.Timetable.model.SingleClass;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshansh on 23/07/15.
 */
public class FullClassInfoHandler {

    public static List<SingleClass> getAllClassOfDay(int day, Context context) {
        List<SingleClass> allClasses = new ArrayList<SingleClass>();

        List<ClassTime> classTimes = TimetableDelegate.getDayWiseTimetable(day, context);

        if (classTimes ==null) return allClasses;


        AttendenceOverviewTable atndOTable = new AttendenceOverviewTable(context);
        TempAtndOverviewTable tempAtndOTable = new TempAtndOverviewTable(context);

        for (ClassTime classTime : classTimes) {

            MySubjectAttendance subAtnd = atndOTable.getSubjectAttendance(classTime.getSubCode());
            if (subAtnd == null) {
                subAtnd = tempAtndOTable.getSubjectAttendance(classTime.getSubCode());
                if (subAtnd == null) continue;
            }

            allClasses.add(new SingleClass(classTime,subAtnd));
        }

        return allClasses;
    }

}
