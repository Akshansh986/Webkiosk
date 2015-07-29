package com.blackMonster.webkiosk.controller.Timetable;

import com.blackMonster.webkiosk.databases.Tables.TimetableTable;

/**
 * Created by akshansh on 22/07/15.
 */
public class TimetableUtils {
    public static String getFormattedTime(int time) {
        if (time < 12) {
            return time + " AM";

        }
        if (time == 12) {
            return time + " NOON";

        }
        return (time - 12) + " PM";

    }

    public static boolean isOfTwoHr(char classType, String subCode) {
        boolean result = false;
        if (classType == TimetableTable.ALIAS_PRACTICAL)
            result = true;
        else if (classType == TimetableTable.ALIAS_TUTORIAL
                && (subCode.equals("PD111") || subCode.equals("PD211"))) //These 2 subjects of 1st year are of two hours
            result = true;
        return result;
    }
}
