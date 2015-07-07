package com.blackMonster.webkiosk.crawler;

import com.blackMonster.webkiosk.M;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FetchPreRegSub extends StudentDetails {
	
	public FetchPreRegSub(SiteConnection cn) throws Exception {
		super(cn);
	}

	@Override
	public List<SubjectLink> getSubjectURL() throws Exception {
		List<SubjectLink> list = new ArrayList<SubjectLink>();
		getFromTable(list);
		
		try {
			getFromTable(list);	//for sem7
		} catch (Exception e) {
			M.log(TAG, "notsem7");
			e.printStackTrace();
		}
		
		response.getEntity().consumeContent();
		return list;

	}
	
	
	public void getFromTable(List<SubjectLink> list) throws Exception {
		String tmp;

		CrawlerUtils.reachToData(reader, "<thead>");
		// connect.reachToData(reader, "Click on Subject to Sort");
		CrawlerUtils.reachToData(reader, "</thead>");
		CrawlerUtils.reachToData(reader, "<tbody>");
		// M.log(TAG, "Reached to data");

		while (true) {
			tmp = reader.readLine();
			if (tmp == null)
				throw new BadHtmlSourceException();

			if (tmp.contains("</tbody>"))
				break;

			if (tmp.contains("<tr>")) {
				readRow(list);
			}
		}

	}
	@Override
	public String getMainUrl(SiteConnection cn) {
		return cn.siteUrl + "/StudentFiles/Academic/PRStudentView.jsp";
	}
	
	@Override
	public void readRow(List<SubjectLink> list) throws Exception {
		String tmp;
		SubjectLink sub = new SubjectLink();

		CrawlerUtils.readSingleData(connect.pattern1, reader);
		tmp = CrawlerUtils.readSingleData(connect.pattern1, reader);
	///	M.log("crawl", tmp);
		int i = CrawlerUtils.lastDash(tmp);
		sub.setName(CrawlerUtils.titleCase(tmp.substring(0, tmp.indexOf('(')).trim()));

		sub.setCode( "T" + (tmp.substring(tmp.indexOf('(')+1, tmp.indexOf(')') )).trim());

		
		
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
