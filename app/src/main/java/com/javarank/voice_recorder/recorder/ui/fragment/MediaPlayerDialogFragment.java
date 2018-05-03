package com.javarank.voice_recorder.recorder.ui.fragment;


import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

    private Handler mHandler = new Handler();
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
        setUpSeekBarListener();
    }

    private void setUpSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mediaPlayer != null && fromUser ) {
                    mediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);
                    updateSeekBar();
                    setDuration(currentPositionTextView, mediaPlayer.getCurrentPosition());
                } else if( mediaPlayer == null && fromUser ) {
                    prepareMediaPlayerFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void prepareMediaPlayerFromPoint(int progress) {
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(progress);
            int length = mediaPlayer.getDuration();
            seekBar.setMax(length);
            setDuration(totalDurationTextView, length);
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            setDuration(currentPositionTextView, mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            updateSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDuration(TextView textView, int length) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(minutes);
        currentPositionTextView.setText(String.format("%02d:%02d", minutes,seconds));
    }

    private void setUpMediaPlayerOnCompleteListener(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlayingMediaPlayer();
            }
        });
    }

    private void stopPlayingMediaPlayer(){
        mHandler.removeCallbacks(mRunnable);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        seekBar.setProgress(seekBar.getMax());
        currentPositionTextView.setText(totalDurationTextView.getText());
        seekBar.setProgress(seekBar.getMax());
    }


    @OnClick(R.id.play_audio_image_view)
    protected void playAudioFromSDCard() {
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            int length = mediaPlayer.getDuration();
            seekBar.setMax(length);
            updateSeekBar();
            setDuration(totalDurationTextView, length);
            setDuration(currentPositionTextView, 0);
            mediaPlayer.start();
            setUpMediaPlayerOnCompleteListener(mediaPlayer);
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

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer != null){
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                setDuration(currentPositionTextView, mCurrentPosition);
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }
}