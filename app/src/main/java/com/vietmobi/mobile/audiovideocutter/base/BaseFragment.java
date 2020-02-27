package com.vietmobi.mobile.audiovideocutter.base;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by jacky on 3/5/18.
 */

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    protected T binding;
    protected CompositeDisposable compositeDisposable;
    protected boolean isDestroy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        compositeDisposable = new CompositeDisposable();
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        getBaseActivity().setTitleApp(getTitleApp());
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public BaseActivity getBaseActivity() {
        if (getActivity() instanceof BaseActivity) {
            return (BaseActivity) getActivity();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        isDestroy = true;
        if (binding != null) {
            binding.unbind();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        // DisposableManager.disposeAll();
        super.onDestroyView();
    }

    public void replaceFragment(BaseFragment fragment, Bundle mBundle) {
        FragmentTransaction fragmentTransaction = getBaseActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(getContainerId(), fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showDialogFragment(BaseDialog dialogFragment) {
        dialogFragment.show(getFragmentManager(), dialogFragment.getClass().getName());
    }

    public int getContainerId() {
        return 0;
    }

    public void onRefresh() {
    }

    public void onLoadMore() {
    }

    public void openActivity(Class<? extends Activity> pClass) {
        openActivity(pClass, null);
    }

    public void openActivity(Class<? extends Activity> pClass, boolean isFinish) {
        openActivity(pClass);
        if (isFinish) {
            getBaseActivity().finish();
        }
    }

    public void openActivity(Class<? extends Activity> pClass, Bundle bundle) {
        Intent intent = new Intent(getBaseActivity(), pClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public <K> Bundle pushBundle(K k) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(k.getClass().getName(), (Parcelable) k);
        return bundle;
    }

    public String getTitleApp() {
        return "";
    }
}