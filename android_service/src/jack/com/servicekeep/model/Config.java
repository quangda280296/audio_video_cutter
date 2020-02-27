package jack.com.servicekeep.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Config  extends RealmObject {
    @SerializedName("run_sv")
    public int runServer;
    @SerializedName("time_start_show_ads")
    public int timeStartShowAds;
    @SerializedName("offset_time_show_ads")
    public int offsetTimeShowAds;
    @SerializedName("time_update_load_fail")
    public int timeUpdateLoadFail;
}
