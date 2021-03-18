package net.senink.seninkapp.ui.util;

import net.senink.seninkapp.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class Utils {

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
		return (int) px;
	}

	public static int getRelativeTop(View myView) {
		if (myView.getId() == android.R.id.content)
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	public static int getRelativeLeft(View myView) {
		if (myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	/**
	 * 获取红绿蓝三色值
	 * 
	 * @param color
	 * @return
	 */
	public static int[] getRGB(int color) {
		int[] colors = new int[3];
		colors[0] = Color.red(color);
		colors[1] = Color.green(color);
		colors[2] = Color.blue(color);
		return colors;
	}

	/**
	 * 根据手机分辨率从 dp 单位 转成 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 初始化加载框
	 */
	public static ProgressDialog createProgressDialog(Activity activity,
			int resId) {
		ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		String msg = activity.getString(resId);
		progressDialog.setMessage(msg);
		return progressDialog;
	}

	/**
	 * 创建等待框
	 * 
	 * @param activity
	 * @return
	 */
	@SuppressLint("InflateParams")
	public static AlertDialog createProgressDialog(Activity activity) {
		View view = LayoutInflater.from(activity).inflate(R.layout.loading,
				null);
		ImageView iv = (ImageView) view.findViewById(R.id.loading_anima);
		AnimationDrawable anima = (AnimationDrawable) iv.getBackground();
		anima.start();
		AlertDialog dialog = new AlertDialog.Builder(activity).setView(view)
				.create();
		dialog.setCancelable(false);
		return dialog;
	}

	/**
	 * 获取设备类型 2：网关 3：灯 4：遥控器
	 * 
	 * @param mac
	 *            从蓝牙设备获取的mac地址
	 * @return
	 */
	public static int getType(String mac) {
		int type = 3;
		if (mac != null && mac.length() == 16) {
			try {
				type = Integer.parseInt(mac.substring(10, 12));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return type;
	}

	public static void printMemoryInfor(String tag, String className) {
//		int maxMemory = ((int) Runtime.getRuntime().maxMemory()) / 1024;
//		// 应用程序已获得内存
//		long totalMemory = ((int) Runtime.getRuntime().totalMemory()) / 1024;
//		// 应用程序已获得内存中未使用内存
//		long freeMemory = ((int) Runtime.getRuntime().freeMemory()) / 1024;
		LogUtils.i(tag, className + "---> maxMemory=");
//		+ maxMemory
//				+ "K,totalMemory=" + totalMemory + "K,freeMemory=" + freeMemory
//				+ "K");
	}

	/**
	 * 获取手机串号
	 * 
	 * @param context
	 * @return
	 */
	public static String getMIEIOnTelephone(Context context) {
		String miei = "";
		TelephonyManager telephonemanage = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			miei = telephonemanage.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return miei;
	}

	/**
	 * 获取手机的型号
	 * 
	 * @return
	 */
	public static String getModelOnTelephone() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取手机系统的版本号
	 * 
	 * @return
	 */
	public static String getVersionOnSystem() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionOnApplication(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取应用的名称
	 */
	public static String getApplicationName(Context context) {
		return context.getString(R.string.app_name);
	}

}
