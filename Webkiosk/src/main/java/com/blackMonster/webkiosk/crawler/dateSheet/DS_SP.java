package com.blackMonster.webkiosk.crawler.dateSheet;

import com.blackMonster.webkiosk.crawler.CrawlerUtils;

/**
 * Created by akshansh on 09/07/15.
 */
public class DS_SP {
    public String sheetCode;
    public String course;
    public String date;
    public String time;
    public String roomNo;
    public String seatNo;

    public DS_SP(String sheetCode2, String course2, String date2,
				 String time2, String roomNo2, String seatNo2) {

        sheetCode = sheetCode2;
        course = CrawlerUtils.titleCase(removeSubCode(course2));
        date = date2;
        time = time2;
        roomNo = roomNo2;
        seatNo = seatNo2;
    }

    private String removeSubCode(String str) {
        //return str.replaceAll(str.substring(str.indexOf('(') -1 , str.indexOf(')')  ), "").trim();
        return str.replaceAll("\\(\\S+\\)", "");
    }
}
