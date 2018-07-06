package com.orpheusdroid.sqliteviewer.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.listeners.IListItemClickListener;

import java.util.ArrayList;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.ViewHolder> implements Filterable {
    private ArrayList<String> tables = new ArrayList<>();
    private IListItemClickListener onClickListener;
    private ArrayList<String> tablesFiltered = new ArrayList<>();

    public TableListAdapter(ArrayList<String> tables, IListItemClickListener onClickListener) {
        this.tables = tables;
        this.tablesFiltered = tables;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_list_items, parent, false), onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tableName.setText(tablesFiltered.get(position));
    }

    @Override
    public int getItemCount() {
        return tablesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    tablesFiltered = tables;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : tables) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    tablesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tablesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tablesFiltered = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tableName;
        LinearLayout tableNamell;

        ViewHolder(View itemView, final IListItemClickListener onClickListener) {
            super(itemView);
            tableName = itemView.findViewById(R.id.table_list_name_tv);
            tableNamell = itemView.findViewById(R.id.table_name_ll);

            tableNamell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
