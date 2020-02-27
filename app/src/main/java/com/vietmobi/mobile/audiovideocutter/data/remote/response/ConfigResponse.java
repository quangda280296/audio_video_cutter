package com.vietmobi.mobile.audiovideocutter.data.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vietmobi.mobile.audiovideocutter.ui.utils.EmptyUtil;

import java.util.List;

public class ConfigResponse {

    @SerializedName("ads")
    @Expose
    public List<Ads> ads = null;
    @SerializedName("config")
    @Expose
    public Config config;
    @SerializedName("update")
    @Expose
    public Update update;
    @SerializedName("shortcut")
    @Expose
    public List<Object> shortcut = null;
    @SerializedName("link_more_apps")
    @Expose
    public String linkMoreApps;

    public class Config {
        @SerializedName("show_banner_ads")
        @Expose
        public int showBannerAds;
        @SerializedName("time_start_show_popup")
        @Expose
        public int timeStartShowPopup;
        @SerializedName("offset_time_show_popup")
        @Expose
        public int offsetTimeShowPopup;
        @SerializedName("show_popup_open_app")
        @Expose
        public int showPopupOpenApp;
        @SerializedName("show_popup_close_app")
        @Expose
        public int showPopupCloseApp;
    }

    public class Update {
        @SerializedName("update_status")
        @Expose
        public int updateStatus;
        @SerializedName("update_title")
        @Expose
        public String updateTitle;
        @SerializedName("update_message")
        @Expose
        public String updateMessage;
        @SerializedName("update_url")
        @Expose
        public String updateUrl;
    }

    public class Ads {
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("key")
        @Expose
        public Key key;
    }

    public class Key {
        @SerializedName("banner")
        @Expose
        public String banner;
        @SerializedName("popup")
        @Expose
        public String popup;
        @SerializedName("video")
        @Expose
        public String video;
        @SerializedName("thumbai")
        @Expose
        public String thumbai;
        @SerializedName("appid")
        @Expose
        public String appid;
    }

    public String keyAppId(String keyType) {
        if (EmptyUtil.isEmpty(ads)) return "";
        String appId = "";
        for (ConfigResponse.Ads data : ads) {
            if (data.type.equals(keyType)) {
                appId = data.key.appid;
                break;
            }
        }
        return appId;
    }

    public String getKeyBanner(String keyType) {
        if (EmptyUtil.isEmpty(ads)) return "";
        String keyBanner = "";
        for (ConfigResponse.Ads data : ads) {
            if (data.type.equals(keyType)) {
                keyBanner = data.key.banner;
                break;
            }
        }
//        return "ca-app-pub-3940256099942544/6300978111";
        return keyBanner;
    }

    public String getKeyPopup(String keyType) {
        if (EmptyUtil.isEmpty(ads)) return "";
        String keyBanner = "";
        for (ConfigResponse.Ads data : ads) {
            if (data.type.equals(keyType)) {
                keyBanner = data.key.popup;
                break;
            }
        }
//        return "ca-app-pub-3940256099942544/1033173712";
        return keyBanner;
    }

    public String getType(int position) {
        if (EmptyUtil.isEmpty(ads) || position >= ads.size()) return "";
        return ads.get(position).type;
    }

    public boolean isOpen() {
        return config.showPopupOpenApp == 1;
    }

    public boolean isClose() {
        return config.showPopupCloseApp == 1;
    }
}
