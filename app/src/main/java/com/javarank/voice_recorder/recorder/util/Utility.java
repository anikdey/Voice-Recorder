package com.javarank.voice_recorder.recorder.util;

import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class Utility {

    public static void setDurationTextOnTextView(TextView textView, int length) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
    }

}
