package com.blackMonster.webkiosk.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.WebkioskApp;
import com.blackMonster.webkiosk.databases.AttendanceUtils;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.ui.AtndOverviewActivity;
import com.blackMonster.webkiosk.ui.UIUtils;
import com.blackMonster.webkioskApp.R;

/**
 * Created by akshansh on 26/07/15.
 */
public class AtndOverviewAdapter extends CursorAdapter {

    private AtndOverviewActivity atndOverviewActivity;

    public AtndOverviewAdapter(AtndOverviewActivity atndOverviewActivity, Context context, Cursor c) {
        super(context, c);
        this.atndOverviewActivity = atndOverviewActivity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.attendence_overview_row,
                parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views
        // TextView subName =
        if (cursor == null) return;
        String subName = cursor.getString(cursor
                .getColumnIndex(AttendenceOverviewTable.C_NAME));
        String subCode = cursor.getString(cursor
                .getColumnIndex(AttendenceOverviewTable.C_CODE));
        setTextView(R.id.atndo_Sub_name, subName, view);


        int pbProgress;

        if (AttendanceUtils.isLab(subCode, context)) {
            pbProgress = cursor.getInt(cursor
                    .getColumnIndex(AttendenceOverviewTable.C_PRACTICAL));
            ((TextView) view.findViewById(R.id.atndo_lect)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.atndo_tute)).setVisibility(View.GONE);
        } else {
            pbProgress = cursor.getInt(cursor
                    .getColumnIndex(AttendenceOverviewTable.C_OVERALL));

            setTextView(R.id.atndo_lect, "L : " + atndToString(cursor.getInt(cursor
                    .getColumnIndex(AttendenceOverviewTable.C_LECTURE))), view);
            setTextView(R.id.atndo_tute, "T : " + atndToString(cursor.getInt(cursor
                    .getColumnIndex(AttendenceOverviewTable.C_TUTORIAL))), view);

        }

        setTextView(R.id.atndo_overall_attendence, atndToString(pbProgress), view);
        if (pbProgress == -1)   //In case attendance is not available.
            pbProgress = 0;
        ProgressBar pbar = ((ProgressBar) view.findViewById(R.id.atndo_progressBar));
        Rect bounds = pbar.getProgressDrawable().getBounds(); //Save the drawable bound
        UIUtils.setProgressBarColor(pbar, pbProgress, atndOverviewActivity);
        pbar.setProgress(1);
        pbar.setProgress(pbProgress);
        pbar.getProgressDrawable().setBounds(bounds);
        if (RefreshServicePrefs.getRecentlyUpdatedTagVisibility(context) &&
                cursor.getInt(cursor.getColumnIndex(AttendenceOverviewTable.C_IS_MODIFIED)) == 1)
            ((TextView) view.findViewById(R.id.atndo_updated_tag)).setVisibility(View.VISIBLE);
        else
            ((TextView) view.findViewById(R.id.atndo_updated_tag)).setVisibility(View.INVISIBLE);
    }


    private String atndToString(int x) {
        if (x == -1)
            return WebkioskApp.ATND_NA;
        else
            return x + "%";
    }

    private void setTextView(int id, String text, View view) {
        if (text == null) text = WebkioskApp.ATND_NA;
        TextView tview = ((TextView) view.findViewById(id));
        tview.setText(text);
        tview.setVisibility(View.VISIBLE);
    }

}
