package com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.select;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.trim.TrimmerActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Constants;

import java.io.File;

import iknow.android.utils.DateUtil;
import iknow.android.utils.DeviceUtil;

/**
 * Author：J.Chou
 * Date：  2016.08.01 3:45 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoSelectAdapter extends CursorAdapter {

    private MediaMetadataRetriever mMetadataRetriever;
    private Context mContext;

    public VideoSelectAdapter(Context context, Cursor c) {
        super(context, c);
        this.mContext = context;
        mMetadataRetriever = new MediaMetadataRetriever();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.video_select_gridview_item, null);
        VideoGridViewHolder holder = new VideoGridViewHolder();
        holder.videoItemView = itemView.findViewById(R.id.video_view);
        holder.videoCover = itemView.findViewById(R.id.cover_image);
        holder.durationTv = itemView.findViewById(R.id.video_duration);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final VideoGridViewHolder holder = (VideoGridViewHolder) view.getTag();
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        if (!checkDataValid(cursor)) {
            return;
        }
        final String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        holder.durationTv.setText(DateUtil.convertSecondsToTime(Integer.parseInt(duration) / 1000));
        Glide.with(context)
                .load(getVideoUri(cursor))
                .centerCrop()
                .into(holder.videoCover);
//        holder.videoItemView.setOnClickListener(v
//                -> VideoTrimmerTrimActivity.call((FragmentActivity) mContext, path));

        holder.videoItemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.VIDEO_PATH_KEY, path);
            Intent intent = new Intent(mContext,
                    TrimmerActivity.class);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });
    }

    private boolean checkDataValid(final Cursor cursor) {
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            return false;
        }
        try {
            mMetadataRetriever.setDataSource(path);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        final String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return !TextUtils.isEmpty(duration);
    }

    private Uri getVideoUri(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
    }

    private static class VideoGridViewHolder {
        ImageView videoCover;
        View videoItemView;
        TextView durationTv;
    }
}
