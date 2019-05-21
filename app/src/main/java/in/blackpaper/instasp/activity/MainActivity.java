package in.blackpaper.instasp.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.adapter.DrawerAdapter;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    ImageView changeDrawerImageVIew;
    boolean isAddAccountViewVisible;
    private TextView mUsername, mEmail;
    private CircleImageView mProfileImage;
    private RecyclerView drawerMenuRecyclerView;
    private Context context;
    private DrawerAdapter drawerAdapter;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();


    }


    @Override
    protected void onStart() {
        super.onStart();
        context = MainActivity.this;
        initUI();
    }

    public void initUI() {

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mProfileImage = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        mUsername = navigationView.getHeaderView(0).findViewById(R.id.userName);
        mEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        changeDrawerImageVIew = navigationView.getHeaderView(0).findViewById(R.id.changeDrawerImageVIew);
        drawerMenuRecyclerView = findViewById(R.id.drawerMenuRecyclerView);

        drawerAdapter = new DrawerAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        drawerMenuRecyclerView.setLayoutManager(mLayoutManager);
        drawerMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        drawerMenuRecyclerView.setAdapter(drawerAdapter);


        addItemsInDrawer();
        drawerAdapter.setEventListener(new DrawerAdapter.EventListener() {
            @Override
            public void onItemClick(DrawerMenuPojo item) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

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
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.action_search:
//                openSearchActivity();
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment;
//        switch (item.getItemId()) {
//            case R.id.nav_add:
//                startActivity(new Intent(this, CreateNotesActivity.class));
//                break;
//            case R.id.nav_settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//                break;
//
//        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void addItemsInDrawer() {
        List<DrawerMenuPojo> drawerMenuPojoList = new ArrayList<>();
        if (isAddAccountViewVisible) {
            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Feed");
            drawerMenuPojoList.add(drawerMenuPojo);

            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Stories");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Profile Picture");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Favourites");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("How to User");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Share App");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("More Apps");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Rate us");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Settings");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerAdapter.setMenu(drawerMenuPojoList);
        } else {
            DrawerMenuPojo drawerMenuPojo = new DrawerMenuPojo();
            drawerMenuPojo.setMenuName("Add Account");
            drawerMenuPojoList.add(drawerMenuPojo);
            drawerAdapter.setMenu(drawerMenuPojoList);
        }
    }

}

