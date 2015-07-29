package com.blackMonster.webkiosk.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.controller.Timetable.TimetableUtils;
import com.blackMonster.webkiosk.controller.model.SingleClass;
import com.blackMonster.webkiosk.WebkioskApp;
import com.blackMonster.webkiosk.ui.TimeLTP;
import com.blackMonster.webkiosk.ui.TimetableListFragment;
import com.blackMonster.webkiosk.ui.UIUtils;
import com.blackMonster.webkioskApp.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by akshansh on 26/07/15.
 */
public class SingleDayTimetableAdapter extends ArrayAdapter<SingleClass> {
    private TimetableListFragment timetableListFragment;
    Context context;
    List<SingleClass> values;

    public SingleDayTimetableAdapter(TimetableListFragment timetableListFragment, Context context, List<SingleClass> objects) {
        super(context, R.layout.activity_timetable_row, objects);
        this.timetableListFragment = timetableListFragment;
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
                .setText(TimetableUtils.getFormattedTime(singleClass
                        .getTime()));

        if (!(RefreshServicePrefs.getRecentlyUpdatedTagVisibility(context) && singleClass.isAtndModified() == 1))
            ((TextView) rowView.findViewById(R.id.timetable_updated_tag))
                    .setVisibility(View.GONE);
        higlightCurrentClass(singleClass, rowView);

        ProgressBar pb = ((ProgressBar) rowView
                .findViewById(R.id.timetable_attendence_progressBar));
        UIUtils.setProgressBarColor(pb,
                singleClass.getOverallAttendence(), timetableListFragment.getActivity());

        if (singleClass.getOverallAttendence() == -1) {
            ((TextView) rowView.findViewById(R.id.timetable_attendence))
                    .setText(WebkioskApp.ATND_NA);
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
        if (TimetableUtils.isOfTwoHr(singleClass.getClassType(),
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
                || (TimetableUtils.isOfTwoHr(singleClass.getClassType(),
                singleClass.getSubjectCode()) && calender
                .get(Calendar.HOUR_OF_DAY) == singleClass.getTime() + 1);

        if (calender.get(Calendar.DAY_OF_WEEK) == timetableListFragment.CURRENT_DAY
                && isCurrentClass) {

            ((RelativeLayout) rowView.findViewById(R.id.timetable_row))
                    .setBackgroundColor(Color.rgb(216, 216, 216));

        }
    }

}
