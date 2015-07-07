package com.blackMonster.webkiosk.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FetchSubReg extends StudentDetails {
	
	public FetchSubReg(SiteConnection cn) throws Exception {
		super(cn);
	}


	@Override
	public List<SubjectLink> getSubjectURL() throws Exception {
		List<SubjectLink> list = new ArrayList<SubjectLink>();
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
	
	
	public void getFromTable(List<SubjectLink> list) throws Exception {
		String tmp;

		CrawlerUtils.reachToData(reader, "/form");
		CrawlerUtils.reachToData(reader, "/tr");

		// connect.reachToData(reader, "Click on Subject to Sort");
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
	public String getMainUrl(SiteConnection cn) {
		return cn.siteUrl + "/StudentFiles/Academic/StudSubjectTaken.jsp";
	}
	
	@Override
	public void readRow(List<SubjectLink> list) throws Exception {
		String tmp;
		SubjectLink sub = new SubjectLink();

		tmp = CrawlerUtils.readSingleData(connect.pattern1, reader);
		if (tmp.toUpperCase().contains("CREDIT")) return;
		tmp = CrawlerUtils.readSingleData(connect.pattern1, reader);
		if (tmp.toUpperCase().contains("CREDIT")) return;

	///	M.log("crawl", tmp);
		int i = CrawlerUtils.lastDash(tmp);
		sub.setName(CrawlerUtils.titleCase(tmp.substring(0, tmp.indexOf('(')).trim()));

		sub.setCode("T" + (tmp.substring(tmp.indexOf('(')+1, tmp.indexOf(')') )).trim());



		sub.setLink("");
		sub.setLect(-1);
		sub.setTute(-1);
		sub.setPract(-1);
		sub.setOverall(-1);
		sub.setLTP(-1);
		list.add(sub);
	}
	
	@Override
	public void setName() throws BadHtmlSourceException, IOException {
	
	
	}

}
