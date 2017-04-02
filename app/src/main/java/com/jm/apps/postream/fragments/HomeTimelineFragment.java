package com.jm.apps.postream.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jm.apps.postream.application.PostreamApplication;
import com.jm.apps.postream.database.PostreamDatabaseHelper;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.network.TwitterClient;
import com.jm.apps.postream.utilities.TimelineType;
import com.jm.apps.postream.utilities.TweetPostResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jared12 on 4/1/17.
 */

public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient mClient;
    private TimelineType mTimelineType = TimelineType.HOME;

    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = PostreamApplication.getRestClient();

        loadAllTweets();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void loadAllTweets() {

        PostreamDatabaseHelper.fetchTweetsList(mTimelineType, new PostreamDatabaseHelper.DatabaseHelperTweetsCallback() {
            @Override
            public void onComplete(List<Tweet> tweetList) {
                if (tweetList != null && tweetList.size() > 0) {
                    setTweets(tweetList);
                } else {
                    requestAllTweets();
                }
            }
        });
    }

    private void requestAllTweets() {

        mClient.getTimeline(mTimelineType, null, new TwitterClient.TwitterClientResponseHandler() {
            @Override
            public void onSuccess(List<Tweet> tweetList) {
                PostreamDatabaseHelper.saveListTweets(tweetList, mTimelineType); // Save to db
                setTweets(tweetList);
            }

            @Override
            public void onFailure(JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
    }

    @Override
    public void loadTweetsSince(Tweet tweet) {

        mClient.getTimelineSince(tweet.getId(), mTimelineType, null, new TwitterClient.TwitterClientResponseHandler() {
            @Override
            public void onSuccess(List<Tweet> tweetList) {
                PostreamDatabaseHelper.saveListTweets(tweetList, mTimelineType); // Save to db
                insertTweetsFront(tweetList);
            }

            @Override
            public void onFailure(JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
                insertTweetsFront(null); // Sending null so that pull animation dismisses
            }
        });
    }

    @Override
    public void loadTweetsFrom(Tweet tweet) {

        mClient.getTimelineFromMax(tweet.getId(), mTimelineType, null, new TwitterClient.TwitterClientResponseHandler() {
            @Override
            public void onSuccess(List<Tweet> tweetList) {
                PostreamDatabaseHelper.saveListTweets(tweetList, mTimelineType); // Save to db
                insertTweetsEnd(tweetList);
            }

            @Override
            public void onFailure(JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
                insertTweetsEnd(null);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tweetPosted(TweetPostResult result) {
        insertTweet(result.getTweet(), true);
    }
}