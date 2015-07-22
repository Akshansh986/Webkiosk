package com.blackMonster.webkiosk.Timetable;

import android.database.Cursor;

import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;

/**
 * Created by akshansh on 02/05/15.
 */
public class SingleClass {
    private char classType;
    private String subjectName;
    private String subCode;
    private String venue;
    private int time;
    private String faculty;
    private Integer oAtnd;
    private Integer specificAtnd;
    private boolean subjectFound;

    public int isModified;

    public SingleClass(char cType, String subCode, String venue,
					   String faculty, int timeIndex, Cursor cursor,
					   Cursor tempAtndOCursor) {
        subjectFound = true;
        classType = cType;
        this.subCode = subCode;
        this.venue = venue;
        this.faculty = faculty;
        time = timeIndex + TimetableData.CLASS_START_TIME - 1;
        setFieldFromAtndOverviewOrTempAO(cursor, tempAtndOCursor, subCode);
    }

    private void setFieldFromAtndOverviewOrTempAO(Cursor cursor,
												  Cursor tempAtndOCursor, String subCode) {
        if (!setFeild(cursor, subCode))
            if (!setFeild(tempAtndOCursor, subCode)) {
                subjectName = subCode;
                oAtnd = null;
                specificAtnd = null;
                isModified = 0;
                subjectFound = false;
            }

    }

    private boolean setFeild(Cursor cursor, String subCode2) {
        String tmp;
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                if (cursor.isNull(cursor
                        .getColumnIndex(AttendenceOverviewTable.C_CODE)))
                    continue;
                tmp = cursor.getString(cursor
                        .getColumnIndex(AttendenceOverviewTable.C_CODE));
                if (tmp.contains(subCode.toUpperCase())) {
                    subjectName = cursor
                            .getString(cursor
                                    .getColumnIndex(AttendenceOverviewTable.C_NAME));
                    isModified = cursor
                            .getInt(cursor
                                    .getColumnIndex(AttendenceOverviewTable.C_IS_MODIFIED));
                    setAttendence(cursor);
                    return true;
                }
            } while (cursor.moveToNext());
        }

        return false;
    }

    private void setAttendence(Cursor cursor) {
        Long tmp;
        Integer columnIndex;
        switch (classType) {
			case TimetableData.ALIAS_LECTURE:
				columnIndex = cursor
						.getColumnIndex(AttendenceOverviewTable.C_LECTURE);
				break;
			case TimetableData.ALIAS_TUTORIAL:
				columnIndex = cursor
						.getColumnIndex(AttendenceOverviewTable.C_TUTORIAL);
				break;

			case TimetableData.ALIAS_PRACTICAL:
				columnIndex = cursor
						.getColumnIndex(AttendenceOverviewTable.C_PRACTICAL);
				break;
			default:
				oAtnd = null;
				specificAtnd = null;
				return;

        }

        tmp = cursor.getLong(columnIndex);
        if (tmp == -1)
            specificAtnd = null;
        else
            specificAtnd = tmp.intValue();

        tmp = cursor.getLong(cursor
                .getColumnIndex(AttendenceOverviewTable.C_OVERALL));
        if (tmp == -1)
            oAtnd = null;
        else
            oAtnd = tmp.intValue();

        if (classType == TimetableData.ALIAS_PRACTICAL)
            oAtnd = specificAtnd;

    }

    public char getClassType() {
        return Character.toUpperCase(classType);
    }

    public String getSubjectName() {
        return subjectName;

    }

    public String getVenue() {
        return venue.toUpperCase();
    }

    public String getFaculty() {
        return faculty.toUpperCase();
    }

    public int getTime() {
        return time;
    }

    public Integer getOverallAttendence() {
        return oAtnd;
    }

    public Integer getSpecificAttendence() {
        return specificAtnd;
    }

    public String getSubjectCode() {
        return subCode;
    }

    public boolean isSubjectFound() {
        return subjectFound;
    }

}
