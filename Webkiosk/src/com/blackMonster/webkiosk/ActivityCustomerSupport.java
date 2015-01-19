package com.blackMonster.webkiosk;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.blackMonster.webkioskApp.R;

public class ActivityCustomerSupport extends ActionBarActivity {
	public static final String SUPPORT_TYPE = "Supporttype";
	// public static final int TYPE_FEEDBACK = -1;
	// public static final int TYPE_REPORT_PROBLEM = -2;
	public static final String OUR_EMAIL = "appwebkiosk@gmail.com";
	// int type;
	String mailSubject = "Bugs/Feedback";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// type = getIntent().getExtras().getInt(SUPPORT_TYPE);
		setContentView(R.layout.activity_customer_support);
		((EditText) findViewById(R.id.support_edittext)).setHint(getResources()
				.getString(R.string.customer_support_hint));
		initActionBar();
		// initSupportType();
	}

	/*
	 * private void initSupportType() { if (type == TYPE_FEEDBACK) { ((EditText)
	 * findViewById
	 * (R.id.support_edittext)).setHint(getResources().getString(R.string
	 * .support_feedback_hint)); getSupportActionBar().setTitle("Feedback");
	 * mailSubject = "Webkiosk Feedback";
	 * 
	 * } else if (type == TYPE_REPORT_PROBLEM) { ((EditText)
	 * findViewById(R.id.support_edittext
	 * )).setHint(getResources().getString(R.string
	 * .support_report_problem_hint));
	 * getSupportActionBar().setTitle("Report problem"); mailSubject =
	 * "Webkiosk Bug Report";
	 * 
	 * }
	 * 
	 * }
	 */

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String str = "";
		str = MainPrefs.getBatch(this) + "  " + MainPrefs.getColg(this) + "  "
				+ MainPrefs.getEnroll(this) + "  "
				+ MainPrefs.getOnlineTimetableFileName(this) + "  "
				+ MainPrefs.getPassword(this) + "  " + MainPrefs.getSem(this)
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
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Contact Us");
		getSupportActionBar().setLogo(
				getResources().getDrawable(R.drawable.ic_logo));

	}
}
