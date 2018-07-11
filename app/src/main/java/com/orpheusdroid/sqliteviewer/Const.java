package com.orpheusdroid.sqliteviewer;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class Const {

    public static final String DBCustomQueryIntent = "db_custom_query";

    public static final int EXTDIR_REQUEST_CODE = 1000;
    public static final String TAG = "SQLiteViewer";
    public static final String FILEMANAGER_PREFS = "File_Manager";
    public static final String FILEMANAGER_PREFS_LAST_LOCATION = "last_location";
    public static final String DBPathIntent = "db_path";
    public static final String DBTableNameIntent = "db_table_name";

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
}
