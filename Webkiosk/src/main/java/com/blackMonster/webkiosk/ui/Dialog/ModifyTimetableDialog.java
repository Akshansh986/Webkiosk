package com.blackMonster.webkiosk.ui.Dialog;

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
import com.blackMonster.webkiosk.controller.Timetable.TimetableDelegate;
import com.blackMonster.webkiosk.controller.Timetable.TimetableUtils;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;
import com.blackMonster.webkiosk.ui.TimetableListFragment;
import com.blackMonster.webkioskApp.R;

import net.simonvt.numberpicker.NumberPicker;

import java.util.Calendar;

public class ModifyTimetableDialog extends DialogFragment {

    private static final String TAG = "ModifyTimetableDialog";

    public static final String ARG_CURRENT_TIME = "ctime";
    public static final String ARG_CURRENT_VENUE = "cvenue";

    public static final String BROADCAST_MODIFY_TIMETABLE_RESULT = "BDC";

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


                        sendDoneBroadcast(getActivity());
                    }

                }).setNegativeButton(getString(R.string.MODIFY_DIALOG_DELETE),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showConformationDialog(currentDay, currentTime, getActivity());
                    }

                });

        builder.setNeutralButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendDoneBroadcast(getActivity());
                    }
                });

    }

    //Static because ModifyTimetableDialog(fragment) is detached from activity as soon as this dialog is shown. So it is unsafe to use
    //data from Fragment.
    private static void showConformationDialog(final int currentDay, final int currentTime, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(context.getResources().getString(R.string.delete_tt_message));

        builder.setPositiveButton(context.getString(R.string.MODIFY_DIALOG_DELETE),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        deleteClass(currentDay,currentTime, context);
                        Toast.makeText(context, context.getString(R.string.MODIFY_DIALOG_DELETED), Toast.LENGTH_SHORT)
                                .show();
                        MainPrefs.setTimetableModified(context);
                        sendDoneBroadcast(context);
                    }

                });
        builder.setNegativeButton(R.string.cancel,

                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDoneBroadcast(context);
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
     *
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

    private static void deleteClass(int currentDay, int currentTime, Context context) {
        TimetableDelegate.deleteClass(currentDay, currentTime, context);
    }

    private static void sendDoneBroadcast(Context context) {
        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(
                        new Intent(BROADCAST_MODIFY_TIMETABLE_RESULT));
    }


}
