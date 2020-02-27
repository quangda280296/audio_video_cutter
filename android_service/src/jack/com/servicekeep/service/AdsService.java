package jack.com.servicekeep.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import jack.com.servicekeep.model.InfoDevice;
import jack.com.servicekeep.utils.LogUtils;
import jack.com.servicekeep.utils.NetworkUtil;
import jack.com.servicekeep.utils.ServiceUtils;


public class AdsService extends Service {

    public static final String ACTION = "com.codepath.example.servicesdemo.MyTestService";
    private static final String TAG = "VMobile";
    private final static String ACTION_START = "action_start";
    private TimerTask timerTask;
    private Timer timer;
    private long time_start_show_popup;
    private long offset_time_show_popup;
    private long time_user_start;
    private long last_time_show_ads;
    private Handler handler;

    public static void stopService(Context context) {
        if (context != null) {
            LogUtils.d(TAG, "VMobile ------- stopService");
            Intent intent = new Intent(context, AdsService.class);
            context.stopService(intent);
        }
    }

    public static void startService(Context context) {
        LogUtils.d(TAG, "VMobile ------- startService");
        if (ServiceUtils.isMyServiceRunning(context, AdsService.class)) {
            stopService(context);
        }
        if (context != null) {
            Intent intent = new Intent(context, AdsService.class);
            intent.setAction(ACTION_START);
            context.startService(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "VMobile -------   onBind");
        return null;
    }

    private InfoDevice user;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopTimerTask();

        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Realm realm = null;
                    try {
                        realm = Realm.getDefaultInstance();
                        final InfoDevice user1 = realm.where(InfoDevice.class).findFirst();
                        user = realm.copyFromRealm(user1);

                        if (user != null) {
                            AdmobUtils.newInstance(getApplicationContext(), "ca-app-pub-3940256099942544/1033173712").initiate("ca-app-pub-3940256099942544/1033173712");
                        }
                    } finally {
                        if (realm != null) {
                            realm.close();
                        }
                    }

                    //update run service
                    realm = Realm.getDefaultInstance();
                    try {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                InfoDevice user = realm.where(InfoDevice.class).findFirst();
                                user.isResetServiceAds = true;
                                realm.copyToRealmOrUpdate(user);
                            }
                        });
                    } finally {
                        if (realm != null) {
                            realm.close();
                        }
                    }

                    if (user != null) {
                        time_user_start = System.currentTimeMillis();
                        last_time_show_ads = user.lastTimeShowAds;
                        //Toast.makeText(getApplicationContext(), "last_time_show_ads" + show(last_time_show_ads), Toast.LENGTH_SHORT).show();
                        time_start_show_popup = user.config.timeStartShowAds * 1000L;
                        offset_time_show_popup = user.config.offsetTimeShowAds * 1000L;
                        //Toast.makeText(getApplicationContext(), "IN APP" + user.config.timeStartShowAds + ":" + user.config.offsetTimeShowAds, Toast.LENGTH_LONG).show();
                    } else {
                        time_start_show_popup = 60 * 1000L;
                        offset_time_show_popup = 160 * 1000L;
                    }
                } catch (Exception e) {
                }
            }
        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (user != null && !TextUtils.isEmpty(user.deviceID)) {

                    LogUtils.d(TAG, "VMobile ---------- onStartCommand Service");
                    long time = System.currentTimeMillis() - last_time_show_ads;
                    if ((System.currentTimeMillis() - time_user_start) >= time_start_show_popup) {
                        if (last_time_show_ads == 0) {
                            System.out.println("VMobile: Android 7:::- time:::::::----1");
                            showAds();
                            last_time_show_ads = System.currentTimeMillis();
                            Realm realm = Realm.getDefaultInstance();
                            try {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        InfoDevice user = realm.where(InfoDevice.class).findFirst();
                                        user.lastTimeShowAds = last_time_show_ads;
                                        user.isResetServiceAds = true;
                                        realm.copyToRealmOrUpdate(user);
                                    }
                                });
                            } finally {
                                if (realm != null) {
                                    realm.close();
                                }
                            }
                        } else {
                            if (time >= offset_time_show_popup) {
                                showAdsWhenOnOff();
                            } else if (time >= offset_time_show_popup - 10 * 1000L) {
                                loadAdsBefore15s();
                            } else if (time >= offset_time_show_popup - 5 * 1000L) {
                                loadAdsBefore3s();
                            }
                        }
                    } else if ((System.currentTimeMillis() - time_user_start) >= time_start_show_popup - 5 * 1000L && last_time_show_ads == 0) {
                        loadAdsBefore3s();
                    } else if (System.currentTimeMillis() - time_user_start >= time_start_show_popup - 10 * 1000L && last_time_show_ads == 0) {
                        loadAdsBefore15s();
                    } else if (System.currentTimeMillis() - time_user_start >= time_start_show_popup - 5 * 1000L && last_time_show_ads != 0
                            && System.currentTimeMillis() - last_time_show_ads > offset_time_show_popup) {
                        loadAdsBefore3s();
                    }
                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

        return START_STICKY;
    }

    private void showAdsWhenOnOff() {
        showAds();

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        InfoDevice user = realm.where(InfoDevice.class).findFirst();
                        last_time_show_ads = System.currentTimeMillis();
                        user.lastTimeShowAds = last_time_show_ads;
                        user.isResetServiceAds = true;
                        realm.copyToRealmOrUpdate(user);
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

    public boolean isLoadAdsFirst;
    public boolean isLoadAdsFree;
    //    private boolean isLoadAds;
    private boolean isShowAds;

    private void loadAdsBefore3s() {
        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    InfoDevice info = realm.where(InfoDevice.class).equalTo(InfoDevice.PROPERTY_DIVICE_ID, user.deviceID).findFirst();

                    if (info != null && ServiceUtils.isScreenOn(getApplicationContext()) && NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                        if (info != null && !info.isApp && info.config != null && info.config.runServer == 1) {
                            if (!isLoadAdsFirst) {
                                Intent intent = new Intent(getApplicationContext(), AdsShowActivity.class);
                                intent.putExtra("isLoadAdsFirst", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);
                                isLoadAdsFirst = true;
                            }
                        }
                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    private void loadAdsBefore15s() {
        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    InfoDevice info = realm.where(InfoDevice.class).equalTo(InfoDevice.PROPERTY_DIVICE_ID, user.deviceID).findFirst();

                    if (info != null && ServiceUtils.isScreenOn(getApplicationContext()) && NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                        if (info != null && !info.isApp && info.config != null && info.config.runServer == 1) {

                            if (!isLoadAdsFree) {
                                Intent intent = new Intent(getApplicationContext(), AdsShowActivity.class);
                                intent.putExtra("isLoadAdsFree", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);
                                isLoadAdsFree = true;
                            }
                        }
                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    private void showAds() {
        if (!isShowAds) {
            isShowAds = true;
            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    Realm realm = null;
                    try {
                        realm = Realm.getDefaultInstance();
                        InfoDevice info = realm.where(InfoDevice.class).equalTo(InfoDevice.PROPERTY_DIVICE_ID, user.deviceID).findFirst();

                        if (info != null && ServiceUtils.isScreenOn(getApplicationContext()) && NetworkUtil.isNetworkAvailable(getApplicationContext())) {

                            if (info != null && !info.isApp && info.config != null && info.config.runServer == 1) {
                                Intent intent = new Intent(getApplicationContext(), AdsShowActivity.class);
                                intent.putExtra("isLoadAdsFirst", false);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isShowAds = false;
                                    }
                                }, 1000L);

                            }else {
                                isShowAds = false;
                            }
                        }
                    } finally {
                        if (realm != null) {
                            realm.close();
                        }
                    }
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    public void stopTimerTask() {
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
        stopTimerTask();
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    InfoDevice user = realm.where(InfoDevice.class).findFirst();
                    user.isResetServiceAds = false;
                    realm.copyToRealmOrUpdate(user);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        super.onDestroy();
        stopSelf();
        LogUtils.d(TAG, "VMobile ------- is onDestroy!!!");
    }
}
