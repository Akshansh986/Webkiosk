package com.blackMonster.webkiosk.crawler;

import com.blackMonster.webkiosk.BadHtmlSourceException;
import com.blackMonster.webkiosk.SiteConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

// getName(), getCurrentSem() and getSubjectURL() must be called one after other if all 3 has to be called. OR
// if only one func is to be called no sequence required.
// Remember to call close() after wrk is done;
public class StudentDetails {
	static final String TAG = "StudentDetails";
	String MAIN_URL;
	SiteConnection connect;
	BufferedReader reader;
	Pattern pattern2 = Pattern.compile("href=([^>]+)");
	HttpResponse response;
	String userName;

	public StudentDetails(SiteConnection cn) throws Exception {
		connect = cn;
		MAIN_URL = getMainUrl(cn);
		sendGet(MAIN_URL);
		String url = getLatestAtnd();
		if (url != null) {
			// Log.d(TAG, "url is null");
			close();
			sendGet(url);
		}
	}

	public String getMainUrl(SiteConnection cn) {
		return cn.siteUrl + "/StudentFiles/Academic/StudentAttendanceList.jsp";
	}

	private void sendGet(String url) throws Exception {
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
		setName();

	}

	private String getLatestAtnd() throws BadHtmlSourceException, IOException {
		String str;
		connect.reachToData(reader, "Exam Code");
		connect.reachToData(reader, "<select");
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

	public void setName() throws BadHtmlSourceException, IOException {
		String str = extractString(connect.reachToData(reader, "Name:"));
		userName = str.substring(0, str.indexOf('['));
	}

	public String getName() {
		return userName;
	}


	private String extractString(String tmp) throws BadHtmlSourceException {
		Matcher matcher = connect.pattern1.matcher(tmp);
		if (!matcher.find())
			throw new BadHtmlSourceException();
		if (!matcher.find())
			throw new BadHtmlSourceException();
		return tmp.substring(matcher.start() + 1, matcher.end() - 1);
	}

	public List<SubjectLink> getSubjectURL() throws Exception {
		String tmp;
		List<SubjectLink> list = new ArrayList<SubjectLink>();

		connect.reachToData(reader, "<thead>");
		// connect.reachToData(reader, "Click on Subject to Sort");
		connect.reachToData(reader, "</thead>");
		connect.reachToData(reader, "<tbody>");
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

	public void readRow(List<SubjectLink> list) throws Exception {
		String tmp;
		SubjectLink sub = new SubjectLink();

		connect.readSingleData(connect.pattern1, reader);
		tmp = connect.readSingleData(connect.pattern1, reader);
		int i = lastDash(tmp);
		sub.name = titleCase((tmp.substring(0, i - 1)).trim());

		sub.code = "T" + (tmp.substring(i + 1)).trim();

		readLink_atnd(sub);

		list.add(sub);
	}

	private void readLink_atnd(SubjectLink sub) throws IOException,
			BadHtmlSourceException {

		String td = getTableData(reader);
		
		sub.link = getLink(td);
		
		sub.overall = getAtnd(td);

		sub.lect = getAtnd(getTableData(reader));
		sub.tute = getAtnd(getTableData(reader));

		td = getTableData(reader);

		if (sub.link==null)		{
			sub.link = getLink(td);
			if (sub.link!=null) sub.LTP = 0;
			else
				sub.LTP = -1;
			
		}
		else
			sub.LTP = 1;
		
		sub.pract = getAtnd(td);
		
		/*
		
		
		if (sub.LTP == 1)
			sub.link = getLink(td);
		sub.overall = getAtnd(td);

		sub.lect = getAtnd(getTableData(reader));
		sub.tute = getAtnd(getTableData(reader));

		td = getTableData(reader);
		if (sub.LTP == 0)
			sub.link = getLink(td);
		sub.pract = getAtnd(td);

	*/
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

	public static String titleCase(String str) {
		char[] tmp = str.toLowerCase().toCharArray();
		int flag = 0;
		tmp[0] = Character.toUpperCase(tmp[0]);
		for (int i = 1; i < tmp.length; ++i) {
			if (Character.isWhitespace(tmp[i]))
				flag = 1;
			else if (flag == 1) {
				tmp[i] = Character.toUpperCase(tmp[i]);
				flag = 0;
			}
		}
		return String.valueOf(tmp);
	}

	public static int lastDash(String str) {
		int n = str.length();
		int tmp = 0;
		for (int i = 0; i < n; ++i)
			if (str.charAt(i) == '-')
				tmp = i;
		return tmp;
	}

	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}

	public static class SubjectLink {
		String name;
		String code;
		String link;
		int overall;
		int lect;
		int tute;
		int pract;
		int LTP;
	}

}
