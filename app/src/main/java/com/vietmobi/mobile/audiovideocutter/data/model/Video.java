package com.vietmobi.mobile.audiovideocutter.data.model;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SongLoader;

import java.io.File;
import java.util.ArrayList;

import iknow.android.utils.DateUtil;

public class Video {

    public byte[] thumb;
    public String duration;
    public String path;

    public static ArrayList<Video> getListVideo(ArrayList<File> files) {
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        ArrayList<Video> videoArrayList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Video video = new Video();
            mMetadataRetriever.setDataSource(files.get(i).getPath());
            String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            video.duration = DateUtil.convertSecondsToTime(Integer.parseInt(duration) / 1000);
            video.path = files.get(i).getAbsolutePath();
//            try {
//                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(files.get(i).getPath(),
//                        MediaStore.Images.Thumbnails.MINI_KIND);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                video.thumb = stream.toByteArray();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            videoArrayList.add(video);
        }
        return videoArrayList;
    }
}
