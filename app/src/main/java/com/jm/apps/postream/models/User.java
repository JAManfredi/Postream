package com.jm.apps.postream.models;

import com.jm.apps.postream.database.PostreamDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by Jared12 on 3/23/17.
 */

@Table(database = PostreamDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel {

    @PrimaryKey
    @Column
    Long id;

    @Column
    String name;

    @Column
    String screen_name;

    @Column
    String location;

    @Column
    String description;

    @Column
    String url;

    @Column
    @SerializedName("protected")
    Boolean private_account;

    @Column
    long followers_count;

    @Column
    long friends_count;

    @Column
    String created_at;

    @Column
    Boolean verified;

    @Column
    String profile_image_url;

    @Column
    Boolean following;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screen_name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getPrivateAccount() {
        return private_account;
    }

    public long getFollowersCount() {
        return followers_count;
    }

    public long getFriendsCount() {
        return friends_count;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public Boolean getVerified() {
        return verified;
    }

    public String getLargeProfileImageUrl() {
        return profile_image_url.replace("normal", "bigger");
    }

    public String getNormalProfileImageUrl() {
        return profile_image_url;
    }

    public Boolean getFollowing() {
        return following;
    }
}
