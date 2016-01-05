package com.ryg.tianxuntest.utils;

import android.os.Environment;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by renyiguang on 2015/11/19.
 */
public class FileUtil {

    public static void writeFileToSD(String str) {
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        try {
            String fileName="wifilog.txt";
            File path = Environment.getExternalStorageDirectory();
            if(!path.exists()){
                path.mkdir();
            }
            File file = new File(path.getAbsolutePath(),fileName);
            if( !file.exists()) {
                file.createNewFile();
            }

            Debug.log("==================path = " + file.getAbsolutePath());
            byte[] buf = str.getBytes();
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(buf);
            raf.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }





}
