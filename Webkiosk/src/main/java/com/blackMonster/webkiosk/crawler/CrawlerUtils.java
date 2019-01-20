package com.blackMonster.webkiosk.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by akshansh on 07/07/15.
 */
public class CrawlerUtils {
    public static Pattern pattern1 = Pattern.compile(">([^<>]+)<");

    public static String reachToData(BufferedReader reader, String tag)
            throws BadHtmlSourceException, IOException {
        String tmp;
        if (tag!= null) tag = tag.toUpperCase();
        while (true) {
            tmp = reader.readLine();
            if (tmp == null)
                throw new BadHtmlSourceException();

            if (tmp.toUpperCase().contains(tag))
                return tmp;

        }
    }

    public static String getInnerHtml(String line) throws BadHtmlSourceException {
        Matcher matcher = pattern1.matcher(line);
        if (matcher.find()) {
            return line.substring(matcher.start() + 1, matcher.end() - 1);
        }
        throw new BadHtmlSourceException();
    }

    //Read single data from a row in a html table. Returns String if data found, BadHtmlSourceException() if eof(HTML) is reached or </tr> is founded;
    public static String readSingleData(Pattern pattern, BufferedReader reader) throws IOException, BadHtmlSourceException {
        String tmp;
        while (true) {
            tmp = reader.readLine();
            //M.log(tmp, tmp);
            Matcher matcher = pattern.matcher(tmp);

            if (matcher.find()) {
                return tmp.substring(matcher.start() + 1, matcher.end() - 1);
            }

            if (tmp == null || tmp.contains("</tr>"))
                throw new BadHtmlSourceException();

        }
    }

    public static String titleCase(String str) {
        char[] tmp = str.toLowerCase().toCharArray();
        int flag = 0;
        tmp[0] = Character.toUpperCase(tmp[0]);
        for (int i = 1; i < tmp.length; ++i) {
            if (Character.isWhitespace(tmp[i]))
                flag = 1;
            else if (flag == 1) {
                tmp[i] = Character.toUpperCase(tmp[i]);
                flag = 0;
            }
        }
        return String.valueOf(tmp);
    }

    public static int lastDash(String str) {
        int n = str.length();
        int tmp = 0;
        for (int i = 0; i < n; ++i)
            if (str.charAt(i) == '-')
                tmp = i;
        return tmp;
    }

}
