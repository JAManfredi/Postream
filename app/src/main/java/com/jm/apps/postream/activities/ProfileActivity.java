package com.jm.apps.postream.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.ActivityProfileBinding;
import com.jm.apps.postream.fragments.UserTImelineFragment;
import com.jm.apps.postream.models.User;
import com.jm.apps.postream.utilities.UserProfileRequestResult;
import com.jm.apps.postream.viewModels.ProfileViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        mViewModel = new ProfileViewModel(user, this);

        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        if (user != null) {
            binding.includedToolbar.toolbarTitle.setText(user.getName());
        }
        binding.setViewModel(mViewModel);
        setSupportActionBar(binding.includedToolbar.toolbar);
        setTitle("");

        Button btClose = binding.includedToolbar.btClose;
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (savedInstanceState == null) {
            String userId = (user != null) ? String.valueOf(user.getId()) : null;
            UserTImelineFragment fragmentUserTimeline = UserTImelineFragment.newInstance(userId);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, fragmentUserTimeline);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProfile(UserProfileRequestResult result) {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra("user", Parcels.wrap(result.getUser()));
        startActivity(profileIntent);
    }
}
