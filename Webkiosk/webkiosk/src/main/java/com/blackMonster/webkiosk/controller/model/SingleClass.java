package com.blackMonster.webkiosk.controller.model;

import com.blackMonster.webkiosk.controller.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;

/**
 * Java class having details of single class of a day.
 */
public class SingleClass {

    private ClassTime classTime;
    private MySubjectAttendance subAtnd;        //Attendance details of this class.

    public SingleClass(ClassTime classTime, MySubjectAttendance subAtnd) {
        this.classTime = classTime;
        this.subAtnd = subAtnd;
    }

    /**
     * @return "L","T" or "P" for lecture, tutorial and practical.
     */
    public char getClassType() {
        return classTime.getClassType();
    }

    public String getSubjectName() {
        return subAtnd.getName();
    }

    public String getVenue() {
        return classTime.getVenue();
    }

    public String getFaculty() {
        return classTime.getFaculty();
    }

    /**
     * @return Start time of class
     */
    public int getTime() {
        return classTime.getTime();
    }

    public Integer getOverallAttendence() {
        return subAtnd.getOverall();
    }

    public Integer getLectureAttendance() {
        return subAtnd.getLect();
    }

    public Integer getTuteAttendance() {
        return subAtnd.getTute();
    }
    public Integer getLabAttendance() {
        return subAtnd.getPract();
    }

    public String getSubjectCode() {
        return subAtnd.getSubjectCode();
    }

    /**
     * Is attendance modified in last refresh. (usually used to show recently updated tag)
     */
    public int isAtndModified() {
        return subAtnd.isModified();
    }


}
