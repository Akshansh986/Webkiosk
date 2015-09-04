package com.blackMonster.webkiosk.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackMonster.webkiosk.databases.Tables.DetailedAttendanceTable;
import com.blackMonster.webkioskApp.R;

/**
 * Created by akshansh on 26/07/15.
 */
public class DetailedAttendanceAdapter extends CursorAdapter {

    public DetailedAttendanceAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.detailed_attendence_row,
                parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views


        if (cursor.getInt(cursor
                .getColumnIndex(DetailedAttendanceTable.C_STATUS)) == 1) {  //student present in class
            ((TextView) view.findViewById(R.id.detailed_atnd_leftbar))
                    .setBackgroundColor(Color.rgb(77, 184, 73));
            ((RelativeLayout) view.findViewById(R.id.detailed_atnd_root))
                    .setBackgroundColor(Color.rgb(249, 249, 249));

        } else {
            ((TextView) view.findViewById(R.id.detailed_atnd_leftbar))
                    .setBackgroundColor(Color.rgb(231, 70, 62));
            ((RelativeLayout) view.findViewById(R.id.detailed_atnd_root))
                    .setBackgroundColor(Color.rgb(252, 233, 232));

        }

        ((TextView) view.findViewById(R.id.detailed_atnd_date))
                .setText(cursor.getString(cursor
                        .getColumnIndex(DetailedAttendanceTable.C_DATE)));
        ((TextView) view.findViewById(R.id.detailed_atnd_teacher))
                .setText(cursor.getString(cursor
                        .getColumnIndex(DetailedAttendanceTable.C_ATTENDENCE_BY)));

    }
}
