package com.javarank.voice_recorder.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.javarank.voice_recorder.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseAppCompatActivity  extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpContentView();
        ButterKnife.bind(this);
        initToolbar();
    }

    public abstract void setUpContentView();

    protected void initToolbar(){
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}