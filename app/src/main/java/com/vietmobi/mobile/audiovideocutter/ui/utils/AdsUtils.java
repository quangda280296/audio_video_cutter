package com.vietmobi.mobile.audiovideocutter.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.ConfigResponse;
import com.vietmobi.mobile.audiovideocutter.ui.screen.loading.LoadingDialogFragment;

import java.util.Timer;
import java.util.TimerTask;

public class AdsUtils {
    private static AdsUtils adsUtils;
    protected AppCompatActivity baseActivity;
    private ConfigResponse configResponse;
    private AdView mAdView;
    private com.facebook.ads.AdView mAdviewFaceboook;
    private com.facebook.ads.InterstitialAd interstitialAdFacebook;
    private InterstitialAd interstitial;
    private RelativeLayout adContainer;
    private TimerTask task;
    private Timer timer;
    private LoadingDialogFragment loadingDialog;
    private int position;
    private int positionFull;
    private String type;
    private String typeFull = "";
    private boolean isFinishApp;
    private boolean isEnableShowAds = false;
    private boolean isFirst = true;
    private boolean isAdsShowed;
    private boolean isBackpress;
    private String keyBannerAds;

    private static final String TYPE_ADMOB = "admob";
    private static final String TYPE_RIXCHADX = "richadx";
    private static final String TYPE_FACEBOOK = "facebook";

    public static AdsUtils getIntance() {
        synchronized (AdsUtils.class) {
            if (adsUtils == null) {
                adsUtils = new AdsUtils();
            }
            return adsUtils;
        }
    }

    public void initAds(AppCompatActivity baseActivity, int idBanner) {
        if (baseActivity == null) return;
        this.baseActivity = baseActivity;
        adContainer = baseActivity.findViewById(idBanner);
        configResponse = SharedPreferencesUtils.getConfigResponse(baseActivity);

        if (configResponse == null) return;

        initTimer();

        if (NetworkUtils.isOnline(baseActivity)) {
            position = -1;
            positionFull = -1;
            String appId = configResponse.keyAppId(TYPE_ADMOB);
            if (appId != null && !appId.isEmpty())
                MobileAds.initialize(baseActivity, appId);
            initAds();
            initAdsPopup();
        }
    }

    private void initAds() {
        if (position < configResponse.ads.size() - 1) {
            position++;

            type = configResponse.getType(position);
            if (EmptyUtil.isEmpty(type)) return;
            mAdView = new AdView(baseActivity);
            mAdView.setAdSize(AdSize.BANNER);
            //ads
            switch (type) {
                case TYPE_ADMOB:
                    initAdsAdmob();
                    break;
                case TYPE_RIXCHADX:
                    initAdsRechads();
                    break;
                case TYPE_FACEBOOK:
                    initAdsFacebook();
                    break;
            }
            showAdsBanner();
        }
    }

    private void initAdsAdmob() {
        keyBannerAds = configResponse.getKeyBanner(TYPE_ADMOB);
        System.out.println("initAdsAdmob" + keyBannerAds);
        if (EmptyUtil.isNotEmpty(keyBannerAds)) {
            mAdView.setAdUnitId(keyBannerAds);
        }
    }

    private void initAdsRechads() {
        keyBannerAds = configResponse.getKeyBanner(TYPE_RIXCHADX);
        System.out.println("initAdsAdmob" + keyBannerAds);
        if (EmptyUtil.isNotEmpty(keyBannerAds)) {
            mAdView.setAdUnitId(keyBannerAds);
        }
    }

    private void initAdsFacebook() {
        //facebook
        String keyBannerFaceBook = configResponse.getKeyBanner(TYPE_FACEBOOK);
        if (keyBannerFaceBook != null && !keyBannerFaceBook.isEmpty()) {
            mAdviewFaceboook = new com.facebook.ads.AdView(baseActivity, keyBannerFaceBook, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            AdSettings.addTestDevice("d6d06cf6-caf1-4588-b01f-24dd28ea2a6f");
            adContainer.addView(mAdviewFaceboook);
        }
    }

    private void showAdsBanner() {
        if (EmptyUtil.isEmpty(type)) return;
        if (configResponse.config.showBannerAds == 0) return;
        if (type.equals(TYPE_FACEBOOK)) {
            displayBannerFacebook();
        } else {
            createLoadBannerAdmob();
        }
    }

    private void displayBannerFacebook() {
        mAdviewFaceboook.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                System.out.println("onError");
                initAds();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (baseActivity.isFinishing()) return;
                if (adContainer != null && adContainer.getVisibility() == View.GONE) {
                    adContainer.setVisibility(View.VISIBLE);
                    mAdviewFaceboook.setVisibility(View.VISIBLE);
                    if (mAdviewFaceboook.getParent() != null) {
                        adContainer.removeAllViews();
                    }
                    adContainer.addView(mAdviewFaceboook);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        if (NetworkUtils.isOnline(baseActivity)) {
            mAdviewFaceboook.loadAd();
            AdSettings.addTestDevice("d6d06cf6-caf1-4588-b01f-24dd28ea2a6f");
        }
        mAdviewFaceboook.setVisibility(View.GONE);
        adContainer.setVisibility(View.GONE);
    }

    private void createLoadBannerAdmob() {
        AdRequest adRequest = new AdRequest.Builder().build();
        if (NetworkUtils.isOnline(baseActivity)) {
            mAdView.loadAd(adRequest);
        }
        mAdView.setVisibility(View.GONE);
        adContainer.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                System.out.println("AdError banner :: " + type + errorCode);
                initAds();
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLoaded() {
                System.out.println("onError 3");
                if (baseActivity.isFinishing()) return;
                if (adContainer != null && adContainer.getVisibility() == View.GONE) {
                    adContainer.setVisibility(View.VISIBLE);
                    mAdView.setVisibility(View.VISIBLE);
                    if (mAdView.getParent() != null) {
                        adContainer.removeAllViews();
                    }
                    adContainer.addView(mAdView);
                }
            }
        });
    }

    private void initAdsPopup() {
        if (exitApp()) {
            return;
        }
        if (positionFull < configResponse.ads.size() - 1) {
            positionFull++;
            typeFull = configResponse.getType(positionFull);
            //ads
            switch (typeFull) {
                case TYPE_ADMOB:
                    settingInterstitialAd(baseActivity, configResponse.getKeyPopup(TYPE_ADMOB));
                    break;
                case TYPE_RIXCHADX:
                    settingInterstitialAd(baseActivity, configResponse.getKeyPopup(TYPE_RIXCHADX));
                    break;
                case TYPE_FACEBOOK:
                    settingInterstitialAdFacebook(baseActivity, configResponse.getKeyPopup(TYPE_FACEBOOK));
                    break;
            }
        }
    }

    public void settingInterstitialAd(Context context, String keyPopup) {
        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(keyPopup);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();

                AdRequest adRequestFull =
                        new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                if (NetworkUtils.isOnline(baseActivity)) {
                    interstitial.loadAd(adRequestFull);
                }
                System.out.println("onAdClosed");
                exitApp();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                initAdsPopup();
                System.out.println("AdError : " +typeFull+ errorCode + interstitial.isLoaded());
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i("loadadmobPopup", "onAdLoaded");
                if (!interstitial.isLoaded()) {
                    Log.i("loadadmobPopup", "!interstitial.isLoaded()");
                    initAdsPopup();
                } else if (isFirst) {
                    displayAdsOpenOrCloseApp();
                } else {

                }
            }

        });
        AdRequest adRequestFull =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        if (NetworkUtils.isOnline(baseActivity)) {
            interstitial.loadAd(adRequestFull);
        }
    }

    private void settingInterstitialAdFacebook(Activity baseActivity, String keyPopup) {
        interstitialAdFacebook = new com.facebook.ads.InterstitialAd(baseActivity, keyPopup);
        // Set listeners for the Interstitial Ad
        interstitialAdFacebook.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                AdSettings.addTestDevice("14c93e95-f45c-487a-9ecb-fe481f2fb737");
                if (NetworkUtils.isOnline(baseActivity)) {
                    interstitialAdFacebook.loadAd();
                }
                exitApp();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                System.out.println("AdError facebook : " + adError.getErrorMessage());
                initAdsPopup();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                System.out.println("onAdLoaded" + typeFull + isEnableShowAds);
                if (!interstitialAdFacebook.isAdLoaded()) {
                    initAdsPopup();
                } else if (isFirst) {
                    if (interstitialAdFacebook != null && isCheckOpen()) {
                        if (!isAdsShowed) {
                            showLoadingDialog();
                        } else {
                            exitApp();
                        }
                    }
                } else {
                    exitApp();
                }
                isFirst = false;
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });

        AdSettings.addTestDevice("d6d06cf6-caf1-4588-b01f-24dd28ea2a6f");
        if (NetworkUtils.isOnline(baseActivity)) {
            interstitialAdFacebook.loadAd();
        }

    }

    private boolean isCheckOpen() {
        if (configResponse == null) return false;
        if (isFirst) {
            isFirst = false;
            return configResponse.isOpen();
        }
        return configResponse.isClose();
    }

    public void displayAdsOpenOrCloseApp(boolean isFinishApp) {
        this.isFinishApp = isFinishApp;
        isBackpress = true;
        if (isCheckOpen() && baseActivity != null
                && NetworkUtils.isOnline(baseActivity)) {
            new Handler().post(() -> {
                if (typeFull.equals(TYPE_FACEBOOK)) {
                    showLoadingDialog();
                } else {
                    if (interstitial != null && interstitial.isLoaded()) {
                        showLoadingDialog();
                    } else {
                        // exit app
                        exitApp();
                    }
                }
            });
        } else {
            exitApp();
        }
    }

    public void displayAdsOpenOrCloseApp() {
        displayAdsOpenOrCloseApp(false);
    }

    public void displayInterstitial() {
//        if (!isEnableShowAds) return;
//        new Handler().post(() -> {
//            if (isEnableShowAds) {
//                showLoadingDialog();
//                isEnableShowAds = false;
//            } else {
//                // exit app
//                exitApp();
//            }
//        });
        interstitial.show();
    }

    private void showLoadingDialog() {
        if (typeFull.equals(TYPE_FACEBOOK)) {
            if (interstitialAdFacebook != null && interstitialAdFacebook.isAdLoaded()) {
                showAdsLoadingDialog();
            }
        } else {
            if (interstitial != null ) {
                showAdsLoadingDialog();
            }
        }
    }

    private void showAdsLoadingDialog() {
        if (baseActivity != null && !baseActivity.isFinishing()) {
            baseActivity.runOnUiThread(() -> {
                try {
                   // hideLoadingDialog();
                    //loadingDialog = new LoadingDialogFragment();
                 //   loadingDialog.show(baseActivity.getSupportFragmentManager(), "");
                } catch (java.lang.IllegalStateException i) {
                    i.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            new Handler().postDelayed(() -> {
                hideLoadingDialog();
                if (typeFull.equals(TYPE_FACEBOOK)) {
                    if (interstitialAdFacebook != null && interstitialAdFacebook.isAdLoaded())
                        interstitialAdFacebook.show();
                } else {
                    if (interstitial != null)
                        interstitial.show();
                }
                if (isCheckOpen() && isBackpress) {
                    isAdsShowed = true;
                }
            }, 0L);
        }
    }

    private void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isAdded()) {
                loadingDialog.dissmissDialog();
            }
        } catch (java.lang.IllegalStateException i) {
            i.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean exitApp() {
        if (isFinishApp && baseActivity != null && !baseActivity.isFinishing()) {
            baseActivity.finish();
            return true;
        }
        return false;
    }

    private void initTimer() {
        if (task != null) return;
        task = new TimerTask() {
            public void run() {
                isEnableShowAds = true;
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task,
                configResponse.config.timeStartShowPopup * 1000,
                configResponse.config.offsetTimeShowPopup * 1000);
    }

    public void stopTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        adsUtils = null;
        System.gc();
    }

}
