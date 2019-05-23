package in.blackpaper.instasp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ItemViewHolder> {

    private Context context;
    private List<IntagramProfileResponse.Edge> items;


    public UserProfileAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();

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

        return new ItemViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        IntagramProfileResponse.Edge edge = items.get(position);
        if (edge.getNode() != null) {
            Glide.with(context).load(edge.getNode().getDisplayUrl()).into(holder.image);


        }


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


