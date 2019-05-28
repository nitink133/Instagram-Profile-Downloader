package in.blackpaper.instasp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.tonyodev.fetch2.Download;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.adapter.StoriesOverViewAdapter;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.room.tables.Downloads;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.models.StoryModel;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;

public class DownloadHistoryActivity extends BaseActivity {
    private TextView username;
    private RecyclerView recyclerView;
    private ArrayList<String> modelList;
    private ArrayList<StoryModel> stories;
    private DataObjectRepositry dataObjectRepositry;
    private StoriesOverViewAdapter adapter;
    private UserObject user;
    private String name, id;
    private LinearLayout noNet, noStories;
    private SpinKitView wave;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_history);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;

        recyclerView = findViewById(R.id.stories_overview_rv);
        noNet = findViewById(R.id.no_net_overview_stories);
        wave = findViewById(R.id.loading_stories_overview);
        noStories = findViewById(R.id.no_stories_found);
        back = findViewById(R.id.back);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList = new ArrayList<>();
        stories = new ArrayList<>();
        adapter = new StoriesOverViewAdapter(this, modelList, stories);
        recyclerView.setAdapter(adapter);
        setDownloades();

        back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    public void setDownloades() {

        LiveData<List<Downloads>> loggedInUsers = dataObjectRepositry.getAllDownloads();
        loggedInUsers.observe(DownloadHistoryActivity.this, new Observer<List<Downloads>>() {
            @Override
            public void onChanged(List<Downloads> downloads) {
                if (downloads.size() > 0) {
                    for (Downloads d : downloads) {
                        modelList.add(d.getPath());
                        StoryModel storyModel = new StoryModel();
                        storyModel.setFileName(d.getFilename());
                        storyModel.setFilePath(d.getPath());
                        storyModel.setSaved(true);
                        storyModel.setType(d.getType());
                        stories.add(storyModel);

                    }
                    adapter.notifyDataSetChanged();

                } else {
                    ToastUtils.ErrorToast(DownloadHistoryActivity.this, "No Downloads Found.");
                }
            }
        });

    }
}
