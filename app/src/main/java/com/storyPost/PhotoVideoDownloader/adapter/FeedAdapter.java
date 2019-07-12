package com.storyPost.PhotoVideoDownloader.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skydoves.elasticviews.ElasticImageView;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.data.prefs.PreferencesManager;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Downloads;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.view.RegularTextView;
import com.storyPost.PhotoVideoDownloader.view.springyRecyclerView.SpringyAdapterAnimationType;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ItemViewHolder> {

    private Context context;
    private List<IntagramProfileResponse.Edge> items;
    private ImagesSliderAdapter imagesSliderAdapter;
    private Bitmap bitmap = null;

    public FeedAdapter(Context context, RecyclerView recyclerView) {

        this.context = context;
        this.items = new ArrayList<>();
        // pass recyclerView in it.

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
        String url = "";
        if (edge.getNode() != null) {
            if (edge.getNode().getThumbnailResources() != null && edge.getNode().getThumbnailResources().size() > 0) {

//                imagesSliderAdapter = new ImagesSliderAdapter(context,edge.getNode().getThumbnailResources());
//                holder.imagesSlider.setAdapter(imagesSliderAdapter);
//                holder.dotsIndicator.setViewPager(holder.imagesSlider);
                url = edge.getNode().getDisplayUrl();
                Glide.with(context).load((String) PreferencesManager.getPref(GlobalConstant.PROFILE_PIC)).into(holder.userProfileImage);
                Glide.with(context).load(edge.getNode().getDisplayUrl()).into(holder.image);
                holder.username.setText(PreferencesManager.getPref(GlobalConstant.USERNAME));

                IntagramProfileResponse.EdgeMediaToCaption edgeMediaToCaption = edge.getNode().getEdgeMediaToCaption();
                if (edgeMediaToCaption != null && edgeMediaToCaption.getEdges() != null && edgeMediaToCaption.getEdges().size() > 0) {
                    if (edgeMediaToCaption.getEdges().get(0).getNode() != null && edgeMediaToCaption.getEdges().get(0).getNode().getText() != null)
                        holder.caption.setText(edge.getNode().getEdgeMediaToCaption().getEdges().get(0).getNode().getText());
                }
            }

            String finalUrl = url;

            holder.share.setOnClickListener(v -> {
                if (bitmap != null) {
                    String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", null);
                    if (bitmapPath != null) {
                        Uri bitmapUri = Uri.parse(bitmapPath);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/png");
                        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                        context.startActivity(Intent.createChooser(intent, "Share via..."));
                    } else {
                        ToastUtils.ErrorToast(context, "Something went wrong!");
                    }
                } else {
                    ToastUtils.ErrorToast(context, "Please download image first.");
                }
            });

            holder.repost.setOnClickListener(v -> {
                if (!edge.getNode().getIsVideo()) {
                    if (bitmap != null) {

                        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", null);
                        if (bitmapPath != null) {
                            Uri bitmapUri = Uri.parse(bitmapPath);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

                            intent.setPackage("com.instagram.android");
                            context.startActivity(Intent.createChooser(intent, "Share via..."));
                        } else {
                            ToastUtils.ErrorToast(context, "Please download image first.");
                        }
                    } else {
                    }
                }
            });
            holder.download.setOnClickListener(v -> {
                Dexter.withActivity((Activity) context)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                if (edge.getNode().getIsVideo()) {
                                } else
                                    new RequestProfileInstagramAPI(finalUrl, edge.getNode().getId(), PreferencesManager.getPref(GlobalConstant.USERNAME)).execute();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                                        .withContext(context)
                                        .withTitle("Storage permission")
                                        .withMessage("Storage permission is needed to save pictures")
                                        .withButtonText(android.R.string.ok)
                                        .build();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            });

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
        RegularTextView caption, username;
        ElasticImageView heart;
        ViewPager imagesSlider;
        CircleImageView userProfileImage;
        WormDotsIndicator dotsIndicator;
        ElasticImageView download, repost, share;
        LinearLayout rootLayoutVIew;
        ElasticImageView image;


        public ItemViewHolder(View itemView) {
            super(itemView);
            caption = itemView.findViewById(R.id.caption);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            username = itemView.findViewById(R.id.username);
            heart = itemView.findViewById(R.id.heart);
            image = itemView.findViewById(R.id.image);
            imagesSlider = itemView.findViewById(R.id.pager);
            repost = itemView.findViewById(R.id.repost);
            share = itemView.findViewById(R.id.share);
            download = itemView.findViewById(R.id.download);
            rootLayoutVIew = itemView.findViewById(R.id.rootLayoutVIew);
            dotsIndicator = itemView.findViewById(R.id.dots_indicator);


        }

    }


    private void saveImage(Bitmap bitmap, String userId, String username) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + GlobalConstant.SAVED_FILE_NAME);
        if (!file.exists())
            file.mkdirs();

        String fileName = GlobalConstant.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".jpg";

        File newImage = new File(file, fileName);
        if (newImage.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(newImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Downloads downloads = new Downloads();
            downloads.setUser_id(userId);
            downloads.setPath(newImage.getPath());
            downloads.setUsername(username);
            downloads.setFilename(fileName);
            downloads.setType(0);
            DataObjectRepositry.dataObjectRepositry.addDownloadedData(downloads);
            ToastUtils.NORMAL(context, "Saving image...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RequestProfileInstagramAPI extends AsyncTask<Void, String, String> {

        String url = "";
        HttpURLConnection connection = null;
        String userId, username;

        public RequestProfileInstagramAPI(String url, String userId, String username) {
            this.url = url;
            this.userId = userId;
            this.username = username;
        }

        @Override
        protected String doInBackground(Void... params) {
            connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            saveImage(bitmap, userId, username);

        }
    }

}



