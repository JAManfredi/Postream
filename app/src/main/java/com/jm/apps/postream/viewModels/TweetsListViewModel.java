package com.jm.apps.postream.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codepath.apps.postream.databinding.FragmentTweetsListBinding;
import com.jm.apps.postream.adapters.TweetAdapter;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.utilities.DividerItemDecoration;
import com.jm.apps.postream.utilities.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jared12 on 3/31/17.
 */

public class TweetsListViewModel extends BaseObservable {
    private Context mContext;
    private FragmentTweetsListBinding mBinding;
    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;

    private static final String TAG = TweetsListViewModel.class.getSimpleName();

    private TweetAdapter mTweetAdapter;
    private ArrayList<Tweet> mTweets = new ArrayList<>();

    public TweetsListViewModel(FragmentTweetsListBinding binding,
                               Context context,
                               EndlessRecyclerViewScrollListener scrollListener,
                               LinearLayoutManager layoutManager) {
        this.mBinding = binding;
        this.mContext = context;
        this.mScrollListener = scrollListener;
        this.mLayoutManager = layoutManager;
    }

    public void onViewCreated() {
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mTweets = new ArrayList<>();
        mTweetAdapter = new TweetAdapter(mContext, mTweets);

        RecyclerView rvTweets = mBinding.rvTweets;
        rvTweets.setAdapter(mTweetAdapter);
        rvTweets.setLayoutManager(mLayoutManager);
        rvTweets.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        rvTweets.addOnScrollListener(mScrollListener);
    }

    public Tweet getNewestTweet() {
        if (mTweets == null
            || mTweets.size() == 0) {
            return null;
        }
        return mTweets.get(0);
    }

    public Tweet getOldestTweet() {
        if (mTweets == null
                || mTweets.size() == 0) {
            return null;
        }
        return mTweets.get(mTweets.size() - 1);
    }

    public void setTweets(List<Tweet> tweetsList) {
        mTweetAdapter.setTweets(tweetsList);
    }

    public void insertTweetsFront(List<Tweet> tweetsList) {
        if (tweetsList != null) {
            mTweetAdapter.insertTweets(tweetsList);
        }
        mBinding.swipeContainer.setRefreshing(false);
    }

    public void insertTweetsEnd(List<Tweet> tweetsList) {
        if (tweetsList != null) {
            mTweetAdapter.appendTweets(tweetsList);
        }
    }

    public void insertTweet(Tweet tweet, boolean scrollToTop) {
        if (tweet != null) {
            mTweetAdapter.addTweet(tweet);

            if (scrollToTop) {
                scrollToTop();
            }
        }
    }

    private void scrollToTop() {
        mBinding.rvTweets.getLayoutManager().scrollToPosition(0);
    }
}
