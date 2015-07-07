package com.blackMonster.webkiosk.crawler.subjectDetails;

import com.blackMonster.webkiosk.crawler.BadHtmlSourceException;
import com.blackMonster.webkiosk.crawler.CrawlerUtils;
import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkiosk.crawler.SubjectLink;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by akshansh on 07/07/15.
 */
public class SubjectAndStudentDetailsMain extends AbstractSubjectDetails {

    String studentName;

    public SubjectAndStudentDetailsMain(SiteConnection cn) throws Exception {
        super(cn);
    }

    @Override
    void sendGet(String url) throws Exception {
        super.sendGet(url);
        setName();

    }

    @Override
    void readRow(List<SubjectLink> list) throws Exception {
        String tmp;
        SubjectLink sub = new SubjectLink();

        CrawlerUtils.readSingleData(connect.pattern1, reader);
        tmp = CrawlerUtils.readSingleData(connect.pattern1, reader);
        int i = CrawlerUtils.lastDash(tmp);
        sub.setName(CrawlerUtils.titleCase((tmp.substring(0, i - 1)).trim()));

        sub.setCode( "T" + (tmp.substring(i + 1)).trim());

        readLink_atnd(sub);

        list.add(sub);
    }

    @Override
    public List<SubjectLink> getSubjectURL() throws Exception {
        String tmp;
        List<SubjectLink> list = new ArrayList<SubjectLink>();

        CrawlerUtils.reachToData(reader, "<thead>");
        // connect.reachToData(reader, "Click on Subject to Sort");
        CrawlerUtils.reachToData(reader, "</thead>");
        CrawlerUtils.reachToData(reader, "<tbody>");
        // Log.d(TAG, "Reached to data");

        while (true) {
            tmp = reader.readLine();
            if (tmp == null)
                throw new BadHtmlSourceException();

            if (tmp.contains("</tbody>"))
                break;

            if (tmp.contains("<tr>")) {
                readRow(list);
            }
        }
        response.getEntity().consumeContent();
        return list;
    }


    void setName() throws BadHtmlSourceException, IOException {
        String str = extractString(CrawlerUtils.reachToData(reader, "Name:"));
        studentName = str.substring(0, str.indexOf('['));
    }

    @Override
    String getMainUrl(SiteConnection cn) {
        return cn.siteUrl + "/StudentFiles/Academic/StudentAttendanceList.jsp";
    }

    public String getStudentName() {
        return studentName;
    }


    private void readLink_atnd(SubjectLink sub) throws IOException,
            BadHtmlSourceException {

        String td = getTableData(reader);

        sub.setLink(getLink(td));

        sub.setOverall(getAtnd(td));

        sub.setLect(getAtnd(getTableData(reader)));
        sub.setTute(getAtnd(getTableData(reader)));

        td = getTableData(reader);

        if (sub.getLink()==null)		{
            sub.setLink(getLink(td));
            if (sub.getLink()!=null) sub.setLTP(0);
            else
                sub.setLTP(-1);

        }
        else
            sub.setLTP(1);

        sub.setPract(getAtnd(td));

    }

    private int getAtnd(String td) {
        if (td == null)
            return -1;
        int result;
        Matcher matcher;
        matcher = connect.pattern1.matcher(td);
        if (matcher.find()) {
            td = td.substring(matcher.start() + 1, matcher.end() - 1);
            if (td.contains("&nbsp;"))
                result = -1;
            else
                result = (int) Float.parseFloat(td);
        } else
            result = -1;

        return result;
    }

    private String getLink(String td) {
        Matcher matcher;

        matcher = pattern2.matcher(td);

        if (matcher.find()) {
            td = td.substring(matcher.start() + 6, matcher.end() - 1);
            return connect.siteUrl + "/StudentFiles/Academic/"
                    + td.replace("&amp;", "&");

        } else
            return null;

    }

    private String getTableData(BufferedReader reader)
            throws BadHtmlSourceException, IOException {
        String line;
        while (true) {
            String tmp = reader.readLine();

            if (tmp == null)
                throw new BadHtmlSourceException();

            if (tmp.contains("<td")) {
                line = tmp;
                break;
            }
            if (tmp.contains("</tr>")) {
                throw new BadHtmlSourceException();
            }

        }
        return line;
    }

    private String extractString(String tmp) throws BadHtmlSourceException {
        Matcher matcher = connect.pattern1.matcher(tmp);
        if (!matcher.find())
            throw new BadHtmlSourceException();
        if (!matcher.find())
            throw new BadHtmlSourceException();
        return tmp.substring(matcher.start() + 1, matcher.end() - 1);
    }



}
