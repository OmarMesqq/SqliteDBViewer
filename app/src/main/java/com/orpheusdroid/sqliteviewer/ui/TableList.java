package com.orpheusdroid.sqliteviewer.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.orpheusdroid.sqliteviewer.Adapter.TableListAdapter;
import com.orpheusdroid.sqliteviewer.Const;
import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.database.DataBase;
import com.orpheusdroid.sqliteviewer.listeners.IListItemClickListener;

import java.io.File;
import java.util.ArrayList;

public class TableList extends AppCompatActivity implements IListItemClickListener {
    private String dbPath;
    private RecyclerView tableList;
    private TableListAdapter adapter;
    private ArrayList<String> tables;
    private DataBase db;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        if (getIntent() != null && getIntent().hasExtra(Const.DBPathIntent))
            dbPath = getIntent().getStringExtra(Const.DBPathIntent);
        else {
            Toast.makeText(this, "No database path found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Log.d(Const.TAG, dbPath);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(new File(dbPath).getName());
        }

        tableList = findViewById(R.id.table_rv);

        //db = new DataBase(dbPath, this);
        db = DataBase.getInstance(this);
        boolean isDb = db.setDatabase(dbPath);
        if (isDb) {
            tables = db.getTables();
            /*Collections.sort(tables, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    if (s.toLowerCase().contains("sqlite"))
                        return -1;
                    else
                        return s.compareTo(t1);
                }
            });*/
            setupRecyclerView();
        } else {
            Toast.makeText(this, "Not a database", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        tableList.setLayoutManager(layoutManager);
        tableList.setHasFixedSize(true);

        adapter = new TableListAdapter(tables, this);

        tableList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(int position) {
        String tableName = tables.get(position);
        Intent tableData = new Intent(this, TableDataActivity.class);
        tableData.putExtra(Const.DBTableNameIntent, tableName);
        startActivity(tableData);
    }
}
