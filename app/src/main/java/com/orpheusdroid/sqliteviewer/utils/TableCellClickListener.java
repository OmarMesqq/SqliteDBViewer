package com.orpheusdroid.sqliteviewer.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.evrencoskun.tableview.listener.ITableViewListener;
import com.orpheusdroid.sqliteviewer.Adapter.MyTableViewAdapter;
import com.orpheusdroid.sqliteviewer.Const;
import com.orpheusdroid.sqliteviewer.R;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class TableCellClickListener implements ITableViewListener {
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout copyLayout;
    private LinearLayout shareLayout;

    public TableCellClickListener(Context context, BottomSheetDialog bottomSheetDialog) {
        this.context = context;
        this.bottomSheetDialog = bottomSheetDialog;
        copyLayout = bottomSheetDialog.findViewById(R.id.bottomSheet_copyLayout);
        shareLayout = bottomSheetDialog.findViewById(R.id.bottomSheet_shareLayout);
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

    }

    @Override
    public void onCellLongPressed(@NonNull final RecyclerView.ViewHolder cellView, int column, int row) {
        Log.d(Const.TAG, "row: " + row + ", col: " + column);
        bottomSheetDialog.show();

        copyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cellView instanceof MyTableViewAdapter.MyCellViewHolder) {
                    String strData = ((MyTableViewAdapter.MyCellViewHolder) cellView).cell_textview.getText().toString();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("SqliteViewer", strData);
                    clipboard.setPrimaryClip(clipData);
                    Toast.makeText(context, R.string.table_cell_popup_menu_toast_message
                            , Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.cancel();
            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cellView instanceof MyTableViewAdapter.MyCellViewHolder) {
                    String strData = ((MyTableViewAdapter.MyCellViewHolder) cellView).cell_textview.getText().toString();
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, strData);
                    context.startActivity(Intent.createChooser(shareIntent, "Share"));
                }
                bottomSheetDialog.cancel();
            }
        });
    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }
}
