package com.jm.apps.postream.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.ActivityTimelineBinding;
import com.jm.apps.postream.viewModels.TimelineActivityViewModel;

public class TimelineActivity extends AppCompatActivity {
    private TimelineActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTimelineBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        mViewModel = new TimelineActivityViewModel(binding, this);
        mViewModel.onCreate();

        setSupportActionBar(binding.includedToolbar.toolbar);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mViewModel.newPost();
        return true;
    }
}
