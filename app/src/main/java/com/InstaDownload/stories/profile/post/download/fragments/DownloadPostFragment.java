package com.InstaDownload.stories.profile.post.download.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.InstaDownload.stories.profile.post.download.GlobalConstant;
import com.InstaDownload.stories.profile.post.download.R;
import com.InstaDownload.stories.profile.post.download.activity.DownloadFromUrlActivity;
import com.InstaDownload.stories.profile.post.download.activity.DownloadHistoryActivity;
import com.InstaDownload.stories.profile.post.download.activity.HowToUseActivity;
import com.InstaDownload.stories.profile.post.download.activity.SearchActivity;
import com.InstaDownload.stories.profile.post.download.activity.ViewStoryActivity;
import com.InstaDownload.stories.profile.post.download.base.BaseFragment;
import com.InstaDownload.stories.profile.post.download.data.repositry.DataObjectRepositry;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Downloads;
import com.InstaDownload.stories.profile.post.download.models.StoryModel;
import com.InstaDownload.stories.profile.post.download.utils.ToastUtils;
import com.InstaDownload.stories.profile.post.download.view.RegularButton;
import com.InstaDownload.stories.profile.post.download.view.RegularEditText;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.tonyodev.fetch2.Fetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadPostFragment extends BaseFragment {
    private RegularButton download;
    private RegularEditText url;
    private Fetch fetch;
    private Context context;
    private boolean type;


    private AdView mAdView,adView1;
    public List<StoryModel> storyModels = new ArrayList<>();
    String pattern = "https://www.instagram.com/p/.";

    public DownloadPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_download_from_url, container, false);

        download = view.findViewById(R.id.download);
        url = view.findViewById(R.id.url);
        bannerAd(view);


        download.setOnClickListener(v -> {

            if (TextUtils.isEmpty(url.getText().toString())) {
                ToastUtils.ErrorToast(context, "Url field can't be empty");
            } else if (!URLUtil.isValidUrl(url.getText().toString())) {
                ToastUtils.ErrorToast(context, "Url is not valid url");
            } else if (checkURL(url.getText().toString())) {
                new ValidateFileFromURL().execute(url.getText().toString());
            } else {
                ToastUtils.ErrorToast(context, "Url is not valid url");
            }


        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

        if (adView1 != null) {
            adView1.resume();
        }

    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adView1 != null) {
            adView1.resume();
        }

        hideKeyboard();
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context != null)
            this.context = context;
    }

    public void bannerAd(View v){
        mAdView = (AdView) v.findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getActivity(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getActivity(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });


        mAdView.loadAd(adRequest);

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
            progressDialog = new ProgressDialog(context);
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
            if (!file.exists()) file.mkdirs();


            Downloads downloads = new Downloads();
            downloads.setUser_id("");
            downloads.setPath(file.getAbsolutePath() + "/" + fileName);
            downloads.setUsername("");
            downloads.setType(1);
            downloads.setFilename(fileName);
            DataObjectRepositry.dataObjectRepositry.addDownloadedData(downloads);

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
                Toast.makeText(context, "Video saved", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(context, new String[]{newVid.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
                } else {
                    context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(newVid)));
                }
            } catch (Exception e) {
                ToastUtils.ErrorToast(context, "Error downloading video. Please try again");
            }
        }
    }

    private class saveImage extends AsyncTask<Void, String, String> {

        String url = "";
        HttpURLConnection connection = null;
        private Bitmap bitmap;

        public saveImage(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());


                File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "Zoomsta");
                if (!file.exists())
                    file.mkdirs();

                String fileName = "Zoomsta-" + System.currentTimeMillis() + ".jpg";


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
                    DataObjectRepositry.dataObjectRepositry.addDownloadedData(downloads);

                    Toast.makeText(context, "Saving image...", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= 19) {
                        MediaScannerConnection.scanFile(context, new String[]{newImage.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                    } else {
                        context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(newImage)));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);


        }
    }

    /**
     * Background Async Task to check validate file and get URL
     */
    class ValidateFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            try {

                Document doc = Jsoup.connect(f_url[0]).get();
                String html = doc.toString();

                type = false;

                //for caption
                int indexcaption = html.indexOf("edge_media_to_caption");
                indexcaption += 48;

                int startCaption = html.indexOf("\"", indexcaption);
                startCaption += 1;
                int endCaption = html.indexOf("\"", startCaption);

                String strCaption = null;
                strCaption = html.substring(startCaption, endCaption);

                //setting caption flag=0 for caption flag=1 for vid flag=2 for image
                publishProgress("0", strCaption);

                //for video
                int indexVid = html.indexOf("\"video_url\"");
                indexVid += 11;
                int startVid = html.indexOf("\"", indexVid);
                startVid += 1;
                int endVid = html.indexOf("\"", startVid);

                String urlVid = null;
                urlVid = html.substring(startVid, endVid);

                if (!urlVid.equalsIgnoreCase("en")) {
                    // it is a vid show play btn
                    type = true;
                }

                //for image url
                int index = html.indexOf("display_url");
                index += 13;
                int start = html.indexOf("\"", index);
                start += 1;
                int end = html.indexOf("\"", start);
                //                System.out.println("start:"+start+ "end:"+ end);
                String urlImage = html.substring(start, end);


//                Bitmap mIcon11 = null;
//                try {
//                    InputStream in = new java.net.URL(urlImage).openStream();
//                    mIcon11 = BitmapFactory.decodeStream(in);
//                } catch (Exception e) {
//                    Log.e("Error", e.getMessage());
//                    e.printStackTrace();
//                }
//                return mIcon11;
                return urlImage;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute(String image) {

            progressDialog.dismiss();
            new downloadFIle(url.getText().toString()).execute();

        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        hideKeyboard();
    }

    boolean checkURL(String url) {

        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(url);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            return true;
        } else {
            System.out.println("NO MATCH");
            return false;
        }
    }

    private class downloadFIle extends AsyncTask<String, Integer, String> {
        String urlString;
        ProgressDialog progressDialog;

        private downloadFIle(String url) {
            this.urlString = url;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Downloading ...");
            progressDialog.setProgressStyle(1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(100);
            progressDialog.show();
            //Toast.makeText(getActivity(), "Downloading video....", Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(String... args) {

            int count;
            type = false;
            try {
                String strCaption = null;

                if(urlString==null)urlString="";
                Document doc = Jsoup.connect(urlString).get();
                URL url = null;
                String html = doc.toString();
                String urlVid = null;

                //for video
                int indexVid = html.indexOf("\"video_url\"");
                indexVid += 11;
                int startVid = html.indexOf("\"", indexVid);
                startVid += 1;
                int endVid = html.indexOf("\"", startVid);

                urlVid = html.substring(startVid, endVid);

                if (urlVid.equalsIgnoreCase("en")) {
                    //
                    //	url = new URL(urlVid);
                    //	type =false;
                    //}else {
                    // for image url

                    int index = html.indexOf("display_url");
                    index += 13;
                    int start = html.indexOf("\"", index);
                    start += 1;
                    int end = html.indexOf("\"", start);
                    //                System.out.println("start:"+start+ "end:"+ end);
                    String urlImage = html.substring(start, end);
                    type = false;
                    url = new URL(urlImage);

                } else {

                    url = new URL(urlVid);
                    type = true;
                }

                // true is for video and false is image


                //for caption
                int indexcaption = html.indexOf("edge_media_to_caption");
                indexcaption += 53;

                int startCaption = html.indexOf("\"", indexcaption);
                startCaption += 1;
                int endCaption = html.indexOf("\"", startCaption);

                strCaption = html.substring(startCaption, endCaption);


                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //generate a unique name

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
                //File myFile = null;


                // Output stream to write file

                File direct = new File(Environment.getExternalStorageDirectory() + "/" + GlobalConstant.SAVED_FILE_NAME);

                if (!direct.exists()) {
                    direct = new File(Environment.getExternalStorageDirectory() + "/" + GlobalConstant.SAVED_FILE_NAME);
                    direct.mkdirs();
                }

                String fileName = null;
                if (!type) {
                    fileName = GlobalConstant.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".jpg";
                } else {

                    fileName = GlobalConstant.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".mp4";
                }

                File file = new File(direct, fileName);
                if (file.exists()) {
                    file.delete();
                }

                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(Integer.valueOf("" + ((total * 100) / lenghtOfFile)));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                // add image into the database


                Downloads downloads = new Downloads();
                downloads.setUser_id("");
                downloads.setPath(file.getAbsolutePath());
                downloads.setUsername("");
                if (type)
                    downloads.setType(1);
                else
                    downloads.setType(0);
                downloads.setFilename(fileName);
                if (storyModels != null) storyModels.clear();
                else storyModels = new ArrayList<>();


                StoryModel storyModel = new StoryModel();
                storyModel.setFileName(fileName);
                storyModel.setFilePath(downloads.getPath());
                storyModel.setId(downloads.getId());
                storyModel.setType(downloads.getType());
                storyModel.setSaved(true);
                storyModels.add(storyModel);

                DataObjectRepositry.dataObjectRepositry.addDownloadedData(downloads);

                return file.getAbsolutePath();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0].intValue());
        }

        protected void onPostExecute(String string) {

            progressDialog.dismiss();

            if (storyModels != null && storyModels.size() > 0) {
                Intent intent = new Intent(context, ViewStoryActivity.class);
                intent.putExtra("isFromNet", false);
                intent.putParcelableArrayListExtra("storylist", (ArrayList<StoryModel>) storyModels);
                intent.putExtra("pos", 0);
                intent.putExtra("isAlreadyDownloaded", true);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
            } else {
                ToastUtils.ErrorToast(context, "Something went wrong.");
            }

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_history:
                Intent intent = new Intent(getActivity(), DownloadHistoryActivity.class);
                startActivity(intent);

                return true;
            case R.id.information:
                Intent intent1 = new Intent(getActivity(), HowToUseActivity.class);
                startActivity(intent1);

                return true;
            default:
                break;
        }

        return false;
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Downloading ...");
            progressDialog.setProgressStyle(1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(100);
            progressDialog.show();

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            type = false;
            try {
                String strCaption = null;

                Document doc = Jsoup.connect(f_url[0]).get();
                URL url = null;
                String html = doc.toString();
                String urlVid = null;

                //for video
                int indexVid = html.indexOf("\"video_url\"");
                indexVid += 11;
                int startVid = html.indexOf("\"", indexVid);
                startVid += 1;
                int endVid = html.indexOf("\"", startVid);

                urlVid = html.substring(startVid, endVid);

                if (urlVid.equalsIgnoreCase("en")) {
                    //
                    //	url = new URL(urlVid);
                    //	type =false;
                    //}else {
                    // for image url

                    int index = html.indexOf("display_url");
                    index += 13;
                    int start = html.indexOf("\"", index);
                    start += 1;
                    int end = html.indexOf("\"", start);
                    //                System.out.println("start:"+start+ "end:"+ end);
                    String urlImage = html.substring(start, end);
                    type = false;
                    url = new URL(urlImage);

                } else {

                    url = new URL(urlVid);
                    type = true;
                }

                // true is for video and false is image


                //for caption
                int indexcaption = html.indexOf("edge_media_to_caption");
                indexcaption += 53;

                int startCaption = html.indexOf("\"", indexcaption);
                startCaption += 1;
                int endCaption = html.indexOf("\"", startCaption);

                strCaption = html.substring(startCaption, endCaption);


                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //generate a unique name

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
                //File myFile = null;


                // Output stream to write file

                File direct = new File(Environment.getExternalStorageDirectory() + "/" + GlobalConstant.SAVED_FILE_NAME);

                if (!direct.exists()) {
                    direct = new File(Environment.getExternalStorageDirectory() + "/" + GlobalConstant.SAVED_FILE_NAME);
                    direct.mkdirs();
                }

                String fileName = null;
                if (!type) {
                    fileName = GlobalConstant.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".jpg";
                } else {

                    fileName = GlobalConstant.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".mp4";
                }

                File file = new File(direct, fileName);
                if (file.exists()) {
                    file.delete();
                }

                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                // add image into the database


                Downloads downloads = new Downloads();
                downloads.setUser_id("");
                downloads.setPath(file.getAbsolutePath());
                downloads.setUsername("");
                if (type)
                    downloads.setType(1);
                else
                    downloads.setType(0);
                downloads.setFilename(fileName);
                if (storyModels != null) storyModels.clear();
                else storyModels = new ArrayList<>();


                StoryModel storyModel = new StoryModel();
                storyModel.setFileName(fileName);
                storyModel.setFilePath(downloads.getPath());
                storyModel.setId(downloads.getId());
                storyModel.setType(downloads.getType());
                storyModel.setSaved(true);
                storyModels.add(storyModel);

                DataObjectRepositry.dataObjectRepositry.addDownloadedData(downloads);

                return file.getAbsolutePath();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return null;
        }


        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(String file_url) {

            progressDialog.dismiss();

            if (storyModels != null && storyModels.size() > 0) {
                Intent intent = new Intent(context, ViewStoryActivity.class);
                intent.putExtra("isFromNet", false);
                intent.putParcelableArrayListExtra("storylist", (ArrayList<StoryModel>) storyModels);
                intent.putExtra("pos", 0);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
            } else {
                ToastUtils.ErrorToast(context, "Something went wrong.");
            }


        }
    }

}
