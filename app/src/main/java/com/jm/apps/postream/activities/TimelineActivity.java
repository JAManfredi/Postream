package com.jm.apps.postream.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.postream.R;
import com.jm.apps.postream.adapters.TweetAdapter;
import com.jm.apps.postream.application.PostreamApplication;
import com.codepath.apps.postream.databinding.ActivityTimelineBinding;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.network.TwitterClient;
import com.jm.apps.postream.utilities.DividerItemDecoration;
import com.jm.apps.postream.utilities.EndlessRecyclerViewScrollListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private Gson gson;
    private TweetAdapter tweetAdapter;

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        client = PostreamApplication.getRestClient();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tweetAdapter = new TweetAdapter(this, tweets);

        RecyclerView rvTweets = binding.rvTweets;
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Load old tweets and append to bottom
            }
        };

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load newest tweets on top of current ones
                binding.swipeContainer.setRefreshing(false);
            }
        });
        populateTimeline();
    }

    private void populateTimeline() {
        client.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
                List<Tweet> tweetList = gson.fromJson(json.toString(), collectionType);
                tweets.addAll(tweetList);
                tweetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
