package com.blackMonster.webkiosk.dateSheet;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.blackMonster.webkiosk.crawler.SiteConnection;
import com.blackMonster.webkiosk.crawler.StudentDetails;
import com.blackMonster.webkiosk.dateSheet.FetchDateSheet.DateSheetRow;
import com.blackMonster.webkiosk.dateSheet.FetchSeatingPlan.SPlanRow;

public  class DSSPFetch {

	public static List<DS_SP> getData(SiteConnection connect, Context context) throws Exception
			 {

		List<SPlanRow> sp=null;
			sp = FetchSeatingPlan.getData(connect, context);
		
		
		List<DateSheetRow> ds=null;
			ds = FetchDateSheet.getData(connect, context);
		
		
		//if (sp==null) sp = new ArrayList<FetchSeatingPlan.SPlanRow>();
		//if (ds == null) ds = new ArrayList<FetchDateSheet.DateSheetRow>();
		
		List<DS_SP> dssp = merge(sp, ds);

		return dssp;
	}

	private static List<DS_SP> merge(List<SPlanRow> spList, List<DateSheetRow> dsList) {
		List<DS_SP> dsspList = new ArrayList<DSSPFetch.DS_SP>();
		
		for (DateSheetRow ds : dsList) {
			if (!addDsIfInSeatingPlan(ds, spList, dsspList))
				dsspList.add(new DS_SP(ds.sheetCode, ds.course, ds.date,
						ds.time, "", ""));
		}

		for (SPlanRow sp : spList)
			dsspList.add(new DS_SP(sp.sheetCode, sp.course, sp.dateTime, "",
					sp.roomNo, sp.seatNo));

		return dsspList;
	}

	private static boolean addDsIfInSeatingPlan(DateSheetRow ds,
			List<SPlanRow> spList, List<DS_SP> dsspList) {
		for (SPlanRow sp : spList) {
			if (sp.dateTime.contains(ds.date) && sp.dateTime.contains(ds.time)) {
				dsspList.add(new DS_SP(ds.sheetCode, ds.course, ds.date,
						ds.time, sp.roomNo, sp.seatNo));
				spList.remove(sp);
				return true;

			}
		}
		return false;

	}

	public static class DS_SP {
		String sheetCode;
		String course;
		String date;
		String time;
		String roomNo;
		String seatNo;

		public DS_SP(String sheetCode2, String course2, String date2,
				String time2, String roomNo2, String seatNo2) {

			sheetCode = sheetCode2;
			course = StudentDetails.titleCase(removeSubCode(course2));
			date = date2;
			time = time2;
			roomNo = roomNo2;
			seatNo = seatNo2;
		}

		private String removeSubCode(String str) {
			//return str.replaceAll(str.substring(str.indexOf('(') -1 , str.indexOf(')')  ), "").trim();
			return str.replaceAll("\\(\\S+\\)","");
		}
	}

}
