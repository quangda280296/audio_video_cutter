package com.vietmobi.mobile.audiovideocutter.ui.utils;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class SlideBindings {

    public static final long DEFAULT_DURATION = 500L;
    public static boolean isUp;

    public static void slideUp(View view) {
        isUp = true;
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(DEFAULT_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideDown(View view) {
        isUp = false;
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(DEFAULT_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void onSlideView(View view) {
        if (isUp) {
            slideDown(view);
        } else {
            slideUp(view);
        }
    }

}
