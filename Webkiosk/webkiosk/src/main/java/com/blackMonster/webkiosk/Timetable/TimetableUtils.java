package com.blackMonster.webkiosk.Timetable;

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
        if (classType == TimetableData.ALIAS_PRACTICAL)
            result = true;
        else if (classType == TimetableData.ALIAS_TUTORIAL
                && (subCode.equals("PD111") || subCode.equals("PD211"))) //These 2 subjects of 1st year are of two hours
            result = true;
        return result;
    }
}
