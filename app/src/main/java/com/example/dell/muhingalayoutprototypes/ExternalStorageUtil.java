package com.example.dell.muhingalayoutprototypes;

import android.os.Environment;

import java.io.File;

public class ExternalStorageUtil {

    //check whether the external storage is mounted or not
    public static Boolean isExternalStorageMounted() {
        String dirState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(dirState);
    }


    //check whether external storage is read only or not
    public static Boolean isExternalStorageReadOnly() {
        String dirState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(dirState);
    }


    //get public external storage base directory
    public static String getPublicExternalStorageBaseDir(String dirType) {

        String ret = "";
        if (isExternalStorageMounted()) {
            File file = Environment.getExternalStoragePublicDirectory(dirType);
            ret = file.getAbsolutePath();
        }
        return ret;

    }

}
