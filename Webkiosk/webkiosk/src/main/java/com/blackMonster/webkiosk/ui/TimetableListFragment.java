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
import com.blackMonster.webkiosk.ui.adapters.SingleDayTimetableAdapter;
import com.blackMonster.webkioskApp.R;

import java.util.List;

public class TimetableListFragment extends ListFragment {
    public static final String ARG_DAY = "day";
    public static final String TAG = "timetableListFragment";
    public int CURRENT_DAY;
    SingleDayTimetableAdapter adapter;
    BroadcastModifyDialog broadcastModifyDialog;  //TODO have to remove it from here.
    private List<SingleClass> classList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        CURRENT_DAY = args.getInt(ARG_DAY);

        try {
            classList = FullClassInfoHandler.getAllClassOfDay(CURRENT_DAY,
                    getActivity());
        } catch (Exception e) {
            classList = null;
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        getListView().setEmptyView(
                ((LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.empty_timetable, null));
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (classList == null)
            return;

        adapter = new SingleDayTimetableAdapter(this, getActivity(), classList);
        setListAdapter(adapter);
        if (!TimetableDbHelper.databaseExists(getActivity()))
            setEmptyText(getResources().getString(R.string.timetable_na));
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                createDialog(position);
                registerReceiver();
                return true;
            }

            private void registerReceiver() {
                broadcastModifyDialog = new BroadcastModifyDialog();
                LocalBroadcastManager
                        .getInstance(getActivity())
                        .registerReceiver(
                                broadcastModifyDialog,
                                new IntentFilter(
                                        ModifyTimetableDialog.BROADCAST_DIALOG));
            }

            private void createDialog(int position) {
                DialogFragment dialogFragment = new ModifyTimetableDialog();

                Bundle args = new Bundle();
                args.putInt(ARG_DAY, CURRENT_DAY);
                args.putInt(ModifyTimetableDialog.ARG_CURRENT_TIME, classList
                        .get(position).getTime());
                args.putString(ModifyTimetableDialog.ARG_CURRENT_VENUE,
                        classList.get(position).getVenue());
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), "timetable");
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void updateThisFragment() throws Exception {
        classList = FullClassInfoHandler.getAllClassOfDay(CURRENT_DAY,
                getActivity());
        adapter.updateDataset(classList);

    }

    private class BroadcastModifyDialog extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ((TimetableActivity) getActivity()).mViewPager.getAdapter()
                        .notifyDataSetChanged();
                LocalBroadcastManager.getInstance(getActivity())
                        .unregisterReceiver(broadcastModifyDialog);
                broadcastModifyDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
