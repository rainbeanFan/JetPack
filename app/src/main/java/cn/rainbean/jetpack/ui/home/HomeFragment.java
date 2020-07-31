package cn.rainbean.jetpack.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.scwang.smartrefresh.layout.api.RefreshLayout;

import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import cn.rainbean.basemodule.annotation.FragmentDestination;
import cn.rainbean.jetpack.R;
import cn.rainbean.jetpack.exoplayer.PageListPlayDetector;
import cn.rainbean.jetpack.exoplayer.PageListPlayManager;
import cn.rainbean.jetpack.module.Feed;
import cn.rainbean.jetpack.ui.AbsListFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


@FragmentDestination(pageUrl = "main/tabs/home",asStarter = true)
public class HomeFragment extends AbsListFragment<Feed,HomeViewModel> {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PageListPlayDetector playDetector;
    private String feedType;
    private boolean shouldPause = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String feedType) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("feedType",feedType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);
            }
        });

        playDetector = new PageListPlayDetector(this,mRecyclerView);
        mViewModel.setFeedType(feedType);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.tvToSecond).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_secondFragment)
         );

        return view;
    }


    @Override
    public PagedListAdapter getAdapter() {

        feedType = getArguments() == null?"all":getArguments().getString("feedType");



        return new FeedAdapter(getContext(),feedType){

            @Override
            public void onViewAttachedToWindow2(ViewHolder holder) {
                super.onViewAttachedToWindow2(holder);
            }
        };
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }


    @Override
    public void onDestroy() {
        PageListPlayManager.release(feedType);
        super.onDestroy();
    }
}