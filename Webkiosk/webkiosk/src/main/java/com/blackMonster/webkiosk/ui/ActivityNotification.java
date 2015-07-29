package com.blackMonster.webkiosk.ui;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blackMonster.notifications.LocalData;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.ui.BaseActivity;
import com.blackMonster.webkioskApp.R;
import com.google.analytics.tracking.android.EasyTracker;

import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;

public class ActivityNotification extends BaseActivity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LocalData.setNotificationAlertDone(getBaseContext());
		getLayoutInflater().inflate(R.layout.webview, activityContent);
		getSupportActionBar().setTitle(LocalData.getNotification(this).title);

		webView = (WebView) findViewById(R.id.webView1);


		String postData = "";
		postData += "colg" + "=" + URLEncoder.encode(MainPrefs.getColg(this));
		postData += "&" + "enroll" + "="
				+ URLEncoder.encode(MainPrefs.getEnroll(this));
		postData += "&" + "batch" + "="
				+ URLEncoder.encode(MainPrefs.getBatch(this));
		//postData = postData.substring(1);
		///Log.d(TAG, postData);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.setInitialScale(1);

		webView.setWebViewClient(new HelloWebViewClient());
		webView.postUrl(LocalData.getNotification(this).link,
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