package com.orpheusdroid.sqliteviewer.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.listeners.IListItemClickListener;
import com.orpheusdroid.sqliteviewer.model.filemanager.FilesModel;

import java.util.ArrayList;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ViewHolder> implements Filterable {
    private ArrayList<FilesModel> files = new ArrayList<>();
    private ArrayList<FilesModel> filesFiltered = new ArrayList<>();
    private IListItemClickListener onClickListener;

    public FileManagerAdapter(ArrayList<FilesModel> files, IListItemClickListener onClickListener) {
        this.files = files;
        this.filesFiltered = files;
        this.onClickListener = onClickListener;
    }

    public void changeAdapter(ArrayList<FilesModel> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fileRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_filemanager_items, parent, false);
        return new ViewHolder(fileRowView, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilesModel file = filesFiltered.get(position);
        holder.fileName.setText(file.getName());
        holder.lastModified.setText(file.getLastModified());
        holder.size.setText(String.valueOf(file.getSize()));

        if (file.isDirectory())
            holder.icon.setImageResource(R.drawable.ic_folder_black_24dp);
        else
            holder.icon.setImageResource(R.drawable.ic_db_black_24dp);
    }

    @Override
    public int getItemCount() {
        return filesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filesFiltered = files;
                } else {
                    ArrayList<FilesModel> filteredList = new ArrayList<>();
                    for (FilesModel row : files) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filesFiltered = (ArrayList<FilesModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView fileName;
        public TextView lastModified;
        public TextView size;
        private LinearLayout fileItemll;

        public ViewHolder(View FileView, final IListItemClickListener onClickListener) {
            super(FileView);
            icon = FileView.findViewById(R.id.filemanager_icon);
            fileName = FileView.findViewById(R.id.fileName_tv);
            lastModified = FileView.findViewById(R.id.file_lastModified_tv);
            size = FileView.findViewById(R.id.file_size_tv);
            fileItemll = FileView.findViewById(R.id.fileItem_ll);

            fileItemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }


}
