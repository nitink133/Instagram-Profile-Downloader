package com.storyPost.PhotoVideoDownloader.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.storyPost.PhotoVideoDownloader.GlobalConstant;

public class SemiBoldTextView extends TextView {
    public SemiBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SemiBoldTextView(Context context) {
        super(context);

        init();
    }

    private void init() {

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                GlobalConstant.SEMI_BOLD_COLOR);

        setTypeface(tf);

    }

}
