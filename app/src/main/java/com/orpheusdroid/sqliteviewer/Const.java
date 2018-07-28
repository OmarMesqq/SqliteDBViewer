package com.orpheusdroid.sqliteviewer;

import android.os.Environment;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class Const {

    public static final String DBCustomQueryIntent = "db_custom_query";

    public static final String COUNTLY_USAGE_STATS_GROUP_NAME = "analytics_group";
    public static final String PREFS_REQUEST_ANALYTICS_PERMISSION = "request_analytics_permission";

    public static final int EXTDIR_REQUEST_CODE = 1000;
    public static final String TAG = "SQLiteViewer";
    public static final String FILEMANAGER_PREFS = "File_Manager";
    public static final String FILEMANAGER_PREFS_LAST_LOCATION = "last_location";
    public static final String LAST_CUSTOM_QUERY_PREFS = "last_custom_query";
    public static final String DBPathIntent = "db_path";
    public static final String DBTableNameIntent = "db_table_name";
    public static final File EXTERNAL_STORAGE_DIR = new File(
            Environment.getExternalStorageDirectory(), "SQLiteViewer");

    public static final File CSV_EXPORT_DIR = new File(EXTERNAL_STORAGE_DIR, "CSV");

    public static String getColumnDataType(int type) {
        switch (type) {
            case 1:
                return "INTEGER";
            case 2:
                return "FLOAT";
            case 3:
                return "STRING";
            case 4:
                return "BLOB";
            default:
                return "null";
        }
    }

    public static Charset getEncodingType(String encoding) {
        switch (encoding) {
            case "UTF-8":
                return StandardCharsets.UTF_8;
            case "UTF-16":
                return StandardCharsets.UTF_16;
            case "UTF-16LE":
                return StandardCharsets.UTF_16LE;
            case "UTF-16BE":
                return StandardCharsets.UTF_16BE;
            case "US-ASCII":
                return StandardCharsets.US_ASCII;
            case "ISO–8859–1":
                return StandardCharsets.ISO_8859_1;
            default:
                return StandardCharsets.UTF_8;
        }
    }
}
