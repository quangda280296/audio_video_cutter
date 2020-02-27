package com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.select;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityVideoSelectBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.common.ui.BaseTrimActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;

import iknow.android.utils.callback.SimpleCallback;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class VideoSelectTrimActivity extends BaseTrimActivity implements View.OnClickListener {

    private ActivityVideoSelectBinding mBinding;
    private VideoSelectAdapter mVideoSelectAdapter;
    private VideoLoadManager mVideoLoadManager;

    @SuppressLint("CheckResult")
    @Override
    public void initUI() {
        mVideoLoadManager = new VideoLoadManager();
        mVideoLoadManager.setLoader(new VideoCursorLoader());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_select);
        mBinding.imageBack.setOnClickListener(this);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) { // Always true pre-M
                mVideoLoadManager.load(this, new SimpleCallback() {
                    @Override
                    public void success(Object obj) {
                        if (mVideoSelectAdapter == null) {
                            mVideoSelectAdapter = new VideoSelectAdapter(
                                    VideoSelectTrimActivity.this, (Cursor) obj);
                        } else {
                            mVideoSelectAdapter.swapCursor((Cursor) obj);
                        }
                        if (mBinding.videoGridview.getAdapter() == null) {
                            mBinding.videoGridview.setAdapter(mVideoSelectAdapter);
                        }
                        mVideoSelectAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.isOnline(this)){
            AdsUtils.getIntance().initAds(this, R.id.banner);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }
}
