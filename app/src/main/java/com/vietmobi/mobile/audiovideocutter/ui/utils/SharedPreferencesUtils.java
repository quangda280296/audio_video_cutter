package com.vietmobi.mobile.audiovideocutter.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.ConfigResponse;
import com.vietmobi.mobile.audiovideocutter.data.remote.response.InfoResponse;

import java.lang.reflect.Type;

public class SharedPreferencesUtils {

    public static String KEY_ROUND_DECIMAL = "KEY_ROUND_DECIMAL";
    public static String TIME_START_OPEN_APP = "TIME_START_OPEN_APP";
    public static String TOKEN_FIREBASE = "TOKEN_FIREBASE";
    public static String KEY_VERSION_NAME = "KEY_VERSION_NAME";

    public static void saveVersionName(Context context, String data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(KEY_VERSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_VERSION_NAME, json);
        editor.apply();
    }

    public static String getVersionName(Context context) {
        SharedPreferences sp = context
                .getSharedPreferences(KEY_VERSION_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_VERSION_NAME, null);
    }

    /**
     * Save Info country
     *
     * @param context
     * @param infoResponse
     **/
    public static void saveInfoResponse(Context context, InfoResponse infoResponse) {
        Gson gson = new Gson();
        String json = gson.toJson(infoResponse);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(InfoResponse.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(InfoResponse.class.getName(), json);
        editor.apply();
    }


    /**
     * Get Info country
     *
     * @param context
     **/
    public static InfoResponse getInfoResponse(Context context) {
        Gson gson = new Gson();
        InfoResponse productFromShared;
        SharedPreferences sharedPref =
                context.getSharedPreferences(InfoResponse.class.getName(), Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(InfoResponse.class.getName(), null);
        Type type = new TypeToken<InfoResponse>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);
        return productFromShared;
    }

    /**
     * save
     *
     * @param context
     * @param configResponse
     **/
    public static void saveConfigResponse(Context context, ConfigResponse configResponse) {
        Gson gson = new Gson();
        String json = gson.toJson(configResponse);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(ConfigResponse.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConfigResponse.class.getName(), json);
        editor.apply();
    }


    /**
     * Get Config locale
     *
     * @param context
     **/
    public static ConfigResponse getConfigResponse(Context context) {
        Gson gson = new Gson();
        ConfigResponse productFromShared;
        SharedPreferences sharedPref =
                context.getSharedPreferences(ConfigResponse.class.getName(), Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(ConfigResponse.class.getName(), null);
        Type type = new TypeToken<ConfigResponse>() {
        }.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);
        return productFromShared;
    }

    /**
     * Save time open app
     *
     * @param context
     * @param timeOpenApp
     */
    public static void saveTimeOpenApp(Context context, String timeOpenApp) {
        SharedPreferences sp = context.getSharedPreferences(TIME_START_OPEN_APP
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TIME_START_OPEN_APP, timeOpenApp);
        editor.apply();
    }

    /**
     * Get time open app
     *
     * @param context
     */
    public static String getTimeOpenApp(Context context) {
        SharedPreferences sp = context
                .getSharedPreferences(TIME_START_OPEN_APP, Context.MODE_PRIVATE);
        return sp.getString(TIME_START_OPEN_APP, "");
    }

    public static void saveTokenFirebase(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(TOKEN_FIREBASE
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TOKEN_FIREBASE, s);
        editor.apply();
    }

    public static String getTokenFirebase(Context context) {
        SharedPreferences sp = context
                .getSharedPreferences(TOKEN_FIREBASE, Context.MODE_PRIVATE);
        return sp.getString(TOKEN_FIREBASE, null);
    }
}
