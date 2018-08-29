package com.javarank.voice_recorder.recorder.ui.fragment;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;
import com.javarank.voice_recorder.recorder.util.Constants;
import com.javarank.voice_recorder.recorder.util.StorageUtil;
import com.javarank.voice_recorder.recorder.util.Utility;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;


public class RecordingFragment extends BaseSupportFragment {

    public static final String TAG = RecordingFragment.class.getSimpleName();

    @BindView(R.id.start_recording_image_button)
    ImageView startRecordingImageButton;
    @BindView(R.id.delete_audio_image_button)
    ImageView deleteAudioImageButton;
    @BindView(R.id.stop_recording_image_button)
    ImageView stopRecordingImageButton;
    @BindView(R.id.save_audio_image_button)
    ImageView saveAudioImageButton;


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



    @BindView(R.id.my_text_view)
    TextView myTextView;

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
        //Todo
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
    }

    @OnClick(R.id.save_audio_image_button)
    protected void onSaveAudioImageButtonClick() {
        restoreInitialState();
        //deleteFile("save and restore initial state");
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
        resetTimer();
        controlStopRecordingButtonVisibility(View.GONE);
        controlDeleteAndSaveButtonVisibility(View.GONE);
        controlRecordingButtonVisibility(View.VISIBLE);
        isAlreadyStartedRecording = false;
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
                        //myTextView.setText(""+passedSeconds);
                        setDurationTextOnTestView(myTextView, passedSeconds);
                    }
                });
            }
        }, 0 ,1000);

    }

    private void setDurationTextOnTestView(TextView textView, int length) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void stopTimer() {
        passedSeconds = 0;
        myTextView.setText("00:00");
        timer.cancel();
    }

    private void resetTimer() {

    }

    private void pauseTimer() {

    }

    private void resumeTimer() {

    }

    private String getGeneratedFilePath() {
        //String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.STORAGE_FOLDER_NAME;
        String storageDirectory = StorageUtil.getStorageDirectory();
        File file = createFile(storageDirectory, System.currentTimeMillis() + Constants.FILE_TYPE_MP4);
        fileName = file.getAbsolutePath();
        return fileName;
    }

    @Override
    public void onCreateComponent() {

    }
}
