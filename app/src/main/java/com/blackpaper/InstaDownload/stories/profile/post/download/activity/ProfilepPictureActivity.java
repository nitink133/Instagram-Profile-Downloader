package com.blackpaper.InstaDownload.stories.profile.post.download.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.InstagramSearchUsernameRequest;
import dev.niekirk.com.instagram4android.requests.payload.InstagramSearchUsernameResult;

import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.ToastUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.view.RegularEditText;

public class ProfilepPictureActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RegularEditText searchText;
    private ImageButton back, search;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilep_picture);
        context = this;
        initUi();

        onClick();
    }

    public void initUi() {
        recyclerView = findViewById(R.id.recycler_view);
        searchText = findViewById(R.id.searchText);
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);

    }

    public void onClick() {
        search.setOnClickListener(v -> {
            if (TextUtils.isEmpty(searchText.getText().toString()))
                ToastUtils.ErrorToast(context, "Search field can't be empty");
            else {
                new RequestInstagramAPI("nitincodes", "Blackpaper13");
            }
        });
    }




    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {
        String username,password;
        public RequestInstagramAPI(String username,String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            Instagram4Android instagram = Instagram4Android.builder().username(username).password(password).build();
            try {
                InstagramSearchUsernameResult result = instagram.sendRequest(new InstagramSearchUsernameRequest(username));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {


            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.some_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


}
