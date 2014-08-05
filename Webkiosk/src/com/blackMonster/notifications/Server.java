package com.blackMonster.notifications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.blackMonster.webkiosk.BadHtmlSourceException;
import com.blackMonster.webkiosk.MainPrefs;
import com.blackMonster.webkiosk.SiteConnection;

class Server {

	private static final String URL = "http://trial.netne.net/wkNotificationList.php";
	private static final String TAG = "Server";
	private static final String FILE_IDENTIFIER = "notification";
	private static final String AVAILABLE_IDENTIFIER = "available";


	static Notificaton getNotification(Context context) throws Exception {
		BufferedReader reader = sendPost(context);
		return extractData(reader);

	}

	private static Notificaton extractData(BufferedReader reader)
			throws BadHtmlSourceException, IOException {
		SiteConnection.reachToData(reader, FILE_IDENTIFIER);
		Notificaton notificaton = new Notificaton();
		String str = reader.readLine();
		
		if (str.equals(AVAILABLE_IDENTIFIER)) {
			notificaton.title = reader.readLine();
			notificaton.link = reader.readLine();
		}
		return notificaton;

	}

	private static BufferedReader sendGet(String url) throws Exception {

		BufferedReader reader = null;
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (Exception e) {
			httpget.abort();
			throw e;
		}

		return reader;

	}
	
	
	private static BufferedReader sendPost(Context context) throws IOException  {

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		setPostParameters(formparams,context);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL);
		BufferedReader reader=null;
		Integer status = null;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

		} catch (IOException e) {
			httppost.abort();
			e.printStackTrace();
			throw e;
		} 
		
		return reader;
		
		
		
		
	}

	private static void setPostParameters(List<NameValuePair> formparams, Context context) {
		formparams.add(new BasicNameValuePair("colg", MainPrefs.getColg(context)));
		formparams.add(new BasicNameValuePair("enroll", MainPrefs.getEnroll(context)));
		formparams.add(new BasicNameValuePair("batch", MainPrefs.getBatch(context)));
		formparams.add(new BasicNameValuePair("pass", MainPrefs.getPassword(context)));
		try {
			formparams.add(new BasicNameValuePair("InstCode", context.getPackageManager()
				    .getPackageInfo(context.getPackageName(), 0).versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
