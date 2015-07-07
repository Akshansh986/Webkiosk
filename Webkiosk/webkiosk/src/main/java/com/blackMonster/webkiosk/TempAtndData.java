package com.blackMonster.webkiosk;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.StudentDetails;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.model.SubjectLink;

import java.util.List;

public class TempAtndData {
	public static final int ERROR = -100;
	
	public static int storeData(List<SubjectLink> details, Context context) {
		int numberSubModifiedOrResult=0;
		AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);
				

		for (SubjectLink row : details) {
			int isModified;
			 SubjectLink tmp = new SubjectLink();
			int result = atndO.getSubjectLink(tmp,row.getCode());
			if (result == AttendenceOverviewTable.SUBJECT_CHANGED) 
				numberSubModifiedOrResult = AttendenceOverviewTable.SUBJECT_CHANGED;
			else {
				if (result == AttendenceOverviewTable.DONE) {
					if (tmp.getOverall()== row.getOverall() && tmp.getLect() == row.getLect() && tmp.getTute() == row.getTute() && tmp.getPract() == row.getPract()) isModified = 0;
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
		if (context.deleteDatabase(DbHelper.DB_NAME)){}
			//Log.d("tmpAtndData", "TMP Database deleted");
	}

}
