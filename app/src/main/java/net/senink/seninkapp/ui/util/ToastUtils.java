package net.senink.seninkapp.ui.util;

import android.content.Context;
import android.widget.Toast;

/**
 * toast提示框
 * 
 * @author zhaojunfeng
 * @date 2015-11-02
 */

public class ToastUtils {
	
	public static void showToast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String str, int time) {
		Toast.makeText(context, str, time).show();
	}

	public static void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int resId, int time) {
		Toast.makeText(context, resId, time).show();
	}

}
