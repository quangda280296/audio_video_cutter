package com.vietmobi.mobile.audiovideocutter.ui.utils;

import android.content.Context;
import android.os.Environment;

import com.vietmobi.mobile.audiovideocutter.R;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {

    public static String getRootMusic(Context context) {
        return Environment.getExternalStorageDirectory().getPath()
                + "/" + context.getString(R.string.app_name) + "/Audio/";
    }

    private static String getRootVideo(Context context) {
        return Environment.getExternalStorageDirectory()
                + File.separator + context.getString(R.string.app_name)+"/Video/";
    }

    public static ArrayList<File> getListFileMusic(Context context) {
        return getListFileMusic(getRootMusic(context));
    }

    private static ArrayList<File> getListFileMusic(String directory) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files;
        File path = new File(directory);
        files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".mp3")) {
                        if (!inFiles.contains(file)) inFiles.add(file);
                    }
                } else if (file.isDirectory()) {
                    inFiles.addAll(getListFileMusic(file.getAbsolutePath()));
                }
            }
        }
        return inFiles;
    }

    public static ArrayList<File> getListFileVideo(Context context) {
        return getListFileVideo(getRootVideo(context));
    }

    private static ArrayList<File> getListFileVideo(String directory) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files;
        File path = new File(directory);
        files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp4")) {
                    if (!inFiles.contains(file)) inFiles.add(file);
                }
            }
        }
        return inFiles;
    }
}
