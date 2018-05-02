package com.javarank.voice_recorder.recorder.ui.fragment;


import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MediaPlayerDialogFragment extends DialogFragment implements MediaPlayer.OnPreparedListener  {
    public static final String TAG = MediaPlayerDialogFragment.class.getSimpleName();
    private static final String ARG_FILE_PATH = "path";

    @BindView(R.id.current_position_text_view)
    TextView currentPositionTextView;
    @BindView(R.id.total_duration_text_view)
    TextView totalDurationTextView;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    private String filePath;
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;

    public static MediaPlayerDialogFragment getInstance(String path) {
        MediaPlayerDialogFragment fragment = new MediaPlayerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, path);
        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
        if( getArguments() != null ) {
            filePath = getArguments().getString(ARG_FILE_PATH);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_player, container, false);
        ButterKnife.bind(this,  view);
        init();
        return view;
    }

    private void init() {

    }

    @OnClick(R.id.play_audio_image_view)
    protected void playAudioFromSDCard() {
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.pause_playing_audio_image_view)
    protected void stopPlayingAudio() {
        if( mediaPlayer != null && mediaPlayer.isPlaying() ) {
            mediaPlayer.pause();
        }
    }

    private void killMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @OnClick(R.id.cancel_dialog_image_view)
    protected void cancelDialog() {
        killMediaPlayer();
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
