package com.jm.apps.postream.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jm.apps.postream.application.PostreamApplication;
import com.jm.apps.postream.database.PostreamDatabaseHelper;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.network.TwitterClient;
import com.jm.apps.postream.utilities.TimelineType;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jared12 on 4/2/17.
 */

public class UserTImelineFragment extends TweetsListFragment {
    private TwitterClient mClient;
    private TimelineType mTimelineType = TimelineType.USER;
    private String mUserId;

    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    public static UserTImelineFragment newInstance(String userId) {
        UserTImelineFragment userTImelineFragment = new UserTImelineFragment();
        if (userId != null) {
            Bundle args = new Bundle();
            args.putString("user_id", userId);
            userTImelineFragment.setArguments(args);
        }
        return userTImelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = PostreamApplication.getRestClient();

        Bundle args = getArguments();
        mUserId = (args != null) ? args.getString("user_id") : null;

        requestAllTweets();
    }

    public void loadAllTweets() {
        PostreamDatabaseHelper.fetchTweetsList(getContext(), mTimelineType, new PostreamDatabaseHelper.DatabaseHelperTweetsCallback() {
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
        mClient.getTimeline(mTimelineType, mUserId, new TwitterClient.TwitterClientResponseHandler() {
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
        mClient.getTimelineSince(tweet.getId(), mTimelineType, mUserId, new TwitterClient.TwitterClientResponseHandler() {
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
        mClient.getTimelineFromMax(tweet.getId(), mTimelineType, mUserId, new TwitterClient.TwitterClientResponseHandler() {
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
}