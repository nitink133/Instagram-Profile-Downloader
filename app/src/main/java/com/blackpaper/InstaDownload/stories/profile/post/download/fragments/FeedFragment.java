package com.blackpaper.InstaDownload.stories.profile.post.download.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import cz.msebera.android.httpclient.util.TextUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.ApiUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.GlobalConstant;
import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.adapter.FeedAdapter;
import com.blackpaper.InstaDownload.stories.profile.post.download.base.BaseFragment;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.prefs.PreferencesManager;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.blackpaper.InstaDownload.stories.profile.post.download.view.BoldTextView;

public class FeedFragment extends BaseFragment {

    private OnFeedFragmentInteractionListener mListener;
    private IntagramProfileResponse.User user = null;
    private RecyclerView recyclerView, storiesRecyclerView;
    FeedAdapter feedAdapter;
    private Context context;
    private BoldTextView noDataText;
    String username = "";
    List<IntagramProfileResponse.Edge> edgeList;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            username = getArguments().getString(GlobalConstant.USERNAME);
            edgeList = getArguments().getParcelableArrayList("feed_list");
            if(edgeList==null){
                edgeList = new ArrayList<>();
            }

        }else{
            edgeList  = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView);
        noDataText = view.findViewById(R.id.noDataText);

        feedAdapter = new FeedAdapter(getActivity(),recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(feedAdapter);
        if (edgeList != null && edgeList.size() > 0)
            feedAdapter.setEdges(edgeList);


        if (!TextUtils.isEmpty(username)) {
            showLoading();
            new RequestInstagramAPI(ApiUtils.getUsernameUrl(username)).execute();
        } else {
            noDataText.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFeedFragmentInteractionListener) {
            mListener = (OnFeedFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFeedFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFeedFragmentInteractionListener {
    }


    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        String url = "";

        public RequestInstagramAPI(String url) {
            this.url = url + "?__a=1";
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            hideLoading();
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    IntagramProfileResponse intagramProfileResponse = new Gson().fromJson(response, IntagramProfileResponse.class);
                    if (intagramProfileResponse.getGraphql() != null && intagramProfileResponse.getGraphql().getUser() != null) {
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC,intagramProfileResponse.getGraphql().getUser().getProfilePicUrl());
                        PreferencesManager.savePref(GlobalConstant.FULL_NAME,intagramProfileResponse.getGraphql().getUser().getFullName());
                        PreferencesManager.savePref(GlobalConstant.BIO,intagramProfileResponse.getGraphql().getUser().getBiography());
                        PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY,intagramProfileResponse.getGraphql().getUser().getFollowedByViewer());
                        PreferencesManager.savePref(GlobalConstant.FOLLOWS,intagramProfileResponse.getGraphql().getUser().getFollowsViewer());
                        IntagramProfileResponse.User user = intagramProfileResponse.getGraphql().getUser();
                        if (user != null && user.getEdgeOwnerToTimelineMedia() != null && user.getEdgeOwnerToTimelineMedia().getEdges() != null) {
                            edgeList = user.getEdgeOwnerToTimelineMedia().getEdges();
                        }
                        if (edgeList.size() > 0) {
                            feedAdapter.setEdges(edgeList);
                            noDataText.setVisibility(View.GONE);
                        } else {
                            noDataText.setVisibility(View.VISIBLE);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(context, getString(R.string.some_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
