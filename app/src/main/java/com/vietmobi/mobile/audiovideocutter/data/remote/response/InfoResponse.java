package com.vietmobi.mobile.audiovideocutter.data.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InfoResponse {
    @SerializedName("ip")
    @Expose
    public String ip;
    @SerializedName("hostname")
    @Expose
    public String hostname;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("region")
    @Expose
    public String region;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("loc")
    @Expose
    public String loc;
    @SerializedName("org")
    @Expose
    public String org;
}
