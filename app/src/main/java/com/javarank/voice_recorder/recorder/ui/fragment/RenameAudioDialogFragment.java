package com.javarank.voice_recorder.recorder.ui.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseDialogFragment;
import com.javarank.voice_recorder.recorder.util.Constants;
import com.javarank.voice_recorder.recorder.util.StorageUtil;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RenameAudioDialogFragment extends BaseDialogFragment {
    public static final String TAG = RenameAudioDialogFragment.class.getSimpleName();
    private static final String ARG_FILE_PATH = "path";

    @BindView(R.id.custom_name_edit_text)
    EditText customNameEditText;

    private String filePath;

    public static RenameAudioDialogFragment getInstance(String path) {
        RenameAudioDialogFragment fragment = new RenameAudioDialogFragment();
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
        return inflater.inflate(R.layout.fragment_rename_audio, container, false);
    }

    @Override
    public void init() {

    }

    @OnClick(R.id.rename_image_button)
    protected void renameAudioFile() {
        if( isEmptyName() ) {
            customNameEditText.setError(getContext().getString(R.string.enter_valid_name));
        } else {
            int result = StorageUtil.renameFile(filePath, getNewFilePath());
            if( result == Constants.FILE_ALREADY_EXISTS ) {
                String message = String.format(getContext().getString(R.string.file_already_exists), getName());
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } else if( result == Constants.FILE_RENAMED ) {
                Toast.makeText(getContext(), getContext().getString(R.string.file_success_fully_renamed), Toast.LENGTH_SHORT).show();
                notifySuccess();
            } else if( result == Constants.FILE_NOT_FOUND ) {
                Toast.makeText(getContext(), getContext().getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
            } else if( result == Constants.UNKNOWN_PROBLEM) {
                Toast.makeText(getContext(), getContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getNewFilePath() {
        String storageDirectory = StorageUtil.getStorageDirectory();
        String newFilePath = storageDirectory+getName()+ Constants.FILE_TYPE_MP4;
        return newFilePath;
    }

    private boolean isEmptyName() {
        String name = getName();
        if( name != null && !name.trim().equals("") ) {
            return false;
        }
        return true;
    }

    private String getName() {
        return customNameEditText.getText().toString();
    }

    private void notifySuccess() {
        if( getTargetFragment() == null ) {
            return;
        }
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }

    @Override
    public void onCreateComponent() {

    }

}