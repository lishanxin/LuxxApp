package net.senink.piservice.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class ByteUtilBigEndian {
	/**
	 * short类型转换成byte数组
	 * 
	 * @param param
	 *            待转的short
	 * @return
	 * 
	 * @author jindegege
	 */
	public static byte[] shortToByteArr(short param) {
		byte[] arr = new byte[2];
		arr[0] = (byte) ((param >> 8) & 0xff);
		arr[1] = (byte) (param & 0xff);
		return arr;
	}

	/**
	 * int类型转换成byte数组
	 * 
	 * @param param
	 *            待转的int
	 * @return
	 * 
	 * @author jindegege
	 */
	public static byte[] intToByteArr(int param) {
		byte[] arr = new byte[4];
		arr[0] = (byte) ((param >> 24) & 0xff);
		arr[1] = (byte) ((param >> 16) & 0xff);
		arr[2] = (byte) ((param >> 8) & 0xff);
		arr[3] = (byte) (param & 0xff);
		return arr;
	}

	/**
	 * long类型转换成byte数组
	 * 
	 * @param param
	 *            待转的long
	 * @return
	 * 
	 * @author jindegege
	 */
	public static byte[] longToByteArr(long param) {
		byte[] arr = new byte[8];
		arr[0] = (byte) ((param >> 56) & 0xff);
		arr[1] = (byte) ((param >> 48) & 0xff);
		arr[2] = (byte) ((param >> 40) & 0xff);
		arr[3] = (byte) ((param >> 32) & 0xff);
		arr[4] = (byte) ((param >> 24) & 0xff);
		arr[5] = (byte) ((param >> 16) & 0xff);
		arr[6] = (byte) ((param >> 8) & 0xff);
		arr[7] = (byte) (param & 0xff);
		return arr;
	}

	/**
	 * 字符到字节转�?
	 * 
	 * @param ch
	 *            字符
	 * @return
	 * 
	 * @author jindegege
	 */
	public static byte[] charToByteArr(char ch) {
		byte[] b = new byte[2];
		int temp = (int) ch;
		b[0] = (byte) (temp >> 8 & 0xff);
		b[1] = (byte) (temp & 0xff);
		return b;
	}

	/**
	 * double转换byte数组
	 * 
	 * @param param
	 *            double
	 * @return byte数组
	 * 
	 * @author jindegege
	 */
	@SuppressLint("UseValueOf")
	public static byte[] doubleToByteArr(double param) {
		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(param);
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return b;
	}

	/**
	 * float转换byte数组
	 * 
	 * @param param
	 *            float
	 * @return byte数组
	 * 
	 * @author jindegege
	 */
	@SuppressLint("UseValueOf")
	public static byte[] floatToByteArr(float param) {
		byte[] b = new byte[4];
		int l = Float.floatToIntBits(param);
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(l).byteValue();
			l = l >> 8;
		}
		return b;
	}

	/**
	 * �?2字节的byte数组转成short�?
	 * 
	 * @param b
	 *            byte数组
	 * @return
	 * 
	 * @author jindegege
	 */
	public static short byteArrToShort(byte[] b) {
		return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
	}

	/**
	 * 把字节数组转换为短整形数组
	 * 
	 * @param b
	 * @param count
	 * @return
	 */
	public static short[] byteArrToShortArray(byte[] b, int count) {
		short[] array = new short[count];
		int len = b.length;
		for (int i = 0; i < count; i++) {
			if (len > 2 * i + 1) {
				array[i] = (short) (((b[2 * i] & 0xff) << 8) | (b[2 * i + 1] & 0xff));
			} else {
				break;
			}
		}
		return array;
	}

	/**
	 * �?4字节的byte数组转成int�?
	 * 
	 * @param b
	 *            byte数组
	 * @return
	 * 
	 * @author jindegege
	 */
	public static int byteArrToInt(byte[] b) {
		int addr = b[0] & 0xFF;

		addr |= ((b[1] << 8) & 0xFF00);

		addr |= ((b[2] << 16) & 0xFF0000);

		addr |= ((b[3] << 24) & 0xFF000000);

		return addr;
	}

	/**
	 * �?8字节的byte数组转成long�?
	 * 
	 * @param b
	 *            byte数组
	 * @return
	 * 
	 * @author jindegege
	 */
	public static long byteArrToLong(byte[] b) {
		byte[] a = new byte[8];
		int i = a.length - 1, j = b.length - 1;
		for (; i >= 0; i--, j--) {
			// 从b的尾�?(即int值的低位)�?始copy数据
			if (j >= 0)
				a[i] = b[j];
			else
				// 如果b.length不足4,则将高位�?0
				a[i] = 0;
		}
		// &0xff将byte值无差异转成int,避免Java自动类型提升�?,会保留高位的符号�?
		int v0 = (a[7] & 0xff) << 56;
		int v1 = (a[6] & 0xff) << 48;
		int v2 = (a[5] & 0xff) << 40;
		int v3 = (a[4] & 0xff) << 32;
		int v4 = (a[3] & 0xff) << 24;
		int v5 = (a[2] & 0xff) << 16;
		int v6 = (a[1] & 0xff) << 8;
		int v7 = (a[0] & 0xff);

		return v0 + v1 + v2 + v3 + v4 + v5 + v6 + v7;

	}

	/**
	 * �?2字节的byte数组转成字符�?
	 * 
	 * @param b
	 *            byte数组
	 * @return
	 * 
	 * @author jindegege
	 */
	public static char byteArrToChar(byte[] b) {
		byte[] a = new byte[2];
		int i = a.length - 1, j = b.length - 1;
		for (; i >= 0; i--, j--) {
			// 从b的尾�?(即int值的低位)�?始copy数据
			if (j >= 0)
				a[i] = b[j];
			else
				// 如果b.length不足2,则将高位�?0
				a[i] = 0;
		}
		// &0xff将byte值无差异转成int,避免Java自动类型提升�?,会保留高位的符号�?
		int v0 = (a[0] & 0xff) << 8;
		int v1 = (a[1] & 0xff);
		return (char) (v0 + v1);
	}

	/**
	 * byte数组到double转换
	 * 
	 * @param byte数组
	 * @return double
	 * 
	 * @author jindegege
	 */
	public static double byteArrToDouble(byte[] b) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;
		l |= ((long) b[7] << 56);
		return Double.longBitsToDouble(l);
	}

	/**
	 * byte数组到float转换
	 * 
	 * @param byte数组
	 * @return float
	 * 
	 */
	public static float byteArrToFloat(byte[] b) {
		int l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		return Float.intBitsToFloat(l);
		// DataInputStream dis = new DataInputStream(new
		// ByteArrayInputStream(b));
		// float f = 0;
		// try {
		// f = dis.readFloat();
		// dis.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return f;
	}

	/**
	 * 默认的字符集编码 UTF-8 �?个汉字占三个字节
	 */
	private static String CHAR_ENCODE = "UTF-8";

	/**
	 * 设置全局的字符编�?
	 * 
	 * @param charEncode
	 */
	public static void configCharEncode(String charEncode) {
		CHAR_ENCODE = charEncode;
	}

	/**
	 * @param str
	 *            源字符串转换成字节数组的字符�?
	 * @return
	 */
	public static byte[] StringToByte(String str) {
		return StringToByte(str, CHAR_ENCODE);
	}

	/**
	 * 
	 * @param srcObj
	 *            源字节数组转换成String的字节数�?
	 * @return
	 */
	public static String ByteToString(byte[] srcObj) {
		String str = ByteToString(srcObj, CHAR_ENCODE);
		if (str != null) {
			str = str.trim();
		}
		return str;
	}

	/**
	 * 
	 * @param srcObj
	 *            把字节数组转换为空字符串（用-分割的字符串）
	 * @return
	 */
	public static String ByteToMacString(byte[] srcObj) {
		StringBuilder sb = new StringBuilder();
		if (srcObj != null && srcObj.length > 0) {
			int len = srcObj.length;
			for (int i = 0; i < len; i++) {
				String str = Integer.toHexString((srcObj[i] & 0xFF));
				sb.append(str);
				if (i < len - 1) {
					sb.append("-");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * UTF-8 �?个汉字占三个字节
	 * 
	 * @param str
	 *            源字符串 转换成字节数组的字符�?
	 * @return
	 */
	public static byte[] StringToByte(String str, String charEncode) {
		byte[] destObj = null;
		try {
			if (null == str || str.trim().equals("")) {
				destObj = new byte[0];
				return destObj;
			} else {
				destObj = str.getBytes(charEncode);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return destObj;
	}

	@SuppressWarnings("deprecation")
	public static byte[] StringToByte2(String a, int length) {
		byte[] b = new byte[length];
		if (a != null) {
			for (int i = 0; i < a.length() && i < length; i++) {
				a.getBytes(i, i + 1, b, i);
			}
		}
		return b;
	}

	/**
	 * @param srcObj
	 *            源字节数组转换成String的字节数�?
	 * @return
	 */
	public static String ByteToString(byte[] srcObj, String charEncode) {
		String destObj = null;
		try {
			destObj = new String(srcObj, charEncode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return destObj.replaceAll("\0", " ");
	}

	public static String macAddrToStr(byte[] mac) {
		String tmpValue = "";
		int len = mac.length>8?8:mac.length;
		for (int i = 0; i < len; i++) {
			if (tmpValue == "") {
				tmpValue = Integer.toHexString(mac[i] & 0x0FF);
				if (tmpValue.length() <= 1) {
					tmpValue = "0" + tmpValue;
				}
			} else {
				String str = Integer.toHexString(mac[i] & 0x0FF);
				if (str.length() <= 1) {
					str = "0" + Integer.toHexString(mac[i] & 0x0FF);
				}
				tmpValue = tmpValue + "-" + str;
			}
		}
		return tmpValue;

	}

	public static String macAddrToStrWithoutSeparator(byte[] mac) {
		String tmpValue = "";
		try {
			for (byte b : mac) {
				tmpValue += String.format("%02X", b);
			}
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
		return tmpValue;

	}

	public static byte[] macStrToByte(String mac, int len) {
		byte[] byteMac;
		String[] tmpStr;
		byteMac = new byte[len];
		if (mac != null && !TextUtils.isEmpty(mac)) {
			if (mac.contains("-") || mac.contains(":")) {
				tmpStr = mac.split("-");
				if (tmpStr == null || mac.contains(":")) {
					tmpStr = mac.split(":");
				}
			} else {
				int size = mac.length() / 2;
				tmpStr = new String[size];
				for (int i = 0; i < size; i++) {
					tmpStr[i] = mac.substring(i * 2, i * 2 + 2);
				}
			}
			for (int i = 0; i < tmpStr.length; i++) {
				int intMac = Integer.parseInt(tmpStr[i], 16);
				byte[] bArr = intToByteArr(intMac);
				byteMac[i] = bArr[3];
			}
		}
		return byteMac;
	}

	public static String classIdToStr(byte[] id) {
		String tmpValue = "";
		for (int i = 0; i < id.length; i++) {
			if (tmpValue == "") {
				tmpValue = Integer.toHexString(id[i] & 0x0FF);
				if (tmpValue.length() <= 1) {
					tmpValue = "0" + tmpValue;
				}
			} else {
				String str = Integer.toHexString(id[i] & 0x0FF);
				if (str.length() <= 1) {
					str = "0" + Integer.toHexString(id[i] & 0x0FF);
				}
				tmpValue = tmpValue/* +"-" */+ str;
			}
		}
		return tmpValue;

	}

	public static byte[] classIdStrToByte(String id, int len) {
		String[] tmpStr;
		byte[] byteId = new byte[len];
		if (id != null && !TextUtils.isEmpty(id)) {
			if (id.contains("-") || id.contains(":")) {
				tmpStr = id.split("-");
				if (tmpStr == null || tmpStr.length > 0) {
					tmpStr = id.split(":");
				}
			} else {
				int size = id.length() / 2;
				tmpStr = new String[size];
				for (int i = 0; i < size; i++) {
					tmpStr[i] = id.substring(i * 2, i * 2 + 2);
				}
			}
			for (int i = 0; i < tmpStr.length; i++) {
				int intId = Integer.parseInt(tmpStr[i], 16);
				byte[] bArr = intToByteArr(intId);
				byteId[i] = bArr[3];
			}
		}
		return byteId;
	}

	public static byte[] macStrToStringByte(String mac, int len) {
		mac.replace('-', ':');
		byte[] byteMac = StringToByte2(mac, len);
		return byteMac;
	}

	public static String bytesToIPStr(byte[] IP) {
		String tmpValue = "";
		for (int i = 0; i < IP.length; i++) {
			if (tmpValue == "") {
				tmpValue = String.valueOf(IP[i] & 0x0FF);
			} else {
				tmpValue = tmpValue + "." + String.valueOf(IP[i] & 0x0FF);
			}
		}
		return tmpValue;

	}

	public static int unsignedByteToInt(byte b) {
		return b & 0xFF;
	}

	@SuppressLint("DefaultLocale")
	public static String getHexString(byte[] b) throws Exception {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		if (result != null) {
			result = result.toUpperCase();
		}
		return result;
	}

	@SuppressLint("DefaultLocale")
	public static String getHexString(byte b) throws Exception {
		String result = Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		if (result != null) {
			return result.toUpperCase();
		}
		return null;
	}

	@SuppressLint("DefaultLocale")
	public static String getHexString(int i) throws Exception {
		String result = Integer.toString(i + 0x100, 16).substring(1);
		if (result != null) {
			return result.toUpperCase();
		}
		return null;
	}

	public static byte[] getByteArray(String hexString) {
		return new BigInteger(hexString, 16).toByteArray();
	}

	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 把两个整数转换为字节
	 * 
	 * @param hight
	 *            高四位的值
	 * @param low
	 *            低四位的值 高地位的值均在0-15直接
	 * @return
	 */
	public static byte integerToByte(int hight, int low) {
		byte result = 0;
		int value1 = (int) ((hight << 4) & 0xFF);
		int value2 = (int) (low & 0xFF);
		result = (byte) ((value1 + value2) & 0xFF);
		return result;
	}

	/**
	 * 把string类型的ASCII转换成字符串类型utf格式的byte数据
	 * 
	 * @param str
	 *            高四位的值
	 * @return
	 */
	public static byte[] stringToBytes(String str) {
		byte[] data = null;
		if (str != null && str.length() > 0) {
			byte[] temp = null;
			try {
				temp = str.getBytes("US-ASCII");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// LogUtils.i(MeshController.TAG, "bytes.length = "+temp.length);
			if (temp != null && temp.length > 7) {
				int len = temp.length;
				data = new byte[len * 2];
				for (int i = 0; i < 6; i++) {
					if ((temp[6] & (1 << i)) != 0) {
						temp[i] |= 0x80;
					}
				}
				for (int i = 0; i < len; i++) {
					temp[i] = (byte) (temp[i] & 0xFF);
					data[i * 2] = (byte) ((temp[i] >> 4) & 0x0F);
					data[i * 2 + 1] = (byte) (temp[i] & 0x0F);
					if (data[i * 2] <= 9) {
						data[i * 2] += 0x30;
					} else {
						data[i * 2] += 0x37;
					}
					if (data[i * 2 + 1] <= 9) {
						data[i * 2 + 1] += 0x30;
					} else {
						data[i * 2 + 1] += 0x37;
					}
					// LogUtils.i(MeshController.TAG,
					// "temp["+(i)+"] = "+temp[i]+",data["+(i * 2)+"] = "+data[i
					// * 2]+", data["+(i * 2+1)+"] = "+data[i * 2+1]);
				}
			}
		}
		return data;
	}

	/**
	 * 把ip地址字符串转换为字节数组
	 * 
	 * @param ip
	 * @return
	 */
	public static byte[] ipStringToBytes(String ip) {
		byte[] data = new byte[4];
		if (!ObjectUtil.isEmpty(ip)) {
			ip = ip.trim();
			String[] array = ip.split(".");
			if (!ObjectUtil.isEmpty(array)) {
				int len = 4 >= array.length ? array.length : 4;
				for (int i = 4 - len; i < len; i++) {
					data[i] = (byte) (array[i - (4 - len)].getBytes()[0]);
				}
			}
		}
		return data;
	}
}
