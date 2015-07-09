package com.blackMonster.webkiosk.crawler.Model;

/**
 * Created by akshansh on 09/07/15.
 */
public class CrawlerSubInfo extends SubjectInfo {

    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {

        if (link == null || link.equals(""))
            link = null;

        this.link = link;
    }
}
