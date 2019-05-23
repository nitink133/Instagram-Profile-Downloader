package in.blackpaper.instasp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;
import in.blackpaper.instasp.view.RegularTextView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ItemViewHolder> {

    private Context context;
    private List<DrawerMenuPojo> items;


    public FeedAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();

    }

    public void setMenu(List<DrawerMenuPojo> itemsList) {
        items.clear();
        items.addAll(itemsList);
        notifyDataSetChanged();

    }

    public interface EventListener {
        void onItemClick(DrawerMenuPojo item);
    }

    private FeedAdapter.EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public DrawerMenuPojo getItemData(int position) {
        return items.get(position);
    }


    @Override
    public FeedAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);

        return new FeedAdapter.ItemViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(final FeedAdapter.ItemViewHolder holder, final int position) {


        holder.rootLayoutVIew.setOnClickListener(v -> {
//            if (eventListener != null)
//                eventListener.onItemClick(drawerMenuPojo);
        });


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RegularTextView caption;
        ImageView heart;
        ImageButton download, repost, share;
        CardView rootLayoutVIew;


        public ItemViewHolder(View itemView) {
            super(itemView);
            caption = itemView.findViewById(R.id.caption);
            heart = itemView.findViewById(R.id.heart);
            repost = itemView.findViewById(R.id.repost);
            share = itemView.findViewById(R.id.share);
            download = itemView.findViewById(R.id.download);
            rootLayoutVIew = itemView.findViewById(R.id.rootLayoutVIew);


        }

    }


}



