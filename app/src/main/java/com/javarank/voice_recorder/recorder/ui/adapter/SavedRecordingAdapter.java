package com.javarank.voice_recorder.recorder.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.recorder.listener.OnItemClickListener;
import com.javarank.voice_recorder.recorder.models.RecordedItem;
import com.javarank.voice_recorder.recorder.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedRecordingAdapter extends RecyclerView.Adapter<SavedRecordingAdapter.SavedRecordingViewHolder> {

    protected OnItemClickListener itemClickListener;
    private List<RecordedItem> recordedItemList;

    public SavedRecordingAdapter() {
        recordedItemList = new ArrayList<>();
    }

    @Override
    public SavedRecordingAdapter.SavedRecordingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new SavedRecordingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SavedRecordingAdapter.SavedRecordingViewHolder holder, int position) {
        RecordedItem song = recordedItemList.get(position);
        holder.itemNameTextView.setText(song.getSongName());
        Utility.setDurationTextOnTextView(holder.lengthTextView, song.getSongLength());
    }



    @Override
    public int getItemCount() {
        return recordedItemList.size();
    }

    public void addItems(List<RecordedItem> recordedItemList) {
        this.recordedItemList = recordedItemList;
    }

    public RecordedItem getItem(int position) {
        return recordedItemList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    public void clear() {
        recordedItemList.clear();
    }

    class SavedRecordingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_name_text_view)
        TextView itemNameTextView;
        @BindView(R.id.item_length_text_view)
        TextView lengthTextView;
        @BindView(R.id.outer_layout)
        LinearLayout layout;
        @BindView(R.id.delete_image_button)
        ImageView deleteImageButton;
        @BindView(R.id.edit_image_button)
        ImageView editImageButton;


        public SavedRecordingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            layout.setOnClickListener(this);
            deleteImageButton.setOnClickListener(this);
            editImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if( itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION ) {
                if( view.getId() == R.id.outer_layout ) {
                    itemClickListener.onItemClick(getAdapterPosition(), view);
                } else if( view.getId() == R.id.delete_image_button ) {
                    itemClickListener.showDeleteDialog(getAdapterPosition());
                } else if( view.getId() == R.id.edit_image_button ) {
                    itemClickListener.showRenameDialogFragment(getAdapterPosition());
                }
            }
        }
    }

}
