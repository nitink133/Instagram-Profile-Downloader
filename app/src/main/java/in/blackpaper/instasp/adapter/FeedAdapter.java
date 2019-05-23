package in.blackpaper.instasp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;
import in.blackpaper.instasp.view.RegularTextView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ItemViewHolder> {

    private Context context;
    private List<IntagramProfileResponse.Edge> items;


    public FeedAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();

    }

    public void setEdges(List<IntagramProfileResponse.Edge> itemsList) {
        items.clear();
        items.addAll(itemsList);
        notifyDataSetChanged();

    }

    public interface EventListener {
        void onItemClick(IntagramProfileResponse.Edge item);
    }

    private FeedAdapter.EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public IntagramProfileResponse.Edge getItemData(int position) {
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
        IntagramProfileResponse.Edge edge = items.get(position);
        if (edge.getNode() != null) {
            Glide.with(context).load(edge.getNode().getDisplayUrl()).into(holder.image);
            IntagramProfileResponse.EdgeMediaToCaption edgeMediaToCaption = edge.getNode().getEdgeMediaToCaption();
            if (edgeMediaToCaption != null && edgeMediaToCaption.getEdges() != null && edgeMediaToCaption.getEdges().size() > 0) {
                if (edgeMediaToCaption.getEdges().get(0).getNode() != null && edgeMediaToCaption.getEdges().get(0).getNode().getText() != null)
                    holder.caption.setText(edge.getNode().getEdgeMediaToCaption().getEdges().get(0).getNode().getText());
            }

        }


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
        ImageView heart, image;
        ImageButton download, repost, share;
        LinearLayout rootLayoutVIew;


        public ItemViewHolder(View itemView) {
            super(itemView);
            caption = itemView.findViewById(R.id.caption);
            heart = itemView.findViewById(R.id.heart);
            image = itemView.findViewById(R.id.image);
            repost = itemView.findViewById(R.id.repost);
            share = itemView.findViewById(R.id.share);
            download = itemView.findViewById(R.id.download);
            rootLayoutVIew = itemView.findViewById(R.id.rootLayoutVIew);


        }

    }


}



