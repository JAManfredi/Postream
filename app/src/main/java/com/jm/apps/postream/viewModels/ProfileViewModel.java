package com.jm.apps.postream.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.postream.R;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.utilities.BorderedCircleTransform;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by Jared12 on 4/2/17.
 */

public class ProfileViewModel extends BaseObservable {
    User mUser;

    public ProfileViewModel(User user, Context context) {
        this.mUser = user;
    }

    public String getName() {
        return mUser.getName();
    }

    public String getUserName() {
        return "@" + mUser.getScreenName();
    }

    public String getDescription() {
        return mUser.getDescription();
    }

    public String getLocation() {
        return mUser.getLocation();
    }

    public String getFollowers() {
        return String.valueOf(mUser.getFollowersCount()) + " FOLLOWERS";
    }

    public String getFollowing() {
        return String.valueOf(mUser.getFriendsCount()) + " FOLLOWING";
    }

    public String getImageUrl() {
        return mUser.getLargeProfileImageUrl();
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadUserImage(ImageView view, String url) {
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
