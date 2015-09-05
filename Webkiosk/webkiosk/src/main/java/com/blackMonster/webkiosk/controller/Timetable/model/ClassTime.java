package com.blackMonster.webkiosk.controller.Timetable.model;

public class ClassTime {
    private char classType;
    private String subCode;
    private String venue;
    private int time;
    private String faculty;
    private int day;

    public ClassTime(char classType, String subCode, String venue, int time, String faculty, int day) {
        this.classType = classType;
        this.subCode = subCode;
        this.venue = venue;
        this.time = time;
        this.faculty = faculty;
        this.day = day;
    }

    public char getClassType() {
        return classType;
    }

    public String getSubCode() {
        return subCode;
    }

    public String getVenue() {
        return venue;
    }

    public int getTime() {
        return time;
    }

    public String getFaculty() {
        return faculty;
    }

    public int getDay() {
        return day;
    }
}
