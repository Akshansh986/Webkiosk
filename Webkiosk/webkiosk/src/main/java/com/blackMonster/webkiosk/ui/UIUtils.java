package com.blackMonster.webkiosk.ui;

import android.content.Context;
import android.widget.ProgressBar;

import com.blackMonster.webkiosk.SharedPrefs.RefreshDBPrefs;
import com.blackMonster.webkioskApp.R;

/**
 * Created by akshansh on 26/07/15.
 */
public class UIUtils {
    public static final String ATND_NA = "NA";
    public static final int ATTENDENCE_GOOD = 80;
    public static final int ATTENDENCE_AVG = 70;

    public static void setProgressBarColor(ProgressBar pb, Integer attendence,
                                           Context context) {
        if (attendence == null || attendence < 0) attendence = 0;

        if (attendence >= ATTENDENCE_GOOD)
            pb.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.custom_progressbar_green));
        else if (attendence >= ATTENDENCE_AVG)
            pb.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.custom_progressbar_orange));
        else
            pb.setProgressDrawable(context.getResources().getDrawable(
                    R.drawable.custom_progressbar_red));

    }

    /**
     * Checks is attendance is available or not.
     * @param context
     * @return
     */
    public static boolean canViewAttendance(Context context) {
        return  !(RefreshDBPrefs.getAvgAttendanceRefreshTimeStamp(context) == RefreshDBPrefs.DEFAULT_TIMESTAMP);
    }
}
