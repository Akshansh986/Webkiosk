package com.blackMonster.webkiosk.crawler;

import android.content.Context;

import com.blackMonster.webkiosk.utils.M;
import com.blackMonster.webkiosk.utils.NetworkUtils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Manages logging in to Webkiosk website.
 */
class SiteLogin {


	private static final String TAG = "SiteLogin";

	private HttpClient httpclient=null;

	HttpClient getConnection() {
		return httpclient;
	}

	int login(String colg,String enroll, String pass, String dob, Context context)  {

		if (!NetworkUtils.isInternetAvailable(context)) return LoginStatus.CONN_ERROR;

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		HttpPost httppost = new HttpPost(WebkioskWebsite.getLoginUrl(colg));
		BufferedReader reader=null;
		Integer status = null;
		try {
			httpclient = getHttpClient();
			HttpResponse homePage = fetchHomePage(colg);
			String captcha = getCaptchaCode(homePage);
			WebkioskWebsite.initiliseLoginDetails(formparams, colg, enroll, pass, dob, captcha);

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			status = responseStatus(reader);

		} catch (Exception e) {
			status = LoginStatus.UNKNOWN_ERROR;
			httppost.abort();
			e.printStackTrace();
		}

		if (status != LoginStatus.LOGIN_DONE) {
			httpclient.getConnectionManager().shutdown();
		}

		if (reader!= null)
			try {
				reader.close();
			} catch (IOException e) {
				status = LoginStatus.UNKNOWN_ERROR;
				e.printStackTrace();
			}

		return status;

	}

	private String getCaptchaCode(HttpResponse homePage) throws Exception {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(homePage.getEntity().getContent()));
			String line	= CrawlerUtils.reachToData(reader, "casteller");
			String captcha = CrawlerUtils.getInnerHtml(line);
			return captcha;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private HttpResponse fetchHomePage(String colg) throws IOException {
		HttpGet httpGet = new HttpGet(WebkioskWebsite.getSiteUrl(colg));
		HttpResponse response= null;
		try {
			response = httpclient.execute(httpGet);
			return response;
		} catch (IOException e) {
			httpGet.abort();
			throw e;
		}
	}

	private int responseStatus(BufferedReader reader) throws IOException {
		String tmp;

		while (true) {
			tmp = reader.readLine();
			if (tmp == null)
				return LoginStatus.UNKNOWN_ERROR;
			M.log(TAG, tmp);
			if (tmp.contains("PersonalFiles/ShowAlertMessageSTUD.jsp") || tmp.contains("StudentPageFinal.jsp"))
				return LoginStatus.LOGIN_DONE;
			if (tmp.contains("Invalid Password"))
				return LoginStatus.INVALID_PASS;
			if (tmp.contains("Login Account Locked"))
				return LoginStatus.ACCOUNT_LOCKED;
			if (tmp.contains("Wrong Member Type or Code") || tmp.toLowerCase().contains("correct institute name and enrollment"))
				return LoginStatus.INVALID_ENROLL;

		}
	}


	void close() {
		if (httpclient !=null)
			httpclient.getConnectionManager().shutdown();

	}


	public HttpClient getHttpClient() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
//		KeyStore trustStore = null;
//		trustStore = KeyStore.getInstance(KeyStore
//				.getDefaultType());
//		trustStore.load(null, null);
//		SSLSocketFactory sf = new TrustSSLSocketFactory(trustStore);
//		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//		HttpParams params = new BasicHttpParams();
//		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//		SchemeRegistry registry = new SchemeRegistry();
//		registry.register(new Scheme("http", PlainSocketFactory
//				.getSocketFactory(), 80));
//		registry.register(new Scheme("https", sf, 443));
//
//		ClientConnectionManager ccm = new ThreadSafeClientConnManager(
//				params, registry);
//
//		return new DefaultHttpClient(ccm, params);
		return new DefaultHttpClient();
	}
}
