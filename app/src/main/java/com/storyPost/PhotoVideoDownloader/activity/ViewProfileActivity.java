package com.storyPost.PhotoVideoDownloader.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Downloads;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;

public class ViewProfileActivity extends BaseActivity {
    private PhotoView imageView;
    private ImageButton save, share, delete, repost;
    private Bitmap bitmap;
    private LinearLayout buttons;
    private ImageButton back,download_history;
    private DataObjectRepositry dataObjectRepositry;
    private String username = "", userId = "";



    private AdView adView, mAdView;

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
        super.onDestroy();
    }

    public void bannerAd() {
        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(ViewProfileActivity.this, "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(ViewProfileActivity.this, "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;

        imageView = findViewById(R.id.main_imageview);
        download_history = findViewById(R.id.download_history);
        save = findViewById(R.id.button_save);
        share = findViewById(R.id.button_share);
        delete = findViewById(R.id.button_delete);
        repost = findViewById(R.id.repost);
        buttons = findViewById(R.id.button_options);
        back = findViewById(R.id.back);

        save.setVisibility(View.VISIBLE);
        bannerAd();

        try {
            bitmap = BitmapFactory.decodeStream(ViewProfileActivity.this.openFileInput("myImage"));
            Glide.with(this).load(bitmap).into(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (getIntent() != null) {
            userId = getIntent().getStringExtra("user_id");
            username = getIntent().getStringExtra("username");

        }

        back.setOnClickListener(v -> {

            onBackPressed();

        });

        download_history.setOnClickListener(v->{
            startActivity(new Intent(this,DownloadHistoryActivity.class));
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

        repost.setOnClickListener(v -> {

            if (bitmap != null) {

                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
                if (bitmapPath != null) {
                    Uri bitmapUri = Uri.parse(bitmapPath);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

                    intent.setPackage("com.instagram.android");
                    startActivity(Intent.createChooser(intent, "Share via..."));
                } else {
                    ToastUtils.ErrorToast(ViewProfileActivity.this, "Please download image first.");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(ViewProfileActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
//                                delete.setVisibility(View.VISIBLE);
                                saveImage(bitmap);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                                        .withContext(ViewProfileActivity.this)
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
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
                if (bitmapPath != null) {
                    Uri bitmapUri = Uri.parse(bitmapPath);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    startActivity(Intent.createChooser(intent, "Share via..."));
                } else {
                    ToastUtils.ErrorToast(ViewProfileActivity.this, "Something went wrong!");
                }
            }
        });


    }

    private void saveImage(Bitmap bitmap) {
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
            dataObjectRepositry.addDownloadedData(downloads);
            Toast.makeText(this, "Saving image...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean verificaInstagram() {
        boolean installed = false;

        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    private void shareInstagram(File filex) {
        hideLoading();

        if (verificaInstagram()) {


            Uri uri = Uri.parse("file://" + filex.toString());
            Intent i = new Intent();
            i.setAction(android.content.Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.setType("image/*");
            i.setPackage("com.instagram.android");
            startActivity(i);

        } else {
            ToastUtils.ErrorToast(context, "Instagram have not been installed.");
        }
    }

    public void repostImage(Bitmap bitmap) {
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
            downloads.setType(0);

            downloads.setFilename(fileName);
            dataObjectRepositry.addDownloadedData(downloads);

            shareInstagram(newImage);
            Toast.makeText(this, "Saving image...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
