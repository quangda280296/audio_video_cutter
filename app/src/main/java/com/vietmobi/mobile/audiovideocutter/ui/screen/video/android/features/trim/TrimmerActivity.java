package com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.trim;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.Toast;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityTrimmerBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.LibraryActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.music.MusicAudioCutterActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Constants;
import com.vietmobi.mobile.audiovideocutter.ui.utils.EmptyUtil;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;

import java.util.List;
import java.util.Vector;

import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;
import life.knowledge4.videotrimmer.view.Thumb;

public class TrimmerActivity extends BaseActivity<ActivityTrimmerBinding>
        implements OnTrimVideoListener, OnK4LVideoListener {

    private ProgressDialog mProgressDialog;
    private String path = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_trimmer;
    }

    @Override
    public void initView() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming));
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            path = getIntent().getExtras().getString(Constants.VIDEO_PATH_KEY);
            if (EmptyUtil.isNotEmpty(path)) {
                initVideoTrimmer();
            }

            if (NetworkUtils.isOnline(TrimmerActivity.this)) {
                AdsUtils.getIntance().displayInterstitial();
            }
        }
    }

    private void initVideoTrimmer() {
        binding.timeLine.setMaxDuration(10);
        binding.timeLine.initThumbs(getListThumb());
        binding.timeLine.setOnTrimVideoListener(this);
        binding.timeLine.setOnK4LVideoListener(this);
        //binding.timeLine.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
        binding.timeLine.setVideoURI(Uri.parse(path));
        binding.timeLine.setVideoInformationVisibility(true);
    }

    public List<Thumb> getListThumb() {
        List<Thumb> thumbs = new Vector<>();

        for (int i = 0; i < 2; i++) {
            Thumb th = new Thumb();
            th.setIndex(i);
            if (i == 0) {
                th.setBitmap(getBitmapFromVectorDrawable(this, R.drawable.end_dragger));
            } else {
                th.setBitmap(getBitmapFromVectorDrawable(this, R.drawable.end_dragger));
            }

            thumbs.add(th);
        }

        return thumbs;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onTrimStarted() {
        if (this.isFinishing()) {
            return;
        } else {
            mProgressDialog.show();
        }
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();

        runOnUiThread(() -> Toast.makeText(TrimmerActivity.this,
                getString(R.string.save_success_message, uri.getPath()),
                Toast.LENGTH_SHORT).show());
        /*Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
        finish();*/
        if (NetworkUtils.isOnline(TrimmerActivity.this)) {
            AdsUtils.getIntance().displayInterstitial();
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.TYPE_VIDEO, Constants.TYPE_VIDEO);
        openActivity(LibraryActivity.class, bundle);
        finish();
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        binding.timeLine.destroy();
        finish();
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(() ->
                Toast.makeText(TrimmerActivity.this, message,
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(() ->
                Toast.makeText(TrimmerActivity.this, "onVideoPrepared",
                        Toast.LENGTH_SHORT).show());
    }
}
