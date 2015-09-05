package com.blackMonster.webkiosk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.blackMonster.webkiosk.controller.FullClassInfoHandler;
import com.blackMonster.webkiosk.controller.model.SingleClass;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.ui.Dialog.ModifyTimetableDialog;
import com.blackMonster.webkiosk.ui.adapters.SingleDayTimetableAdapter;
import com.blackMonster.webkioskApp.R;

import java.util.List;

public class TimetableListFragment extends ListFragment {
    public static final String ARG_DAY = "day";
    public static final String TAG = "timetableListFragment";
    public int currentDay;
    SingleDayTimetableAdapter adapter;
    BroadcastModifyDialog broadcastModifyTimetableDialog;
    private List<SingleClass> classList;            //List of all classes on current day

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        currentDay = args.getInt(ARG_DAY);

        try {
            classList = FullClassInfoHandler.getAllClassOfDay(currentDay,
                    getActivity());
        } catch (Exception e) {
            classList = null;
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        getListView().setEmptyView(                                     //Set empty view, incase timetable is not present.
                ((LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.empty_timetable, null));
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (classList == null)  //Error handling
            return;

        adapter = new SingleDayTimetableAdapter(currentDay, classList, getActivity());
        setListAdapter(adapter);
        if (!TimetableDbHelper.databaseExists(getActivity()))
            setEmptyText(getResources().getString(R.string.timetable_na));
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                createDialog(position); //Modify timetable dialog
                registerReceiver();     //To receive timetable modification status.
                return true;
            }

            private void registerReceiver() {
                broadcastModifyTimetableDialog = new BroadcastModifyDialog();
                LocalBroadcastManager
                        .getInstance(getActivity())
                        .registerReceiver(
                                broadcastModifyTimetableDialog,
                                new IntentFilter(
                                        ModifyTimetableDialog.BROADCAST_MODIFY_TIMETABLE_RESULT));
            }

            private void createDialog(int position) {
                DialogFragment dialogFragment = new ModifyTimetableDialog();

                Bundle args = new Bundle();
                args.putInt(ARG_DAY, currentDay);
                args.putInt(ModifyTimetableDialog.ARG_CURRENT_TIME, classList
                        .get(position).getTime());
                args.putString(ModifyTimetableDialog.ARG_CURRENT_VENUE,
                        classList.get(position).getVenue());
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), "timetable");
            }

        });
    }

    //Update fragment with new data.
    public void updateThisFragment() throws Exception {
        classList = FullClassInfoHandler.getAllClassOfDay(currentDay,
                getActivity());
        adapter.updateDataSet(classList);

    }

    //Received when modification in timetable is done.
    private class BroadcastModifyDialog extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ((TimetableActivity) getActivity()).mViewPager.getAdapter()
                        .notifyDataSetChanged();
                LocalBroadcastManager.getInstance(getActivity())
                        .unregisterReceiver(broadcastModifyTimetableDialog); //modification done, better to unregister it.
                broadcastModifyTimetableDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
