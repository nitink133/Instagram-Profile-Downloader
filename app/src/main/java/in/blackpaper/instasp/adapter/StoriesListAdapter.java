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
import android.widget.Filter;
import android.widget.Filterable;
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
import in.blackpaper.instasp.activity.SearchActivity;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.fragments.StoriesOverview;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.OverviewDialog;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.BoldTextView;
import in.blackpaper.instasp.view.RegularTextView;

public class StoriesListAdapter extends RecyclerView.Adapter<StoriesListAdapter.StoriesListHolder> implements Filterable {
    private static final String TAG = "StoriesListAdapter";
    private Activity context;
    private List<UserObject> userObjects;
    private OverviewDialog overviewDialog;
    private FragmentManager fm;
    private int count, prof;
    private List<UserObject> mDisplayedValues;    // Values to be displayed


    public StoriesListAdapter(Activity context) {
        this.userObjects = new ArrayList<>();
        this.mDisplayedValues = new ArrayList<>();
        this.context = context;
    }

    public void setUserObjects(List<UserObject> list) {
        userObjects.clear();
        userObjects.addAll(list);
        mDisplayedValues.clear();
        mDisplayedValues.addAll(userObjects);
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
        final UserObject object = mDisplayedValues.get(position);
        if (context instanceof MainActivity)
            fm = ((MainActivity) context).getSupportFragmentManager();
        else
            fm = ((SearchActivity) context).getSupportFragmentManager();
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
        return mDisplayedValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mDisplayedValues = (ArrayList<UserObject>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<UserObject> FilteredArrList = new ArrayList<UserObject>();

                if (userObjects == null) {
                    userObjects = new ArrayList<UserObject>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = userObjects.size();
                    results.values = userObjects;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < userObjects.size(); i++) {
                        String data = userObjects.get(i).getUserName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            UserObject userObject = new UserObject();
                            userObject.setFaved(userObjects.get(i).getFaved());
                            userObject.setBitmap(userObjects.get(i).getBitmap());
                            userObject.setImage(userObjects.get(i).getImage());
                            userObject.setRealName(userObjects.get(i).getRealName());
                            userObject.setUserName(userObjects.get(i).getUserName());
                            userObject.setUserId(userObjects.get(i).getUserId());
                            FilteredArrList.add(userObject
                            );
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }


    public class StoriesListHolder extends RecyclerView.ViewHolder {
        private CircleImageView userIcon;
        private BoldTextView userName;
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