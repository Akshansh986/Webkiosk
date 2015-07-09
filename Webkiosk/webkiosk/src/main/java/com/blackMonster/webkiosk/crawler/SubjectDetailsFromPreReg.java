package com.blackMonster.webkiosk.crawler;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.crawler.Model.CrawlerSubInfo;

import org.apache.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;


class SubjectDetailsFromPreReg extends AbstractSubjectDetails {
	private static final String TAG = "SubjectDetailsFromPreReg";

	SubjectDetailsFromPreReg(HttpClient siteConnection, String colg) throws Exception {
		super(siteConnection,colg);
	}

	@Override
	List<CrawlerSubInfo> fetchSubjectInfo() throws Exception {
		List<CrawlerSubInfo> list = new ArrayList<CrawlerSubInfo>();
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
	
	
	private void getFromTable(List<CrawlerSubInfo> list) throws Exception {
		String tmp;

		CrawlerUtils.reachToData(reader, "<thead>");
		// siteConnection.reachToData(reader, "Click on Subject to Sort");
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
	String getMainUrl() {
		return WebkioskWebsite.getSiteUrl(colg) + "/StudentFiles/Academic/PRStudentView.jsp";
	}
	
	@Override
	void readRow(List<CrawlerSubInfo> list) throws Exception {
		String tmp;
		CrawlerSubInfo sub = new CrawlerSubInfo();

		CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
		tmp = CrawlerUtils.readSingleData(CrawlerUtils.pattern1, reader);
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
	


}
