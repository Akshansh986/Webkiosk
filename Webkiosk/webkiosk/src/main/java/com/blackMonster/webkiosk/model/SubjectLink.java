package com.blackMonster.webkiosk.model;

/**
* Created by akshansh on 19/04/15.
*/
public class SubjectLink {
    String name;
    String code;
    String link;
    int overall;
    int lect;
    int tute;
    int pract;
    int LTP;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getLink() {
        return link;
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

    public void setLink(String link) {
        this.link = link;
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
