package com.javarank.voice_recorder.recorder.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.javarank.voice_recorder.R;
import com.javarank.voice_recorder.recorder.listener.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedRecordingAdapter extends RecyclerView.Adapter<SavedRecordingAdapter.SavedRecordingViewHolder> {

    protected OnItemClickListener itemClickListener;
    private List<String> itemNameList;

    @Override
    public SavedRecordingAdapter.SavedRecordingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new SavedRecordingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SavedRecordingAdapter.SavedRecordingViewHolder holder, int position) {
        String itemName = itemNameList.get(position);
        holder.itemNameTextView.setText(itemName);
    }

    @Override
    public int getItemCount() {
        return itemNameList.size();
    }

    public void addItems(List<String> itemNameList) {
        this.itemNameList = itemNameList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    class SavedRecordingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_name_text_view)
        TextView itemNameTextView;

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
