package com.blackMonster.webkiosk.databases.model;

import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;

/**
 * Created by akshansh on 24/07/15.
 */
public class MySubjectAttendance extends SubjectAttendance{
    private int isModified;

    public MySubjectAttendance(String name, String code, int overall, int lect, int tute, int pract, int notLab, int isModified) {

        setName(name);
        setSubCode(code);
        setOverall(overall);
        setLect(lect);
        setTute(tute);
        setPract(pract);
        setNotLab(notLab);
        this.isModified = isModified;
    }

    public void setIsModified(int isModified) {
        this.isModified = isModified;
    }

    public int isModified() {

        return isModified;
    }
}
