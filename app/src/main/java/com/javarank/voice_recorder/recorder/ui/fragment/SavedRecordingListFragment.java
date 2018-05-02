package com.javarank.voice_recorder.recorder.ui.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;
import com.javarank.voice_recorder.recorder.listener.OnItemClickListener;
import com.javarank.voice_recorder.recorder.ui.adapter.SavedRecordingAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SavedRecordingListFragment extends BaseSupportFragment implements MediaPlayer.OnPreparedListener {
    public static final String TAG = SavedRecordingListFragment.class.getSimpleName();

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private SavedRecordingAdapter adapter;
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;
    private List<String> itemsNames;

    public static SavedRecordingListFragment getNewInstance() {
        SavedRecordingListFragment fragment = new SavedRecordingListFragment();
        return fragment;
    }

    @Override
    public View getRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_recording_list, container, false);
    }

    @Override
    public void init() {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"/music_file.mp3";
                MediaPlayerDialogFragment fragment = MediaPlayerDialogFragment.getInstance(path);
                fragment.setCancelable(false);
                fragment.show(getFragmentManager(), MediaPlayerDialogFragment.TAG);
            }
        });

        initRecyclerView();
        setItems();
    }

    private  void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setItems() {
        itemsNames = new ArrayList<>();
        itemsNames.add("Item 1");
        itemsNames.add("Item 2");
        itemsNames.add("Item 3");
        itemsNames.add("Item 4");
        adapter.addItems(itemsNames);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateComponent() {
        adapter = new SavedRecordingAdapter();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
