package com.jm.apps.postream.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.FragmentComposeDialogBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jm.apps.postream.application.PostreamApplication;
import com.jm.apps.postream.listeners.OnPostActionListener;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.network.TwitterClient;
import com.jm.apps.postream.utilities.BorderedCircleTransform;

import org.json.JSONObject;

public class ComposeDialogFragment extends DialogFragment {
    private static final String TAG = ComposeDialogFragment.class.getSimpleName();

    private Gson gson;
    private FragmentComposeDialogBinding binding;
    private OnPostActionListener listener;

    public ComposeDialogFragment() {
        this.listener = null;
    }

    public void setComposeDialogOnPostActionListener(OnPostActionListener listener) {
        this.listener = listener;
    }

    public static ComposeDialogFragment newInstance(String userImageUrl) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", userImageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        loadUserImage();
        addTextWatcher();

        Button btClose = binding.includedToolbar.btClose;
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        Button btPost = binding.includedToolbar.btPost;
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private void loadUserImage() {
        TwitterClient client = PostreamApplication.getRestClient();
        client.getCurrentUser(new TwitterClient.TwitterClientUserResponseHandler() {
            @Override
            public void onSuccess(User user) {
                Context context = getContext();
                if (user != null
                        && context != null) {
                    Glide.with(context)
                            .load(user.getLargeProfileImageUrl())
                            .override(48, 48)
                            .placeholder(R.drawable.image_placeholder)
                            .transform(new BorderedCircleTransform(getContext()))
                            .into(binding.ivUserImage);
                }
            }

            @Override
            public void onFailure(JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });
    }

    private void addTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsRemaining = Math.max(140 - s.length(), 0);
                binding.tvCharsLeft.setText(String.valueOf(charsRemaining));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        binding.etNewPost.addTextChangedListener(textWatcher);
    }

    private void close() {
        dismiss();
    }

    private void post() {
        String postString = binding.etNewPost.getText().toString();
        listener.sendNewPost(postString);
        dismiss();
    }
}
