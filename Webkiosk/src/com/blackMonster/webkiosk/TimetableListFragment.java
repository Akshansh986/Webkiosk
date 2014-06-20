package com.blackMonster.webkiosk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackMonster.webkiosk.TimetableData.SingleClass;

public class TimetableListFragment extends ListFragment {
	public static final String ARG_DAY = "day";
	public static final String TAG = "timetableListFragment";

	List<SingleClass> classList;
	int CURRENT_DAY;

	MyAdapter adapter;
	BroadcastModifyDialog broadcastModifyDialog;
	String table;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		table = MainPrefs.getBatch(getActivity());
		classList = new ArrayList<SingleClass>();
		CURRENT_DAY = args.getInt(ARG_DAY);

		try {
			classList = TimetableData.getDayWiseClass(CURRENT_DAY, table,
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

		adapter = new MyAdapter(getActivity(), classList);
		setListAdapter(adapter);
		if (!TimetableDataHelper.databaseExists(getActivity()))
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

	private class MyAdapter extends ArrayAdapter<SingleClass> {
		Context context;
		List<SingleClass> values;

		public MyAdapter(Context context, List<SingleClass> objects) {
			super(context, R.layout.activity_timetable_row, objects);
			this.context = context;
			values = objects;

		}

		public void updateDataset(List<SingleClass> list) {
			values = list;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return values.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.activity_timetable_row,
					parent, false);

			SingleClass singleClass = values.get(position);

			setProgressCircle(singleClass, rowView);
			((TextView) rowView.findViewById(R.id.timetable_Sub_name))
					.setText(singleClass.getSubjectName());
			((TextView) rowView.findViewById(R.id.timetable_venue))
					.setText(singleClass.getVenue());
			((TextView) rowView.findViewById(R.id.timetable_class_time))
					.setText(TimetableData.getFormattedTime(singleClass
							.getTime()));

			if (!(TimetableData.showRecentUpdatedTag(getActivity()) && singleClass.isModified == 1))
				((TextView) rowView.findViewById(R.id.timetable_updated_tag))
						.setVisibility(View.GONE);
			higlightCurrentClass(singleClass, rowView);

			ProgressBar pb = ((ProgressBar) rowView
					.findViewById(R.id.timetable_attendence_progressBar));
			AtndOverviewActivity.setProgressBarColor(pb,
					singleClass.getOverallAttendence(), getActivity());

			if (singleClass.getOverallAttendence() == null) {
				((TextView) rowView.findViewById(R.id.timetable_attendence))
						.setText(AtndOverviewActivity.ATND_NA);
				pb.setProgress(0);
			} else {
				((TextView) rowView.findViewById(R.id.timetable_attendence))
						.setText(singleClass.getOverallAttendence().toString()
								+ "%");

				pb.setProgress(singleClass.getOverallAttendence());

			}

			return rowView;

		}

		private void setProgressCircle(SingleClass singleClass, View view) {
			int t2;
			if (TimetableData.isOfTwoHr(singleClass.getClassType(),
					singleClass.getSubjectCode()))
				t2 = singleClass.getTime() + 2;
			else
				t2 = singleClass.getTime() + 1;

			((TimeLTP) view.findViewById(R.id.timetable_TimeLTP)).setParams(
					singleClass.getTime(), t2, singleClass.getClassType());

		}

		private void higlightCurrentClass(SingleClass singleClass, View rowView) {
			Calendar calender = Calendar.getInstance();
			boolean isCurrentClass = (calender.get(Calendar.HOUR_OF_DAY) == singleClass
					.getTime())
					|| (TimetableData.isOfTwoHr(singleClass.getClassType(),
							singleClass.getSubjectCode()) && calender
							.get(Calendar.HOUR_OF_DAY) == singleClass.getTime() + 1);

			if (calender.get(Calendar.DAY_OF_WEEK) == CURRENT_DAY
					&& isCurrentClass) {

				((RelativeLayout) rowView.findViewById(R.id.timetable_row))
						.setBackgroundColor(Color.rgb(216, 216, 216));

			}
		}

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

	public void updateThisFragment() throws Exception {
		classList = TimetableData.getDayWiseClass(CURRENT_DAY, table,
				getActivity());
		adapter.updateDataset(classList);

	}

}
