package com.storyPost.PhotoVideoDownloader.dialog;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.DialogFragment;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.listener.AuthenticationListener;


public class AuthenticationDialog  extends DialogFragment {

    public static String TAG = "FullScreenDialog";

    private String request_url;
    private String redirect_url;
    private AuthenticationListener listener;

    public AuthenticationDialog(AuthenticationListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

        this.redirect_url = getContext().getResources().getString(R.string.redirect_url);
        this.request_url = getContext().getResources().getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                getContext().getResources().getString(R.string.insta_client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.item_auth_dialog, container, false);


        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(redirect_url)) {
                    AuthenticationDialog.this.dismiss();
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("access_token=")) {
                    Uri uri = Uri.EMPTY.parse(url);
                    String access_token = uri.getEncodedFragment();
                    access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                    Log.e("access_token", access_token);
                    listener.onCodeReceived(access_token);
                    dismiss();
                } else if (url.contains("?error")) {
                    Log.e("access_token", "getting error fetching access token");
                    dismiss();
                }
            }
        };


        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


}



