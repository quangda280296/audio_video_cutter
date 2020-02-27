package com.vietmobi.mobile.audiovideocutter.ui.screen.music;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.adapter.BaseRecyclerAdapter;
import com.vietmobi.mobile.audiovideocutter.data.model.Song;
import com.vietmobi.mobile.audiovideocutter.databinding.ItemMusicBinding;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Utils;

public class MusicAdapter extends BaseRecyclerAdapter<Song, ItemMusicBinding> {

    public MusicAdapter(Context context) {
        super(context);
    }

    @Override
    protected int layoutItemId() {
        return R.layout.item_music;
    }

    @Override
    protected void bindData(ItemMusicBinding binding, Song item, int position) {
        if (item != null) {
            binding.musicName.setText(item.title);
            binding.musicAuthor.setText(item.artistName);
            Glide.with(context)
                    .load(Utils.getAlbumArtUri(item.albumId))
                    .placeholder(R.drawable.ic_music)
                    .into(binding.imageMusic);
        }
    }
}


