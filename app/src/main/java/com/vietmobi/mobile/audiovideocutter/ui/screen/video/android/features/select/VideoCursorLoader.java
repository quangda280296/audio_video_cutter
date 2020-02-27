package com.vietmobi.mobile.audiovideocutter.ui.screen.video.android.features.select;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import iknow.android.utils.callback.SimpleCallback;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/10/04 1:51 PM
 * version: 1.0
 * description:
 */
public class VideoCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>, ILoader {

  private Context mContext;
  private SimpleCallback mSimpleCallback;

  @Override
  public void load(final Context context, final SimpleCallback listener) {
    mContext = context;
    mSimpleCallback = listener;
    ((FragmentActivity)context).getSupportLoaderManager().initLoader(1, null, this);
  }

  @NonNull @Override
  public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
    return new CursorLoader(
        mContext,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        PROJECTION,
        SELECTION,
        SELECTION_ARGS,
        ORDER_BY
    );
  }

  @Override
  public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
    if (mSimpleCallback != null && cursor != null) {
      mSimpleCallback.success(cursor);
    }
  }

  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {

  }
}
