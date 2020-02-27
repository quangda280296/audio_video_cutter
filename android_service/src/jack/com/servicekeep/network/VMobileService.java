package jack.com.servicekeep.network;

import io.reactivex.Observable;
import jack.com.servicekeep.model.AppInfoResponse;
import jack.com.servicekeep.model.CountryResponse;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface VMobileService {

    @GET("api/apps/control_s.php")
    Observable<AppInfoResponse> getInfoControl(
            @Query("code") String code,
            @Query("deviceID") String deviceId,
            @Query("version") String version,
            @Query("os_version") String os_version,
            @Query("country") String country,
            @Query("timereg") String timereg,
            @Query("timenow") String timenow,
            @Query("time_delay") long timeDelay
    );

    @GET
    Observable<CountryResponse> getCountry(@Url String url);


    @POST("api/apps/log_show_ads_sv.php")
    @FormUrlEncoded
    Observable<ResponseBody> postAds(
            @Field("code") String code,
            @Field("deviceID") String deviceID,
            @Field("version") String version,
            @Field("os_version") String os_version,
            @Field("country") String country,
            @Field("timereg") String timereg,
            @Field("timenow") String timenow,
            @Field("key_ads") String key_ads);
}
