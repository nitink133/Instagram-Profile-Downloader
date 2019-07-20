package com.storyPost.PhotoVideoDownloader.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.StoriesOverViewAdapter;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * Created by tirgei on 11/4/17.
 */

public class StoriesOverview extends DialogFragment {
    private TextView username;
    private RecyclerView recyclerView;
    private ArrayList<String> modelList;
    private ArrayList<StoryModel> stories;
    private StoriesOverViewAdapter adapter;
    private UserObject user;
    private String name, id;
    private LinearLayout noNet, noStories;
    private SpinKitView wave;
    private ImageView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    private AdView mAdView;
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

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

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stories_overview, container, false);

        username = view.findViewById(R.id.stories_overview_username);
        recyclerView = view.findViewById(R.id.stories_overview_rv);
        noNet = view.findViewById(R.id.no_net_overview_stories);
        wave = view.findViewById(R.id.loading_stories_overview);
        noStories = view.findViewById(R.id.no_stories_found);
        back = view.findViewById(R.id.back);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList = new ArrayList<>();
        stories = new ArrayList<>();
        adapter = new StoriesOverViewAdapter(getActivity(), modelList, stories);
        recyclerView.setAdapter(adapter);
        bannerAd(view);

        name = getArguments().getString("username");
        id = getArguments().getString("user_id");
        username.setText(name);

        int count = ZoomstaUtil.getIntegerPreference(getActivity(), "clickCount");
        count++;
        if(count < 6562)
            ZoomstaUtil.setIntegerPreference(getActivity(), count, "clickCount");
        else
            ZoomstaUtil.setIntegerPreference(getActivity(), 1, "clickCount");
        setStories();


        back.setOnClickListener(v->{
            dismiss();
        });

        return view;
    }

    private void setStories(){
        if(ZoomstaUtil.haveNetworkConnection(getActivity())){
            new GetStoriesFeed().execute(new String[0]);
        } else {
            noNet.setVisibility(View.VISIBLE);
        }

    }

    private class GetStoriesFeed extends AsyncTask<String, String, String> {
        private String response;

        private GetStoriesFeed(){}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wave.setVisibility(View.VISIBLE);
            if(noNet.isShown())
                noNet.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... args) {
            try{
                modelList.addAll(InstaUtils.stories(id, getActivity()));
                stories.addAll(InstaUtils.fetchStories(id, getActivity()));
            } catch (Exception e){
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            wave.setVisibility(View.GONE);

            if(modelList.size() == 0)
                noStories.setVisibility(View.VISIBLE);
            else
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}