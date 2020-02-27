package com.vietmobi.mobile.audiovideocutter.ui.screen.music;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semantive.waveformandroid.waveform.Segment;
import com.semantive.waveformandroid.waveform.soundfile.CheapSoundFile;
import com.semantive.waveformandroid.waveform.view.MarkerView;
import com.semantive.waveformandroid.waveform.view.WaveformView;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.data.model.Song;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityAudioTrimerBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.home.HomeActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.LibraryActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Dialogs.FileSaveDialog;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.PublicMethod;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Ringdroid.SongMetadataReader;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SongLoader;
import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.utils.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import life.knowledge4.videotrimmer.utils.TrimVideoUtils;

public class MusicAudioCutterActivity extends BaseActivity<ActivityAudioTrimerBinding>
        implements MarkerView.MarkerListener, WaveformView.WaveformListener, View.OnClickListener {

    private Song musicModel;
    private String mFilename = "";
    public static final String TAG = "WaveformFragment";

    protected long mLoadingLastUpdateTime;
    protected boolean mLoadingKeepGoing;
    protected ProgressDialog mProgressDialog;
    protected CheapSoundFile mSoundFile;
    protected File mFile;
    protected WaveformView mWaveformView;
    protected MarkerView mStartMarker;
    protected MarkerView mEndMarker;
    protected EditText mStartText;
    protected EditText mEndText;
    protected TextView mInfo;
    protected ImageView mPlayButton;
    protected ImageButton mRewindButton;
    protected ImageButton mFfwdButton;
    protected boolean mKeyDown;
    protected String mCaption = "";
    protected int mWidth;
    protected int mMaxPos;
    protected int mStartPos;
    protected int mEndPos;
    protected boolean mStartVisible;
    protected boolean mEndVisible;
    protected int mLastDisplayedStartPos;
    protected int mLastDisplayedEndPos;
    protected int mOffset;
    protected int mOffsetGoal;
    protected int mFlingVelocity;
    protected int mPlayStartMsec;
    protected int mPlayStartOffset;
    protected int mPlayEndMsec;
    protected Handler mHandler;
    protected boolean mIsPlaying;
    protected MediaPlayer mPlayer;
    protected boolean mTouchDragging;
    protected float mTouchStart;
    protected int mTouchInitialOffset;
    protected int mTouchInitialStartPos;
    protected int mTouchInitialEndPos;
    protected long mWaveformTouchStartMsec;
    protected float mDensity;
    protected int mMarkerLeftInset;
    protected int mMarkerRightInset;
    protected int mMarkerTopOffset;
    protected int mMarkerBottomOffset;
    private MediaPlayer mPlayerNotCut;
    private Timer timer;
    private boolean mUserIsSeeking;
    private int userSelectedPosition = 0;


    private Thread mSaveSoundFileThread;
    private int mNewFileKind = 0;
    private String mTitle = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_audio_trimer;
    }

    @Override
    public void initView() {
        loadGui();
        binding.txtAudioReset.setOnClickListener(this);
        binding.txtAudioDone.setOnClickListener(this);
        binding.toolbar.imageBack.setOnClickListener(this);
        binding.toolbar.imageSave.setOnClickListener(this);
       // binding.imagePlay.setOnClickListener(this);
        enableDisableButtons();
        initSeekbar();
        AdsUtils.getIntance().initAds(MusicAudioCutterActivity.this, R.id.banner);
    }

    private void initSeekbar() {
       /* binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                if (mPlayerNotCut != null) {
                    mPlayerNotCut.seekTo(userSelectedPosition);
                }
            }
        });*/
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras().containsKey(Song.class.getName())) {
            musicModel = (Song) getIntent().getExtras().getSerializable(Song.class.getName());
            if (musicModel != null) {
                try {
                    mFilename = musicModel.mPath;
                } catch (NullPointerException e) {
                    mFilename = musicModel.mPath;
                }
                mPlayer = null;
                mIsPlaying = false;
                mSoundFile = null;
                mKeyDown = false;

                mHandler = new Handler();

                mHandler.postDelayed(mTimerRunnable, 100);

                if (mSoundFile == null) {
                    loadFromFile();
                } else {
                    mHandler.post(() -> finishOpeningSoundFile());
                }
                if (NetworkUtils.isOnline(MusicAudioCutterActivity.this)) {
                    AdsUtils.getIntance().displayInterstitial();
                }
            }
        }

        mPlayer = null;
        mIsPlaying = false;
        mSoundFile = null;
        mKeyDown = false;
    }


    @Override
    public void onDestroy() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        mSoundFile = null;
        mWaveformView = null;
        mLoadingKeepGoing = false;
        closeThread(mSaveSoundFileThread);
        mSaveSoundFileThread = null;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer = null;
        }

        mHandler.removeCallbacks(mTimerRunnable);

        super.onDestroy();
    }

    private void closeThread(Thread thread) {
        if (thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    //
    // WaveformListener
    //

    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another redraw.
     */
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown)
            updateDisplay();
        else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        updateDisplay();
    }

    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = System.currentTimeMillis() - mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs((int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec && seekMsec < mPlayEndMsec) {
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    public void waveformZoomIn() {
       /* mWaveformView.zoomIn();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();*/
    }

    public void waveformZoomOut() {
       /* mWaveformView.zoomOut();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();*/
    }

    //
    // MarkerListener
    //

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(() -> updateDisplay(), 100);
    }

    //
    // Internal methods
    //

    protected void loadGui() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int) (20.5 * mDensity);
        mMarkerRightInset = (int) (20.5 * mDensity);
        mMarkerTopOffset = (int) (6 * mDensity);
        mMarkerBottomOffset = (int) (6 * mDensity);

        mStartText = (EditText) findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = (EditText) findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);

        mPlayButton = (ImageView) findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);
        mRewindButton = (ImageButton) findViewById(R.id.rew);
        mRewindButton.setOnClickListener(getRewindListener());
        mFfwdButton = (ImageButton) findViewById(R.id.ffwd);
        mFfwdButton.setOnClickListener(getFwdListener());

        TextView markStartButton = (TextView) findViewById(R.id.mark_start);
        markStartButton.setOnClickListener(mMarkStartListener);
        TextView markEndButton = (TextView) findViewById(R.id.mark_end);
        markEndButton.setOnClickListener(mMarkEndListener);

        enableDisableButtons();

        mWaveformView = (WaveformView) findViewById(R.id.waveform);
        mWaveformView.setListener(this);
        mWaveformView.setSegments(getSegments());

      /*  mInfo = (TextView) findViewById(R.id.info);
        mInfo.setText(mCaption);*/

        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null && !mWaveformView.hasSoundFile()) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = (MarkerView) findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setImageAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker = (MarkerView) findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setImageAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;

        updateDisplay();
    }

    protected void loadFromFile() {
        mFile = new File(mFilename);
        SongMetadataReader metadataReader = new SongMetadataReader(
                this, mFilename);
        mTitle = metadataReader.mTitle;
        binding.textMusicName.setText(metadataReader.mTitle);

        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener((DialogInterface dialog) -> mLoadingKeepGoing = false);
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener = (double fractionComplete) -> {
            long now = System.currentTimeMillis();
            if (now - mLoadingLastUpdateTime > 100) {
                mProgressDialog.setProgress(
                        (int) (mProgressDialog.getMax() * fractionComplete));
                mLoadingLastUpdateTime = now;
            }
            return mLoadingKeepGoing;
        };

        // Create the MediaPlayer in a background thread
        new Thread() {
            public void run() {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepare();
                    mPlayer = player;

                } catch (final java.io.IOException e) {
                    Log.e(TAG, "Error while creating media player", e);
                }
            }
        }.start();

        // Load the sound file in a background thread
        new Thread() {
            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), listener);
                } catch (final Exception e) {
                    Log.e(TAG, "Error while loading sound file", e);
                    mProgressDialog.dismiss();
                    mInfo.setText(e.toString());
                    return;
                }
                if (mLoadingKeepGoing) {
                    mHandler.post(() -> {
                        mWaveformView.setIsDrawBorder(true);
                        finishOpeningSoundFile();
                    });
                }
            }
        }.start();
    }

    protected void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();

        mCaption = mSoundFile.getFiletype() + ", " +
                mSoundFile.getSampleRate() + " Hz, " +
                mSoundFile.getAvgBitrateKbps() + " kbps, " +
                formatTime(mMaxPos) + " " + getResources().getString(R.string.time_seconds);
//        mInfo.setText(mCaption);
        mProgressDialog.dismiss();
        updateDisplay();
    }

    protected synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                float saveVel = mFlingVelocity;

                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(getResources().getText(R.string.start_marker) + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription(getResources().getText(R.string.end_marker) + " " + formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(() -> {
                    mStartVisible = true;
                    mStartMarker.setImageAlpha(255);
                }, 0);
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setImageAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() + mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(() -> {
                    mEndVisible = true;
                    mEndMarker.setImageAlpha(255);
                }, 0);
            }
        } else {
            if (mEndVisible) {
                mEndMarker.setImageAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                startX,
//                mMarkerTopOffset,
//                -markerStart.getWidth(),
//                -markerStart.getHeight());
        params.setMargins(
                startX,
                mWaveformView.getMeasuredHeight() / 2 / 2,
                -mStartMarker.getWidth(),
                -mStartMarker.getHeight());

        mStartMarker.setLayoutParams(params);


        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(
                endX,
                mWaveformView.getMeasuredHeight() / 2 + mStartMarker.getHeight(),
                -mEndMarker.getWidth(),
                -mEndMarker.getHeight());

        mEndMarker.setLayoutParams(params);
    }

    protected Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating an EditText is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos && !mStartText.hasFocus()) {
                mStartText.setText(PublicMethod.converTime(formatTime(mStartPos)));
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos && !mEndText.hasFocus()) {
                mEndText.setText(PublicMethod.converTime(formatTime(mEndPos)));
                mLastDisplayedEndPos = mEndPos;
                if (mPlayer != null) {
                    //binding.textDuration.setText(TrimVideoUtils.stringForTime(mPlayer.getDuration()));
                }
            }
            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };

    protected void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.ic_pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
        } else {
            mPlayButton.setImageResource(R.drawable.ic_play);
            mPlayButton.setContentDescription(getResources().getText(R.string.play));
        }
    }

    protected void resetPositions() {
        mStartPos = 0;
        mEndPos = mMaxPos;
    }

    protected int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    protected void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    protected void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    protected void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    protected void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    protected void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    protected void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    protected String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    protected String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    protected synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    private void enableDisableButtonsNotCut() {
       /* runOnUiThread(() -> {
            if (mPlayerNotCut == null) return;
            if (mPlayerNotCut != null && mPlayerNotCut.isPlaying()) {
                binding.imagePlay.setImageResource(R.drawable.pause);
                //  mPlayNotCut.setContentDescription(getResources().getText(R.string.stop));
            } else {
                binding.imagePlay.setImageResource(R.drawable.play);
                //  mPlayNotCut.setContentDescription(getResources().getText(R.string.play));
            }
        });*/
    }

    private synchronized void onPlayNotCut(int startPosition) {
        handlePause();
        Uri musicUri = Uri.fromFile(mFile);
        if (mPlayerNotCut == null) {
            mPlayerNotCut = new MediaPlayer();
            mPlayerNotCut.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayerNotCut.setDataSource(this, musicUri);
                mPlayerNotCut.prepareAsync();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mPlayerNotCut.isPlaying()) {
                mPlayerNotCut.pause();
            } else {
                mPlayerNotCut.start();
            }
            enableDisableButtonsNotCut();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateProgress();
                }
            }, 0, 1000);
        }
        try {
            mPlayerNotCut.setOnPreparedListener(mp -> {
                mPlayerNotCut.start();
                enableDisableButtonsNotCut();
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        updateProgress();
                    }
                }, 0, 1000);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProgress() {
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
    }


    protected synchronized void onPlay(int startPosition) {
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

        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            int startFrame = mWaveformView.secondsToFrames(mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(), startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    Log.e(TAG, "Exception trying to play file subset", e);
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }

            mPlayer.setOnCompletionListener((MediaPlayer mediaPlayer) -> handlePause());
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception e) {
            Log.e(TAG, "Exception while playing file", e);
        }
    }

    protected void enableZoomButtons() {
    }

    protected View.OnClickListener mPlayListener = new View.OnClickListener() {
        public void onClick(View sender) {
            onPlay(mStartPos);
        }
    };

    protected View.OnClickListener mRewindListener = new View.OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec)
                    newPos = mPlayStartMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartPos = trap(mStartPos - mWaveformView.secondsToPixels(getStep()));
                updateDisplay();
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    protected View.OnClickListener mFfwdListener = new View.OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = 5000 + mPlayer.getCurrentPosition();
                if (newPos > mPlayEndMsec)
                    newPos = mPlayEndMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartPos = trap(mStartPos + mWaveformView.secondsToPixels(getStep()));
                updateDisplay();
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    protected View.OnClickListener mMarkStartListener = new View.OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };

    protected View.OnClickListener mMarkEndListener = new View.OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };

    protected TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
        }
    };

    protected List<Segment> getSegments() {
        return null;
    }

    protected View.OnClickListener getFwdListener() {
        return mFfwdListener;
    }

    protected View.OnClickListener getRewindListener() {
        return mRewindListener;
    }

    protected int getStep() {
        int maxSeconds = (int) mWaveformView.pixelsToSeconds(mWaveformView.maxPos());
        if (maxSeconds / 3600 > 0) {
            return 600;
        } else if (maxSeconds / 1800 > 0) {
            return 300;
        } else if (maxSeconds / 300 > 0) {
            return 60;
        }
        return 5;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAudioReset:
                mWaveformView.setIsDrawBorder(true);
                finishOpeningSoundFile(mSoundFile, 1);
                mWaveformView.setBackgroundColor(getResources().getColor(R.color.colorWaveformBg));
                break;
            case R.id.txtAudioCrop:
                mWaveformView.setIsDrawBorder(true);
                mWaveformView.setBackgroundColor(getResources().getColor(R.color.colorWaveformBg));
                break;
            case R.id.image_back:
                finish();
                break;
            case R.id.image_play:
                onPlayNotCut(0);
                break;
            case R.id.image_save:
                if (mIsPlaying) {
                    handlePause();
                }

                @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
                    public void handleMessage(Message response) {
                        CharSequence newTitle = (CharSequence) response.obj;
                        mNewFileKind = response.arg1;
                        saveRingtone(String.valueOf(newTitle));
                    }
                };
                Message message = Message.obtain(handler);
                FileSaveDialog dlog = new FileSaveDialog(
                        this, getResources(), mTitle, message);
                dlog.show();
                break;
        }
    }

    private void finishOpeningSoundFile(CheapSoundFile mSoundFile, int isReset) {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos)
            mEndPos = mMaxPos;

        if (isReset == 1) {
            mStartPos = mWaveformView.secondsToPixels(0);
            mEndPos = mWaveformView.secondsToPixels(mWaveformView.pixelsToSeconds(mMaxPos));
        }

        if (mWaveformView != null && mWaveformView.isInitialized()) {
            double seconds = mWaveformView.pixelsToSeconds(mMaxPos);
            int min = (int) (seconds / 60);
            float sec = (float) (seconds - 60 * min);
//            txtAudioRecordTimeUpdate.setText(String.format(Locale.US, "%02d:%05.2f", min, sec));
        }

        updateDisplay();
    }

    private void saveRingtone(final CharSequence title) {
        double startTime = mWaveformView.pixelsToSeconds(mStartPos);
        double endTime = mWaveformView.pixelsToSeconds(mEndPos);
        final int startFrame = mWaveformView.secondsToFrames(startTime);
        final int endFrame = mWaveformView.secondsToFrames(endTime);
        final int duration = (int) (endTime - startTime + 0.5);
        // Create an indeterminate progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(R.string.progress_dialog_saving);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        // Save the sound file in a background thread
        mSaveSoundFileThread = new Thread() {
            public void run() {
                // Try AAC first.
                String outPath = makeRingtoneFilename(title, Utility.AUDIO_FORMAT);
                if (outPath == null) {
                    Log.e(" >> ", "Unable to find unique filename");
                    return;
                }
                File outFile = new File(outPath);
                try {
                    // Write the new file
                    mSoundFile.WriteFile(outFile, startFrame,
                            endFrame - startFrame);
                } catch (Exception e) {
                    // log the error and try to create a .wav file instead
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    e.printStackTrace();
                }

                mProgressDialog.dismiss();

                final String finalOutPath = outPath;
                Runnable runnable = new Runnable() {
                    public void run() {
                        afterSavingRingtone(title,
                                finalOutPath,
                                duration, 0);
                    }
                };
                mHandler.post(runnable);
            }
        };
        mSaveSoundFileThread.start();
    }


    /**
     * Generating name for ringtone
     *
     * @param title     title of file
     * @param extension extension for file
     * @return filename
     */

    private String makeRingtoneFilename(CharSequence title, String extension) {
        String subdir = "";
        String externalRootDir = Environment.getExternalStorageDirectory().getPath()
                + File.separator + getString(R.string.app_name);
        if (!externalRootDir.endsWith("/")) {
            externalRootDir += "/";
        }

        switch (mNewFileKind) {
            default:
            case FileSaveDialog.FILE_KIND_MUSIC:
                // TODO(nfaralli): can directly use Environment.getExternalStoragePublicDirectory(
                // Environment.DIRECTORY_MUSIC).getPath() instead
                subdir = "Audio/music/";
                break;
            case FileSaveDialog.FILE_KIND_ALARM:
                subdir = "Audio/alarms/";
                break;
            case FileSaveDialog.FILE_KIND_NOTIFICATION:
                subdir = "Audio/notifications/";
                break;
            case FileSaveDialog.FILE_KIND_RINGTONE:
                subdir = "Audio/ringtones/";
                break;
        }

        String parentDir = externalRootDir + subdir;

        // Create the parent directory
        File parentDirFile = new File(parentDir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentDir = externalRootDir;
        }

        // Turn the title into a filename
        String filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }
        }

        // Try to make the filename unique
        String path = null;
        for (int i = 0; i < 100; i++) {
            String testPath;
            if (i > 0)
                testPath = parentDir + filename + i + extension;
            else
                testPath = parentDir + filename + extension;

            try {
                RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
                f.close();
            } catch (Exception e) {
                // Good, the file didn't exist
                path = testPath;
                break;
            }
        }

        return path;
    }

    /**
     * After saving as ringtone set its content values
     *
     * @param title    title
     * @param outPath  output path
     * @param duration duration of file
     * @param finish   flag for finish
     */
    private void afterSavingRingtone(CharSequence title,
                                     String outPath,
                                     int duration, int finish) {
        File outFile = new File(outPath);
        long fileSize = outFile.length();
        String artist = SongLoader.ARTIST_APP;

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, outPath);
        values.put(MediaStore.MediaColumns.TITLE, title.toString());
        values.put(MediaStore.MediaColumns.SIZE, fileSize);
        values.put(MediaStore.MediaColumns.MIME_TYPE, Utility.AUDIO_MIME_TYPE);

        values.put(MediaStore.Audio.Media.ARTIST, artist);
        values.put(MediaStore.Audio.Media.DURATION, duration);
        values.put(MediaStore.Audio.Media.IS_RINGTONE,
                mNewFileKind == FileSaveDialog.FILE_KIND_RINGTONE);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION,
                mNewFileKind == FileSaveDialog.FILE_KIND_NOTIFICATION);
        values.put(MediaStore.Audio.Media.IS_ALARM,
                mNewFileKind == FileSaveDialog.FILE_KIND_ALARM);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(outPath);
        final Uri newUri = getContentResolver().insert(uri, values);
        Log.e("final URI >> ", newUri + " >> " + outPath);

        if (NetworkUtils.isOnline(MusicAudioCutterActivity.this)) {
            AdsUtils.getIntance().displayInterstitial();
        }

        Toast.makeText(this,
                R.string.save_success_message,
                Toast.LENGTH_SHORT)
                .show();
        //finish();
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
    }
}
