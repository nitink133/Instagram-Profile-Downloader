package com.InstaDownload.stories.profile.post.download.utils;

import android.content.Context;
import android.net.Uri.Builder;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.cookie.SM;
import cz.msebera.android.httpclient.protocol.HTTP;
import com.InstaDownload.stories.profile.post.download.BuildConfig;
import com.InstaDownload.stories.profile.post.download.models.StoryModel;
import com.InstaDownload.stories.profile.post.download.models.UserObject;

/**
 * Created by tirgei on 10/31/17.
 */

public class InstaUtils {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static String cookies, user_id, session_id, csrf;
    private static ArrayList<String> stories = new ArrayList<>();
    private static ArrayList<StoryModel> getStories = new ArrayList<>();
    public static HashMap<String, String> vids = new HashMap<>();
    private static final String igAuth = "https://www.instagram.com/accounts/login/ajax/";
    private static final String ig = "https://www.instagram.com/";
    private static final String storiesFeed = "https://i.instagram.com/api/v1/feed/reels_tray/";

    public static String login(String username, String pass) throws Exception {
        getInitCookies(ig);

        URLConnection connection = new URL(igAuth).openConnection();
        if(connection instanceof HttpURLConnection){
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod(HttpPost.METHOD_NAME);
            httpURLConnection = addPostHeaders(httpURLConnection);

            String query = new Builder().appendQueryParameter("username", username).appendQueryParameter("password", pass).build().getEncodedQuery();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, HTTP.UTF_8));
            bufferedWriter.write(query);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode() != HttpStatus.SC_OK){
                return "bad_request";
            }

            extractAndSetCookies(httpURLConnection);
            JSONObject jsonObject = new JSONObject(buildResultString(httpURLConnection));
            if(jsonObject.get("user").toString().isEmpty()){
                return BuildConfig.VERSION_NAME;
            }

            return jsonObject.get("authenticated").toString();
        }

        throw new IOException("Url is not valid");
    }

    public static List<UserObject> usersList(Context context){

        URL url = null;
        try {
            url = new URL(storiesFeed);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        try {
            httpURLConnection.setRequestMethod(HttpGet.METHOD_NAME);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        httpURLConnection.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpURLConnection.setRequestProperty("x-ig-capabilities", "3w==");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
        httpURLConnection.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
        httpURLConnection.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
        httpURLConnection.setRequestProperty("authority", "i.instagram.com/");

        if (getUserId() == null) {
            setUserId(ZoomstaUtil.getStringPreference(context, "userid"));
        }
        if (getSessionid() == null) {
            setSessionId(ZoomstaUtil.getStringPreference(context, "sessionid"));
        }
        httpURLConnection.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
        try {
            httpURLConnection.getResponseCode();
        } catch (IOException e22) {
            e22.printStackTrace();
        }
        String result = null;
        try {
            result = buildResultString(httpURLConnection);
        } catch (Exception e4) {
            e4.printStackTrace();
        }

        List<UserObject> userListResp = new ArrayList();
        try {
            JSONArray array = new JSONObject(result).getJSONArray("tray");
            for (int i = 0; i < array.length(); i++) {
                JSONObject userObj = array.getJSONObject(i).getJSONObject("user");
                UserObject object = new UserObject();
                object.setImage(userObj.get("profile_pic_url").toString());
                object.setRealName(userObj.get("full_name").toString());
                object.setUserName(userObj.get("username").toString());
                object.setUserId(userObj.get("pk").toString());
                object.setFaved(ZoomstaUtil.containsUser(context, userObj.getString("pk")));
                Log.d("" + userObj.get("username").toString(), "" + ZoomstaUtil.containsUser(context, userObj.getString("pk")));
                userListResp.add(object);
            }
        } catch (Exception e42) {
            System.out.println(e42);
        }
        return userListResp;
    }

    public static List<UserObject> favesList(Context context){

        URL url = null;
        try {
            url = new URL(storiesFeed);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        try {
            httpURLConnection.setRequestMethod(HttpGet.METHOD_NAME);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        httpURLConnection.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpURLConnection.setRequestProperty("x-ig-capabilities", "3w==");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
        httpURLConnection.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
        httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
        httpURLConnection.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
        httpURLConnection.setRequestProperty("authority", "i.instagram.com/");

        if (getUserId() == null) {
            setUserId(ZoomstaUtil.getStringPreference(context, "userid"));
        }
        if (getSessionid() == null) {
            setSessionId(ZoomstaUtil.getStringPreference(context, "sessionid"));
        }
        httpURLConnection.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
        try {
            httpURLConnection.getResponseCode();
        } catch (IOException e22) {
            e22.printStackTrace();
        }
        String result = null;
        try {
            result = buildResultString(httpURLConnection);
        } catch (Exception e4) {
            e4.printStackTrace();
        }

        List<UserObject> userListResp = new ArrayList();
        try {
            JSONArray array = new JSONObject(result).getJSONArray("tray");
            for (int i = 0; i < array.length(); i++) {
                JSONObject userObj = array.getJSONObject(i).getJSONObject("user");
                UserObject object = new UserObject();
                object.setImage(userObj.get("profile_pic_url").toString());
                object.setRealName(userObj.get("full_name").toString());
                object.setUserName(userObj.get("username").toString());
                object.setUserId(userObj.get("pk").toString());
                object.setFaved(ZoomstaUtil.containsUser(context, userObj.getString("pk")));
                if(ZoomstaUtil.containsUser(context, userObj.getString("pk")))
                    userListResp.add(object);
            }
        } catch (Exception e42) {
            System.out.println(e42);
        }
        return userListResp;
    }


    public static List<UserObject> searchUser(Context context, String input_username) {
        URL urlString = null;
        try {
            urlString = new URL("https://i.instagram.com/api/v1/users/search?q=" + input_username);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection urlConn = null;
        try {
            urlConn = urlString.openConnection();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        HttpURLConnection httpConn = (HttpURLConnection) urlConn;
        try {
            httpConn.setRequestMethod(HttpGet.METHOD_NAME);
        } catch (ProtocolException e3) {
            e3.printStackTrace();
        }
        httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpConn.setRequestProperty("x-ig-capabilities", "3w==");
        httpConn.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
        httpConn.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
        httpConn.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
        httpConn.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
        httpConn.setRequestProperty("authority", "i.instagram.com/");
        httpConn.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
        try {
            httpConn.getResponseCode();
        } catch (IOException e22) {
            e22.printStackTrace();
        }
        String result = null;
        try {
            result = buildResultString(httpConn);
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        List<UserObject> userListResp = new ArrayList();
        try {
            JSONArray array = new JSONObject(result).getJSONArray("users");
            for (int i = 0; i < array.length(); i++) {
                boolean isPrivate = array.getJSONObject(i).getBoolean("is_private");
                boolean isFollowing = array.getJSONObject(i).getJSONObject("friendship_status").getBoolean("following");
                if (!isPrivate || (isPrivate && isFollowing)) {
                    UserObject object = new UserObject();
                    object.setImage(array.getJSONObject(i).get("profile_pic_url").toString());
                    object.setRealName(array.getJSONObject(i).get("full_name").toString());
                    object.setUserName(array.getJSONObject(i).get("username").toString());
                    object.setUserId(array.getJSONObject(i).get("pk").toString());
                    object.setFaved(ZoomstaUtil.containsUser(context, array.getJSONObject(i).get("pk").toString()));
                    userListResp.add(object);

                    Log.d("searched", object.getUserName());
                }
            }
        } catch (Exception e42) {
            System.out.println(e42);
        }
        return userListResp;
    }

    public static ArrayList<String> stories(String userId, Context context) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL("https://i.instagram.com/api/v1/feed/user/" + userId + "/reel_media/").openConnection();
            httpConn.setRequestMethod(HttpGet.METHOD_NAME);
            httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
            httpConn.setRequestProperty("x-ig-capabilities", "3w==");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
            httpConn.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
            httpConn.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
            httpConn.setRequestProperty("authority", "i.instagram.com/");
            if (getUserId() == null) {
                setUserId(ZoomstaUtil.getStringPreference(context, "userid"));
            }
            if (getSessionid() == null) {
                setSessionId(ZoomstaUtil.getStringPreference(context, "sessionid"));
            }
            httpConn.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
            int responseCode = httpConn.getResponseCode();
            String result = buildResultString(httpConn);
            List<String> imageList = new ArrayList();
            List<String> videoList = new ArrayList();
            List<String> videoThumbs = new ArrayList();
            try {
                vids.clear();
                JSONArray array = new JSONObject(result).getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject itemObj = array.getJSONObject(i);
                    JSONArray video = itemObj.optJSONArray("video_versions");
                    if (video != null) {
                        videoList.add(video.getJSONObject(0).getString("url"));
                        JSONArray imageArray = itemObj.getJSONObject("image_versions2").getJSONArray("candidates");
                        videoThumbs.add(imageArray.getJSONObject(imageArray.length() - 1).getString("url"));
                        vids.put(imageArray.getJSONObject(imageArray.length() - 1).getString("url"), video.getJSONObject(0).getString("url"));
                        Log.d("videos" , "" + video.getJSONObject(0).getString("url"));
                    } else {
                        String url = itemObj.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url");
                        if (!url.endsWith(".jpg")) {
                            url = url + ".jpg";
                        }
                        imageList.add(url);
                    }
                }
                stories.clear();
                stories.addAll(imageList);
                stories.addAll(videoThumbs);

                /*for(int i=0; i<stories.size(); i++){
                    String model = stories.get(i);
                    Log.d("stories", "" + model);
                }*/
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e2) {
            System.out.println(e2);
        }
        return stories;
    }

    public static ArrayList<StoryModel> fetchStories(String userId, Context context) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL("https://i.instagram.com/api/v1/feed/user/" + userId + "/reel_media/").openConnection();
            httpConn.setRequestMethod(HttpGet.METHOD_NAME);
            httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
            httpConn.setRequestProperty("x-ig-capabilities", "3w==");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-GB,en-US;q=0.8,en;q=0.6");
            httpConn.setRequestProperty(HTTP.USER_AGENT, "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
            httpConn.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
            httpConn.setRequestProperty("authority", "i.instagram.com/");
            if (getUserId() == null) {
                setUserId(ZoomstaUtil.getStringPreference(context, "userid"));
            }
            if (getSessionid() == null) {
                setSessionId(ZoomstaUtil.getStringPreference(context, "sessionid"));
            }
            httpConn.setRequestProperty(SM.COOKIE, "ds_user_id=" + getUserId() + "; sessionid=" + getSessionid() + ";");
            int responseCode = httpConn.getResponseCode();
            String result = buildResultString(httpConn);
            List<StoryModel> imageList = new ArrayList();
            List<StoryModel> videoList = new ArrayList();
            List<StoryModel> videoThumbs = new ArrayList();
            try {
                vids.clear();
                JSONArray array = new JSONObject(result).getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject itemObj = array.getJSONObject(i);
                    JSONArray video = itemObj.optJSONArray("video_versions");
                    if (video != null) {
                        StoryModel model = new StoryModel();
                        model.setFilePath(video.getJSONObject(0).getString("url"));
                        //Log.d("addvid", "" + video.getJSONObject(0).getString("url") );
                        model.setFileName(null);
                        model.setType(1);
                        model.setSaved(false);
                        videoList.add(model);

                        JSONArray imageArray = itemObj.getJSONObject("image_versions2").getJSONArray("candidates");
                        StoryModel storyModel = new StoryModel();
                        model.setFilePath(imageArray.getJSONObject(imageArray.length() - 1).getString("url"));
                        storyModel.setFileName(null);
                        storyModel.setType(1);
                        storyModel.setSaved(false);
                        videoThumbs.add(storyModel);
                        vids.put(imageArray.getJSONObject(imageArray.length() - 1).getString("url"), video.getJSONObject(0).getString("url"));
                    } else {
                        String url = itemObj.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url");
                        if (!url.endsWith(".jpg")) {
                            url = url.split(".jpg")[0] + ".jpg";
                        }
                        StoryModel imageStories = new StoryModel();
                        imageStories.setFilePath(url);
                        imageStories.setFileName(null);
                        imageStories.setSaved(false);
                        imageStories.setType(0);
                        imageList.add(imageStories);
                    }
                }
                getStories.clear();
                getStories.addAll(imageList);
                getStories.addAll(videoList);

            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e2) {
            System.out.println(e2);
        }
        return getStories;
    }

    public static String getUserId() {
        return user_id;
    }

    public static String getSessionid() {
        return session_id;
    }

    public static String buildResultString(HttpURLConnection httpconn) throws Exception {
        BufferedReader rd = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
        if ("gzip".equals(httpconn.getContentEncoding())) {
            rd = new BufferedReader(new InputStreamReader(new GZIPInputStream(httpconn.getInputStream())));
        }
        StringBuffer result = new StringBuffer();
        String str;
        while (true) {
            str = rd.readLine();
            if (str == null) {
                return result.toString();
            }
            result.append(str);
        }
    }

    public static String getInitCookies(String url) throws Exception {
        URLConnection urlConn = new URL(url).openConnection();
        if (urlConn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setRequestMethod(HttpGet.METHOD_NAME);
            httpConn.setRequestProperty(HTTP.USER_AGENT, USER_AGENT);
            httpConn.setRequestProperty(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpConn.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5");
            httpConn.connect();
            int resCode = httpConn.getResponseCode();
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuffer result = new StringBuffer();
            String str;
            while (true) {
                str = rd.readLine();
                if (str != null) {
                    result.append(str);
                } else {
                    extractAndSetCookies(httpConn);
                    return result.toString();
                }
            }
        }
        throw new IOException("URL is not an Http URL");
    }

    public static HttpURLConnection addPostHeaders(HttpURLConnection post) {
        post.setRequestProperty("Origin", "https://www.instagram.com");
        post.setRequestProperty("accept-encoding", "gzip, deflate, br");
        post.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5");
        post.setRequestProperty(HTTP.USER_AGENT, USER_AGENT);
        post.setRequestProperty("x-requested-with", "XMLHttpRequest");
        post.setRequestProperty("x-csrftoken", getCsrf());
        post.setRequestProperty("x-instagram-ajax", "1");
        post.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
        post.setRequestProperty(HttpHeaders.ACCEPT, "*/*");
        post.setRequestProperty(HttpHeaders.REFERER, "https://www.instagram.com/");
        post.setRequestProperty(SM.COOKIE, getCookies());
        post.setRequestProperty("authority", "https://www.instagram.com/");
        post.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
        return post;
    }

    public static void setCookies(String cookieString) {
        cookies = cookieString;
    }

    public static void extractAndSetCookies(HttpURLConnection httpConn) {
        StringBuilder sb = new StringBuilder();
        for (String cookie : httpConn.getHeaderFields().get("set-cookie")) {
            for (String parse : cookie.split(";")) {
                if (parse.startsWith("mid") || parse.startsWith("s_network") || parse.startsWith("csrftoken") || parse.startsWith("sessionid") || parse.startsWith("ds_user_id")) {
                    String[] pair;
                    sb.append(parse).append("; ");
                    if (parse.startsWith("sessionid")) {
                        pair = parse.split("=");
                        if (pair.length == 2) {
                            session_id = pair[1];
                        } else {
                            session_id = BuildConfig.VERSION_NAME;
                        }
                    }
                    if (parse.startsWith("ds_user_id")) {
                        pair = parse.split("=");
                        if (pair.length == 2) {
                            user_id = pair[1];
                        } else {
                            user_id = BuildConfig.VERSION_NAME;
                        }
                    }
                }
            }
        }
        String cookieString = sb.toString();
        sb.setLength(0);
        setCookies(cookieString);
        setCsrf(null, cookieString);
    }

    public static void setCsrf(String csrfString, String cookieString) {
        if (csrfString == null && cookieString == null) {
            csrf = null;
        } else if (csrfString == null) {
            Matcher matcher = Pattern.compile("csrftoken=(.*?);").matcher(cookieString);
            if (matcher.find()) {
                csrf = matcher.group(1);
            }
        } else {
            csrf = csrfString;
        }
    }

    public static void setSessionId(String sessionIdInp) {
        session_id = sessionIdInp;
    }

    public static void setUserId(String ds_user_idInp) {
        user_id = ds_user_idInp;
    }

    public static String getCsrf() {
        return csrf;
    }

    public static String getCookies() {
        return cookies;
    }

}
