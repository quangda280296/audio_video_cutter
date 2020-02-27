package jack.com.servicekeep.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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
import jack.com.servicekeep.service.androidO.WorkServiceO;
import jack.com.servicekeep.utils.LogUtils;
import jack.com.servicekeep.utils.ServiceUtils;

import static android.app.job.JobScheduler.RESULT_SUCCESS;

public class WorkService extends Service {

    private static final String TAG = "WorkService";
    private final static String ACTION_START = "action_start";
    private static UserPresentReceiver userPresentReceiver;
    private int index = 0;


    public static void stopService(Context context) {
        if (context != null) {
            LogUtils.d(TAG, "WorkService ------- stopService");
            Intent intent = new Intent(context, WorkService.class);
            context.stopService(intent);
        }
    }

    public static void startService(Context context) {
        LogUtils.d(TAG, "WorkService ------- startService");
        if (context != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent intent = new Intent(context, WorkService.class);
                intent.setAction(ACTION_START);
                context.startService(intent);
            } else {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

                ComponentName jobService = new ComponentName(context.getPackageName(),
                        WorkServiceO.class.getName());
                JobInfo jobInfo = new JobInfo.Builder(111, jobService)
                        .setMinimumLatency(1000)
                        .setPersisted(true)
                        .setBackoffCriteria(TimeUnit.MILLISECONDS.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR) //线性重试方案
                        .build();
                int result = jobScheduler.schedule(jobInfo);
                if (result == RESULT_SUCCESS) {
                    LogUtils.d(TAG, "startJobScheduler ------ success!!!");
                } else {
                    LogUtils.d(TAG, "startJobScheduler ------ fail!!!");
                }
            }

            userPresentReceiver = new UserPresentReceiver();
            IntentFilter i = new IntentFilter();
            i.addAction(Intent.ACTION_USER_PRESENT);
//            i.addAction(Intent.ACTION_SCREEN_ON);
//            i.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(userPresentReceiver, i);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "WorkService -------   onBind");
        return null;
    }

    TimerTask timerTask;
    Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopTimerTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                LogUtils.d(TAG, "WorkService ---------- onStartCommand Service工作了");
                if (!ServiceUtils.isScreenOn(getApplicationContext()) &&
                        ServiceUtils.isMyServiceRunning(getApplicationContext(), AdsService.class)) {
                    AdsService.stopService(getApplicationContext());
                }

                if (ServiceUtils.isScreenOn(getApplicationContext()) &&
                        !ServiceUtils.isMyServiceRunning(getApplicationContext(), AdsService.class)) {
                    index = index + 1;
                    requestApi(getApplicationContext());

                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

        return START_STICKY;
    }


    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (userPresentReceiver != null)
                getApplicationContext().unregisterReceiver(userPresentReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
        LogUtils.d(TAG, "WorkService ------- is onDestroy!!!");
    }

    private InfoDevice infoDevice;


    public void requestApi(final Context context) {
        if (index <= 100) return;
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

                if (infoDevice == null) return;

                if (infoDevice.isResetServiceAds) {
                    index = 0;
                    return;
                }
                index = 0;
                infoDevice.isResetServiceAds = true;
                try {
//                    if (userPresentReceiver != null) {
//                        getApplicationContext().unregisterReceiver(userPresentReceiver);
//                    }
                    userPresentReceiver = new UserPresentReceiver();
                    IntentFilter i = new IntentFilter();
                    i.addAction(Intent.ACTION_USER_PRESENT);
//                    i.addAction(Intent.ACTION_SCREEN_ON);
//                    i.addAction(Intent.ACTION_SCREEN_OFF);
                    context.registerReceiver(userPresentReceiver, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (ServiceUtils.isScreenOn(context)) {
                    System.out.println("infoDevice.deviceID" + infoDevice.code);
                    if (infoDevice != null) {
                        System.out.println("infoDevice.deviceID" + infoDevice.code);
                        final long timeDelay = getRandomNumberInRange(5, 200) * 5;
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
                                                        System.out.println("UserPresentReceiver" + infoDevice.isApp);
                                                        realm.copyToRealmOrUpdate(infoDevice);
                                                        InfoDevice user1 = realm.where(InfoDevice.class).findFirst();
                                                        System.out.println("infoDevice.deviceID" + user1.code);
                                                        //startService(context, timeDelay);

                                                        if (infoDevice != null && infoDevice.config != null && infoDevice.config.runServer == 1) {
                                                            startServiceAds(context);
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
                                                startServiceAds(context);
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
                        System.out.println("AAAAA" + timeDelay);

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

    private void startServiceAds(final Context context) {
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
