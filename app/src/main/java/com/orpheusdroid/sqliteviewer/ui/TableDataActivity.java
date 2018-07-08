package com.orpheusdroid.sqliteviewer.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
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
import com.orpheusdroid.sqliteviewer.model.TabelModel.TableModel;
import com.orpheusdroid.sqliteviewer.utils.TableCellClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TableDataActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private MyTableViewAdapter mTableViewAdapter;
    private TableModel model;
    private DataBase db;
    private String tableName;
    private Button previous;
    private Button next;
    private int tableViewRowCount;
    private long offset = 0;
    private long totalRows = 0;
    private List<ColumnHeader> columnHeaders;
    private List<List<Cell>> tableData;
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

        String dbName = new File(db.get_dbPath()).getName();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(stripExtension(dbName) + "." + tableName);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tableViewRowCount = Integer.parseInt(
                prefs.getString(getString(R.string.preference_settings_table_row_count_key), "50")
        );

        previous = findViewById(R.id.previous_btn);
        next = findViewById(R.id.next_btn);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        totalRows = db.getCount(tableName);
        tableData = generateTableData();

        if (tableData.size() == 0) {
            Toast.makeText(this, R.string.toast_message_table_empty, Toast.LENGTH_SHORT).show();
            //finish();
        }

        TableView table = findViewById(R.id.tableDataContent);

        Log.d(Const.TAG, "Data for " + db.get_dbPath() + "." + tableName);

        columnHeaders = new ArrayList<>();
        for (FieldModel field : db.getFields(tableName))
            columnHeaders.add(new ColumnHeader("1", field.getHeaderName()));

        model = new TableModel(this);
        List<RowHeader> Rowheader = generateRowHeader();

        mTableViewAdapter = new MyTableViewAdapter(this);
        table.setAdapter(mTableViewAdapter);
        table.setTableViewListener(new TableCellClickListener(this));

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
