package com.jm.apps.postream.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.ActivityTimelineBinding;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.utilities.TweetPostResult;
import com.jm.apps.postream.utilities.UserProfileRequestResult;
import com.jm.apps.postream.viewModels.TimelineActivityViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {
    private TimelineActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTimelineBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setSupportActionBar(binding.includedToolbar.toolbar);
        setTitle("");

        mViewModel = new TimelineActivityViewModel(binding, this);
        mViewModel.setupPagerAndTabs();
        mViewModel.loadUserImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
                EventBus.getDefault().post(new TweetPostResult(tweet));
            }
        });
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProfile(UserProfileRequestResult result) {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra("user", Parcels.wrap(result.getUser()));
        startActivity(profileIntent);
    }
}
