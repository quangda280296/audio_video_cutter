package jack.com.servicekeep.network;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import jack.com.servicekeep.BuildConfig;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jacky on 1/9/18.
 */

public class VMobileClient {

    private static final int DEFAULT_TIMEOUT = 30;
    private VMobileService apiService;
    private static OkHttpClient okHttpClient;
    public static Context context;
    private static Retrofit retrofit;
    private File httpCacheDirectory;

    public Class<VMobileService> getClassService() {
        return VMobileService.class;
    }

    public static class SingletonHolder {
        private static VMobileClient INSTANCE = new VMobileClient(context);
    }

    public static VMobileClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static VMobileClient getInstance(Context context) {
        if (context != null) {
            VMobileClient.context = context;
        }
        return SingletonHolder.INSTANCE;
    }

    public VMobileClient(Context context) {
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(context.getCacheDir(), "tamic_cache");
        }

        if (BuildConfig.DEBUG) {
            okHttpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(
                            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new CacheInterceptor(context))
                    .addNetworkInterceptor(new CacheInterceptor(context))
                    .retryOnConnectionFailure(true)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                    .build();
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new CacheInterceptor(context))
                    .addNetworkInterceptor(new CacheInterceptor(context))
                    .retryOnConnectionFailure(true)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                    .build();
        }


        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .build();

        createAppApi();
    }


    private VMobileClient createAppApi() {
        apiService = getApiService();
        return this;
    }

    public VMobileService getApiService() {
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
}