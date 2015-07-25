package com.blackMonster.webkiosk.Timetable;

import android.content.Context;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.controller.CreateDatabase;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.Model.SubjectAttendance;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.Tables.TimetableTable;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimetableCreateRefresh {
    public static final String TAG = "Timetable";

    public static final int ERROR_BATCH_UNAVAILABLE = -5;
    public static final int DONE = -123;
    public static final int TRANSFER_FOUND_DONE = -31;
    public static final int ERROR_DB_UNAVAILABLE = -4;
    public static final int ERROR_UNKNOWN = -3;
    public static final int ERROR_CONNECTION = -2;


    public static int createDatabase(List<SubjectAttendance> subjectLink,
                                     String colg, String enroll, String batch, Context context) {
        M.log("Timetable", "creartedatabse");
        int result;

        try {
            String ttFileName = getTimetableFileName(subjectLink, colg, context);

            if (ttFileName == null)
                result = DONE; // TIMETABLE NOT AVAILABLE
            else {
                M.log("Timetable", ttFileName);
                String newFileName = handleTimetableTransfers(ttFileName, colg,
                        enroll, batch, context);
                result = createTimetableDatabase(newFileName, colg, enroll, batch,
                        context);
                if (result == DONE && transferFound(ttFileName, newFileName))
                    result = TRANSFER_FOUND_DONE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ERROR_UNKNOWN;
        }

        return result;
    }

    public static void refresh(Context context) {

        String colg, enroll, batch, fileName;
        colg = MainPrefs.getColg(context);
        enroll = MainPrefs.getEnroll(context);
        batch = MainPrefs.getBatch(context);
        fileName = MainPrefs.getOnlineTimetableFileName(context);
        try {
            if (TimetableDbHelper.databaseExists(colg, enroll, batch,
                    fileName, context)) {
                M.log(TAG,
                        "imetableDataHelper.databaseExists(colg, enroll, batch, fileName, context)");
                String newFilename = handleTimetableTransfers(fileName, colg,
                        enroll, batch, context);
                if (transferFound(fileName, newFilename)) {
                    M.log(TAG, "transferFound(fileName, newFilename)");
                    CrawlerDelegate cd = new CrawlerDelegate(context);
                    cd.login(MainPrefs.getColg(context),
                            MainPrefs.getEnroll(context), MainPrefs.getPassword(context));
                    CreateDatabase.createFillTempAtndOverviewFromPreregSub(cd, context);
                    deleteTimetableDb(context);
                    createTimetableDatabase(newFilename, colg, enroll, batch,
                            context);
                }
            } else {
                List<SubjectAttendance> tmp = (List<SubjectAttendance>) (List<?>) (new AttendenceOverviewTable(context)).getAllSubjectAttendance();
                createDatabase(tmp, colg, enroll, batch,context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleteTimetableDb(Context context) {
        String oldDbName = TimetableDbHelper.getDbNameThroughPrefs(context);
        TimetableDbHelper.shutdown();
        if (context.deleteDatabase(oldDbName)) {
            M.log(TAG, "database deleted");
            MainPrefs.setOnlineTimetableFileName(context, "NULL");
        }

    }

    public static boolean isError(int result) {
        return result == ERROR_BATCH_UNAVAILABLE
                || result == ERROR_UNKNOWN
                || result == ERROR_CONNECTION;
    }


    private static boolean transferFound(String f1, String f2) {
        if (f1 == null || f2 == null)
            return false;
        return !f1.equals(f2);
    }

    private static String handleTimetableTransfers(String ttFileName,
                                                   String colg, String enroll, String batch, Context context)
            throws Exception {
        M.log("Timetable", "handleTimetableTransfers");

        String finalFileName = ttFileName;
        BufferedReader transferList = FetchFromServer.getTransferList(colg,
                context);

        String newTtFileName = findTransfers(ttFileName, transferList);
        closeReader(transferList);
        if (newTtFileName != null) {
            finalFileName = newTtFileName;
            // createFillTempAtndOverviewFromPreregSub(context);

        }
        // /M.log("timetable", finalFileName);
        return finalFileName;

    }

    private static int createTimetableDatabase(String fileName, String colg,
                                               String enroll, String batch, Context context) {

        // /M.log(TAG, "loadTimetable");
        int result;

        if (!TimetableDbHelper.databaseExists(colg, enroll, batch, fileName,
                context)) {

            List<String> timetableDataList = new ArrayList<String>();
            result = FetchFromServer.getDataBundle(colg, fileName, batch,
                    timetableDataList, context);

            if (result == DONE) {
                TimetableTable.createDb(colg, fileName, batch, enroll, timetableDataList,
                        context);
                result = DONE;
            }


        } else {
            MainPrefs.setOnlineTimetableFileName(context, fileName);
            MainPrefs.setTimetableModified(context);
            result = DONE;

        }
        // /M.log(TAG, "load timetable  result : " + result);
//        if (result == FetchFromServer.DONE)
//            result = DONE;
        return result;
    }

    private static String getTimetableFileName(List<SubjectAttendance> subjectLink,
                                               String colg, Context context) throws Exception {
        // /M.log("Timetable", "getTimetableFileName");
        BufferedReader subCodeList = FetchFromServer.getSubcodeList(colg,
                context);
        String result = findMatch(subjectLink, subCodeList);
        closeReader(subCodeList);
        return result;

    }

    private static String findTransfers(String ttFileName,
                                        BufferedReader transferList) {
        // /M.log("Timetable", "findTransfers");

        try {
            if (!transferDatabaseExist(transferList))
                return null;

            while (true) {
                String line = transferList.readLine();
                if (line == null)
                    break;
                if (line.replaceAll("\\s", "").equals(""))
                    continue;
                if (line.substring(0, line.indexOf('$')).contains(ttFileName))
                    return line.substring(line.indexOf('$') + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String findMatch(List<SubjectAttendance> subjectLink,
                                    BufferedReader subCodeList) {
        // /M.log("Timetable", "findMatch");

        if (subCodeList == null || subjectLink == null)
            return null;
        int size = subjectLink.size();

        int i;
        String line;
        try {
            if (!subCodeDatabaseExist(subCodeList))
                return null;
            while (true) {
                line = subCodeList.readLine();
                if (line == null)
                    break;
                if (line.replaceAll("\\s", "").equals(""))
                    continue;
                // / M.log("Timetable", line);

                int matched = 0;
                for (i = 0; i < size; ++i) {
                    if (line.contains(subjectLink.get(i).getSubjectCode().substring(1)))
                        ++matched;
                }

                if (matched >= (size / 2 + 1)) {
                    return line.substring(line.indexOf('$') + 1);
                }

            }
        } catch (Exception e) {
        }
        return null;
    }

    private static boolean transferDatabaseExist(BufferedReader transferList)
            throws IOException {
        if (transferList == null)
            return false;
        return transferList.readLine().contains("transfers");
    }

    private static boolean subCodeDatabaseExist(BufferedReader subCodeList)
            throws IOException {
        return subCodeList.readLine().contains("subjectCodes");
    }

    private static void closeReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
