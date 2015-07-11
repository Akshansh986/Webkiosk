package com.blackMonster.webkiosk;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.DbHelper;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;

import java.util.List;

public class TempAtndData {
	public static final int ERROR = -100;
	
	public static int storeData(List<SubjectInfo> details, Context context) {
		int numberSubModifiedOrResult=0;
		AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);
				

		for (SubjectInfo row : details) {
			int isModified;
			 SubjectInfo tmp = new SubjectInfo();
			int result = atndO.getSubjectInfo(tmp, row.getSubjectCode());
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
	
	public static int storeData(CrawlerDelegate crawlerDelegate, Context context) {
		int result;
		try {
			List<SubjectInfo> listt = crawlerDelegate.getSubjectInfoMain();
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
