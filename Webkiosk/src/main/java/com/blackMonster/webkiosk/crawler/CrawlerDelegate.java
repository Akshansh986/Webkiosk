package com.blackMonster.webkiosk.crawler;

import android.content.Context;

import com.blackMonster.webkiosk.crawler.Model.CrawlerSubjectInfo;
import com.blackMonster.webkiosk.crawler.Model.DetailedAttendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
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

    public void reset() {
        if (siteLogin != null) siteLogin.close();
        siteLogin = null;
    }


    SubjectAndStudentDetailsMain subjectAndStudentDetailsMain = null;

    /**
     * SubCode in SubjectAttendance is concatenated with "T"
     * "-1" for field where attendance is not available.
     */
    public List<SubjectAttendance> getSubjectAttendanceMain() throws Exception {
        if (subjectAndStudentDetailsMain == null)
            subjectAndStudentDetailsMain = new SubjectAndStudentDetailsMain(siteLogin.getConnection(), colg);


        return (List<SubjectAttendance>)(List<?>)subjectAndStudentDetailsMain.getSubjectInfo();
    }

    public String getStudentName() throws Exception {
        if (subjectAndStudentDetailsMain == null)
            subjectAndStudentDetailsMain = new SubjectAndStudentDetailsMain(siteLogin.getConnection(), colg);
        return subjectAndStudentDetailsMain.getStudentName();
    }

    /**
     * here average attendance data is kept "-1" i.e overall, lect, tute, pract.
     * SubCode in SubjectAttendance is concatenated with "T"
     * @return SubjectInfo
     * @throws Exception
     */
    public List<SubjectAttendance> getSubjectAttendanceFromPreReg() throws  Exception{
        return (List<SubjectAttendance>)(List<?>)new SubjectDetailsFromPreReg(siteLogin.getConnection(),colg).getSubjectInfo();
    }

    /**
     * here average attendance data is kept "-1" i.e overall, lect, tute, pract.
     * SubCode in SubjectAttendance is concatenated with "T"
     * @return SubjectInfo
     * @throws Exception
     */
    public List<SubjectAttendance> getSubjectAttendanceFromSubRegistered() throws  Exception{
        return (List<SubjectAttendance>)(List<?>)new SubjectDetailsFromSubReg(siteLogin.getConnection(),colg).getSubjectInfo();
    }

    public List<DetailedAttendance> getDetailedAttendance(String subCode) throws Exception{

        List<CrawlerSubjectInfo> crawlerSubInfos = (List<CrawlerSubjectInfo>)(List<?>) getSubjectAttendanceMain();

        CrawlerSubjectInfo crawlerSubInfo = findSubjectInfo(crawlerSubInfos, subCode);

        if (crawlerSubInfo == null || crawlerSubInfo.getLink() == null) return null;

        return new FetchDetailedAttendence(siteLogin.getConnection(),
                crawlerSubInfo.getLink(),crawlerSubInfo.isNotLab()).getAttendance();

    }


    public  List<DS_SP> getDateSheetSeatingPlan() throws Exception
    {
       return DSSPFetch.getData(siteLogin.getConnection(), colg, context);
    }



    private CrawlerSubjectInfo findSubjectInfo(List<CrawlerSubjectInfo> crawlerSubInfos, String subCode) {

        for (CrawlerSubjectInfo crawlerSubInfo : crawlerSubInfos) {

            if (crawlerSubInfo.getSubjectCode().equals(subCode)) return crawlerSubInfo;
        }

        return  null;
    }




}
