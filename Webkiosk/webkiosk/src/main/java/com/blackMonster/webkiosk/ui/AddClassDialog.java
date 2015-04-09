package com.blackMonster.webkiosk.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
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

import com.blackMonster.webkiosk.databases.AttendenceData;
import com.blackMonster.webkiosk.databases.AttendenceData.AttendenceOverviewTable;
import com.blackMonster.webkiosk.MainPrefs;
import com.blackMonster.webkiosk.databases.TimetableData;
import com.blackMonster.webkioskApp.R;

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

		myDialog = ((Activity) getActivity()).getLayoutInflater().inflate(
				R.layout.modify_timetable_add_class, null);
		initiliseUIElements(myDialog);

		builder.setView(myDialog);
		builder.setTitle("New class details : ");

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						//Log.d("addclassDialog", "type : " + type + " subcode "
							//	+ subCode);
						venueString = venue.getEditableText().toString().trim();
						if (venueString.equals(""))
							Toast.makeText(getActivity(),
									"Venue can't be empty!!",
									Toast.LENGTH_SHORT).show();
						else
							addToDb();
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
								new Intent(BROADCAST_ADD_CLASS_DIALOG));
					}

				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
								new Intent(BROADCAST_ADD_CLASS_DIALOG));
					}
				});

		return builder.create();

	}

	private void addToDb() {

		boolean result = TimetableData.addNewClass(day.getValue(),
                time.getValue(), type, subCode, venueString, "NA",
                MainPrefs.getBatch(getActivity()), getActivity());
		if (result) {
			Toast.makeText(getActivity(), "Class added!!", Toast.LENGTH_SHORT)
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
		time.setMaxValue(TimetableData.CLASS_END_TIME);
		time.setMinValue(TimetableData.CLASS_START_TIME);
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

		AttendenceOverviewTable atndOverviewTable = AttendenceData
				.getInstance(getActivity()).new AttendenceOverviewTable();
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
			if (AttendenceData.isLab(subCode,getActivity())) {
				type = TimetableData.ALIAS_PRACTICAL;
				hideLTPSpinner();
			} else {
				showLTPSpinner();
				type = TimetableData.ALIAS_LECTURE;
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
				type = TimetableData.ALIAS_LECTURE;
			else if (position == 1)
				type = TimetableData.ALIAS_TUTORIAL;

		}

	}

}
