package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.controller.Timetable.TimetableDelegate;
import com.blackMonster.webkiosk.controller.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.controller.model.SingleClass;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TempAtndOverviewTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshansh on 23/07/15.
 */
public class FullClassInfoHandler {

    /**
     * Gets full details of class on a particular day.
     * @param day
     * @param context
     * @return List of classes on given day
     */
    public static List<SingleClass> getAllClassOfDay(int day, Context context) {
        List<SingleClass> allClasses = new ArrayList<SingleClass>();

        List<ClassTime> classTimes = TimetableDelegate.getDayWiseTimetable(day, context);

        if (classTimes ==null) return allClasses;


        AttendenceOverviewTable atndOTable = new AttendenceOverviewTable(context);
        TempAtndOverviewTable tempAtndOTable = new TempAtndOverviewTable(context);  //Table created from "PreReg subjects" of webkiosk website.


        //Here we check if subject from timetable is present either in atndOTable or tempAtndOTable.
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
