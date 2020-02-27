package com.vietmobi.mobile.audiovideocutter.ui.screen.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.data.model.Song;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityMusicCutBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.home.HomeActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.music.playcut.RingdroidEditActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SongLoader;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SelectMusicActivity extends BaseActivity<ActivityMusicCutBinding>
        implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_REQUEST_CODE = 1111;
    private static final int REQUEST_ID_PERMISSIONS = 1222;
    private SongAdapter songAdapter;
    private ArrayList<Song> musicArrayList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_cut;
    }

    @Override
    public void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setTitle("");
        binding.searchView.setVoiceSearch(false);
        binding.imageBack.setOnClickListener(this);
        binding.searchView.setEllipsize(true);
        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ("".equals(newText.toLowerCase())) {
                    loadData();
                    return false;
                } else {
                    if (songAdapter != null) {
                        songAdapter.clearData();
                        songAdapter.setDataList(SelectMusicActivity.this, SongLoader
                                .searchSongs(SelectMusicActivity.this, newText, 10));
                    }
                }
                return false;
            }
        });
        binding.searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                binding.titleToolbar.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                binding.titleToolbar.setVisibility(View.VISIBLE);
            }
        });
        if (NetworkUtils.isOnline(this)) {
            AdsUtils.getIntance().initAds(this, R.id.banner);
        }
    }


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(SelectMusicActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_ID_PERMISSIONS);
    }

    private boolean checkStoragePermission() {
        return (ActivityCompat.checkSelfPermission(SelectMusicActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void initData() {
        setUpMusicAdapter();
        songAdapter.setOnRecyclerViewItemClickListener((song, position) -> {
            if (checkStoragePermission()) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Song.class.getName(), song);
                openActivity(MusicAudioCutterActivity.class, bundle);
            }else {
                requestStoragePermission();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            loadData();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            loadData();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void setUpMusicAdapter() {
        musicArrayList = new ArrayList<>();
        songAdapter = new SongAdapter();
        binding.recyclerViewMusic.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerViewMusic.setAdapter(songAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadData();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                EasyPermissions.requestPermissions(this,
                        getString(R.string.text_message_permission_external),
                        PERMISSION_REQUEST_CODE,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData() {
        new AsyncTask<Void, Void, List<Song>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<Song> doInBackground(final Void... unused) {
                musicArrayList.clear();
                musicArrayList = SongLoader.getAllSongs(SelectMusicActivity.this);
                return musicArrayList;
            }

            @Override
            protected void onPostExecute(List<Song> songList) {
                binding.progressBar.setVisibility(View.GONE);
                songAdapter.setDataList(SelectMusicActivity.this, songList);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchViewItem.expandActionView();
        binding.searchView.setMenuItem(searchViewItem);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.searchView.isSearchOpen()) {
            binding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}
