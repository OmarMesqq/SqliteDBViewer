package com.orpheusdroid.sqliteviewer.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;

public class TableDataActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener {
    private MyTableViewAdapter mTableViewAdapter;
    private static DataBase db;
    private static String tableName;
    private static String customQuery = "";
    private static boolean isCustomQuery = false;
    private int tableViewRowCount;
    private long offset = 0;
    private long totalRows = 0;
    private List<List<Cell>> tableData = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static List<ColumnHeader> columnHeaders;
    private static TableDataActivity context;
    private BottomNavigationView bottomNavigationView;
    private Spinner spinner;
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            if (dy > 0 && bottomNavigationView.isShown()) {
                bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(800);
            } else if (dy < 0) {
                bottomNavigationView.animate().translationY(0).setDuration(800);
            }
        }
    };

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

    private static void showCustomQueryErrorAlert(String message) {
        Toast.makeText(context, "Query execution failed", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(context)
                .setTitle("SQL query failed")
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.finish();
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

        context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tableViewRowCount = Integer.parseInt(
                prefs.getString(getString(R.string.preference_settings_table_row_count_key), "50")
        );

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        final BottomSheetDialog bottomBarDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_actions_menu, null);
        bottomBarDialog.setContentView(bottomSheetView);

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
        }

        TableView table = findViewById(R.id.tableDataContent);

        Log.d(Const.TAG, "Data for " + db.get_dbPath() + "." + tableName);

        columnHeaders = new ArrayList<>();
        for (FieldModel field : fields)
            columnHeaders.add(new ColumnHeader("1", field.getHeaderName()));

        List<RowHeader> Rowheader = generateRowHeader();

        mTableViewAdapter = new MyTableViewAdapter(this);
        table.setAdapter(mTableViewAdapter);
        table.setTableViewListener(new TableCellClickListener(this, bottomBarDialog));
        table.getCellRecyclerView().addOnScrollListener(onScrollListener);
        mTableViewAdapter.setAllItems(columnHeaders, Rowheader, tableData);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private String formatFileName(String fileName) {
        return (fileName.toLowerCase().endsWith(".csv")) ? fileName : fileName + ".csv";
    }

    private void exportToCSV(String fileName) {
        Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
        File csvExportDir = Const.CSV_EXPORT_DIR;
        if (csvExportDir.exists() || csvExportDir.mkdirs()) {
            File csv = new File(csvExportDir, formatFileName(fileName));
            new WriteToCSV(csv).execute();
        }
    }

    private boolean shouldShowSpinner() {
        long divident = totalRows / tableViewRowCount;
        return divident > 0;
    }

    private List<String> addValuesToSpinner() {
        long divident = (totalRows / tableViewRowCount) + 1;
        //divident = (totalRows / tableViewRowCount > 0) ? divident + 1 : divident;

        List<String> spinnerItems = new ArrayList<>();
        for (int i = 1; i <= divident; i++)
            spinnerItems.add(String.valueOf(i));

        return spinnerItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabledata, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();

        //List<String> spinnerItems = addValuesToSpinner();

        if (shouldShowSpinner()) {
            adapter = new ArrayAdapter<>(this, R.layout.table_data_spinner_text_view, addValuesToSpinner());
            adapter.setDropDownViewResource(R.layout.table_data_spinner_drop_down_items);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        } else
            item.setVisible(false);

        //addValuesToSpinner(adapter);
        return true;
    }

    private void exportToCSVDialog() {
        AlertDialog.Builder customQueryBuilderDialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.content_custom_query_alert_view, null);
        customQueryBuilderDialog.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.custom_query_editText);
        editText.setHint(R.string.export_fileName_dialog_edittext_hint);
        editText.setSelection(editText.getText().length());

        customQueryBuilderDialog.setTitle(R.string.export_fileName_dialog_title);
        customQueryBuilderDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        customQueryBuilderDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        customQueryBuilderDialog.setCancelable(true);
        final AlertDialog alertDialog = customQueryBuilderDialog.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty())
                    editText.setError(getString(R.string.export_edittext_empty_error_message));
                else {
                    exportToCSV(editText.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.spinner:
                return true;
            case R.id.action_export_csv:
                exportToCSVDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                if ((offset + tableViewRowCount) < totalRows) {
                    offset += tableViewRowCount;
                    Log.d(Const.TAG, "Total: " + totalRows + ", offset :" + offset);
                    refreshPageSpinner(offset);
                }
                return true;
            case R.id.action_previous:
                if (offset < totalRows && offset != 0) {
                    offset = ((offset - tableViewRowCount) > 0) ? offset - tableViewRowCount : 0;
                    Log.d(Const.TAG, "Total: " + totalRows + ", offset :" + offset);
                    refreshPageSpinner(offset);
                }
                return true;
            case R.id.action_export_csv:
                exportToCSVDialog();
                return true;
            default:
                return true;
        }
    }

    private void refreshPageSpinner(long offset) {
        if (shouldShowSpinner()) {
            long divident = (offset / (tableViewRowCount));
            spinner.setSelection((int) divident);
        }
    }

    private static class WriteToCSV extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog;
        private DataBase db;
        private File targetFile;
        private int limit = -1;
        private int offset = 0;
        private int exportedRowCount = 0;
        private int maxQueryCount;
        private Charset charset;

        WriteToCSV(File targetFile) {
            this.targetFile = targetFile;
            db = DataBase.getInstance(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            maxQueryCount = Integer.parseInt(
                    prefs.getString(
                            context.getString(R.string.preference_max_export_query_count_key), "50"
                    ));
            charset = Const.getEncodingType(prefs.getString(
                    context.getString(R.string.preference_export_charset_key), "UTF-8"));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            dialog.setTitle(context.getString(R.string.export_progress_dialog_title));
            dialog.setMessage(context.getString(R.string.export_progress_dialog_message));
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            Toast.makeText(context, R.string.toast_message_export_success, Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (dialog.isIndeterminate())
                dialog.setIndeterminate(false);
            dialog.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int totalRows;
            if (isCustomQuery) {
                totalRows = (int) db.getCustomQueryCount(customQuery);
            } else
                totalRows = (int) db.getCount(tableName);
            dialog.setMax(totalRows);
            CsvWriter csvWriter = new CsvWriter();
            try (CsvAppender br = csvWriter.append(targetFile, charset)) {
                for (int i = 0; i < columnHeaders.size(); i++) {
                    br.appendField(columnHeaders.get(i).getData().toString());
                }
                br.endLine();
                br.flush();
                if (totalRows > maxQueryCount) {
                    limit = maxQueryCount;
                    do {
                        writeToCSV(br, generateData());
                        offset += limit;
                    } while (offset < totalRows);
                } else
                    writeToCSV(br, generateData());
            } catch (IOException e) {
                Toast.makeText(context, R.string.toast_message_failed_export_message, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        private void writeToCSV(CsvAppender br, List<List<Cell>> data) throws IOException {
            for (int rowCount = 0; rowCount < data.size(); rowCount++) {
                List<Cell> row = data.get(rowCount);
                for (int col = 0; col < row.size(); col++) {
                    if (row.get(col).getData() == null) {
                        br.appendField(" ");
                    } else
                        br.appendField(row.get(col).getData().toString());
                }
                br.endLine();
                br.flush();
                exportedRowCount++;
            }
            publishProgress(exportedRowCount);
        }

        private List<List<Cell>> generateData() {
            if (isCustomQuery) {
                try {
                    return db.runQuery(customQuery, limit, offset);
                } catch (SQLiteException e) {
                    showCustomQueryErrorAlert(e.getMessage());
                }
            } else {
                return db.getTableData(tableName, limit, offset);
            }
            return new ArrayList<>();
        }
    }
}
