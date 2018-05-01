package com.javarank.voice_recorder.recorder.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseAppCompatActivity;
import com.javarank.voice_recorder.recorder.ui.fragment.RecordingPagerFragment;

public class MainActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RecordingPagerFragment.getNewInstance(), RecordingPagerFragment.TAG).commit();
        }
    }

    @Override
    public void setUpContentView() {
        setContentView(R.layout.activity_main);
    }

}