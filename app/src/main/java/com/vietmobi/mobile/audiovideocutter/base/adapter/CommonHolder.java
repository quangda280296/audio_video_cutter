package com.vietmobi.mobile.audiovideocutter.base.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class CommonHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public V binding;

    public CommonHolder(V binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public V getBinding() {
        return binding;
    }

}