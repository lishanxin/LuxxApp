package net.senink.seninkapp.ui.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinRemoter;
import net.senink.seninkapp.Foreground;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;

import net.senink.seninkapp.sqlite.UserService;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.ProduceModel;
import net.senink.seninkapp.ui.entity.CacheManager;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.upgrade.DownApkThread;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.services.PISXinCenter;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;

/**
 * 公共工具类
 * 
 */
@SuppressWarnings("deprecation")
public class CommonUtils {

	public static String ip;

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/** 获取正确的afid */
	public static String getCorrectAfid(String lastLoginAfid) {
		if (!"".equals(lastLoginAfid)
				&& null != lastLoginAfid
				&& (lastLoginAfid.startsWith("a") || lastLoginAfid
						.startsWith("r"))) {
			lastLoginAfid = lastLoginAfid.substring(1, lastLoginAfid.length());
			return lastLoginAfid;
		}
		return "";
	}

	/** 判断是否符合邮箱格式 */
	public static boolean isEmail(String email) {
		if (!TextUtils.isEmpty(email)) {
			String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			Pattern pattern = Pattern.compile(strPattern);
			Matcher m = pattern.matcher(email);
			return m.matches();
		}
		return false;
	}

	/** get the right pattern imei */
	public static String getRightIMEI(String imei) {
		if (!"".equals(imei) && null != imei && !"null".equals(imei)) {
			int length = imei.length();
			for (int i = 0; i < length; i++) {
				if (Character.isLetter(imei.charAt(i))) {
					imei = imei.replace(imei.charAt(i), '0');
				}
			}
			int imeiPatterLength = 15;// imei standard pattern length
			if (length < imeiPatterLength) {
				for (int i = 0; i < imeiPatterLength; i++) {
					imei += "9";
					if (imei.length() == imeiPatterLength) {
						break;
					}
				}
			} else if (length > imeiPatterLength) {
				imei = imei.substring(0, imeiPatterLength);
			}
		}
		return imei;
	}

	public static boolean isCanUseSim(Context context) {
		try {
			TelephonyManager mgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = inetAddress.getHostAddress().toString();
						// LogUtils.d("ip", ip + "");
						setIp(ip);
						return;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference", ex.toString());
		}
		setIp(null);
	}

	public static void setIp(String ip) {
		if (verifi(ip)) {
			CommonUtils.ip = ip;
		} else {
			CommonUtils.ip = "0.0.0.0";
		}
	}

	private static boolean verifi(String ip) {
		if (ip == null) {
			return false;
		}
		String patter = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
		Pattern p = Pattern.compile(patter);
		Matcher m = p.matcher(ip);
		return m.find() ? true : false;
	}

	/**
	 * true 为空 false 不为空
	 */
	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str) || "".equals(str.trim())
				|| "null".equals(str)) {
			return true;
		}
		return false;
	}

	/** 字符串转换成数字 */
	public static int stringToInt(String str) {
		return Integer.getInteger(str);
	}

	/**
	 * 判断网络是否畅通
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null)
			return false;
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null)
			return false;
		if (networkInfo.isConnected())
			return true;
		return false;
	}

	@SuppressWarnings("finally")
	public static String getCurrentVersion(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				versionName = "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception " + e.getMessage());
		} finally {
			return versionName;
		}
	}

	/** 打印请求以及返回来的log */
	@SuppressLint("SimpleDateFormat")
	public static void writeToSdcard(String log, Context context) {
		// if (SeninkLogUtils.DEBUG) {
		if (isHasSDCard()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String filename = getCurrentVersion(context);
			File file = new File(PISConstantDefine.LOGS_CACHE + "/" + filename
					+ ".txt");
			try {
				if (!file.exists())
					file.createNewFile();
				// 创建一个文件输出流
				FileOutputStream out = new FileOutputStream(file, true);// true表示在文件末尾添加
				String msg = new String(dateFormat.format(new Date()) + "\t\t"
						+ log + "\n\n");
				out.write(msg.getBytes("UTF-8"));
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("log:::", "log:::  " + e.getMessage());
			}
		}
		// }
	}

	@SuppressLint("SimpleDateFormat")
	public static String saveCrashInfo2File(Throwable ex, Context context) {
		// if (SeninkLogUtils.DEBUG) {
		if (isHasSDCard()) {
			StringBuffer sb = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			Writer writer = new StringWriter();
			PrintWriter pw = new PrintWriter(writer);
			ex.printStackTrace(pw);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(pw);
				cause = cause.getCause();
			}
			pw.close();
			String result = writer.toString();
			String time = format.format(new Date());
			sb.append(time + "==  ==  ==" + result);

			long timetamp = System.currentTimeMillis();

			String fileName = getCurrentVersion(context) + "-crash-" + time
					+ "-" + timetamp + ".log";

			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "crash");
				Log.i("CrashHandler", dir.toString());
				if (!dir.exists())
					dir.mkdir();
				FileOutputStream fos = new FileOutputStream(new File(dir,
						fileName));
				fos.write(sb.toString().getBytes());
				fos.close();
				return fileName;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// }
		return null;
	}

	/**
	 * 判断是否存在sdcard
	 * 
	 * @return true存在 false则反
	 */
	public static boolean isHasSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/***
	 * true 代表可以选择的日期 false代表当前日期不可选择
	 * 
	 * @param yearText
	 *            选择的年份
	 * @param monthText
	 *            选择的月份
	 * @param dayText
	 *            选择的天数
	 * @return
	 */
	public static boolean compareDate(CharSequence yearText,
			CharSequence monthText, CharSequence dayText, View view) {
		try {
			Date selected = df
					.parse(yearText + "-" + monthText + "-" + dayText);
			Date today = new Date();
			if (selected.getTime() - today.getTime() < 0) {
				view.setEnabled(true);
				view.setClickable(true);
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		view.setEnabled(false);
		view.setClickable(false);
		return false;
	}

	/**
	 * 替换字符串
	 * 
	 * @param targetConstant
	 *            将要被替换的特殊字符
	 * @param targetName
	 *            被替换成的字符串
	 * @param str
	 *            整个字符串
	 * @return 替换后的字符串
	 */
	public static String replace(String targetConstant, String targetName,
			String str) {
		str = str.replace(targetConstant, targetName);
		return str;
	}

	public static void closeSoftKeyBoard(View editText) {
		InputMethodManager imm = (InputMethodManager) editText.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		// ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ChattingRoomMessagesActivity.this.getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void showSoftKeyBoard(View editText) {
		InputMethodManager inputManager = (InputMethodManager) editText
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, 0);
		// ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ChattingRoomMessagesActivity.this.getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static final String CONTENT_URI_SMS_CONVERSATIONS = "content://sms/conversations";
	public static final String CONTENT_URI_SMS = "content://sms";
	public static String[] THREAD_COLUMNS = new String[] { "thread_id",
			"msg_count", "snippet"

	};
	public static String[] SMS_COLUMNS = new String[] { "_id", // 0
			"thread_id", // 1
			"address", // 2
			"person", // 3
			"date", // 4
			"body", // 5
			"read", // 6; 0:not read 1:read; default is 0
			"type", // 7; 0:all 1:inBox 2:sent 3:draft 4:outBox 5:failed
					// 6:queued
			"service_center" // 8
	};

	public static long getThreadsNum(int thread_id, Context context) {
		Cursor cursor = null;
		ContentResolver contentResolver = context.getContentResolver();
		cursor = contentResolver.query(Uri.parse(CONTENT_URI_SMS), SMS_COLUMNS,
				"thread_id = " + thread_id, null, null);
		if (cursor == null || cursor.getCount() == 0)
			return -1;
		cursor.moveToFirst();
		long date = cursor.getLong(4);
		cursor.close();
		return date;
	}

	/** 比较最近一条短信的时间和当前时间比较是否小于30秒,小于(返回true)则认为是最新收到的短信否则(返回false)不是最新收到的短信 */
	public static boolean diffBetweenTwoTime(long time1, long time2) {
		long l;
		if (time1 > time2) {
			l = time1 - time2;
		} else {
			l = time2 - time1;
		}
		long second = l / 1000;
		return second < 30;
	}

	/** 获取最近一条短信的内容 */
	public static String getLastSmsInfo(Context context) {
		Cursor cursor = null;
		ContentResolver contentResolver = context.getContentResolver();
		try {
			cursor = contentResolver.query(
					Uri.parse(CONTENT_URI_SMS_CONVERSATIONS), THREAD_COLUMNS,
					null, null, "date desc");
			if (cursor == null || cursor.getCount() == 0)
				return null;
			String content = null;
			cursor.moveToFirst();
			int thread_id = cursor.getInt(0);
			long date = getThreadsNum(thread_id, context);
			if (date == -1 || date == 0)
				return null;
			boolean flag = diffBetweenTwoTime(date, System.currentTimeMillis());
			content = cursor.getString(2);
			Log.e("content", "thread_id  " + thread_id + "  date  " + date
					+ "  content  " + content);
			if (!flag)
				return null;
			return content;
		} catch (Exception e) {
			if (cursor != null) {
				cursor.close();
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = null;
		try {
			if (null != bitmap) {
				output = Bitmap.createBitmap(bitmap.getWidth(),
						bitmap.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);

				// 将画布的四角圆化
				final int color = Color.RED;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(),
						bitmap.getHeight());
				final RectF rectF = new RectF(rect);
				// 值越大角度越明显
				final float roundPx = 8;// 圆角不明显，暂时改为8 modify by zhangkun
										// 20110830

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);
			}
		} catch (OutOfMemoryError e) {
			if (null != output && !output.isRecycled()) {
				output.recycle();
				output = null;
			}
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * convert byte[] to bitmap
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		if (null == bm) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 将byte[]转成Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap BytesToBimap(byte[] b) {
		try {
			if (null != b && b.length != 0) {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			} else {
				return null;
			}
		} catch (OutOfMemoryError oom) {
			Log.i("error", " --------lowmemory---------  StringUtil(line 511)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getOrientation(Context context) {
		return context.getResources().getConfiguration().orientation;
	}

	public static void copy(TextView tagView, Context context) {
		int selStart = tagView.getSelectionStart();
		int selEnd = tagView.getSelectionEnd();

		if (!tagView.isFocused()) {
			selStart = 0;
			selEnd = tagView.getText().toString().length();
		}

		int min = Math.min(selStart, selEnd);
		int max = Math.max(selStart, selEnd);

		if (min < 0) {
			min = 0;
		}
		if (max < 0) {
			max = 0;
		}

		ClipboardManager clip = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(tagView.getText());
	}

	// sdcard是否可读写
	public static boolean IsCanUseSdCard() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static public char getSortKey(String str) {
		if (!TextUtils.isEmpty(str)) {
			char ch = str.charAt(0);
			if (ch >= 'A' && ch <= 'Z') {
				return ch;
			} else if (ch >= 'a' && ch <= 'z') {
				return (char) (ch - 'a' + 'A');
			}
		}
		// 放置在最后面
		return 'a';
	}

	public static String showImageSize(int fileSize) {
		int size = fileSize / 1024;
		if (fileSize % 1024 > 0) {
			size = size + 1;
		}
		return (size) + "kb";
	}

	public static String diffTime(long cur, long last) {
		int diff = (int) ((cur - last) / 100);
		int ms = (diff / 10);
		int dms = diff % 10;
		return ms + "." + dms + "s";
	}

	public static String getRealPhoneNO(String phoneNO) {
		// SeninkLogUtils.println("getRealPhoneNO  " + phoneNO);
		if (!isEmpty(phoneNO) && phoneNO.startsWith("0")) {
			return phoneNO.substring(1);
		}
		return phoneNO;
	}

	public static String getCurrentActivity(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		return cn.getClassName();
	}

	public static boolean isEmpty(String str, boolean isToTrim) {
		if (null == str) {
			return true;
		}
		if (isToTrim) {
			if (0 >= str.trim().length()) {
				return true;
			}
		} else {
			if (0 >= str.length()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 跳转到其他界�?
	 * 
	 * @param cls
	 *            跳转页面
	 * @param bundle
	 *            Bundle参数
	 * @param isReturn
	 *            是否返回
	 * @param requestCode
	 *            请求Code
	 * @param isFinish
	 *            是否�?��当前页面
	 */
	public static void jumpToPage(Activity context, Class<?> cls,
			Bundle bundle, boolean isReturn, int requestCode, boolean isFinish) {
		if (cls == null) {
			return;
		}

		Intent intent = new Intent();
		intent.setClass(context, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}

		if (isReturn) {
			context.startActivityForResult(intent, requestCode);
		} else {

			try {
				context.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (isFinish) {
			context.finish();
		}
	}

	/**
	 * 字节数组转字符串
	 */
	public static String byteArrayToString(byte[] byBuffer) {
		String strRead = new String(byBuffer);
		strRead = String.copyValueOf(strRead.toCharArray(), 0, byBuffer.length);
		return strRead;
	}

	public static String tokenerString(String str) {
		if (null == str || str.equals(PISConstantDefine.NULL)) {
			str = "";
		}
		return str;
	}

	public static boolean equals(String org, String des) {
		if ((null == org && null == des)
				|| (org != null && des != null && org.equals(des))) {
			return true;
		}
		return false;
	}

	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (null == bitmap) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static String getPosition(Context context, String position) {
		String[] array = context.getResources().getStringArray(
				R.array.position_array);
		int index = 0;
		if (position != null && position.length() > 2
				&& position.startsWith("0x")) {
			// index = Integer.parseInt(position);
			position = position.substring(2);
			index = Integer.valueOf(position, 16);
		} else {
			return array[0];
		}
		if (array != null && array.length > 0) {
			if (index < array.length) {
				return array[index];
			}
		}
		return array[0];
	}

	// 将字符串转为时间戳
	@SuppressLint("SimpleDateFormat")
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		Date d;
		try {

			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	// 将时间戳转为字符串
	@SuppressLint("SimpleDateFormat")
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;

	}

	/**
	 * 获取Android手机上面的UUID
	 * 
	 * @param context
	 * @return
	 */
	public static String getUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

	/**
	 * 获取当前的手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getTelephoneNumber(Context context) {
		String number = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		number = telephonyManager.getLine1Number();
		return number;
	}

	/**
	 * 获取当前包的信息
	 * 
	 * @param context
	 * @return
	 */
	public static PackageInfo getApkInfor(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	/**
	 * 安装apk
	 * 
	 * @param context
	 * @param path
	 */
	public static void installApk(Context context, String path) {
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				context.startActivity(intent);
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param urlFile
	 * @param savePath
	 * @return
	 */
	public static boolean downloadFile(String urlFile, String savePath) {
		boolean success = false;
		try {
			URL url = new URL(urlFile);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			FileOutputStream fileOutputStream = null;
			InputStream inputStream;
			if (connection.getResponseCode() == 200) {
				inputStream = connection.getInputStream();
				if (inputStream != null) {
					File file = new File(savePath);
					file.getParentFile().mkdirs();
					if (savePath.contains("/data/data")) {
						chmodDataDir(savePath);
					}
					fileOutputStream = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int length = 0;
					while ((length = inputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, length);
					}
					fileOutputStream.close();
					fileOutputStream.flush();
				}
				inputStream.close();
				success = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return success;
	} // End downloadFile

	/**
	 * 为/data/data/packagename下的目录一层一层赋予777权限，直至/data/data目录
	 * 
	 * @param dataPath
	 */
	@SuppressLint("SdCardPath")
	public static boolean chmodDataDir(String dataPath) {
		boolean isRight = false;
		try {
			ProcessBuilder builder = null;
			String[] command = null;
			while (dataPath != null && dataPath.contains("/data/data/")
					&& !dataPath.endsWith("/data")
					&& !dataPath.endsWith("/data/")) {
				command = new String[] { "chmod", "777", dataPath };
				builder = new ProcessBuilder(command);
				builder.start();
				dataPath = new File(dataPath).getParent();
			}
			isRight = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isRight;
	}

	/**
	 * 显示版本升级框
	 */
	@SuppressLint("InflateParams")
	public static void showUpgradeDialog(final Context context,
			final String apkName, final String apkUrl) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_upgrade, null);
		TextView tvName = (TextView) view
				.findViewById(R.id.dialog_upgrade_versionname);
		Button cancelBtn = (Button) view
				.findViewById(R.id.dialog_upgrade_cancel);
		ImageButton upgradeBtn = (ImageButton) view
				.findViewById(R.id.dialog_upgrade_btn);
		tvName.setText(apkName == null ? "" : apkName);
		final AlertDialog upgradeDialog = new AlertDialog.Builder(context,
				R.style.upGradeDialog).setView(view).create();
		upgradeDialog.setInverseBackgroundForced(true);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upgradeDialog.dismiss();
			}
		});
		upgradeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				upgradeDialog.dismiss();
				if (!TextUtils.isEmpty(apkUrl)) {
					new DownApkThread(context, apkUrl).start();
				}
			}
		});
		upgradeDialog.show();
	}

	/**
	 * 显示版本升级框
	 */
	public static void showUpgradeDialog(final Context context,
			final View view, final String apkName, final String apkUrl) {
		TextView tvName = (TextView) view
				.findViewById(R.id.dialog_upgrade_versionname);
		Button cancelBtn = (Button) view
				.findViewById(R.id.dialog_upgrade_cancel);
		ImageButton upgradeBtn = (ImageButton) view
				.findViewById(R.id.dialog_upgrade_btn);
		tvName.setText(apkName == null ? "" : apkName);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				view.setVisibility(View.GONE);
			}
		});
		upgradeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				view.setVisibility(View.GONE);
				if (!TextUtils.isEmpty(apkUrl)) {
					new DownApkThread(context, apkUrl).start();
				}
			}
		});
		view.setVisibility(View.VISIBLE);
	}

	/**
	 * 是否是手机号码
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isTelNumber(String num) {
		boolean isTel = false;
		if (!TextUtils.isEmpty(num) && num.length() == 11) {
			Pattern p = Pattern
					.compile("^((1[0-9]))\\d{9}$");
//					.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
			Matcher m = p.matcher(num);
			isTel = m.matches();
		}
		return isTel;
	}
//
//	/**
//	 * 序列化PISManager对象：保存
//	 *
//	 * @param manager
//	 * @param path
//	 * @return
//	 */
//	public static boolean savePISManager(PISManager manager, String path) {
//		boolean success = false;
//		PISMCSManager mcm = manager.getPisMcmObject();
//		List<String> macs = null;
//		if (mcm != null && mcm.getBindedDeviceMacList() != null
//				&& mcm.getBindedDeviceMacList().size() > 0) {
//			macs = mcm.getBindedDeviceMacList();
//		} else if (net.senink.seninkapp.ui.cache.CacheManager.bindedMacs != null
//				&& net.senink.seninkapp.ui.cache.CacheManager.bindedMacs.size() > 0) {
//			macs = net.senink.seninkapp.ui.cache.CacheManager.bindedMacs;
//		}
//		LogUtils.i("SaveManager", "savePISManager(): before macs = ");
//		if (macs != null && macs.size() > 0) {
//			LogUtils.i("SaveManager",
//					"savePISManager(): macs.size() = " + macs.size());
//		} else {
//			LogUtils.i("SaveManager", "savePISManager(): macs == null");
//		}
//		CacheManager cm = new CacheManager();
//		cm.mPISMap = new HashMap<String, PISBase>();
//		if (macs != null && macs.size() > 0) {
//			try {
//				synchronized (PISManager.mPISMap) {
//					Iterator<Entry<String, PISBase>> iter = PISManager.mPISMap
//							.entrySet().iterator();
//					while (iter.hasNext()) {
//						Map.Entry<String, PISBase> entry = (Map.Entry<String, PISBase>) iter
//								.next();
//						PISBase pis = (PISBase) entry.getValue();
//						if (!TextUtils.isEmpty(pis.macAddr)) {
//							String mac = com.senink.seninkapp.core.CommonUtils
//									.getStringOnMacAddr(pis.macAddr);
//							for (String macAddr : macs) {
//								macAddr = com.senink.seninkapp.core.CommonUtils
//										.getStringOnMacAddr(macAddr);
//								if (!TextUtils.isEmpty(macAddr)
//										&& mac.equals(macAddr)) {
//									if (!(pis instanceof PISMCSManager)) {
//										cm.mPISMap.put(pis.getPISKeyString(),
//												pis);
//										break;
//									}
//								}
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				LogUtils.i("SaveManager", "savePISManager(): in macs error = "
//						+ e.getMessage());
//			}
//		}
//		LogUtils.i("SaveManager",
//				"savePISManager(): after macs  cm.mPISMap.size() = "
//						+ cm.mPISMap.size());
//		if (manager.getDeviceGroupInfors() != null
//				&& manager.getDeviceGroupInfors().size() > 0) {
//			if (null == cm.deviceGroupInfors) {
//				cm.deviceGroupInfors = new HashMap<Short, PisDeviceGroup>();
//			} else {
//				cm.deviceGroupInfors.clear();
//			}
//			int size = manager.getDeviceGroupInfors().size();
//			for (int i = 0; i < size; i++) {
//				PisDeviceGroup group = manager.getDeviceGroupInfors()
//						.valueAt(i);
//				cm.deviceGroupInfors.put(group.groupId, group);
//			}
//		}
//		File file = new File(path);
//		if (file.exists()) {
//			file.delete();
//		}
//		file.getParentFile().mkdirs();
//		ObjectOutputStream oos = null;
//		LogUtils.i("SaveManager", "savePISManager(): before ios = ");
//		try {
//			FileOutputStream fos = new FileOutputStream(file);
//			oos = new ObjectOutputStream(fos);
//			oos.writeObject(cm);
//			LogUtils.i("SaveManager", "savePISManager(): try ios = ");
//			oos.flush();
//			success = true;
//		} catch (Exception e) {
//			LogUtils.i("SaveManager",
//					"savePISManager(): error = " + e.getMessage());
//			// e.printStackTrace();
//		} finally {
//			if (oos != null) {
//				try {
//					oos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		LogUtils.i("SaveManager", "savePISManager(): success = " + success);
//		return success;
//	}
//
//	/**
//	 * 序列化PISManager对象:获取
//	 *
//	 * @param path
//	 * @return
//	 */
//	public static CacheManager getPISManager(String path) {
//		File file = new File(path);
//		CacheManager manager = null;
//		if (file.exists()) {
//			ObjectInputStream ois = null;
//			try {
//				FileInputStream fos = new FileInputStream(file);
//				ois = new ObjectInputStream(fos);
//				Object obj = null;
//				while ((obj = (CacheManager) ois.readObject()) != null) {
//					manager = (CacheManager) obj;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (ois != null) {
//					try {
//						ois.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		LogUtils.i("SaveManager", "getPISManager(): manager = "
//				+ (manager == null));
//		return manager;
//	}
//
//	/**
//	 * 保存PISManager序列化对象并把保存地址存到数据库中
//	 */
//	@SuppressLint("SdCardPath")
//	public static void savePisManager(final HomeActivity context,
//									  final PISManager manager, final UserService mUserService) {
//		String ph = null;
//		if (Environment.getExternalStorageDirectory().exists()) {
//			ph = Environment.getExternalStorageDirectory().getAbsolutePath()
//					+ Constant.SAVE_PATH;
//		} else {
//			if (CommonUtils.getApkInfor(context) != null) {
//				ph = "/data/data/"
//						+ CommonUtils.getApkInfor(context).packageName
//						+ Constant.SAVE_PATH;
//				boolean result = CommonUtils.chmodDataDir(new String(ph));
//				if (!result) {
//					ph = null;
//				}
//			}
//		}
//		File fl = new File(ph);
//		File[] files = fl.listFiles(new FilenameFilter() {
//
//			@Override
//			public boolean accept(File dir, String filename) {
//				String prex = MeshController.KEY + "_";
//				if (filename.startsWith(prex)) {
//					return true;
//				}
//				return false;
//			}
//		});
//		if (files != null && files.length > 0) {
//			for (File file : files) {
//				file.delete();
//			}
//			files = null;
//		}
//		if (!TextUtils.isEmpty(ph)) {
//			final String path = ph + MeshController.KEY + "_"
//					+ +System.currentTimeMillis() + ".txt";
//			// 删除以前的数据
//			if (!TextUtils.isEmpty(PISManager.savePath)) {
//				File file = new File(PISManager.savePath);
//				if (file.exists()) {
//					file.delete();
//				}
//			}
//			if (mUserService != null && manager != null) {
//				new Thread() {
//					@Override
//					public void run() {
//						boolean success = savePISManager(manager, path);
//						if (success) {
//							mUserService.insertUser(SharePreferenceUtils
//									.getInstance(context).getUsername(), path);
//						}
//						context.runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								context.finish();
//							}
//						});
//					}
//				}.start();
//			}
//		}
//	}
//
//	/**
//	 * 序列化的对象转为二进制数组
//	 */
//	public static byte[] PISBaseToByteArray(PISBase base) {
//		byte[] data = null;
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		ObjectOutputStream oos = null;
//		try {
//			oos = new ObjectOutputStream(out);
//			oos.writeObject(base);
//			oos.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (oos != null) {
//				try {
//					oos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			data = out.toByteArray();
//		}
//		return data;
//	}
//
//	public static byte[] PisDeviceGroupToByteArray(PisDeviceGroup group) {
//		byte[] data = null;
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		ObjectOutputStream oos = null;
//		try {
//			oos = new ObjectOutputStream(out);
//			oos.writeObject(group);
//			oos.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (oos != null) {
//				try {
//					oos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			data = out.toByteArray();
//		}
//		return data;
//	}
//
//	/**
//	 * 反序列
//	 */
//	public static PISBase byteArrayToPISBase(byte[] data) {
//		ByteArrayInputStream in = new ByteArrayInputStream(data);
//		ObjectInputStream ois = null;
//		PISBase base = null;
//		try {
//			ois = new ObjectInputStream(in);
//			base = (PISBase) ois.readObject();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (ois != null) {
//				try {
//					ois.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return base;
//	}
//
//	public static PisDeviceGroup byteArrayToPisDeviceGroup(byte[] data) {
//		ByteArrayInputStream in = new ByteArrayInputStream(data);
//		ObjectInputStream ois = null;
//		PisDeviceGroup base = null;
//		try {
//			ois = new ObjectInputStream(in);
//			base = (PisDeviceGroup) ois.readObject();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (ois != null) {
//				try {
//					ois.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return base;
//	}

	/**
	 * 通过classId获取对应设备的类型
	 * 
	 * @param classId
	 * @return 1：LIGTHLED 2：BLUEBLINKER 3：LIGHTRGB 4：REMOTER 5：SWITCH 6:INSOLE
	 */
	public static int getDeviceTypeFromClassId(String classId) {
		int type = 0;
		if (!TextUtils.isEmpty(classId)) {
			if (ProduceModel.BLE_BLINK_CLASSID.equals(classId)) {
				type = 2;
			} else if (ProduceModel.BLE_COLORBUBBLE_CLASSID.equals(classId)
					|| ProduceModel.BLE_COLORBUBBLE_CLASSID_1.equals(classId)
					|| ProduceModel.BLE_COLORBUBBLE_CLASSID_2.equals(classId) ) {
				type = 3;
			} else if (ProduceModel.BLE_LED_CLASSID.equals(classId)) {
				type = 1;
			} else if (ProduceModel.BLE_REMOTER_CLASSID.equals(classId)
					|| ProduceModel.BLE_REMOTER_CLASSID_1.equals(classId)) {
				type = 4;
//			} else if (ProduceModel.SWITCH_CLASSID.equals(classId)) {
			} else if (ProduceModel.BLE_CANDLE_CLASSID.equals(classId)) {

				type = 5;
			} else if (ProduceModel.INSOLE_CLASSID.equals(classId)) {
				type = 6;
			}
		}
		return type;
	}

	/**
	 * 通过PISBase对象类型获取对应设备的类型
	 * 
	 * @param base
	 * @return 1：LIGTHLED 2：BLUEBLINKER 3：LIGHTRGB 4：REMOTER
	 */
	public static int getDeviceTypeFromPISBase(Object base) {
		int type = 0;
		if (base != null) {
			if (base instanceof PISXinCenter) {
				type = 2;
			} else if (base instanceof PISxinColor) {
				type = 3;
			} else if (base instanceof PISXinLight) {
				type = 1;
			} else if (base instanceof PISXinRemoter) {
				type = 4;
			}
		}
		return type;
	}

	/**
	 * 设置activity在后台运行
	 * 
	 * @param context
	 */
	public static void setRunningOnBackground(Context context) {
		PackageManager pm = context.getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(
				new Intent(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME), 0);
		ActivityInfo ai = homeInfo.activityInfo;
		Intent startIntent = new Intent(Intent.ACTION_MAIN);
		startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
		startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(startIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回主界面
	 * 
	 * @param context
	 */
	public static void backToMain(Activity context) {
		Foreground.exitToActivity(context, HomeActivity.class);
		context.overridePendingTransition(R.anim.anim_in_from_left,
				R.anim.anim_out_to_right);
	}

	public static float[] getScreenInfor(Activity context) {
		float[] content = new float[3];
		Display display = context.getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		content[0] = displayMetrics.density; // 得到密度
		content[1] = displayMetrics.widthPixels;// 得到宽度
		content[2] = displayMetrics.heightPixels;// 得到高度
		return content;
	}
}
