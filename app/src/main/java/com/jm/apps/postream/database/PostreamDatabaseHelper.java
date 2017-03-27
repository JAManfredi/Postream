package com.jm.apps.postream.database;

import android.support.annotation.NonNull;

import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.models.Tweet_Table;
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
    public interface DatabaseHelperCallback {
        public void onComplete(List<Tweet> tweets);
    }

    public static void saveListTweets(List<Tweet> tweets) {
        ProcessModelTransaction<Tweet> processTweetTransaction = new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<Tweet>() {
            @Override
            public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                tweet.save();
            }
        }).addAll(tweets).build();

        Transaction transaction = FlowManager.getDatabase(PostreamDatabase.class)
                .beginTransactionAsync(processTweetTransaction).build();
        transaction.execute();
    }

    public static void fetchListTweets(final DatabaseHelperCallback callback) {
        SQLite.select()
                .from(Tweet.class)
                .orderBy(Tweet_Table.created_at, false)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Tweet>() {
            @Override
            public void onQueryResult(QueryTransaction<Tweet> transaction, @NonNull CursorResult<Tweet> tResult) {
                callback.onComplete(tResult.toList());
            }
        }).execute();
    }
}
