package com.vietmobi.mobile.audiovideocutter.ui.screen;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.vietmobi.mobile.audiovideocutter.BuildConfig;

import java.io.File;

import iknow.android.utils.BaseUtils;
import io.fabric.sdk.android.Fabric;

/**
 * Author：J.Chou
 * Date：  2016.09.27 10:44 AM
 * Email： who_know_me@163.com
 * Describe:
 */
public class MainApplication extends Application {

    public static String deviceId = "";
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BaseUtils.init(this);
        context = this;
        //initInfoDevice(BuildConfig.code, BuildConfig.VERSION_NAME);
        Fabric.with(this, new Crashlytics());
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getInstallTime() {
        ApplicationInfo appInfor = null;
        String timeInstall = "";
        try {
            appInfor = context.getPackageManager()
                    .getApplicationInfo(BuildConfig.APPLICATION_ID, 0);
            if (appInfor != null) {
                timeInstall = String.valueOf(new File(appInfor.sourceDir).lastModified() / 1000);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return timeInstall;
    }
}
