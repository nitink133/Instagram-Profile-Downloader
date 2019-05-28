package in.blackpaper.instasp.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;
import in.blackpaper.instasp.fragments.StoriesOverview;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.OverviewDialog;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.RegularTextView;

public class FaveListAdapter extends RecyclerView.Adapter<FaveListAdapter.FaveListHolder> {
    private static final String TAG = "FaveListAdapter";
    private Activity context;
    private List<UserObject> userObjects;
    private OverviewDialog overviewDialog;
    private FragmentManager fm;
    private int count, prof;


    public FaveListAdapter(Activity context){
        this.userObjects = new ArrayList<>();
        this.context = context;
    }

    public void setUserObjects(List<UserObject> itemList){
        userObjects.clear();
        userObjects.addAll(itemList);
        notifyDataSetChanged();
    }

    @Override
    public FaveListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fave_user_item, parent, false);

        return new FaveListHolder(view);
    }

    @Override
    public void onBindViewHolder(FaveListHolder holder, int position) {
        final UserObject object = userObjects.get(position);
        fm = ((MainActivity) context).getSupportFragmentManager();

        holder.storyObject.setVisibility(View.VISIBLE);

        if(object.getFaved()){
            startCheckAnimation(holder);
        }else{}
        holder.realName.setText(object.getRealName());
        holder.userName.setText(object.getUserName());
        Glide.with(context).load(object.getImage()).thumbnail(0.2f).into(holder.userIcon);

        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overviewDialog = new OverviewDialog(context, object);
                overviewDialog.show();

                prof = ZoomstaUtil.getIntegerPreference(context, "profCount");

            }
        });

        holder.storyObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = ZoomstaUtil.getIntegerPreference(context, "clickCount");

                StoriesOverview overview = new StoriesOverview();
                Bundle args = new Bundle();
                args.putString("username", object.getUserName());
                args.putString("user_id", object.getUserId());
                overview.setArguments(args);
                overview.show(fm, "Story Overview");
            }
        });

        holder.favourite.setOnClickListener(v->{
            if(!object.getFaved()){
                ZoomstaUtil.addFaveUser(context,object.getUserId());
                startCheckAnimation(holder);
                object.setFaved(true);
                ToastUtils.SuccessToast(context, object.getUserName()+ " added to fave IG'ers");

            } else {
                ZoomstaUtil.removeFaveUser(context,object.getUserId());
                startCheckAnimation(holder);
                object.setFaved(false);
                ToastUtils.SuccessToast(context, object.getUserName()+ " removed from fave IG'ers");
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

    public class FaveListHolder extends RecyclerView.ViewHolder{
        private CircleImageView userIcon;
        private TextView userName;
        private TextView realName;
        private LinearLayout storyObject;
        LottieAnimationView favourite;

        public FaveListHolder(View view){
            super(view);

            userIcon = view.findViewById(R.id.story_icon);
            userName = view.findViewById(R.id.user_name);
            realName = view.findViewById(R.id.real_name);

            storyObject = view.findViewById(R.id.story_object);
            favourite = view.findViewById(R.id.favourite);

        }
    }


    private void startCheckAnimation(FaveListHolder holder) {
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

}