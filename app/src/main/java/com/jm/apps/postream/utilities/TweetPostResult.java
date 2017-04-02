package com.jm.apps.postream.utilities;

import com.jm.apps.postream.models.Tweet;

/**
 * Created by Jared12 on 4/1/17.
 */

public class TweetPostResult {
    private Tweet mTweet;

    public TweetPostResult(Tweet tweet) {
        mTweet = tweet;
    }

    public Tweet getTweet() {
        return mTweet;
    }
}