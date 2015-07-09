package com.blackMonster.webkiosk.crawler.dateSheet;

import android.content.Context;
import android.net.Uri;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.BadHtmlSourceException;
import com.blackMonster.webkiosk.crawler.CrawlerUtils;
import com.blackMonster.webkiosk.crawler.WebkioskWebsite;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FetchDateSheet {

    private static final int MAX_X = 4;
    private static final int MAX_Y = 20;
    private static HttpClient siteConnection;
    private static Context context;

    static List<DateSheetRow> getData(HttpClient cn, String colg, Context context) throws Exception {
        M.log("fetchdatesheet", "getdata");
        siteConnection = cn;
        FetchDateSheet.context = context;
        List<DateSheetRow> ds = new ArrayList<DateSheetRow>();
        getDateSheet(ds, colg);
        //for (DateSheetRow a : ds) {
        //	M.log("datesheet", "course " + a.course +" date  " + a.date + " sheet code" + a.sheetCode + " time " + a.time);
        //}
        return ds;

    }

    private static void getDateSheet(List<DateSheetRow> dsList, String colg) throws Exception {

        //String initialURL = "https://dl.dropboxusercontent.com/u/95984737/ds.htm";
        String initialURL = WebkioskWebsite.getSiteUrl(colg)
                + "/StudentFiles/Exam/StudViewDateSheet.jsp";
        BufferedReader reader = sendGet(initialURL);
        List<String> codeList = new ArrayList<String>();
        getDSCodes(reader, codeList);
        reader.close();

        for (String sheetCode : codeList) {
            reader = sendGet(getUrl(sheetCode, colg, context));
            extractDSdata(reader, dsList, sheetCode);
            reader.close();
        }
    }

    private static void extractDSdata(BufferedReader reader,
                                      List<DateSheetRow> dsList, String sheetCode) throws Exception {
        CrawlerUtils.reachToData(reader, "submit");
        CrawlerUtils.reachToData(reader, "<table");
        CrawlerUtils.reachToData(reader, "</tr>");

        String[][] tableData = ExtractTable.extractTable(reader, MAX_X, MAX_Y);

        copyData(dsList, tableData, sheetCode);


    }

    private static void copyData(List<DateSheetRow> dsList, String[][] tableData,
                                 String sheetCode) {
        for (int i = 0; i < tableData.length; ++i) {
            DateSheetRow ds = new DateSheetRow();
            ds.date = tableData[i][1];
            ds.time = tableData[i][2];
            ds.course = tableData[i][3];
            ds.sheetCode = sheetCode;
            if (ds.isEmpty())
                return;
            dsList.add(ds);
        }

    }

    private static String getUrl(String code, String colg, Context context) {
        String url = WebkioskWebsite.getSiteUrl(colg)
                + "/StudentFiles/Exam/StudViewDateSheet.jsp?"
                + "x=&SrcType=&Inst=" + MainPrefs.getColg(context) + "&DScode="
                + Uri.encode(code) + "&Subject=ALL";
        M.log("tt", url);
        //return "https://dl.dropboxusercontent.com/u/95984737/ds.htm";
        return url;
    }

    private static BufferedReader sendGet(String url) {

        BufferedReader reader = null;
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse response = siteConnection.execute(httpget);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
        } catch (Exception e) {
            httpget.abort();
        }

        return reader;

    }


    private static void getDSCodes(BufferedReader reader, List<String> codeList)
            throws BadHtmlSourceException, IOException {
        String tmp;
        CrawlerUtils.reachToData(reader, "id=\"DScode\"");

        while (true) {
            tmp = reader.readLine();
            if (tmp == null)
                throw new BadHtmlSourceException();

            if (tmp.contains("</select>"))
                return;
            if (tmp.toUpperCase().contains("<OPTION")) {
                String optn = getOptionValue(tmp);
                if (optn == null)
                    return;
                else
                    codeList.add(optn);
            }

        }
    }

    private static String getOptionValue(String str) throws BadHtmlSourceException {
        Pattern pattern = Pattern.compile("value=([^>]+)>");
        if (str == null)
            return null;
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find())
            throw new BadHtmlSourceException();
        // M.log(TAG, "option " + str.substring(matcher.start() + 1,
        // matcher.end() - 1).trim());
        str = str.substring(matcher.start() + 6, matcher.end() - 1).trim();
        return str.replaceAll("\"", "");
    }

    static class DateSheetRow {
        String sheetCode;
        String date;
        String time;
        String course;

        boolean isEmpty() {
            if (date.equals("") && time.equals("") && course.equals(""))
                return true;
            else
                return false;
        }
    }

}
