package com.orpheusdroid.sqliteviewer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.orpheusdroid.sqliteviewer.Const;
import com.orpheusdroid.sqliteviewer.model.TabelModel.Cell;
import com.orpheusdroid.sqliteviewer.model.TabelModel.FieldModel;
import com.topjohnwu.superuser.ShellUtils;
import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuFileInputStream;
import com.topjohnwu.superuser.io.SuFileOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class DataBase {
    private static SQLiteDatabase _db = null;
    private static Context _cont;
    private static DataBase dbInstance;
    private boolean isDatabase = false;
    private boolean isRoot = false;
    private String _dbPath;
    private String originalDBPath;

    private DataBase() {
    }

    public static DataBase getInstance(Context context) {
        if (dbInstance != null)
            return dbInstance;
        _cont = context;
        return dbInstance = new DataBase();
    }

    public boolean setDatabase(String dbPath) {
        _dbPath = dbPath;
        originalDBPath = dbPath;
        //this._cont = _cont;
        //_logging = Prefs.getLogging(cont);
        try {
            if (testDBFile(_dbPath)) {
                // Here we know it is a SQLite 3 file
                Log.d(Const.TAG, "Trying to open (RW): " + _dbPath);
                //_db = DBViewer._db;
                if (isRoot)
                    copyDbTOTemp();
                Log.d(Const.TAG, "Path after copy: " + _dbPath);
                _db = SQLiteDatabase.openDatabase(_dbPath, null, SQLiteDatabase.OPEN_READONLY);
                isDatabase = true;
            } else
                isDatabase = false;
        } catch (Exception e) {
            Log.d(Const.TAG, "Trying to open Exception: " + e.getMessage());
            e.printStackTrace();
            // It is not a database
            isDatabase = false;
        }
        return isDatabase;
    }

    /*public DataBase(String _dbPath, Context _cont) {
        this._dbPath = _dbPath;
        this._cont = _cont;
        //_logging = Prefs.getLogging(cont);
        try {
            if (testDBFile(_dbPath)) {
                // Here we know it is a SQLite 3 file
                Log.d(Const.TAG, "Trying to open (RW): " + _dbPath);
                //_db = DBViewer._db;
                if (isRoot)
                    copyDbTOTemp();
                _db = SQLiteDatabase.openDatabase(_dbPath, null, SQLiteDatabase.OPEN_READONLY);
                isDatabase = true;
            }
        } catch (Exception e) {
            Log.d(Const.TAG,"Trying to open Exception: " + e.getMessage());
            e.printStackTrace();
            // It is not a database
            isDatabase = false;
        }
    }*/

    public boolean isDatabase() {
        return isDatabase;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public boolean isOpen() {
        return _db.isOpen();
    }

    private boolean testDBFile(String dbPath) {
        // File must start with the following 16 bytes
        // 0x53 0x51 0x4c 0x69 0x74 0x65 0x20 0x66 0x6f 0x72 0x6d 0x61 0x74 0x20 0x33 0x00
        // to be a SQLite 3 database
        File backupFile = new File(dbPath);

        if (!backupFile.canRead()) {
            backupFile = new SuFile(dbPath);
            isRoot = true;
            Log.d(Const.TAG, "Database: Should use root");
            //f = new SuFileRead
        }
        if (backupFile.canRead()) {
            try {
                char buffer[] = new char[16];
                if (isRoot) {
                    byte[] bytes = new byte[16];
                    SuFileInputStream f = new SuFileInputStream(backupFile);
                    f.read(bytes, 0, 16);
                    buffer = new String(bytes, "UTF-8").toCharArray();
                    f.close();
                } else {
                    FileReader f = new FileReader(backupFile);
                    f.read(buffer, 0, 16);
                    f.close();
                }
                if (buffer[0] == 0x53 &&
                        buffer[1] == 0x51 &&
                        buffer[2] == 0x4c &&
                        buffer[3] == 0x69 &&
                        buffer[4] == 0x74 &&
                        buffer[5] == 0x65 &&
                        buffer[6] == 0x20 &&
                        buffer[7] == 0x66 &&
                        buffer[8] == 0x6f &&
                        buffer[9] == 0x72 &&
                        buffer[10] == 0x6d &&
                        buffer[11] == 0x61 &&
                        buffer[12] == 0x74 &&
                        buffer[13] == 0x20 &&
                        buffer[14] == 0x33 &&
                        buffer[15] == 0x00) {
                    //f.close();
                    Log.d(Const.TAG, "IS DB");
                    return true;
                }
            } catch (FileNotFoundException e) {
                Log.e(Const.TAG, "File not found");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(Const.TAG, "IO error");
                e.printStackTrace();

            }
            return false;
        }
        return false;
    }

    private boolean copyDbTOTemp() {
        SuFile originalFile = new SuFile(_dbPath);
        File tempFile = new File(_cont.getApplicationContext().getFilesDir(), "temp.db");
        if (originalFile.exists()) {
            try (InputStream in = new SuFileInputStream(originalFile);
                 OutputStream out = new SuFileOutputStream(tempFile)) {
                /* All file data can be accessed by Java Streams */

                // For example, use a helper method to copy the logs
                ShellUtils.pump(in, out);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        Log.d(Const.TAG, "Copied to temp");
        this._dbPath = tempFile.getPath();
        return true;
    }

    private void testDB() {
        if (_db == null) {
            Log.d(Const.TAG, "TestDB database is null");
            if (_dbPath != null) {  //then Content probably also null
                try {
                    _db = SQLiteDatabase.openDatabase(_dbPath, null, SQLiteDatabase.OPEN_READONLY);
                } catch (Exception e) {
                    Log.e(Const.TAG, "testDB " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(_cont, "Something strange happened! Try again later", Toast.LENGTH_SHORT).show();
            }
        } else if (!_db.isOpen()) {
            Log.d(Const.TAG, "TestDB database not open");
            if (_dbPath == null) {
                Toast.makeText(_cont, "Something strange happened! Try again later", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    _db = SQLiteDatabase.openDatabase(_dbPath, null, SQLiteDatabase.OPEN_READWRITE);
                } catch (Exception e) {
                    Log.e(Const.TAG, "testDB " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {

        }
    }

    public ArrayList<String> getTables() {
        testDB();
        String sql = "select name from sqlite_master where type = 'table' order by name";
        Cursor res = _db.rawQuery(sql, null);
        //int recs = res.getCount();
        //String[] tables = new String[recs + 1];
        ArrayList<String> tables = new ArrayList<>();
        //int i = 1;
        tables.add("sqlite_master");
        //Utils.logD("Tables: " + recs);
        while (res.moveToNext()) {
            tables.add(res.getString(0));
            //i++;
        }
        res.close();
        return tables;
    }

    public String get_dbPath() {
        return originalDBPath;
    }

    public ArrayList<FieldModel> getFields(String table) {
        // Get field type
        // SELECT typeof(sql) FROM sqlite_master where typeof(sql) <> "null" limit 1
        testDB();
        String sql = "select * from [" + table + "] limit 1";
        sql = "pragma table_info([" + table + "])";
        Cursor res = _db.rawQuery(sql, null);

        ArrayList<FieldModel> fields = new ArrayList<>();
        // getting field names
        while (res.moveToNext()) {
            FieldModel field = new FieldModel();
            field.setFieldName(res.getString(1));
            field.setFieldType(res.getString(2));
            field.setNotNull(res.getInt(3));
            field.setDef(res.getString(4));
            field.setPk(res.getInt(5));
            fields.add(field);

        }
        res.close();
        return fields;
    }

    public int getNumCols(String table) {
        testDB();
        String sql = "select * from [" + table + "] limit 1";
        Cursor cursor = _db.rawQuery(sql, null);
        int cols = cursor.getColumnCount();
        cursor.close();
        return cols;
    }

    public String[] getFieldsNames(String table) {
        testDB();
        String sql = "pragma table_info([" + table + "])";
        Cursor res = _db.rawQuery(sql, null); //TODO 3.3 NullPointerException here
        int cols = res.getCount();
        String[] fields = new String[cols];
        int i = 0;
        // getting field names
        while (res.moveToNext()) {
            fields[i] = res.getString(1);
            i++;
        }
        res.close();
        return fields;
    }

    public long getCount(String tableName) {
        long count = DatabaseUtils.queryNumEntries(_db, tableName);
        return count;
    }

    public long getCustomQueryCount(String customQuery) {
        Cursor cursor = _db.rawQuery(customQuery, null);
        return cursor.getCount();
    }

    public List<List<Cell>> getTableData(String tableName, int limit, long offsetFrom) {
        int size = getNumCols(tableName);
        String[] cols = getFieldsNames(tableName);

        StringBuilder sql = new StringBuilder().append("Select ");
        for (int i = 0; i < cols.length; i++) {
            sql.append(cols[i]);
            if (i < cols.length - 1)
                sql.append(", ");
        }
        sql.append(" FROM ").append(tableName).append(" LIMIT ").append(limit)
                .append(" OFFSET ").append(offsetFrom);

        Log.d(Const.TAG, "Query " + sql.toString());

        Cursor cursor = _db.rawQuery(sql.toString(), null);
        //cursor.moveToFirst();
        List<List<Cell>> Tabledata = new ArrayList<>();
        while (cursor.moveToNext()) {
            List<Cell> colData = new ArrayList<>();
            String data = "";
            for (String col : cols) {
                int colIndex = cursor.getColumnIndex(col);
                try {
                    data = cursor.getString(cursor.getColumnIndex(col));
                } catch (SQLException e) {
                    //e.printStackTrace();
                    if (e.getMessage().contains("Unable to convert BLOB to string"))
                        data = "(BLOB)";
                }
                colData.add(new Cell(String.valueOf(colIndex), data));
            }
            Tabledata.add(colData);
        }
        cursor.close();
        //dumpData(Tabledata);
        return Tabledata;
    }

    private int getLimitPosition(String query) {
        return query.toLowerCase().contains("limit") ? query.toLowerCase().lastIndexOf("limit") : 0;
    }

    private String getLimitSubstr(String query) {
        return query.toLowerCase().contains("limit") ?
                query.substring(query.toLowerCase().lastIndexOf("limit", query.length())) : query;
    }

    public ArrayList<FieldModel> getCustomQueryFields(String sql) {
        testDB();
        String subSubstr = getLimitSubstr(sql);
        int limitpos = getLimitPosition(sql);
        StringBuilder columnSQL = new StringBuilder();
        if (limitpos > 0 && !subSubstr.contains(")")) {
            columnSQL.append(sql.substring(0, limitpos)).append(" LIMIT 1");
        } else
            columnSQL.append(sql).append(" LIMIT 1");

        ArrayList<FieldModel> fields = new ArrayList<>();

        Cursor res = _db.rawQuery(columnSQL.toString(), null);
        int colCount = res.getColumnCount();
        while (res.moveToNext()) {
            for (int i = 0; i < colCount; i++) {
                FieldModel field = new FieldModel();
                field.setFieldName(res.getColumnName(i));
                field.setFieldType(Const.getColumnDataType(res.getType(i)));
                fields.add(field);
            }
        }

        res.close();
        return fields;
    }

    public List<List<Cell>> runQuery(String query, int limit, long offsetFrom) throws SQLiteException {
        String subSubstr = getLimitSubstr(query);
        int limitpos = getLimitPosition(query);
        StringBuilder customSQL = new StringBuilder();
        int customQueryLimit = -1; //Moved from global var
        if (limitpos > 0 && !subSubstr.contains(")")) {

            customQueryLimit = Integer.parseInt(
                    subSubstr.toLowerCase().split("limit")[1].trim()
            );
            customSQL.append(query.substring(0, limitpos));
        } else
            customSQL.append(query);

        customSQL.append(" LIMIT ");
        if (customQueryLimit != -1) {
            if (customQueryLimit < limit) {
                customSQL.append(customQueryLimit);
            } else if ((offsetFrom + offsetFrom) > customQueryLimit) {
                customSQL.append(customQueryLimit - offsetFrom).append(" OFFSET ").append(offsetFrom);
            } else {
                customSQL.append(limit).append(" OFFSET ").append(offsetFrom);
            }
        } else {
            customSQL.append(limit).append(" OFFSET ").append(offsetFrom);
        }

        Cursor cursor = _db.rawQuery(customSQL.toString(), null);

        List<List<Cell>> Tabledata = new ArrayList<>();
        while (cursor.moveToNext()) {
            List<Cell> colData = new ArrayList<>();
            String data = "";
            int colCount = cursor.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                try {
                    data = cursor.getString(i);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    if (e.getMessage().contains("Unable to convert BLOB to string"))
                        data = "(BLOB)";
                }
                colData.add(new Cell(String.valueOf(i), data));
            }
            Tabledata.add(colData);
        }
        cursor.close();
        //dumpData(Tabledata);
        return Tabledata;
    }

    private void dumpData(List<List<Cell>> data) {
        Log.d(Const.TAG, "Size: " + data.size());
        for (List<Cell> row : data) {
            StringBuilder rowData = new StringBuilder();
            for (Cell col : row) {
                rowData.append((String) col.getData()).append(",");
            }
            Log.d(Const.TAG, "Row:[" + rowData.toString() + "]");
        }
    }

    public void close() {
        //This sometimes throws SQLiteException: unable to close due to unfinalised statements
        try {
            Log.d(Const.TAG, "Closing database");
            _db.close();
        } catch (Exception e) {
            Log.e(Const.TAG, "onClose");
            e.printStackTrace();
        }
    }
}
