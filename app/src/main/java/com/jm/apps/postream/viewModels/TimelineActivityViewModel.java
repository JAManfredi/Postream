package com.jm.apps.postream.viewModels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.ActivityTimelineBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jm.apps.postream.activities.ProfileActivity;
import com.jm.apps.postream.activities.TimelineActivity;
import com.jm.apps.postream.application.PostreamApplication;
import com.jm.apps.postream.database.PostreamDatabaseHelper;
import com.jm.apps.postream.fragments.ComposeDialogFragment;
import com.jm.apps.postream.fragments.HomeTimelineFragment;
import com.jm.apps.postream.fragments.MentionsTImelineFragment;
import com.jm.apps.postream.listeners.OnPostActionListener;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.network.TwitterClient;
import com.jm.apps.postream.utilities.BorderedCircleTransformPC;

import org.json.JSONObject;
import org.parceler.Parcels;

/**
 * Created by Jared12 on 3/26/17.
 */

public class TimelineActivityViewModel extends BaseObservable {
    private Context mContext;
    private ActivityTimelineBinding mBinding;

    private Gson mGson;
    private TwitterClient mClient;

    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final String TAG = TimelineActivityViewModel.class.getSimpleName();

    public TimelineActivityViewModel(ActivityTimelineBinding binding, Context context) {
        this.mContext = context;
        this.mBinding = binding;

        mClient = PostreamApplication.getRestClient();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(TWITTER_DATE_FORMAT);
        mGson = gsonBuilder.create();

        mBinding.includedToolbar.ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileView();
            }
        });
    }

    public void newPost(final OnTweetPosted callback) {
        final ComposeDialogFragment composeFragment = new ComposeDialogFragment();
        composeFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "fragment_compose_dialog");
        composeFragment.setComposeDialogOnPostActionListener(new OnPostActionListener() {
            @Override
            public void sendNewPost(String postString) {
                mClient.postStatus(postString, new TwitterClient.TwitterClientTweetResponseHandler() {
                    @Override
                    public void onSuccess(Tweet tweet) {
                        tweet.save(); // Save to db
                        callback.onPosted(tweet);
                    }

                    @Override
                    public void onFailure(JSONObject errorResponse) {
                        Log.e(TAG, errorResponse.toString());
                    }
                });
            }
        });
    }

    public interface OnTweetPosted {
        void onPosted(Tweet tweet);
    }

    public void loadUserImage() {
        mClient.getCurrentUser(new TwitterClient.TwitterClientUserResponseHandler() {
            @Override
            public void onSuccess(User user) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("currentUserId", user.getId());
                editor.apply();

                user.save(); // Save current user to db

                ImageButton ibProfile = mBinding.includedToolbar.ibProfile;
                Glide.with(mContext)
                        .load(user.getLargeProfileImageUrl())
                        .override(38, 38)
                        .placeholder(R.drawable.image_placeholder)
                        .transform(new BorderedCircleTransformPC(mContext))
                        .into(ibProfile);
            }

            @Override
            public void onFailure(JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
    }

    private void onProfileView() {
        PostreamDatabaseHelper.fetchCurrentUser(mContext, new PostreamDatabaseHelper.DatabaseHelperUserCallback() {
            @Override
            public void onComplete(User user) {
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("user", Parcels.wrap(user));
                mContext.startActivity(profileIntent);
            }
        });
    }

    public void setupPagerAndTabs() {
        ViewPager vpPager = mBinding.viewpager;
        vpPager.setAdapter(new TweetsPagerAdapter(((TimelineActivity) mContext).getSupportFragmentManager()));

        PagerSlidingTabStrip tbTabStrip = mBinding.tabs;
        tbTabStrip.setViewPager(vpPager);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = { "Home", "Mentions" };

        public TweetsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTImelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
