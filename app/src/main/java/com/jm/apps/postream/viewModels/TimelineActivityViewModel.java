package com.jm.apps.postream.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.postream.databinding.ActivityTimelineBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jm.apps.postream.adapters.TweetAdapter;
import com.jm.apps.postream.application.PostreamApplication;
import com.jm.apps.postream.database.PostreamDatabaseHelper;
import com.jm.apps.postream.fragments.ComposeDialogFragment;
import com.jm.apps.postream.listeners.OnPostActionListener;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.network.TwitterClient;
import com.jm.apps.postream.utilities.DividerItemDecoration;
import com.jm.apps.postream.utilities.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jared12 on 3/26/17.
 */

public class TimelineActivityViewModel extends BaseObservable {
    private Context mContext;
    private ActivityTimelineBinding mBinding;

    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final String TAG = TimelineActivityViewModel.class.getSimpleName();

    private Gson mGson;
    private TwitterClient mClient;
    private TweetAdapter mTweetAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ArrayList<Tweet> mTweets = new ArrayList<>();

    public TimelineActivityViewModel(ActivityTimelineBinding binding, Context context) {
        this.mBinding = binding;
        this.mContext = context;
    }

    public void onCreate() {
        mClient = PostreamApplication.getRestClient();

        // Initialize Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(TWITTER_DATE_FORMAT);
        mGson = gsonBuilder.create();

        setupRecyclerView();
        setupPullToRefresh();
        loadTimeline();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mTweetAdapter = new TweetAdapter(mContext, mTweets);

        RecyclerView rvTweets = mBinding.rvTweets;
        rvTweets.setAdapter(mTweetAdapter);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));

        // Pagination
        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pageDown();
            }
        };
        rvTweets.addOnScrollListener(mScrollListener);
    }

    private void setupPullToRefresh() {
        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
    }

    private void loadTimeline() {
        PostreamDatabaseHelper.fetchListTweets(new PostreamDatabaseHelper.DatabaseHelperCallback() {
            @Override
            public void onComplete(List<Tweet> tweets) {
                if (tweets != null && tweets.size() > 0) {
                    mTweetAdapter.setTweets(tweets);
                } else {
                    getTimeline();
                }
            }
        });
    }

    private void getTimeline() {
        mClient.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                List<Tweet> tweetList = mGson.fromJson(json.toString(), new TypeToken<List<Tweet>>(){}.getType());
                PostreamDatabaseHelper.saveListTweets(tweetList); // Save to db
                mTweetAdapter.setTweets(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
    }

    private void pullToRefresh() {
        if (mTweets == null || mTweets.size() == 0) {
            return;
        }

        Tweet latestTweet = mTweets.get(0);
        mClient.getTimeline(latestTweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                List<Tweet> tweetList = mGson.fromJson(json.toString(), new TypeToken<List<Tweet>>(){}.getType());
                PostreamDatabaseHelper.saveListTweets(tweetList); // Save to db
                mTweetAdapter.insertTweets(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
        mBinding.swipeContainer.setRefreshing(false);
    }

    private void pageDown() {
        if (mTweets == null || mTweets.size() == 0) {
            return;
        }

        // Load old tweets and append to bottom
        Tweet oldestTweet = mTweets.get(mTweets.size() - 1);
        mClient.getTimelineFromMax(oldestTweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
                List<Tweet> tweetList = mGson.fromJson(json.toString(), collectionType);

                int newlyInsertedStartRange = mTweets.size() + 1;
                mTweets.addAll(tweetList);
                PostreamDatabaseHelper.saveListTweets(tweetList); // Save to db
                mTweetAdapter.notifyItemRangeInserted(newlyInsertedStartRange, tweetList.size());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
    }

    public void newPost() {
        final ComposeDialogFragment composeFragment = new ComposeDialogFragment();
        composeFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "fragment_compose_dialog");
        composeFragment.setComposeDialogOnPostActionListener(new OnPostActionListener() {
            @Override
            public void sendNewPost(String postString) {
                mClient.postStatus(postString, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        Tweet tweet = mGson.fromJson(json.toString(), Tweet.class);
                        tweet.save(); // Save to db
                        mTweetAdapter.addTweet(tweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, errorResponse.toString());
                    }
                });
            }
        });
    }
}
