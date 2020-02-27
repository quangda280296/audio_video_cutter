package com.vietmobi.mobile.audiovideocutter.ui.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.data.model.Music;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by REYANSH on 4/8/2017.
 */

public class UtilsMusic {

    private static final String[] INTERNAL_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID,
            "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\""
    };
    private static final String[] EXTERNAL_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID,
            "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\""
    };

    public static ArrayList<Music> getSongList(Context context,
                                               boolean internal,
                                               String searchString) {
        String[] selectionArgs = null;
        String selection = null;
        if (searchString != null && searchString.length() > 0) {
            selection = "title LIKE ?";
            selectionArgs = new String[]{"%" + searchString + "%"};
        }

        ArrayList<Music> songsModels = new ArrayList<>();
        Uri CONTENT_URI;
        String[] COLUMNS;

        if (internal) {
            CONTENT_URI = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            COLUMNS = INTERNAL_COLUMNS;
        } else {
            CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            COLUMNS = EXTERNAL_COLUMNS;
        }
        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI,
                COLUMNS,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String fileType = "";
                try {
                    if (cursor.getString(6).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_RINGTONE;
                    } else if (cursor.getString(7).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_ALARM;
                    } else if (cursor.getString(8).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_NOTIFICATION;
                    } else {
                        fileType = Constants.IS_MUSIC;
                    }
                } catch (Exception e) {
                    //lets assume its ringtone.
                    fileType = Constants.IS_RINGTONE;
                }
                if (cursor.getString(5).endsWith(".mp3") ||
                        cursor.getString(5).endsWith(".wav") ||
                        cursor.getString(5).endsWith(".flac")) {
                    Music song = new Music(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(10),
                            fileType);
                    songsModels.add(song);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songsModels;
    }

    public static ArrayList<Music> getSongList(Context context, File file) {
        ArrayList<Music> songsModels = new ArrayList<>();
        Uri CONTENT_URI;
        String[] COLUMNS;
        CONTENT_URI = Uri.fromFile(file);
        COLUMNS = EXTERNAL_COLUMNS;
        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI,
                COLUMNS,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String fileType = "";
                try {
                    if (cursor.getString(6).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_RINGTONE;
                    } else if (cursor.getString(7).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_ALARM;
                    } else if (cursor.getString(8).equalsIgnoreCase("1")) {
                        fileType = Constants.IS_NOTIFICATION;
                    } else {
                        fileType = Constants.IS_MUSIC;
                    }
                } catch (Exception e) {
                    //lets assume its ringtone.
                    fileType = Constants.IS_RINGTONE;
                }
                if (cursor.getString(5).endsWith(".mp3") ||
                        cursor.getString(5).endsWith(".wav") ||
                        cursor.getString(5).endsWith(".flac")) {
                    Music song = new Music(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(10),
                            fileType);
                    songsModels.add(song);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songsModels;
    }

    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }


    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

    /*public static void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_art)
                .showImageForEmptyUri(R.drawable.default_art)
                .showImageOnFail(R.drawable.default_art)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(500))
                .handler(new Handler()).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new WeakMemoryCache()).defaultDisplayImageOptions(options).memoryCacheSizePercentage(13).build();
        ImageLoader.getInstance().init(config);
    }*/

    public static int getDimensionInPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(0, dp, context.getResources().getDisplayMetrics());
    }


    /*public static ArrayList<ContactsModel> getContacts(Context context, String searchQuery) {

        String selection = "(DISPLAY_NAME LIKE \"%" + searchQuery + "%\")";

        ArrayList<ContactsModel> contactsModels = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.CUSTOM_RINGTONE,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.LAST_TIME_CONTACTED,
                        ContactsContract.Contacts.STARRED,
                        ContactsContract.Contacts.TIMES_CONTACTED},
                selection,
                null,
                "STARRED DESC, " +
                        "TIMES_CONTACTED DESC, " +
                        "LAST_TIME_CONTACTED DESC, " +
                        "DISPLAY_NAME ASC");


        if (cursor != null && cursor.moveToFirst()) {
            do {
                ContactsModel contactsModel = new ContactsModel(cursor.getString(2),
                        cursor.getString(0));
                contactsModels.add(contactsModel);
            } while (cursor.moveToNext());
        }

        return contactsModels;
    }*/


    public static int getMatColor(Context context) {
        int returnColor;
        {
            TypedArray colors = context.getResources().obtainTypedArray(R.array.mdcolor_500);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

}
