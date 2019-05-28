package in.blackpaper.instasp.adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.blackpaper.instasp.BuildConfig;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.fragments.StoriesOverview;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.OverviewDialog;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.RegularTextView;

public class StoriesListAdapter extends RecyclerView.Adapter<StoriesListAdapter.StoriesListHolder> {
    private static final String TAG = "StoriesListAdapter";
    private Activity context;
    private List<UserObject> userObjects;
    private OverviewDialog overviewDialog;
    private FragmentManager fm;
    private int count, prof;


    public StoriesListAdapter(Activity context) {
        this.userObjects = new ArrayList<>();
        this.context = context;
    }

    public void setUserObjects(List<UserObject> list) {
        userObjects.clear();
        userObjects.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public StoriesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_list_object, parent, false);


        return new StoriesListHolder(view);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(StoriesListHolder holder, final int position) {
        final UserObject object = userObjects.get(position);
        fm = ((MainActivity) context).getSupportFragmentManager();
        holder.storyObject.setVisibility(View.VISIBLE);

        holder.realName.setText(object.getRealName());
        holder.userName.setText(object.getUserName());
        if (object.getFaved()) {
            if (ZoomstaUtil.containsUser(context, object.getUserId())) {
                startCheckAnimation(holder);
            } else {
                startFalseCheckAnimation(holder);
                object.setFaved(false);
            }
        } else {
            startFalseCheckAnimation(holder);
        }
        Glide.with(context).load(object.getImage()).thumbnail(0.2f).into(holder.userIcon);

        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prof = ZoomstaUtil.getIntegerPreference(context, "profCount");

                overviewDialog = new OverviewDialog(context, object);
                overviewDialog.show();
            }
        });

        holder.storyObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = ZoomstaUtil.getIntegerPreference(context, "clickCount");


                if (BuildConfig.DEBUG)
                    Log.d(object.getUserName() + "userId", object.getUserId());

                StoriesOverview overview = new StoriesOverview();
                Bundle args = new Bundle();
                args.putString("username", object.getUserName());
                args.putString("user_id", object.getUserId());
                overview.setArguments(args);
                overview.show(fm, "Story Overview");


            }
        });

        holder.favourite.setOnClickListener(v -> {
            if (!object.getFaved()) {
                ZoomstaUtil.addFaveUser(context, object.getUserId());
                startCheckAnimation(holder);
                object.setFaved(true);
                ToastUtils.SuccessToast(context, object.getUserName() + " added to fave IG'ers");

            } else {
                ZoomstaUtil.removeFaveUser(context, object.getUserId());
                startCheckAnimation(holder);
                object.setFaved(false);
                ToastUtils.SuccessToast(context, object.getUserName() + " removed from fave IG'ers");
            }
        });

    }

    @Override
    public int getItemCount() {
        return userObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class StoriesListHolder extends RecyclerView.ViewHolder {
        private CircleImageView userIcon;
        private RegularTextView userName;
        private RegularTextView realName;
        private LinearLayout storyObject;
        private LottieAnimationView favourite;

        public StoriesListHolder(View view) {
            super(view);

            userIcon = view.findViewById(R.id.story_icon);
            userName = view.findViewById(R.id.user_name);
            realName = view.findViewById(R.id.real_name);
            storyObject = view.findViewById(R.id.story_object);
            favourite = view.findViewById(R.id.favourite);

        }
    }

    private void startCheckAnimation(StoriesListHolder holder) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500);
        animator.addUpdateListener(valueAnimator -> {
            holder.favourite.setProgress((Float) valueAnimator.getAnimatedValue());

        });

        if (holder.favourite.getProgress() == 0f) {
            animator.start();
        } else {
            holder.favourite.setProgress(0f);
        }
    }

    private void startFalseCheckAnimation(StoriesListHolder holder) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500);
        animator.addUpdateListener(valueAnimator -> {
            holder.favourite.setProgress((Float) valueAnimator.getAnimatedValue());

        });


        holder.favourite.setProgress(0f);

    }
}