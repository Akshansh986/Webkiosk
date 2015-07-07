package com.blackMonster.webkiosk.databases;

import android.content.Context;

//close reader object;
public class AttendenceData {
	static final String TAG = "AttendenceData";
	public static final String DB_NAME = "attendence.db";

	Context context;

	private static AttendenceData atndDataInstance = null;

	public static AttendenceData getInstance(Context context) {
		if (atndDataInstance == null) {
			atndDataInstance = new AttendenceData(
					context.getApplicationContext());
		}
		return atndDataInstance;
	}

	private AttendenceData(Context context) {
		this.context = context;

	}

	public void close() {
		context = null;
		atndDataInstance = null;
	}

	public static boolean isLab(String subCode, Context context) {
		return (AttendenceData.getInstance(context).new SubjectLinkTable().getLTP(subCode)) == 0;
	}

}
