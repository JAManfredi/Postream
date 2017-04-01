package com.jm.apps.postream.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.FragmentTweetsListBinding;
import com.jm.apps.postream.activities.TimelineActivity;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.utilities.EndlessRecyclerViewScrollListener;
import com.jm.apps.postream.viewModels.TweetsListViewModel;

import java.util.List;

/**
 * Created by Jared12 on 3/30/17.
 */

public class TweetsListFragment extends Fragment {
    private FragmentTweetsListBinding binding;
    private TweetsListViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweets_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Setup scrollview paging
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        EndlessRecyclerViewScrollListener listener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet tweet = mViewModel.getOldestTweet();
                if (tweet != null) {
                    ((TimelineActivity) getActivity()).loadTweetsFrom(tweet);
                }
            }
        };

        mViewModel = new TweetsListViewModel(binding, this.getActivity(), listener, layoutManager);
        mViewModel.onViewCreated();

        setupPullToRefreshListener();
    }

    public void setupPullToRefreshListener() {
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Tweet tweet = mViewModel.getNewestTweet();
                if (tweet != null) {
                    ((TimelineActivity) getActivity()).loadTweetsSince(tweet);
                } else {
                    // Reset pull to refresh
                    insertTweetsFront(null);
                }
            }
        });
    }

    public void setTweets(List<Tweet> tweetsList) {
        mViewModel.setTweets(tweetsList);
    }

    public void insertTweetsFront(List<Tweet> tweetsList) {
        mViewModel.insertTweetsFront(tweetsList);
    }

    public void insertTweetsEnd(List<Tweet> tweetsList) {
        mViewModel.insertTweetsEnd(tweetsList);
    }

    public void insertTweet(Tweet tweet, boolean scrollToTop) {
        mViewModel.insertTweet(tweet, scrollToTop);
    }
}