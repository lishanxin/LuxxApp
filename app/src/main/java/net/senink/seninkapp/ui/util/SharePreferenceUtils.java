package net.senink.seninkapp.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.fusion.JsonConstant;
import net.senink.seninkapp.ui.fusion.RequestConstant;

/**
 * 偏好设置的工具类
 * 
 * @author ryan
 * @date 2015-07-15
 */
public class SharePreferenceUtils {

	private Context context;
	private static SharePreferenceUtils instance;

	private SharePreferenceUtils(Context context) {
		this.context = context.getApplicationContext();
	}

	public static final String KEY_SHARE_UUID = "ACCOUNT_UUID";
	public static final String KEY_SHARE_MSISDN = "ACCOUNT_MSISDN";
	public static final String KEY_SHARE_PASSWORD = "ACCOUNT_PASSWORD";
	public static final String KEY_SHARE_SESSION = "ACCOUNT_SESSION";
	public static final String SHARE_ACCOUNT = "ACCOUNT";

	//public static final String ACCOUNT_USER = "user";
	public static final String ACCOUNT_PASSWORD = "pwd";
	public static final String ACCOUNT_CHECKED = "checked";


	public static final String ACCOUNT_CURRENT = "ACCOUNT_CURRENT";
	public static final String ACCOUNT_LIST = "ACCOUNT_LIST";
	public static final String ACCOUNT_COUNT = "ACCOUNT_COUNT";

	private static SharedPreferences settingPref = null;
	private static SharedPreferences accountPref = null;
	private static SharedPreferences locationPref = null;
	private static final String UPDATE_TIME = "update_time";
	private static final String LOCATION_FILE_NAME = "location";
	private static final String INTRO_FILE_NAME = "INTRO";

	public static final String FILENAME_PISMANAGER = "pismgr.dat";
	public static final String FILENAME_DEVICEINFO = "devinfo.dat";
	public static final String FILENAME_USERINFO   = "userinfo.dat";




	public static SharePreferenceUtils getInstance(Context context) {
		if (instance == null) {
			instance = new SharePreferenceUtils(context);
		}
		return instance;
	}

	/**
	 * 获取当前用户名
	 * @return 当前用户名
     */
	public String getCurrentUser() {
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		return accountPref.getString(ACCOUNT_CURRENT, null);
	}

	public void setCurrentUser(String user){
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		Editor editor = accountPref.edit();
		editor.putString(ACCOUNT_CURRENT, user);
		editor.apply();
	}
	public void unsetCurrentUser(){
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		Editor editor = accountPref.edit();
		editor.remove(ACCOUNT_CURRENT);
		editor.apply();
	}

	public List<UserInfo> loadUserList(){
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		ArrayList<UserInfo> userList = new ArrayList<>();
		HashSet<String> nameList = (HashSet<String>)accountPref.getStringSet(ACCOUNT_LIST, null);

		if (nameList == null)
			return userList;

		for (String username : nameList){
			UserInfo uInfo = loadUser(username);
			if (uInfo != null)
				userList.add(uInfo);
		}

		return userList;
	}

	public UserInfo loadUser(String username){
		UserInfo uInfo = null;

		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		if (username == null)
			return null;
		String storePath = accountPref.getString(username, null);
		if (storePath == null)
			return null;
		File userFile = new File(storePath);
		if(!userFile.exists())
			return null;

		ObjectInputStream ois = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(userFile);
			ois = new ObjectInputStream(fis);
			uInfo = (UserInfo) ois.readObject();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return uInfo;
	}

	public void saveUser(UserInfo uInfo){
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		if (uInfo == null || uInfo.loginUser == null)
			return;
		String storePath = getUserDataPath(context, FILENAME_USERINFO, uInfo.loginUser);
		HashSet<String> nameList = (HashSet<String>)accountPref.getStringSet(ACCOUNT_LIST, null);
		if (nameList == null){
			nameList = new HashSet<String>();
		}
		if (!nameList.contains(uInfo.loginUser)){
			nameList.add(uInfo.loginUser);
		}
		buildUserPath(context, uInfo.loginUser);
		File userFile = new File(storePath);
		if (!userFile.exists()){
			try{
				if (!userFile.createNewFile())
					return;
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(userFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(uInfo);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Editor editor = accountPref.edit();
		editor.putStringSet(ACCOUNT_LIST, nameList);
		editor.putString(uInfo.loginUser, storePath);
		editor.apply();
	}

	public void saveUserInfolist(List<UserInfo> userList){
		for (int i=0; i<userList.size() ;i++){
			UserInfo uInfo = userList.get(i);
			if (uInfo != null && uInfo.loginUser != null)
				saveUser(uInfo);
		}
	}

	/**
	 * 删除单个文件
	 * @param   filePath    被删除文件的文件名
	 * @return 文件删除成功返回true，否则返回false
	 */
	public boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 删除文件夹以及目录下的文件
	 * @param   filePath 被删除目录的文件路径
	 * @return  目录删除成功返回true，否则返回false
	 */
	public boolean deleteDirectory(String filePath) {
		boolean flag = false;
		//如果filePath不以文件分隔符结尾，自动添加文件分隔符
		if (!filePath.endsWith(File.separator)) {
			filePath = filePath + File.separator;
		}
		File dirFile = new File(filePath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		//遍历删除文件夹下的所有文件(包括子目录)
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				//删除子文件
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			} else {
				//删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
		//删除当前空目录
		return dirFile.delete();
	}

	/**
	 *  根据路径删除指定的目录或文件，无论存在与否
	 *@param filePath  要删除的目录或文件
	 *@return 删除成功返回 true，否则返回 false。
	 */
	public boolean DeleteFolder(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {
				// 为文件时调用删除文件方法
				return deleteFile(filePath);
			} else {
				// 为目录时调用删除目录方法
				return deleteDirectory(filePath);
			}
		}
	}
	public void removeUser(String username){
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		if (username == null)
			return;

		//删除整个用户目录
		String userPath = getUserDataPath(context, null, username);
		DeleteFolder(userPath);


		Editor editor = accountPref.edit();
		editor.remove(username);

		//如果是当前用户，则清除当前用户
		String currentUser = accountPref.getString(ACCOUNT_CURRENT, null);
		HashSet<String> nameList = (HashSet<String>)accountPref.getStringSet(ACCOUNT_LIST, null);
		if (nameList != null && nameList.contains(username)){
			nameList.remove(username);
			editor.putStringSet(ACCOUNT_LIST, nameList);
		}
		if ( currentUser != null && currentUser.compareTo(username) == 0){
			editor.remove(ACCOUNT_CURRENT);
		}
		editor.apply();
	}

	public String getPassword() {
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		return accountPref.getString(ACCOUNT_PASSWORD, null);
	}
//
//	/**
//	 * 保存当前用户密码是否保存的状态
//	 * @param isChecked
//     */
//	public void putChecked(boolean isChecked) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		Editor editor = accountPref.edit();
//		editor.putBoolean(ACCOUNT_CHECKED, isChecked);
//		editor.commit();
//	}
//
//	/**
//	 * 获取当前用户是否保存了密码
//	 * @return
//     */
//	public boolean getChecked() {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		return accountPref.getBoolean(ACCOUNT_CHECKED, false);
//	}
//
//	public void putUpdateTime(String time) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		Editor editor = accountPref.edit();
//		editor.putString(UPDATE_TIME, time);
//		editor.commit();
//	}
//
//	/**
//	 * 获取连接蓝牙设备的deviceId
//	 *
//	 * @return
//	 */
//	public int getDeviceIdOnBLE() {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		String key = getKeyOnBLE();
//		int deviceID = 0;
//		if (!TextUtils.isEmpty(key)) {
//			deviceID = accountPref.getInt(key, 0);
//		}
//		return deviceID;
//	}
//
//	/**
//	 * 保存连接蓝牙设备的deviceId
//	 */
//	public void putDeviceIdOnBLE(String bleKey, int deviceId) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		Editor editor = accountPref.edit();
//		editor.putInt(bleKey, deviceId);
//		editor.commit();
//	}
//
//	/**
//	 * 获取连接蓝牙设备的key
//	 *
//	 * @return
//	 */
//	public String getKeyOnBLE() {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		String user = getUsername();
//		return accountPref.getString(user, null);
//	}
//
//	/**
//	 * 保存连接蓝牙设备的key
//	 */
//	public void putKeyOnBLE(String key) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		String user = getUsername();
//		Editor editor = accountPref.edit();
//		editor.putString(user, key);
//		editor.commit();
//	}
//
//	public String getUpdateTime() {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		return accountPref.getString(UPDATE_TIME, null);
//	}

	/*
	 * public String getLauncherMode() { SharedPreferences setting
	 * =context.getSharedPreferences(RequestConstant.SETTING,
	 * Context.MODE_PRIVATE); String launcherMode =
	 * setting.getString(RequestConstant.LAUNCHER_MODE, ""); return
	 * launcherMode; }
	 * 
	 * public void putLauncherMode( String mode) { SharedPreferences setting
	 * =context.getSharedPreferences(RequestConstant.SETTING,
	 * Context.MODE_PRIVATE); Editor editor = setting.edit();
	 * editor.putString(RequestConstant.LAUNCHER_MODE, mode); editor.commit(); }
	 * 
	 * public void clearLoginInfo() {
	 * 
	 * }
	 */

//	public void cleanLoginAccount(SharedPreferences pref) {
//		Editor editor = pref.edit();
////		editor.remove(ACCOUNT_USER);
//		editor.remove(ACCOUNT_PASSWORD);
//		editor.remove(KEY_SHARE_UUID);
//		editor.remove(KEY_SHARE_MSISDN);
//		editor.remove(KEY_SHARE_PASSWORD);
//		editor.remove(KEY_SHARE_SESSION);
//		editor.commit();
//	}
//
//	/** set is first time enter */
//	public void setIsFirstTimeEnter(boolean isFirst) {
//		if (null == settingPref) {
//			settingPref = context.getSharedPreferences(RequestConstant.SETTING,
//					Context.MODE_PRIVATE);
//		}
//		Editor editor = settingPref.edit();
//		editor.putBoolean(JsonConstant.KEY_IS_FIRST_TIME, isFirst);
//		editor.commit();
//	}
//
//	/** get is first time enter */
//	public boolean getIsFirstTimeEnter() {
//		if (null == settingPref) {
//			settingPref = context.getSharedPreferences(RequestConstant.SETTING,
//					Context.MODE_PRIVATE);
//		}
//		return settingPref.getBoolean(JsonConstant.KEY_IS_FIRST_TIME, true);
//	}

//	public final static int SAVE_MAX_USER_NUM = 50;
//
//	public static List<UserInfo> getAllLoginUser(Context context) {
////		List<UserInfo> infos = new ArrayList<UserInfo>();
////		for (int i = 0; i < SAVE_MAX_USER_NUM; i++) {
////			UserInfo userInfo = new UserInfo();
////			String nameKey = ACCOUNT_USER;
////			String pwdKey = ACCOUNT_PASSWORD;
////			if (i > 0) {
////				nameKey = nameKey + i;
////				pwdKey = pwdKey + i;
////			}
////			if (getValue(context, nameKey) != null) {
////				infos.add(userInfo);
////				userInfo.loginUser = getValue(context, nameKey);
////				userInfo.pwd = getValue(context, pwdKey);
////				// userInfo.userIcon = POSTsubmitUtil.getImage(context,
////				// userInfo.loginUser, null);
////				userInfo.isAuto = isAutoLogin(context, userInfo.loginUser);
////			}
////		}
////		return infos;
//		return SharePreferenceUtils.getInstance(context).loadUserList();
//	}
//
//	public static String getValue(Context context, String key) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		return accountPref.getString(key, null);
//	}
//
//	public static Long getLongValue(Context context, String key) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		return accountPref.getLong(key, 0);
//	}

	public static void buildUserPath(Context context, String user){
		String userPath = context.getCacheDir().getPath() + "/" + user + "/";
		File userFile = new File(userPath);

		if (!userFile.exists()){
			userFile.mkdir();
		}
	}
	public static String getUserDataPath(@NonNull Context context, String type, String user){
		return context.getDir(user, Context.MODE_PRIVATE).getPath()  + "/" + (type == null?"":type);
	}
	
	public static void saveValue(Context context, String key, String value) {
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		Editor editor = accountPref.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void saveValue(Context context, String key, long value) {
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		Editor editor = accountPref.edit();
		editor.putLong(key, value);
		editor.apply();
	}
	
//	public static String getMacs(Context context) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		return accountPref.getString(getInstance(context).getUsername()
//				+ "macs", null);
//	}
//
//	public static void saveMacs(Context context, String value) {
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		Editor editor = accountPref.edit();
//		editor.putString(getInstance(context).getUsername() + "macs", value);
//		editor.commit();
//	}

//	public static void saveLoginUser(Context context, String userName,
//			String pwd, boolean isAutoLogin) {
//		List<UserInfo> userInfos = loadUserList(context);
//
//		if (userInfos.size() > SAVE_MAX_USER_NUM) {
//			return;
//		}
//		UserInfo info = null;
//		for (int i = 0; i < userInfos.size(); i++) {
//			if (userName.compareTo(userInfos.get(i).loginUser) == 0 ||
//					userName.compareTo(userInfos.get(i).email) == 0 ||
//					userName.compareTo(userInfos.get(i).tel) == 0){
////				userInfos.remove(i);
//				info = userInfos.get(i);
//				break;
//			}
//			info = null;
//		}
//		if (info == null) {
//			info = new UserInfo();
//			info.loginUser = userName;
//			userInfos.add(0, info);
//		}
//		info.isAuto = isAutoLogin;
//		if (info.isAuto)
//			info.pwd = pwd;
//
////		for (int i = 0; i < userInfos.size(); i++) {
////			String nameKey = ACCOUNT_USER;
////			String pwdKey = ACCOUNT_PASSWORD;
////			if (i > 0) {
////				nameKey = nameKey + i;
////				pwdKey = pwdKey + i;
////			}
////			saveValue(context, nameKey, userInfos.get(i).loginUser);
////			saveValue(context, pwdKey, userInfos.get(i).pwd);
////			saveIsAutoLogin(context, userInfos.get(i).loginUser,
////					userInfos.get(i).isAuto);
////		}
//
//	}
//
//	public static void saveIsAutoLogin(Context context, String userName,
//			boolean flag) {
//		String key = userName + "auto";
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		Editor editor = accountPref.edit();
//		editor.putBoolean(key, flag);
//		editor.commit();
//	}

//	public static boolean isAutoLogin(Context context, String userName) {
//		String key = userName + "auto";
//		if (null == accountPref) {
//			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
//					Context.MODE_PRIVATE);
//		}
//		return accountPref.getBoolean(key, false);
//
//		UserInfo uInfo = getInstance(context).getUserInfo(userName);
//
//		if (uInfo != null)
//			return uInfo.isAuto;
//		else
//			return false;
//	}

//	public static void clearCurLoginInfo(Context context) {
//		try {
//			List<UserInfo> userInfos = getAllLoginUser(context);
//			try {
//
//				userInfos.remove(0);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			boolean flag = SharePreferenceUtils.getInstance(context)
//					.getChecked();
//			if (null == accountPref) {
//				accountPref = context.getSharedPreferences(
//						RequestConstant.ACCOUNT, Context.MODE_PRIVATE);
//			}
//			Editor editor = accountPref.edit();
//			editor.clear();
//			editor.commit();
//			for (int i = 0; i < userInfos.size(); i++) {
//				saveLoginUser(context, userInfos.get(i).loginUser,
//						userInfos.get(i).pwd, userInfos.get(i).isAuto);
//			}
//			SharePreferenceUtils.getInstance(context).putChecked(flag);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void saveLocationValue(Context context, String key,
			String value) {
		if (null == locationPref) {
			locationPref = context.getSharedPreferences(LOCATION_FILE_NAME,
					Context.MODE_PRIVATE);
		}
		Editor editor = locationPref.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void saveLocations(Context context, String value) {
		if (null == locationPref) {
			locationPref = context.getSharedPreferences(LOCATION_FILE_NAME,
					Context.MODE_PRIVATE);
		}
		Editor editor = locationPref.edit();
		editor.putString("locations", value);
		editor.apply();
	}

	public static String getLocations(Context context) {
		if (null == locationPref) {
			locationPref = context.getSharedPreferences(LOCATION_FILE_NAME,
					Context.MODE_PRIVATE);
		}
		String locations = locationPref.getString("locations", null);
		return locations;
	}

	public static String getLocationValue(Context context, String key) {
		if (null == locationPref) {
			locationPref = context.getSharedPreferences(LOCATION_FILE_NAME,
					Context.MODE_PRIVATE);
		}
		return locationPref.getString(key, null);
	}

	public static boolean isFirst(Context context) {
		SharedPreferences intro = context.getSharedPreferences(INTRO_FILE_NAME,
				Context.MODE_PRIVATE);
		return intro.getBoolean("isFirst", false);
	}

	public static void setIsFirst(Context context) {
		SharedPreferences intro = context.getSharedPreferences(INTRO_FILE_NAME,
				Context.MODE_PRIVATE);
		Editor editor = intro.edit();
		editor.putBoolean("isFirst", true);
		editor.apply();
	}

	/**
	 * 是否是按home键让程序后台运行
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackGround(Context context) {
		SharedPreferences intro = context.getSharedPreferences(
				RequestConstant.ACCOUNT, Context.MODE_PRIVATE);
		return intro.getBoolean("background", false);
	}

	/**
	 * 是否是按home键让程序后台运行
	 * 
	 * @param context
	 * @return
	 */
	public static void setBackGroundRun(Context context, boolean isRun) {
		SharedPreferences intro = context.getSharedPreferences(
				RequestConstant.ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = intro.edit();
		editor.putBoolean("background", isRun);
		editor.apply();
	}

	/**
	 * 是否是第一登陆该应用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFirstOnLogin(Context context) {
		SharedPreferences intro = context.getSharedPreferences(
				RequestConstant.ACCOUNT, Context.MODE_PRIVATE);
		return intro.getBoolean("isFirstOnLogin", true);
	}

	/**
	 * 保存是否是第一登陆该应用
	 * 
	 * @param context
	 * @return
	 */
	public static void setFirstOnLogin(Context context, boolean isFirst) {
		SharedPreferences intro = context.getSharedPreferences(
				RequestConstant.ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = intro.edit();
		editor.putBoolean("isFirstOnLogin", isFirst);
		editor.apply();
	}

	/**
	 * 获取保存在本地的统计数据
	 * @param context
	 * @return
	 */
	public static Map<String, String> getInforOnData(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				RequestConstant.ACCOUNT, Context.MODE_PRIVATE);
		Map<String, String> infors = new HashMap<String, String>();
		long ble_Index = pref.getLong(Constant.BLE_COM_ID, 1);
		String phoneId = pref.getString(Constant.PHONE_ID, null);
		String startTime = pref.getString(Constant.CONNECT_TIME_START, null);
		String endTime = pref.getString(Constant.CONNECT_TIME_END, null);
		String connectResult = pref.getString(Constant.CONNECT_RESULT, null);
		String failedReason = pref.getString(Constant.CONNECT_FAILED_REASON, null);
		String costTime = pref.getString(Constant.CONNECT_COST_TIME, null);
		String disconnectTime = pref.getString(Constant.DISCONNECT_TIME, null);
		String disconnectReason = pref.getString(Constant.DISCONNECT_REASON, null);
		String connectTime = pref.getString(Constant.CONNECT_TIME, null);
		String countOnSend = pref.getString(Constant.COUNT_ON_SEND_DATA, null);
		String countOnRecieve = pref.getString(Constant.COUNT_ON_RECIEVER_DATA, null);
		String sumOnSend = pref.getString(Constant.SUM_ON_SEND_DATA, null);
		String sumOnRecieve = pref.getString(Constant.SUM_ON_RECIEVE_DATA, null);
		infors.put(Constant.BLE_COM_ID, "" + ble_Index);
		if (TextUtils.isEmpty(phoneId)) {
			phoneId = Utils.getMIEIOnTelephone(context);
		}
		infors.put(Constant.PHONE_ID, phoneId);
		if (!TextUtils.isEmpty(startTime)) {
			infors.put(Constant.CONNECT_TIME_START, startTime);
		}
		if (!TextUtils.isEmpty(endTime)) {
			infors.put(Constant.CONNECT_TIME_END, endTime);
		}
		if (!TextUtils.isEmpty(connectResult)) {
			infors.put(Constant.CONNECT_RESULT, connectResult);
		}
		if (!TextUtils.isEmpty(failedReason)) {
			infors.put(Constant.CONNECT_FAILED_REASON, failedReason);
		}
		if (!TextUtils.isEmpty(costTime)) {
			infors.put(Constant.CONNECT_COST_TIME, costTime);
		}
		if (!TextUtils.isEmpty(disconnectTime)) {
			infors.put(Constant.DISCONNECT_TIME, disconnectTime);
		}
		if (!TextUtils.isEmpty(disconnectReason)) {
			infors.put(Constant.DISCONNECT_REASON, disconnectReason);
		}
		if (!TextUtils.isEmpty(connectTime)) {
			infors.put(Constant.CONNECT_TIME, connectTime);
		}
		if (!TextUtils.isEmpty(countOnSend)) {
			infors.put(Constant.COUNT_ON_SEND_DATA, countOnSend);
		}
		if (!TextUtils.isEmpty(countOnRecieve)) {
			infors.put(Constant.COUNT_ON_RECIEVER_DATA, countOnRecieve);
		}
		if (!TextUtils.isEmpty(sumOnSend)) {
			infors.put(Constant.SUM_ON_SEND_DATA, sumOnSend);
		}
		if (!TextUtils.isEmpty(sumOnRecieve)) {
			infors.put(Constant.SUM_ON_RECIEVE_DATA, sumOnRecieve);
		} else {
			infors = null;
		}
		clearData(context);
		return infors;
	}

	/**
	 * 清理统计数据的本地保存数据
	 * @param context
	 */
	private static void clearData(Context context) {
		if (null == accountPref) {
			accountPref = context.getSharedPreferences(RequestConstant.ACCOUNT,
					Context.MODE_PRIVATE);
		}
		Editor editor = accountPref.edit();
		editor.remove(Constant.CONNECT_TIME_START);
		editor.remove(Constant.CONNECT_TIME_END);
		editor.remove(Constant.CONNECT_RESULT);
		editor.remove(Constant.CONNECT_FAILED_REASON);
		editor.remove(Constant.CONNECT_COST_TIME);
		editor.remove(Constant.DISCONNECT_TIME);
		editor.remove(Constant.DISCONNECT_REASON);
		editor.remove(Constant.CONNECT_TIME);
		editor.remove(Constant.COUNT_ON_SEND_DATA);
		editor.remove(Constant.COUNT_ON_RECIEVER_DATA);
		editor.remove(Constant.SUM_ON_SEND_DATA);
		editor.remove(Constant.SUM_ON_RECIEVE_DATA);
		editor.apply();
	}
    
	/**
	 * 保存统计数据
	 * @param context
	 * @param infors
	 */
	public static void saveInfors(Context context,Map<String,String> infors){
		if (infors != null && infors.size() > 0) {
			Iterator<Entry<String,String>> iter = infors.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String,String> entry = iter.next();
				if (entry.getKey().equals(Constant.BLE_COM_ID)) {
					saveValue(context, Constant.BLE_COM_ID, Long.parseLong(entry.getValue()));
				} else {
                    saveValue(context, entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	public static void clear(){
		settingPref = null;
		accountPref = null;
		locationPref = null;
		instance = null;
	}
}
