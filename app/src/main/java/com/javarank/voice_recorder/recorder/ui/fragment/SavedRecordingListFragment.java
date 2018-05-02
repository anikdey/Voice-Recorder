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

    private static final String AUDIO_PATH = "http://www.androidbook.com/akc/filestorage/android/documentfiles/3389/play.mp3";

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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
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

    protected void playAudioOnClick() {
        try {
            playAudio(AUDIO_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.play_audio_button)
    protected void playLocalAudio() {
        killMediaPlayer();
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.music_file);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();
        Toast.makeText(getContext(), "Start called", Toast.LENGTH_SHORT).show();
    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
    }

    private void killMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
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
