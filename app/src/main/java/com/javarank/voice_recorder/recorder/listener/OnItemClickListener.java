package com.javarank.voice_recorder.recorder.listener;

import android.view.View;

public interface OnItemClickListener {

    void onItemClick(int position, View view);

    void showDeleteDialog(int position);

    void showRenameDialogFragment(int position);
}
