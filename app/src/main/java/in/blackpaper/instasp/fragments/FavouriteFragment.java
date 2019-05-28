package in.blackpaper.instasp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.ProfilepPictureActivity;
import in.blackpaper.instasp.adapter.FaveListAdapter;
import in.blackpaper.instasp.models.UserObject;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;


public class FavouriteFragment extends Fragment {

    private List<UserObject> userObjectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FaveListAdapter adapter;
    private LinearLayout noNet, noFaves;
    private ImageButton noNetRefresh;
    private SpinKitView wave;
    private SwipeRefreshLayout refreshLayout;
    private TextView text1, text2;
    private FloatingActionButton fab;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    private class GetStoriesFeed extends AsyncTask<Boolean, String, String> {
        private String response;

        private GetStoriesFeed(){}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!refreshLayout.isRefreshing())
                wave.setVisibility(View.VISIBLE);
            if(noNet.isShown())
                noNet.setVisibility(View.GONE);
            if(!userObjectList.isEmpty())
                userObjectList.clear();
            adapter.setUserObjects(userObjectList);
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
            try {
                userObjectList.addAll(InstaUtils.favesList(getActivity()));
            } catch (Exception e){
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(userObjectList.size() == 0){
                text1.setText(R.string.no_fave_stories);
                text2.setText(R.string.refresh);
                noFaves.setVisibility(View.VISIBLE);
            }
            else
                adapter.setUserObjects(userObjectList);

            wave.setVisibility(View.GONE);
            if(refreshLayout.isRefreshing()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
            if(noFaves.isShown())
                noFaves.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        Log.d("FaveStoriesFragment", "Loading stories");
        recyclerView = view.findViewById(R.id.fave_users_stories_rv);
        wave = view.findViewById(R.id.loading_fave_stories);
        noNet = view.findViewById(R.id.no_net_faves);
        noFaves = view.findViewById(R.id.no_faves);
        refreshLayout = view.findViewById(R.id.refresh_faves);
        noNetRefresh = view.findViewById(R.id.refresh_fave_stories_button);
        text1 = view.findViewById(R.id.fave_text1);
        text2 = view.findViewById(R.id.fave_text2);
        fab = view.findViewById(R.id.search_activity);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FaveListAdapter(getActivity());
        adapter.setUserObjects(userObjectList);
        recyclerView.setAdapter(adapter);

        loadStories();

        noNetRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadStories();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!userObjectList.isEmpty())
                    userObjectList.clear();

                loadStories();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            fab.setTransitionName("reveal");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfilepPictureActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
            }
        });

        return view;
    }

    private void loadStories(){
        if(ZoomstaUtil.haveNetworkConnection(getActivity())){

            if(ZoomstaUtil.getUsers(getActivity()) != null)
                new GetStoriesFeed().execute(new Boolean[]{Boolean.FALSE});
            else {
                noFaves.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if (refreshLayout.isRefreshing()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                        }
                    }, 1500);
                }

            }

        } else {
            noNet.setVisibility(View.VISIBLE);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);

    }



}
