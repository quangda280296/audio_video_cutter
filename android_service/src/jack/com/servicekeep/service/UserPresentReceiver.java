package jack.com.servicekeep.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import jack.com.servicekeep.model.AppInfoResponse;
import jack.com.servicekeep.model.Config;
import jack.com.servicekeep.model.InfoDevice;
import jack.com.servicekeep.network.BaseObserver;
import jack.com.servicekeep.network.VMobileApi;
import jack.com.servicekeep.service.androidO.AdsServiceO;
import jack.com.servicekeep.utils.LogUtils;
import jack.com.servicekeep.utils.ServiceUtils;

import static android.app.job.JobScheduler.RESULT_SUCCESS;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class UserPresentReceiver extends BroadcastReceiver {

    private InfoDevice infoDevice;

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("UserPresentReceiver: Start app jacky");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    InfoDevice user1 = realm.where(InfoDevice.class).findFirst();
                    if (user1 != null) {
                        infoDevice = realm.copyFromRealm(user1);
                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }

                if (ServiceUtils.isScreenOn(context)) {
                    if (infoDevice != null) {
                        final long timeDelay = getRandomNumberInRange(5, 500) * 5;
                        infoDevice.timeDelay = timeDelay;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                VMobileApi.getInfoControl(infoDevice, new BaseObserver<AppInfoResponse>() {
                                    @Override
                                    protected void onResponse(final AppInfoResponse appInfoResponse) {
                                            Realm realm = Realm.getDefaultInstance();
                                            try {
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        InfoDevice info = realm.where(InfoDevice.class).equalTo(InfoDevice.PROPERTY_DIVICE_ID, infoDevice.deviceID).findFirst();
                                                        if (info != null) {
                                                            info.deleteFromRealm();
                                                        }
                                                        try {
                                                            infoDevice.isApp = false;
                                                            infoDevice.ads = new RealmList<>();
                                                            infoDevice.ads.addAll(appInfoResponse.ads);
                                                            infoDevice.config = realm.createObject(Config.class);
                                                            infoDevice.config.runServer = appInfoResponse.config.runServer;
                                                            infoDevice.config.offsetTimeShowAds = appInfoResponse.config.offsetTimeShowAds;
                                                            infoDevice.config.timeStartShowAds = appInfoResponse.config.timeStartShowAds;
                                                            infoDevice.config.timeUpdateLoadFail = appInfoResponse.config.timeUpdateLoadFail;

                                                            realm.copyToRealmOrUpdate(infoDevice);

                                                            if (infoDevice != null && infoDevice.config != null && infoDevice.config.runServer == 1) {
                                                                    startService(context);
                                                            } else {
                                                                AdsService.stopService(context);
                                                                WorkService.stopService(context);
                                                            }
                                                        } catch (RealmPrimaryKeyConstraintException e) {

                                                        }

                                                    }
                                                });
                                            } finally {
                                                if (realm != null) {
                                                    realm.close();
                                                }
                                            }
                                    }

                                    @Override
                                    protected void onFailure() {
                                        try {
                                            if (infoDevice != null && infoDevice.config != null && infoDevice.config.runServer == 1) {
                                                startService(context);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    protected void addDisposableManager(Disposable disposable) {

                                    }
                                });
                            }
                        }, timeDelay);

                    }
                } else {
                    try {
                        if (infoDevice != null && infoDevice.config != null && infoDevice.config.runServer == 1 && context != null) {
                            AdsService.stopService(context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void startService(final Context context) {
        if (context == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            AdsService.startService(context);
        } else {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

            ComponentName jobService = new ComponentName(context.getPackageName(),
                    AdsServiceO.class.getName());
            JobInfo jobInfo = new JobInfo.Builder(121, jobService)
                    .setMinimumLatency(1000)
                    .setPersisted(true)
                    .setBackoffCriteria(TimeUnit.MILLISECONDS.toMillis(200), JobInfo.BACKOFF_POLICY_LINEAR) //线性重试方案
                    .build();
            int result = jobScheduler.schedule(jobInfo);
            if (result == RESULT_SUCCESS) {
                LogUtils.d("startJobScheduler", "startJobScheduler ------ success!!!");
            } else {
                LogUtils.d("startJobScheduler", "startJobScheduler ------ fail!!!");
            }
        }
    }
}