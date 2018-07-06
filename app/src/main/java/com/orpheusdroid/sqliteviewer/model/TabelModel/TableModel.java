package com.orpheusdroid.sqliteviewer.model.TabelModel;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class TableModel {

    private Context mContext;

    private List<RowHeader> rowHeader = new ArrayList<>();

    public TableModel(Context context) {
        mContext = context;
    }

    /**
     * This is a dummy model list test some cases.
     */
    private List<ColumnHeader> getRandomColumnHeaderList() {
        List<ColumnHeader> header = new ArrayList<>();

        header.add(new ColumnHeader("1", "_id"));
        header.add(new ColumnHeader("2", "Name"));
        header.add(new ColumnHeader("3", "Age"));
        header.add(new ColumnHeader("4", "Mobile"));

        return header;
    }

    public void setRowHeader(List<RowHeader> rowHeader) {
        this.rowHeader = rowHeader;
    }

    /**
     * This is a dummy model list test some cases.
     */
    private List<List<Cell>> getCellListForSortingTest() {
        List<List<Cell>> TableData = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            List<Cell> rowData = new ArrayList<>();
            rowData.add(new Cell("1", "1"));
            rowData.add(new Cell(String.valueOf(i), "Vijai Chandra Prasad"));
            rowData.add(new Cell(String.valueOf(i), "Age"));
            rowData.add(new Cell(String.valueOf(i), "123"));
            TableData.add(rowData);
        }

        return TableData;
    }

    public List<List<Cell>> getCellList() {
        return getCellListForSortingTest();
    }

    public List<RowHeader> getRowHeaderList() {
        //return getSimpleRowHeaderList();
        return this.rowHeader;
    }

    public List<ColumnHeader> getColumnHeaderList() {
        return getRandomColumnHeaderList();
        //return this.colHeader;
    }


}
