package com.blackMonster.webkiosk.dateSheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.Uri;

import com.blackMonster.webkiosk.crawler.BadHtmlSourceException;
import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.SiteConnection;

public class FetchSeatingPlan {
	private static final int MAX_X = 5;
	private static final int MAX_Y = 20;
	private static	SiteConnection connect;
	private static Context context;

	public static List<SPlanRow> getData(SiteConnection cn, Context context) throws Exception {
		M.log("fetchseatingplan", "getdata");
		connect = cn;
		FetchSeatingPlan.context = context;
		List<SPlanRow> sp = new ArrayList<SPlanRow>();
		getDateSheet(sp);
		//for (SPlanRow a : sp) {
	//		M.log("datesheet", "course " + a.course +" date  " + a.dateTime + " sheet code" + a.sheetCode + " time " + a.seatNo);
	//	}
		return sp;

	}

	private static void getDateSheet(List<SPlanRow> dsList) throws Exception {
		 
		
		//String initialURL = "https://dl.dropboxusercontent.com/u/95984737/sp.htm";
			String initialURL = connect.siteUrl
					+ "/StudentFiles/Exam/StudViewSeatPlan.jsp";
		BufferedReader reader = sendGet(initialURL);
		List<String> codeList = new ArrayList<String>();
		getDSCodes(reader, codeList);
		reader.close();

		for (String sheetCode : codeList) {
			reader = sendGet(getUrl(sheetCode, context));
			extractSPdata(reader, dsList, sheetCode);
			reader.close();
		}
	}

	private static void extractSPdata(BufferedReader reader,
			List<SPlanRow> dsList, String sheetCode) throws Exception {
		SiteConnection.reachToData(reader, "submit");
		SiteConnection.reachToData(reader, "<table");
		SiteConnection.reachToData(reader, "</tr>");

		String[][] tableData = ExtractTable.extractTable(reader, MAX_X, MAX_Y);

		copyData(dsList, tableData, sheetCode);
		
		
		
	}

	private static void copyData(List<SPlanRow> dsList, String[][] tableData,
			String sheetCode) {
		for (int i = 0; i < tableData.length; ++i) {
			SPlanRow sp = new SPlanRow();
			sp.course = tableData[i][1];
			sp.dateTime = tableData[i][2];
			sp.roomNo = tableData[i][3];
			sp.seatNo = tableData[i][4];
			sp.sheetCode = sheetCode;
			if (sp.isEmpty())
				return;
			dsList.add(sp);
		}

	}

	private static String getUrl(String code, Context context) {
		String url = connect.siteUrl
				+ "/StudentFiles/Exam/StudViewSeatPlan.jsp?"
				+"x=&Inst=" + MainPrefs.getColg(context) + "&DScode=" + Uri.encode(code);
		M.log("tt", url);
		//return "https://dl.dropboxusercontent.com/u/95984737/sp.htm";
		return url;
	}

	private static BufferedReader sendGet(String url) {

		BufferedReader reader = null;
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = connect.httpclient.execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (Exception e) {
			httpget.abort();
		}

		return reader;

	}

	public static class SPlanRow {
		String sheetCode;
		String dateTime;
		String roomNo;
		String course;
		String seatNo;

		boolean isEmpty() {
			if (dateTime.equals("") && roomNo.equals("") && course.equals("") && seatNo.equals(""))
				return true;
			else
				return false;
		}
	}
	
	private static void getDSCodes(BufferedReader reader, List<String> codeList)
			throws BadHtmlSourceException, IOException {
		String tmp;
		SiteConnection.reachToData(reader, "id=\"DScode\"");

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
		str = str.substring(matcher.start() + 6, matcher.end() - 1 ).trim();
		return str.replaceAll("\"", "");
	}
}
