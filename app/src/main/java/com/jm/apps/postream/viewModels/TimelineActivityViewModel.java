package com.jm.apps.postream.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jm.apps.postream.application.PostreamApplication;
import com.jm.apps.postream.database.PostreamDatabaseHelper;
import com.jm.apps.postream.fragments.ComposeDialogFragment;
import com.jm.apps.postream.listeners.OnPostActionListener;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jared12 on 3/26/17.
 */

public class TimelineActivityViewModel extends BaseObservable {
    private Context mContext;

    private Gson mGson;
    private TwitterClient mClient;

    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final String TAG = TimelineActivityViewModel.class.getSimpleName();

    public TimelineActivityViewModel(Context context) {
        this.mContext = context;

        mClient = PostreamApplication.getRestClient();

        // Initialize Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(TWITTER_DATE_FORMAT);
        mGson = gsonBuilder.create();
    }

    // Load from DB or server
    public void loadTimelineTweets(final OnTweetsLoadedCallback callback) {
        PostreamDatabaseHelper.fetchListTweets(new PostreamDatabaseHelper.DatabaseHelperCallback() {
            @Override
            public void onComplete(List<Tweet> tweetList) {
                if (tweetList != null && tweetList.size() > 0) {
                    callback.onLoaded(tweetList);
                } else {
                    requestFullTimeline(callback);
                }
            }
        });
    }

    // Fetch all from server
    private void requestFullTimeline(final OnTweetsLoadedCallback callback) {
        mClient.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                List<Tweet> tweetList = mGson.fromJson(json.toString(), new TypeToken<List<Tweet>>(){}.getType());
                PostreamDatabaseHelper.saveListTweets(tweetList); // Save to db
                callback.onLoaded(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
    }

    // Loads for pull to refresh
    public void requestTweetsSince(Tweet tweet, final OnTweetsLoadedCallback callback) {
        mClient.getTimeline(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                List<Tweet> tweetList = mGson.fromJson(json.toString(), new TypeToken<List<Tweet>>(){}.getType());
                PostreamDatabaseHelper.saveListTweets(tweetList); // Save to db
                callback.onLoaded(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
                callback.onLoaded(null);
            }
        });
    }

    // Loads for pagination
    public void requestTweetsFrom(Tweet tweet, final OnTweetsLoadedCallback callback) {
        mClient.getTimelineFromMax(tweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
                List<Tweet> tweetList = mGson.fromJson(json.toString(), collectionType);
                PostreamDatabaseHelper.saveListTweets(tweetList); // Save to db
                callback.onLoaded(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
                callback.onLoaded(null);
            }
        });
    }

    // Send new tweet
    public void newPost(final OnTweetPosted callback) {
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
                        callback.onPosted(tweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, errorResponse.toString());
                    }
                });
            }
        });
    }

    public interface OnTweetsLoadedCallback {
        void onLoaded(List<Tweet> tweetsList);
    }

    public interface OnTweetPosted {
        void onPosted(Tweet tweet);
    }
}
