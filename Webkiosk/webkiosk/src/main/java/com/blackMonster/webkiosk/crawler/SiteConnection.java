package com.blackMonster.webkiosk.crawler;

import android.content.Context;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.utils.NetworkUtils;
import com.blackMonster.webkioskApp.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SiteConnection {
	public static final int LOGIN_DONE = 1;
	public static final int CONN_ERROR = 2;
	public static final int INVALID_PASS = 3;
	public static final int INVALID_ENROLL = 4;
	public static final int ACCOUNT_LOCKED = 5;
	public static final int UNKNOWN_ERROR = 6;
	

	
	static final String TAG = "SiteConncetion";
	public static Pattern pattern1 = Pattern.compile(">([^<>]+)<");

	public HttpClient httpclient=null;
	public String siteUrl;
	private String loginUrl;
	String colg;
	public SiteConnection(String colg) {
		httpclient = new DefaultHttpClient();
		siteUrl = getSiteUrl(colg);
		loginUrl =getLoginUrl(colg);
		this.colg = colg;
	}
	
	public static String getSiteUrl(String colg) {
		if (colg.equals("J128")) return  "https://webkiosk.jiit.ac.in";
		return "https://webkiosk."+ colg +".ac.in";
	}
	
	public static String getLoginUrl(String colg) {
		return getSiteUrl(colg) + "/CommonFiles/UserAction.jsp";
	}

	public int login(String enroll, String pass, Context context)  {

		if (!NetworkUtils.isInternetAvailable(context)) return CONN_ERROR;

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		initiliseLoginDetails(formparams,colg, enroll,pass);
		
		httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(loginUrl);
		BufferedReader reader=null;
		Integer status = null;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			status = responseStatus(reader);

		} catch (IOException e) {
			status = UNKNOWN_ERROR; 
			httppost.abort();
			e.printStackTrace();
		} 
		
		if (status != LOGIN_DONE) {
			httpclient.getConnectionManager().shutdown();
		}
		
		if (reader!= null)
			try {
				reader.close();
			} catch (IOException e) {
				status = UNKNOWN_ERROR;
				e.printStackTrace();
			}
		
		return status;
		
	}
	
	private int responseStatus(BufferedReader reader) throws IOException {
		String tmp;
		
		while (true) {
			tmp = reader.readLine();
			if (tmp == null)
				return UNKNOWN_ERROR;
			M.log(TAG, tmp);
			if (tmp.contains("PersonalFiles/ShowAlertMessageSTUD.jsp") || tmp.contains("StudentPageFinal.jsp"))
				return LOGIN_DONE;
			if (tmp.contains("Invalid Password"))
				return INVALID_PASS;
			if (tmp.contains("Login Account Locked"))
				return ACCOUNT_LOCKED;
			if (tmp.contains("Wrong Member Type or Code") || tmp.toLowerCase().contains("correct institute name and enrollment"))
				return INVALID_ENROLL;
				
		}
	}
	
	public static  String responseToString(Context context,int response, boolean isFirstTimeLogin) {
		switch (response) {

		case INVALID_PASS:
			if (isFirstTimeLogin)
				return context.getString(R.string.first_login_invalid_pass);
//			else
	//			return context.getString(R.string.password_changed);

		case INVALID_ENROLL:
			return context.getString(R.string.invalid_enroll);

		case CONN_ERROR:
			return context.getString(R.string.con_error);
		case ACCOUNT_LOCKED :
			if (isFirstTimeLogin) 
				return context.getString(R.string.webkiosk_account_locked_at_first_login);
		//	else
			//	return context.getString(R.string.webkiosk_account_locked);

		case UNKNOWN_ERROR:
			return context.getString(R.string.unknown_error);

		default:
			return null;

		}
	}
	

	public static void initiliseLoginDetails(List<NameValuePair> formparams, String colg, String enroll, String pass) {
		//Log.d(TAG, "Connecting........");
		formparams.add(new BasicNameValuePair("txtInst", "Institute"));
		formparams.add(new BasicNameValuePair("InstCode", colg.toUpperCase() + " "));
		formparams.add(new BasicNameValuePair("txtuType", "Member Type "));
		formparams.add(new BasicNameValuePair("UserType", "S"));
		formparams.add(new BasicNameValuePair("txtCode", "Enrollment No"));
		formparams.add(new BasicNameValuePair("MemberCode", enroll));
		formparams.add(new BasicNameValuePair("txtPin", "Password/Pin"));
		formparams.add(new BasicNameValuePair("Password", pass));
		formparams.add(new BasicNameValuePair("BTNSubmit", "Submit"));

		//Log.d(TAG, "Initiliased");

	}


	public void close() {
		if (httpclient !=null)
		httpclient.getConnectionManager().shutdown();

	}


}
