package com.blackMonster.webkiosk.controller.model;

import com.blackMonster.webkiosk.controller.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.databases.model.MySubjectAttendance;

/**
 * Created by akshansh on 02/05/15.
 */
public class SingleClass {

    private ClassTime classTime;
    private MySubjectAttendance subAtnd;

    public SingleClass(ClassTime classTime, MySubjectAttendance subAtnd) {
        this.classTime = classTime;
        this.subAtnd = subAtnd;
    }

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

    public int isAtndModified() {
        return subAtnd.isModified();
    }


}
