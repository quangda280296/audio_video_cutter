package com.vietmobi.mobile.audiovideocutter.data.model;

import android.content.Context;
import android.os.Environment;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SongLoader;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {

    public long albumId;
    public String albumName;
    public long artistId;
    public String artistName;
    public int duration;
    public long id;
    public String title;
    public int trackNumber;
    public String mPath = "";

    public Song() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1;
        this.trackNumber = -1;
    }

    public Song(long _id, long _albumId, long _artistId, String _title,
                String _artistName, String _albumName, int _duration, int _trackNumber, String path) {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
        this.mPath = path;
    }

    private static String getRootMusic(Context context) {
        return Environment.getExternalStorageDirectory().getPath()
                + "/" + context.getString(R.string.app_name) + "/Audio/";
    }

    public static ArrayList<Song> getListSong(Context context) {
        return getListSong(context, getRootMusic(context));
    }

    private static ArrayList<Song> getListSong(Context context, String directory) {
        ArrayList<Song> songArrayList = new ArrayList<>();
        File[] files;
        File path = new File(directory);
        files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    songArrayList.addAll(SongLoader.getSongListInFolder(context, file.getPath()));
                } else {
                    songArrayList.addAll(SongLoader.getSongListInFolder(context, directory));
                }
            }
        }
        return songArrayList;
    }
}
