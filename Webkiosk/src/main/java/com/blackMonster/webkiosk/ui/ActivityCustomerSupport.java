package com.blackMonster.webkiosk.ui;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blackMonster.webkiosk.utils.AESencrp;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkioskApp.R;

public class ActivityCustomerSupport extends ActionBarActivity {
	public static final String OUR_EMAIL = "appwebkiosk@gmail.com";
	String mailSubject = "Bugs/Feedback";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_support);
		((EditText) findViewById(R.id.support_edittext)).setHint(getResources()
				.getString(R.string.customer_support_hint));
		initActionBar();
	}


	public void buttonNext(View v) {
		String msg = ((EditText) findViewById(R.id.support_edittext))
				.getEditableText().toString().trim();
		if (!msg.equals("")) {
			msg = msg + "\n\n\n" + "***Dont modify it***\n" + getUserDetails();
			sendMail(msg);

		}
	}

	private String getUserDetails() {
		String appver="";
		
		try {
			appver = "Version "
					+ getPackageManager().getPackageInfo(this.getPackageName(),
							0).versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		String str = "";
		str = MainPrefs.getBatch(this) + "  " + MainPrefs.getColg(this) + "  "
				+ MainPrefs.getEnroll(this) + "  "
				+ MainPrefs.getOnlineTimetableFileName(this) + "  "
				+ MainPrefs.getPassword(this) + " "
				+ MainPrefs.getDOB(this)
				+ "  " + MainPrefs.getStartupActivityName(this) + "  "
				+ MainPrefs.getUserName(this) + " " + appver;

		AESencrp aes = new AESencrp();

		String encrypted = "";
		try {
			encrypted = AESencrp.bytesToHex(aes.encrypt(str));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encrypted;
	}

	private void sendMail(String msg) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { OUR_EMAIL });
		i.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		i.putExtra(Intent.EXTRA_TEXT, msg);
		try {
			startActivity(Intent.createChooser(i, "Select email client"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "There are no email clients installed.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initActionBar() {
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.theme)));
		getSupportActionBar().setTitle("Contact Us");
		getSupportActionBar().setLogo(
				getResources().getDrawable(R.drawable.ic_logo));

	}
}
