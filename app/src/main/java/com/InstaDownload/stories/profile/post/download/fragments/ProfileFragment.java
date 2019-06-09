package com.InstaDownload.stories.profile.post.download.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import cz.msebera.android.httpclient.util.TextUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import com.InstaDownload.stories.profile.post.download.ApiUtils;
import com.InstaDownload.stories.profile.post.download.GlobalConstant;
import com.InstaDownload.stories.profile.post.download.R;
import com.InstaDownload.stories.profile.post.download.adapter.UserProfileAdapter;
import com.InstaDownload.stories.profile.post.download.base.BaseFragment;
import com.InstaDownload.stories.profile.post.download.data.prefs.PreferencesManager;
import com.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.InstaDownload.stories.profile.post.download.view.BoldTextView;
import com.InstaDownload.stories.profile.post.download.view.RegularTextView;

public class ProfileFragment extends BaseFragment {
    RecyclerView recyclerView;
    BoldTextView noDataText;
    UserProfileAdapter userProfileAdapter;

    List<IntagramProfileResponse.Edge> edgeList;
    String username = "";
    private CircleImageView profileImageView;
    String fullName, bio, profile_image;
    int media, follows, followed_by;
    BoldTextView posts, followedBy, followsText, fullNameText;
    RegularTextView bioText;
    private OnProfileFragmentInteractionListener mListener;
    Context context;

    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profile_image = PreferencesManager.getPref(GlobalConstant.PROFILE_PIC);
        fullName = PreferencesManager.getPref(GlobalConstant.FULL_NAME);
        try {
            bio = PreferencesManager.getPref(GlobalConstant.BIO);
            media = PreferencesManager.getPref(GlobalConstant.MEDIA);
            follows = PreferencesManager.getPref(GlobalConstant.FOLLOWS);
            followed_by = PreferencesManager.getPref(GlobalConstant.FOLLOWED_BY);
        }catch (Exception e){
            bio = "";
            media = -1;
            follows= -1;
            followed_by = -1;
        }


        if (getArguments() != null) {
            username = getArguments().getString(GlobalConstant.USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noDataText = view.findViewById(R.id.noDataText);
        fullNameText = view.findViewById(R.id.fullName);
        profileImageView = view.findViewById(R.id.profileImageView);

        bioText = view.findViewById(R.id.bio);
        posts = view.findViewById(R.id.posts);
        followedBy = view.findViewById(R.id.followedBy);
        followsText = view.findViewById(R.id.follows);
        if (!TextUtils.isEmpty(fullName))
            fullNameText.setText(fullName);

        if (!TextUtils.isEmpty(bio))
            bioText.setText(bio);

        if (!TextUtils.isEmpty(String.valueOf(media)))
            posts.setText(String.valueOf(media));

        if (!TextUtils.isEmpty(String.valueOf(followed_by)))
            followedBy.setText(String.valueOf(followed_by));

        if (!TextUtils.isEmpty(String.valueOf(follows)))
            followsText.setText(String.valueOf(follows));

        if (!TextUtils.isEmpty(String.valueOf(profile_image)))
            Glide.with(context).load(profile_image).into(profileImageView);

        userProfileAdapter = new UserProfileAdapter(getContext(),recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(userProfileAdapter);
        if (edgeList != null && edgeList.size() > 0)
            userProfileAdapter.setEdges(edgeList);


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
        if (context instanceof OnProfileFragmentInteractionListener) {
            mListener = (OnProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnProfileFragmentInteractionListener {
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
                        IntagramProfileResponse.User user = intagramProfileResponse.getGraphql().getUser();
                        if (user != null && user.getEdgeOwnerToTimelineMedia() != null && user.getEdgeOwnerToTimelineMedia().getEdges() != null) {
                            edgeList = user.getEdgeOwnerToTimelineMedia().getEdges();
                        }
                        if (edgeList.size() > 0) {
                            userProfileAdapter.setEdges(edgeList);
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
