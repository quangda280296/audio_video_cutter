package com.vietmobi.mobile.audiovideocutter.data.remote.api;

import com.vietmobi.mobile.audiovideocutter.data.remote.response.ConfigResponse;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.InfoResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ConfigService {

    @GET
    Observable<InfoResponse> getInfo(@Url String url);
    @GET
    Observable<ConfigResponse> getConfig(@Url String url,
                                         @Query("code") String code,
                                         @Query("country") String country,
                                         @Query("version") String version,
                                         @Query("deviceID") String deviceID,
                                         @Query("timereg") String timereg,
                                         @Query("package") String mPackage,
                                         @Query("os_version") String os_version,
                                         @Query("phone_name") String phone_name);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> postRegisterToken(@Url String url,
                                               @Field("token_id") String token_id,
                                               @Field("code") String code,
                                               @Field("version") String version,
                                               @Field("deviceID") String deviceID,
                                               @Field("package") String packageApp,
                                               @Field("os_version") String os_version,
                                               @Field("phone_name") String phone_name,
                                               @Field("country") String country);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> postStasticticalToken(@Url String url,
                                                   @Field("pushId") String pushId,
                                                   @Field("code") String code,
                                                   @Field("version") String version,
                                                   @Field("deviceID") String deviceID,
                                                   @Field("token_id") String token_id,
                                                   @Field("package") String packageApp,
                                                   @Field("os_version") String os_version,
                                                   @Field("phone_name") String phone_name,
                                                   @Field("country") String country);
}
