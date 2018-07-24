package com.javarank.voice_recorder.recorder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordedItem implements Parcelable {

    private String songName;
    private String filePath;
    private int songLength;

    public RecordedItem(){

    }

    protected RecordedItem(Parcel in) {
        songName = in.readString();
        filePath = in.readString();
        songLength = in.readInt();
    }

    public static final Creator<RecordedItem> CREATOR = new Creator<RecordedItem>() {
        @Override
        public RecordedItem createFromParcel(Parcel in) {
            return new RecordedItem(in);
        }

        @Override
        public RecordedItem[] newArray(int size) {
            return new RecordedItem[size];
        }
    };

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSongLength() {
        return songLength;
    }

    public void setSongLength(int songLength) {
        this.songLength = songLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(songName);
        parcel.writeString(filePath);
        parcel.writeInt(songLength);
    }
}
