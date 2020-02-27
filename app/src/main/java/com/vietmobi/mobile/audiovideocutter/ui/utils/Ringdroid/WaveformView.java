/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vietmobi.mobile.audiovideocutter.ui.utils.Ringdroid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Ringdroid.cheapsoundfile.CheapSoundFile;
import com.vietmobi.mobile.audiovideocutter.ui.utils.customAudioViews.SoundFile;


/**
 * WaveformView is an Android view that displays a visual representation
 * of an audio waveform.  It retrieves the frame gains from a CheapSoundFile
 * object and recomputes the shape contour at several zoom levels.
 * <p>
 * This class doesn't handle selection or any of the touch interactions
 * directly, so it exposes a listener interface.  The class that embeds
 * this view should addSong itself as a listener and make the view scroll
 * and respond to other events appropriately.
 * <p>
 * WaveformView doesn't actually handle selection, but it will just display
 * the selected part of the waveform in a different color.
 */
public class WaveformView extends View {

    public interface WaveformListener {
        public void waveformTouchStart(float x);

        public void waveformTouchMove(float x);

        public void waveformTouchEnd();

        public void waveformFling(float x);

        public void waveformDraw();

        public void waveformZoomIn();

        public void waveformZoomOut();
    }

    ;

    // Colors
    protected Paint mBorderLinePaint;
    protected float mDensity;
    protected GestureDetector mGestureDetector;
    protected Paint mGridPaint = new Paint();
    protected float mInitialScaleSpan;
    protected boolean mInitialized;
    protected int[] mLenByZoomLevel;
    protected WaveformListener mListener;
    protected int mNumZoomLevels;
    protected int mOffset;
    protected Paint mPlaybackLinePaint;
    protected int mPlaybackPos;
    protected int mSampleRate;
    protected int mSamplesPerFrame;
    protected ScaleGestureDetector mScaleGestureDetector;
    protected Paint mSelectedLinePaint;
    protected int mSelectionEnd;
    protected int mSelectionStart;
    protected SoundFile mSoundFile;
    protected Paint mTimecodePaint;
    protected Paint mUnselectedBkgndLinePaint;
    protected Paint mUnselectedLinePaint;
    protected float[] mZoomFactorByZoomLevel;
    protected int mZoomLevel;
    protected float minGain;
    protected float range;
    protected float scaleFactor;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We don't want keys, the markers get these
        setFocusable(false);

        Resources res = getResources();

        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(ContextCompat.getColor(getContext(), R.color.grid_line));

        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorHopBush));

        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.transparent_black));

        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(4f);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        mBorderLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.selection_border));

        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setStrokeWidth(8);
        mPlaybackLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.playback_indicator));

        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        mTimecodePaint.setShadowLayer(2, 1, 1, ContextCompat.getColor(getContext(), R.color.timecode_shadow));

        mGestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        mListener.waveformFling(vx);
                        return true;
                    }
                }
        );

        mScaleGestureDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    public boolean onScaleBegin(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleBegin " + d.getCurrentSpanX());
                        mInitialScaleSpan = Math.abs(d.getCurrentSpanX());
                        return true;
                    }

                    public boolean onScale(ScaleGestureDetector d) {
                        float scale = Math.abs(d.getCurrentSpanX());
                        Log.v("Ringdroid", "Scale " + (scale - mInitialScaleSpan));
//                        if (scale - mInitialScaleSpan > 40) {
//                            mListener.waveformZoomIn();
//                            mInitialScaleSpan = scale;
//                        }
//                        if (scale - mInitialScaleSpan < -40) {
//                            mListener.waveformZoomOut();
//                            mInitialScaleSpan = scale;
//                        }
                        return true;
                    }

                    public void onScaleEnd(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleEnd " + d.getCurrentSpanX());
                    }
                }
        );

        this.mSoundFile = null;
        this.mLenByZoomLevel = null;
        this.mOffset = 0;
        this.mPlaybackPos = -1;
        this.mSelectionStart = 0;
        this.mSelectionEnd = 0;
        this.mDensity = 1.0f;
        this.mInitialized = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mListener.waveformTouchStart(event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                mListener.waveformTouchMove(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                mListener.waveformTouchEnd();
                break;
        }
        return true;
    }

    public boolean hasSoundFile() {
        return mSoundFile != null;
    }

    public void setSoundFile(SoundFile soundFile) {
        this.mSoundFile = soundFile;
        this.mSampleRate = this.mSoundFile.getSampleRate();
        this.mSamplesPerFrame = this.mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public int getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(int i) {
        this.mZoomLevel = i;
    }

    public boolean canZoomIn() {
        return this.mZoomLevel < this.mNumZoomLevels - 1;
    }

    public void zoomIn() {
        if (canZoomIn()) {
            this.mZoomLevel++;
            float f = ((float) this.mLenByZoomLevel[this.mZoomLevel]) / ((float) this.mLenByZoomLevel[this.mZoomLevel - 1]);
            this.mSelectionStart = (int) (((float) this.mSelectionStart) * f);
            this.mSelectionEnd = (int) (((float) this.mSelectionEnd) * f);
            this.mOffset = ((int) (((float) (this.mOffset + ((int) (((float) getMeasuredWidth()) / f)))) * f)) - ((int) (((float) getMeasuredWidth()) / f));
            if (this.mOffset < 0) {
                this.mOffset = 0;
            }
            invalidate();
        }
    }

    public boolean canZoomOut() {
        return this.mZoomLevel > 0;
    }

    public void zoomOut() {
        if (canZoomOut()) {
            this.mZoomLevel--;
            float f = ((float) this.mLenByZoomLevel[this.mZoomLevel + 1]) / ((float) this.mLenByZoomLevel[this.mZoomLevel]);
            this.mSelectionStart = (int) (((float) this.mSelectionStart) / f);
            this.mSelectionEnd = (int) (((float) this.mSelectionEnd) / f);
            this.mOffset = ((int) (((float) ((int) (((float) this.mOffset) + (((float) getMeasuredWidth()) / f)))) / f)) - ((int) (((float) getMeasuredWidth()) / f));
            if (this.mOffset < 0) {
                this.mOffset = 0;
            }
            invalidate();
        }
    }

    public int maxPos() {
        return this.mLenByZoomLevel[this.mZoomLevel];
    }

    public int secondsToFrames(double d) {
        return (int) ((((1.0d * d) * ((double) this.mSampleRate)) / ((double) this.mSamplesPerFrame)) + 0.5d);
    }

    public int secondsToPixels(double d) {
        return (int) ((((((double) this.mZoomFactorByZoomLevel[this.mZoomLevel]) * d) * ((double) this.mSampleRate)) / ((double) this.mSamplesPerFrame)) + 0.5d);
    }

    public double pixelsToSeconds(int i) {
        return (((double) i) * ((double) this.mSamplesPerFrame)) / (((double) this.mSampleRate) * ((double) this.mZoomFactorByZoomLevel[this.mZoomLevel]));
    }

    public int millisecsToPixels(int i) {
        return (int) (((((((double) i) * 1.0d) * ((double) this.mSampleRate)) * ((double) this.mZoomFactorByZoomLevel[this.mZoomLevel])) / (1000.0d * ((double) this.mSamplesPerFrame))) + 0.5d);
    }

    public int pixelsToMillisecs(int i) {
        return (int) (((((double) i) * (1000.0d * ((double) this.mSamplesPerFrame))) / (((double) this.mSampleRate) * ((double) this.mZoomFactorByZoomLevel[this.mZoomLevel]))) + 0.5d);
    }

    public void setParameters(int i, int i2, int i3) {
        this.mSelectionStart = i;
        this.mSelectionEnd = i2;
        this.mOffset = i3;
    }

    public int getStart() {
        return this.mSelectionStart;
    }

    public int getEnd() {
        return this.mSelectionEnd;
    }

    public int getOffset() {
        return this.mOffset;
    }

    public void setPlayback(int i) {
        this.mPlaybackPos = i;
    }

    public void setListener(WaveformListener waveformListener) {
        this.mListener = waveformListener;
    }

    public void recomputeHeights(float f) {
        this.mDensity = f;
        this.mTimecodePaint.setTextSize((float) ((int) (12.0f * f)));
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void drawWaveformLine(Canvas canvas, int i, int i2, int i3, Paint paint) {
        float f = (float) i;
        canvas.drawLine(f, (float) i2, f, (float) i3, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (this.mSoundFile != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int i2 = this.mOffset;
            int i3 = this.mLenByZoomLevel[this.mZoomLevel] - i2;
            int i4 = measuredHeight / 2;
            int i5 = i3 > measuredWidth ? measuredWidth : i3;
            double pixelsToSeconds = pixelsToSeconds(1);
            int i6 = 0;
            boolean z = pixelsToSeconds > 0.02d;
            double d = ((double) this.mOffset) * pixelsToSeconds;
            int i7 = (int) d;
            double d2 = 1.0d;
            int i8 = 1;
            while (d2 / pixelsToSeconds < 50.0d) {
                d2 = 5.0d * ((double) i8);
                i8++;
                i2 = i2;
                i4 = i4;
            }
            int i9 = i2;
            int i10 = i4;
            int i11 = (int) (d / d2);
            int i12 = 0;
            while (i12 < i5) {
                double d3 = d + pixelsToSeconds;
                int i13 = (int) d3;
                if (i13 != i7) {
                    if (!z || i13 % 5 == 0) {
                        float f = (float) (i12 + 1);
                       // canvas.drawLine(f, 0.0f, f, (float) measuredHeight, this.mGridPaint);
                    }
                    i = i13;
                } else {
                    i = i7;
                }
                int i14 = i9;
                int i15 = i14;
                double d4 = d3;
                drawWaveform(canvas, i12, i14, measuredHeight, i10, selectWaveformPaint(i12, i14, d3));
                i12++;
                i7 = i;
                i9 = i15;
                d = d4;
            }
            for (int i16 = i5; i16 < measuredWidth; i16++) {
                drawWaveformLine(canvas, i16, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
            }
            float f2 = (float) measuredHeight;
            canvas.drawLine(((float) (this.mSelectionStart - this.mOffset)) + 0.5f,
                    0.0f, ((float) (this.mSelectionStart - this.mOffset)) + 0.5f, f2, this.mBorderLinePaint);
            canvas.drawLine(((float) (this.mSelectionEnd - this.mOffset)) + 0.5f,
                    0.0f, ((float) (this.mSelectionEnd - this.mOffset)) + 0.5f, f2, this.mBorderLinePaint);
            double d5 = ((double) this.mOffset) * pixelsToSeconds;
//            while (i6 < i5) {
//                i6++;
//                d5 += pixelsToSeconds;
//                int i17 = (int) d5;
//                int i18 = (int) (d5 / d2);
//                if (i18 != i11) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("");
//                    sb.append(i17 / 60);
//                    String sb2 = sb.toString();
//                    StringBuilder sb3 = new StringBuilder();
//                    sb3.append("");
//                    int i19 = i17 % 60;
//                    sb3.append(i19);
//                    String sb4 = sb3.toString();
//                    if (i19 < 10) {
//                        StringBuilder sb5 = new StringBuilder();
//                        sb5.append("0");
//                        sb5.append(sb4);
//                        sb4 = sb5.toString();
//                    }
//                    StringBuilder sb6 = new StringBuilder();
//                    sb6.append(sb2);
//                    sb6.append(":");
//                    sb6.append(sb4);
//                    String sb7 = sb6.toString();
//                    //canvas.drawText(sb7, ((float) i6) - ((float) (0.5d * ((double) this.mTimecodePaint.measureText(sb7)))), (float) ((int) (12.0f * this.mDensity)), this.mTimecodePaint);
//                    i11 = i18;
//                } else {
//                    Canvas canvas2 = canvas;
//                }
//            }
            if (this.mListener != null) {
                this.mListener.waveformDraw();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void drawWaveform(Canvas canvas, int i, int i2, int i3, int i4, Paint paint) {
        int i5 = i2 + i;
        if (i5 < this.mSelectionStart || i5 >= this.mSelectionEnd) {
            drawWaveformLine(canvas, i, 0, i3, this.mUnselectedBkgndLinePaint);
        }
        int scaledHeight = (int) ((getScaledHeight(this.mZoomFactorByZoomLevel[this.mZoomLevel], i5) * ((float) getMeasuredHeight())) / 2.0f);
        drawWaveformLine(canvas, i, i4 - scaledHeight, i4 + 1 + scaledHeight, paint);
        if (i5 == this.mPlaybackPos) {
            float f = (float) i;
            canvas.drawLine(f, 0.0f, f, (float) i3, this.mPlaybackLinePaint);
        }
    }

    /* access modifiers changed from: protected */
    public Paint selectWaveformPaint(int i, int i2, double d) {
        Paint paint;
        int i3 = i + i2;
        if (i3 < this.mSelectionStart || i3 >= this.mSelectionEnd) {
            paint = this.mUnselectedLinePaint;
        } else {
            paint = this.mSelectedLinePaint;
        }
//        if (this.segmentsMap != null && !this.segmentsMap.isEmpty()) {
//            if (this.nextSegment == null && ((Double) this.segmentsMap.ceilingKey(Double.valueOf(d))) != null) {
//                this.nextSegment = (Segment) this.segmentsMap.get(this.segmentsMap.ceilingKey(Double.valueOf(d)));
//            }
//            if (this.nextSegment != null) {
//                if (this.nextSegment.getStart().compareTo(Double.valueOf(d)) <= 0 && this.nextSegment.getStop().compareTo(Double.valueOf(d)) >= 0) {
//                    Paint paint2 = new Paint();
//                    paint2.setAntiAlias(false);
//                    paint2.setColor(this.nextSegment.getColor());
//                    return paint2;
//                } else if (((Double) this.segmentsMap.ceilingKey(Double.valueOf(d))) != null) {
//                    this.nextSegment = (Segment) this.segmentsMap.get(this.segmentsMap.ceilingKey(Double.valueOf(d)));
//                }
//            }
//        }
        return paint;
    }

    /* access modifiers changed from: protected */
    public float getGain(int i, int i2, int[] iArr) {
        int i3 = i2 - 1;
        int min = Math.min(i, i3);
        if (i2 < 2) {
            return (float) iArr[min];
        }
        if (min == 0) {
            return (((float) iArr[0]) / 2.0f) + (((float) iArr[1]) / 2.0f);
        }
        if (min == i3) {
            return (((float) iArr[i2 - 2]) / 2.0f) + (((float) iArr[i3]) / 2.0f);
        }
        return (((float) iArr[min - 1]) / 3.0f) + (((float) iArr[min]) / 3.0f) + (((float) iArr[min + 1]) / 3.0f);
    }

    /* access modifiers changed from: protected */
    public float getHeight(int i, int i2, int[] iArr, float f, float f2, float f3) {
        float gain = ((getGain(i, i2, iArr) * f) - f2) / f3;
        if (((double) gain) < 0.0d) {
            gain = 0.0f;
        }
        if (((double) gain) > 1.0d) {
            return 1.0f;
        }
        return gain;
    }

    /* access modifiers changed from: protected */
    public void computeDoublesForAllZoomLevels() {
        int numFrames = this.mSoundFile.getNumFrames();
        float f = 1.0f;
        for (int i = 0; i < numFrames; i++) {
            float gain = getGain(i, numFrames, this.mSoundFile.getFrameGains());
            if (gain > f) {
                f = gain;
            }
        }
        this.scaleFactor = 1.0f;
        if (((double) f) > 255.0d) {
            this.scaleFactor = 255.0f / f;
        }
        int[] iArr = new int[256];
        float f2 = 0.0f;
        for (int i2 = 0; i2 < numFrames; i2++) {
            int gain2 = (int) (getGain(i2, numFrames, this.mSoundFile.getFrameGains()) * this.scaleFactor);
            if (gain2 < 0) {
                gain2 = 0;
            }
            if (gain2 > 255) {
                gain2 = 255;
            }
            float f3 = (float) gain2;
            if (f3 > f2) {
                f2 = f3;
            }
            iArr[gain2] = iArr[gain2] + 1;
        }
        this.minGain = 0.0f;
        int i3 = 0;
        while (this.minGain < 255.0f && i3 < numFrames / 20) {
            i3 += iArr[(int) this.minGain];
            this.minGain += 1.0f;
        }
        int i4 = 0;
        while (f2 > 2.0f && i4 < numFrames / 100) {
            i4 += iArr[(int) f2];
            f2 -= 1.0f;
        }
        this.range = f2 - this.minGain;
        this.mNumZoomLevels = 4;
        this.mLenByZoomLevel = new int[4];
        this.mZoomFactorByZoomLevel = new float[4];
        float f4 = (float) numFrames;
        float measuredWidth = ((float) getMeasuredWidth()) / f4;
        if (measuredWidth < 1.0f) {
            this.mLenByZoomLevel[0] = Math.round(f4 * measuredWidth);
            this.mZoomFactorByZoomLevel[0] = measuredWidth;
            this.mLenByZoomLevel[1] = numFrames;
            this.mZoomFactorByZoomLevel[1] = 1.0f;
            this.mLenByZoomLevel[2] = numFrames * 2;
            this.mZoomFactorByZoomLevel[2] = 2.0f;
            this.mLenByZoomLevel[3] = numFrames * 3;
            this.mZoomFactorByZoomLevel[3] = 3.0f;
            this.mZoomLevel = 0;
        } else {
            this.mLenByZoomLevel[0] = numFrames;
            this.mZoomFactorByZoomLevel[0] = 1.0f;
            this.mLenByZoomLevel[1] = numFrames * 2;
            this.mZoomFactorByZoomLevel[1] = 2.0f;
            this.mLenByZoomLevel[2] = numFrames * 3;
            this.mZoomFactorByZoomLevel[2] = 3.0f;
            this.mLenByZoomLevel[3] = numFrames * 4;
            this.mZoomFactorByZoomLevel[3] = 4.0f;
            this.mZoomLevel = 0;
            for (int i5 = 0; i5 < 4 && this.mLenByZoomLevel[this.mZoomLevel] - getMeasuredWidth() <= 0; i5++) {
                this.mZoomLevel = i5;
            }
        }
        this.mInitialized = true;
    }

    /* access modifiers changed from: protected */
    public float getZoomedInHeight(float f, int i) {
        int i2 = (int) f;
        if (i == 0) {
            return 0.5f * getHeight(0, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range);
        } else if (i == 1) {
            return getHeight(0, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range);
        } else if (i % i2 == 0) {
            int i3 = i / i2;
            return 0.5f * (getHeight(i3 - 1, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range) + getHeight(i3, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range));
        } else {
            int i4 = i - 1;
            if (i4 % i2 != 0) {
                return 0.0f;
            }
            return getHeight(i4 / i2, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range);
        }
    }

    /* access modifiers changed from: protected */
    public float getZoomedOutHeight(float f, int i) {
        int i2 = (int) (((float) i) / f);
        return 0.5f * (getHeight(i2, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range) + getHeight(i2 + 1, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range));
    }

    /* access modifiers changed from: protected */
    public float getNormalHeight(int i) {
        return getHeight(i, this.mSoundFile.getNumFrames(), this.mSoundFile.getFrameGains(), this.scaleFactor, this.minGain, this.range);
    }

    /* access modifiers changed from: protected */
    public float getScaledHeight(float f, int i) {
        double d = (double) f;
        if (d == 1.0d) {
            return getNormalHeight(i);
        }
        if (d < 1.0d) {
            return getZoomedOutHeight(f, i);
        }
        return getZoomedInHeight(f, i);
    }
}
