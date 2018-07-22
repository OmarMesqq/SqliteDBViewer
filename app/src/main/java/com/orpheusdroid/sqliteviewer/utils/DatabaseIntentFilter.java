package com.orpheusdroid.sqliteviewer.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.orpheusdroid.sqliteviewer.Const;
import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.ui.TableList;

import java.io.File;

public class DatabaseIntentFilter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();

        if (uri != null) {
            Log.d(Const.TAG, "URI: " + uri.getPath());
            startDatabaseViewerActivity(new File(uri.getPath()));
        } else {
            Toast.makeText(this, R.string.toast_message_intent_filter_no_uri_data, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startDatabaseViewerActivity(File file) {
        if (file.isFile() && file.getName().contains(".db")) {
            Intent tableListIntent = new Intent(this, TableList.class);
            tableListIntent.putExtra(Const.DBPathIntent, file.getPath());
            startActivity(tableListIntent);
            finish();
        }
    }
}
