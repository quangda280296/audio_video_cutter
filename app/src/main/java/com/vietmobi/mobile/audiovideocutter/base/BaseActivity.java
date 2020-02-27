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
import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        initView();
        initData();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    protected T getBinding() {
        return binding;
    }

    public void openActivity(Class<? extends Activity> pClass) {
        openActivity(pClass, null);
    }

    public void openActivity(Class<? extends Activity> pClass, boolean isFinish) {
        openActivity(pClass);
        if (isFinish) {
            finish();
        }
    }

    public void openActivity(Class<? extends Activity> pClass, Bundle bundle) {
        Intent intent = new Intent(this, pClass);
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

    public void showDialogFragment(BaseDialog dialogFragment) {
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getName());
    }

    public void replaceFragment(BaseFragment fragment, Bundle bundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (bundle != null && fragment != null) fragment.setArguments(bundle);
        fragmentTransaction.replace(getContainerId(), fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTitleApp(fragment.getTitleApp());
    }

    public void replaceFragment(BaseFragment fragment) {
        replaceFragment(fragment, null);
    }

    public void setTitleApp(String title) {
    }

    public int getContainerId() {
        return 0;
    }

    public void onRefresh() {
        if (getCurrentBaseFragment() != null) {
            getCurrentBaseFragment().onRefresh();
        }
    }

    public void onLoadMore() {
        if (getCurrentBaseFragment() != null) {
            getCurrentBaseFragment().onLoadMore();
        }
    }

    public BaseFragment getCurrentBaseFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getContainerId());
        if (fragment instanceof BaseFragment) {
            return (BaseFragment) fragment;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
