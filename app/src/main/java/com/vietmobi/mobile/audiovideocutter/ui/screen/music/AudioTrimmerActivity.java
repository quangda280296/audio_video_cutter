////  The MIT License (MIT)
//
////  Copyright (c) 2018 Intuz Pvt Ltd.
//
////  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
////  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
////  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
////  furnished to do so, subject to the following conditions:
//
////  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
////  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
////  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
////  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
//package com.vietmobi.mobile.audiovideocutter.ui.screen.music;
//
//
//import android.annotation.SuppressLint;
//import android.app.ProgressDialog;
//import android.content.ContentValues;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.hardware.usb.UsbRequest;
//import android.media.AudioManager;
//import android.media.MediaMetadataRetriever;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.vietmobi.mobile.audiovideocutter.R;
//import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
//import com.vietmobi.mobile.audiovideocutter.data.model.Song;
//import com.vietmobi.mobile.audiovideocutter.databinding.ActivityAudioTrimerBinding;
//import com.vietmobi.mobile.audiovideocutter.ui.screen.library.LibraryActivity;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.Dialogs.FileSaveDialog;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.Ringdroid.SongMetadataReader;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.MarkerView;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.SamplePlayer;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.SoundFile;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.WaveformView;
//import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.utils.Utility;
//
//import java.io.File;
//import java.io.RandomAccessFile;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import life.knowledge4.videotrimmer.utils.TrimVideoUtils;
//
//public class AudioTrimmerActivity extends BaseActivity<ActivityAudioTrimerBinding> implements View.OnClickListener,
//        MarkerView.MarkerListener,
//        WaveformView.WaveformListener {
//
//    /* Audio trimmer*/
//
//
//    private int mNewFileKind;
//
//    //private TextView txtAudioUpload;
//    private TextView txtStartPosition;
//    private TextView txtEndPosition;
//    private MarkerView markerStart;
//    private MarkerView markerEnd;
//    private WaveformView audioWaveform;
//    private TextView txtAudioReset;
//    private TextView txtAudioDone;
//    private ImageView txtAudioPlay;
//    private TextView txtAudioCrop;
//
//    private long mRecordingLastUpdateTime;
//    private double mRecordingTime;
//    private SoundFile mLoadedSoundFile;
//    private SoundFile mRecordedSoundFile;
//    private SamplePlayer mPlayer;
//
//    private MediaPlayer mPlayerNotCut;
//
//    private Handler mHandler;
//
//    private boolean mTouchDragging;
//    private float mTouchStart;
//    private int mTouchInitialOffset;
//    private int mTouchInitialStartPos;
//    private int mTouchInitialEndPos;
//
//    private long mWaveformTouchStartMsec;
//
//    private int mPlayStartMsec;
//    private int mPlayEndMsec;
//    private float mDensity;
//    private int mMarkerLeftInset;
//    private int mMarkerRightInset;
//    private int mMarkerTopOffset;
//    private int mMarkerBottomOffset;
//
//    private int mTextLeftInset;
//    private int mTextRightInset;
//    private int mTextTopOffset;
//    private int mTextBottomOffset;
//
//    private int mOffset;
//    private int mOffsetGoal;
//    private int mFlingVelocity;
//    private int mPlayEndMillSec;
//    private int mWidth;
//    private int mMaxPos;
//    private int mStartPos;
//    private int mEndPos;
//
//    private int positionSeekTo = 0;
//
//    private boolean mStartVisible;
//    private boolean mEndVisible;
//    private int mLastDisplayedStartPos;
//    private int mLastDisplayedEndPos;
//    private boolean mIsPlaying = false;
//    private boolean mKeyDown;
//    private ProgressDialog mProgressDialog;
//    private long mLoadingLastUpdateTime;
//    private boolean mLoadingKeepGoing;
//    private File mFile;
//    private Song musicModel;
//    private String mFilename = "";
//    private String mTitle = "";
//    private String mArtist = "";
//    private Timer timer;
//    private boolean mUserIsSeeking;
//    private int userSelectedPosition = 0;
//
//    public AudioTrimmerActivity() {
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_audio_trimer;
//    }
//
//    @Override
//    public void initView() {
//        // txtAudioUpload = (TextView) findViewById(R.id.txtAudioUpload);
//        txtStartPosition = (TextView) findViewById(R.id.txtStartPosition);
//        txtEndPosition = (TextView) findViewById(R.id.txtEndPosition);
//        markerStart = (MarkerView) findViewById(R.id.markerStart);
//        markerEnd = (MarkerView) findViewById(R.id.markerEnd);
//        audioWaveform = (WaveformView) findViewById(R.id.audioWaveform);
//        txtAudioReset = (TextView) findViewById(R.id.txtAudioReset);
//        txtAudioDone = (TextView) findViewById(R.id.txtAudioDone);
//        //txtAudioPlay = (ImageView) findViewById(R.id.txtAudioPlay);
//        txtAudioPlay = null;
//        txtAudioCrop = (TextView) findViewById(R.id.txtAudioCrop);
//        mRecordedSoundFile = null;
//        mKeyDown = false;
//        audioWaveform.setListener(this);
//        markerStart.setListener(this);
//        markerStart.setAlpha(1f);
//        markerStart.setFocusable(true);
//        markerStart.setFocusableInTouchMode(true);
//        mStartVisible = true;
//        markerEnd.setListener(this);
//        markerEnd.setAlpha(1f);
//        markerEnd.setFocusable(true);
//        markerEnd.setFocusableInTouchMode(true);
//        mEndVisible = true;
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        mDensity = metrics.density;
//
//        /**
//         * Change this for marker handle as per your view
//         */
//        mMarkerLeftInset = (int) (20.5 * mDensity);
//        mMarkerRightInset = (int) (20.5 * mDensity);
//        mMarkerTopOffset = (int) (6 * mDensity);
//        mMarkerBottomOffset = (int) (6 * mDensity);
//
//        /**
//         * Change this for marker handle as per your view
//         */
//
//        mTextLeftInset = (int) (20 * mDensity);
//        mTextTopOffset = (int) (-1 * mDensity);
//        mTextRightInset = (int) (19 * mDensity);
//        mTextBottomOffset = (int) (-40 * mDensity);
//
//        //txtAudioUpload.setOnClickListener(this);
//        txtAudioDone.setOnClickListener(this);
//        txtAudioPlay.setOnClickListener(this);
//        txtAudioCrop.setOnClickListener(this);
//        txtAudioReset.setOnClickListener(this);
//        binding.toolbar.imageSave.setOnClickListener(this);
//        binding.toolbar.imageBack.setOnClickListener(this);
//        binding.imagePlay.setOnClickListener(this);
//
//
//        findViewById(R.id.rew).setOnClickListener(mRewindListener);
//        findViewById(R.id.ffwd).setOnClickListener(mFfwdListener);
//
//        enableDisableButtons();
//    }
//
//    private View.OnClickListener mRewindListener = new View.OnClickListener() {
//        public void onClick(View sender) {
//            if (mIsPlaying) {
//                int newPos = mPlayer.getCurrentPosition() - 5000;
//                if (newPos < mPlayStartMsec)
//                    newPos = mPlayStartMsec;
//                mPlayer.seekTo(newPos);
//            } else {
//                mStartPos = trap(mStartPos - audioWaveform.secondsToPixels(getStep()));
//                updateDisplay();
//                markerStart.requestFocus();
//                markerFocus(markerStart);
//            }
//        }
//    };
//
//    private View.OnClickListener mFfwdListener = new View.OnClickListener() {
//        public void onClick(View sender) {
//            if (mIsPlaying) {
//                int newPos = 5000 + mPlayer.getCurrentPosition();
//                if (newPos > mPlayEndMsec)
//                    newPos = mPlayEndMsec;
//                mPlayer.seekTo(newPos);
//            } else {
//                if (mStartPos > mEndPos) return;
//                mStartPos = trap(mStartPos + audioWaveform.secondsToPixels(getStep()));
//                updateDisplay();
//                markerStart.requestFocus();
//                markerFocus(markerStart);
//            }
//
////            mStartPos = trap(mStartPos + mWaveformView.secondsToPixels((double) getStep()));
////            updateDisplay();
////            mStartMarker.requestFocus();
////            markerFocus(mStartMarker);
//        }
//    };
//
//    protected int getStep() {
//        int maxSeconds = (int) audioWaveform.pixelsToSeconds(audioWaveform.maxPos());
//        if (maxSeconds / 3600 > 0) {
//            return 600;
//        } else if (maxSeconds / 1800 > 0) {
//            return 300;
//        } else if (maxSeconds / 300 > 0) {
//            return 60;
//        }
//        return 5;
//    }
//
//    @Override
//    public void initData() {
//        if (getIntent() != null && getIntent().getExtras().containsKey(Song.class.getName())) {
//            musicModel = (Song) getIntent().getExtras().getSerializable(Song.class.getName());
//            if (musicModel != null) {
//                try {
//                    mFilename = musicModel.mPath;
//                } catch (NullPointerException e) {
//                    mFilename = musicModel.mPath;
//                }
//
//                if (NetworkUtils.isOnline(AudioTrimmerActivity.this)) {
//                    //   AdsUtils.getIntance().displayInterstitial();
//                }
//            }
//        }
//
//        mHandler = new Handler();
//        initView();
//        loadFromFile();
//        mHandler.postDelayed(mTimerRunnable, 100);
//
//        initSeekbar();
//    }
//
//    private void initSeekbar() {
//        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    userSelectedPosition = progress;
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                mUserIsSeeking = true;
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                mUserIsSeeking = false;
//                if (mPlayerNotCut != null) {
//                    mPlayerNotCut.seekTo(userSelectedPosition);
//                }
//            }
//        });
//    }
//
//
//    private Runnable mTimerRunnable = new Runnable() {
//        public void run() {
//            // Updating Text is slow on Android.  Make sure
//            // we only do the update if the text has actually changed.
//            if (mStartPos != mLastDisplayedStartPos) {
//                txtStartPosition.setText(formatTime(mStartPos));
//                mLastDisplayedStartPos = mStartPos;
//            }
//
//            if (mEndPos != mLastDisplayedEndPos) {
//                txtEndPosition.setText(formatTime(mEndPos));
//                mLastDisplayedEndPos = mEndPos;
//            }
//
//            mHandler.postDelayed(mTimerRunnable, 100);
//        }
//    };
//
//
//    private void enableDisableButtons() {
//        runOnUiThread(() -> {
//            if (mIsPlaying) {
//                txtAudioPlay.setImageResource(R.drawable.ic_pause);
//            } else {
//                txtAudioPlay.setImageResource(R.drawable.ic_play);
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.txtAudioPlay:
//                onPlay(mStartPos);
//                break;
//            case R.id.txtAudioDone:
//                double startTime = audioWaveform.pixelsToSeconds(mStartPos);
//                double endTime = audioWaveform.pixelsToSeconds(mEndPos);
//                double difference = endTime - startTime;
//
//                if (difference <= 0) {
//                    Toast.makeText(AudioTrimmerActivity.this, "Trim seconds should be greater than 0 seconds", Toast.LENGTH_SHORT).show();
//                } else if (difference > 60) {
//                    Toast.makeText(AudioTrimmerActivity.this, "Trim seconds should be less than 1 minute", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (mIsPlaying) {
//                        handlePause();
//                    }
//                    saveRingtone(false, 0, mTitle);
//
//                    // markerStart.setVisibility(View.INVISIBLE);
//                    markerEnd.setVisibility(View.INVISIBLE);
//                    txtEndPosition.setVisibility(View.INVISIBLE);
//                }
//                break;
//            case R.id.txtAudioReset:
//                audioWaveform.setIsDrawBorder(true);
//                mPlayer = new SamplePlayer(mRecordedSoundFile);
//                finishOpeningSoundFile(mRecordedSoundFile, 1);
//                audioWaveform.setBackgroundColor(getResources().getColor(R.color.colorWaveformBg));
//                // markerStart.setVisibility(View.VISIBLE);
//                markerEnd.setVisibility(View.VISIBLE);
//                txtEndPosition.setVisibility(View.VISIBLE);
//                break;
//            case R.id.txtAudioCrop:
//                audioWaveform.setIsDrawBorder(true);
//                audioWaveform.setBackgroundColor(getResources().getColor(R.color.colorWaveformBg));
//                // markerStart.setVisibility(View.VISIBLE);
//                markerEnd.setVisibility(View.VISIBLE);
//                txtEndPosition.setVisibility(View.VISIBLE);
//                break;
//            case R.id.image_save:
//                if (mIsPlaying) {
//                    handlePause();
//                }
//
//                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
//                    public void handleMessage(Message response) {
//                        CharSequence newTitle = (CharSequence) response.obj;
//                        mNewFileKind = response.arg1;
//                        saveRingtone(true, 1, String.valueOf(newTitle));
//                    }
//                };
//                Message message = Message.obtain(handler);
//                FileSaveDialog dlog = new FileSaveDialog(
//                        this, getResources(), mTitle, message);
//                dlog.show();
//                break;
//            case R.id.image_back:
//                finish();
//                break;
//            case R.id.image_play:
//                onPlayNotCut(0);
//                break;
//        }
//    }
//
//    /**
//     * Start recording
//     */
//    private void loadFromFile() {
//        mFile = new File(mFilename);
//
//        SongMetadataReader metadataReader = new SongMetadataReader(
//                this, mFilename);
//        mTitle = metadataReader.mTitle;
//        mArtist = metadataReader.mArtist;
//        binding.textMusicName.setText(metadataReader.mTitle);
//
//        mLoadingKeepGoing = true;
//        mProgressDialog = new ProgressDialog(AudioTrimmerActivity.this);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setTitle(R.string.progress_dialog_loading);
//        mProgressDialog.setCancelable(true);
//        mProgressDialog.setOnCancelListener(
//                new DialogInterface.OnCancelListener() {
//                    public void onCancel(DialogInterface dialog) {
//                        mLoadingKeepGoing = false;
//                        finish();
//                    }
//                });
//        mProgressDialog.show();
//
//        final SoundFile.ProgressListener listener =
//                new SoundFile.ProgressListener() {
//                    public boolean reportProgress(double elapsedTime) {
//                        long now = Utility.getCurrentTime();
//                        if (now - mRecordingLastUpdateTime > 5) {
//                            mRecordingTime = elapsedTime;
//                            // Only UI thread can update Views such as TextViews.
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    int min = (int) (mRecordingTime / 60);
//                                    float sec = (float) (mRecordingTime - 60 * min);
//                                }
//                            });
//                            mRecordingLastUpdateTime = now;
//                        }
//
//                        if (now - mLoadingLastUpdateTime > 100) {
//                            mProgressDialog.setProgress(
//                                    (int) (mProgressDialog.getMax() * elapsedTime));
//                            mLoadingLastUpdateTime = now;
//                        }
//                        return mLoadingKeepGoing;
//                    }
//                };
//
//        // Record the audio stream in a background thread
//        Thread mCreateAudioThread = new Thread() {
//            public void run() {
//                try {
//                    mRecordedSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
//                    mProgressDialog.dismiss();
//                    if (mRecordedSoundFile == null) {
//                        mProgressDialog.dismiss();
//                        Runnable runnable = new Runnable() {
//                            public void run() {
//                                Log.e("error >> ", "sound file null");
//                            }
//                        };
//                        mHandler.post(runnable);
//                        return;
//                    }
//                    mPlayer = new SamplePlayer(mRecordedSoundFile);
//
//                    if (mPlayer != null) {
//                        Uri uri = Uri.parse(mFile.getPath());
//                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//                        mmr.setDataSource(AudioTrimmerActivity.this, uri);
//                        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                        int millSecond = Integer.parseInt(durationStr);
//                        runOnUiThread(() -> binding.textDuration.setText(TrimVideoUtils.stringForTime(millSecond)));
//                    }
//                } catch (final Exception e) {
//                    finish();
//                    e.printStackTrace();
//                    return;
//                }
//                mProgressDialog.dismiss();
//                Runnable runnable = new Runnable() {
//                    public void run() {
//                        System.out.println("");
//                        audioWaveform.setIsDrawBorder(true);
//                        finishOpeningSoundFile(mRecordedSoundFile, 0);
//                        markerEnd.setVisibility(View.VISIBLE);
//                        markerStart.setVisibility(View.VISIBLE);
//                    }
//                };
//                mHandler.post(runnable);
//            }
//        };
//        mCreateAudioThread.start();
//    }
//
//    /**
//     * After recording finish do necessary steps
//     *
//     * @param mSoundFile sound file
//     * @param isReset    isReset
//     */
//    private void finishOpeningSoundFile(SoundFile mSoundFile, int isReset) {
//        audioWaveform.setVisibility(View.VISIBLE);
//        audioWaveform.setSoundFile(mSoundFile);
//        audioWaveform.recomputeHeights(mDensity);
//
//        mMaxPos = audioWaveform.maxPos();
//        mLastDisplayedStartPos = -1;
//        mLastDisplayedEndPos = -1;
//
//        mTouchDragging = false;
//
//        mOffset = 0;
//        mOffsetGoal = 0;
//        mFlingVelocity = 0;
//        resetPositions();
//        if (mEndPos > mMaxPos)
//            mEndPos = mMaxPos;
//
//        if (isReset == 1) {
//            mStartPos = audioWaveform.secondsToPixels(0);
//            mEndPos = audioWaveform.secondsToPixels(audioWaveform.pixelsToSeconds(mMaxPos));
//        }
//
//        if (audioWaveform != null && audioWaveform.isInitialized()) {
//            double seconds = audioWaveform.pixelsToSeconds(mMaxPos);
//            int min = (int) (seconds / 60);
//            float sec = (float) (seconds - 60 * min);
////            txtAudioRecordTimeUpdate.setText(String.format(Locale.US, "%02d:%05.2f", min, sec));
//        }
//
//        updateDisplay();
//    }
//
//    /**
//     * Update views
//     */
//
//    private synchronized void updateDisplay() {
//        if (mIsPlaying) {
//            int now = mPlayer.getCurrentPosition();
//            int frames = audioWaveform.millisecsToPixels(now);
//            audioWaveform.setPlayback(frames);
//            Log.e("mWidth >> ", "" + mWidth);
//            setOffsetGoalNoUpdate(frames - mWidth / 2);
//            if (now >= mPlayEndMillSec) {
//                handlePause();
//            }
//        }
//
//        if (!mTouchDragging) {
//            int i2 = this.mFlingVelocity;
//            int i3 = this.mFlingVelocity / 30;
//            if (this.mFlingVelocity > 80) {
//                this.mFlingVelocity -= 80;
//            } else if (this.mFlingVelocity < -80) {
//                this.mFlingVelocity += 80;
//            } else {
//                this.mFlingVelocity = 0;
//            }
//            this.mOffset += i3;
//            if (this.mOffset + (this.mWidth / 2) > this.mMaxPos) {
//                this.mOffset = this.mMaxPos - (this.mWidth / 2);
//                this.mFlingVelocity = 0;
//            }
//            if (this.mOffset < 0) {
//                this.mOffset = 0;
//                this.mFlingVelocity = 0;
//            }
//            this.mOffsetGoal = this.mOffset;
//        } else {
//            int i4 = this.mOffsetGoal - this.mOffset;
//            int i5 = i4 > 10 ? i4 / 10 : i4 > 0 ? 1 : i4 < -10 ? i4 / 10 : i4 < 0 ? -1 : 0;
//            this.mOffset += i5;
//        }
//
//        audioWaveform.setParameters(mStartPos, mEndPos, mOffset);
//        audioWaveform.invalidate();
//
//        markerStart.setContentDescription(
//                " Start Marker" +
//                        formatTime(mStartPos));
//        markerEnd.setContentDescription(
//                " End Marker" +
//                        formatTime(mEndPos));
//
//        int startX = mStartPos - mOffset - mMarkerLeftInset;
//        if (startX + markerStart.getWidth() >= 0) {
//            if (!mStartVisible) {
//                // Delay this to avoid flicker
//                mHandler.postDelayed(new Runnable() {
//                    public void run() {
//                        mStartVisible = true;
//                        markerStart.setAlpha(1f);
//                    }
//                }, 0);
//            }
//        } else {
//            if (mStartVisible) {
//                markerStart.setAlpha(0f);
//                mStartVisible = false;
//            }
//            startX = 0;
//        }
//
//
//        int startTextX = mStartPos - mOffset - mTextLeftInset;
//        if (startTextX + markerStart.getWidth() < 0) {
//            startTextX = 0;
//        }
//
//
//        int endX = mEndPos - mOffset - markerEnd.getWidth() + mMarkerRightInset;
//        if (endX + markerEnd.getWidth() >= 0) {
//            if (!mEndVisible) {
//                // Delay this to avoid flicker
//                mHandler.postDelayed(new Runnable() {
//                    public void run() {
//                        mEndVisible = true;
//                        markerEnd.setAlpha(1f);
//                    }
//                }, 0);
//            }
//        } else {
//            if (mEndVisible) {
//                markerEnd.setAlpha(0f);
//                mEndVisible = false;
//            }
//            endX = 0;
//        }
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
////        params.setMargins(
////                startX,
////                mMarkerTopOffset,
////                -markerStart.getWidth(),
////                -markerStart.getHeight());
//        params.setMargins(
//                startX,
//                audioWaveform.getMeasuredHeight() / 2 + mMarkerTopOffset,
//                -markerStart.getWidth(),
//                -markerStart.getHeight());
//        markerStart.setLayoutParams(params);
//
//
//        params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                startTextX,
//                mTextTopOffset,
//                -txtStartPosition.getWidth(),
//                -txtStartPosition.getHeight());
//        //   txtStartPosition.setLayoutParams(params);
//
//        int endTextX = mEndPos - mOffset - txtEndPosition.getWidth() + mTextRightInset;
//        if (endTextX + markerEnd.getWidth() < 0) {
//            endTextX = 0;
//        }
//
//        params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                endX,
//                audioWaveform.getMeasuredHeight() / 2 + mMarkerBottomOffset,
//                -markerEnd.getWidth(),
//                -markerEnd.getHeight());
////        params.setMargins(
////                endX,
////                audioWaveform.getMeasuredHeight() - markerEnd.getHeight() - mMarkerBottomOffset,
////                -markerEnd.getWidth(),
////                -markerEnd.getHeight());
//        markerEnd.setLayoutParams(params);
//
//
//        params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                endTextX,
//                audioWaveform.getMeasuredHeight() - txtEndPosition.getHeight() - mTextBottomOffset,
//                -txtEndPosition.getWidth(),
//                -txtEndPosition.getHeight());
//
//        //  txtEndPosition.setLayoutParams(params);
//    }
//
//    /**
//     * Reset all positions
//     */
//
//    private void resetPositions() {
//        mStartPos = audioWaveform.secondsToPixels(0.0);
//        mEndPos = audioWaveform.secondsToPixels(audioWaveform.maxPos());
//    }
//
//    private void setOffsetGoalNoUpdate(int offset) {
//        if (mTouchDragging) {
//            return;
//        }
//
//        mOffsetGoal = offset;
//        if (mOffsetGoal + mWidth / 2 > mMaxPos)
//            mOffsetGoal = mMaxPos - mWidth / 2;
//        if (mOffsetGoal < 0)
//            mOffsetGoal = 0;
//    }
//
//    private String formatTime(int pixels) {
//        if (audioWaveform != null && audioWaveform.isInitialized()) {
//            return formatDecimal(audioWaveform.pixelsToSeconds(pixels));
//        } else {
//            return "";
//        }
//    }
//
//    private String formatDecimal(double x) {
//        int xWhole = (int) x;
//        int xFrac = (int) (100 * (x - xWhole) + 0.5);
//
//        if (xFrac >= 100) {
//            xWhole++; //Round up
//            xFrac -= 100; //Now we need the remainder after the round up
//            if (xFrac < 10) {
//                xFrac *= 10; //we need a fraction that is 2 digits long
//            }
//        }
//
//        if (xFrac < 10) {
//            if (xWhole < 10)
//                return "0" + xWhole + ".0" + xFrac;
//            else
//                return xWhole + ".0" + xFrac;
//        } else {
//            if (xWhole < 10)
//                return "0" + xWhole + "." + xFrac;
//            else
//                return xWhole + "." + xFrac;
//
//        }
//    }
//
//    private int trap(int pos) {
//        if (pos < 0)
//            return 0;
//        if (pos > mMaxPos)
//            return mMaxPos;
//        return pos;
//    }
//
//    private void setOffsetGoalStart() {
//        setOffsetGoal(mStartPos - mWidth / 2);
//    }
//
//    private void setOffsetGoalStartNoUpdate() {
//        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
//    }
//
//    private void setOffsetGoalEnd() {
//        setOffsetGoal(mEndPos - mWidth / 2);
//    }
//
//    private void setOffsetGoalEndNoUpdate() {
//        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
//    }
//
//    private void setOffsetGoal(int offset) {
//        setOffsetGoalNoUpdate(offset);
//        updateDisplay();
//    }
//
//    public void markerDraw() {
//    }
//
//    public void markerTouchStart(MarkerView marker, float x) {
//        mTouchDragging = true;
//        mTouchStart = x;
//        mTouchInitialStartPos = mStartPos;
//        mTouchInitialEndPos = mEndPos;
//        handlePause();
//    }
//
//    public void markerTouchMove(MarkerView marker, float x) {
//        float delta = x - mTouchStart;
//
//        if (marker == markerStart) {
//            mStartPos = trap((int) (mTouchInitialStartPos + delta));
//            mEndPos = trap((int) (mTouchInitialEndPos + delta));
//        } else {
//            mEndPos = trap((int) (mTouchInitialEndPos + delta));
//            if (mEndPos < mStartPos)
//                mEndPos = mStartPos;
//        }
//
//        updateDisplay();
//    }
//
//    public void markerTouchEnd(MarkerView marker) {
//        mTouchDragging = false;
//        if (marker == markerStart) {
//            setOffsetGoalStart();
//        } else {
//            setOffsetGoalEnd();
//        }
//    }
//
//    public void markerLeft(MarkerView marker, int velocity) {
//        mKeyDown = true;
//
//        if (marker == markerStart) {
//            int saveStart = mStartPos;
//            mStartPos = trap(mStartPos - velocity);
//            mEndPos = trap(mEndPos - (saveStart - mStartPos));
//            setOffsetGoalStart();
//        }
//
//        if (marker == markerEnd) {
//            if (mEndPos == mStartPos) {
//                mStartPos = trap(mStartPos - velocity);
//                mEndPos = mStartPos;
//            } else {
//                mEndPos = trap(mEndPos - velocity);
//            }
//
//            setOffsetGoalEnd();
//        }
//
//        updateDisplay();
//    }
//
//    public void markerRight(MarkerView marker, int velocity) {
//        mKeyDown = true;
//
//        if (marker == markerStart) {
//            int saveStart = mStartPos;
//            mStartPos += velocity;
//            if (mStartPos > mMaxPos)
//                mStartPos = mMaxPos;
//            mEndPos += (mStartPos - saveStart);
//            if (mEndPos > mMaxPos)
//                mEndPos = mMaxPos;
//
//            setOffsetGoalStart();
//        }
//
//        if (marker == markerEnd) {
//            mEndPos += velocity;
//            if (mEndPos > mMaxPos)
//                mEndPos = mMaxPos;
//
//            setOffsetGoalEnd();
//        }
//
//        updateDisplay();
//    }
//
//    public void markerEnter(MarkerView marker) {
//    }
//
//    public void markerKeyUp() {
//        mKeyDown = false;
//        updateDisplay();
//    }
//
//    public void markerFocus(MarkerView marker) {
//        mKeyDown = false;
//        if (marker == markerStart) {
//            setOffsetGoalStartNoUpdate();
//        } else {
//            setOffsetGoalEndNoUpdate();
//        }
//
//        // Delay updaing the display because if this focus was in
//        // response to a touch event, we want to receive the touch
//        // event too before updating the display.
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                updateDisplay();
//            }
//        }, 100);
//    }
//
//    //
//    // WaveformListener
//    //
//
//    /**
//     * Every time we get a message that our waveform drew, see if we need to
//     * animate and trigger another redraw.
//     */
//    public void waveformDraw() {
//        mWidth = audioWaveform.getMeasuredWidth();
//        if (mOffsetGoal != mOffset && !mKeyDown)
//            updateDisplay();
//        else if (mIsPlaying) {
//            updateDisplay();
//        } else if (mFlingVelocity != 0) {
//            updateDisplay();
//        }
//    }
//
//    public void waveformTouchStart(float x) {
//        mTouchDragging = true;
//        mTouchStart = x;
//        mTouchInitialOffset = mOffset;
//        mFlingVelocity = 0;
//        mWaveformTouchStartMsec = Utility.getCurrentTime();
//    }
//
//    public void waveformTouchMove(float x) {
//        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
//        updateDisplay();
//    }
//
//    public void waveformTouchEnd() {
//        mTouchDragging = false;
//        mOffsetGoal = mOffset;
//
//        long elapsedMsec = Utility.getCurrentTime() - mWaveformTouchStartMsec;
//        if (elapsedMsec < 300) {
//            if (mIsPlaying) {
//                int seekMsec = audioWaveform.pixelsToMillisecs(
//                        (int) (mTouchStart + mOffset));
//                if (seekMsec >= mPlayStartMsec &&
//                        seekMsec < mPlayEndMillSec) {
//                    mPlayer.seekTo(seekMsec);
//                } else {
////                    handlePause();
//                }
//            } else {
//                onPlay((int) (mTouchStart + mOffset));
//            }
//        }
//    }
//
//    private synchronized void handlePause() {
//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.pause();
//        }
//        audioWaveform.setPlayback(-1);
//        mIsPlaying = false;
//        enableDisableButtons();
//    }
//
//    private void enableDisableButtonsNotCut() {
//        runOnUiThread(() -> {
//            if (mPlayerNotCut == null) return;
//            if (mPlayerNotCut != null && mPlayerNotCut.isPlaying()) {
//                binding.imagePlay.setImageResource(R.drawable.pause);
//                //  mPlayNotCut.setContentDescription(getResources().getText(R.string.stop));
//            } else {
//                binding.imagePlay.setImageResource(R.drawable.play);
//                //  mPlayNotCut.setContentDescription(getResources().getText(R.string.play));
//            }
//        });
//    }
//
//    private synchronized void onPlayNotCut(int startPosition) {
//        handlePause();
//        Uri musicUri = Uri.fromFile(mFile);
//        if (mPlayerNotCut == null) {
//            mPlayerNotCut = new MediaPlayer();
//            mPlayerNotCut.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mPlayerNotCut.setDataSource(this, musicUri);
//                mPlayerNotCut.prepareAsync();
//            } catch (java.io.IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            if (mPlayerNotCut.isPlaying()) {
//                mPlayerNotCut.pause();
//            } else {
//                mPlayerNotCut.start();
//            }
//            enableDisableButtonsNotCut();
//            timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    updateProgress();
//                }
//            }, 0, 1000);
//        }
//        try {
//            mPlayerNotCut.setOnPreparedListener(mp -> {
//                mPlayerNotCut.start();
//                enableDisableButtonsNotCut();
//                timer = new Timer();
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        updateProgress();
//                    }
//                }, 0, 1000);
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private synchronized void onPlay(int startPosition) {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//            if (mPlayerNotCut != null && mPlayerNotCut.isPlaying()) {
//                mPlayerNotCut.seekTo(0);
//                mPlayerNotCut.pause();
//                updateProgress();
//            }
//            binding.imagePlay.setImageResource(R.drawable.play);
//            enableDisableButtonsNotCut();
//        }
//
//        if (mIsPlaying) {
//            handlePause();
//            return;
//        }
//
//        if (mPlayer == null) {
//            // Not initialized yet
//            return;
//        }
//
//        try {
//            mPlayStartMsec = audioWaveform.pixelsToMillisecs(startPosition);
//            if (startPosition < mStartPos) {
//                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mStartPos);
//            } else if (startPosition > mEndPos) {
//                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mMaxPos);
//            } else {
//                mPlayEndMillSec = audioWaveform.pixelsToMillisecs(mEndPos);
//            }
//            mPlayer.setOnCompletionListener(new SamplePlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion() {
//                    handlePause();
//                }
//            });
//            mIsPlaying = true;
//
//            mPlayer.seekTo(mPlayStartMsec);
//            mPlayer.start();
//            updateDisplay();
//            enableDisableButtons();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateProgress() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                binding.seekbar.setMax(mPlayerNotCut.getDuration());
//                if (!mUserIsSeeking) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        binding.seekbar.setProgress(mPlayerNotCut.getCurrentPosition(), true);
//                    } else {
//                        binding.seekbar.setProgress(mPlayerNotCut.getCurrentPosition());
//                    }
//                }
//                binding.textDurationStart.setText(TrimVideoUtils.stringForTime(mPlayerNotCut.getCurrentPosition()));
//            }
//        });
//    }
//
//    public void waveformFling(float vx) {
//        mTouchDragging = false;
//        mOffsetGoal = mOffset;
//        mFlingVelocity = (int) (-vx);
//        updateDisplay();
//    }
//
//    public void waveformZoomIn() {
//      /*  audioWaveform.zoomIn();
//        mStartPos = audioWaveform.getStart();
//        mEndPos = audioWaveform.getEnd();
//        mMaxPos = audioWaveform.maxPos();
//        mOffset = audioWaveform.getOffset();
//        mOffsetGoal = mOffset;
//        updateDisplay();*/
//    }
//
//    public void waveformZoomOut() {
//        /*audioWaveform.zoomOut();
//        mStartPos = audioWaveform.getStart();
//        mEndPos = audioWaveform.getEnd();
//        mMaxPos = audioWaveform.maxPos();
//        mOffset = audioWaveform.getOffset();
//        mOffsetGoal = mOffset;
//        updateDisplay();*/
//    }
//
//    /**
//     * Save sound file as ringtone
//     *
//     * @param finish flag for finish
//     */
//
//    private void saveRingtone(boolean save, final int finish, String title) {
//        double startTime = audioWaveform.pixelsToSeconds(mStartPos);
//        double endTime = audioWaveform.pixelsToSeconds(mEndPos);
//        final int startFrame = audioWaveform.secondsToFrames(startTime);
//        final int endFrame = audioWaveform.secondsToFrames(endTime - 0.04);
//        final int duration = (int) (endTime - startTime + 0.5);
//
//        // Create an indeterminate progress dialog
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setTitle(save ? "Saving...." : "Trim....");
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//
//        // Save the sound file in a background thread
//        Thread mSaveSoundFileThread = new Thread() {
//            public void run() {
//                // Try AAC first.
//                String outPath = makeRingtoneFilename(title, Utility.AUDIO_FORMAT);
//                if (outPath == null) {
//                    Log.e(" >> ", "Unable to find unique filename");
//                    return;
//                }
//                File outFile = new File(outPath);
//                try {
//                    // Write the new file
//                    mRecordedSoundFile.WriteFile(outFile, startFrame,
//                            endFrame - startFrame);
//                } catch (Exception e) {
//                    // log the error and try to create a .wav file instead
//                    if (outFile.exists()) {
//                        outFile.delete();
//                    }
//                    e.printStackTrace();
//                }
//
//                mProgressDialog.dismiss();
//
//                final String finalOutPath = outPath;
//                Runnable runnable = new Runnable() {
//                    public void run() {
//                        afterSavingRingtone(title,
//                                finalOutPath,
//                                duration, finish);
//                    }
//                };
//                mHandler.post(runnable);
//            }
//        };
//        mSaveSoundFileThread.start();
//    }
//
//    /**
//     * After saving as ringtone set its content values
//     *
//     * @param title    title
//     * @param outPath  output path
//     * @param duration duration of file
//     * @param finish   flag for finish
//     */
//    private void afterSavingRingtone(CharSequence title,
//                                     String outPath,
//                                     int duration, int finish) {
//        File outFile = new File(outPath);
//        long fileSize = outFile.length();
//        String artist = "" + getResources().getText(R.string.artist_name);
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DATA, outPath);
//        values.put(MediaStore.MediaColumns.TITLE, title.toString());
//        values.put(MediaStore.MediaColumns.SIZE, fileSize);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, Utility.AUDIO_MIME_TYPE);
//
//        values.put(MediaStore.Audio.Media.ARTIST, artist);
//        values.put(MediaStore.Audio.Media.DURATION, duration);
//        values.put(MediaStore.Audio.Media.IS_RINGTONE,
//                mNewFileKind == FileSaveDialog.FILE_KIND_RINGTONE);
//        values.put(MediaStore.Audio.Media.IS_NOTIFICATION,
//                mNewFileKind == FileSaveDialog.FILE_KIND_NOTIFICATION);
//        values.put(MediaStore.Audio.Media.IS_ALARM,
//                mNewFileKind == FileSaveDialog.FILE_KIND_ALARM);
//        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
//
//        Uri uri = MediaStore.Audio.Media.getContentUriForPath(outPath);
//        final Uri newUri = getContentResolver().insert(uri, values);
//        Log.e("final URI >> ", newUri + " >> " + outPath);
//
//        if (finish == 0) {
//            loadFromFile(outPath);
//        } else if (finish == 1) {
//            Toast.makeText(this,
//                    R.string.save_success_message,
//                    Toast.LENGTH_SHORT)
//                    .show();
//            Intent intent = new Intent(this, LibraryActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    /**
//     * Generating name for ringtone
//     *
//     * @param title     title of file
//     * @param extension extension for file
//     * @return filename
//     */
//
//    private String makeRingtoneFilename(CharSequence title, String extension) {
//        String subdir = "";
//        String externalRootDir = Environment.getExternalStorageDirectory().getPath()
//                + File.separator + getString(R.string.app_name);
//        if (!externalRootDir.endsWith("/")) {
//            externalRootDir += "/";
//        }
//
//        switch (mNewFileKind) {
//            default:
//            case FileSaveDialog.FILE_KIND_MUSIC:
//                // TODO(nfaralli): can directly use Environment.getExternalStoragePublicDirectory(
//                // Environment.DIRECTORY_MUSIC).getPath() instead
//                subdir = "Audio/music/";
//                break;
//            case FileSaveDialog.FILE_KIND_ALARM:
//                subdir = "Audio/alarms/";
//                break;
//            case FileSaveDialog.FILE_KIND_NOTIFICATION:
//                subdir = "Audio/notifications/";
//                break;
//            case FileSaveDialog.FILE_KIND_RINGTONE:
//                subdir = "Audio/ringtones/";
//                break;
//        }
//
//        String parentDir = externalRootDir + subdir;
//
//        // Create the parent directory
//        File parentDirFile = new File(parentDir);
//        parentDirFile.mkdirs();
//
//        // If we can't write to that special path, try just writing
//        // directly to the sdcard
//        if (!parentDirFile.isDirectory()) {
//            parentDir = externalRootDir;
//        }
//
//        // Turn the title into a filename
//        String filename = "";
//        for (int i = 0; i < title.length(); i++) {
//            if (Character.isLetterOrDigit(title.charAt(i))) {
//                filename += title.charAt(i);
//            }
//        }
//
//        // Try to make the filename unique
//        String path = null;
//        for (int i = 0; i < 100; i++) {
//            String testPath;
//            if (i > 0)
//                testPath = parentDir + filename + i + extension;
//            else
//                testPath = parentDir + filename + extension;
//
//            try {
//                RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
//                f.close();
//            } catch (Exception e) {
//                // Good, the file didn't exist
//                path = testPath;
//                break;
//            }
//        }
//
//        return path;
//    }
//
//    /**
//     * Load file from path
//     *
//     * @param mFilename file name
//     */
//
//    private void loadFromFile(String mFilename) {
//        mFile = new File(mFilename);
////        SongMetadataReader metadataReader = new SongMetadataReader(this, mFilename);
//        mLoadingLastUpdateTime = Utility.getCurrentTime();
//        mLoadingKeepGoing = true;
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setTitle("Loading ...");
//        mProgressDialog.show();
//
//        final SoundFile.ProgressListener listener =
//                new SoundFile.ProgressListener() {
//                    public boolean reportProgress(double fractionComplete) {
//
//                        long now = Utility.getCurrentTime();
//                        if (now - mLoadingLastUpdateTime > 100) {
//                            mProgressDialog.setProgress(
//                                    (int) (mProgressDialog.getMax() * fractionComplete));
//                            mLoadingLastUpdateTime = now;
//                        }
//                        return mLoadingKeepGoing;
//                    }
//                };
//
//        // Load the sound file in a background thread
//        Thread mLoadSoundFileThread = new Thread() {
//            public void run() {
//                try {
//                    mLoadedSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
//                    if (mLoadedSoundFile == null) {
//                        mProgressDialog.dismiss();
//                        String name = mFile.getName().toLowerCase();
//                        String[] components = name.split("\\.");
//                        String err;
//                        if (components.length < 2) {
//                            err = "No Extension";
//                        } else {
//                            err = "Bad Extension";
//                        }
//                        final String finalErr = err;
//                        Log.e(" >> ", "" + finalErr);
//                        return;
//                    }
//                    mPlayer = new SamplePlayer(mLoadedSoundFile);
//                } catch (final Exception e) {
//                    mProgressDialog.dismiss();
//                    e.printStackTrace();
//                    return;
//                }
//                mProgressDialog.dismiss();
//                if (mLoadingKeepGoing) {
//                    Runnable runnable = new Runnable() {
//                        public void run() {
//                            audioWaveform.setVisibility(View.INVISIBLE);
//                            audioWaveform.setBackgroundColor(getResources().getColor(R.color.waveformUnselectedBackground));
//                            audioWaveform.setIsDrawBorder(false);
//                            finishOpeningSoundFile(mLoadedSoundFile, 0);
//                        }
//                    };
//                    mHandler.post(runnable);
//                }
//            }
//        };
//        mLoadSoundFileThread.start();
//    }
//}
