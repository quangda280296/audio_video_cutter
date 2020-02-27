package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.adapter.BaseRecyclerAdapter;
import com.vietmobi.mobile.audiovideocutter.data.model.Video;
import com.vietmobi.mobile.audiovideocutter.databinding.VideoSelectGridviewItemBinding;

import java.io.File;

import iknow.android.utils.DeviceUtil;

public class VideoAdapter extends BaseRecyclerAdapter<Video, VideoSelectGridviewItemBinding> {

    private int videoCoverSize = DeviceUtil.getDeviceWidth() / 3;

    public VideoAdapter(Context context) {
        super(context);
    }

    @Override
    protected int layoutItemId() {
        return R.layout.video_select_gridview_item;
    }

    @Override
    protected void bindData(VideoSelectGridviewItemBinding binding, Video item, int position) {
        if (item.thumb != null) {
//            Glide.with(context)
//                    .load(item.thumb)
//                    .asBitmap()
//                    .override(videoCoverSize, videoCoverSize)
//                    .into(binding.coverImage);
        }

        Glide.with(context)
                    .load(Uri.fromFile(new File(item.path)))
                    .override(videoCoverSize, videoCoverSize)
                    .into(binding.coverImage);
        binding.videoDuration.setText(item.duration);
    }
}
