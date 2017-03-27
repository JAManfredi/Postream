package com.jm.apps.postream.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.postream.R;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.utilities.BorderedCircleTransform;
import com.jm.apps.postream.utilities.TimeAgo;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by Jared12 on 3/26/17.
 */

public class TweetViewModel extends BaseObservable {
    private Context mContext;
    private Tweet mTweet;
    private User mUser;
    private User mOriginalUser;
    private Boolean mIsRetweet;

    public TweetViewModel(Context context, Tweet tweet) {
        this.mContext = context;
        mIsRetweet = tweet.isRetweet();

        if (mIsRetweet) {
            this.mOriginalUser = tweet.getUser();
            this.mTweet = tweet.getRetweetedStatus();
            this.mUser = this.mTweet.getUser();
        } else {
            this.mTweet = tweet;
            this.mUser = tweet.getUser();
        }
    }

    public String getName() {
        return mUser.getName();
    }

    public String getTweetText() {
        return mTweet.getText();
    }

    public String getScreenName() {
        return "@" + mUser.getScreenName();
    }

    public String getTimeAgo() {
        return TimeAgo.getTimeAgo(mTweet.getCreatedAt().getTime());
    }

    public int getRetweetVisibility() {
        if (mIsRetweet) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public String retweetedBy() {
        if (mIsRetweet) {
            return mOriginalUser.getScreenName();
        } else {
            return "";
        }
    }

    public String getImageUrl() {
        return mUser.getLargeProfileImageUrl();
    }

    public String getSmallImageUrl() {
        if (mIsRetweet) {
            return mOriginalUser.getNormalProfileImageUrl();
        } else {
            return null;
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadLargeImage(ImageView view, String url) {
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.image_placeholder)
                .transform(new BorderedCircleTransform(getContext()))
                .into(view);
    }

    @BindingAdapter({"bind:smallImageUrl"})
    public static void loadSmallImage(ImageView view, String url) {
        if (url == null) {
            return;
        }

        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.image_placeholder)
                .transform(new BorderedCircleTransform(getContext()))
                .into(view);
    }
}
