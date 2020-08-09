package net.senink.seninkapp.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * 日志管理工具类
 * Created by sx on 2018/12/27.
 */

public enum LogUtil {
    INSTANCE;
    public int VERBOSE_LEVEL = 0;
    public int DEBUG_LEVEL = 1;
    public int INFO_LEVEL = 2;
    public int WARN_LEVEL = 3;
    public int ERROR_LEVEL = 4;
    public int NO_SHOW_LEVEL = 99;
    private int LEVEL = DEBUG_LEVEL;
    private static final String DEFAULT_TAG = LogUtil.class.getSimpleName();

    public void setLevel(int level) {
        LEVEL = level;
    }

    public void setNoShowLog() {
        setLevel(NO_SHOW_LEVEL);
    }

    public void v(String msg) {
        v(DEFAULT_TAG, msg);
    }

    public void v(String tag, String msg) {
        if (VERBOSE_LEVEL >= LEVEL) {
            Log.v(tag, msg);
        }
    }

    public void d(String msg) {
        d(DEFAULT_TAG, msg);
    }

    public void d(String tag, String msg) {
        if (DEBUG_LEVEL >= LEVEL) {
            Log.d(tag, msg);
        }
    }

    public void i(String msg) {
        i(DEFAULT_TAG, msg);
    }

    public void i(String tag, String msg) {
        if (INFO_LEVEL >= LEVEL) {
            Log.i(tag, msg);
        }
    }

    public void w(String msg) {
        w(DEFAULT_TAG, msg);
    }

    public void w(String tag, String msg) {
        if (WARN_LEVEL >= LEVEL) {
            Log.w(tag, msg);
        }
    }

    public void e(String msg) {
        e(DEFAULT_TAG, msg);
    }

    public void e(String tag, String msg) {
        if (ERROR_LEVEL >= LEVEL) {
            Log.e(tag, msg);
        }
    }


    /**
     * need permissions
     * <uses-permission android:name="android.permission.READ_LOGS" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     */
    public void storeLog() {
        if (isExternalStorageWritable()) {

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/lsxLog");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() / 1000 + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    LogUtil(){
    }


    public static BufferedWriter out;

//    private void createFileOnDevice(Boolean append) throws IOException {
//        /*
//         * Function to initially create the log file and it also writes the time of creation to file.
//         */
//        File Root = Environment.getExternalStorageDirectory();
//        if (Root.canWrite()) {
//            File LogFile = new File(Root, "Log.txt");
//            FileWriter LogWriter = new FileWriter(LogFile, append);
//            out = new BufferedWriter(LogWriter);
//            Date date = new Date();
//            out.write("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n"));
//            out.close();
//        }
//    }

    public void writeToFile(Exception e){
        writeToFile("message:" + e.getMessage() + ";toString:" + e.toString());
    }

    public void writeToFile(String message) {
        try {
            File Root = Environment.getExternalStorageDirectory();
            if (Root.canWrite()) {
                File LogFile = new File(Root, "LogLee.txt");
                FileWriter LogWriter = new FileWriter(LogFile, true);
                out = new BufferedWriter(LogWriter);
                Date date = new Date();
                out.write("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n"));
                out.write(message);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
