package jack.com.servicekeep.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class AdmobUtils {
    private static String TAG = "AdmobUtils";

    public static AdmobUtils admobUtils;
    public String keyAds;
    private int count;
    private Context context;
    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;
    private AdmobUtilsListener mListener;
    private boolean showAfterLoad = false;

    public static AdmobUtils newInstance(Context context) {
        if (admobUtils == null) {
            admobUtils = new AdmobUtils();
            admobUtils.context = context;
            admobUtils.count = 0;
            System.out.println(TAG + admobUtils);
        }
        return admobUtils;
    }

    public static AdmobUtils newInstance(Context context, String key) {
        if (admobUtils == null) {
            admobUtils = new AdmobUtils();
            admobUtils.context = context;
            admobUtils.keyAds = key;
            admobUtils.count = 0;
            System.out.println(TAG + admobUtils);
        }
        return admobUtils;
    }

    public interface AdmobUtilsListener {
        void onAdsClicked();

        void onAdsOpened(String key);

        void onAdsOpenLoadFail();

        void onAdsClosed();
    }

    public void initiate(final String adUnitId, final boolean isLoadAdsFirst) {
        try {
            keyAds = adUnitId;
            mInterstitialAd = new InterstitialAd(context);
            mInterstitialAd.setAdUnitId(adUnitId);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    if (mListener != null) {
                        mListener.onAdsClosed();
                        Log.i(TAG, "onAdClosed()");
                    }
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    showAfterLoad = false;

                    if (adUnitId.equals("ca-app-pub-3940256099942544/1033173712")) return;

                    if (isLoadAdsFirst) {
                    } else {
                        count = count + 1;
                    }
                    if(mListener!=null) mListener.onAdsOpenLoadFail();
                    Log.i(TAG, "onAdFailedToLoad():errorCode = " + errorCode);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    showAfterLoad = false;
                    if (mListener != null) {
                        mListener.onAdsClicked();
                        Log.i(TAG, "onAdLeftApplication()");
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.i(TAG, "onAdLoaded()");

                    if (showAfterLoad) {

                    }

                    showAfterLoad = false;

                    if (adUnitId.equals("ca-app-pub-3940256099942544/1033173712")) return;

                    if (!isLoadAdsFirst)
                        count = count + 1;
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    showAfterLoad = false;
                    if (mListener != null) {
                        mListener.onAdsOpened(keyAds);
                        Log.i(TAG, "onAdOpened()");
                    }
                }
            });

            mAdRequest = new AdRequest.Builder().addTestDevice("49C5B9568508C5D85FE989CE0E5143A8").build();
            mInterstitialAd.loadAd(mAdRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initiate(String adUnitId) {
        initiate(adUnitId, false);
    }

    public boolean isLoaded() {
        try {
            return mInterstitialAd.isLoaded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean showAds(AdmobUtilsListener listener) {
        Log.i(TAG, "show Ads");
        try {
            mListener = listener;

            try {
                mInterstitialAd.show();
                Log.i(TAG, "Ads show()");
                return true;
            } catch (Exception e) {
                Log.i(TAG, "Catch exception 1 = " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.i(TAG, "Catch exception 2 = " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
