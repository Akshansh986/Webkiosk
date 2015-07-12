package com.blackMonster.webkiosk.crawler;

import android.content.Context;

import com.blackMonster.webkiosk.crawler.Model.CrawlerSubInfo;
import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.crawler.dateSheet.DSSPFetch;
import com.blackMonster.webkiosk.crawler.dateSheet.DS_SP;

import java.util.List;

/**
 * Created by akshansh on 07/07/15.
 */
public class CrawlerDelegate {
    private String colg, enroll, pass;
    private Context context;


    private SiteLogin siteLogin;

    public CrawlerDelegate( Context context) {

        this.context = context;
    }


    public int login(String colg, String enroll, String pass) {
        this.colg = colg;
        this.enroll = enroll;
        this.pass = pass;
        siteLogin = new SiteLogin();
        return siteLogin.login(colg, enroll, pass, context);
    }

    public void logout() {
        if (siteLogin != null) siteLogin.close();
        siteLogin = null;
    }


    SubjectAndStudentDetailsMain subjectAndStudentDetailsMain = null;

    public List<SubjectInfo> getSubjectInfoMain() throws Exception {
        if (subjectAndStudentDetailsMain == null)
            subjectAndStudentDetailsMain = new SubjectAndStudentDetailsMain(siteLogin.getConnection(), colg);
        return (List<SubjectInfo>)(List<?>)subjectAndStudentDetailsMain.getSubjectInfo();
    }

    public String getStudentName() throws Exception {
        if (subjectAndStudentDetailsMain == null)
            subjectAndStudentDetailsMain = new SubjectAndStudentDetailsMain(siteLogin.getConnection(), colg);
        return subjectAndStudentDetailsMain.getStudentName();
    }

    /**
     * here average attendance data is kept "-1" i.e overall, lect, tute, pract.
     * @return SubjectInfo
     * @throws Exception
     */
    public List<SubjectInfo> getSubjectInfoFromPreReg() throws  Exception{
        return (List<SubjectInfo>)(List<?>)new SubjectDetailsFromPreReg(siteLogin.getConnection(),colg).getSubjectInfo();
    }

    /**
     * here average attendance data is kept "-1" i.e overall, lect, tute, pract.
     * @return SubjectInfo
     * @throws Exception
     */
    public List<SubjectInfo> getSubjectInfoFromSubRegistered() throws  Exception{
        return (List<SubjectInfo>)(List<?>)new SubjectDetailsFromSubReg(siteLogin.getConnection(),colg).getSubjectInfo();
    }

    public List<DetailedAttendance> getDetailedAttendance(String subCode) throws Exception{

        List<CrawlerSubInfo> crawlerSubInfos = (List<CrawlerSubInfo>)(List<?>)getSubjectInfoMain();

        CrawlerSubInfo crawlerSubInfo = findSubjectInfo(crawlerSubInfos, subCode);

        if (crawlerSubInfo == null || crawlerSubInfo.getLink() == null) return null;

        return new FetchDetailedAttendence(siteLogin.getConnection(),
                crawlerSubInfo.getLink(),crawlerSubInfo.isNotLab()).getAttendance();

    }


    public  List<DS_SP> getDateSheetSeatingPlan() throws Exception
    {
       return DSSPFetch.getData(siteLogin.getConnection(), colg, context);
    }



    private CrawlerSubInfo findSubjectInfo(List<CrawlerSubInfo> crawlerSubInfos, String subCode) {

        for (CrawlerSubInfo crawlerSubInfo : crawlerSubInfos) {

            if (crawlerSubInfo.getSubjectCode().equals(subCode)) return crawlerSubInfo;
        }

        return  null;
    }




}
