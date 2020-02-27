package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.player;

import com.vietmobi.mobile.audiovideocutter.data.model.Song;

public interface OnUpdateStateMediaListener {

    void onPlayPause(boolean play);

    void onUpdateProgress(int position);

    void onUpdateDurationProgress(int position);

    void onUpdateMusicChange(Song music);

    void onBufferingProgress(int position);
}
