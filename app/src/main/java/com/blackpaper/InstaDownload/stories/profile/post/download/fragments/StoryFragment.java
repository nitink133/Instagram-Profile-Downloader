package com.blackpaper.InstaDownload.stories.profile.post.download.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blackpaper.InstaDownload.stories.profile.post.download.activity.BaseActivity;
import com.blackpaper.InstaDownload.stories.profile.post.download.activity.DownloadHistoryActivity;
import com.blackpaper.InstaDownload.stories.profile.post.download.activity.ViewProfileActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.blackpaper.InstaDownload.stories.profile.post.download.GlobalConstant;
import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.repositry.DataObjectRepositry;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.room.tables.Downloads;
import com.blackpaper.InstaDownload.stories.profile.post.download.models.StoryModel;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.InstaUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.StoryListener;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.ToastUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.ZoomstaUtil;
import com.blackpaper.InstaDownload.stories.profile.post.download.view.BoldTextView;
import com.blackpaper.InstaDownload.stories.profile.post.download.view.RegularTextView;

public class StoryFragment extends Fragment {
    private PhotoView imageView;
    private ImageButton save, share, delete, repost, back;
    private Bitmap bitmap;
    private LinearLayout buttons;
    private StoryModel storyItem;
    private String url;
    private boolean isDownloadImagePost;
    private SpinKitView wave;
    private StoryListener storyListener;
    private FrameLayout video;
    private DataObjectRepositry dataObjectRepositry;
    private UniversalVideoView videoView;
    private UniversalMediaController mediaController;
    private ImageButton play, download_history;
    private Context context;
    private static Boolean isFromNet;
    private String vidUrl;
    private File cachedFile;

    public boolean isAlreadyDownloaded() {
        return isAlreadyDownloaded;
    }

    public void setAlreadyDownloaded(boolean alreadyDownloaded) {
        isAlreadyDownloaded = alreadyDownloaded;
    }

    private boolean isAlreadyDownloaded;

    public StoryFragment() {
        // Required empty public constructor
    }

    public static StoryFragment newInstance(Boolean isFromNet) {
        StoryFragment storyFragment = new StoryFragment();
        isFromNet(isFromNet);

        return storyFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof StoryListener)
            storyListener = (StoryListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        imageView = view.findViewById(R.id.story_imageview);

        download_history = view.findViewById(R.id.download_history);
        save = view.findViewById(R.id.story_button_save);
        share = view.findViewById(R.id.story_button_share);
        back = view.findViewById(R.id.back);
        repost = view.findViewById(R.id.repost);
        delete = view.findViewById(R.id.story_button_delete);
        delete.setVisibility(View.GONE);
        buttons = view.findViewById(R.id.story_button_options);
        wave = view.findViewById(R.id.loading_saved_item);
        video = view.findViewById(R.id.video_layout);
        videoView = view.findViewById(R.id.video_view);
        mediaController = view.findViewById(R.id.media_controller);
        videoView.setMediaController(mediaController);
        play = view.findViewById(R.id.story_video);

        setupVideo();
        setStory();

        back.setOnClickListener(v -> {
            if (getActivity() != null) {

                int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();

                if (count == 0) {
                    getActivity().onBackPressed();
                    //additional code
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        download_history.setOnClickListener(v -> {
            if (context != null)
                startActivity(new Intent(context, DownloadHistoryActivity.class));

        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttons.isShown())
                    buttons.setVisibility(View.GONE);
                else
                    buttons.setVisibility(View.VISIBLE);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storyItem.getFilePath() != null) {
                    File file = new File(storyItem.getFilePath());
                    onDeleteClick(file);
                } else
                    ToastUtils.ErrorToast(getContext(), "Unable to find file");
            }
        });

        repost.setOnClickListener(v -> {


            if (!isFromNet) {
                if (storyItem.getType() == 0) {
                    repostLocalImage(storyItem.getFilePath());
                } else {
                    repostLocalVideo(storyItem.getFilePath());
                }
            } else {
                if (url.endsWith(".jpg")) {

                    if (bitmap != null) {
                        String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
                        Uri bitmapUri = Uri.parse(bitmapPath);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/png");

                        intent.setPackage("com.instagram.android");
                        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                        startActivity(Intent.createChooser(intent, "Share via..."));
                    } else
                        ToastUtils.ErrorToast(getContext(), "Something went wrong.");
                } else {
                    if (url.contains(".jpg") && isDownloadImagePost) {
                        if (bitmap != null) {
                            String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
                            Uri bitmapUri = Uri.parse(bitmapPath);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");

                            intent.setPackage("com.instagram.android");
                            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                            startActivity(Intent.createChooser(intent, "Share via..."));
                        } else
                            ToastUtils.ErrorToast(getContext(), "Something went wrong.");
                    } else new RepostVideo().execute(new String[0]);
                }
            }

        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

//                                delete.setVisibility(View.VISIBLE);
                                if (isAlreadyDownloaded) {
                                    ToastUtils.SuccessToast(context, "Post already downloaded");
                                } else {
                                    if (url.endsWith(".jpg") && bitmap != null) {
                                        saveImage(bitmap);
                                    } else {
                                        if (url.contains(".jpg") && isDownloadImagePost) {
                                            saveImage(bitmap);
                                        } else
                                            new saveVideo(vidUrl).execute(new String[0]);
                                    }
                                }
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                                        .withContext(getActivity())
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
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFromNet) {
                    if (storyItem.getType() == 0) {
                        shareLocalImage(storyItem.getFilePath());
                    } else {
                        shareLocalVideo(storyItem.getFilePath());
                    }
                } else {
                    if (url.endsWith(".jpg")) {
                        if (bitmap != null) {
                            String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
                            Uri bitmapUri = Uri.parse(bitmapPath);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                            startActivity(Intent.createChooser(intent, "Share via..."));
                        } else {
                            ToastUtils.ErrorToast(getContext(), "Something went wrong.");
                        }
                    } else {
                        if (url.contains(".jpg") && isDownloadImagePost) {
                            if (bitmap != null) {
                                String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
                                Uri bitmapUri = Uri.parse(bitmapPath);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("image/png");
                                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                                startActivity(Intent.createChooser(intent, "Share via..."));
                            } else {
                                ToastUtils.ErrorToast(getContext(), "Something went wrong.");
                            }
                        } else
                            new ShareVid().execute(new String[0]);
                    }
                }
            }
        });

        int count = ZoomstaUtil.getIntegerPreference(getActivity(), "itemCount");
        count++;
        if (count < 3376)
            ZoomstaUtil.setIntegerPreference(getActivity(), count, "itemCount");
        else
            ZoomstaUtil.setIntegerPreference(getActivity(), 1, "itemCount");

        return view;
    }

    public void setStoryList(StoryModel story) {
        this.storyItem = story;
    }

    public void getStories(String url) {
        this.url = url;
    }

    public void setIsDownloadImagePost(Boolean isDownloadImagePost) {
        this.isDownloadImagePost = isDownloadImagePost;
    }


    private void setStory() {

        if (!isFromNet) {
            if (storyItem.getSaved()) {

                Glide.with(getActivity()).load(storyItem.getFilePath()).thumbnail(0.5f).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (wave.isShown())
                            wave.setVisibility(View.GONE);

                        return false;
                    }
                }).into(imageView);

                if (storyItem.getType() == 1) {
                    video.setVisibility(View.VISIBLE);
                    play.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(storyItem.getFilePath()));
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            play.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            videoView.start();
                        }
                    });

                }

//                delete.setVisibility(View.VISIBLE);

            }
        } else {
            Glide.with(getActivity()).asBitmap().load(url).thumbnail(0.5f).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    bitmap = resource;
                    imageView.setImageBitmap(bitmap);
                }
            });

            if (!url.endsWith(".jpg")) {
                if (url.contains(".jpg") && isDownloadImagePost) {
                } else {

                    vidUrl = InstaUtils.vids.get(url);
                    if (vidUrl == null) vidUrl = url;
                    video.setVisibility(View.VISIBLE);
                    play.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(vidUrl));
                    Log.d("playing_vid", "" + vidUrl);
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            play.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            videoView.start();
                        }
                    });
                }

            }

            save.setVisibility(View.VISIBLE);
        }

    }

    private void setupVideo() {
        videoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {

            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) {
                if (!play.isShown())
                    play.setVisibility(View.VISIBLE);

                if (!buttons.isShown())
                    buttons.setVisibility(View.VISIBLE);

                if (mediaController.isShowing())
                    mediaController.setVisibility(View.GONE);
            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) {
                if (play.isShown())
                    play.setVisibility(View.GONE);

                if (buttons.isShown())
                    buttons.setVisibility(View.VISIBLE);

                if (!mediaController.isShowing())
                    mediaController.setVisibility(View.VISIBLE);
            }


            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {
                if (!buttons.isShown())
                    buttons.setVisibility(View.VISIBLE);
            }


        });
    }

    private void saveImage(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "InstaDownload");
        if (!file.exists())
            file.mkdirs();

        String fileName = "InstaDownload-" + System.currentTimeMillis() + ".jpg";

        File newImage = new File(file, fileName);
        if (newImage.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(newImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();


            Downloads downloads = new Downloads();
            downloads.setUser_id("");
            downloads.setPath(file.getAbsolutePath() + "/" + fileName);
            downloads.setUsername("");
            downloads.setType(0);
            downloads.setFilename(fileName);
            dataObjectRepositry.addDownloadedData(downloads);

            Toast.makeText(getActivity(), "Saving image...", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= 19) {
                MediaScannerConnection.scanFile(getActivity(), new String[]{newImage.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
            } else {
                getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(newImage)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDeleteClick(final File f) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View view2 = layoutInflaterAndroid.inflate(R.layout.item_dialog, null);
        builder.setView(view2);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        BoldTextView title = view2.findViewById(R.id.titleText);
        RegularTextView descriptionsText = view2.findViewById(R.id.descriptionText);
        title.setText("Delete");
        descriptionsText.setText("Are you sure you wanna delete this file?");

        view2.findViewById(R.id.yes).setOnClickListener(v1 -> {
            if (Build.VERSION.SDK_INT >= 19) {
                MediaScannerConnection.scanFile(getActivity(), new String[]{f.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
            } else {
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_REMOVED, Uri.fromFile(f)));
            }

            f.delete();
            alertDialog.dismiss();

            storyListener.deletePage(true);

            if (context != null)
                ToastUtils.SuccessToast(context, "File deleted successfully");

        });
        view2.findViewById(R.id.no).setOnClickListener(v12 -> alertDialog.dismiss());


    }

    private void shareLocalImage(String path) {
        Uri bitmapUri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(intent, "Share via..."));
    }

    private void shareLocalVideo(String path) {
        Uri uri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share via..."));
    }

    private void repostLocalImage(String path) {
        Uri bitmapUri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

        intent.setPackage("com.instagram.android");
        startActivity(Intent.createChooser(intent, "Share via..."));
    }

    private void repostLocalVideo(String path) {
        Uri uri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setPackage("com.instagram.android");
        startActivity(Intent.createChooser(intent, "Share via..."));
    }

    private static void isFromNet(Boolean val) {
        if (val == null) isFromNet = false;
        else
            isFromNet = val;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView.isPlaying())
            videoView.stopPlayback();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFromNet == null)
            isFromNet(false);

        if (!isFromNet) {
            if (storyItem.getType() == 1 && !videoView.isPlaying())
                play.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (videoView.isPlaying())
            videoView.stopPlayback();


    }


    private class saveVideo extends AsyncTask<String, Integer, List<String>> {
        File newVid;
        String url;
        ProgressDialog progressDialog;

        private saveVideo(String url) {
            this.url = url;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Downloading video...");
            progressDialog.setProgressStyle(1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(100);
            progressDialog.show();
            //Toast.makeText(getActivity(), "Downloading video....", Toast.LENGTH_SHORT).show();
        }

        protected List<String> doInBackground(String... args) {
            String fileName = GlobalConstant.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".mp4";

            File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + GlobalConstant.SAVED_FILE_NAME);
            if (storyItem == null) storyItem = new StoryModel();
            storyItem.setFilePath(file.getPath());
            storyItem.setFileName(fileName);
            if (!file.exists()) file.mkdirs();


            Downloads downloads = new Downloads();
            downloads.setUser_id("");
            downloads.setPath(file.getAbsolutePath() + "/" + fileName);
            downloads.setUsername("");
            downloads.setType(1);
            downloads.setFilename(fileName);
            dataObjectRepositry.addDownloadedData(downloads);

            newVid = new File(file, fileName);
            if (!newVid.exists())
                try {
                    newVid.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                int lenghtOfFile = connection.getContentLength();
                FileOutputStream out = new FileOutputStream(newVid);
                InputStream is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                long total = 0;
                while (true) {
                    int len1 = is.read(buffer);
                    total += (long) len1;
                    publishProgress(new Integer[]{Integer.valueOf((int) ((100 * total) / ((long) lenghtOfFile)))});
                    if (len1 == -1) {
                        break;
                    }
                    out.write(buffer, 0, len1);
                }
                out.close();
                is.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0].intValue());
        }

        protected void onPostExecute(List<String> list) {
            progressDialog.dismiss();
            try {
                Toast.makeText(getActivity(), "Video saved", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(getActivity(), new String[]{newVid.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
                } else {
                    getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(newVid)));
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error downloading video. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getLocalBitmapUri() {

        if (Environment.getExternalStorageState().equals("mounted")) {
            this.cachedFile = new File(getActivity().getExternalCacheDir() + "/.data");
        } else {
            this.cachedFile = new File(getActivity().getCacheDir() + "/.data");
        }

        if (!this.cachedFile.exists()) {
            this.cachedFile.mkdir();
        }
        File file = new File(this.cachedFile.getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".mp4");
        file.getParentFile().mkdirs();
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            InputStream is = (InputStream) new URL(vidUrl).getContent();
            byte[] buffer = new byte[1024];
            while (true) {
                int len1 = is.read(buffer);
                if (len1 == -1) {
                    out.close();
                    is.close();
                    return Uri.fromFile(file);
                }
                out.write(buffer, 0, len1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private class ShareVid extends AsyncTask<String, String, List<String>> {
        ProgressDialog pd;
        Uri uri;

        private ShareVid() {
        }

        protected void onPreExecute() {
            this.pd = new ProgressDialog(getActivity());
            this.pd.setMessage("Sharing the video");
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.show();
            super.onPreExecute();
        }

        protected List<String> doInBackground(String... args) {
            try {
                this.uri = getLocalBitmapUri();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(List<String> list) {
            this.pd.dismiss();
            try {
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, this.uri);
                getActivity().startActivity(Intent.createChooser(shareIntent, "Share video"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RepostVideo extends AsyncTask<String, String, List<String>> {
        ProgressDialog pd;
        Uri uri;

        private RepostVideo() {
        }

        protected void onPreExecute() {
            this.pd = new ProgressDialog(getActivity());
            this.pd.setMessage("Sharing the video");
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.show();
            super.onPreExecute();
        }

        protected List<String> doInBackground(String... args) {
            try {
                this.uri = getLocalBitmapUri();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(List<String> list) {
            this.pd.dismiss();
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, this.uri);

                intent.setPackage("com.instagram.android");
                startActivity(Intent.createChooser(intent, "Share via..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
