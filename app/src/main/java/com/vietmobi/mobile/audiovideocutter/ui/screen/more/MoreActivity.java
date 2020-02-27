package com.vietmobi.mobile.audiovideocutter.ui.screen.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.vietmobi.mobile.audiovideocutter.BuildConfig;
import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseActivity;
import com.vietmobi.mobile.audiovideocutter.databinding.ActivityMoreBinding;
import com.vietmobi.mobile.audiovideocutter.ui.utils.AdsUtils;
import com.vietmobi.mobile.audiovideocutter.ui.utils.NetworkUtils;

public class MoreActivity extends BaseActivity<ActivityMoreBinding>
        implements View.OnClickListener {

    private boolean isToasted;

    @Override
    public int getLayoutId() {
        return R.layout.activity_more;
    }

    @Override
    public void initView() {
        binding.imageBack.setOnClickListener(this);
        binding.rate.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.feedback.setOnClickListener(this);
        binding.moreApp.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (NetworkUtils.isOnline(this)){
            AdsUtils.getIntance().initAds(this, R.id.banner);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.rate:
                rateApp();
                break;
            case R.id.share:
                shareLinkApp();
                break;
            case R.id.feedback:
                if (isToasted) {
                    Toast.makeText(this, "Feature update in next version !", Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(() -> isToasted = true, 1200L);
                break;
            case R.id.more_app:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));
                break;
        }
    }

    private void rateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                + BuildConfig.APPLICATION_ID)));
    }

    private void shareLinkApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage = "\nPlease! Let me recommend you this application\n\n";
            shareMessage += "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

}
