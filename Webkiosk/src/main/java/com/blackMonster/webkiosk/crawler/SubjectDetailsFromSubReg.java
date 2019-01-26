package com.blackMonster.webkiosk.crawler;

import com.blackMonster.webkiosk.crawler.Model.CrawlerSubjectInfo;


import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.client.HttpClient;

/**
 * Fetches data from  "Sub reg" page
 */
class SubjectDetailsFromSubReg extends AbstractSubjectDetails {
	
	SubjectDetailsFromSubReg(HttpClient siteConnection, String colg) throws Exception {
		super(siteConnection,colg);
	}


	@Override
	List<CrawlerSubjectInfo> fetchSubjectInfo() throws Exception {
		List<CrawlerSubjectInfo> list = new ArrayList<CrawlerSubjectInfo>();
		getFromTable(list);
/*		
		try {
			getFromTable(list);	//for sem7
		} catch (Exception e) {
			M.log(TAG, "notsem7");
			e.printStackTrace();
		}
*/		
		response.getEntity().consumeContent();
		return list;

	}
	
	
	private void getFromTable(List<CrawlerSubjectInfo> list) throws Exception {
		String tmp;

		CrawlerUtils.reachToData(reader, "/form");
		CrawlerUtils.reachToData(reader, "/tr");

		// siteConnection.reachToData(reader, "Click on Subject to Sort");
		//SiteConnection.reachToData(reader, "</thead>");
		//SiteConnection.reachToData(reader, "<tbody>");
		// M.log(TAG, "Reached to data");

		while (true) {
			tmp = reader.readLine();
			if (tmp == null)
				throw new BadHtmlSourceException();
			if (tmp.toUpperCase().contains("CREDIT")) 
				break;
			if (tmp.contains("/table"))
				break;

			if (tmp.contains("<tr>")) {
				readRow(list);
			}
		}

	}
	@Override
	String getMainUrl() {
		return WebkioskWebsite.getSiteUrl(colg) + "/StudentFiles/Academic/StudSubjectTaken.jsp";
	}
	
	@Override
	void readRow(List<CrawlerSubjectInfo> list) throws Exception {
		String tmp;
		CrawlerSubjectInfo sub = new CrawlerSubjectInfo();

		tmp = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
		if (tmp.toUpperCase().contains("CREDIT")) return;
		tmp = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
		if (tmp.toUpperCase().contains("CREDIT")) return;

	///	M.log("crawl", tmp);
		int i = CrawlerUtils.lastDash(tmp);
		sub.setName(CrawlerUtils.titleCase(tmp.substring(0, tmp.indexOf('(')).trim()));

		sub.setSubCode("T" + (tmp.substring(tmp.indexOf('(') + 1, tmp.indexOf(')'))).trim());



		sub.setLink("");
		sub.setLect(-1);
		sub.setTute(-1);
		sub.setPract(-1);
		sub.setOverall(-1);
		sub.setNotLab(-1);
		list.add(sub);
	}


}
