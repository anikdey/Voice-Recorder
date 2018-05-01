package com.javarank.voice_recorder.recorder.ui.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;

import java.io.IOException;

import butterknife.OnClick;

/**
 * Created by anik.dey on 1/24/2018.
 */

public class SavedRecordingListFragment extends BaseSupportFragment implements MediaPlayer.OnPreparedListener {
    public static final String TAG = SavedRecordingListFragment.class.getSimpleName();

    private static final String AUDIO_PATH = "http://www.androidbook.com/akc/filestorage/android/documentfiles/3389/play.mp3";

    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;

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

    @OnClick(R.id.play_audio_button)
    protected void playAudioFromSDCard() {
        killMediaPlayer();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"/music_file.mp3";
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getContext(), "Start called", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
