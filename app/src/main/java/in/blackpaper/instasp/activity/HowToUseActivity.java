package in.blackpaper.instasp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import in.blackpaper.instasp.R;

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
