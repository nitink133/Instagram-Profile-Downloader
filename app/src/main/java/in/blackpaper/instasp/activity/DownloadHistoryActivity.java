package in.blackpaper.instasp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar;
import com.github.ybq.android.spinkit.SpinKitView;
import com.snatik.storage.Storage;
import com.tonyodev.fetch2.Download;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.adapter.DownloadStoriesoverViewAdapter;
import in.blackpaper.instasp.adapter.StoriesOverViewAdapter;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.room.tables.Downloads;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.models.StoryModel;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.AlertDialogHelper;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.RecyclerItemClickListener;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.Utility;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.BoldTextView;
import in.blackpaper.instasp.view.RegularTextView;

public class DownloadHistoryActivity extends BaseActivity {
    private TextView username;
    private RecyclerView recyclerView;
    private ArrayList<String> modelList;
    private ArrayList<StoryModel> stories;
    private DataObjectRepositry dataObjectRepositry;
    private DownloadStoriesoverViewAdapter adapter;
    private UserObject user;
    private String name, id;
    private LinearLayout noNet, noStories;
    private SpinKitView wave;
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    ArrayList<StoryModel> selected_usersList = new ArrayList<>();

    private ImageView back;
    private Toolbar toolbar;
    Storage storage;
    private Menu context_menu;
    private int selectedItem = 0;
    private RegularTextView title;
    private ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_history);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        storage = new Storage(getApplicationContext());
        recyclerView = findViewById(R.id.stories_overview_rv);
        noNet = findViewById(R.id.no_net_overview_stories);
        title = findViewById(R.id.title);
        wave = findViewById(R.id.loading_stories_overview);
        noStories = findViewById(R.id.no_stories_found);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList = new ArrayList<>();
        stories = new ArrayList<>();


        adapter = new DownloadStoriesoverViewAdapter(this, modelList, stories);
        recyclerView.setAdapter(adapter);
        setDownloades();

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        delete.setOnClickListener(v -> {
            showAlertDialog();
        });


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
                else
                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selected_usersList = new ArrayList<StoryModel>();
                    isMultiSelect = true;


                    delete.setVisibility(View.VISIBLE);

                }

                multi_select(position);

            }
        }));
// Add/Remove the item from/to the list


    }

    public void multi_select(int position) {
        if (selected_usersList.contains(stories.get(position)))
            selected_usersList.remove(stories.get(position));
        else
            selected_usersList.add(stories.get(position));

        if (selected_usersList.size() > 0) {


            delete.setVisibility(View.VISIBLE);
            title.setText(selected_usersList.size() + " items selected");
        } else {


            delete.setVisibility(View.GONE);
            title.setText("Download History");
        }

        refreshAdapter();

    }

    public void refreshAdapter() {
        adapter.selected_usersList = selected_usersList;
        adapter.storyModels = stories;
        adapter.modelList = modelList;
        adapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    showAlertDialog();
                    return true;
                default:
                    toolbar.setVisibility(View.VISIBLE);
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            selected_usersList = new ArrayList<StoryModel>();
            refreshAdapter();
        }
    };


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
                        storyModel.setId(d.getId());
                        stories.add(storyModel);

                    }
                    adapter.notifyDataSetChanged();

                } else {
                    ToastUtils.ErrorToast(DownloadHistoryActivity.this, "No Downloads Found.");
                }
            }
        });

    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view2 = layoutInflaterAndroid.inflate(R.layout.item_dialog, null);
        builder.setView(view2);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        BoldTextView _title = view2.findViewById(R.id.titleText);
        RegularTextView descriptionsText = view2.findViewById(R.id.descriptionText);
        _title.setText("Delete");
        descriptionsText.setText("Are you sure you wanna delete this file?");

        view2.findViewById(R.id.yes).setOnClickListener(v1 -> {
            if (selected_usersList.size() > 0) {
                for (int i = 0; i < selected_usersList.size(); i++) {
                    for (StoryModel storyModel : stories) {
                        if (storyModel.getId() == selected_usersList.get(i).getId()) {
                            stories.remove(storyModel);
                            break;
                        }
                    }
                    dataObjectRepositry.deleteDownloadedData(selected_usersList.get(i).getId());
                    storage.deleteFile(selected_usersList.get(i).getFilePath());
                }

                modelList.clear();
                for (int i = 0; i < stories.size(); i++) {
                    modelList.add(stories.get(i).getFilePath());
                }


                selected_usersList.clear();
                adapter.selected_usersList.clear();
                adapter.storyModels.clear();
                adapter.modelList.clear();

                adapter.storyModels.addAll(stories);
                adapter.modelList.addAll(modelList);

                adapter.notifyDataSetChanged();
                title.setText("Download History");
                delete.setVisibility(View.GONE);
            }


            alertDialog.dismiss();

        });
        view2.findViewById(R.id.no).

                setOnClickListener(v12 -> alertDialog.dismiss());

    }
}
