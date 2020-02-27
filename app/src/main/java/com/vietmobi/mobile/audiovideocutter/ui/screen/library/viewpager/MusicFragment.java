package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseFragment;
import com.vietmobi.mobile.audiovideocutter.data.model.Song;
import com.vietmobi.mobile.audiovideocutter.databinding.FragmentListMusicBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.LibraryActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.player.MediaPlayerMusic;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.player.OnMediaPlayerListener;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.player.OnUpdateStateMediaListener;
import com.vietmobi.mobile.audiovideocutter.ui.screen.music.SongAdapter;
import com.vietmobi.mobile.audiovideocutter.ui.utils.EmptyUtil;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SlideBindings;

import java.util.ArrayList;

import life.knowledge4.videotrimmer.utils.TrimVideoUtils;

public class MusicFragment extends BaseFragment<FragmentListMusicBinding>
        implements View.OnClickListener, OnUpdateStateMediaListener, OnViewPagerChangeListener {

    private SongAdapter songAdapter;
    private OnMediaPlayerListener onMediaPlayerListener;
    private int userSelectedPosition = 0;
    private boolean mUserIsSeeking;

    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_list_music;
    }

    @Override
    public void initView() {
        binding.play.setOnClickListener(this);
        binding.playNext.setOnClickListener(this);
        binding.playPrevious.setOnClickListener(this);
        binding.imageClear.setOnClickListener(this::onClick);
        initMediaPlayer();
        scrollRecyclerListener();
        if (getBaseActivity() instanceof LibraryActivity) {
            ((LibraryActivity) getBaseActivity()).setOnViewPagerChangeListener(this);
        }
    }

    private void initMediaPlayer() {
        MediaPlayerMusic mediaPlayerMusic = new MediaPlayerMusic(getBaseActivity());
        onMediaPlayerListener = mediaPlayerMusic;
        mediaPlayerMusic.setOnUpdateStateMediaListener(this);
    }

    private void scrollRecyclerListener() {
        binding.recyclerListData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                } else {
                    // Scrolling down
                    if (!SlideBindings.isUp && onMediaPlayerListener.isPlaying()) {
                        SlideBindings.slideUp(binding.layoutControl);
                    }
                }
            }
        });
    }

    @Override
    public void onPageChanged() {
        if (onMediaPlayerListener.isPlaying()) {
            onMediaPlayerListener.pause();
        }
    }

    @Override
    public void initData() {
        songAdapter = new SongAdapter();
        binding.recyclerListData.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        binding.recyclerListData.setAdapter(songAdapter);
        songAdapter.setOnRecyclerViewItemClickListener((music, position) -> {
            if (!SlideBindings.isUp) {
                SlideBindings.slideUp(binding.layoutControl);
            }
            onMediaPlayerListener.loadMedia(position);
            binding.textTitleMusic.setText(EmptyUtil.isNotEmpty(music.title) ? music.title : "");
        });
        getMusic();

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = false;
                onMediaPlayerListener.seekTo(userSelectedPosition);
            }
        });
    }

    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    private void getMusic() {
        RxPermissions rxPermissions = new RxPermissions(getBaseActivity());
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        new AsyncTask<String, Void, ArrayList<Song>>() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                binding.progressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected ArrayList<Song> doInBackground(String... voids) {
                                return Song.getListSong(getBaseActivity());
                            }

                            @Override
                            protected void onPostExecute(ArrayList<Song> musicArrayList) {
                                super.onPostExecute(musicArrayList);
                                if (musicArrayList != null) {
                                    if (songAdapter != null) {
                                        songAdapter.setDataList(getBaseActivity(), musicArrayList);
                                        onMediaPlayerListener.loadMedia(musicArrayList, -1);
                                    }
                                }
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        }.execute();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                onMediaPlayerListener.playPause();
                break;
            case R.id.play_next:
                onMediaPlayerListener.next();
                break;
            case R.id.play_previous:
                onMediaPlayerListener.previous();
                break;
            case R.id.image_clear:
                SlideBindings.slideDown(binding.layoutControl);
                if (onMediaPlayerListener != null) {
                    onMediaPlayerListener.release();
                }
                break;
        }
    }

    @Override
    public void onPlayPause(boolean play) {
        if (play) {
            binding.play.setImageResource(R.drawable.play_btn_pause);
        } else {
            binding.play.setImageResource(R.drawable.play_btn_play);
        }
    }

    // buffered only file network
    @Override
    public void onBufferingProgress(int position) {
        binding.seekBar.setSecondaryProgress(position);
    }

    @Override
    public void onUpdateDurationProgress(int position) {
        if (getBaseActivity() == null) return;
        getBaseActivity().runOnUiThread(() ->
                binding.timeDuration.setText(TrimVideoUtils.stringForTime(position)));
    }

    @Override
    public void onUpdateProgress(int currentPosition) {
        if (getBaseActivity() == null) return;
        getBaseActivity().runOnUiThread(() -> {
            binding.seekBar.setMax(onMediaPlayerListener.getDuration());
            if (!mUserIsSeeking) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.seekBar.setProgress(currentPosition, true);
                } else {
                    binding.seekBar.setProgress(currentPosition);
                }
            }
            System.out.println("onUpdateProgress" + TrimVideoUtils.stringForTime(currentPosition));
            binding.timeDurationStart.setText(TrimVideoUtils.stringForTime(currentPosition));
        });
    }

    @Override
    public void onUpdateMusicChange(Song music) {
        binding.textTitleMusic.setText(music.title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SlideBindings.isUp = false;
        onMediaPlayerListener.release();
    }
}
