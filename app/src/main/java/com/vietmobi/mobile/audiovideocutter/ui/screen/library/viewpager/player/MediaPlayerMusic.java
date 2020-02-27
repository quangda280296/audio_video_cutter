package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.vietmobi.mobile.audiovideocutter.data.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerMusic implements OnMediaPlayerListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener {

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> musicArrayList;
    public int currentlyPlaying = -1;
    private OnUpdateStateMediaListener onUpdateStateMediaListener;
    private Timer timer;

    public void setOnUpdateStateMediaListener(OnUpdateStateMediaListener onUpdateStateMediaListener) {
        this.onUpdateStateMediaListener = onUpdateStateMediaListener;
    }

    public MediaPlayerMusic(Context context) {
        mContext = context;
        musicArrayList = new ArrayList<>();
    }

    @Override
    public void loadMedia(int position) {
        if (isPlaying() && currentlyPlaying == position) {
            mMediaPlayer.seekTo(getCurrentPosition());
        } else {
            loadMedia(null, position);
        }
    }

    @Override
    public void loadMedia(List<Song> data, int position) {
        currentlyPlaying = position;
        if (data != null) {
            this.musicArrayList.addAll(data);
        }
        if (musicArrayList != null && musicArrayList.size() != 0 && currentlyPlaying != -1) {
            updateMusicQueue();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        updateProgress();
        if (onUpdateStateMediaListener != null) {
            onUpdateStateMediaListener.onPlayPause(true);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        double ratio = percent / 100.0;
        int bufferingLevel = (int) (mp.getDuration() * ratio);
        onBufferingProgress(bufferingLevel);
    }

    @Override
    public void onBufferingProgress(int position) {
        System.out.println("onBufferingProgress" + position + onUpdateStateMediaListener);
        if (onUpdateStateMediaListener != null) {
            onUpdateStateMediaListener.onBufferingProgress(position);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentlyPlaying < musicArrayList.size()) {
            if (currentlyPlaying == musicArrayList.size() - 1) {
                currentlyPlaying = -1;
                return;
            } else {
                currentlyPlaying++;
            }
            updateMusicQueue();
            updateProgress();
        }

        if (timer != null) {
            timer.cancel();
        }
    }

    private void updateMusicQueue() {
        release();
        System.out.println("updateMusicQueue" + currentlyPlaying);
        System.out.println("updateMusicQueue :: ::" + musicArrayList.size());
        File f = new File(musicArrayList.get(currentlyPlaying).mPath);
        Uri musicUri = Uri.fromFile(f);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(mContext, musicUri);
                if (currentlyPlaying != -1) {
                    mMediaPlayer.prepareAsync();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            onMusicChanged(musicArrayList.get(currentlyPlaying));
        }
    }


    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void playPause() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                onUpdateStateMediaListener.onPlayPause(true);
            } else {
                onUpdateStateMediaListener.onPlayPause(false);
                mMediaPlayer.pause();
            }
            updateProgress();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                onUpdateStateMediaListener.onPlayPause(false);
                mMediaPlayer.pause();
            }
            updateProgress();
        }
    }

    private void updateProgress() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (onUpdateStateMediaListener != null) {
                    onUpdateStateMediaListener.onUpdateProgress(getCurrentPosition());
                    onUpdateStateMediaListener.onUpdateDurationProgress(getDuration());
                }
            }
        }, 0, 1000);
    }

    @Override
    public void next() {
        if (currentlyPlaying == musicArrayList.size() - 1) return;
        if (currentlyPlaying < musicArrayList.size()) {
            currentlyPlaying++;
            updateMusicQueue();
        }
    }

    @Override
    public void previous() {
        if (currentlyPlaying == 0) return;
        if (currentlyPlaying < musicArrayList.size()) {
            currentlyPlaying--;
            updateMusicQueue();
            onMusicChanged(musicArrayList.get(currentlyPlaying));
        }
    }

    @Override
    public void onMusicChanged(Song music) {
        if (onUpdateStateMediaListener != null) {
            onUpdateStateMediaListener.onUpdateMusicChange(music);
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (timer != null) {
            timer.cancel();
        }
    }
}
