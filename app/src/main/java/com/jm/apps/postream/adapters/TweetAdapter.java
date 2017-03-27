package com.jm.apps.postream.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.ItemTweetBinding;
import com.jm.apps.postream.viewModels.TweetViewModel;
import com.jm.apps.postream.models.Tweet;

import java.util.List;

/**
 * Created by Jared12 on 3/23/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.BindingHolder> {
    private List<Tweet> mTweets;
    private Context mContext;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    @Override
    public TweetAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemTweetBinding tweetBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_tweet,
                parent,
                false);
        return new BindingHolder(tweetBinding);
    }

    @Override
    public void onBindViewHolder(TweetAdapter.BindingHolder holder, int position) {
        ItemTweetBinding postBinding = holder.binding;
        postBinding.setViewModel(new TweetViewModel(mContext, mTweets.get(position)));
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void setTweets(List<Tweet> tweets) {
        if (tweets == null
                || tweets.size() == 0) {
            return;
        }
        mTweets = tweets;
        notifyDataSetChanged();
    }

    public void insertTweets(List<Tweet> tweets) {
        if (tweets == null
                || tweets.size() == 0) {
            return;
        }
        mTweets.addAll(0, tweets);
        notifyItemRangeInserted(0, tweets.size());
    }

    public void appendTweets(List<Tweet> tweets) {
        if (tweets == null
                || tweets.size() == 0) {
            return;
        }
        int initialCount = mTweets.size();
        mTweets.addAll(tweets);
        notifyItemRangeInserted(initialCount + 1, tweets.size());
    }

    public void addTweet(Tweet tweet) {
        mTweets.add(0, tweet);
        notifyItemInserted(0);
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemTweetBinding binding;

        public BindingHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
