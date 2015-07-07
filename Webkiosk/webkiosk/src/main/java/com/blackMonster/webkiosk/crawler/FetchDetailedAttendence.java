package com.blackMonster.webkiosk.crawler;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FetchDetailedAttendence {
	static final String TAG = "LoadAttendence";
	SiteConnection connect;
	BufferedReader reader;
	int SNo;
	int LTP;

	public FetchDetailedAttendence(SiteConnection cn, String url,int ltp, int SrNo)
			throws Exception {
		connect = cn;
		SNo = SrNo;
		LTP = ltp;
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = connect.httpclient.execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (Exception e) {
			httpget.abort();
			throw e;
		}

		connect.reachToData(reader, "<tbody>");
	}

	public Attendence getAttendence() throws IOException,
            BadHtmlSourceException {
		
		int num = skipLessThenSNo();
		
		if (num==0) return null;
		return loadRow(num);
	
	}

	private int skipLessThenSNo() throws IOException, BadHtmlSourceException {
		String tmp;
		int num;
		while (true){
			tmp = reader.readLine();
			if (tmp==null) throw new BadHtmlSourceException();
			
			if (tmp.contains("<tr>")) {
				tmp = connect.readSingleData(connect.pattern1, reader);
				num = Integer.decode(tmp.substring(0, tmp.length() - 1));
				if (num>SNo) 
					return num;
				
			}
			else if (tmp.contains("</tbody>")) return 0;
			
			
		}
		
	}

	
	private Attendence loadRow(int n) throws IOException,
			BadHtmlSourceException {
		
		Attendence atnd = new Attendence();
		atnd.SNo = n;
		atnd.date = connect.readSingleData(connect.pattern1,reader);
		atnd.AttendenceBY = connect.readSingleData(connect.pattern1,reader);

		if (connect.readSingleData(connect.pattern1,reader).equals("Present"))
			atnd.status = 1;
		else
			atnd.status = 0;

		atnd.ClassType = connect.readSingleData(connect.pattern1,reader);
		
		if (LTP==1) atnd.LTP = connect.readSingleData(connect.pattern1,reader);
		return atnd;

	}
	
	public void close() throws IOException {
		reader.close();
	}

	public static class Attendence {
		public int SNo;
		public String date;
		public String AttendenceBY;
		public int status;
		public String ClassType;
		public String LTP;
	}

}
