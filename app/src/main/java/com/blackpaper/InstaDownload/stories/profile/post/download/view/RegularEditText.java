package com.blackpaper.InstaDownload.stories.profile.post.download.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.blackpaper.InstaDownload.stories.profile.post.download.GlobalConstant;



/**
 * Created by nitin on 19/05/19.
 */
@SuppressLint("AppCompatCustomView")
public class RegularEditText extends EditText {

    public RegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegularEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                GlobalConstant.REGULAR_FONT);
        setTypeface(tf);
    }
}
