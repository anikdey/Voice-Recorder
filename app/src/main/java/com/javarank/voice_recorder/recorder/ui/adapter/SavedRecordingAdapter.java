package com.javarank.voice_recorder.recorder.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.recorder.listener.OnItemClickListener;
import com.javarank.voice_recorder.recorder.models.RecordedItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedRecordingAdapter extends RecyclerView.Adapter<SavedRecordingAdapter.SavedRecordingViewHolder> {

    protected OnItemClickListener itemClickListener;
    private List<RecordedItem> recordedItemList;

    @Override
    public SavedRecordingAdapter.SavedRecordingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new SavedRecordingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SavedRecordingAdapter.SavedRecordingViewHolder holder, int position) {
        RecordedItem song = recordedItemList.get(position);
        holder.itemNameTextView.setText(song.getSongName());
        setDurationTextOnTestView(holder.lengthTextView, song.getSongLength());
    }

    private void setDurationTextOnTestView(TextView textView, int length) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
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

    class SavedRecordingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_name_text_view)
        TextView itemNameTextView;
        @BindView(R.id.item_length_text_view)
        TextView lengthTextView;

        public SavedRecordingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if( itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION ) {
                itemClickListener.onItemClick(getAdapterPosition(), view);
            }
        }
    }

}
