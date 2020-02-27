package com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.record;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.common.ui.BaseTrimActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.record.view.PreviewSurfaceView;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2019/02/22 4:24 PM
 * version: 1.0
 * description:
 */
public class VideoRecordTrimActivity extends BaseTrimActivity implements View.OnClickListener {
    private PreviewSurfaceView mGLView;
    private ImageView mIvRecordBtn, mIvSwitchCameraBtn;

    public static void call(Context context) {
        context.startActivity(new Intent(context, VideoRecordTrimActivity.class));
    }

    @Override
    public void initUI() {
        setContentView(R.layout.activity_video_recording);
        mGLView = this.findViewById(R.id.glView);
        mIvRecordBtn = this.findViewById(R.id.ivRecord);
        mIvSwitchCameraBtn = this.findViewById(R.id.ivSwitch);
        mIvRecordBtn.setOnClickListener(this);
        mIvSwitchCameraBtn.setOnClickListener(this);
        mGLView.startPreview();
    }

    @Override
    public void onClick(View view) {
        if (R.id.ivRecord == view.getId()) {
            mGLView.startPreview();
        }
    }
}
