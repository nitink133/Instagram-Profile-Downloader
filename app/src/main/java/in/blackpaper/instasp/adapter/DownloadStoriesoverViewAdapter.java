package in.blackpaper.instasp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.ViewStoryActivity;
import in.blackpaper.instasp.models.StoryModel;
import in.blackpaper.instasp.utils.SquareLayout;
import in.blackpaper.instasp.utils.ZoomstaUtil;

public class DownloadStoriesoverViewAdapter  extends RecyclerView.Adapter<DownloadStoriesoverViewAdapter.StoriesOverViewHolder> {
    public List<String> modelList;
    public List<StoryModel> storyModels;
    private Context context;

    public ArrayList<StoryModel> selected_usersList=new ArrayList<>();
    private int count;

    public DownloadStoriesoverViewAdapter(Context context, List<String> models, List<StoryModel> storyModels){
        this.context = context;
        this.modelList = models;
        this.storyModels = storyModels;
    }

    @Override
    public StoriesOverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_overview_object, parent, false);

        return new StoriesOverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StoriesOverViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final String model = modelList.get(position);

        Glide.with(context).load(model).thumbnail(0.2f).into(holder.imageView);
        holder.layout.setVisibility(View.VISIBLE);
        if(!model.endsWith(".jpg"))
            holder.isVideo.setVisibility(View.VISIBLE);
        else holder.isVideo.setVisibility(View.GONE);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, ViewStoryActivity.class);
//                DataHolder.setData(storyModels);
//                intent.putExtra("isLarge", true);
//                intent.putExtra("pos", position);
//                context.startActivity(intent);
                count = ZoomstaUtil.getIntegerPreference(context, "itemCount");


                Intent intent = new Intent(context, ViewStoryActivity.class);
                intent.putExtra("isFromNet", false);
                intent.putParcelableArrayListExtra("storylist", (ArrayList<StoryModel>) storyModels);
                intent.putExtra("pos", position);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
            }
        });


    }

    @Override
    public int getItemCount() {
        return storyModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class StoriesOverViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private ImageView isVideo;
        private SquareLayout layout;

        public StoriesOverViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.overview_media_holder);
            isVideo = itemView.findViewById(R.id.overview_is_video);
            this.layout = itemView.findViewById(R.id.select_stories_overview_item);
        }
    }



}
