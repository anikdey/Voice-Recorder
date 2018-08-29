package com.javarank.voice_recorder.recorder.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;
import com.javarank.voice_recorder.recorder.events.AudioAddedEvent;
import com.javarank.voice_recorder.recorder.util.Constants;
import com.javarank.voice_recorder.recorder.util.StorageUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class RecordingFragment extends BaseSupportFragment {
    public static final String TAG = RecordingFragment.class.getSimpleName();
    private static final int RENAME_RECORDED_AUDIO_FILE_REQUEST_CODE = 301;

    @BindView(R.id.start_recording_image_button)
    ImageView startRecordingImageButton;
    @BindView(R.id.delete_audio_image_button)
    ImageView deleteAudioImageButton;
    @BindView(R.id.stop_recording_image_button)
    ImageView stopRecordingImageButton;
    @BindView(R.id.save_audio_image_button)
    ImageView saveAudioImageButton;
    @BindView(R.id.rename_audio_image_button)
    ImageView renameAudioImageButton;
    @BindView(R.id.timer_text_view)
    TextView timerTextView;

    private Timer timer;
    private MediaRecorder mediaRecorder;
    private boolean isAlreadyStartedRecording = false;
    private int passedSeconds = 0;
    private String fileName = null;

    public static RecordingFragment getNewInstance() {
        RecordingFragment fragment = new RecordingFragment();
        return fragment;
    }

    @Override
    public View getRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    @Override
    public void init() {

    }

    private File createFile(String storageDirectory, String fileName) {
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File directory = new File(storageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory.getAbsolutePath(), fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @OnClick(R.id.start_recording_image_button)
    protected void startRecordingImageButtonClick() {
        /*if( isAlreadyStartedRecording ) {
            resumeTimer();
            resumeRecording();
            controlRecordingButtonVisibility(View.GONE);
            controlStopRecordingButtonVisibility(View.VISIBLE);
            controlDeleteAndSaveButtonVisibility(View.GONE);
        } else {
            startTimer();
            isAlreadyStartedRecording = true;
            controlRecordingButtonVisibility(View.GONE);
            controlStopRecordingButtonVisibility(View.VISIBLE);
            startRecording();
        }*/
        startTimer();
        isAlreadyStartedRecording = true;
        controlRecordingButtonVisibility(View.GONE);
        controlStopRecordingButtonVisibility(View.VISIBLE);
        startRecording();
    }

    protected void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getGeneratedFilePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    protected void resumeRecording() {
        // To be implemented in version 2.0
    }

    @OnClick(R.id.stop_recording_image_button)
    protected void onStopImageButtonClick() {
        //pauseTimer();
        //pauseRecording();
        //controlRecordingButtonVisibility(View.VISIBLE);
        stopRecording();
        stopTimer();
        controlDeleteAndSaveButtonVisibility(View.VISIBLE);
        controlStopRecordingButtonVisibility(View.GONE);
    }

    private void stopRecording() {
        if( mediaRecorder != null ) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void pauseRecording() {
        if( mediaRecorder != null ) {
            //mediaRecorder.pause();
        }
    }

    protected void controlRecordingButtonVisibility(int visibility) {
        startRecordingImageButton.setVisibility(visibility);
    }

    protected void controlStopRecordingButtonVisibility(int visibility) {
        stopRecordingImageButton.setVisibility(visibility);
    }

    protected void controlDeleteAndSaveButtonVisibility(int visibility) {
        deleteAudioImageButton.setVisibility(visibility);
        saveAudioImageButton.setVisibility(visibility);
        renameAudioImageButton.setVisibility(visibility);
    }

    @OnClick(R.id.save_audio_image_button)
    protected void onSaveAudioImageButtonClick() {
        postAudioSavedEvent();
        restoreInitialState();
    }

    private void postAudioSavedEvent(){
        //Todo post event
        EventBus.getDefault().postSticky(new AudioAddedEvent());
    }

    @OnClick(R.id.rename_audio_image_button)
    protected void renameAudioFile() {
        RenameAudioDialogFragment fragment = RenameAudioDialogFragment.getInstance(fileName);
        fragment.setCancelable(true);
        fragment.setTargetFragment(RecordingFragment.this, RENAME_RECORDED_AUDIO_FILE_REQUEST_CODE);
        fragment.show(getFragmentManager(), RenameAudioDialogFragment.TAG);
    }

    @OnClick(R.id.delete_audio_image_button)
    protected void onDeleteAudioImageButtonClick() {
        deleteFile();
    }

    private void deleteFile() {
        boolean isDeleted = StorageUtil.deleteFile(fileName);
        if( isDeleted ) {
            restoreInitialState();
            Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreInitialState() {
        resetTimer();
        fileName = null;
        isAlreadyStartedRecording = false;
        setDurationTextOnTestView(timerTextView, 0);
        controlStopRecordingButtonVisibility(View.GONE);
        controlDeleteAndSaveButtonVisibility(View.GONE);
        controlRecordingButtonVisibility(View.VISIBLE);
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                passedSeconds++;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDurationTextOnTestView(timerTextView, passedSeconds);
                    }
                });
            }
        }, 0 ,1000);

    }

    private void setDurationTextOnTestView(TextView textView, int length) {
        int min = length / 60;
        int sec = length % 60;
        textView.setText(String.format("%02d:%02d", min, sec));
    }

    private void stopTimer() {
        passedSeconds = 0;
        timer.cancel();
    }

    private void resetTimer() {
        // To be implemented in version 2.0
    }

    private void pauseTimer() {
        // To be implemented in version 2.0
    }

    private void resumeTimer() {
        // To be implemented in version 2.0
    }

    private String getGeneratedFilePath() {
        String storageDirectory = StorageUtil.getStorageDirectory();
        File file = createFile(storageDirectory, System.currentTimeMillis() + Constants.FILE_TYPE_MP4);
        fileName = file.getAbsolutePath();
        return fileName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }
        if( requestCode == RENAME_RECORDED_AUDIO_FILE_REQUEST_CODE ) {
            restoreInitialState();
            postAudioSavedEvent();
        }
    }

    @Override
    public void onCreateComponent() {

    }
}