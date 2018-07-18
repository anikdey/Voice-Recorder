package com.javarank.voice_recorder.recorder.models;

public class RecordedItem {

    private String songName;
    private String filePath;
    private int songLength;

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
}
