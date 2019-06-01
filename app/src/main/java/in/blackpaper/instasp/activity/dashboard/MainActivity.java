package in.blackpaper.instasp.activity.dashboard;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import in.blackpaper.instasp.ApiUtils;
import in.blackpaper.instasp.BuildConfig;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.DownloadHistoryActivity;
import in.blackpaper.instasp.activity.DownloadProfileImageActivity;
import in.blackpaper.instasp.activity.HowToUseActivity;
import in.blackpaper.instasp.activity.LoginActivity;
import in.blackpaper.instasp.activity.ProfilepPictureActivity;
import in.blackpaper.instasp.activity.introscreen.IntroScreenActivity;
import in.blackpaper.instasp.activity.splash.SplashActivity;
import in.blackpaper.instasp.adapter.DrawerAdapter;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.contractor.MainActivityContractor;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.fragments.FavouriteFragment;
import in.blackpaper.instasp.fragments.FeedFragment;
import in.blackpaper.instasp.fragments.ProfileFragment;
import in.blackpaper.instasp.fragments.StoriesFragment;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;

import static in.blackpaper.instasp.data.repositry.DataObjectRepositry.dataObjectRepositry;

public class MainActivity extends BaseActivity<MainActivityPresenter> implements MainActivityContractor.View,
        FeedFragment.OnFeedFragmentInteractionListener,
        ProfileFragment.OnProfileFragmentInteractionListener,
        RatingDialogListener {

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


    @Override
    public MainActivityPresenter createPresenter() {
        return new MainActivityPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        ButterKnife.bind(this);
        initUI();
        onClick();


        if (!TextUtils.isEmpty(username)) {
            showLoading();
            new RequestInstagramAPI(ApiUtils.getUsernameUrl(username)).execute();
        }

        changeFragment(new StoriesFragment());


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

        mProfileImage.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START, true);
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(GlobalConstant.USERNAME, username);
            profileFragment.setArguments(bundle);
            changeFragment(profileFragment);
        });

        drawerAdapter.setEventListener(new DrawerAdapter.EventListener() {
            @Override
            public void onItemClick(DrawerMenuPojo drawerMenuPojo) {

                drawer.closeDrawer(GravityCompat.START, false);
                if (!isAddAccountViewVisible) {
                    if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FEED)) {
                        FeedFragment feedFragment = new FeedFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalConstant.USERNAME, username);
                        feedFragment.setArguments(bundle);
                        changeFragment(feedFragment);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.STORIES))
                        changeFragment(new StoriesFragment());
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.PROFILE_PICTURE)) {
                        startActivity(new Intent(MainActivity.this, DownloadProfileImageActivity.class));
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FAVOURITES))
                        changeFragment(new FavouriteFragment());
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.HOW_TO_USER)) {
                        startActivity(new Intent(MainActivity.this, HowToUseActivity.class));
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.SHARE_APP)) {
                        shareApp();
//                        ToastUtils.SuccessToast(context, GlobalConstant.SHARE_APP);
                    } else if (drawerMenuPojo.getMenuName().equalsIgnoreCase("Download History")) {
                        startActivity(new Intent(MainActivity.this, DownloadHistoryActivity.class));
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.MORE_APPS)) {
                        moreApp();
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.RATE_US)) {
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
                        logoutFromInsta(PreferencesManager.getPref(GlobalConstant.USERNAME));

                    } else {
                        ToastUtils.ErrorToast(context, getString(R.string.some_error));
                    }
                } else {

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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(GlobalConstant.KEY_THEME_CHANGE, false)) {
            sharedPreferences.edit().putBoolean(GlobalConstant.KEY_THEME_CHANGE, false).apply();
            // https://stackoverflow.com/questions/10844112/runtimeexception-performing-pause-of-activity-that-is-not-resumed
            // Briefly, let the activity resume properly posting the recreate call to end of the message queue
            new Handler(Looper.getMainLooper()).post(() -> {
                Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            });
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
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
            drawerMenuPojo.setMenuName(GlobalConstant.PROFILE_PICTURE);
            drawerMenuPojo.setImage(R.drawable.ic_person);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.FAVOURITES);
            drawerMenuPojo.setImage(R.drawable.ic_fav);
            drawerMenuPojoList.add(drawerMenuPojo);

            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Download History");
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

            LiveData<List<Logins>> loggedInUsers = mPresenter.getAllLoggedInUsers();
            loggedInUsers.observe(this, new Observer<List<Logins>>() {
                @Override
                public void onChanged(List<Logins> logins) {
                    if (logins.size() > 0) {
                        for (Logins logins1 : logins) {
                            DrawerMenuPojo drawerMenuPojo1 = new DrawerMenuPojo();
                            drawerMenuPojo1.setMenuName(logins1.getUserName());
                            drawerMenuPojo1.setImage(R.drawable.ic_account);
                            drawerMenuPojoList.add(drawerMenuPojo1);

                        }
                    }
                }
            });
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
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void moreApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Blackpaper")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Blackpaper")));
        }
    }

    private void showRatingDialog() {


        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setDefaultComment("This app is pretty cool !")
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.starColor)
                .setTitleTextColor(R.color.black)
                .setDescriptionTextColor(R.color.notDescriptionTextColor)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.textHintColor)
                .setCommentTextColor(R.color.black)
                .setCommentBackgroundColor(R.color.commentBoxBackground)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(MainActivity.this)
                .show();
    }


    @Override
    public void onNegativeButtonClicked() {
    }

    @Override
    public void onNeutralButtonClicked() {
    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        ToastUtils.SuccessToast(context, "Thanks for your feedback");
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

    public void changeAccount(String usernameSelecte) {
        LiveData<List<Logins>> loggedInUsers = mPresenter.getAllLoggedInUsers();
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
                            ZoomstaUtil.setStringPreference(MainActivity.this, username, "username");
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getCookies(), "cooki");
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getCsrf(), "csrf");
                            ZoomstaUtil.setStringPreference(MainActivity.this, logins1.getSession_id(), "sessionid");


                            PreferencesManager.savePref(GlobalConstant.USERNAME, logins1.getUserName());
                            PreferencesManager.savePref(GlobalConstant.USER_ID, logins1.getUserId());
                            PreferencesManager.savePref(GlobalConstant.TOKEN, logins1.getToken());


                            startActivity(intent);

                        }
                    }
                }
            }
        });
    }

    public void logoutFromInsta(String username) {
        LiveData<List<Logins>> loggedInUsers = mPresenter.getAllLoggedInUsers();
        loggedInUsers.observe(this, new Observer<List<Logins>>() {
            @Override
            public void onChanged(List<Logins> logins) {
                if (logins.size() > 0) {
                    for (Logins logins1 : logins) {

                        if (logins1.getUserName().equals(username)) {

                            dataObjectRepositry.deleteExistingUser(logins1.getId());
                            ToastUtils.SuccessToast(context, GlobalConstant.LOGOUT);
                            PreferencesManager.clear();
                            ZoomstaUtil.clearPref(context);
                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
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

