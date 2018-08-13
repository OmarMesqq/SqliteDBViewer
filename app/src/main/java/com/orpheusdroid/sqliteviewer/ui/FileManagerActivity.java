package com.orpheusdroid.sqliteviewer.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orpheusdroid.sqliteviewer.Adapter.FileManagerAdapter;
import com.orpheusdroid.sqliteviewer.Const;
import com.orpheusdroid.sqliteviewer.DonateActivity;
import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.listeners.IListItemClickListener;
import com.orpheusdroid.sqliteviewer.model.filemanager.FilesModel;
import com.orpheusdroid.sqliteviewer.utils.SharedPreferenceManager;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import ly.count.android.sdk.Countly;

public class FileManagerActivity extends AppCompatActivity implements IListItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView FilesView;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout dirUp;
    private TextView currentPathTV;
    private boolean isRoot = false;
    private FileManagerAdapter adapter;
    private SearchView searchView;
    private SharedPreferenceManager prefs;
    private File selectedDir;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<FilesModel> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        FilesView = findViewById(R.id.filesList);
        currentPathTV = findViewById(R.id.currentPath_tv);

        prefs = SharedPreferenceManager.getInstance(this);
        String lastDirLocation = prefs.getString(Const.FILEMANAGER_PREFS,
                Const.FILEMANAGER_PREFS_LAST_LOCATION,
                Environment.getExternalStorageDirectory().getPath());
        selectedDir = new File(lastDirLocation);

        dirUp = findViewById(R.id.dirUp_ll);
        /*if (requestPermissionStorage()) {
            Toast.makeText(this, "Storage permission is required to view databases",Toast.LENGTH_SHORT).show();
            finish();
        }*/

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        checkPermission();

        dirUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedDir.getPath().equals("/")) {
                    if (selectedDir.getParentFile().exists()) {
                        selectedDir = selectedDir.getParentFile();
                        new GenerateFilesList(true).execute();
                    }
                }
            }
        });
        requestAnalyticsPermission();
        Countly.onCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Countly.sharedInstance().onStart(this);
        Log.d(Const.TAG, "Countly started");
    }

    @Override
    protected void onStop() {
        Countly.sharedInstance().onStop();
        Log.d(Const.TAG, "Countly stopped");
        super.onStop();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionStorage();
        } else {
            new GenerateFilesList(false).execute();
        }
    }

    private void requestAnalyticsPermission() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(Const.PREFS_REQUEST_ANALYTICS_PERMISSION, true))
            return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_dialog_analytics_title)
                .setMessage(R.string.alert_dialog_analytics_message)
                .setPositiveButton(R.string.alert_dialog_analytics_positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        prefs.edit()
                                .putBoolean(getString(R.string.countly_basic_crash_reporting_key), true)
                                .putBoolean(getString(R.string.countly_anonymous_usage_stats_key), true)
                                .putBoolean(Const.PREFS_REQUEST_ANALYTICS_PERMISSION, false)
                                .apply();
                        showAppRestartToast();
                    }
                })
                .setNeutralButton(R.string.alert_dialog_analytics_neutral_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        prefs.edit()
                                .putBoolean(getString(R.string.countly_basic_crash_reporting_key), true)
                                .putBoolean(Const.PREFS_REQUEST_ANALYTICS_PERMISSION, false)
                                .apply();
                        showAppRestartToast();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_analytics_negative_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        prefs.edit()
                                .putBoolean(Const.PREFS_REQUEST_ANALYTICS_PERMISSION, false)
                                .apply();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void showAppRestartToast() {
        Toast.makeText(this, R.string.toast_message_analytics_app_restart_required_message, Toast.LENGTH_SHORT).show();
    }

    private void refreshList() {
        adapter.changeAdapter(files);
        prefs.setString(Const.FILEMANAGER_PREFS, Const.FILEMANAGER_PREFS_LAST_LOCATION, selectedDir.getPath());
        Log.d(Const.TAG, "Saved location" + selectedDir.getPath());
    }

    private ArrayList<FilesModel> buildFilesList() {
        ArrayList<FilesModel> files = new ArrayList<>();
        if (!selectedDir.canRead() || !selectedDir.getPath().contains(Environment.getExternalStorageDirectory().getPath())) {
            Log.d(Const.TAG, "Using root");
            selectedDir = new SuFile(selectedDir.getPath());
            isRoot = true;
        } else {
            Log.d(Const.TAG, "Readable. Using normal File: " + selectedDir.getPath());
            selectedDir = new File(selectedDir.getPath());
            isRoot = false;
        }
        currentPathTV.setText(selectedDir.getPath());
        File[] allFiles = selectedDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isDirectory() || file.getName().contains(".db") && !file.isHidden());
            }
        });

        for (File filter : allFiles) {
            FilesModel file = new FilesModel(filter, filter.isDirectory(),
                    filter.lastModified());
            if (!filter.isDirectory())
                file.setSize(Integer.parseInt(String.valueOf(filter.length() / 1024)));

            files.add(file);
        }
        Collections.sort(files);
        return files;
    }


    private void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        FilesView.setLayoutManager(mLayoutManager);

        adapter = new FileManagerAdapter(files, this);
        FilesView.setAdapter(adapter);
    }


    public boolean requestPermissionStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.storage_permission_request_title))
                    .setMessage(getString(R.string.storage_permission_request_summary))
                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(FileManagerActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1000);
                        }
                    })
                    .setCancelable(false);

            alert.create().show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Const.EXTDIR_REQUEST_CODE:
                if ((grantResults.length > 0) &&
                        (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "write storage Permission Denied");
                } else {
                    /* Since we have write storage permission now, lets create the app directory
                     * in external storage*/
                    Log.d(Const.TAG, "write storage Permission granted");
                    new GenerateFilesList(false).execute();
                }
        }
    }

    @Override
    public void onClick(int position) {
        File file = files.get(position).getFile();
        Log.d(Const.TAG, "List item: " + file.getName());
        if (file.exists() && file.isDirectory()) {
            selectedDir = file;
            new GenerateFilesList(true).execute();
        }
        if (file.isFile() && file.getName().contains(".db")) {
            Intent tableListIntent = new Intent(this, TableList.class);
            tableListIntent.putExtra(Const.DBPathIntent, file.getPath());
            startActivity(tableListIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        setupSearch(menu);
        return true;
    }

    private void setupSearch(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.donate:
                startActivity(new Intent(this, DonateActivity.class));
                return true;
            case R.id.menu_support:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/SQLiteViewer")));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No browser app installed!", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new GenerateFilesList(true).execute();
    }

    private class GenerateFilesList extends AsyncTask<Void, Void, ArrayList<FilesModel>> {
        private boolean isReplaceModel;
        private boolean hasListingFailed;

        GenerateFilesList(boolean isReplaceModel) {
            this.isReplaceModel = isReplaceModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
            FilesView.setClickable(false);
            dirUp.setClickable(false);
        }

        @Override
        protected void onPostExecute(ArrayList<FilesModel> filesModels) {
            if (hasListingFailed)
                Toast.makeText(FileManagerActivity.this, "No root access. Cannot list root files", Toast.LENGTH_SHORT)
                        .show();
            FileManagerActivity.this.files = filesModels;
            swipeRefreshLayout.setRefreshing(false);
            FilesView.setClickable(true);
            dirUp.setClickable(true);
            if (isReplaceModel)
                refreshList();
            else
                setupRecyclerView();

        }

        @Override
        protected ArrayList<FilesModel> doInBackground(Void... aVoid) {
            try {
                return buildFilesList();
            } catch (NullPointerException e) {
                if (isRoot)
                    hasListingFailed = true;
            }
            selectedDir = Environment.getExternalStorageDirectory();
            return buildFilesList();
        }
    }
}
