package com.blackMonster.webkiosk.crawler.dateSheet;

import android.content.Context;

import com.blackMonster.webkiosk.crawler.dateSheet.FetchSeatingPlan.SPlanRow;


import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.client.HttpClient;

public  class DSSPFetch {

	public static List<DS_SP> getData(HttpClient siteConnection, String colg, Context context) throws Exception
	{

		List<SPlanRow> sp=null;
		sp = FetchSeatingPlan.getData(siteConnection, colg, context);


		List<FetchDateSheet.DateSheetRow> ds=null;
		ds = FetchDateSheet.getData(siteConnection,colg, context);


		//if (sp==null) sp = new ArrayList<FetchSeatingPlan.SPlanRow>();
		//if (ds == null) ds = new ArrayList<FetchDateSheet.DateSheetRow>();

		List<DS_SP> dssp = merge(sp, ds);

		return dssp;
	}

	private static List<DS_SP> merge(List<SPlanRow> spList, List<FetchDateSheet.DateSheetRow> dsList) {
		List<DS_SP> dsspList = new ArrayList<DS_SP>();
		
		for (FetchDateSheet.DateSheetRow ds : dsList) {
			if (!addDsIfInSeatingPlan(ds, spList, dsspList))
				dsspList.add(new DS_SP(ds.sheetCode, ds.course, ds.date,
						ds.time, "", ""));
		}

		for (SPlanRow sp : spList)
			dsspList.add(new DS_SP(sp.sheetCode, sp.course, sp.dateTime, "",
					sp.roomNo, sp.seatNo));

		return dsspList;
	}

	private static boolean addDsIfInSeatingPlan(FetchDateSheet.DateSheetRow ds,
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

}
