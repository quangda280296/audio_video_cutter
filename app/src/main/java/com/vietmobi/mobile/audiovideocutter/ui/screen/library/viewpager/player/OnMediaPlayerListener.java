package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.player;

import com.vietmobi.mobile.audiovideocutter.data.model.Song;

import java.util.List;

public interface OnMediaPlayerListener {

    void loadMedia(List<Song> musicArrayList, int position);

    void loadMedia(int position);

    void release();

    boolean isPlaying();

    void playPause();

    void pause();

    void next();

    void previous();

    int getDuration();

    int getCurrentPosition();

    void seekTo(int position);

    void onBufferingProgress(int position);

    void onMusicChanged(Song track);

}
