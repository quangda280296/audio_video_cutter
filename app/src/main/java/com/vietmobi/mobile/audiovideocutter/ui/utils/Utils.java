package com.vietmobi.mobile.audiovideocutter.ui.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

public class Utils {

    private static Context context;

    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
    }

    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }
}
