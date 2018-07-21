package com.javarank.voice_recorder.recorder.ui.fragment;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;
import com.javarank.voice_recorder.recorder.util.Constants;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.OnClick;


public class RecordingFragment extends BaseSupportFragment {

    public static final String TAG = RecordingFragment.class.getSimpleName();

    @BindView(R.id.start_recording_image_button)
    ImageView startRecordingImageButton;
    @BindView(R.id.delete_audio_image_button)
    ImageView deleteAudioImageButton;
    @BindView(R.id.pause_recording_image_button)
    ImageView pauseRecordingImageButton;
    @BindView(R.id.save_audio_image_button)
    ImageView saveAudioImageButton;

    private boolean isAlreadyStartedRecording = false;

    private static final String FILE_TYPE = ".mp4";
    private String fileName = null;
    private MediaRecorder mediaRecorder;

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

    private File createFile(String filePath, String fileName) {
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File directory = new File(filePath);
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
        if( isAlreadyStartedRecording ) {
            resumeRecording();
            controlRecordingButtonVisibility(View.GONE);
            controlPauseButtonVisibility(View.VISIBLE);
            controlDeleteAndSaveButtonVisibility(View.GONE);
        } else {
            isAlreadyStartedRecording = true;
            controlRecordingButtonVisibility(View.GONE);
            controlPauseButtonVisibility(View.VISIBLE);
            startRecording();
        }
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
        if( mediaRecorder != null ) {
            mediaRecorder.start();
        }
    }

    @OnClick(R.id.pause_recording_image_button)
    protected void onPauseImageButtonClick() {
        pauseRecording();
        controlRecordingButtonVisibility(View.VISIBLE);
        controlDeleteAndSaveButtonVisibility(View.VISIBLE);
        controlPauseButtonVisibility(View.GONE);
    }

    private void pauseRecording() {
        if( mediaRecorder != null ) {
            //mediaRecorder.pause();
        }
    }

    protected void controlRecordingButtonVisibility(int visibility) {
        startRecordingImageButton.setVisibility(visibility);
    }

    protected void controlPauseButtonVisibility(int visibility) {
        pauseRecordingImageButton.setVisibility(visibility);
    }

    protected void controlDeleteAndSaveButtonVisibility(int visibility) {
        deleteAudioImageButton.setVisibility(visibility);
        saveAudioImageButton.setVisibility(visibility);
    }

    @OnClick(R.id.save_audio_image_button)
    protected void onSaveAudioImageButtonClick() {
        restoreInitialState();
        deleteFile("save and restore initial state");
    }

    @OnClick(R.id.delete_audio_image_button)
    protected void onDeleteAudioImageButtonClick() {
        deleteFile("delete and restore initial state");
        restoreInitialState();
    }

    private void deleteFile(String filePath) {
        Toast.makeText(getContext(), filePath, Toast.LENGTH_SHORT).show();
    }

    private void restoreInitialState() {
        controlPauseButtonVisibility(View.GONE);
        controlDeleteAndSaveButtonVisibility(View.GONE);
        controlRecordingButtonVisibility(View.VISIBLE);
        isAlreadyStartedRecording = false;
    }

    private void startTimer() {

    }

    private void pauseTimer() {

    }

    private void resumeTimer() {

    }

    private void resetTimer() {

    }

    private String getGeneratedFilePath() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.STORAGE_FOLDER_NAME;
        File file = createFile(filePath, System.currentTimeMillis() + FILE_TYPE);
        fileName = file.getAbsolutePath();
        return fileName;
    }

    @Override
    public void onCreateComponent() {

    }
}
