package com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseFragment;
import com.vietmobi.mobile.audiovideocutter.data.model.Video;
import com.vietmobi.mobile.audiovideocutter.databinding.FragmentListdataBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.music.SelectMusicActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.select.VideoCursorLoader;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.select.VideoLoadManager;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.FileUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;

import java.util.ArrayList;

public class VideoFragment extends BaseFragment<FragmentListdataBinding> {

    private VideoLoadManager mVideoLoadManager;
    private VideoAdapter adapter;

    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_listdata;
    }

    @Override
    public void initView() {
        adapter = new VideoAdapter(getBaseActivity());
        binding.recyclerListData.setLayoutManager(new GridLayoutManager(getBaseActivity(), 3));
        binding.recyclerListData.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener((video, position) -> {
            if (NetworkUtils.isOnline(getBaseActivity())) {
                AdsUtils.getIntance().displayInterstitial();
            }
            Uri uri = Uri.parse(video.path);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> getFileTask(), 0L);
    }

    @SuppressLint({"CheckResult", "StaticFieldLeak"})
    private void getFileTask() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mVideoLoadManager = new VideoLoadManager();
        mVideoLoadManager.setLoader(new VideoCursorLoader());
        RxPermissions rxPermissions = new RxPermissions(getBaseActivity());
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        new AsyncTask<String, Void, ArrayList<Video>>() {
                            @Override
                            protected ArrayList<Video> doInBackground(String... voids) {
                                return Video.getListVideo(FileUtils
                                        .getListFileVideo(getBaseActivity()));
                            }

                            @Override
                            protected void onPostExecute(ArrayList<Video> listVideo) {
                                super.onPostExecute(listVideo);
                                if (listVideo != null) {
                                    if (adapter != null) {
                                        adapter.setDataList(listVideo);
                                    }
                                }
                                binding.progressBar.setVisibility(View.GONE);

                            }
                        }.execute();
                    }
                });
    }
}