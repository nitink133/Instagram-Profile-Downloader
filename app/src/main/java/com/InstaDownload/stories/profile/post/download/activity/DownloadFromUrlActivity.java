package com.InstaDownload.stories.profile.post.download.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.InstaDownload.stories.profile.post.download.GlobalConstant;
import com.InstaDownload.stories.profile.post.download.R;
import com.InstaDownload.stories.profile.post.download.data.repositry.DataObjectRepositry;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Downloads;
import com.InstaDownload.stories.profile.post.download.fragments.StoryFragment;
import com.InstaDownload.stories.profile.post.download.models.StoryModel;
import com.InstaDownload.stories.profile.post.download.utils.ToastUtils;
import com.InstaDownload.stories.profile.post.download.utils.ZoomstaUtil;
import com.InstaDownload.stories.profile.post.download.view.RegularButton;
import com.InstaDownload.stories.profile.post.download.view.RegularEditText;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadFromUrlActivity extends AppCompatActivity {
    private RegularButton download;
    private RegularEditText url;
    private Fetch fetch;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_from_url);
        context = this;
        download = findViewById(R.id.download);
        url = findViewById(R.id.url);
        bannerAd();


        download.setOnClickListener(v -> {

            if (TextUtils.isEmpty(url.getText().toString())) {
                ToastUtils.ErrorToast(context, "Url field can't be empty");
            } else if (URLUtil.isValidUrl(url.getText().toString())) {
                ToastUtils.ErrorToast(context, "Url is not valid url");
            } else if (URLUtil.isFileUrl(url.getText().toString())) {
                String _url = url.getText().toString();
                if (_url.endsWith(".jpg")) {
                    new saveImage(_url).execute();
                } else {
                    new saveVideo(_url).execute(new String[0]);
                }
            }


        });


    }

    private AdView adView,mAdView;
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (adView != null) {
            adView.resume();
        }

    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adView != null) {
            adView.resume();
        }
        super.onDestroy();
    }

    public void bannerAd(){
        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .build();


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(DownloadFromUrlActivity.this, "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(DownloadFromUrlActivity.this, "Ad left application!", Toast.LENGTH_SHORT).show();
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
}
