package com.storyPost.PhotoVideoDownloader.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.storyPost.PhotoVideoDownloader.R;


/**
 * Created by tirgei on 11/1/17.
 */

public class TermsDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private TextView textView;
    private Button button;

    public TermsDialog(Activity activity){
        super(activity);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.toc_layout);

        final String terms = "By downloading or using the app, these terms will automatically apply to you – you should make sure therefore that you read them carefully before using the app. \n\n The app itself, and all features related to it, still belong to Zoomsta and therefore you should not attempt to sell it as it’s provided at no cost.\n" +
                "This page is used to inform website visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.\n\n" +
                "Zoomsta app does not collect personal information about you the user such as Instagram user-name or password. The app uses cookies from Instagram.com to log you in to the app." +
                "If you choose to use the app, then you agree to the collection and use of information in relation to this policy. The Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.\n\n" +
                "Zoomsta is committed to ensuring that the app is as useful and efficient as possible. For that reason, we reserve the right to make changes to the app or to charge for its services, at any time and for any reason. We will never charge you for the app or its services without making it very clear to you exactly what you’re paying for.\n\n" +
                "The app will require the app to have an active internet connection. The connection can be Wi-Fi, or provided by your mobile network provider, but Zoomsta cannot take responsibility for the app not working at full functionality if you don’t have access to Wi-Fi, and you don’t have any of your data allowance left.\n" +
                "If you’re using the app outside of an area with Wi-Fi, you should remember that your terms of the agreement with your mobile network provider will still apply. As a result, you may be charged by your mobile provider for the cost of data for the duration of the connection while accessing the app, or other third party charges. In using the app, you’re accepting responsibility for any such charges, including roaming data charges if you use the app outside of your home territory (i.e. region or country) without turning off data roaming. If you are not the bill payer for the device on which you’re using the app, please be aware that we assume that you have received permission from the bill payer for using the app.\n\n" +
                "With respect to Zoomsta’s responsibility for your use of the app, when you’re using the app, it’s important to bear in mind that although we endeavor to ensure that it is updated and correct at all times, we do rely on third parties to access information so that we can make it available to you. \n Zoomsta accepts no liability for any loss, direct or indirect, you experience as a result of relying wholly on this functionality of the app.\n\n " +
                "Zoomsta is not a product of Instagram nor is it neither sponsored nor endorsed by Instagram and should only be used for personal use. Other users images/ videos should only be downloaded with their consent and you – the user of Zoomsta app – is solely responsible for any use of the app.\n\n" +
                "If you have any questions or suggestions about my Terms and Conditions, do not hesitate to contact me.";

       textView = findViewById(R.id.tou_text);
       textView.setText(terms);

       button = findViewById(R.id.tou_got_it);
       button.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tou_got_it:
                dismiss();
                if(!ZoomstaUtil.getBooleanPreference(activity, "skipTerms"))
                    ZoomstaUtil.setBooleanPreference(activity, "skipTerms", true);
                break;

            default:
                break;
        }
    }

}
