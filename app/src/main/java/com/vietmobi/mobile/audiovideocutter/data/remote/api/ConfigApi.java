package com.vietmobi.mobile.audiovideocutter.data.remote.api;

import com.vietmobi.mobile.audiovideocutter.data.remote.response.ConfigResponse;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.InfoResponse;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ConfigApi {

    private static <M> void addObservable(Observable<M> observable, Observer<M> subscription) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscription);
    }

    private static ConfigService getConfigService() {
        return ConfigClient.getInstance().getApiService();
    }

    public static void getInfo(String url, Observer<InfoResponse> subscriber) {
        addObservable(getConfigService().getInfo(url), subscriber);
    }

    public static void getConfig(String url,
                                 String code,
                                 String country,
                                 String version,
                                 String deviceID,
                                 String timereg,
                                 String mPackage,
                                 String os_version,
                                 String phone_name,
                                 Observer<ConfigResponse> subscriber) {
        addObservable(getConfigService().getConfig(
                url,
                code,
                country,
                version,
                deviceID,
                timereg,
                mPackage,
                os_version,
                phone_name), subscriber);
    }

    public static void postRegisterToken(String url,
                                         String token,
                                         String code,
                                         String country,
                                         String version,
                                         String deviceID,
                                         String mPackage,
                                         String os_version,
                                         String phone_name, Observer<ResponseBody> subscriber) {
        addObservable(getConfigService().postRegisterToken(
                url,
                token,
                code,
                version,
                deviceID,
                mPackage,
                os_version,
                phone_name,
                country), subscriber);
    }

    public static void postStasticticalToken(String url,
                                             String pushId,
                                             String code,
                                             String version,
                                             String deviceID,
                                             String token_id,
                                             String packageApp,
                                             String os_version,
                                             String phone_name,
                                             String country, Observer<ResponseBody> subscriber) {
        addObservable(getConfigService().postStasticticalToken(
                url,
                pushId,
                code,
                version,
                deviceID,
                token_id,
                packageApp,
                os_version,
                phone_name,
                country), subscriber);
    }
}
