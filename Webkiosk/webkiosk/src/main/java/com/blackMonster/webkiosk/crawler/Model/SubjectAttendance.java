package com.blackMonster.webkiosk.crawler.Model;

/**
* Created by akshansh on 19/04/15.
*/
public class SubjectAttendance {
    private String name;
    private String subCode;
    private int overall, lect, tute, pract, notLab;


    public String getName() {
        return name;
    }


    /**
     * SubjectCode in whole app have "T" concatenated. ex "T10B11EC211"
     * As Sqlite tables(detailed attendance tables) can't have name staring from integer, it was done.
     * Same subject subCode was then used in whole app context, crawler itself returns with "T" concatenated.
     *  @return String
     */

    public String getSubjectCode() {
        return subCode;
    }


    public int getOverall() {
        return overall;
    }

    public int getLect() {
        return lect;
    }

    public int getTute() {
        return tute;
    }

    public int getPract() {
        return pract;
    }

    public int isNotLab() {
        return notLab;
    }

    public void setName(String name) {

//        if (name.toUpperCase().equals("COMPUTER NETWORKS"))
//            name = "COMPUTER sss";
        this.name = name;
    }

    public void setSubCode(String subCode) {
//        if (subCode.toUpperCase().equals("T10B11CI611"))
//            subCode = "T10B11CI711";
        this.subCode = subCode;
    }



    public void setOverall(int overall) {
        this.overall = overall;
    }

    public void setLect(int lect) {
        this.lect = lect;
    }

    public void setTute(int tute) {
        this.tute = tute;
    }

    public void setPract(int pract) {
        this.pract = pract;
    }

    public void setNotLab(int notLab) {
        this.notLab = notLab;
    }
}
