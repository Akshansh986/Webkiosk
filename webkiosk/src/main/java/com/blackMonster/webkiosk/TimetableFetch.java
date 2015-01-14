package com.blackMonster.webkiosk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

public class TimetableFetch {
	public static final int ERROR_DB_UNAVAILABLE = -4;
	public static final int ERROR_UNKNOWN = -3;
	public static final int ERROR_CONNECTION = -2;
	public static final int DONE = -1;
	
	public static final String TAG = "TimetableFetch";
	public static final String URL = "https://googledrive.com/host/0B6GvdakwbRU-dTg0X19xSmlDQ1k";
	
	private static HttpClient httpclient;
	/*
	private static BufferedReader connect(String institute,String fileName, Context context) {
		if (!SiteConnection.isInternetAvailable(context)) return null;
		
		//int calenderYear = Calendar.getInstance().get(Calendar.YEAR);
		String finalUrl = URL + "/" + institute + "/" + calenderYear + "/sem" + sem + ".txt"; 
		httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet(finalUrl);
		
	
		
		BufferedReader reader=null;
		HttpResponse response;
		
			try {
				 response = httpclient.execute(httpget);
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (Exception e) {
				httpget.abort();
			}
			
		return reader;
			
	}*/
	
	private static void closeConnection() {
		httpclient.getConnectionManager().shutdown();
	}
	
	public static int getDataBundle(String colg,String fileName, String batch, List<String> ttList, Context context)  {
		int result;
			try {
				BufferedReader reader = getFileFromServer(fileName, colg, context);
				//Log.d(TAG, "connected to timetable server");			
				if (DatabaseFound(reader)) {
					//Log.d(TAG, "database found");			

					 result = getTimetable(batch,ttList, reader);
				}
				else {
					result = ERROR_DB_UNAVAILABLE;
				}
			} catch (Exception e) {
				result = ERROR_UNKNOWN;
				e.printStackTrace();
			}
			
		
		closeConnection();
		return result;
	}

	private static int getTimetable(String batch,List<String> list, BufferedReader reader) throws IOException {
			int result = ERROR_UNKNOWN;
			String tmp;
			batch = batch.toUpperCase();
			while(true) {
				tmp = reader.readLine();
				if (tmp==null) {
				//	Log.d(TAG, "tmp is null");
					break;
				}
				//Log.d(TAG, tmp);
				
				if (tmp.contains("CREATE TABLE " + batch)) {
					list.add(tmp);
				//	Log.d(TAG, "added to list");
					while(true) {
						tmp = reader.readLine();
					//	Log.d(TAG,"useful " +  tmp);
						if (tmp == null) break;
						if (tmp.contains("INSERT INTO " + batch  + " ")) list.add(tmp);
					}
				result = DONE;
				break;
				}
			}
			
		if (list.size()==0) result = Timetable.ERROR_BATCH_UNAVAILABLE;
		return result;
		}

	private static boolean DatabaseFound(BufferedReader reader) throws IOException {
			return reader.readLine().contains("BEGIN TRANSACTION;");
		}

	public static BufferedReader getFileFromServer(String fileName,String colg,Context context) throws Exception {
		if (!SiteConnection.isInternetAvailable(context)) throw new IOException();
		
	
		httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet(getFinalUrl(fileName,colg));
		
		BufferedReader reader=null;
		HttpResponse response;
		
			try {
				 response = httpclient.execute(httpget);
				reader = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (Exception e) {
				httpget.abort();
				throw e;
			}
			
		return reader;	
	}

	private static String getFinalUrl(String fileName, String colg) {
		return URL+ "/" + colg + "/" + fileName ;
	}

	public static BufferedReader getTransferList(String colg, Context context) throws Exception {
		return getFileFromServer("transfer.txt", colg, context);
	}

	public static BufferedReader getSubcodeList(String colg, Context context) throws Exception {
		return getFileFromServer("subcode.txt", colg, context);
	}
}
