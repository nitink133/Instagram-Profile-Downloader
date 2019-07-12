package com.storyPost.PhotoVideoDownloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.data.localpojo.DrawerMenuPojo;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ItemViewHolder> {

    private Context context;
    private List<IntagramProfileResponse.Edge> items;

//    private SpringyAdapterAnimator springyAdapterAnimator;

    public UserProfileAdapter(Context context,RecyclerView recyclerView) {

        this.context = context;
        this.items = new ArrayList<>();

//        springyAdapterAnimator = new SpringyAdapterAnimator(recyclerView);
//        // set SpringyAdapterAnimationType
//        springyAdapterAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SCALE);
//        // (optional) add Spring Config
//        springyAdapterAnimator.addConfig(85,15);

    }

    public void setEdges(List<IntagramProfileResponse.Edge> itemsList) {
        items.clear();
        items.addAll(itemsList);
        notifyDataSetChanged();

    }

    public interface EventListener {
        void onItemClick(DrawerMenuPojo item);
    }

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public IntagramProfileResponse.Edge getItemData(int position) {
        return items.get(position);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_profile, parent, false);
//        springyAdapterAnimator.onSpringItemCreate(itemLayoutView);
        return new ItemViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        IntagramProfileResponse.Edge edge = items.get(position);
        if (edge.getNode() != null) {
//            if (edge.getNode().getThumbnailResources() != null && edge.getNode().getThumbnailResources().size() > 0) {
                Glide.with(context).load(edge.getNode().getDisplayUrl()).into(holder.image);
//            }


        }
//        springyAdapterAnimator.onSpringItemBind(holder.itemView, position);


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView image;


        public ItemViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);


        }

    }


}


