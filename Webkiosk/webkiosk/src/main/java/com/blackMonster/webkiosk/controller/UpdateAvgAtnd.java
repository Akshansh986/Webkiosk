package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.SharedPrefs.RefreshServicePrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;

import java.util.List;

public class UpdateAvgAtnd {
	public static final int ERROR = -100;
	
	public static int update(List<SubjectInfo> newSubjectInfos, Context context) {
		int numberSubModifiedOrResult=0;
		AttendenceOverviewTable atndO = new AttendenceOverviewTable(context);
				

		for (SubjectInfo newSubjectInfo : newSubjectInfos) {

			int isModified;
			SubjectInfo oldSubjectInfo = new SubjectInfo(); //Fetched from local database.

			int result = atndO.getSubjectInfo(oldSubjectInfo, newSubjectInfo.getSubjectCode());
			if (result == AttendenceOverviewTable.SUBJECT_CHANGED) 
				numberSubModifiedOrResult = AttendenceOverviewTable.SUBJECT_CHANGED;
			else {
				if (result == AttendenceOverviewTable.DONE) {
					if (oldSubjectInfo.getOverall()== newSubjectInfo.getOverall() && oldSubjectInfo.getLect() == newSubjectInfo.getLect() && oldSubjectInfo.getTute() == newSubjectInfo.getTute() && oldSubjectInfo.getPract() == newSubjectInfo.getPract()) isModified = 0;
					else{
						isModified = 1;
						++numberSubModifiedOrResult;
					}
					
					atndO.update(newSubjectInfo, isModified);
				}
				else numberSubModifiedOrResult = ERROR;
			}
			
		
		}
		
		atndO.close();
		if (numberSubModifiedOrResult >= 0) RefreshServicePrefs.setAtndOverviewTimestamp(context);
		return numberSubModifiedOrResult;

	}
	
	public static int update(CrawlerDelegate crawlerDelegate, Context context) {
		int result;
		try {
			List<SubjectInfo> listt = crawlerDelegate.getSubjectInfoMain();
			result = update(listt, context);
		} catch (Exception e) {
			result = ERROR;
			e.printStackTrace();
		}
		
		return result;
	}

}
