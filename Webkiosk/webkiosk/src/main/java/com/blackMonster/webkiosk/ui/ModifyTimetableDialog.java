package com.blackMonster.webkiosk.ui;

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

import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.Timetable.TimetableDelegate;
import com.blackMonster.webkiosk.Timetable.TimetableUtils;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;
import com.blackMonster.webkioskApp.R;

import net.simonvt.numberpicker.NumberPicker;

import java.util.Calendar;

public class ModifyTimetableDialog extends DialogFragment {

    private static final String TAG = "ModifyTimetableDialog";

    public static final String ARG_CURRENT_TIME = "ctime";
    public static final String ARG_CURRENT_VENUE = "cvenue";

    public static final String BROADCAST_DIALOG = "BDC";

    private NumberPicker day, time;
    private EditText venue;

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
        builder.setPositiveButton(getString(R.string.MODIFY_DIALOG_MOVE),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (moveOrChangeVenue()) {
                            Toast.makeText(getActivity(),
                                    R.string.timetable_update_sucess,
                                    Toast.LENGTH_LONG).show();

                            MainPrefs.setTimetableModified(getActivity());
                        } else
                            Toast.makeText(getActivity(),
                                    R.string.timetable_update_error,
                                    Toast.LENGTH_LONG).show();
                        LocalBroadcastManager
                                .getInstance(getActivity())
                                .sendBroadcast(
                                        new Intent(BROADCAST_DIALOG));
                    }

                }).setNegativeButton(getString(R.string.MODIFY_DIALOG_DELETE),
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
                                        new Intent(BROADCAST_DIALOG));  //TODO have to remove it.
                    }
                });

    }

    private void showConformationDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(getResources().getString(R.string.delete_tt_message));

        builder.setPositiveButton(getString(R.string.MODIFY_DIALOG_DELETE),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        deleteClass(context);
                        Toast.makeText(context, getString(R.string.MODIFY_DIALOG_DELETED), Toast.LENGTH_SHORT)
                                .show();
                        LocalBroadcastManager.getInstance(getActivity())
                                .sendBroadcast(
                                        new Intent(BROADCAST_DIALOG));
                       MainPrefs.setTimetableModified(context);
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
        time.setMaxValue(TimetableTable.CLASS_END_TIME);
        time.setMinValue(TimetableTable.CLASS_START_TIME);
        time.setValue(currentTime);
        time.setDisplayedValues(getFormattedTimeStringArray());

        venue = (EditText) myView.findViewById(R.id.modify_timetable_venue);
        venue.setText(currentVenue);

    }

    public static String[] getFormattedTimeStringArray() {
        String[] arr = new String[TimetableTable.CLASS_END_TIME
                - TimetableTable.CLASS_START_TIME + 1];
        for (int i = TimetableTable.CLASS_START_TIME; i <= TimetableTable.CLASS_END_TIME; ++i) {
            arr[i - TimetableTable.CLASS_START_TIME] = TimetableUtils
                    .getFormattedTime(i);
        }
        return arr;
    }

    /**
     * Move class or modify venue.
     * If destination is not empty, classes are swapped.
     * @return
     */
    private boolean moveOrChangeVenue() {

        //Source ( class which is long pressed)
        int firstDay = currentDay;
        int firstTime = currentTime;
        String firstVenue = venue.getEditableText().toString().toUpperCase();
        String firstData = TimetableTable.getRawData(currentDay, currentTime,
                getActivity());
        if (firstData == null) return false;
        firstData = firstData.replace("-" + currentVenue + "-", "-" + firstVenue + "-"); //updating venue, will remain same if it is not modified.

        //Destination
        int secondDay = day.getValue();
        int secondTime = time.getValue();
        String secondData = TimetableTable.getRawData(secondDay, secondTime,
                getActivity());


        //Swapping source and destination
        TimetableTable.insertRawData(firstDay, firstTime, secondData, getActivity());
        TimetableTable.insertRawData(secondDay, secondTime, firstData, getActivity());

        return true;
    }

    private void deleteClass(Context context) {
        TimetableDelegate.deleteClass(currentDay, currentTime, context);
    }

}
