package com.storyPost.PhotoVideoDownloader.activity.dashboard;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.storyPost.PhotoVideoDownloader.activity.BaseActivity;
import com.storyPost.PhotoVideoDownloader.fragments.DownloadPostFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import com.storyPost.PhotoVideoDownloader.BuildConfig;
import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.DownloadHistoryActivity;
import com.storyPost.PhotoVideoDownloader.activity.DownloadProfileImageActivity;
import com.storyPost.PhotoVideoDownloader.activity.HowToUseActivity;
import com.storyPost.PhotoVideoDownloader.activity.introscreen.IntroScreenActivity;
import com.storyPost.PhotoVideoDownloader.activity.splash.SplashActivity;
import com.storyPost.PhotoVideoDownloader.adapter.DrawerAdapter;
import com.storyPost.PhotoVideoDownloader.data.localpojo.DrawerMenuPojo;
import com.storyPost.PhotoVideoDownloader.data.prefs.PreferencesManager;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import com.storyPost.PhotoVideoDownloader.fragments.FavouriteFragment;
import com.storyPost.PhotoVideoDownloader.fragments.FeedFragment;
import com.storyPost.PhotoVideoDownloader.fragments.ProfileFragment;
import com.storyPost.PhotoVideoDownloader.fragments.StoriesFragment;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

public class MainActivity extends BaseActivity implements
        FeedFragment.OnFeedFragmentInteractionListener,
        ProfileFragment.OnProfileFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    DataObjectRepositry dataObjectRepositry;
    ImageView changeDrawerImageVIew;
    boolean isAddAccountViewVisible = false;
    private TextView mUsername, mEmail;
    private CircleImageView mProfileImage;
    private RecyclerView drawerMenuRecyclerView;
    private Context context;
    private DrawerAdapter drawerAdapter;
    NavigationView navigationView;
    DrawerLayout drawer;
    private FrameLayout frame_container;
    String username = "", profileImage = "";
    private boolean doubleBackToExitPressedOnce = false;
    private List<DrawerMenuPojo> allLoginUserList = new ArrayList<>();
    private String user_id = "", database_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        ButterKnife.bind(this);
        getSafeIntent();
        initUI();
        onClick();
        allLoginUserList.clear();
        LiveData<List<Logins>> loggedInUsers = DataObjectRepositry.dataObjectRepositry.getAllUsers();
        loggedInUsers.observe(MainActivity.this, new Observer<List<Logins>>() {
            @Override
            public void onChanged(List<Logins> logins) {
                if (logins.size() > 0) {
                    allLoginUserList.clear();
                    for (Logins logins1 : logins) {

                        DrawerMenuPojo drawerMenuPojo1 = new DrawerMenuPojo();
                        drawerMenuPojo1.setMenuName(logins1.getUserName());
                        drawerMenuPojo1.setImage(R.drawable.ic_account);
                        allLoginUserList.add(drawerMenuPojo1);

                    }
                }
            }
        });


        if (!TextUtils.isEmpty(user_id)) {
            showLoading();
            new GetUserInfo(user_id).execute();
        } else {


            changeFragment(new StoriesFragment());
        }


    }

    public void getSafeIntent() {
        if (getIntent() != null) {
            user_id = getIntent().getStringExtra("user");
            database_id = getIntent().getStringExtra("database_id");


        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        context = MainActivity.this;
    }

    public void initUI() {

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView;
        mProfileImage = headerLayout.findViewById(R.id.mProfileImage);
        mUsername = headerLayout.findViewById(R.id.userName);
        mEmail = headerLayout.findViewById(R.id.userEmail);
        changeDrawerImageVIew = headerLayout.findViewById(R.id.changeDrawerImageVIew);

        drawerMenuRecyclerView = findViewById(R.id.drawerMenuRecyclerView);

        frame_container = findViewById(R.id.frame_container);
        drawerAdapter = new DrawerAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        drawerMenuRecyclerView.setLayoutManager(mLayoutManager);
        drawerMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        drawerMenuRecyclerView.setAdapter(drawerAdapter);

        username = PreferencesManager.getPref(GlobalConstant.USERNAME);
        profileImage = PreferencesManager.getPref(GlobalConstant.PROFILE_PIC);

        if (profileImage != null && !TextUtils.isEmpty(profileImage))
            Glide.with(this).load(profileImage).into(mProfileImage);
        if (username != null && !TextUtils.isEmpty(username))
            mUsername.setText(username);


        addItemsInDrawer();


    }


    public void onClick() {

//        mProfileImage.setOnClickListener(v -> {
//            drawer.closeDrawer(GravityCompat.START, true);
//            ProfileFragment profileFragment = new ProfileFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString(GlobalConstant.USERNAME, username);
//            profileFragment.setArguments(bundle);
//            changeFragment(profileFragment);
//        });

        drawerAdapter.setEventListener(new DrawerAdapter.EventListener() {
            @Override
            public void onItemClick(DrawerMenuPojo drawerMenuPojo) {
                toolbar.setTitle("InstaSP");


                if (!isAddAccountViewVisible) {
                    if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FEED)) {
                        toolbar.setTitle("InstaSP");
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                FeedFragment feedFragment = new FeedFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(GlobalConstant.USERNAME, username);
                                feedFragment.setArguments(bundle);
                                changeFragment(feedFragment);
                            }
                        }.start();
                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.STORIES)) {
                        toolbar.setTitle("InstaSP");
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                changeFragment(new StoriesFragment());
                            }
                        }.start();
                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.PROFILE_PICTURE)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                startActivity(new Intent(MainActivity.this, DownloadProfileImageActivity.class));
                            }
                        }.start();

                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FAVOURITES)) {
                        toolbar.setTitle("Favourite Stories");
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                changeFragment(new FavouriteFragment());
                            }
                        }.start();
                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.POST)) {

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                toolbar.setTitle("Download Post");
                                changeFragment(new DownloadPostFragment());
                            }
                        }.start();
                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.HOW_TO_USER)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                startActivity(new Intent(MainActivity.this, HowToUseActivity.class));
                            }
                        }.start();

                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.SHARE_APP)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                shareApp();
                            }
                        }.start();

//                        ToastUtils.SuccessToast(context, GlobalConstant.SHARE_APP);
                    }
                    else if (drawerMenuPojo.getMenuName().equalsIgnoreCase(GlobalConstant.DOWNLOAD_HISTORY)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                startActivity(new Intent(MainActivity.this, DownloadHistoryActivity.class));
                            }
                        }.start();

                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.MORE_APPS)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                                moreApp();
                            }
                        }.start();
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.RATE_US)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
//                        showRatingDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse(GlobalConstant.storeLink + getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(GlobalConstant.storeLink + getPackageName())));
                                }
                            }
                        }, 300);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.LOGOUT)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {


                                logoutFromInsta(PreferencesManager.getPref(GlobalConstant.USER_ID));
                            }
                        }.start();


                    } else {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        ToastUtils.ErrorToast(context, getString(R.string.some_error));
                    }
                } else {


                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    if (drawerMenuPojo.getMenuName().equalsIgnoreCase("Add account")) {

                        startActivity(new Intent(MainActivity.this, IntroScreenActivity.class));

                    } else {
                        changeAccount(drawerMenuPojo.getMenuName());
                    }

                }
            }
        });

        changeDrawerImageVIew.setOnClickListener(v -> {

            if (isAddAccountViewVisible) {
                Glide.with(this).load(R.drawable.ic_arrow_drop).into(changeDrawerImageVIew);
                isAddAccountViewVisible = false;
                addItemsInDrawer();
            } else {
                Glide.with(this).load(R.drawable.ic_arrow_up).into(changeDrawerImageVIew);
                isAddAccountViewVisible = true;
                addItemsInDrawer();

            }

        });

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onResume() {
        super.onResume();

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if (sharedPreferences.getBoolean(GlobalConstant.KEY_THEME_CHANGE, false)) {
//            sharedPreferences.edit().putBoolean(GlobalConstant.KEY_THEME_CHANGE, false).apply();
//            // https://stackoverflow.com/questions/10844112/runtimeexception-performing-pause-of-activity-that-is-not-resumed
//            // Briefly, let the activity resume properly posting the recreate call to end of the message queue
//            new Handler(Looper.getMainLooper()).post(() -> {
//                Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
//                int mPendingIntentId = 123456;
//                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity,
//                        PendingIntent.FLAG_CANCEL_CURRENT);
//                AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                System.exit(0);
//            });
//        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void addItemsInDrawer() {
        List<DrawerMenuPojo> drawerMenuPojoList = new ArrayList<>();
        if (!isAddAccountViewVisible) {
//            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
////            drawerMenuPojo.setMenuName(GlobalConstant.FEED);
////            drawerMenuPojo.setImage(R.drawable.ic_feed);
//            drawerMenuPojoList.add(drawerMenuPojo);

            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.STORIES);
            drawerMenuPojo.setImage(R.drawable.ic_story);
            drawerMenuPojoList.add(drawerMenuPojo);

            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.POST);
            drawerMenuPojo.setImage(R.drawable.ic_download);
            drawerMenuPojoList.add(drawerMenuPojo);

            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.PROFILE_PICTURE);
            drawerMenuPojo.setImage(R.drawable.ic_person);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.FAVOURITES);
            drawerMenuPojo.setImage(R.drawable.ic_fav);
            drawerMenuPojoList.add(drawerMenuPojo);


            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.DOWNLOAD_HISTORY);
            drawerMenuPojo.setImage(R.drawable.ic_history);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.HOW_TO_USER);
            drawerMenuPojo.setImage(R.drawable.ic_about);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.SHARE_APP);
            drawerMenuPojo.setImage(R.drawable.ic_share);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.MORE_APPS);
            drawerMenuPojo.setImage(R.drawable.ic_launch);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.RATE_US);
            drawerMenuPojo.setImage(R.drawable.ic_star);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.LOGOUT);
            drawerMenuPojo.setImage(R.drawable.ic_logout);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerAdapter.setMenu(drawerMenuPojoList);
        } else {
            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Add Account");
            drawerMenuPojo.setImage(R.drawable.ic_add_person);
            drawerMenuPojoList.add(drawerMenuPojo);

            drawerMenuPojoList.addAll(allLoginUserList);

            drawerAdapter.setMenu(drawerMenuPojoList);
        }
    }


    public void changeFragment(Fragment targetFragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, targetFragment, "fragment")
                .addToBackStack(null)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "\nInstaSP : Download Instagram stories, profile photo, post, photos, videos of anyone in just one click :\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via..."));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void moreApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Ashok Lathwal")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Ashok Lathwal")));
        }
    }


    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        String url = "";

        public RequestInstagramAPI(String url) {
            this.url = url + "?__a=1";
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            hideLoading();
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    IntagramProfileResponse intagramProfileResponse = new Gson().fromJson(response, IntagramProfileResponse.class);
                    if (intagramProfileResponse.getGraphql() != null && intagramProfileResponse.getGraphql().getUser() != null) {
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, intagramProfileResponse.getGraphql().getUser().getProfilePicUrl());
                        PreferencesManager.savePref(GlobalConstant.FULL_NAME, intagramProfileResponse.getGraphql().getUser().getFullName());
                        PreferencesManager.savePref(GlobalConstant.BIO, intagramProfileResponse.getGraphql().getUser().getBiography());
                        PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, intagramProfileResponse.getGraphql().getUser().getEdgeFollowedBy().getCount());
                        PreferencesManager.savePref(GlobalConstant.FOLLOWS, intagramProfileResponse.getGraphql().getUser().getEdgeFollow().getCount());
                        PreferencesManager.savePref(GlobalConstant.MEDIA, intagramProfileResponse.getGraphql().getUser().getEdgeOwnerToTimelineMedia().getCount());
                        IntagramProfileResponse.User user = intagramProfileResponse.getGraphql().getUser();
                        if (user != null && user.getEdgeOwnerToTimelineMedia() != null && user.getEdgeOwnerToTimelineMedia().getEdges() != null) {
                            List<IntagramProfileResponse.Edge> edgeList = user.getEdgeOwnerToTimelineMedia().getEdges();


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if (profileImage != null && !TextUtils.isEmpty(profileImage))
                                        Glide.with(MainActivity.this).load(profileImage).into(mProfileImage);
                                    if (username != null && !TextUtils.isEmpty(username))
                                        mUsername.setText(username);
//                                    StoriesFragment storiesFragment = new StoriesFragment();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelableArrayList("feed_list", (ArrayList<? extends Parcelable>) edgeList);
//                                    storiesFragment.setArguments(bundle);
                                    try {
                                        changeFragment(new StoriesFragment());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(context, getString(R.string.some_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


    private class GetUserInfo extends AsyncTask<Void, String, String> {

        String url = "https://i.instagram.com/api/v1/users/";
        String userid = "";
        String end_url = "/info/";
        String final_url = "";

        public GetUserInfo(String userid) {
            this.userid = userid;
            this.final_url = url + userid + end_url;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(final_url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            hideLoading();
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("response", jsonObject.toString());
                    String _username, full_name, profile_pic, bio, followed_by, follows, media;

                    if (jsonObject.has("user")) {
                        JSONObject usersObject = jsonObject.getJSONObject("user");
                        _username = usersObject.getString("username");
                        full_name = usersObject.getString("full_name");
                        profile_pic = usersObject.getString("profile_pic_url");
                        followed_by = usersObject.getString("follower_count");
                        follows = usersObject.getString("following_count");
                        media = usersObject.getString("media_count");
                        bio = usersObject.getString("biography");

                        PreferencesManager.savePref(GlobalConstant.USERNAME, _username);
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, profile_pic);
                        PreferencesManager.savePref(GlobalConstant.FULL_NAME, full_name);
                        PreferencesManager.savePref(GlobalConstant.BIO, bio);
                        PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, followed_by);
                        PreferencesManager.savePref(GlobalConstant.FOLLOWS, follows);
                        PreferencesManager.savePref(GlobalConstant.MEDIA, media);

                        Integer existinguserId = -1;
                        existinguserId = DataObjectRepositry.dataObjectRepositry.checkIfUserExist(_username);
                        if (existinguserId == null && existinguserId == -1) {

                            if (database_id != null && !database_id.isEmpty()) {
                                DataObjectRepositry.dataObjectRepositry.updateUserNameInfo(Integer.parseInt(database_id),
                                        _username, profile_pic);
                            }


                        } else {
                            DataObjectRepositry.dataObjectRepositry.deleteExistingUser(existinguserId);
                            if (database_id != null && !database_id.isEmpty()) {
                                DataObjectRepositry.dataObjectRepositry.updateUserNameInfo(Integer.parseInt(database_id),
                                        _username, profile_pic);
                            }
                        }


                        DrawerMenuPojo drawerMenuPojo1 = new DrawerMenuPojo();
                        drawerMenuPojo1.setMenuName(_username);
                        drawerMenuPojo1.setImage(R.drawable.ic_account);
                        allLoginUserList.add(drawerMenuPojo1);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                if (profile_pic != null && !TextUtils.isEmpty(profile_pic))
                                    Glide.with(MainActivity.this).load(profile_pic).into(mProfileImage);
                                if (_username != null && !TextUtils.isEmpty(_username))
                                    mUsername.setText(_username);
//                                    StoriesFragment storiesFragment = new StoriesFragment();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelableArrayList("feed_list", (ArrayList<? extends Parcelable>) edgeList);
//                                    storiesFragment.setArguments(bundle);
                                try {
                                    changeFragment(new StoriesFragment());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(context, getString(R.string.some_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void changeAccount(String usernameSelecte) {
        LiveData<List<Logins>> loggedInUsers = DataObjectRepositry.dataObjectRepositry.getAllUsers();
        loggedInUsers.observe(this, new Observer<List<Logins>>() {
            @Override
            public void onChanged(List<Logins> logins) {
                if (logins.size() > 0) {
                    for (Logins logins1 : logins) {

                        if (logins1.getUserName().equals(usernameSelecte)) {
                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);

                            if (BuildConfig.DEBUG)
                                Log.d(username + "userId", InstaUtils.getUserId());
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getUserId(), "userid");
                            ZoomstaUtil.setStringPreference(MainActivity.this, usernameSelecte, "username");
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getCooki(), "cooki");
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getCsrf(), "csrf");
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getSession_id(), "sessionid");

                            InstaUtils.setCookies(logins1.getCooki());
                            InstaUtils.setCsrf(logins1.getCsrf(), logins1.getCooki());
                            InstaUtils.setSessionId(logins1.getSession_id());


                            PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, logins1.getProfilePic());
                            PreferencesManager.savePref(GlobalConstant.USERNAME, logins1.getUserName());
                            PreferencesManager.savePref(GlobalConstant.USER_ID, logins1.getUserId());
                            PreferencesManager.savePref(GlobalConstant.TOKEN, logins1.getSession_id());

                            PreferencesManager.savePref(GlobalConstant.FULL_NAME, logins1.getFullName());
                            PreferencesManager.savePref(GlobalConstant.BIO, logins1.getBio());
                            PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, logins1.getFollowedBy());
                            PreferencesManager.savePref(GlobalConstant.FOLLOWS, logins1.getFollows());
                            PreferencesManager.savePref(GlobalConstant.MEDIA, logins1.getMedia());


                            startActivity(intent);

                        }
                    }
                }
            }
        });
    }

    public void logoutFromInsta(String user_id) {
        LiveData<List<Logins>> loggedInUsers = DataObjectRepositry.dataObjectRepositry.getAllUsers();
        loggedInUsers.observe(this, new Observer<List<Logins>>() {
            @Override
            public void onChanged(List<Logins> logins) {
                if (logins.size() > 0) {
                    for (Logins logins1 : logins) {

                        if (logins1.getUserId().equals(user_id)) {

                            dataObjectRepositry.deleteExistingUser(logins1.getId());
                            ToastUtils.SuccessToast(context, GlobalConstant.LOGOUT);
                            PreferencesManager.clear();
                            ZoomstaUtil.clearPref(context);
                            InstaUtils.setSessionId(null);
                            InstaUtils.setCsrf(null, null);
                            InstaUtils.setCookies(null);
                            Intent intent = new Intent(MainActivity.this, IntroScreenActivity.class);
                            PreferencesManager.savePref("isLogin", false);
                            intent.putExtra("isLogOut", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;


                        }
                    }
                }
            }
        });
    }


}

