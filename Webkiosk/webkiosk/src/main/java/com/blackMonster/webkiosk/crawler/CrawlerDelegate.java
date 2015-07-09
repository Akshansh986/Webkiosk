package com.blackMonster.webkiosk.crawler;

import android.content.Context;

import com.blackMonster.webkiosk.crawler.Model.Attendance;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.crawler.dateSheet.DSSPFetch;

import java.util.List;

/**
 * Created by akshansh on 07/07/15.
 */
public class CrawlerDelegate {
    private String colg, enroll, pass;
    private Context context;


    private SiteLogin siteLogin;

    public CrawlerDelegate(String colg, String enroll, String pass, Context context) {
        this.colg = colg;
        this.enroll = enroll;
        this.pass = pass;
        this.context = context;
    }


    public int login() {
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
        return subjectAndStudentDetailsMain.getSubjectInfo();
    }

    public String getStudentName() throws Exception {
        if (subjectAndStudentDetailsMain == null)
            subjectAndStudentDetailsMain = new SubjectAndStudentDetailsMain(siteLogin.getConnection(), colg);
        return subjectAndStudentDetailsMain.getStudentName();
    }

    public List<SubjectInfo> getSubjectInfoFromPreReg() throws  Exception{
        return new SubjectDetailsFromPreReg(siteLogin.getConnection(),colg).getSubjectInfo();
    }

    public List<SubjectInfo> getSubjectInfoFromSubRegistered() throws  Exception{
        return new SubjectDetailsFromSubReg(siteLogin.getConnection(),colg).getSubjectInfo();
    }

    public List<Attendance> getDetailedAttendance(String subCode) throws Exception{

        List<SubjectInfo> subjectInfos = getSubjectInfoMain();

        SubjectInfo subjectInfo = findSubjectInfo(subjectInfos, subCode);

        if (subjectInfo == null || subjectInfo.getLink() == null) return null;

        return new FetchDetailedAttendence(siteLogin.getConnection(),
                subjectInfo.getLink(),subjectInfo.getLTP()).getAttendance();

    }





    private SubjectInfo findSubjectInfo(List<SubjectInfo> subjectInfos, String subCode) {

        for (SubjectInfo subjectInfo : subjectInfos) {

            if (subjectInfo.getSubjectCode().equals(subCode)) return subjectInfo;
        }

        return  null;
    }

    public  List<DSSPFetch.DS_SP> getDateSheetSeatingPlan() throws Exception
    {
       return DSSPFetch.getData(siteLogin.getConnection(),colg,context);
    }



}
