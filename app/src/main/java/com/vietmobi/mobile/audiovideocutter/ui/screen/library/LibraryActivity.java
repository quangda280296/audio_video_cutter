package com.vietmobi.mobile.audiovideocutter.ui.screen.library;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityGalleryBinding;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.LibraryViewPager;
import com.vietmobi.mobile.audiovideocutter.ui.screen.library.viewpager.OnViewPagerChangeListener;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.Constants;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;

public class LibraryActivity extends BaseActivity<ActivityGalleryBinding> {

    private LibraryViewPager viewPager;
    private OnViewPagerChangeListener onViewPagerChangeListener;

    public void setOnViewPagerChangeListener(OnViewPagerChangeListener onViewPagerChangeListener) {
        this.onViewPagerChangeListener = onViewPagerChangeListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gallery;
    }

    @Override
    public void initView() {
        binding.imageBack.setOnClickListener(v -> finish());
        if (NetworkUtils.isOnline(this)){
            AdsUtils.getIntance().initAds(this, R.id.banner);
        }
    }

    @Override
    public void initData() {
        setUpViewPager();
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constants.TYPE_VIDEO)) {
                binding.viewpager.setCurrentItem(1);
            } else {
                binding.viewpager.setCurrentItem(0);
            }
        }
        viewPagerChangeListener();
    }

    private void setUpViewPager() {
        viewPager = new LibraryViewPager(getSupportFragmentManager());
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        binding.viewpager.setAdapter(viewPager);
        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(10, 10, 10, 0);
            tab.requestLayout();
        }
    }

    private void viewPagerChangeListener() {
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int currentPage = 0;

            @Override
            public void onPageScrolled(int position, float v, int i1) {
                if (currentPage != position) {
                    if (onViewPagerChangeListener != null) {
                        onViewPagerChangeListener.onPageChanged();
                    }
                }
                currentPage = position;
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
