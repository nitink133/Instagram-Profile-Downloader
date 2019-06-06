package com.blackpaper.InstaDownload.stories.profile.post.download.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skydoves.elasticviews.ElasticLayout;

import java.util.ArrayList;
import java.util.List;

import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.localpojo.DrawerMenuPojo;
import com.blackpaper.InstaDownload.stories.profile.post.download.view.RegularTextView;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ItemViewHolder> {

    private Context context;
    private List<DrawerMenuPojo> items;


    public DrawerAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();

    }

    public void setMenu(List<DrawerMenuPojo> itemsList) {
        items.clear();
        items.addAll(itemsList);
        notifyDataSetChanged();

    }

    public interface EventListener{
        void onItemClick(DrawerMenuPojo item);
    }

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public DrawerMenuPojo getItemData(int position) {
        return items.get(position);
    }


    @Override
    public DrawerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawer_menu, parent, false);

        return new DrawerAdapter.ItemViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(final DrawerAdapter.ItemViewHolder holder, final int position) {

        DrawerMenuPojo drawerMenuPojo = items.get(position);
        holder.text.setText(drawerMenuPojo.getMenuName());
        Glide.with(context).load(drawerMenuPojo.getImage()).into(holder.image);
//        holder.image.setVisibility(View.GONE);

        holder.rootLayoutVIew.setOnClickListener(v->{
            if(eventListener!=null)
                eventListener.onItemClick(drawerMenuPojo);
        });


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RegularTextView text;
        ImageView image;
        ElasticLayout rootLayoutVIew;


        public ItemViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);
            rootLayoutVIew = itemView.findViewById(R.id.rootLayoutVIew);


        }

    }


}


