package in.blackpaper.instasp.activity.dashboard;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.ProfilepPictureActivity;
import in.blackpaper.instasp.adapter.DrawerAdapter;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.contractor.MainActivityContractor;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.fragments.FavouriteFragment;
import in.blackpaper.instasp.fragments.FeedFragment;
import in.blackpaper.instasp.fragments.ProfileFragment;
import in.blackpaper.instasp.fragments.StoriesFragment;
import in.blackpaper.instasp.utils.ToastUtils;

public class MainActivity extends BaseActivity<MainActivityPresenter> implements MainActivityContractor.View,
        FeedFragment.OnFeedFragmentInteractionListener, StoriesFragment.OnStoriesFragmentInteractionListener,
        FavouriteFragment.OnFragmentInteractionListener, ProfileFragment.OnProfileFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    ImageView changeDrawerImageVIew;
    boolean isAddAccountViewVisible = false;
    private TextView mUsername, mEmail;
    private CircleImageView mProfileImage;
    private RecyclerView drawerMenuRecyclerView;
    private Context context;
    private DrawerAdapter drawerAdapter;
    NavigationView navigationView;
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
        ButterKnife.bind(this);
        initUI();
        onClick();


        FeedFragment feedFragment = new FeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GlobalConstant.USERNAME, username);
        feedFragment.setArguments(bundle);
        changeFragment(feedFragment);


    }


    @Override
    protected void onStart() {
        super.onStart();
        context = MainActivity.this;
    }

    public void initUI() {

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        drawerAdapter.setEventListener(new DrawerAdapter.EventListener() {
            @Override
            public void onItemClick(DrawerMenuPojo drawerMenuPojo) {
                if (!isAddAccountViewVisible) {
                    if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FEED)) {
                        FeedFragment feedFragment = new FeedFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalConstant.USERNAME, username);
                        feedFragment.setArguments(bundle);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.STORIES))
                        changeFragment(new StoriesFragment());
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.PROFILE_PICTURE)){
                        ProfileFragment profileFragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(GlobalConstant.USERNAME,username);
                        profileFragment.setArguments(bundle);
                        changeFragment(profileFragment);
                    }
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.FAVOURITES))
                        changeFragment(new FavouriteFragment());
                    else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.HOW_TO_USER)) {
                        ToastUtils.SuccessToast(context, GlobalConstant.HOW_TO_USER);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.SHARE_APP)) {
                        ToastUtils.SuccessToast(context, GlobalConstant.SHARE_APP);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.MORE_APPS)) {
                        ToastUtils.SuccessToast(context, GlobalConstant.MORE_APPS);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.RATE_US)) {
                        ToastUtils.SuccessToast(context, GlobalConstant.RATE_US);
                    } else if (drawerMenuPojo.getMenuName().equals(GlobalConstant.LOGOUT)) {
                        ToastUtils.SuccessToast(context, GlobalConstant.LOGOUT);
                    } else {
                        ToastUtils.ErrorToast(context, getString(R.string.some_error));
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
            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.FEED);
            drawerMenuPojoList.add(drawerMenuPojo);

            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.STORIES);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.PROFILE_PICTURE);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.FAVOURITES);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.HOW_TO_USER);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.SHARE_APP);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.MORE_APPS);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.RATE_US);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName(GlobalConstant.LOGOUT);
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerAdapter.setMenu(drawerMenuPojoList);
        } else {
            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Add Account");
            drawerMenuPojoList.add(drawerMenuPojo);

            LiveData<List<Logins>> loggedInUsers = mPresenter.getAllLoggedInUsers();
            loggedInUsers.observe(this, new Observer<List<Logins>>() {
                @Override
                public void onChanged(List<Logins> logins) {
                    if (logins.size() > 0) {
                        for (Logins logins1 : logins) {
                            DrawerMenuPojo drawerMenuPojo1 = new DrawerMenuPojo();
                            drawerMenuPojo1.setMenuName(logins1.getUserName());
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


}

