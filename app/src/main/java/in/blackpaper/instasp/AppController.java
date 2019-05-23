package in.blackpaper.instasp;


import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

import org.acra.ACRA;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.acra.sender.ReportSenderFactory;

import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.helper.report.AcraReportSenderFactory;
import in.blackpaper.instasp.helper.report.ErrorActivity;
import in.blackpaper.instasp.helper.report.UserAction;


public class AppController extends MultiDexApplication {
    @SuppressWarnings("unchecked")
    private static final Class<? extends ReportSenderFactory>[]
            reportSenderFactoryClasses = new Class[]{AcraReportSenderFactory.class};

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        PreferencesManager.init(this);

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
