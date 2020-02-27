package com.vietmobi.mobile.audiovideocutter.base.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vietmobi.mobile.audiovideocutter.base.callback.OnRecyclerViewItemClick;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRecyclerAdapter<T, V extends ViewDataBinding>
        extends RecyclerView.Adapter<CommonHolder> {

    private V binding;
    public Context context;
    protected boolean isMoreData;
    private List<T> dataList = new ArrayList<>();
    protected OnRecyclerViewItemClick<T> onRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClick<T> mOnRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
    }

    protected abstract int layoutItemId();

    @NonNull
    @Override
    public CommonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(getInflater(), layoutItemId(), parent, false);
        System.out.println("onCreateViewHolder");
        CommonHolder commonHolder = new CommonHolder(binding);
        return commonHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommonHolder holder, int position) {
        holder.setIsRecyclable(false);
        if (dataList != null && dataList.size() != 0 && dataList.get(position) != null) {
            bindData((V) holder.binding, dataList.get(position), position);
        }
        holder.itemView.setOnClickListener(v -> {
            if (onRecyclerViewItemClickListener != null) {
                int position1 = holder.getAdapterPosition();
                onRecyclerViewItemClickListener.onItemClick(dataList.get(position1), position1);
            }
        });
    }

    protected abstract void bindData(V binding, T item, int position);

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public boolean isMoreData() {
        return isMoreData;
    }

    public void setMoreData(boolean isMoreData) {
        this.isMoreData = isMoreData;
        notifyDataSetChanged();
    }

    protected LayoutInflater getInflater() {
        return LayoutInflater.from(context);
    }

    public void setDataList(List<T> items) {
        dataList.clear();
        if (items != null) {
            dataList.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public void addItemsAtFront(List<T> items) {
        if (items == null) {
            return;
        }
        dataList.addAll(dataList.size(), items);
        notifyItemRangeChanged(dataList.size(), dataList.size() + items.size());
    }

    public void addItems(List<T> items, boolean isRefresh) {
        if (items == null) {
            return;
        }

        if (isRefresh && dataList != null) dataList.clear();
        dataList.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (item == null) {
            return;
        }
        dataList.add(item);
        notifyDataSetChanged();
    }

    public void addItem(T item, int position) {
        if (item == null) {
            return;
        }
        dataList.add(position, item);
        notifyDataSetChanged();
    }

    public void deleteItem(T item) {
        if (item == null) {
            return;
        }
        dataList.remove(item);
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public T getDataItem(int position) {
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }
        return dataList.get(position);
    }
}
