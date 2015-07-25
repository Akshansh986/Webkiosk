package com.blackMonster.webkiosk.Timetable;

import android.content.Context;

import com.blackMonster.webkiosk.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;

import java.util.List;

/**
 * Created by akshansh on 23/07/15.
 */
class AddNewClass {

    static boolean addNewClass(ClassTime classTime,
                                      Context context) {
        boolean result;
        if (isCellEmpty(classTime, context)) {
            TimetableTable.insertRawData(classTime.getDay(), classTime.getTime(),
                    createRawData(classTime),
                    context);

            result = true;
        } else
            result = false;

        return result;
    }

    private static boolean isCellEmpty(ClassTime classTime, Context context) {

        String rawData;

        if (classTime.getTime() > TimetableTable.CLASS_START_TIME) {
            rawData = getMyClass(classTime.getDay(), classTime.getTime() - 1, context);
            if (rawData != null && isOfTwoHr(rawData))
                return false;
        }

        rawData = getMyClass(classTime.getDay(), classTime.getTime(), context);

        if (rawData != null) return false;

        boolean isNewOfTwoHr = TimetableUtils.isOfTwoHr(classTime.getClassType(), classTime.getSubCode());
        if (isNewOfTwoHr) {
            if (classTime.getTime() + 1 > TimetableTable.CLASS_END_TIME) return false;
            rawData = getMyClass(classTime.getDay(), classTime.getTime() + 1, context);
            if (rawData != null)
                return false;
        }

        return true;
    }

    private static String createRawData(ClassTime classTime) {
        return classTime.getClassType() + "-" + classTime.getSubCode() +
                "-" + classTime.getVenue() + "-" + classTime.getFaculty();
    }

    private static String getMyClass(String rawData, Context context) {
        if (rawData == null || rawData.equals("")) {
            return null;
        }
        List<MySubjectAttendance> subCodeList = new AttendenceOverviewTable(context)
                .getAllSubjectAttendance();

        String singleRaw[];
        if (rawData.contains("#")) {
            singleRaw = rawData.split("#");

            for (int p = 0; p < singleRaw.length; ++p) {
                String subCode = singleRaw[p].split("-")[1];
                for (SubjectAttendance listItem : subCodeList)
                    if (listItem.getSubjectCode().contains(subCode))
                        return singleRaw[p];
            }
        } else {

            String subCode = rawData.split("-")[1];

            for (SubjectAttendance listItem : subCodeList)
                if (listItem.getSubjectCode().contains(subCode))
                    return rawData;
        }
        return null;
    }

    private static String getMyClass(int day, int time, Context context) {
        return getMyClass(TimetableTable.getRawData(day, time, context), context);
    }

    private static boolean isOfTwoHr(String rawData) {
        char classType = rawData.split("-")[0].charAt(0);
        String subCode = rawData.split("-")[1];
        return TimetableUtils.isOfTwoHr(classType, subCode);
    }
}
