package in.blackpaper.instasp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.adapter.StoriesOverViewAdapter;
import in.blackpaper.instasp.models.StoryModel;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stories_overview, container, false);

        username = view.findViewById(R.id.stories_overview_username);
        recyclerView = view.findViewById(R.id.stories_overview_rv);
        noNet = view.findViewById(R.id.no_net_overview_stories);
        wave = view.findViewById(R.id.loading_stories_overview);
        noStories = view.findViewById(R.id.no_stories_found);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList = new ArrayList<>();
        stories = new ArrayList<>();
        adapter = new StoriesOverViewAdapter(getActivity(), modelList, stories);
        recyclerView.setAdapter(adapter);

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
