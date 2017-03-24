package com.jm.apps.postream.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.postream.R;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.utilities.CircleTransform;
import com.jm.apps.postream.utilities.TimeAgo;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jared12 on 3/23/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    private Context mContext;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivUserImage;
        public TextView tvUserName;
        public TextView tvScreenName;
        public TextView tvTweetTime;
        public TextView tvTweetText;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUserImage = (ImageView) itemView.findViewById(R.id.ivUserImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvTweetTime = (TextView) itemView.findViewById(R.id.tvTweetTime);
            tvTweetText = (TextView) itemView.findViewById(R.id.tvTweetText);
        }
    }

    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetAdapter.ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        User user = tweet.getUser();
        holder.tvUserName.setText(user.getName());
        holder.tvTweetText.setText(tweet.getText());

        String screenName = "@" + user.getScreenName();
        holder.tvScreenName.setText(screenName);

        Locale primaryLocale = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            primaryLocale = getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            primaryLocale = getContext().getResources().getConfiguration().locale;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", primaryLocale);
        try {
            Date createdDate = sdf.parse(tweet.getCreatedAt());
            long timeInMillis = createdDate.getTime();
            String timeAgo = TimeAgo.getTimeAgo(timeInMillis);
            holder.tvTweetTime.setText(timeAgo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(getContext()).load(user.getProfileImageUrl()).fit().transform(new CircleTransform()).into(holder.ivUserImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
