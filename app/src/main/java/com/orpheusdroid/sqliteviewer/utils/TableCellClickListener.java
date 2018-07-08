package com.orpheusdroid.sqliteviewer.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

    public TableCellClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

    }

    @Override
    public void onCellLongPressed(@NonNull final RecyclerView.ViewHolder cellView, int column, int row) {
        Log.d(Const.TAG, "row: " + row + ", col: " + column);
        PopupMenu popup = new PopupMenu(context, cellView.itemView);
        popup.getMenuInflater().inflate(R.menu.popup_menu_table_cell, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_copy:
                        if (cellView instanceof MyTableViewAdapter.MyCellViewHolder) {
                            String strData = ((MyTableViewAdapter.MyCellViewHolder) cellView).cell_textview.getText().toString();
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("SqliteViewer", strData);
                            clipboard.setPrimaryClip(clipData);
                            Toast.makeText(context, R.string.table_cell_popup_menu_toast_message
                                    , Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.action_share:
                        if (cellView instanceof MyTableViewAdapter.MyCellViewHolder) {
                            String strData = ((MyTableViewAdapter.MyCellViewHolder) cellView).cell_textview.getText().toString();
                            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, strData);
                            context.startActivity(Intent.createChooser(shareIntent, "Share"));
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
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
