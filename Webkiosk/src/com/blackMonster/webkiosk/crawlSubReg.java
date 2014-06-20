package com.blackMonster.webkiosk;

import java.io.IOException;
import java.util.List;


public class crawlSubReg extends StudentDetails {
	
	public crawlSubReg(SiteConnection cn) throws Exception {
		super(cn);
	}

	

	@Override
	public String getMainUrl(SiteConnection cn) {
		return cn.siteUrl + "/StudentFiles/Academic/PRStudentView.jsp";
	}
	
	@Override
	public void readRow(List<SubjectLink> list) throws Exception {
		String tmp;
		SubjectLink sub = new SubjectLink();

		connect.readSingleData(connect.pattern1, reader);
		tmp = connect.readSingleData(connect.pattern1, reader);
	///	Log.d("crawl", tmp);
		int i = lastDash(tmp);
		sub.name = titleCase(tmp.substring(0,tmp.indexOf('(')).trim());

		sub.code = "T" + (tmp.substring(tmp.indexOf('(')+1, tmp.indexOf(')') )).trim();

		
		
		sub.link="";
		sub.lect=-1;
		sub.tute = -1;
		sub.pract = -1;
		sub.overall = -1;
		sub.LTP = -1;
		list.add(sub);
	}
	
	@Override
	public void setName() throws BadHtmlSourceException, IOException {
	
	
	}

}
