package jack.com.servicekeep.act;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import jack.com.servicekeep.model.InfoDevice;

public class BaseVMActivity extends Activity {

    private Realm realm;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                updateInfoApp(true);
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 100, 2500);

    }

    @Override
    protected void onPause() {
        if (timerTask != null) {
            timer.cancel();
            timer = null;
            timerTask.cancel();
            timerTask = null;
        }
        updateInfoApp(false);

        super.onPause();
    }

    private void updateInfoApp(final boolean isActive) {
        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                InfoDevice infoDevice;
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    infoDevice = realm.where(InfoDevice.class).findFirst();
                    if (infoDevice != null) {
                        updateEmployeeRecords(infoDevice, isActive);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateInfoApp(false);
        if (realm != null) {
            realm.close();
        }
    }

    private void updateEmployeeRecords(final InfoDevice infoDevice, final boolean isActive) {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (infoDevice != null && !TextUtils.isEmpty(infoDevice.deviceID)) {
                        try {
                            infoDevice.isApp = isActive;

                            realm.copyToRealmOrUpdate(infoDevice);
                            InfoDevice user = realm.where(InfoDevice.class).findFirst();
                            System.out.println("infoDevice.deviceID" + user.isApp);
                        } catch (RealmPrimaryKeyConstraintException e) {

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
