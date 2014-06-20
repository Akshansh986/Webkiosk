package com.blackMonster.webkiosk;

import java.util.Calendar;

import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyTimetableDialog extends DialogFragment {

	private static final String TAG = "ModifyTimetableDialog";

	public static final String ARG_CURRENT_TIME = "ctime";
	public static final String ARG_CURRENT_VENUE = "cvenue";
	public static final String IS_MODIFIED = "isTimetableModified";

	public static final String BROADCAST_DIALOG = "BDC";

	NumberPicker day, time;
	EditText venue;

	private int currentDay;
	private int currentTime;
	private String currentVenue;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		saveArguments();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.modify_timetable));
		View dialogView = getActivity().getLayoutInflater().inflate(
				R.layout.modify_timetable_dialog, null);
		initiliseUIElements(dialogView);
		builder.setView(dialogView);
		addButtons(builder);

		return builder.create();
	}

	private void saveArguments() {
		Bundle args = getArguments();
		currentDay = args.getInt(TimetableListFragment.ARG_DAY);
		currentTime = args.getInt(ARG_CURRENT_TIME);
		currentVenue = args.getString(ARG_CURRENT_VENUE);
	}

	private void addButtons(Builder builder) {
		builder.setPositiveButton("Move",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (modifyTimetable()) {
							Toast.makeText(getActivity(),
									R.string.timetable_update_sucess,
									Toast.LENGTH_LONG).show();
							getActivity()
									.getSharedPreferences(
											MainActivity.PREFS_NAME, 0).edit()
									.putBoolean(IS_MODIFIED, true).commit();

						} else
							Toast.makeText(getActivity(),
									R.string.timetable_update_error,
									Toast.LENGTH_LONG).show();
						LocalBroadcastManager
						.getInstance(getActivity())
						.sendBroadcast(
								new Intent(BROADCAST_DIALOG));
					}

				}).setNegativeButton("Delete",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						showConformationDialog(getActivity());
						
					}

				});

		builder.setNeutralButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						LocalBroadcastManager
						.getInstance(getActivity())
						.sendBroadcast(
								new Intent(BROADCAST_DIALOG));
					}
				});

	}

	private void showConformationDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(getResources().getString(R.string.delete_tt_message));

		builder.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						deleteClass(context);
						Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT)
								.show();
						LocalBroadcastManager.getInstance(getActivity())
								.sendBroadcast(
										new Intent(BROADCAST_DIALOG));
						context.getSharedPreferences(MainActivity.PREFS_NAME, 0)
								.edit().putBoolean(IS_MODIFIED, true).commit();
					}

				});
		builder.setNegativeButton(R.string.cancel,

		new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				LocalBroadcastManager
				.getInstance(getActivity())
				.sendBroadcast(
						new Intent(BROADCAST_DIALOG));
			}
		});

		builder.show();

	}

	private void initiliseUIElements(View myView) {
		day = (NumberPicker) myView.findViewById(R.id.modify_timetable_day);
		day.setMaxValue(Calendar.SATURDAY);
		day.setMinValue(Calendar.MONDAY);
		day.setValue(currentDay);
		day.setDisplayedValues(getResources().getStringArray(
				R.array.days_of_week));

		time = (NumberPicker) myView.findViewById(R.id.modify_timetable_time);
		time.setMaxValue(TimetableData.CLASS_END_TIME);
		time.setMinValue(TimetableData.CLASS_START_TIME);
		time.setValue(currentTime);
		time.setDisplayedValues(getFormattedTimeStringArray());

		venue = (EditText) myView.findViewById(R.id.modify_timetable_venue);
		venue.setText(currentVenue);

	}

	public static String[] getFormattedTimeStringArray() {
		String[] arr = new String[TimetableData.CLASS_END_TIME
				- TimetableData.CLASS_START_TIME + 1];
		for (int i = TimetableData.CLASS_START_TIME; i <= TimetableData.CLASS_END_TIME; ++i) {
			arr[i - TimetableData.CLASS_START_TIME] = TimetableData
					.getFormattedTime(i);
		}
		return arr;
	}

	private boolean modifyTimetable() {
		//current = first,,,, second = new
		String table = MainPrefs.getBatch(getActivity());

		int firstDay = currentDay;
		int firstTime = currentTime;
		String firstVenue = venue.getEditableText().toString().toUpperCase();
		String firstData = TimetableData.getRawData(currentDay, currentTime,
				table, getActivity());
		if (firstData == null) return false;
		firstData = firstData.replace("-" + currentVenue + "-" , "-" + firstVenue + "-");
		
		
		int secondDay = day.getValue();
		int secondTime = time.getValue();
		String secondData = TimetableData.getRawData(secondDay, secondTime,
				table, getActivity());
		
		
		TimetableData.insertRawData(firstDay, firstTime, secondData, MainPrefs.getBatch(getActivity()), getActivity());
		TimetableData.insertRawData(secondDay, secondTime, firstData, MainPrefs.getBatch(getActivity()), getActivity());
		
		return true;
/*
		if (currentData == null)
			return false;
		boolean result;
		if (isPractical(currentData))
			result = updatePractical(newDay, newTime, newVenue, currentData,
					table);
		else
			result = updateLT(newDay, newTime, newVenue, currentData, table);

		return result;*/
	}
/*
	private boolean updateLT(int newDay, int newTime, String newVenue,
			String currentData, String table) {
		String newData = TimetableData.getRawData(newDay, newTime, table,
				getActivity());
		if (newData != null) {
			if (newData.equals(TimetableData.PRACTICAL_SECOND_CLASS))
				return false;
			if (isPractical(newData)
					&& currentTime == TimetableData.CLASS_END_TIME)
				return false;
		}

		if (!newVenue.equals(currentVenue))
			updateVenue(currentData, newVenue, table);
		TimetableData.swapClass(currentDay, currentTime, newDay, newTime,
				table, getActivity());
		return true;
	}

	private boolean updatePractical(int newDay, int newTime, String newVenue,
			String currentData, String table) {

		if (newTime == TimetableData.CLASS_END_TIME) {
			return false;
		} else {
			if (!newVenue.equals(currentVenue))
				updateVenue(currentData, newVenue, table);
			TimetableData.swapClass(currentDay, currentTime, newDay, newTime,
					table, getActivity());
			TimetableData.swapClass(currentDay, currentTime + 1, newDay,
					newTime + 1, table, getActivity());
			return true;
		}
	}

	private void updateVenue(String rawData, String newVenue, String table) {
		TimetableData.updateRawData(currentDay, currentTime,
				rawData.replace(currentVenue, newVenue), table, getActivity());
	}

	private boolean isPractical(String data) {
		return data.charAt(0) == TimetableData.ALIAS_PRACTICAL;
	}
	*/

	private void deleteClass(Context context) {
		TimetableData.deleteClass(currentDay, currentTime, context);
	}
	
}
