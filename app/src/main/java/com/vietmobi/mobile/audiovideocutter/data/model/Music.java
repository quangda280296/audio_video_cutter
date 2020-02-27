package com.vietmobi.mobile.audiovideocutter.data.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Music implements Serializable {

    public String _ID;
    public String mSongsName;
    public String mArtistName;
    public String mDuration;
    public String mPath;
    public String mAlbum;
    public String mFileType;
    public String mAlbumId;

    public Music() {

    }

    public Music(String _ID,
                 String songsName,
                 String artistName,
                 String duration,
                 String album,
                 String path,
                 String albumId,
                 String fileType) {
        this._ID = _ID;
        mSongsName = songsName;
        mArtistName = artistName;
        mDuration = duration;
        mPath = path;
        mAlbum = album;
        mAlbumId = albumId;
        mFileType = fileType;
    }

    public static ArrayList<Music> getListMusic(ArrayList<File> files) {
        ArrayList<Music> musicArrayList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Music music = new Music();
            music.mSongsName = files.get(i).getName().replace(".mp3", "");
            music.mPath = files.get(i).getPath();
            musicArrayList.add(music);
        }
        return musicArrayList;
    }
}
