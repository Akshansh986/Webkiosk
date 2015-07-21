package com.blackMonster.webkiosk.controller;

import android.content.Context;

import com.blackMonster.webkiosk.M;
import com.blackMonster.webkiosk.SharedPrefs.MainPrefs;
import com.blackMonster.webkiosk.crawler.CrawlerDelegate;
import com.blackMonster.webkiosk.crawler.TimetableFetch;
import com.blackMonster.webkiosk.databases.Tables.AttendenceOverviewTable;
import com.blackMonster.webkiosk.databases.TimetableData;
import com.blackMonster.webkiosk.databases.TimetableDbHelper;
import com.blackMonster.webkiosk.crawler.Model.SubjectInfo;
import com.blackMonster.webkiosk.ui.ModifyTimetableDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Timetable {
    public static final int ERROR_BATCH_UNAVAILABLE = -5;
    public static final int DONE = -123;
    public static final int TRANSFER_FOUND_DONE = -31;
    public static final String TAG = "Timetable";

    public static boolean isError(int result) {
        return result == ERROR_BATCH_UNAVAILABLE
                || result == TimetableFetch.ERROR_UNKNOWN
                || result == TimetableFetch.ERROR_CONNECTION;
    }

    public static void handleChangesRefresh(Context context) {
        M.log(TAG, "handleChangesRefresh");

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
                    //TODO complete jugad
                    CrawlerDelegate cd = new CrawlerDelegate(context);
                    cd.login(MainPrefs.getColg(context),
                            MainPrefs.getEnroll(context), MainPrefs.getPassword(context));
                    CreateDatabase.createFillTempAtndOverviewFromPreregSub(cd, context);
                    deleteTimetableDb(context);
                    createTimetableDatabase(newFilename, colg, enroll, batch,
                            context);
                }
            } else
                createDatabase(
                        new AttendenceOverviewTable(context)
                                .getAllSubjectInfo(), colg, enroll, batch,
                        context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTimetableDb(Context context) {
        String oldDbName = TimetableDbHelper.getDbNameThroughPrefs(context);
        TimetableDbHelper.nullifyInstance();
        if (context.deleteDatabase(oldDbName)) {
            M.log(TAG, "database deleted");
            MainPrefs.setOnlineTimetableFileName(context, "NULL");
        }

    }

    public static int createDatabase(List<SubjectInfo> subjectLink,
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
            result = TimetableFetch.ERROR_UNKNOWN;
        }

        return result;
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
        BufferedReader transferList = TimetableFetch.getTransferList(colg,
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
            // / M.log(TAG, "timetable doesnt exist");
            result = TimetableData.createDb(colg, fileName, batch, enroll,
                    context);
        } else {
            MainPrefs.setOnlineTimetableFileName(context, fileName);
            context.getSharedPreferences(MainPrefs.PREFS_NAME, 0).edit()
                    .putBoolean(ModifyTimetableDialog.IS_MODIFIED, true)
                    .commit();
            result = TimetableFetch.DONE;

        }
        // /M.log(TAG, "load timetable  result : " + result);
        if (result == TimetableFetch.DONE)
            result = DONE;
        return result;
    }

    private static String getTimetableFileName(List<SubjectInfo> subjectLink,
                                               String colg, Context context) throws Exception {
        // /M.log("Timetable", "getTimetableFileName");
        BufferedReader subCodeList = TimetableFetch.getSubcodeList(colg,
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

    private static String findMatch(List<SubjectInfo> subjectLink,
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
