package com.blackMonster.webkiosk.crawler;

import com.blackMonster.webkiosk.crawler.Model.CrawlerSubjectInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


abstract class AbstractSubjectDetails {
	private static final String TAG = "StudentDetails";
	private String MAIN_URL;
	private HttpClient siteConnection;
	private List<CrawlerSubjectInfo> crawlerSubInfoList = null;

	BufferedReader reader;
	Pattern pattern2 = Pattern.compile("href=([^>]+)");
	HttpResponse response;
	String colg;


	AbstractSubjectDetails(HttpClient siteConnection, String colg) throws Exception {
		this.siteConnection = siteConnection;
		this.colg = colg;
		MAIN_URL = getMainUrl();
		sendGet(MAIN_URL);
		String url = getLatestSem();
		if (url != null) {
			close();
			sendGet(url);
		}
	}

	abstract String getMainUrl();

    abstract void readRow(List<CrawlerSubjectInfo> list) throws Exception;

    abstract List<CrawlerSubjectInfo> fetchSubjectInfo() throws Exception ;



	List<CrawlerSubjectInfo> getSubjectInfo() throws  Exception {

		if (crawlerSubInfoList == null) {
			crawlerSubInfoList = fetchSubjectInfo();
		}

		return crawlerSubInfoList;
	}


    void sendGet(String url) throws Exception {
		// /Log.d(TAG, "sendget");
		HttpGet httpget = new HttpGet(url);
		try {
			response = siteConnection.execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (Exception e) {
			httpget.abort();
			throw e;
		}
		//siteConnection.reachToData(reader, "Student Attendance");

	}

	private String getLatestSem() throws BadHtmlSourceException, IOException {
		String str;
		CrawlerUtils.reachToData(reader, "Exam Code");
		CrawlerUtils.reachToData(reader, "<select");
		String selected = null;
		String tmp;
		String latestOption = null;
		String strLowerCase;
		while (true) {
			str = reader.readLine();
			if (str == null)
				throw new BadHtmlSourceException();
			strLowerCase = str.toLowerCase();
			if (strLowerCase.contains("</select>"))
				break;

			if (strLowerCase.contains("<option")) {
				tmp = getOptionValue(str);
				latestOption = getLatest(tmp, latestOption);
				if (isSelectedOption(str))
					selected = tmp;
			}

		}
		if (selected != null && latestOption != null) {
			if (selected.equals(latestOption))
				return null;
			else
				return MAIN_URL + "?x=&exam=" + latestOption;
		}
		return null;
	}

	private String getLatest(String sa, String sb) {
		if (sa == null && sb == null)
			return null;
		if (sa == null)
			return sb;
		if (sb == null)
			return sa;
		String result;
		sa = sa.toUpperCase();
		sb = sb.toUpperCase();
		int yearA = getYear(sa);
		int yearB = getYear(sb);
		if (yearA > yearB)
			result = sa;
		else if (yearA < yearB)
			result = sb;
		else
			result = getLatestUsingSem(sa, sb);
		// Log.d(TAG, "latestsem : " + result);
		return result;
	}

	private String getLatestUsingSem(String sa, String sb) {

		if (sa.contains("ODD"))
			return sa;
		if (sb.contains("ODD"))
			return sb;
		if (sa.contains("SUMMER"))
			return sa;
		if (sb.contains("SUMMER"))
			return sb;
		if (sa.contains("EVE"))
			return sa;
		if (sb.contains("EVE"))
			return sb;

		return null;

	}

	private int getYear(String sa) {
		int len = sa.length();
		String year = "";
		boolean flag=false;
		for (int i = 0; i < len; ++i) {
			if (Character.isDigit(sa.charAt(i))) {
				year = year + sa.charAt(i);
				flag = true;
			}
			else if (flag==true)
				break;
			
		}
			
		// Log.d(TAG, "year  " + year);
		return Integer.parseInt(year);
	}

	private boolean isSelectedOption(String str) {
		return str.toUpperCase().contains("Selected Value".toUpperCase());
	}

	private String getOptionValue(String str) throws BadHtmlSourceException {
		Pattern pattern = Pattern.compile("=([^>]+)>");
		if (str == null)
			return null;
		Matcher matcher = pattern.matcher(str);
		if (!matcher.find())
			throw new BadHtmlSourceException();
		// Log.d(TAG, "option " + str.substring(matcher.start() + 1,
		// matcher.end() - 1).trim());
		return str.substring(matcher.start() + 1, matcher.end() - 1).trim();
	}





	void close() throws IOException {
		if (reader != null)
			reader.close();
	}

}
