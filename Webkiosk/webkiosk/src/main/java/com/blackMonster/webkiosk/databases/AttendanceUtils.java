package com.blackMonster.webkiosk.databases;

import android.content.Context;

import com.blackMonster.webkiosk.databases.Tables.SubjectLinkTable;

//close reader object;
public class AttendanceUtils {
	static final String TAG = "AttendanceUtils";

	public static boolean isLab(String subCode, Context context) {
		return (new SubjectLinkTable(context).getLTP(subCode)) == 0;
	}

}
