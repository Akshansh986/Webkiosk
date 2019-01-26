package com.blackMonster.webkiosk.ui;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.WebkioskWebsite;
import com.blackMonster.webkioskApp.R;
import com.google.analytics.tracking.android.EasyTracker;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.util.EncodingUtils;

public class WebViewActivity extends BaseActivity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLayoutInflater().inflate(R.layout.webview, activityContent);

		webView = (WebView) findViewById(R.id.webView1);

		String enroll = MainPrefs.getEnroll(this);
		String pass = MainPrefs.getPassword(this);
		String dob = MainPrefs.getDOB(this);

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		WebkioskWebsite.initiliseLoginDetails(formparams, MainPrefs.getColg(this), enroll, pass, dob,  null);	//TODO : pass actual captcha.


		String postData = "";
		for (NameValuePair data : formparams) {
			postData += "&" + data.getName() + "=" + URLEncoder.encode(data.getValue());
		}
		postData = postData.substring(1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.setInitialScale(1);

		webView.setWebViewClient(new HelloWebViewClient());
		webView.postUrl(WebkioskWebsite.getLoginUrl(MainPrefs.getColg(this)),
				EncodingUtils.getBytes(postData, "BASE64"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.action_refresh).setVisible(false);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Google analytics.
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Google analytics.

	}

	@Override
	protected void onDestroy() {
		setVisible(false);
		super.onDestroy();
	}


	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	}

}