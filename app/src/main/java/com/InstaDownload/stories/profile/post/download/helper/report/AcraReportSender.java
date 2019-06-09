package com.InstaDownload.stories.profile.post.download.helper.report;

import android.content.Context;

import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;

import androidx.annotation.NonNull;
import com.InstaDownload.stories.profile.post.download.R;


public class AcraReportSender implements ReportSender {

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData report) {
        ErrorActivity.reportError(context, report,
                ErrorActivity.ErrorInfo.make(UserAction.UI_ERROR,"none",
                        "App crash, UI failure", R.string.app_ui_crash));
    }
}
