package com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.trim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityVideoTrimBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.common.ui.BaseTrimActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.interfaces.VideoTrimListener;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.utils.ToastUtil;


public class VideoTrimmerTrimActivity extends BaseTrimActivity implements VideoTrimListener {

    private static final String TAG = "jason";
    private static final String VIDEO_PATH_KEY = "video-file-path";
    private static final String COMPRESSED_VIDEO_FILE_NAME = "compress.mp4";
    public static final int VIDEO_TRIM_REQUEST_CODE = 0x001;
    private ActivityVideoTrimBinding mBinding;
    private ProgressDialog mProgressDialog;
    public static final int Progress_Dialog_Progress = 0;

    public static void call(FragmentActivity from, String videoPath) {
        if (!TextUtils.isEmpty(videoPath)) {
            Bundle bundle = new Bundle();
            bundle.putString(VIDEO_PATH_KEY, videoPath);
            Intent intent = new Intent(from, VideoTrimmerTrimActivity.class);
            intent.putExtras(bundle);
            from.startActivityForResult(intent, VIDEO_TRIM_REQUEST_CODE);
        }
    }

    @Override
    public void initUI() {
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_video_trim);
        Bundle bd = getIntent().getExtras();
        String path = "";
        if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
        if (mBinding.trimmerView != null) {
            mBinding.trimmerView.setOnTrimVideoListener(this);
            mBinding.trimmerView.initVideoByURI(Uri.parse(path));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.trimmerView.onVideoPause();
        mBinding.trimmerView.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.trimmerView.onDestroy();
    }

    @Override
    public void onStartTrim() {
        System.out.println("onStartTrim");
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void showProgress(int showProgress) {

        mProgressDialog.setProgress(showProgress);
    }

    @Override
    public void onFinishTrim(String in) {
        System.out.println("onStartTrim");
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        ToastUtil.longShow(this, getString(R.string.trimmed_done));
        finish();
        //TODO: please handle your trimmed video url here!!!
        //String out = StorageUtil.getCacheDir() + File.separator + COMPRESSED_VIDEO_FILE_NAME;
        //buildDialog(getResources().getString(R.string.compressing)).show();
        //VideoCompressor.compress(this, in, out, new VideoCompressListener() {
        //  @Override public void onSuccess(String message) {
        //  }
        //
        //  @Override public void onFailure(String message) {
        //  }
        //
        //  @Override public void onFinish() {
        //    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        //    finish();
        //  }
        //});
    }

    @Override
    public void onCancel() {
        mBinding.trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(0);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}
