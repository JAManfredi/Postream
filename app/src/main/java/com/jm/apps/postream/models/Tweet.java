package com.jm.apps.postream.models;

import com.jm.apps.postream.database.PostreamDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.sql.Date;

/**
 * Created by Jared12 on 3/23/17.
 */

@Table(database = PostreamDatabase.class,
        orderedCursorLookUp = true,
        assignDefaultValuesFromCursor = false)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel {

    @PrimaryKey
    @Column
    long id;

    @Column
    Date created_at;

    @Column
    String text;

    @Column
    Boolean truncated;

    @Column
    Boolean is_quote_status;

    @Column
    long retweet_count;

    @Column
    long favorite_count;

    @Column
    Boolean favorited;

    @Column
    Boolean retweeted;

    @Column
    Boolean is_mention;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Tweet retweeted_status;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public String getText() {
        return text;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public Boolean getIsQuoteStatus() {
        return is_quote_status;
    }

    public long getRetweetCount() {
        return retweet_count;
    }

    public long getFavoriteCount() {
        return favorite_count;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public User getUser() {
        return user;
    }

    public Tweet getRetweetedStatus() {
        return retweeted_status;
    }

    public Boolean isRetweet() {
        return retweeted_status != null;
    }

    public Boolean getIsMention() {
        return is_mention;
    }

    public void setIsMention(Boolean isMention) {
        this.is_mention = isMention;
    }
}
