package com.storyPost.PhotoVideoDownloader;


import android.content.Context;

import androidx.multidex.MultiDexApplication;


import org.acra.ACRA;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.ReportSenderFactory;

import com.crashlytics.android.Crashlytics;
import com.facebook.ads.AudienceNetworkAds;
import com.storyPost.PhotoVideoDownloader.data.prefs.PreferencesManager;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.helper.report.AcraReportSenderFactory;
import com.storyPost.PhotoVideoDownloader.helper.report.ErrorActivity;
import com.storyPost.PhotoVideoDownloader.helper.report.UserAction;
import com.google.android.gms.ads.MobileAds;
import com.storyPost.PhotoVideoDownloader.utils.AppRater;

import io.fabric.sdk.android.Fabric;


public class AppController extends MultiDexApplication {
    @SuppressWarnings("unchecked")
    private static final Class<? extends ReportSenderFactory>[]
            reportSenderFactoryClasses = new Class[]{AcraReportSenderFactory.class};

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager.init(this);
        DataObjectRepositry.init(this);

        Fabric.with(this, new Crashlytics());
        MobileAds.initialize(this, getString(R.string.admob_app_id));

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        initACRA();


    }

    private void initACRA() {
        try {
            final ACRAConfiguration acraConfig = new ConfigurationBuilder(this)
                    .setReportSenderFactoryClasses(reportSenderFactoryClasses)
                    .setBuildConfigClass(BuildConfig.class)
                    .build();
            ACRA.init(this, acraConfig);
        } catch (ACRAConfigurationException ace) {
            ace.printStackTrace();
            ErrorActivity.reportError(this,
                    ace,
                    null,
                    null,
                    ErrorActivity.ErrorInfo.make(UserAction.SOMETHING_ELSE, "none",
                            "Could not initialize ACRA crash report", R.string.app_ui_crash));
        }
    }
}
