package in.blackpaper.instasp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import in.blackpaper.instasp.R;


public class DialogUitls {

    public static void infoPopup(Context context, String message, String buttonName) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView btnTxt = (TextView) dialog.findViewById(R.id.txtOK);
        TextView txt = (TextView) dialog.findViewById(R.id.txt);

        txt.setText(message);
        btnTxt.setText(buttonName);
        btnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        dialog.show();
    }

    public static void infoPopupForCloseActivity(final Activity activity, String message, String buttonName) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView btnTxt = (TextView) dialog.findViewById(R.id.txtOK);
        TextView txt = (TextView) dialog.findViewById(R.id.txt);

        txt.setText(message);
        btnTxt.setText(buttonName);
        btnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });
        dialog.show();
    }

    public static Dialog infoPopupWithListner(Context context, String message, String buttonName, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView btnTxt = (TextView) dialog.findViewById(R.id.txtOK);
        TextView txt = (TextView) dialog.findViewById(R.id.txt);

        txt.setText(message);
        btnTxt.setText(buttonName);
        if (listener != null) {
            btnTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    listener.onClick(view);
                }
            });
        }

        dialog.show();

        return dialog;
    }


    public interface SelectListner {
        public void onSelect(int pos, String value);
    }
}
