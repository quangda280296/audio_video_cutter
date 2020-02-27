package com.vietmobi.mobile.audiovideocutter.ui.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.vietmobi.mobile.audiovideocutter.BuildConfig;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.data.remote.api.BaseObserver;
import com.vietmobi.mobile.audiovideocutter.data.remote.api.ConfigApi;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.InfoResponse;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.NotificationResponse;
import com.vietmobi.mobile.audiovideocutter.ui.screen.MainApplication;
import com.vietmobi.mobile.audiovideocutter.ui.screen.home.HomeActivity;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SharedPreferencesUtils;

import java.io.IOException;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TYPE_NOTIFY = "notify";
    private static final String TYPE_STATISTICAL = "statistical";
    private static final String TYPE_ADS = "ads";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println("Token ::: " + s);

    }

    @Override
    public void onMessageReceived(RemoteMessage rm) {
        Gson gson = new Gson();
        NotificationResponse response =
                gson.fromJson(gson.toJson(rm.getData()), NotificationResponse.class);
        handleNotify(response);
        System.out.println("onMessageReceived" + response.pushType);
    }

    private void handleNotify(NotificationResponse response) {
        if (response != null && !response.pushType.isEmpty()) {
            switch (response.pushType) {
                case TYPE_NOTIFY:
                    customNotification(response);
                    break;
                case TYPE_STATISTICAL:
                    callApiPostStatistical(response);
                    break;
                case TYPE_ADS:
                    break;
            }
        }
    }

    private void customNotification(NotificationResponse response) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify);
        NotificationCompat.BigPictureStyle notifystyle = new NotificationCompat.BigPictureStyle();
        notifystyle.bigPicture(bitmap);
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.layout_custom_nofity);
        remoteViews.setImageViewResource(R.id.image_notify, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.title, response.subtitle);
        remoteViews.setTextViewText(R.id.message, response.message);
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri u = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notify)
                .setTicker("Ticker")
                .setSound(response.sound.equals("0") ? u : null)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setCustomBigContentView(remoteViews);

        final Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = remoteViews;
        }
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, notification);

    }

    private void callApiPostStatistical(NotificationResponse response) {
        InfoResponse infoResponse = SharedPreferencesUtils.getInfoResponse(MainApplication.context);
        String url = "http://gamemobileglobal.com/api/apps/update_time_push_notify.php";
        ConfigApi.postStasticticalToken(url,
                response.pushId,
                BuildConfig.code,
                BuildConfig.VERSION_NAME,
                MainApplication.deviceId,
                FirebaseInstanceId.getInstance().getToken(),
                BuildConfig.APPLICATION_ID,
                String.valueOf(Build.VERSION.SDK_INT),
                Build.MODEL,
                infoResponse == null ? "" : infoResponse.country, new BaseObserver<ResponseBody>() {
                    @Override
                    protected void onResponse(ResponseBody responseBody) {
                        try {
                            System.out.println("responseBody" + responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
