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
    @BindView(R.id.timer_text_view)
    TextView timerTextView;

    private Timer timer;
    private MediaRecorder mediaRecorder;
    private boolean isAlreadyStartedRecording = false;
    private int passedSeconds = 0;
    private String filePath = null;

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

    @OnClick(R.id.stop_recording_image_button)
    protected void onStopImageButtonClick() {
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

    protected void controlRecordingButtonVisibility(int visibility) {
        startRecordingImageButton.setVisibility(visibility);
    }

    protected void controlStopRecordingButtonVisibility(int visibility) {
        stopRecordingImageButton.setVisibility(visibility);
    }

    protected void controlDeleteAndSaveButtonVisibility(int visibility) {
        deleteAudioImageButton.setVisibility(visibility);
        saveAudioImageButton.setVisibility(visibility);
    }

    @OnClick(R.id.save_audio_image_button)
    protected void onSaveAudioImageButtonClick() {
        String fileName = StorageUtil.getFileName(filePath);
        RenameAudioDialogFragment fragment = RenameAudioDialogFragment.getInstance(filePath, fileName);
        fragment.setCancelable(true);
        fragment.setTargetFragment(RecordingFragment.this, RENAME_RECORDED_AUDIO_FILE_REQUEST_CODE);
        fragment.show(getFragmentManager(), RenameAudioDialogFragment.TAG);
    }

    @OnClick(R.id.delete_audio_image_button)
    protected void onDeleteAudioImageButtonClick() {
        deleteFile();
    }

    private void deleteFile() {
        boolean isDeleted = StorageUtil.deleteFile(filePath);
        if( isDeleted ) {
            restoreInitialState();
            showMessage(getContext().getString(R.string.deleted));
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
        }, 0 ,Constants.ONE_THOUSAND_SECOND);

    }

    private void setDurationTextOnTestView(TextView textView, int length) {
        int min = length / Constants.SIXTY_SECOND;
        int sec = length % Constants.SIXTY_SECOND;
        textView.setText(String.format(getContext().getString(R.string.passed_time_format), min, sec));
    }

    private void stopTimer() {
        passedSeconds = 0;
        timer.cancel();
    }

    private void restoreInitialState() {
        resetTimer();
        filePath = null;
        isAlreadyStartedRecording = false;
        setDurationTextOnTestView(timerTextView, 0);
        controlStopRecordingButtonVisibility(View.GONE);
        controlDeleteAndSaveButtonVisibility(View.GONE);
        controlRecordingButtonVisibility(View.VISIBLE);
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

    protected void resumeRecording() {
        // To be implemented in version 2.0
    }

    private void pauseRecording() {
        if( mediaRecorder != null ) {
            //mediaRecorder.pause();
        }
    }

    private String getGeneratedFilePath() {
        String storageDirectory = StorageUtil.getStorageDirectory();
        File file = createFile(storageDirectory, System.currentTimeMillis() + Constants.FILE_TYPE_MP4);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mediaRecorder != null ) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            filePath = null;
        }
        if(timer!=null) {
            timer.cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }
        if( requestCode == RENAME_RECORDED_AUDIO_FILE_REQUEST_CODE ) {
            restoreInitialState();
            postAudioSavedEvent();
            showMessage(getContext().getString(R.string.saved));
        }
    }

    private void postAudioSavedEvent(){
        EventBus.getDefault().postSticky(new AudioAddedEvent());
    }

    @Override
    public void onCreateComponent() {

    }
}