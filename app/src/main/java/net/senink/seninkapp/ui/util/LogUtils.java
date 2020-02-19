package net.senink.seninkapp.ui.util;

import android.util.Log;

/**
 * 用于打印日志的类
 * 
 * @author zhaojunfeng
 * @date 2015-06-29
 */
public class LogUtils {
	public static void i(String tag, String msg) {
		if (!net.senink.seninkapp.BuildConfig.Release) {
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (!net.senink.seninkapp.BuildConfig.Release) {
			Log.e(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (!net.senink.seninkapp.BuildConfig.Release) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		Log.w(tag, msg);
	}
}
