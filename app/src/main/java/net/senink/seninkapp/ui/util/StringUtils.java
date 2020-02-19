package net.senink.seninkapp.ui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import net.senink.seninkapp.R;

/**
 * 字符转换的工具类
 * 
 * @author zhaojunfeng
 * @date 2015-12-16
 * 
 */

public class StringUtils {

	public static String getMD5(String val) {
		byte[] m = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());
			m = md5.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getString(m);
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}

	public static String getNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH",
				Locale.getDefault());
		Date date = new Date(System.currentTimeMillis());
		sdf.format(date);
		return sdf.format(date);
	}

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	/**
	 * 对密码进行md5加密
	 * 
	 * @param password
	 * @return
	 */
	public static String getPKey(String password) {
		return MD5(MD5(MD5("android3t6i") + getNowDate()) + password); // MD5(MD5(password)));
	}

	/**
	 * 把字节数组转换为年月日周（字节数组包含20151230+周（从周一到周日七个字符0123456）+时间（12:36）） 格式： 2015
	 * 
	 * @param data
	 * @param context
	 */
	public static String getDay(Context context, byte[] data) {
		String str = null;
		if (data != null && data.length > 0) {
			if (data.length >= 8) {
				byte[] year = new byte[8];
				System.arraycopy(data, 0, year, 0, 8);
				str = ByteUtilBigEndian.ByteToString(year);
				if (!TextUtils.isEmpty(str) && str.length() >= 8) {
					if (str.startsWith("xxxx") && str.endsWith("xxxx")) {
						str = "";
					} else if (str.startsWith("xxxx") && !str.endsWith("xxxx")) {
						if (str.startsWith("xxxxxx")) {
							str = context.getString(R.string.everymonth)
									+ str.substring(6, 8)
									+ context.getString(R.string.day);
						} else {
							str = context.getString(R.string.everyyear)
									+ str.substring(4, 6)
									+ context.getString(R.string.month)
									+ str.substring(6, 8)
									+ context.getString(R.string.day);
						}
					} else if (!str.startsWith("xxxx") && !str.endsWith("xxxx")) {
						str = str.substring(0, 4)
								+ context.getString(R.string.year)
								+ str.substring(4, 6)
								+ context.getString(R.string.month)
								+ str.substring(6, 8)
								+ context.getString(R.string.day);
					}
					if (!TextUtils.isEmpty(str)) {
						str = str + " ";
					}
				}
			}
			if (data.length >= 15) {
				byte[] weeks = new byte[7];
				System.arraycopy(data, 8, weeks, 0, 7);
				String temp = ByteUtilBigEndian.ByteToString(weeks);
				LogUtils.i("ddddd", "=====temp = " + temp);
				if (!TextUtils.isEmpty(temp) && temp.length() >= 7
						&& !temp.equals("xxxxxxx")) {
					if (temp.substring(1, 2).equals("1")) {
						str = str + context.getString(R.string.monday) + " ";
					}
					if (temp.substring(2, 3).equals("2")) {
						str = str + context.getString(R.string.tuesday) + " ";
					}
					if (temp.substring(3, 4).equals("3")) {
						str = str + context.getString(R.string.wednesday) + " ";
					}
					if (temp.substring(4, 5).equals("4")) {
						str = str + context.getString(R.string.thursday) + " ";
					}
					if (temp.substring(5, 6).equals("5")) {
						str = str + context.getString(R.string.friday) + " ";
					}
					if (temp.substring(6).equals("6")) {
						str = str + context.getString(R.string.saturday) + " ";
					}
					if (temp.substring(0, 1).equals("0")) {
						str = str + context.getString(R.string.sunday);
					}
					str = str.trim();
				}
			}
			if (data.length >= 15 && TextUtils.isEmpty(str)) {
				str = context.getString(R.string.everyday);
			}
		}
		return str;
	}

	/**
	 * 把字节数组转换为年月日（字节数组包含20151230+周（从周一到周日七个字符0123456）+时间（12:36）） 格式：
	 * 20151123012345612:31
	 * 
	 * @param data
	 */
	public static String getDate(byte[] data) {
		String str = null;
		if (data != null && data.length >= 15) {
			byte[] year = new byte[15];
			System.arraycopy(data, 0, year, 0, 15);
			str = ByteUtilBigEndian.ByteToString(year);
		}
		return str;
	}

	/**
	 * 把字节数组转换为对应的时间（20151230+周（从周一到周日七个字符0123456）+时间（12:36）） 时间格式： 12:36
	 * 
	 * @param data
	 * @param context
	 */
	public static String getTime(byte[] data) {
		String str = null;
		if (data != null && data.length > 0) {
			if (data.length >= 20) {
				byte[] time = new byte[5];
				System.arraycopy(data, 15, time, 0, 5);
				str = ByteUtilBigEndian.ByteToString(time);
			}
		}
		return str;
	}

	/**
	 * 把时间字符串（12:30）转换为整形数组
	 * 
	 * @param str
	 */
	public static int[] getHourAndMinute(String str) {
		int[] data = new int[2];
		if (!TextUtils.isEmpty(str) && str.contains(":")) {
			str = str.trim();
			String[] array = str.split(":");
			try {
				data[0] = Integer.parseInt(array[0]);
				data[1] = Integer.parseInt(array[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 把值转换为字符串（小于10的值变成以0开头的字符串）
	 * 
	 * @param value
	 * @return
	 */
	public static String getString(int value) {
		String str = "" + value;
		if (value < 10) {
			str = "0" + value;
		}
		return str;
	}

	public static String getUTF8XMLString(String xml) {
		StringBuffer sb = new StringBuffer();
		sb.append(xml);
		String xmString = "";
		String xmlUTF8 = "";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return xmlUTF8;
	}

	/**
	 * 把时间转换为xx:xx格式的字符串
	 * 
	 * @param context
	 * @param time
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getTime(Context context, int time) {
		String str = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		str = sdf.format(time * 1000);
		return str;
	}

	/**
	 * 把mac地址列表转换为字符串
	 */
	public static String getMacsString(List<String> list) {
		String macs = "";
		if (list != null && list.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String mac : list) {
				sb.append(mac).append(":");
			}
			String str = sb.toString();
			macs = str.substring(0, str.length() - 1);
		}
		return macs;
	}

	/**
	 * 把macs地址字符串转换为字符串列表
	 */
	public static ArrayList<String> getMacs(String str) {
		LogUtils.i("StringUtils", "getMacs(): macs = " + str);
		ArrayList<String> macs = null;
		if (!TextUtils.isEmpty(str)) {
			macs = new ArrayList<String>();
			if (str.contains(":")) {
				String[] array = str.split(":");
				for (String mac : array) {
					if (!TextUtils.isEmpty(mac)) {
						macs.add(mac);
					}
				}
			} else {
			    macs.add(str);
			}
		}
		return macs;
	}

	/**
	 * 判断路径是图片的路径
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isPicture(String path) {
		boolean isRight = false;
		if (!TextUtils.isEmpty(path)
				&& (path.endsWith(".png") || path.endsWith(".PNG")
						|| path.endsWith(".jpg") || path.endsWith(".JPG"))) {
			isRight = true;
		}
		return isRight;
	}
}
