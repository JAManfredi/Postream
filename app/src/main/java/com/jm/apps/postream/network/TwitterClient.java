package com.jm.apps.postream.network;

import android.content.Context;
import android.support.annotation.Nullable;

import com.codepath.oauth.OAuthBaseClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jm.apps.postream.models.Tweet;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.utilities.TimelineType;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TwitterClient extends OAuthBaseClient {
	public interface TwitterClientResponseHandler {
		void onSuccess(List<Tweet> tweetList);
		void onFailure(JSONObject errorResponse);
	}

	public interface TwitterClientUserResponseHandler {
        void onSuccess(User user);
        void onFailure(JSONObject errorResponse);
    }

    public interface TwitterClientTweetResponseHandler {
        void onSuccess(Tweet tweet);
        void onFailure(JSONObject errorResponse);
    }

    private Gson mGson;
    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = "rtco4XaRk8rrHKZAiK02iUrjr";
	public static final String REST_CONSUMER_SECRET = "SCbXuX1OBTNY708SaCaFIV3KcFMHqJeGexgfgG1cEhIjqfOaGx";
	public static final String REST_CALLBACK_URL = "oauth://postream";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);

        // Initialize Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(TWITTER_DATE_FORMAT);
        mGson = gsonBuilder.create();
	}

	public void getTimeline(TimelineType type, @Nullable String userId, final TwitterClientResponseHandler handler) {
        String apiUrl = getApiUrl(getApiPathForType(type));

        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);

        if (userId != null) {
            params.put("user_id", userId);
        }

        client.get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                List<Tweet> tweetList = mGson.fromJson(json.toString(), new TypeToken<List<Tweet>>(){}.getType());
                handler.onSuccess(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure(errorResponse);
            }
        });
    }

    // Pull to refresh

    public void getTimelineSince(long sinceId, TimelineType type, @Nullable String userId, final TwitterClientResponseHandler handler) {
        String apiUrl = getApiUrl(getApiPathForType(type));

		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", sinceId);

        if (userId != null) {
            params.put("user_id", userId);
        }

		client.get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                List<Tweet> tweetList = mGson.fromJson(json.toString(), new TypeToken<List<Tweet>>(){}.getType());
                handler.onSuccess(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure(errorResponse);
            }
        });
	}

	// Page down

	public void getTimelineFromMax(long maxId, TimelineType type, @Nullable String userId, final TwitterClientResponseHandler handler) {
		String apiUrl = getApiUrl(getApiPathForType(type));

		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxId - 1);

        if (userId != null) {
            params.put("user_id", userId);
        }

		client.get(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Type collectionType = new TypeToken<List<Tweet>>(){}.getType();
                List<Tweet> tweetList = mGson.fromJson(json.toString(), collectionType);
                handler.onSuccess(tweetList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure(errorResponse);
            }
        });
	}

	public void getCurrentUser(final TwitterClientUserResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                final User user = mGson.fromJson(json.toString(), User.class);
                handler.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure(errorResponse);
            }
        });
	}

	public void postStatus(String status, final TwitterClientTweetResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", status);

		client.post(apiUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Tweet tweet = mGson.fromJson(json.toString(), Tweet.class);
                handler.onSuccess(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure(errorResponse);
            }
        });
	}

	private String getApiPathForType(TimelineType type) {
        String timelineUrlComponent = "";
        switch (type) {
            case HOME:
                timelineUrlComponent = "home_timeline";
                break;
            case MENTIONS:
                timelineUrlComponent = "mentions_timeline";
                break;
            case USER:
                timelineUrlComponent = "user_timeline";
                break;
        }
        return "statuses/" + timelineUrlComponent + ".json";
    }
}
