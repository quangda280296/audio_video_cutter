package com.vietmobi.mobile.audiovideocutter.data.remote.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigClient {

    private ConfigService apiService;
    private static Retrofit retrofit;
    private static String BASE_URL = "http://gamemobileglobal.com/";

    public Class<ConfigService> getClassService() {
        return ConfigService.class;
    }

    private static ConfigClient INSTANCE;

    public static ConfigClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigClient();
        }
        return INSTANCE;
    }

    public ConfigClient() {
        retrofit = getClient();
        createAppApi();
    }


    private ConfigClient createAppApi() {
        apiService = getApiService();
        return this;
    }

    public ConfigService getApiService() {
        if (apiService == null) {
            apiService = create(getClassService());
        }
        return apiService;
    }


    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    public static Retrofit getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        } else {
            if (!retrofit.baseUrl().equals(BASE_URL)) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }

        return retrofit;
    }
}
