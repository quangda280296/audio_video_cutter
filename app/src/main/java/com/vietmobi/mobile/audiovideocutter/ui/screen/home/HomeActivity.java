package com.vietmobi.mobile.audiovideocutter.ui.screen.home;

import android.os.Build;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.vietmobi.mobile.audiovideocutter.BuildConfig;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.data.remote.api.BaseObserver;
import com.vietmobi.mobile.audiovideocutter.data.remote.api.ConfigApi;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.ConfigResponse;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.InfoResponse;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityMainBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.MainApplication;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.LibraryActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.more.MoreActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.music.SelectMusicActivity;
import com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.select.VideoSelectTrimActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SharedPreferencesUtils;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class HomeActivity extends BaseActivity<ActivityMainBinding>
        implements View.OnClickListener {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        binding.icSoundCut.setOnClickListener(this::onClick);
        binding.icSoudCoupling.setOnClickListener(this::onClick);
        binding.icLibrary.setOnClickListener(this::onClick);
        binding.icVideoCut.setOnClickListener(this::onClick);
        binding.icVideoCouping.setOnClickListener(this::onClick);
        binding.icMore.setOnClickListener(this::onClick);
    }

    @Override
    public void initData() {
        if (SharedPreferencesUtils.getInfoResponse(this) == null) {
            callApiGetInfo();
        } else {
            InfoResponse infoResponse = SharedPreferencesUtils.getInfoResponse(this);
            callApiGetConfig(infoResponse);
        }
    }

    private void callApiGetInfo() {
        String url = "https://ipinfo.io/json";
        ConfigApi.getInfo(url, new BaseObserver<InfoResponse>() {
            @Override
            protected void onResponse(InfoResponse infoResponse) {
                System.out.println("onResponse" + new Gson().toJson(infoResponse));
                SharedPreferencesUtils.saveInfoResponse(HomeActivity.this, infoResponse);
                callApiGetConfig(infoResponse);
                //Post token first install app
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            String token = task.getResult().getToken();
                            if (SharedPreferencesUtils.getVersionName(HomeActivity.this) != null) {
                                if (SharedPreferencesUtils.getVersionName(HomeActivity.this)
                                        != BuildConfig.VERSION_NAME) {
                                    callApiPostRegisterToken(infoResponse, token);
                                }
                            }
                        });
            }

            @Override
            protected void onFailure() {
                System.out.println("onResponse" + "onFailure");
            }

            @Override
            protected void addDisposableManager(Disposable disposable) {

            }
        });
    }

    private void callApiPostRegisterToken(InfoResponse infoResponse, String token) {
        String url = "http://gamemobileglobal.com/api/apps/register_token.php";
        ConfigApi.postRegisterToken(url,
                token,
                BuildConfig.code,
                infoResponse.country,
                BuildConfig.VERSION_NAME,
                MainApplication.deviceId,
                BuildConfig.APPLICATION_ID,
                Build.VERSION.RELEASE,
                Build.MODEL, new BaseObserver<ResponseBody>() {
                    @Override
                    protected void onResponse(ResponseBody responseBody) {

                    }

                    @Override
                    protected void onFailure() {

                    }

                    @Override
                    protected void addDisposableManager(Disposable disposable) {

                    }
                });
    }

    private void callApiGetConfig(InfoResponse infoResponse) {
        String url = "http://gamemobileglobal.com/api/apps/control_apps.php";
        ConfigApi.getConfig(url,
                BuildConfig.code,
                infoResponse.country,
                BuildConfig.VERSION_NAME,
                MainApplication.deviceId,
                MainApplication.getInstallTime(),
                BuildConfig.APPLICATION_ID,
                Build.VERSION.RELEASE,
                Build.MODEL
                , new BaseObserver<ConfigResponse>() {
                    @Override
                    protected void onResponse(ConfigResponse configResponse) {
                        System.out.println("onResponse" + new Gson().toJson(configResponse));
                        SharedPreferencesUtils.saveConfigResponse(HomeActivity.this, configResponse);
                        //TODO init Ads
                        AdsUtils.getIntance().initAds(HomeActivity.this, R.id.banner);
                    }

                    @Override
                    protected void onFailure() {
                    }

                    @Override
                    protected void addDisposableManager(Disposable disposable) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_sound_cut:
                openActivity(SelectMusicActivity.class);
                break;
            case R.id.ic_soud_coupling:
                break;
            case R.id.ic_library:
                openActivity(LibraryActivity.class);
                break;
            case R.id.ic_video_cut:
                openActivity(VideoSelectTrimActivity.class);
                AdsUtils.getIntance().displayInterstitial();
                break;
            case R.id.ic_video_couping:
                break;
            case R.id.ic_more:
                openActivity(MoreActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        AdsUtils.getIntance().stopTimer();
        super.onDestroy();
    }
}
