package jack.com.servicekeep.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class InfoDevice extends RealmObject {
    public static final String PROPERTY_DIVICE_ID = "deviceID";
    @PrimaryKey
    @Required
    public String deviceID;
    public String code;
    public String version;
    public String osVersion;
    public String country;
    public String timereg;
    public String timenow;
    public boolean isApp;
    public boolean install;
    public RealmList<Admob> ads;
    public Config config;
    public long lastTimeShowAds;
    public long timeNeedShow;
    public long timeDelay;
    public long timeUpdateLoadFail;
    public boolean isResetServiceAds = false;
}
