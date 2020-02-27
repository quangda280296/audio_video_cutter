package jack.com.servicekeep.manager;

import android.content.Context;
import android.content.SharedPreferences;

import jack.com.servicekeep.Constant;

public enum PreferencesManager {

    INSTANCE;

    public static PreferencesManager getInstance() {
        return INSTANCE;
    }

    public String getHostAppPackageName(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().
                getSharedPreferences(Constant.PREFERENCE_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.PREFERENCE_KEY_HOST_APP_PACKAGE_NAME, "");
    }

    public void setHostAppPackageName(Context context, String packageName) {
        SharedPreferences sharedPreferences = context.getApplicationContext().
                getSharedPreferences(Constant.PREFERENCE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PREFERENCE_KEY_HOST_APP_PACKAGE_NAME, packageName);
        editor.commit();
    }

}
