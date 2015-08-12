package com.blackMonster.webkiosk.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.blackMonster.webkioskApp.R;

public class AppRater {
	private final static String APP_TITLE = "Webkiosk";
	private final static String APP_PNAME = "com.blackMonster.webkioskApp";

	private final static int DAYS_UNTIL_PROMPT = 3;
	private final static int LAUNCHES_UNTIL_PROMPT = 7;

	public static void app_launched(Context mContext) {
		M.log(APP_TITLE, "app launched");
		SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);
		M.log(APP_TITLE, "launch count" + launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch
					+ (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				createAlertDialog(mContext, editor);
			}
		}

		editor.commit();
	}
/*
	public static void showRateDialog(final Context mContext,
			final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setTitle("Rate " + APP_TITLE);

		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(mContext);
		tv.setText("If you enjoy using " + APP_TITLE
				+ ", please take a moment to rate it. Thanks for your support!");
		tv.setWidth(240);
		tv.setPadding(4, 0, 4, 10);
		ll.addView(tv);

		Button b1 = new Button(mContext);
		b1.setText("Rate " + APP_TITLE);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}

				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id=" + APP_PNAME)));
				dialog.dismiss();
			}
		});
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText("Remind me later");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText("No, thanks");
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);
		dialog.show();
	}
*/
	public static AlertDialog createAlertDialog(final Context context,
			final SharedPreferences.Editor editor) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	

		builder.setPositiveButton(R.string.rate_dialog_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						editor.putLong("launch_count", 0).commit();
						dialog.dismiss();

					}
				});

		builder.setNegativeButton(R.string.rate_dialog_no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (editor != null) {
							editor.putBoolean("dontshowagain", true);
							editor.commit();
						}
						dialog.dismiss();
					}
				});
		
		builder.setNeutralButton(R.string.rate_dialog_ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						if (editor != null) {
							editor.putBoolean("dontshowagain", true);
							editor.commit();
						}

						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("market://details?id=" + APP_PNAME)));
						dialog.dismiss();
					}
				});
		
		builder.setMessage(R.string.rate_dialog_message);
		builder.setTitle(R.string.rate_dialog_title);
		builder.create().show();
		return builder.create();

	}
}
// see
// http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater