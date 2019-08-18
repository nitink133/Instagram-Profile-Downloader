package com.storyPost.PhotoVideoDownloader.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.snatik.storage.Storage;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.DownloadHistoryActivity;
import com.storyPost.PhotoVideoDownloader.adapter.DownloadStoriesoverViewAdapter;
import com.storyPost.PhotoVideoDownloader.base.BaseFragment;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Downloads;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.utils.RecyclerItemClickListener;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.view.BoldTextView;
import com.storyPost.PhotoVideoDownloader.view.RegularTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DownloadHistoryPhotoFragment extends BaseFragment {

    private ArrayList<Object> modelList = new ArrayList<>();
    private ArrayList<String> modelListCopy = new ArrayList<>();
    private ArrayList<StoryModel> stories = new ArrayList<>();

    ArrayList<StoryModel> selected_usersList = new ArrayList<>();

    private RecyclerView recyclerView;
    private DownloadStoriesoverViewAdapter adapter;
    private LinearLayout noNet, noStories;
    private RegularTextView no_downloads;
    private Context context;

    boolean isMultiSelect = false;
    private DataObjectRepositry dataObjectRepositry;
    private Storage storage;
    private SpinKitView wave;
    int spaceBetweenAds = 7;

    public DownloadHistoryPhotoFragment() {
        // Required empty public constructor
    }


    public static DownloadHistoryPhotoFragment init(ArrayList<String> modelList, ArrayList<StoryModel> stories) {
        DownloadHistoryPhotoFragment downloadHistoryPhotoFragment1 = new DownloadHistoryPhotoFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("modelList", modelList);
        args.putParcelableArrayList("stories", stories);
        downloadHistoryPhotoFragment1.setArguments(args);
        return downloadHistoryPhotoFragment1;
    }


    public interface EventListener {
        void multi_select(int position);

        void updateActionBar(boolean isVisible, int count);

        void isPhotoFragmentVisible(boolean isVisible);
    }

    public void setEventListener(DownloadHistoryPhotoFragment.EventListener eventListener) {
        this.eventListener = eventListener;
    }

    private DownloadHistoryPhotoFragment.EventListener eventListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_history_photo, container, false);
        init(view);
        setDownloades();
        loadNativeAds();
        return view;
    }

    public void init(View view) {
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        storage = new Storage(context);

        no_downloads = view.findViewById(R.id.no_downloads);
        wave = view.findViewById(R.id.loading_stories_overview);


        recyclerView = view.findViewById(R.id.stories_overview_rv);
        noNet = view.findViewById(R.id.no_net_overview_stories);

        noStories = view.findViewById(R.id.no_stories_found);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        adapter = new DownloadStoriesoverViewAdapter(context, spaceBetweenAds);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                }
//                else
//                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selected_usersList = new ArrayList<StoryModel>();
                    isMultiSelect = true;

                    if (eventListener != null)
                        eventListener.updateActionBar(true, -1);


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


            if (eventListener != null)
                eventListener.updateActionBar(true, selected_usersList.size());
        } else {


            if (eventListener != null)
                eventListener.updateActionBar(false, -1);
        }

        refreshAdapter();

    }

    /**
     * This method will called at first time viewpager created and when we switch between each page
     * NOT called when we go to background or another activity (fragment) when we go back
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isResumed()) { // fragment have created
            if (eventListener != null)
                eventListener.isPhotoFragmentVisible(isVisibleToUser);
        }
    }


    public void refreshAdapter() {
        adapter.selected_usersList = selected_usersList;
        adapter.addAll(modelList, stories, modelListCopy);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
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
                    if (eventListener != null)
                        eventListener.updateActionBar(false, -1);
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isMultiSelect = false;
            selected_usersList = new ArrayList<StoryModel>();
            refreshAdapter();
        }
    };

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
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
                modelListCopy.clear();
                for (int i = 0; i < stories.size(); i++) {
                    modelList.add(stories.get(i).getFilePath());
                    modelListCopy.add(stories.get(i).getFilePath());
                }


                selected_usersList.clear();
                adapter.selected_usersList.clear();
                refreshAdapter();
                if (eventListener != null)
                    eventListener.updateActionBar(false, -1);
            }


            alertDialog.dismiss();

        });
        view2.findViewById(R.id.no).

                setOnClickListener(v12 -> alertDialog.dismiss());

    }


    public void setDownloades() {

        LiveData<List<Downloads>> loggedInUsers = dataObjectRepositry.getAllDownloads();
        loggedInUsers.observe(getActivity(), new Observer<List<Downloads>>() {
            @Override
            public void onChanged(List<Downloads> downloads) {
                if (downloads.size() > 0) {
                    modelListCopy.clear();
                    modelList.clear();
                    stories.clear();
                    for (Downloads d : downloads) {
                        if (d.getType() == 0) {
                            if(new File(d.getPath()).exists()) {
                                modelList.add(d.getPath());
                                modelListCopy.add(d.getPath());
                                StoryModel storyModel = new StoryModel();
                                storyModel.setFileName(d.getFilename());
                                storyModel.setFilePath(d.getPath());
                                storyModel.setSaved(true);
                                storyModel.setType(d.getType());
                                storyModel.setId(d.getId());
                                stories.add(storyModel);
                            }else dataObjectRepositry.deleteDownloadedData(d.getId());
                        }


                    }
                    Collections.reverse(modelList);
                    Collections.reverse(stories);
                    Collections.reverse(modelListCopy);
                    adapter.addAll(modelList, stories, modelListCopy);
                    adLoader.loadAds(new AdRequest.Builder().build(), modelList.size() / spaceBetweenAds + 1);

                } else {
                    no_downloads.setVisibility(View.VISIBLE);

                }

                if (modelListCopy.size() == 0) {
                    no_downloads.setVisibility(View.VISIBLE);

                } else no_downloads.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    // The AdLoader used to load ads.
    AdLoader adLoader;
    List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    private void loadNativeAds() {

        AdLoader.Builder builder = new AdLoader.Builder(context, getString(R.string.native_ad_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // Load the Native Express ad.
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }
        List<UnifiedNativeAd> nativeAdList = new ArrayList<>();
        nativeAdList.addAll(mNativeAds);
        nativeAdList.remove(mNativeAds.size() - 1);

        for (UnifiedNativeAd ad : nativeAdList) {
            modelList.add(spaceBetweenAds, ad);
        }
        modelList.add(mNativeAds.get(mNativeAds.size() - 1));

        adapter.addAll(modelList, stories, modelListCopy);
    }
    public void setMargins( ) {
        try {

            if (recyclerView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                recyclerView.requestLayout();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public static int pxToDp(int px) {
//        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
//    }
//
//    public static int dpToPx(int dp) {
//        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
//    }
}