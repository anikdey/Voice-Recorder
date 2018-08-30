package com.javarank.voice_recorder.recorder.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.common.BaseSupportFragment;
import com.javarank.voice_recorder.recorder.events.AudioAddedEvent;
import com.javarank.voice_recorder.recorder.listener.OnItemClickListener;
import com.javarank.voice_recorder.recorder.models.RecordedItem;
import com.javarank.voice_recorder.recorder.ui.adapter.SavedRecordingAdapter;
import com.javarank.voice_recorder.recorder.util.Constants;
import com.javarank.voice_recorder.recorder.util.StorageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;

public class SavedRecordingListFragment extends BaseSupportFragment implements MediaPlayer.OnPreparedListener {
    public static final String TAG = SavedRecordingListFragment.class.getSimpleName();
    private static final int RENAME_SAVED_AUDIO_FILE_REQUEST_CODE = 300;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.nothing_found_linear_layout)
    LinearLayout nothingFoundLinearLayout;

    private SavedRecordingAdapter adapter;
    private ArrayList<RecordedItem> recordings;

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
        EventBus.getDefault().register(this);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                MediaPlayerDialogFragment fragment = MediaPlayerDialogFragment.getInstance(position, recordings);
                fragment.setCancelable(false);
                fragment.show(getFragmentManager(), MediaPlayerDialogFragment.TAG);
            }

            @Override
            public void showDeleteDialog(int position) {
                String filePath = adapter.getItem(position).getFilePath();
                showAlertDialog(filePath);
            }

            @Override
            public void showRenameDialogFragment(int position) {
                String filePath = adapter.getItem(position).getFilePath();
                String fileCurrentName = adapter.getItem(position).getSongName();
                RenameAudioDialogFragment fragment = RenameAudioDialogFragment.getInstance(filePath, fileCurrentName);
                fragment.setCancelable(true);
                fragment.setTargetFragment(SavedRecordingListFragment.this, RENAME_SAVED_AUDIO_FILE_REQUEST_CODE);
                fragment.show(getFragmentManager(), RenameAudioDialogFragment.TAG);
            }
        });
        initRecyclerView();
        loadRecordedAudios();
    }

    private void showAlertDialog(final String filePath) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle(R.string.delete_audio)
                .setMessage(getContext().getString(R.string.are_you_sure))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        StorageUtil.deleteFile(filePath);
                        initRecyclerView();
                        loadRecordedAudios();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private  void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void loadRecordedAudios() {
        recordings = getRecordedItems();
        if( recordings != null && recordings.size()>0 ) {
            controlVisibility(View.VISIBLE, View.GONE);
            adapter.clear();
            adapter.addItems(recordings);
            adapter.notifyDataSetChanged();
        } else {
            controlVisibility(View.GONE, View.VISIBLE);
        }
    }

    private void controlVisibility(int listVisibility, int noDataVisibility){
        recyclerView.setVisibility(listVisibility);
        nothingFoundLinearLayout.setVisibility(noDataVisibility);
    }

    private ArrayList<RecordedItem> getRecordedItems() {
        ArrayList<RecordedItem> songs = new ArrayList<>();
        File targetDirector = new File(StorageUtil.getStorageDirectory());
        File[] files = targetDirector.listFiles();
        if(files != null) {
            for (File file : files) {
                String path = file.getAbsolutePath();
                int lastIndexOfDot = path.lastIndexOf(".");
                int pathLength = path.length();
                String extension = path.substring(lastIndexOfDot, pathLength);
                if( extension.equalsIgnoreCase(Constants.FILE_TYPE_MP3) || extension.equalsIgnoreCase(Constants.FILE_TYPE_MP4) ) {
                    String fileName = StorageUtil.getFileName(path);
                    RecordedItem recordedItem = new RecordedItem();
                    recordedItem.setSongName(fileName);
                    recordedItem.setFilePath(path);
                    recordedItem.setSongLength(getFileDuration(path));
                    songs.add(recordedItem);
                }
            }
        }
        Collections.reverse(songs);
        return songs;
    }

    private int getFileDuration(String filePath) {
        int length = 0;
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            length = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateComponent() {
        adapter = new SavedRecordingAdapter();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void consumeNotification(AudioAddedEvent event) {
        initRecyclerView();
        loadRecordedAudios();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }
        if( requestCode == RENAME_SAVED_AUDIO_FILE_REQUEST_CODE ) {
            initRecyclerView();
            loadRecordedAudios();
        }
    }
}