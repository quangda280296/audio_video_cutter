package jack.com.servicekeep.network;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jack.com.servicekeep.model.AppInfoResponse;
import jack.com.servicekeep.model.CountryResponse;
import jack.com.servicekeep.model.InfoDevice;
import okhttp3.ResponseBody;

public class VMobileApi {

    protected static <M> void addObservable(Observable<M> observable, Observer<M> subscription) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscription);
    }

    public static void getInfoControl(InfoDevice infoDevice,Observer<AppInfoResponse> subscriber) {
        addObservable(VMobileClient.getInstance().getApiService().getInfoControl(
                infoDevice.code,
                infoDevice.deviceID,
                infoDevice.version,
                infoDevice.osVersion,
                infoDevice.country,
                infoDevice.timereg,
                infoDevice.timenow,
                infoDevice.timeDelay
        ), subscriber);
    }


    public static void postAds(InfoDevice infoDevice,String key_ads,Observer<ResponseBody> subscriber) {
        addObservable(VMobileClient.getInstance().getApiService().postAds(
                infoDevice.code,
                infoDevice.deviceID,
                infoDevice.version,
                infoDevice.osVersion,
                infoDevice.country,
                infoDevice.timereg,
                infoDevice.timenow,
                key_ads
        ), subscriber);
    }

    public static void getCountry(String url, Observer<CountryResponse> subscriber) {
        addObservable(VMobileClient.getInstance().getApiService().getCountry(url), subscriber);
    }
}
