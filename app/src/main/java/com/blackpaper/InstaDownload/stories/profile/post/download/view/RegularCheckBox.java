package com.blackpaper.InstaDownload.stories.profile.post.download.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;
import com.blackpaper.InstaDownload.stories.profile.post.download.GlobalConstant;

/**
 * Created by nitin on 19/05/19.
 */

@SuppressLint("AppCompatCustomView")
public class RegularCheckBox extends AppCompatCheckBox
{
    public RegularCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RegularCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegularCheckBox(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                GlobalConstant.REGULAR_FONT);
        setTypeface(tf);
    }

}
