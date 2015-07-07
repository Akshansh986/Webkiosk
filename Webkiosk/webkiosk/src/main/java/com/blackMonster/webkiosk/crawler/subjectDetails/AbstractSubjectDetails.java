package com.blackMonster.webkiosk.crawler.subjectDetails;

import com.blackMonster.webkiosk.crawler.BadHtmlSourceException;
import com.blackMonster.webkiosk.crawler.CrawlerUtils;
import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkiosk.crawler.SubjectLink;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractSubjectDetails {
	static final String TAG = "StudentDetails";
	String MAIN_URL;
	SiteConnection connect;
	BufferedReader reader;
	Pattern pattern2 = Pattern.compile("href=([^>]+)");
	HttpResponse response;

	public AbstractSubjectDetails(SiteConnection cn) throws Exception {
		connect = cn;
		MAIN_URL = getMainUrl(cn);
		sendGet(MAIN_URL);
		String url = getLatestAtnd();
		if (url != null) {
			close();
			sendGet(url);
		}
	}

	abstract String getMainUrl(SiteConnection cn);

    abstract void readRow(List<SubjectLink> list) throws Exception;

    public abstract List<SubjectLink> getSubjectURL() throws Exception ;


    void sendGet(String url) throws Exception {
		// /Log.d(TAG, "sendget");
		HttpGet httpget = new HttpGet(url);
		try {
			response = connect.httpclient.execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (Exception e) {
			httpget.abort();
			throw e;
		}
		//connect.reachToData(reader, "Student Attendance");

	}

	private String getLatestAtnd() throws BadHtmlSourceException, IOException {
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





	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

}
