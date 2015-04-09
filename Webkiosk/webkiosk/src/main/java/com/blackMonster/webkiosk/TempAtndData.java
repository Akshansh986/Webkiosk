package com.blackMonster.webkiosk;

import java.util.List;

import android.content.Context;

import com.blackMonster.webkiosk.crawler.StudentDetails;
import com.blackMonster.webkiosk.databases.AttendenceData;
import com.blackMonster.webkiosk.databases.AttendenceData.AttendenceOverviewTable;
import com.blackMonster.webkiosk.crawler.StudentDetails.SubjectLink;

public class TempAtndData {
	public static final int ERROR = -100;
	
	public static int storeData(List<SubjectLink> details, Context context) {
		int numberSubModifiedOrResult=0;
		AttendenceOverviewTable atndO = AttendenceData.getInstance(context).new AttendenceOverviewTable();
				

		for (SubjectLink row : details) {
			int isModified;
			 SubjectLink tmp = new SubjectLink();
			int result = atndO.getSubjectLink(tmp,row.code);
			if (result == AttendenceOverviewTable.SUBJECT_CHANGED) 
				numberSubModifiedOrResult = AttendenceOverviewTable.SUBJECT_CHANGED;
			else {
				if (result == AttendenceOverviewTable.DONE) {
					if (tmp.overall== row.overall && tmp.lect == row.lect && tmp.tute == row.tute && tmp.pract == row.pract) isModified = 0;
					else{
						isModified = 1;
						++numberSubModifiedOrResult;
					}
					
					atndO.update(row, isModified);
				}
				else numberSubModifiedOrResult = ERROR;
			}
			
		
		}
		
		atndO.close();
		if (numberSubModifiedOrResult >= 0) RefreshServicePrefs.setAtndOverviewTimestamp(context);
		return numberSubModifiedOrResult;

	}
	
	public static int storeData(Context context) {
		int result;
		try {
			StudentDetails student = new StudentDetails(
					CreateDatabase.getWaPP(context).connect);
			List<SubjectLink> listt = student.getSubjectURL();
			result = storeData(listt, context);
		} catch (Exception e) {
			result = ERROR;
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void deleteDatabase(Context context) {
		if (context.deleteDatabase(AttendenceData.DB_NAME)){}
			//Log.d("tmpAtndData", "TMP Database deleted");
	}

}
