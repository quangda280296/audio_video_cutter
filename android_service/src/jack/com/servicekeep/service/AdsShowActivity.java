package jack.com.servicekeep.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Toast;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import jack.com.servicekeep.R;
import jack.com.servicekeep.model.InfoDevice;
import jack.com.servicekeep.network.BaseObserver;
import jack.com.servicekeep.network.VMobileApi;
import okhttp3.ResponseBody;

public class AdsShowActivity extends AppCompatActivity {

    private InfoDevice user;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);


        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            InfoDevice infoDevice = realm.where(InfoDevice.class).findFirst();
            user = realm.copyFromRealm(infoDevice);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        if (user == null) {
            finish();
            return;
        }

        boolean isLoadAdsFirst;

        if (getIntent() != null && getIntent().getExtras().containsKey("isLoadAdsFirst")) {
            isLoadAdsFirst = getIntent().getExtras().getBoolean("isLoadAdsFirst");

            if (isLoadAdsFirst) {
                AdmobUtils.newInstance(AdsShowActivity.this,
                        user.ads.get(index).key.popup
                ).initiate(user.ads.get(index).key.popup, true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
            }
        }

        if (getIntent() != null && getIntent().getExtras().containsKey("isLoadAdsFree")) {

            isLoadAdsFirst = getIntent().getExtras().getBoolean("isLoadAdsFree");
            if (isLoadAdsFirst) {
                AdmobUtils.newInstance(AdsShowActivity.this,
                        "ca-app-pub-3940256099942544/1033173712"
                ).initiate("ca-app-pub-3940256099942544/1033173712", true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
            }
        }

        if (AdmobUtils.newInstance(this,
                user.ads.get(index).key.popup).isLoaded()) {
            AdmobUtils.newInstance(this,
                    user.ads.get(index).key.popup).showAds(new AdmobUtils.AdmobUtilsListener() {
                @Override
                public void onAdsClicked() {
                }

                @Override
                public void onAdsOpenLoadFail() {
                }

                @Override
                public void onAdsOpened(String key) {
                    postApi(key);
                }

                @Override
                public void onAdsClosed() {
                    AdmobUtils.newInstance(AdsShowActivity.this,
                            user.ads.get(index).key.popup
                    ).initiate(user.ads.get(index).key.popup);
                    finish();
                }
            });
        } else {
            AdmobUtils.newInstance(this).initiate(user.ads.get(index).key.popup);
            finish();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing())
                    finish();
            }
        }, 1000L);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(AdsService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            Toast.makeText(AdsShowActivity.this, "AKM", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                Toast.makeText(AdsShowActivity.this, resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void finishActivityAds() {
        try {
            updateEmployeeRecords(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void postApi(String key) {
        if (!TextUtils.isEmpty(key)) {
            user.isApp = false;
            user.timenow = String.valueOf(System.currentTimeMillis());
            updateEmployeeRecords(user);
            VMobileApi.postAds(user, key, new BaseObserver<ResponseBody>() {

                @Override
                protected void onResponse(final ResponseBody ads) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing())
                                finishActivityAds();
                        }
                    }, 500L);
                }

                @Override
                protected void onFailure() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing())
                                finishActivityAds();
                        }
                    }, 500L);
                }

                @Override
                protected void addDisposableManager(Disposable disposable) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (user != null)
            updateEmployeeRecords(user);
        super.onDestroy();
    }

    private void updateEmployeeRecords(final InfoDevice infoDevice) {
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
                        realm.copyToRealm(infoDevice);
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
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
