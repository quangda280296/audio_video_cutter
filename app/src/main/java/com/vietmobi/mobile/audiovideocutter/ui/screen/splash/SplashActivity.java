package com.vietmobi.mobile.audiovideocutter.ui.screen.splash;

import android.os.Handler;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivitySplashBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.home.HomeActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    private Handler handler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        handler = new Handler();
        handler.postDelayed(runnable, 1500L);
    }

    private Runnable runnable = () -> {
        openActivity(HomeActivity.class);
        finish();
    };

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        if (handler != null){
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }
}
