package com.storyPost.PhotoVideoDownloader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.storyPost.PhotoVideoDownloader.BuildConfig;
import com.storyPost.PhotoVideoDownloader.models.UserObject;

/**
 * Created by tirgei on 10/31/17.
 */

public class ZoomstaUtil {
    public static final String PREFS_NAME = "Zoomsta";

    public static void clearPref(Context c) {
        c.getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
    }

    public static boolean setStringPreference(Context c, String value, String key) {
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getStringPreference(Context c, String key) {
        return c.getSharedPreferences(PREFS_NAME, 0).getString(key, BuildConfig.VERSION_NAME);
    }

    public static boolean setIntegerPreference(Context c, int value, String key) {
        SharedPreferences.Editor editor = c.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static Integer getIntegerPreference(Context c, String key) {
        return c.getSharedPreferences(PREFS_NAME, 0).getInt(key, 0);
    }

    public static Boolean setBooleanPreference(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static Boolean getBooleanPreference(Context context, String key) {
        return context.getSharedPreferences(PREFS_NAME, 0).getBoolean(key, false);
    }

    public static void showToast(Activity context, String message, int status) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static void saveArrayList(Context context, List<String> favedUsers) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();

        Gson gson = new Gson();
        String jsonUsers = gson.toJson(favedUsers);

        editor.putString("favedUsers", jsonUsers);

        editor.commit();
    }

    public static ArrayList<String> getUsers(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        List<String> users;

        if (settings.contains("favedUsers")) {
            String jsonUsers = settings.getString("favedUsers", null);
            Gson gson = new Gson();
            String[] userItems = gson.fromJson(jsonUsers, String[].class);

            users = Arrays.asList(userItems);
            users = new ArrayList<>(users);

            if (!users.isEmpty())
                return (ArrayList<String>) users;
            else
                return null;


        } else
            return null;

    }


    public static void addToFav(Context context, List<UserObject> cart_data) {
        android.content.SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        android.content.SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        List<UserObject> textList = cart_data;
        String jsonText = gson.toJson(textList);
        prefsEditor.putString("addToFav", jsonText);
        prefsEditor.commit();
    }

    public static void appendExistingFavList(Context context, UserObject favUserModel) {
        List<UserObject> favUserModelList = getFav(context);
        favUserModelList.add(favUserModel);
        addToFav(context, new ArrayList<>());
        addToFav(context, favUserModelList);

    }


    // get Cart Data
    public static List<UserObject> getFav(Context context) {
        android.content.SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String jsonText = mPrefs.getString("addToFav", null);
        UserObject[] text = gson.fromJson(jsonText, UserObject[].class);

        List<UserObject> getCarts = new ArrayList<>();
        if (text != null) {
            for (int i = 0; i < text.length; i++) {
                getCarts.add(text[i]);
            }
        }
        return getCarts;
    }

    // get Cart Data
    public static void removeFav(Context context, UserObject favUserModel) {
        android.content.SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String jsonText = mPrefs.getString("addToFav", null);
        UserObject[] text = gson.fromJson(jsonText, UserObject[].class);


        List<UserObject> getCarts = new ArrayList<>();
        if (text != null) {
            for (int i = 0; i < text.length; i++) {
                if (!text[i].getUserId().equalsIgnoreCase(favUserModel.getUserId()))
                    getCarts.add(text[i]);
            }
        }

        addToFav(context, new ArrayList<>());
        addToFav(context, getCarts);
    }


    public static void addFaveUser(Context context, String user) {
        List<String> users = getUsers(context);
        if (users == null)
            users = new ArrayList<>();

        users.add(user);
        saveArrayList(context, users);
    }

    public static void removeFaveUser(Context context, String user) {
        List<String> users = getUsers(context);

        users.remove(user);
        saveArrayList(context, users);
    }

    public static boolean containsUser(Context context, String userId) {
        List<String> users = getUsers(context);

        if (users != null && users.contains(userId))
            return true;
        else
            return false;
    }

    public static String createImageFromBitmap(Context context, Bitmap bitmap) {
        String fileName = "myImage";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
