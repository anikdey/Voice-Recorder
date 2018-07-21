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
import android.widget.LinearLayout;

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
    @BindView(R.id.control_panel_linear_layout)
    LinearLayout controlPanelLinearLayout;



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

    //@OnClick(R.id.start_recording)
    protected void startRecording() {

        controlPanelVisibility(View.VISIBLE);

       /* mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getGeneratedFilePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }*/
    }

    private void controlPanelVisibility(int visibility) {
        controlPanelLinearLayout.setVisibility(visibility);
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
