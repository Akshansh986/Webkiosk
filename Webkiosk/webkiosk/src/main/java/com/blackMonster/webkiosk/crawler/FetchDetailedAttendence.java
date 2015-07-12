package com.blackMonster.webkiosk.crawler;

import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class FetchDetailedAttendence {
    private static final String TAG = "LoadAttendence";
    private BufferedReader reader;
    private int LTP;
    HttpResponse response;

    FetchDetailedAttendence(HttpClient siteConnection, String url, int ltp)
            throws Exception {
        LTP = ltp;
        HttpGet httpget = new HttpGet(url);
        try {
            response = siteConnection.execute(httpget);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
        } catch (Exception e) {
            httpget.abort();
            throw e;
        }

        CrawlerUtils.reachToData(reader, "<tbody>");
    }

    //As this function is heavily modified you might find it little absurd.
    List<DetailedAttendance> getAttendance() throws IOException,
            BadHtmlSourceException {

        List<DetailedAttendance> detailedAttendanceList = new ArrayList<DetailedAttendance>();

        while (true) {
            int SNo = getSNo();
            if (SNo == 0) break;
            detailedAttendanceList.add(loadRow(SNo));
        }
        response.getEntity().consumeContent();
        return detailedAttendanceList;

    }

    private int getSNo() throws IOException, BadHtmlSourceException {
        String tmp;
        int num;
        while (true) {
            tmp = reader.readLine();
            if (tmp == null) throw new BadHtmlSourceException();

            if (tmp.contains("<tr>")) {
                tmp = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
                num = Integer.decode(tmp.substring(0, tmp.length() - 1));
                return num;
            } else if (tmp.contains("</tbody>")) return 0;

        }

    }


    private DetailedAttendance loadRow(int n) throws IOException,
            BadHtmlSourceException {

        DetailedAttendance atnd = new DetailedAttendance();
        atnd.SNo = n;
        atnd.date = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
        atnd.AttendenceBY = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);

        if (CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader).equals("Present"))
            atnd.status = 1;
        else
            atnd.status = 0;

        atnd.ClassType = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);

        if (LTP == 1) atnd.LTP = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
        return atnd;

    }

    void close() throws IOException {
        reader.close();
    }


}
