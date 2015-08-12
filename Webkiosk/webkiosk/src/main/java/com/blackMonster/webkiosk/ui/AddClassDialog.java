package com.blackMonster.webkiosk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackMonster.webkiosk.controller.Timetable.TimetableDelegate;
import com.blackMonster.webkiosk.controller.Timetable.model.ClassTime;
import com.blackMonster.webkiosk.databases.AttendanceUtils;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;
import com.blackMonster.webkioskApp.R;

import net.simonvt.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddClassDialog extends DialogFragment {
	NumberPicker day, time;
	EditText venue;
	String venueString;
	char type;
	String subCode;
	View myDialog;
	List<String> listSubName, listSubCode;

	public static final String BROADCAST_ADD_CLASS_DIALOG = "BROADCAST_ADD_CLASS_DIALOG";

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		listSubName = new ArrayList<String>();
		listSubCode = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		myDialog = getActivity().getLayoutInflater().inflate(
				R.layout.modify_timetable_add_class, null);
		initiliseUIElements(myDialog);

		builder.setView(myDialog);
		builder.setTitle(getString(R.string.Dialog_add_class_title));

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						venueString = venue.getEditableText().toString().trim();
						if (venueString.equals(""))
							Toast.makeText(getActivity(),
									getString(R.string.Error_venue_empty),
									Toast.LENGTH_SHORT).show();
						else
							addToDb();
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
								new Intent(BROADCAST_ADD_CLASS_DIALOG));
					}

				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
								new Intent(BROADCAST_ADD_CLASS_DIALOG));
					}
				});

		return builder.create();

	}

	private void addToDb() {
		ClassTime classTime = 	new ClassTime(type,subCode,venueString,time.getValue(),"NA",day.getValue());
		boolean result = TimetableDelegate.addNewClass(classTime,
				getActivity());
		if (result) {
			Toast.makeText(getActivity(), getString(R.string.Class_added_message), Toast.LENGTH_SHORT)
					.show();
		} else
			Toast.makeText(getActivity(), R.string.add_tt_error,
					Toast.LENGTH_SHORT).show();
	}

	private void initiliseUIElements(View myView) {
		day = (NumberPicker) myView.findViewById(R.id.modify_tt_ac_day);
		day.setMaxValue(Calendar.SATURDAY);
		day.setMinValue(Calendar.MONDAY);
		day.setDisplayedValues(getResources().getStringArray(
				R.array.days_of_week));

		time = (NumberPicker) myView.findViewById(R.id.modify_tt_ac_time);
		time.setMaxValue(TimetableTable.CLASS_END_TIME);
		time.setMinValue(TimetableTable.CLASS_START_TIME);
		time.setDisplayedValues(ModifyTimetableDialog
				.getFormattedTimeStringArray());

		venue = (EditText) myView.findViewById(R.id.modify_tt_ac_venue);
		initSubjectSpinner(myView);
		initClassTypeSpinner(myView);

	}

	private void initClassTypeSpinner(View myView) {
		Spinner spinner = (Spinner) myView
				.findViewById(R.id.modify_tt_ac_class_type);

		List<String> listType = new ArrayList<String>();
		listType.add("Lec");
		listType.add("Tut");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, listType);

		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new ClassTypeOnItemSelectedListener());

	}

	private void initSubjectSpinner(View myView) {
		Spinner spinner = (Spinner) myView
				.findViewById(R.id.modify_tt_ac_sublist);

		AttendenceOverviewTable atndOverviewTable = new AttendenceOverviewTable(getActivity());
		Cursor cursor = atndOverviewTable.getData();
		extractData(cursor);
		cursor.close();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				listSubName);

		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new SubListOnItemSelectedListener());
	}

	private void extractData(Cursor cursor) {
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				listSubName.add(cursor.getString(cursor
						.getColumnIndex(AttendenceOverviewTable.C_NAME)));
				listSubCode.add(cursor.getString(cursor
						.getColumnIndex(AttendenceOverviewTable.C_CODE)));
			} while (cursor.moveToNext());
		}
	}

	public class SubListOnItemSelectedListener implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			subCode = listSubCode.get(pos);
			hideLTPIfLab(subCode);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			subCode = listSubCode.get(0);
			hideLTPIfLab(subCode);

		}

		private void hideLTPIfLab(String subCode) {
			if (AttendanceUtils.isLab(subCode, getActivity())) {
				type = TimetableTable.ALIAS_PRACTICAL;
				hideLTPSpinner();
			} else {
				showLTPSpinner();
				type = TimetableTable.ALIAS_LECTURE;
			}
		}

		private void showLTPSpinner() {
			((Spinner) myDialog.findViewById(R.id.modify_tt_ac_class_type))
					.setVisibility(View.VISIBLE);
			((Spinner) myDialog.findViewById(R.id.modify_tt_ac_class_type))
					.setSelection(0);

		}

		private void hideLTPSpinner() {
			((Spinner) myDialog.findViewById(R.id.modify_tt_ac_class_type))
					.setVisibility(View.INVISIBLE);
		}

	}

	public class ClassTypeOnItemSelectedListener implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			setType(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// type = TimetableData.ALIAS_LECTURE;

		}

		private void setType(int position) {
			if (position == 0)
				type = TimetableTable.ALIAS_LECTURE;
			else if (position == 1)
				type = TimetableTable.ALIAS_TUTORIAL;

		}

	}

}
