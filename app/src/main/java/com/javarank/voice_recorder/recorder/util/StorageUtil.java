package com.javarank.voice_recorder.recorder.util;

import android.os.Environment;

import java.io.File;

public class StorageUtil {

    public static String getAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getStorageDirectory() {
        return getAbsolutePath() + Constants.STORAGE_FOLDER_NAME;
    }

    public static boolean deleteFile(String filePath) {
        boolean isDeleted = false;
        File file = new File(filePath);
        if( file.exists() ) {
            isDeleted = file.delete();
        }
        return isDeleted;
    }

    public static int renameFile(String oldFilePath, String newFilePath) {
        int result;
        File from = new File(oldFilePath);
        File to = new File(newFilePath);
        if(from.exists()) {
            result = rename(from, to);
        } else {
            result = Constants.FILE_NOT_FOUND;
        }
        return result;
    }

    private static int rename(File from, File to) {
        int result = Constants.UNKNOWN_PROBLEM;
        if( to.exists() ) {
            result = Constants.FILE_ALREADY_EXISTS;
        } else {
            boolean isSuccess = from.renameTo(to);
            if(isSuccess) {
                result = Constants.FILE_RENAMED;
            }
        }
        return  result;
    }
}
