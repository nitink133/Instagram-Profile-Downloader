package com.storyPost.PhotoVideoDownloader.activity.dashboard;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.storyPost.PhotoVideoDownloader.BuildConfig;
import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.BaseActivity;
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
import com.storyPost.PhotoVideoDownloader.fragments.DownloadIGTVFragment;
import com.storyPost.PhotoVideoDownloader.fragments.DownloadPostFragment;
import com.storyPost.PhotoVideoDownloader.fragments.FavouriteFragment;
import com.storyPost.PhotoVideoDownloader.fragments.FeedFragment;
import com.storyPost.PhotoVideoDownloader.fragments.ProfileFragment;
import com.storyPost.PhotoVideoDownloader.fragments.StoriesFragment;
import com.storyPost.PhotoVideoDownloader.models.User;
import com.storyPost.PhotoVideoDownloader.utils.AppRater;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

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

public class MainActivity extends BaseActivity implements
        FeedFragment.OnFeedFragmentInteractionListener,
        ProfileFragment.OnProfileFragmentInteractionListener, InterstitialAdListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    DataObjectRepositry dataObjectRepositry;
    ImageView changeDrawerImageVIew;
    boolean isAddAccountViewVisible = false;
    NavigationView navigationView;
    DrawerLayout drawer;
    String username = "", profileImage = "";
    private TextView mUsername, mEmail;
    private CircleImageView mProfileImage;
    private RecyclerView drawerMenuRecyclerView;
    private Context context;
    private DrawerAdapter drawerAdapter;
    private FrameLayout frame_container;
    private boolean doubleBackToExitPressedOnce = false;
    private List<DrawerMenuPojo> allLoginUserList = new ArrayList<>();
    private String user_id = "", database_id = "";
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd mInterstitialFbAd;
    private boolean isFbAdsLoading = false;
    private RewardedVideoAd mRewardedVideoAd;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AudienceNetworkAds.initialize(this);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        ButterKnife.bind(this);
        getSafeIntent();
        initUI();
        onClick();
        addToFirebase();


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

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
            toolbar.setTitle("Stories");
        }
        toolbar.setTitle("Stories");
    }

    public void getSafeIntent() {
        if (getIntent() != null) {
            user_id = getIntent().getStringExtra("user");
            database_id = getIntent().getStringExtra("database_id");


        }

    }

    public void addToFirebase() {
        // Obtain the Firebase Analytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        User user = new User();
        user.setDevice_id(FirebaseInstanceId.getInstance().getToken());
        user.setFull_name(PreferencesManager.getPref(GlobalConstant.FULL_NAME));
        user.setUser_id(PreferencesManager.getPref(GlobalConstant.USER_ID));
        user.setUsername(PreferencesManager.getPref(GlobalConstant.USERNAME));
        user.setDevice_name(android.os.Build.MODEL);

        Bundle bundle = new Bundle();
        bundle.putString(GlobalConstant.FULL_NAME, user.getFull_name());
        bundle.putString(GlobalConstant.USER_ID, user.getUser_id());
        bundle.putString(GlobalConstant.USERNAME, user.getUsername());
        bundle.putString(GlobalConstant.DEVICE_TYPE, user.getDevice_name());
        bundle.putString(GlobalConstant.DEVICE_ID, user.getDevice_id());

        //Logs an app event.
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        firebaseAnalytics.setMinimumSessionDuration(20000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(500);

        //Sets the user ID property.
        firebaseAnalytics.setUserId(String.valueOf(user.getUser_id()));

        //Sets a user property to a given value.
        firebaseAnalytics.setUserProperty(GlobalConstant.USER_ID, user.getUser_id());
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
        loadRewardedVideoAd();
        loadFullscreenAd();
        loadFbFullscreenAd();


    }


    public void onClick() {
        drawerAdapter.setEventListener(new DrawerAdapter.EventListener() {
            @Override
            public void onItemClick(DrawerMenuPojo drawerMenuPojo) {


                if (!isAddAccountViewVisible) {
                    if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FEED)) {
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
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.STORIES)) {

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                toolbar.setTitle("Stories");
                                showFullScreenAds();
                                changeFragment(new StoriesFragment());
                            }
                        }.start();
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.PROFILE_PICTURE)) {
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

                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FAVOURITES)) {
                        toolbar.setTitle("Favourite Stories");
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                showFullScreenAds();
                                changeFragment(new FavouriteFragment());
                            }
                        }.start();
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.POST)) {

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                showFullScreenAds();
                                toolbar.setTitle("Download Post");
                                changeFragment(new DownloadPostFragment());
                            }
                        }.start();
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.HOW_TO_USER)) {
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

                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.SHARE_APP)) {
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
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.IGTV_DOWNLOAD)) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        new CountDownTimer(250, 100) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                showFullScreenAds();
                                toolbar.setTitle("Download IGTV Video");
                                changeFragment(new DownloadIGTVFragment());

                            }
                        }.start();
                    } else if (drawerMenuPojo.getMenuName().equalsIgnoreCase(GlobalConstant.DOWNLOAD_HISTORY)) {
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.pause(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(this);
        }
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

    InterstitialAd interstitialAd = null;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            showRewardedVideoAd();
        }else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
//        exitDialog();
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
            drawerMenuPojo.setMenuName(GlobalConstant.IGTV_DOWNLOAD);
            drawerMenuPojo.setImage(R.drawable.ic_igtv_video);
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
            String shareMessage = "\nJust found the best app to download Instagram Stories, photos, post, IGTV videos or profile pic of your favourites! Check it out:\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via..."));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void moreApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Appkrunch")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Appkrunch")));
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

    private void loadFullscreenAd() {

        if (mInterstitialAd != null) {
            if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                mInterstitialAd.loadAd(new AdRequest.Builder()
                        .build());
            }
            return;
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e(TAG, "onAdFailedToLoad: " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                loadFbFullscreenAd();
                loadFullscreenAd();

            }
        });
    }

    private void loadFbFullscreenAd() {
        if (mInterstitialFbAd != null) {
            if (!isFbAdsLoading && !mInterstitialFbAd.isAdLoaded()) {
                mInterstitialFbAd.loadAd();
                isFbAdsLoading = true;
                Log.e(TAG, "loadFbFullscreenAd: " + "Request");
            }

            Log.e(TAG, "loadFbFullscreenAd: " + "Requesting");
            return;
        }

        mInterstitialFbAd = new com.facebook.ads.InterstitialAd(MainActivity.this, getString(R.string.facebook_interstitial));
        mInterstitialFbAd.setAdListener(this);
        mInterstitialFbAd.loadAd();
        isFbAdsLoading = true;
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {

    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        isFbAdsLoading = false;
        loadFbFullscreenAd();
        loadFullscreenAd();

    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.e(TAG, "facebookAdsonError: " + adError.getErrorMessage());
        isFbAdsLoading = false;
    }

    @Override
    public void onAdLoaded(Ad ad) {
        isFbAdsLoading = false;
        Log.e(TAG, "loadFbFullscreenAd: " + "Loaded");
        return;
    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    public void showFullScreenAds() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else if (mInterstitialFbAd.isAdLoaded()) {
            mInterstitialFbAd.show();
        } else {
            loadFbFullscreenAd();
            loadFullscreenAd();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInterstitialFbAd.destroy();
    }

    private void loadRewardedVideoAd() {


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                closeApp();

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.e(TAG, "onRewardedVideoAdFailedToLoad: " + i);
            }

            @Override
            public void onRewardedVideoCompleted() {
                closeApp();
            }
        });

        mRewardedVideoAd.loadAd(getString(R.string.rewarded_video),
                new AdRequest.Builder()
//                        .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
                        .build());
    }

    public void showRewardedVideoAd() {

        try {
            if (mRewardedVideoAd != null && mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            } else {
                closeApp();
            }
        } catch (Exception e) {
            Log.e(TAG, "showRewardedVideoAd: " + e);
        }

    }

    private void exitDialog() {

        View view = getLayoutInflater().inflate(R.layout.custom_exit_sheet, null);
        final Dialog dialog = new Dialog(this,
                R.style.MaterialDialogSheet);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        LinearLayout layoutMain = dialog.findViewById(R.id.layoutMain);
        LinearLayout layoutRate = dialog.findViewById(R.id.layoutRate);
        layoutRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

            }
        });
        LinearLayout layoutExit = dialog.findViewById(R.id.layoutExit);
        layoutExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedVideoAd();
                dialog.dismiss();
            }
        });
        setCornerRadius(layoutMain, Color.WHITE);
        Rect displayRectangle = new Rect();
        dialog.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

    }

    public void setCornerRadius(View view, int resId) {
        view.setBackgroundResource(R.drawable.bg_common_corner_radius);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(resId);
    }

    public void closeApp() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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
                                        toolbar.setTitle("Stories");
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
                    String _username = "", full_name = "", profile_pic = "", bio = "", followed_by = "", follows = "", media = "";

                    if (jsonObject.has("user")) {

                        JSONObject usersObject = jsonObject.getJSONObject("user");
                        if (usersObject.has("username"))
                            _username = usersObject.getString("username");

                        if (usersObject.has("full_name"))
                            full_name = usersObject.getString("full_name");

                        if (usersObject.has("profile_pic_url"))
                            profile_pic = usersObject.getString("profile_pic_url");

                        if (usersObject.has("follower_count"))
                            followed_by = usersObject.getString("follower_count");

                        if (usersObject.has("following_count"))
                            follows = usersObject.getString("following_count");

                        if (usersObject.has("media_count"))
                            media = usersObject.getString("media_count");

                        if (usersObject.has("biography"))
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

                        String finalProfile_pic = profile_pic;
                        String final_username = _username;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                if (finalProfile_pic != null && !TextUtils.isEmpty(finalProfile_pic))
                                    Glide.with(MainActivity.this).load(finalProfile_pic).into(mProfileImage);
                                if (final_username != null && !TextUtils.isEmpty(final_username))
                                    mUsername.setText(final_username);
//                                    StoriesFragment storiesFragment = new StoriesFragment();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putParcelableArrayList("feed_list", (ArrayList<? extends Parcelable>) edgeList);
//                                    storiesFragment.setArguments(bundle);
                                try {
                                    toolbar.setTitle("Stories");
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

}

