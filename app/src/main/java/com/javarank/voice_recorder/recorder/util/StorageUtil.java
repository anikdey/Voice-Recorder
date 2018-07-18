package com.javarank.voice_recorder.recorder.util;

import android.os.Environment;

public class StorageUtil {

    public static String getAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


}
