package com.javarank.voice_recorder.recorder.ui.fragment;


import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseDialogFragment;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MediaPlayerDialogFragment extends BaseDialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static final String TAG = MediaPlayerDialogFragment.class.getSimpleName();
    private static final String ARG_FILE_PATH = "path";

    @BindView(R.id.current_position_text_view)
    TextView currentPositionTextView;
    @BindView(R.id.total_duration_text_view)
    TextView totalDurationTextView;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.play_pause_audio_image_view)
    ImageView playPauseImageView;

    private Handler mHandler = new Handler();
    private String filePath;
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;
    private boolean isPlaying = false;

    public static MediaPlayerDialogFragment getInstance(String path) {
        MediaPlayerDialogFragment fragment = new MediaPlayerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filePath = getArguments().getString(ARG_FILE_PATH);
        }
    }

    @Override
    public View getRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_player, container, false);
    }

    @Override
    public void onCreateComponent() {

    }

    @Override
    public void init() {
        setUpSeekBarListener();
    }

    private void setUpSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                    if (isPlaying == false) {
                        mediaPlayer.start();
                        setPlayPauseIcon(R.drawable.pause_white);
                        isPlaying = true;
                    }
                    mHandler.removeCallbacks(mRunnable);
                    updateSeekBar();
                    setDurationTextOnTestView(currentPositionTextView, mediaPlayer.getCurrentPosition());
                } else if (mediaPlayer == null && fromUser) {
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

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopPlayingMediaPlayer();
    }

    @OnClick(R.id.play_pause_audio_image_view)
    protected void controlPlayAndPause() {
        if (isPlaying) {
            pausePlaying();
        } else {
            startPlaying();
        }
    }

    private void startPlaying() {
        killMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            int length = mediaPlayer.getDuration();
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
            setPlayPauseIcon(R.drawable.pause_white);
            setDurationTextOnTestView(totalDurationTextView, length);
            setDurationTextOnTestView(currentPositionTextView, mediaPlayer.getCurrentPosition());
            seekBar.setMax(length);
            updateSeekBar();
            isPlaying = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pausePlaying() {
        if (mediaPlayer != null && isPlaying == true) {
            mediaPlayer.pause();
            playbackPosition = mediaPlayer.getCurrentPosition();
            setPlayPauseIcon(R.drawable.play);
            isPlaying = false;
            mHandler.removeCallbacks(mRunnable);
        }
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
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            setDurationTextOnTestView(currentPositionTextView, mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
            setPlayPauseIcon(R.drawable.pause_white);
            isPlaying = true;
            updateSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDurationTextOnTestView(TextView textView, int length) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void setPlayPauseIcon(int resId) {
        playPauseImageView.setImageResource(resId);
    }

    private void stopPlayingMediaPlayer() {
        mHandler.removeCallbacks(mRunnable);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        isPlaying = false;
        playbackPosition = 0;
        seekBar.setProgress(0);
        setPlayPauseIcon(R.drawable.play);
        setDurationTextOnTestView(currentPositionTextView, 0);
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
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
            if (mediaPlayer != null) {
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);
                setDurationTextOnTestView(currentPositionTextView, mCurrentPosition);
                updateSeekBar();
            }
        }
    };

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }
}