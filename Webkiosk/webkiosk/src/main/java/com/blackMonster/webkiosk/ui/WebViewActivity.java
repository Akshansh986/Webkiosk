package com.blackMonster.webkiosk.ui;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.util.EncodingUtils;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blackMonster.webkiosk.MainActivity;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkioskApp.R;
import com.google.analytics.tracking.android.EasyTracker;

public class WebViewActivity extends BaseActivity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLayoutInflater().inflate(R.layout.webview, activityContent);

		webView = (WebView) findViewById(R.id.webView1);

		SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME,
				0);
		String enroll = MainPrefs.getEnroll(this);
		String pass = MainPrefs.getPassword(this);

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		SiteConnection.initiliseLoginDetails(formparams, MainPrefs.getColg(this), enroll, pass);

		String postData = "";
		for (NameValuePair data : formparams) {
			postData += "&" + data.getName() + "=" + URLEncoder.encode(data.getValue());
		}
		postData = postData.substring(1);
		///Log.d(TAG, postData);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.setInitialScale(1);

		webView.setWebViewClient(new HelloWebViewClient());
		webView.postUrl(SiteConnection.getLoginUrl(MainPrefs.getColg(this)),
				EncodingUtils.getBytes(postData, "BASE64"));

		// Log.d(TAG, "oncreate end");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.findItem(R.id.action_refresh).setVisible(false);
		return true;
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.

	}

	
	@Override
	protected void onDestroy() {
		setVisible(false);
		super.onDestroy();
	}
}