package com.jm.apps.postream.models;

import com.jm.apps.postream.database.PostreamDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by Jared12 on 3/23/17.
 */

@Table(database = PostreamDatabase.class)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel {

    @PrimaryKey
    @Column
    Long id;

    @Column
    String created_at;

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
    @ForeignKey(saveForeignKeyModel = true)
    User user;

    public Long getId() {
        return id;
    }

    public String getCreatedAt() {
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

//    // Record Finders
//    public static SampleModel byId(long id) {
//        return new Select().from(SampleModel.class).where(SampleModel_Table.id.eq(id)).querySingle();
//    }
//
//    public static List<SampleModel> recentItems() {
//        return new Select().from(SampleModel.class).orderBy(SampleModel_Table.id, false).limit(300).queryList();
//    }
}
