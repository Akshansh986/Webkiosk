package com.blackMonster.webkiosk.dateSheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.blackMonster.webkiosk.BadHtmlSourceException;
import com.blackMonster.webkiosk.SiteConnection;


public class ExtractTable {
	/*public static void run(SiteConnection connect) {
		BufferedReader reader;
		HttpResponse response;
		Log.d("Table", "run");

		HttpGet httpget = new HttpGet("http://www.w3schools.com/Tags/tryit.asp?filename=tryhtml_td_rowspan");
		try {
			response = connect.httpclient.execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			connect.reachToData(reader, "<html>");
			connect.reachToData(reader, "table,th,td");
			connect.reachToData(reader, "<table>");
			String[][] data = extractTable(reader);
		
			for (int i =0; i<MAX_Y ; ++i)
				for (int j=0 ; j<MAX_X ; ++j)
					Log.d("Table", "(" + i +"," + j + ")" + data[i][j] ) ;
		} catch (Exception e) {
			httpget.abort();
			e.printStackTrace();
		}
		
		
	}*/
	
	public static String[][] extractTable(BufferedReader reader, int maxX, int maxY) throws Exception{
		Log.d("Table", "extracttable");
		String tmp;
		String[][] data = new String[maxY][maxX];
		init(data,maxX, maxY);
		int rowCount = 0;
		while (true) {
			tmp = reader.readLine();
			Log.d("Table", tmp);
			if (tmp == null)
				throw new BadHtmlSourceException();

			if (tmp.contains("</table>"))
				return data;
			if (tmp.contains("<tr>")) {
				readRow(reader, data,tmp, rowCount++, maxX, maxY);
			}
		}
	}
	
	

	private static void init(String[][] data, int maxX, int maxY) {
		for (int i =0; i<maxY ; ++i)
			for (int j=0 ; j<maxX ; ++j)
				data[i][j] = "";
	}

	private static void readRow(BufferedReader reader, String[][] data,
			String initString, int rowCount, int maxX, int maxY) throws Exception {
		Log.d("Table", "readRow");
		SingleData sd;
		while (true) {
			sd = readSingleData(SiteConnection.pattern1, reader,initString);
			initString = null;
			if (sd==null) return;
			
			int freeCell = getFreeRowCell(data[rowCount], maxX);
			Log.d("table", "length" + freeCell);
			
			if (sd.data.contains("&nbsp")) {
				data[rowCount][freeCell] = data[rowCount-1][freeCell];
				continue;
			}
			data[rowCount][freeCell] = sd.data;
			sd.original = sd.original.toUpperCase();
			
			if (sd.original.contains("ROWSPAN")) {
				int rs = getRowSpan(sd.original);
				Log.d("table", "rowSpan " + rs);
				for (int i =1 ; i< rs ; ++i) data[rowCount + i][freeCell] = sd.data;
			}
			
		}
				
	}
	
	
	private static int getFreeRowCell(String[] str, int maxX) {
		for (int i = 0 ; i < maxX ; ++i) 
			if ( str[i].equals(""))
				return i;
			
		return -1;
	}



	private static int getRowSpan(String str) {
		str = str.replaceAll("\\s","");
		str = str.replaceAll("\"","");

		int x = str.indexOf("ROWSPAN");
		str = str.substring(x+7,x+9);
		str = str.replaceAll("\\D","");
		return Integer.parseInt(str);
	}

	private static SingleData readSingleData(Pattern pattern, BufferedReader reader, String initString) throws IOException {
		String tmp;
		SingleData singleData = new SingleData();
		Log.d("Table", "readsingledata");
		while (true) {
			if (initString!=null){
				tmp = initString;
				initString = null;
			}
			else
			tmp = reader.readLine();
			Log.d("Table", tmp);
			Matcher matcher = pattern.matcher(tmp);

			if (matcher.find()) {
				singleData.original = tmp;
				singleData.data = tmp.substring(matcher.start() + 1, matcher.end() - 1);
				return singleData;
			}

			if (tmp == null || tmp.contains("</tr>"))
				return null;
			
		}
	}
	
	private static class SingleData{
		String  original;
		String data;
	}
}
