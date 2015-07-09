package com.blackMonster.webkiosk.crawler.Model;

/**
* Created by akshansh on 19/04/15.
*/
public class SubjectInfo {
    private String name;
    private String code;
    private int overall, lect, tute, pract, LTP;

    public String getName() {
        return name;
    }


    /**
     * SubjectCode in whole app have "T" concatenated. ex "T10B11EC211"
     * As Sqlite tables(detailed attendance tables) can't have name staring from integer, it was done.
     * Same subject code was then used in whole app context, crawler itself returns with "T" concatenated.
     *  @return String
     */

    public String getSubjectCode() {
        return code;
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

    public int getLTP() {
        return LTP;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
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

    public void setLTP(int LTP) {
        this.LTP = LTP;
    }
}
