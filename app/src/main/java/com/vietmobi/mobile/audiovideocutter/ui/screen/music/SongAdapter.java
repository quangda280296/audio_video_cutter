package com.vietmobi.mobile.audiovideocutter.ui.screen.music;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.callback.OnRecyclerViewItemClick;
import com.vietmobi.mobile.audiovideocutter.data.model.Song;
import com.vietmobi.mobile.audiovideocutter.ui.utils.EmptyUtil;
import com.vietmobi.mobile.audiovideocutter.ui.utils.SongLoader;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;
    protected OnRecyclerViewItemClick<Song> onRecyclerViewItemClickListener;
    private Context context;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClick<Song> mOnRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public void setDataList(Context context, List<Song> songList) {
        if (songList == null) {
            this.songList = new ArrayList<>();
        } else {
            this.songList = songList;
            notifyDataSetChanged();
        }
        this.context = context;
    }

    public void clearData() {
        if (songList == null) {
            return;
        }
        songList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        if (holder != null && songList != null && songList.get(position) != null) {
            holder.titleSong.setText(EmptyUtil.isNotEmpty(songList.get(position).title) ?
                    songList.get(position).title : "");
            holder.artistName.setText(EmptyUtil.isNotEmpty(songList.get(position).artistName) ?
                    songList.get(position).artistName : "");
            Glide.with(holder.itemView.getContext())
                    .load(Utils.getAlbumArtUri(songList.get(position).albumId))
                    .placeholder(R.drawable.ic_music)
                    .into(holder.imageSong);
            holder.itemView.setOnClickListener(v -> {
                if (onRecyclerViewItemClickListener != null) {
                    onRecyclerViewItemClickListener.onItemClick(songList.get(position), position);
                }
            });

            holder.imagePopupMenu.setOnClickListener(v -> {
                initPopupMenu(holder, position, v);
            });

        }
    }

    private void initPopupMenu(SongViewHolder holder, int position, View v) {
        final PopupMenu menu = new PopupMenu(holder.itemView.getContext(), v);
        menu.setOnMenuItemClickListener(item -> {
            Uri uri = Uri.fromFile(new File(songList.get(position).mPath));
            switch (item.getItemId()) {
                case R.id.popup_song_set_default_ringtone:
                    setRingtones(uri, RingtoneManager.TYPE_RINGTONE,
                            R.string.alert_title_success,
                            R.string.what_to_do_with_ringtone);
                    break;
                case R.id.popup_song_set_default_alarm:
                    setRingtones(uri, RingtoneManager.TYPE_ALARM,
                            R.string.alert_title_success,
                            R.string.set_default_alarm);
                    break;
                case R.id.popup_song_set_default_notification:
                    setRingtones(uri, RingtoneManager.TYPE_NOTIFICATION,
                            R.string.alert_title_success,
                            R.string.set_default_notification);
                    break;
                case R.id.popup_song_delete:
                    long[] deleteIds = {songList.get(position).id};
                    new MaterialDialog.Builder(context)
                            .title("Delete song?")
                            .content("Are you sure you want to delete "
                                    + songList.get(position).title
                                    + " ?")
                            .positiveText("Delete")
                            .negativeText("Cancel")
                            .onPositive((dialog, which) -> {
                                SongLoader.deleteTracks(holder.itemView.getContext(), deleteIds);
                                if (songList.get(position) == null) {
                                    return;
                                }
                                songList.remove(songList.get(position));
                                notifyDataSetChanged();
                            })
                            .onNegative((dialog, which) -> dialog.dismiss())
                            .show();
                    break;
            }
            return false;
        });
        menu.inflate(R.menu.popup_song);
        menu.show();
    }

    private void setRingtones(Uri uri, int type, int title, int message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(R.string.alert_yes_button,
                                (dialog, whichButton) -> {
                                    switch (type) {
                                        case RingtoneManager.TYPE_RINGTONE:
                                            RingtoneManager.setActualDefaultRingtoneUri(
                                                    context,
                                                    RingtoneManager.TYPE_RINGTONE, uri);
                                            break;
                                        case RingtoneManager.TYPE_NOTIFICATION:
                                            RingtoneManager.setActualDefaultRingtoneUri(
                                                    context,
                                                    RingtoneManager.TYPE_NOTIFICATION, uri);
                                            break;
                                        case RingtoneManager.TYPE_ALARM:
                                            RingtoneManager.setActualDefaultRingtoneUri(context,
                                                    RingtoneManager.TYPE_ALARM, uri);
                                            Settings.System.putString(context.getContentResolver(), Settings.System.ALARM_ALERT,
                                                    uri.getPath());
                                            break;
                                    }
                                    Toast.makeText(context, "Success !", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                })
                        .setNegativeButton(R.string.alert_no_button,
                                (dialog, whichButton) -> dialog.dismiss())
                        .setCancelable(false)
                        .show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("Permission")
                        .setMessage("You have system write settings permission now !")
                        .setPositiveButton(R.string.alert_yes_button,
                                (dialog, whichButton) -> {
                                    Intent intent = new Intent(Settings
                                            .ACTION_MANAGE_WRITE_SETTINGS);
                                    context.startActivity(intent);
                                    dialog.dismiss();
                                })
                        .setNegativeButton(
                                R.string.alert_no_button,
                                (dialog, whichButton) -> dialog.dismiss())
                        .setCancelable(false)
                        .show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageSong, imagePopupMenu;
        private TextView titleSong, artistName;

        public SongViewHolder(View itemView) {
            super(itemView);
            imageSong = itemView.findViewById(R.id.image_music);
            imagePopupMenu = itemView.findViewById(R.id.popup_menu);
            titleSong = itemView.findViewById(R.id.music_name);
            artistName = itemView.findViewById(R.id.music_author);
        }
    }
}
