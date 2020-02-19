package net.senink.piservice.struct;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.security.MessageDigest;


public class UserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7825220614043728879L;
	public String msn;
	public int visible;
	public String nickSrcMid;
	public String chineseName;
	public String tel;
	public String qq;
	public String companyName;
	public String message;
	public String openId;
	public String mobile;
	public float prices;
	public int id;
	public int groupId;
	public int parentId;
	public String address;
	public String email;
	public String nickName;
	public String nickSrc;
	public Bitmap userIcon;

	public String content;
	public boolean isShowBottom = false;
	public boolean isAuto = false;
	public boolean hasCloud = true;

	/**
	 * 用户本地缓存信息包括
	 * 1. 该用户所对应的PISManager实例对象
	 * 2. 头像图片
	 * 3. 位置列表
	 * 4. 
	 */

	/**MCS相关*/
	public String loginUser;
	public String pwd;

	/**蓝牙相关*/
	public String bleKey;
	public int bleId;

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

	public String getMD5Passwd(){
		if (pwd != null)
			return MD5(MD5(pwd));
		else
			return null;
	}
}
