package com.jm.apps.postream.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.postream.R;
import com.codepath.apps.postream.databinding.FragmentTweetsListBinding;
import com.jm.apps.postream.viewModels.TweetsListViewModel;

/**
 * Created by Jared12 on 3/30/17.
 */

public class TweetsListFragment extends Fragment {
    private FragmentTweetsListBinding binding;
    private TweetsListViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweets_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new TweetsListViewModel(binding, this.getActivity());
        mViewModel.onCreate();
    }
}
