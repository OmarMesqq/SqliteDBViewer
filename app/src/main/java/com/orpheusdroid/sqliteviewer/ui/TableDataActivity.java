package com.orpheusdroid.sqliteviewer.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;
import com.orpheusdroid.sqliteviewer.Adapter.MyTableViewAdapter;
import com.orpheusdroid.sqliteviewer.Const;
import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.database.DataBase;
import com.orpheusdroid.sqliteviewer.model.TabelModel.Cell;
import com.orpheusdroid.sqliteviewer.model.TabelModel.ColumnHeader;
import com.orpheusdroid.sqliteviewer.model.TabelModel.FieldModel;
import com.orpheusdroid.sqliteviewer.model.TabelModel.RowHeader;
import com.orpheusdroid.sqliteviewer.utils.TableCellClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TableDataActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private MyTableViewAdapter mTableViewAdapter;
    private DataBase db;
    private String tableName;
    private String customQuery = "";
    private boolean isCustomQuery = false;
    private int tableViewRowCount;
    private long offset = 0;
    private long totalRows = 0;
    private List<List<Cell>> tableData = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_data);

        ActionBar actionBar = getSupportActionBar();

        db = DataBase.getInstance(this);
        if (!db.isDatabase()) {
            Toast.makeText(this, "No Database found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (getIntent() != null && getIntent().hasExtra(Const.DBTableNameIntent))
            tableName = getIntent().getStringExtra(Const.DBTableNameIntent);
        else if (getIntent() != null && getIntent().hasExtra(Const.DBCustomQueryIntent)) {
            Log.d(Const.TAG, "is custom query");
            tableName = "Custom Query";
            customQuery = getIntent().getStringExtra(Const.DBCustomQueryIntent);
            isCustomQuery = true;
        }

        String dbName = new File(db.get_dbPath()).getName();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(stripExtension(dbName) + "." + tableName);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tableViewRowCount = Integer.parseInt(
                prefs.getString(getString(R.string.preference_settings_table_row_count_key), "50")
        );

        Button previous = findViewById(R.id.previous_btn);
        Button next = findViewById(R.id.next_btn);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        BottomSheetDialog bottomBarMenu = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_actions_menu, null);
        bottomBarMenu.setContentView(bottomSheetView);

        ArrayList<FieldModel> fields = new ArrayList<>();
        if (isCustomQuery) {
            try {
                tableData = db.runQuery(customQuery, tableViewRowCount, offset);
                fields = db.getCustomQueryFields(customQuery);
                totalRows = db.getCustomQueryCount(customQuery);
            } catch (SQLiteException e) {
                showCustomQueryErrorAlert(e.getMessage());
            }
        } else {
            tableData = generateTableData();
            fields = db.getFields(tableName);
            totalRows = db.getCount(tableName);
        }

        if (tableData.size() == 0) {
            Toast.makeText(this, R.string.toast_message_table_empty, Toast.LENGTH_SHORT).show();
            //finish();
        }

        TableView table = findViewById(R.id.tableDataContent);

        Log.d(Const.TAG, "Data for " + db.get_dbPath() + "." + tableName);

        List<ColumnHeader> columnHeaders = new ArrayList<>();
        for (FieldModel field : fields)
            columnHeaders.add(new ColumnHeader("1", field.getHeaderName()));

        List<RowHeader> Rowheader = generateRowHeader();

        mTableViewAdapter = new MyTableViewAdapter(this);
        table.setAdapter(mTableViewAdapter);
        table.setTableViewListener(new TableCellClickListener(this, bottomBarMenu));

        mTableViewAdapter.setAllItems(columnHeaders, Rowheader, tableData);
    }

    private List<List<Cell>> generateTableData() {
        return db.getTableData(tableName, tableViewRowCount, offset);
    }

    private List<RowHeader> generateRowHeader() {
        List<RowHeader> rowHeader = new ArrayList<>();
        long localOffset = offset;
        for (long i = 0; i < tableData.size(); i++) {
            rowHeader.add(new RowHeader(String.valueOf(i), String.valueOf(localOffset += 1)));
        }
        return rowHeader;
    }

    private List<List<Cell>> runCustomQuery(String query) {
        try {
            return db.runQuery(query, tableViewRowCount, offset);
        } catch (SQLiteException e) {
            showCustomQueryErrorAlert(e.getMessage());
        }
        return null;
    }

    private void showCustomQueryErrorAlert(String message) {
        Toast.makeText(this, "Query execution failed", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setTitle("SQL query failed")
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private String stripExtension(String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }

    private void refreshTableViewData() {
        if (isCustomQuery)
            tableData = runCustomQuery(customQuery);
        else
            tableData = generateTableData();
        mTableViewAdapter.refreshData(tableData, generateRowHeader());
    }

    private List<String> addValuesToSpinner() {
        long divident = totalRows / tableViewRowCount;
        divident = (totalRows / tableViewRowCount > 0) ? divident + 1 : divident;

        List<String> spinnerItems = new ArrayList<>();
        for (int i = 1; i <= divident; i++)
            spinnerItems.add(String.valueOf(i));

        return spinnerItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabledata, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        adapter = new ArrayAdapter<>(this, R.layout.table_data_spinner_text_view, addValuesToSpinner());
        adapter.setDropDownViewResource(R.layout.table_data_spinner_drop_down_items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //addValuesToSpinner(adapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.spinner:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                if ((offset + tableViewRowCount) < totalRows) {
                    offset += tableViewRowCount;
                    Log.d(Const.TAG, "Total: " + totalRows + ", offset :" + offset);
                    refreshTableViewData();
                }
                break;
            case R.id.previous_btn:
                if (offset < totalRows && offset != 0) {
                    offset = ((offset - tableViewRowCount) > 0) ? offset - tableViewRowCount : 0;
                    Log.d(Const.TAG, "Total: " + totalRows + ", offset :" + offset);
                    refreshTableViewData();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int val = Integer.valueOf(adapter.getItem(i)) - 1;
        offset = val * tableViewRowCount;
        refreshTableViewData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        offset = 0;
        refreshTableViewData();
    }
}
