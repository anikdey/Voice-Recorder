package com.javarank.voice_recorder.recorder.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import android.support.design.widget.TabLayout;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;
import com.javarank.voice_recorder.recorder.ui.adapter.RecordingPagerAdapter;

import butterknife.BindView;

/**
 * Created by anik.dey on 1/24/2018.
 */

public class RecordingPagerFragment extends BaseSupportFragment {

    public static final String TAG = RecordingPagerFragment.class.getSimpleName();

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private RecordingPagerAdapter recordingPagerAdapter;

    public static RecordingPagerFragment getNewInstance() {
        RecordingPagerFragment fragment = new RecordingPagerFragment();
        return fragment;
    }

    @Override
    public View getRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recording_pager_fragment, container, false);
    }

    @Override
    public void init() {
        recordingPagerAdapter = new RecordingPagerAdapter(getChildFragmentManager());
        recordingPagerAdapter.addFragment(RecordingFragment.getNewInstance(), "Record");
        recordingPagerAdapter.addFragment(SavedRecordingListFragment.getNewInstance(), "Saved Recording");
        viewPager.setAdapter(recordingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateComponent() {

    }
}
