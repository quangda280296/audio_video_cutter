package com.vietmobi.mobile.audiovideocutter.ui.widgets.imageview;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class ImageSquare extends AppCompatImageView {

    public ImageSquare(Context context) {
        super(context);
    }

    public ImageSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageSquare(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
