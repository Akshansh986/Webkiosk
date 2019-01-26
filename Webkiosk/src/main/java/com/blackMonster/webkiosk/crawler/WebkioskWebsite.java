package com.blackMonster.webkiosk.crawler;


import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by akshansh on 09/07/15.
 */
public class WebkioskWebsite {

    public static String getSiteUrl(String colg) {
        if (colg.equals("J128")) return "https://webkiosk.jiit.ac.in";
        return "https://webkiosk." + colg + ".ac.in";
    }

    public static String getLoginUrl(String colg) {
        return getSiteUrl(colg) + "/CommonFiles/UserAction.jsp";
    }

    //parameters to be sent to webkiosk at time of login.
    public static void initiliseLoginDetails(List<NameValuePair> formparams, String colg, String enroll, String pass, String dob, String captcha) {
        formparams.add(new BasicNameValuePair("txtInst", "Institute"));
        formparams.add(new BasicNameValuePair("InstCode", colg.toUpperCase() + " "));
        formparams.add(new BasicNameValuePair("txtuType", "Member Type "));
        formparams.add(new BasicNameValuePair("UserType", "S"));
        formparams.add(new BasicNameValuePair("txtCode", "Enrollment No"));
        formparams.add(new BasicNameValuePair("MemberCode", enroll));
        formparams.add(new BasicNameValuePair("txtPin", "Password/Pin"));
        formparams.add(new BasicNameValuePair("Password", pass));
        formparams.add(new BasicNameValuePair("BTNSubmit", "Submit"));
        formparams.add(new BasicNameValuePair("DATE1", dob));
        formparams.add((new BasicNameValuePair("txtcap", captcha)));
    }
}
