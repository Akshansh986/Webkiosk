package com.blackMonster.webkiosk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.SharedPrefs.RefreshBroadcasts;
import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkiosk.controller.Timetable.TimetableCreateRefresh;
import com.blackMonster.webkiosk.controller.appLogin.CreateDatabase;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateAvgAtnd;
import com.blackMonster.webkiosk.controller.updateAtnd.UpdateDetailedAttendence;
import com.blackMonster.webkiosk.crawler.LoginStatus;
import com.blackMonster.webkiosk.controller.appLogin.InitDB;
import com.blackMonster.webkioskApp.R;

/**
 * Alert dialog for error messages.
 */
public class AlertDialogHandler {

	public static final String DIALOG_KEY = "dialog";
	public static final String DIALOG_DEFAULT_VALUE = "NA";

	static AlertDialog dialog = null;
	static SharedPreferences prefs = null;

	private static void initPrefInstance(Context context) {
		if (prefs == null)
			prefs = context.getSharedPreferences(MainPrefs.PREFS_NAME, 0);
	}

	public static void checkDialog(Context context) {
		initPrefInstance(context);
		String dialogMsg = prefs.getString(DIALOG_KEY, DIALOG_DEFAULT_VALUE);

		if (!dialogMsg.equals(DIALOG_DEFAULT_VALUE)) {
			dialog = createAlertDialog(dialogMsg, context);
			dialog.show();
		}
	}

	public static void dismissIfPresent() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public static AlertDialog createAlertDialog(String msg,
			final Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog = null;
						context.getSharedPreferences(MainPrefs.PREFS_NAME, 0)
								.edit()
								.putString(DIALOG_KEY, DIALOG_DEFAULT_VALUE)
								.commit();
					}
				});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog = null;
				context.getSharedPreferences(MainPrefs.PREFS_NAME, 0).edit()
						.putString(DIALOG_KEY, DIALOG_DEFAULT_VALUE).commit();
			}
		});

		builder.setMessage(msg);
		return builder.create();

	}

	public static void saveDialogToPref(String type, int result, String batch,
			boolean isFirstTimeLogin, Context context) {

		if (type.equals(RefreshBroadcasts.BROADCAST_LOGIN_RESULT)) {
			if (result != LoginStatus.LOGIN_DONE)
				addToPrefs(LoginStatus.responseToString(context, result,
						isFirstTimeLogin), context);
		}

		else if (type.equals(RefreshBroadcasts.BROADCAST_UPDATE_AVG_ATND_RESULT)) {
			if (result == UpdateAvgAtnd.ERROR)
				addToPrefs(context.getString(R.string.attendence_update_error),
						context);
		}

		else if (type
				.equals(InitDB.BROADCAST_DATEBASE_CREATION_RESULT)) {
			if (result == CreateDatabase.ERROR)
				addToPrefs(context.getString(R.string.database_creation_error),
						context);
			else

			if (result == TimetableCreateRefresh.ERROR_BATCH_UNAVAILABLE)
				addToPrefs(context.getString(R.string.invalid_batch), context);
			else if (result == TimetableCreateRefresh.ERROR_CONNECTION)
				addToPrefs(
						context.getString(R.string.error_timetableserver_connection),
						context);
			else if (result == TimetableCreateRefresh.ERROR_UNKNOWN)
				addToPrefs(context.getString(R.string.unknown_error), context);
		} else if (type
				.equals(RefreshBroadcasts.BROADCAST_UPDATE_DETAILED_ATTENDENCE_RESULT)) {
			if (result == UpdateDetailedAttendence.ERROR)
				addToPrefs(context.getString(R.string.attendence_update_error),
						context);
		}

	}

	private static void addToPrefs(String msg, Context context) {
		initPrefInstance(context);
		prefs.edit().putString(AlertDialogHandler.DIALOG_KEY, msg).commit();
	}

	public static void showChangePasswordDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final View myView = activity.getLayoutInflater().inflate(
				R.layout.dialog_change_pass, null);

		builder.setView(myView);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						String pass = ((EditText) myView
								.findViewById(R.id.changepass_dialog_editbox))
								.getEditableText().toString().trim();
						if (pass.equals(""))
							showChangePasswordDialog(activity);
						else {
							activity.getSharedPreferences(
									MainPrefs.PREFS_NAME, 0).edit()
									.putString(MainPrefs.PASSWORD, pass)
									.commit();
							Toast.makeText(
									activity,
									activity.getResources()
											.getString(
													R.string.password_changed_successfully),
									Toast.LENGTH_SHORT).show();
							RefreshDBPrefs.setPasswordUptoDate(activity);

						}
					}

				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.create().show();
	}
}
