package com.orpheusdroid.sqliteviewer.model.filemanager;

import android.support.annotation.NonNull;

import com.topjohnwu.superuser.io.SuFile;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class FilesModel implements Comparable<FilesModel> {
    private File file;
    private String name;
    private boolean isDirectory;
    private String size = "";
    private String lastModified = "";

    public FilesModel(File file, boolean isDirectory, long lastModified, int size) {
        this.file = file;
        this.isDirectory = isDirectory;
        this.size = size(size);
        this.lastModified = calculateLastModified(lastModified);
        this.name = file.getName();
    }


    public FilesModel(File file, boolean isDirectory, long lastModified) {
        this.file = file;
        this.isDirectory = isDirectory;
        this.lastModified = calculateLastModified(lastModified);
        this.name = file.getName();
    }

    private String calculateLastModified(long lastModified) {
        long mModified = lastModified;
        //Log.d(Const.TAG, "Date: " + lastModified);
        if (mModified == 0) {
            SuFile file = new SuFile(this.file);
            mModified = file.lastModified();
            //Log.d(Const.TAG, "Root Date:" + file.lastModified() + " Path: " + file.getPath());
        }
        Date modified = new Date(mModified);
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US);
        return format.format(modified);
        //return null;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getSize() {
        return size;
    }

    public void setSize(int size) {
        //this.size = size;
        this.size = size(size);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String size(int size) {
        String hrSize = "";
        if (size < 1024)
            return size + " KB";
        double m = size / 1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (m / 1024.0 > 1) {
            hrSize = dec.format(m / 1024.0).concat(" GB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }

    @Override
    public int compareTo(@NonNull FilesModel filesModel) {
        if (file.isDirectory() == filesModel.file.isDirectory())
            return name.compareTo(filesModel.getName());
        else if (file.isDirectory())
            return -1;
        else
            return 1;
    }
}
