package com.blackMonster.webkiosk.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.crawler.dateSheet.DS_SP;
import com.blackMonster.webkiosk.databases.Tables.DSSPTable;
import com.blackMonster.webkioskApp.R;

import java.util.List;

public class ActivityDateSheet extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		M.log("ActivityDatesheet", "oncreate");
		initActionBar();
		showListView();

	}

	private void showListView() {

		activityContent.removeAllViews();
		activityContent.setPadding(dpToPx(10), 0, dpToPx(10), 0);
		activityContent.setBackgroundColor(Color.parseColor("#E9EAED"));

			MyAdapter adapter = new MyAdapter(this, DSSPTable.getDS(this));
			ListView listView = new ListView(this);
			listView.setAdapter(adapter);
			activityContent.addView(listView);
	

	}

	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	private class MyAdapter extends ArrayAdapter<DS_SP> {
		Context context;
		List<DS_SP> values;

		public MyAdapter(Context context, List<DS_SP> objects) {
			super(context, R.layout.activity_datesheet, objects);
			this.context = context;
			values = objects;

		}

		public void updateDataset(List<DS_SP> list) {
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
			View view = inflater.inflate(R.layout.activity_datesheet, parent,
					false);

			DS_SP dssp = values.get(position);

			if (!isPresentBefore(position)) {
				M.log("ads", "pos " + position);
				((TextView) view.findViewById(R.id.ds_header))
						.setText(dssp.sheetCode);
				((TextView) view.findViewById(R.id.ds_header))
						.setVisibility(View.VISIBLE);

			} else {
				M.log("ads", "pos11 " + position);
				((TextView) view.findViewById(R.id.ds_header))
						.setVisibility(View.GONE);
			}

			((TextView) view.findViewById(R.id.ds_Sub_name))
					.setText(dssp.course);
			((TextView) view.findViewById(R.id.ds_date)).setText(dssp.date);
			((TextView) view.findViewById(R.id.ds_time)).setText(dssp.time);

			if (dssp.roomNo == null)
				M.log("ads", "null");
			else
				M.log("ads", "not null");
			if (dssp.roomNo == null || dssp.roomNo.equals(""))
				((TextView) view.findViewById(R.id.ds_venue))
						.setVisibility(View.GONE);
			else
				((TextView) view.findViewById(R.id.ds_venue))
						.setText(dssp.roomNo);

			if (dssp.seatNo == null || dssp.seatNo.equals(""))
				((TextView) view.findViewById(R.id.ds_seatNo))
						.setVisibility(View.GONE);
			else
				((TextView) view.findViewById(R.id.ds_seatNo))
						.setText(dssp.seatNo);

			return view;

		}

		private boolean isPresentBefore(int position) {
			for (int i = 0; i < position; ++i)
				if (values.get(i).sheetCode
						.equals(values.get(position).sheetCode))
					return true;

			return false;
		}

	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Date Sheet");
		openDrawerWithIcon(true);

	}



	

	@Override
	public void inflateOnCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (TimetableDbHelper.databaseExists(this))
			inflater.inflate(R.menu.menu_without_refresh, menu);
		else
			inflater.inflate(R.menu.mainmenu, menu);
		// M.log(TAG, "oncreateoptinosmenu");
	}
	

	
	
	

}
