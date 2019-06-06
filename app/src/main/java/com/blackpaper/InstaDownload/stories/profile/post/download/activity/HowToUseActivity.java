package com.blackpaper.InstaDownload.stories.profile.post.download.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.blackpaper.InstaDownload.stories.profile.post.download.R;

public class HowToUseActivity extends AppCompatActivity {
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}
