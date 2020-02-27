package jack.com.servicekeep.app;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import jack.com.servicekeep.Constant;
import jack.com.servicekeep.manager.KeepAliveManager;
import jack.com.servicekeep.model.CountryResponse;
import jack.com.servicekeep.model.InfoDevice;
import jack.com.servicekeep.network.BaseObserver;
import jack.com.servicekeep.network.VMobileApi;
import jack.com.servicekeep.network.VMobileClient;

public class VMApplication extends MultiDexApplication implements LifeCycleDelegate {

    private InfoDevice infoDevice;

    @Override
    public void onCreate() {
        super.onCreate();

        AppLifecycleHandler appLifecycleHandler = new AppLifecycleHandler(this);
        registerLifecycleHandler(appLifecycleHandler);

        VMobileClient.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        KeepAliveManager.INSTANCE.startKeepAliveService(this);

        Realm.init(getApplicationContext());

        RealmConfiguration config =
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

        Realm.setDefaultConfiguration(config);
    }

    private void callApiIpInfo(final String code, final String version) {
        final Realm realm = Realm.getDefaultInstance();
        infoDevice = realm.where(InfoDevice.class).findFirst();
        if (infoDevice == null || (infoDevice != null && TextUtils.isEmpty(infoDevice.country))) {
            VMobileApi.getCountry("https://ipinfo.io/json", new BaseObserver<CountryResponse>() {

                @Override
                protected void onResponse(final CountryResponse responseBody) {

                    final String osVersion = String.valueOf(Build.VERSION.RELEASE);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            try {
                                if (infoDevice == null) {
                                    infoDevice = new InfoDevice();
                                }
                                infoDevice.timereg = String.valueOf(System.currentTimeMillis());
                                infoDevice.deviceID = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID);

                                infoDevice.code = code;
                                infoDevice.osVersion = osVersion;
                                infoDevice.version = version;
                                infoDevice.country = responseBody.country;
                                realm.copyToRealmOrUpdate(infoDevice);
                                System.out.println("BEER" + infoDevice);
                            } catch (RealmPrimaryKeyConstraintException e) {

                            }
                        }
                    });

                }

                @Override
                protected void onFailure() {

                }

                @Override
                protected void addDisposableManager(Disposable disposable) {

                }
            });
        }
    }

    protected void initInfoDevice(String codeApp, String versionName) {
        Constant.CODE = codeApp;
        Constant.VERSION = versionName;
        callApiIpInfo(Constant.CODE, Constant.VERSION);
    }

    protected void registerLifecycleHandler(AppLifecycleHandler lifeCycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler);
        registerComponentCallbacks(lifeCycleHandler);
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (RuntimeException multiDexException) {
            multiDexException.printStackTrace();
        }

    }

    @Override
    public void onAppBackgrounded() {

    }

    @Override
    public void onAppForegrounded() {

    }
}
