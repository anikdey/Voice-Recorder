package com.javarank.voice_recorder.recorder.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
    private static final int REQUEST_AUDIO_RECORDING = 1;

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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_RECORDING);
        } else {
            initializePager();
        }
    }

    private void initializePager() {
        recordingPagerAdapter = new RecordingPagerAdapter(getChildFragmentManager());
        recordingPagerAdapter.addFragment(RecordingFragment.getNewInstance(), "Record");
        recordingPagerAdapter.addFragment(SavedRecordingListFragment.getNewInstance(), "Saved Recording");
        viewPager.setAdapter(recordingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_RECORDING) {
            boolean isGranted = true;
            for(int result : grantResults) {
                if( result != PackageManager.PERMISSION_GRANTED ) {
                    isGranted = false;
                }
            }

            if( !isGranted ) {
                getActivity().finish();
            } else {
                initializePager();
            }
        }
    }

    @Override
    public void onCreateComponent() {

    }
}
