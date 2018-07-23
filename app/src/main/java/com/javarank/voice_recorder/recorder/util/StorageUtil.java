package com.javarank.voice_recorder.recorder.util;

import android.os.Environment;

import java.io.File;

public class StorageUtil {

    public static String getAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public static boolean deleteFile(String filePath) {
        boolean isDeleted = false;
        File file = new File(filePath);
        if( file.exists() ) {
            isDeleted = file.delete();
        }
        return isDeleted;
    }

}
