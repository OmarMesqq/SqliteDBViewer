package com.orpheusdroid.sqliteviewer.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.TextView;

import com.orpheusdroid.sqliteviewer.BuildConfig;
import com.orpheusdroid.sqliteviewer.R;

import java.util.Calendar;

public class AboutActivity extends AppCompatActivity {

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView appVersion = findViewById(R.id.versionTxt);
        TextView iconCredit = findViewById(R.id.icon_credit_tv);
        TextView libSuCredit = findViewById(R.id.root_library_credit_tv);
        TextView tableViewCredit = findViewById(R.id.table_view_library_credit_tv);
        TextView csvExportCredit = findViewById(R.id.csv_export_library_credit_tv);
        TextView splashScreenCredit = findViewById(R.id.splash_screen_library_credit_tv);
        TextView openSourceInfo = findViewById(R.id.opensource_info_tv);

        iconCredit.setText(getString(R.string.app_icon_credit_Niko, "Niko Hörkkö", "http://nikosite.net"));
        libSuCredit.setText(getString(R.string.libsu_library_credit, "topjohnwu",
                "https://github.com/topjohnwu/libsu",
                "Apache 2.0"));
        tableViewCredit.setText(getString(R.string.tableview_library_credit, "evrencoskun",
                "https://github.com/evrencoskun/TableView",
                "MIT Opensource License"));
        csvExportCredit.setText(getString(R.string.csv_export_library_credit, "osiegmar",
                "https://github.com/osiegmar/FastCSV",
                "Apache-2.0"));
        splashScreenCredit.setText(getString(R.string.splash_screen_library_credit, "ViksaaSkool",
                "https://github.com/ViksaaSkool/AwesomeSplash",
                "MIT Opensource License"));
        openSourceInfo.setText(getString(R.string.opensource_info, "https://gitlab.com/vijai/SqliteDBViewer", "GNU AGPLv3"));

        //Let's build the copyright text using String builder
        StringBuilder copyRight = new StringBuilder();
        copyRight.append("Copyright &copy; orpheusdroid 2014-")
                .append(Calendar.getInstance().get(Calendar.YEAR))
                .append("\n");


        //If the apk is beta version include version code. Else ignore
        if (BuildConfig.VERSION_NAME.contains("Beta")) {
            copyRight.append(getResources().getString(R.string.app_name))
                    .append(" Build")
                    .append(BuildConfig.VERSION_CODE)
                    .append(" V")
                    .append(BuildConfig.VERSION_NAME)
                    .append("\n Internal Build. Not to be released");
        } else {
            copyRight.append(getResources().getString(R.string.app_name))
                    .append(" V")
                    .append(BuildConfig.VERSION_NAME);
        }
        switch (BuildConfig.FLAVOR) {
            case "playstore":
                copyRight.append(" PlayStore");
                break;
            case "fdroid":
                copyRight.append(" Fdroid");
                break;
        }
        if (BuildConfig.DEBUG)
            copyRight.append(" Debug version");
        //set the text as html to get copyright symbol
        appVersion.setText(fromHtml(copyRight.toString()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //finish this activity and return to parent activity
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
