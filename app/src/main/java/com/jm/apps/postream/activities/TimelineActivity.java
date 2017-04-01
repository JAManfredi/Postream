package com.jm.apps.postream.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.ActivityTimelineBinding;
import com.jm.apps.postream.fragments.TweetsListFragment;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.viewModels.TimelineActivityViewModel;

import java.util.List;

public class TimelineActivity extends AppCompatActivity {
    private TimelineActivityViewModel mViewModel;
    private TweetsListFragment mFragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTimelineBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setSupportActionBar(binding.includedToolbar.toolbar);
        setTitle("");

        mFragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        mViewModel = new TimelineActivityViewModel(this);
        loadAllTweets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mViewModel.newPost(new TimelineActivityViewModel.OnTweetPosted() {
            @Override
            public void onPosted(Tweet tweet) {
                mFragmentTweetsList.insertTweet(tweet, true);
            }
        });
        return true;
    }

    public void loadAllTweets() {
        mViewModel.loadTimelineTweets(new TimelineActivityViewModel.OnTweetsLoadedCallback() {
            @Override
            public void onLoaded(List<Tweet> tweetsList) {
                mFragmentTweetsList.setTweets(tweetsList);
            }
        });
    }

    public void loadTweetsSince(Tweet tweet) {
        mViewModel.requestTweetsSince(tweet, new TimelineActivityViewModel.OnTweetsLoadedCallback() {
            @Override
            public void onLoaded(List<Tweet> tweetsList) {
                mFragmentTweetsList.insertTweetsFront(tweetsList);
            }
        });
    }

    public void loadTweetsFrom(Tweet tweet) {
        mViewModel.requestTweetsFrom(tweet, new TimelineActivityViewModel.OnTweetsLoadedCallback() {
            @Override
            public void onLoaded(List<Tweet> tweetsList) {
                mFragmentTweetsList.insertTweetsEnd(tweetsList);
            }
        });
    }
}
