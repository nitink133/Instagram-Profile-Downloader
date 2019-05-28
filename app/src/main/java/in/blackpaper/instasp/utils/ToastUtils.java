package in.blackpaper.instasp.utils;

import android.content.Context;
import android.os.Build;

import es.dmoral.toasty.Toasty;

public class ToastUtils {



    public static void SuccessToast(Context context, String message){
        Toasty.success(context,message,Toasty.LENGTH_SHORT).show();
    }

    public static void ErrorToast(Context context,String message){
        Toasty.error(context,message,Toasty.LENGTH_SHORT).show();
    }
    public static void NORMAL(Context context,String message){
        Toasty.normal(context,message,Toasty.LENGTH_SHORT).show();
    }
}
