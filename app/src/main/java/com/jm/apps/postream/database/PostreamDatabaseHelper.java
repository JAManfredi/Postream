package com.jm.apps.postream.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.models.Tweet_Table;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.models.User_Table;
import com.jm.apps.postream.utilities.TimelineType;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

/**
 * Created by Jared12 on 3/25/17.
 */

public class PostreamDatabaseHelper {
    public interface DatabaseHelperTweetsCallback {
        public void onComplete(List<Tweet> tweets);
    }

    public interface DatabaseHelperUserCallback {
        public void onComplete(User user);
    }

    public static void saveListTweets(List<Tweet> tweets, final TimelineType type) {
        ProcessModelTransaction<Tweet> processTweetTransaction = new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<Tweet>() {
            @Override
            public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                if (type == TimelineType.MENTIONS) {
                    tweet.setIsMention(true);
                }
                tweet.save();
            }
        }).addAll(tweets).build();

        Transaction transaction = FlowManager.getDatabase(PostreamDatabase.class)
                .beginTransactionAsync(processTweetTransaction).build();
        transaction.execute();
    }

    public static void fetchTweetsList(Context context, TimelineType type, DatabaseHelperTweetsCallback callback) {
        switch (type) {
            case HOME:
                fetchListHomeTimelineTweets(callback);
                break;
            case MENTIONS:
                fetchListMentionsTweets(callback);
                break;
            case USER:
                fetchUserTweets(context, callback);
                break;
        }
    }

    public static void fetchTweetsList(TimelineType type, DatabaseHelperTweetsCallback callback) {
        fetchTweetsList(null, type, callback);
    }

    private static void fetchListHomeTimelineTweets(final DatabaseHelperTweetsCallback callback) {
        SQLite.select()
                .from(Tweet.class)
                .where(Tweet_Table.is_mention.is(false))
                .orderBy(Tweet_Table.created_at, false)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Tweet>() {
            @Override
            public void onQueryResult(QueryTransaction<Tweet> transaction, @NonNull CursorResult<Tweet> tResult) {
                callback.onComplete(tResult.toList());
            }
        }).execute();
    }

    private static void fetchListMentionsTweets(final DatabaseHelperTweetsCallback callback) {
        SQLite.select()
                .from(Tweet.class)
                .where(Tweet_Table.is_mention.is(true))
                .orderBy(Tweet_Table.created_at, false)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Tweet>() {
                    @Override
                    public void onQueryResult(QueryTransaction<Tweet> transaction, @NonNull CursorResult<Tweet> tResult) {
                        callback.onComplete(tResult.toList());
                    }
                }).execute();
    }

    private static void fetchUserTweets(Context context, final DatabaseHelperTweetsCallback callback) {
        fetchCurrentUser(context, new DatabaseHelperUserCallback() {
            @Override
            public void onComplete(User user) {
                SQLite.select()
                        .from(Tweet.class)
                        .where(Tweet_Table.user_id.is(user.getId()))
                        .orderBy(Tweet_Table.created_at, false)
                        .async()
                        .queryResultCallback(new QueryTransaction.QueryResultCallback<Tweet>() {
                            @Override
                            public void onQueryResult(QueryTransaction<Tweet> transaction, @NonNull CursorResult<Tweet> tResult) {
                                callback.onComplete(tResult.toList());
                            }
                        }).execute();
            }
        });
    }

    public static void fetchCurrentUser(Context context, final DatabaseHelperUserCallback callback) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        long userId = sharedPrefs.getLong("currentUserId", 0);
        SQLite.select()
                .from(User.class)
                .where(User_Table.id.is(userId))
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<User>() {
                    @Override
                    public void onQueryResult(QueryTransaction<User> transaction, @NonNull CursorResult<User> uResult) {
                        callback.onComplete(uResult.toModel());
                    }
                }).execute();
    }
}
