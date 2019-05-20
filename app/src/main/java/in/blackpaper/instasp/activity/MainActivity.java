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
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.base.BaseActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    ImageView changeDrawerImageVIew;
    boolean isAddAccountViewVisible;
    private TextView mUsername, mEmail;
    private CircleImageView mProfileImage;
    private Context context;
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

        Menu menu = navigationView.getMenu();
        SubMenu topChannelMenu = menu.addSubMenu("Instagram");
        topChannelMenu.add("Feed");
        topChannelMenu.add("Stories");
        topChannelMenu.add("Profile Picture");
        topChannelMenu.add("Favourites");
        SubMenu appMenu = menu.addSubMenu("Application Related");
        appMenu.add("How to User");
        appMenu.add("Share App");
        appMenu.add("More Apps");
        appMenu.add("Rate us");
        appMenu.add("Settings");

    }

    @Override
    protected void onResume() {
        super.onResume();

        changeDrawerImageVIew.setOnClickListener(v -> {

            Menu menu = navigationView.getMenu();
            menu.clear();
            if (isAddAccountViewVisible) {
                Glide.with(this).load(R.drawable.ic_arrow_drop).into(changeDrawerImageVIew);
                isAddAccountViewVisible = false;


                SubMenu topChannelMenu = menu.addSubMenu("Instagram");
                topChannelMenu.add("Feed");
                topChannelMenu.add("Stories");
                topChannelMenu.add("Profile Picture");
                topChannelMenu.add("Favourites");
                SubMenu appMenu = menu.addSubMenu("Application Related");
                appMenu.add("How to User");
                appMenu.add("Share App");
                appMenu.add("More Apps");
                appMenu.add("Rate us");
                appMenu.add("Settings");
            } else {
                Glide.with(this).load(R.drawable.ic_arrow_up).into(changeDrawerImageVIew);
                isAddAccountViewVisible = true;
                SubMenu topChannelMenu = menu.addSubMenu("Add Account");
                topChannelMenu.add("n.i.t.i.n_k.h.a.n.na");
                topChannelMenu.setIcon(R.drawable.ic_share);
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


}

