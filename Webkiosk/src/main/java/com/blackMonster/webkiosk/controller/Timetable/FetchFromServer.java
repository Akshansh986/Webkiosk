package com.blackMonster.webkiosk.controller.Timetable;

import android.content.Context;

import com.blackMonster.webkiosk.utils.NetworkUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Returns timetable text files saved on Google drive.
 */
class FetchFromServer {

	static final String TAG = "TimetableFetch";
	static final String URL = "https://googledrive.com/host/0B6GvdakwbRU-dTg0X19xSmlDQ1k";
	
	private static HttpClient httpclient;

	private static void closeConnection() {
		httpclient.getConnectionManager().shutdown();
	}
	
	static int getDataBundle(String colg,String fileName, String batch, List<String> ttList, Context context)  {
		int result;
			try {
				BufferedReader reader = getFileFromServer(fileName, colg, context);
				//Log.d(TAG, "connected to timetable server");			
				if (DatabaseFound(reader)) {
					//Log.d(TAG, "database found");			

					 result = getTimetable(batch,ttList, reader);
				}
				else {
					result = TimetableCreateRefresh.ERROR_DB_UNAVAILABLE;
				}
			} catch (Exception e) {
				result = TimetableCreateRefresh.ERROR_UNKNOWN;
				e.printStackTrace();
			}
			
		
		closeConnection();
		return result;
	}

	private static int getTimetable(String batch,List<String> list, BufferedReader reader) throws IOException {
			int result = TimetableCreateRefresh.ERROR_UNKNOWN;
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
				result = TimetableCreateRefresh.DONE;
				break;
				}
			}
			
		if (list.size()==0) result = TimetableCreateRefresh.ERROR_BATCH_UNAVAILABLE;
		return result;
		}

	private static boolean DatabaseFound(BufferedReader reader) throws IOException {
			return reader.readLine().contains("BEGIN TRANSACTION;");
		}

	static BufferedReader getFileFromServer(String fileName,String colg,Context context) throws Exception {
		if (!NetworkUtils.isInternetAvailable(context)) throw new IOException();
		
	
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

	static BufferedReader getTransferList(String colg, Context context) throws Exception {
		return getFileFromServer("transfer.txt", colg, context);
	}

	static BufferedReader getSubcodeList(String colg, Context context) throws Exception {
		return getFileFromServer("subcode.txt", colg, context);
	}
}
