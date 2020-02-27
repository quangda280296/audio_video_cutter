package com.vietmobi.mobile.audiovideocutter.ui.screen.loading;

import android.view.View;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseDialog;
import com.vietmobi.mobile.audiovideocutter.databinding.FragmentLoadingDialogBinding;

public class LoadingDialogFragment extends BaseDialog<FragmentLoadingDialogBinding>
        implements View.OnClickListener {

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int idLayoutRes() {
        return R.layout.fragment_loading_dialog;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getStyleDialog() {
        return 0;
    }

    @Override
    public void onClick(View v) {
    }
}