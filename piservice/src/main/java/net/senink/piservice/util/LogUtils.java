package net.senink.piservice.util;

/**
 * Created by wensttu on 2016/6/20.
 */

import android.util.Log;

/**
 * 用于打印日志的类
 *
 * @author zhaojunfeng
 * @date 2015-06-29
 */
public class LogUtils {

    public static boolean isTest = true;

    public static final String TAG = "Ryan";

    public static void i(String tag, String msg) {
        if (isTest) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isTest) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isTest) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isTest) {
            Log.w(tag, msg);
        }
    }
}

