package com.blackpaper.InstaDownload.stories.profile.post.download.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.blackpaper.InstaDownload.stories.profile.post.download.R;


public class Utility {

    public static void setActionBarColor(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = activity.getTheme();
            theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            @ColorInt int color = typedValue.data;
            window.setStatusBarColor(color);
        }

    }

    public static void setToolbar(AppCompatActivity activity, Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(activity.getResources().getColor(R.color.white));
        activity.setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }


}

