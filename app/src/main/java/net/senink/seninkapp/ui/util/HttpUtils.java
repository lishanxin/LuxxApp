package net.senink.seninkapp.ui.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity;
import net.senink.seninkapp.ui.activity.SwitchDetailActivity;
import net.senink.seninkapp.ui.entity.BLEInfor;
import net.senink.seninkapp.ui.entity.BlueToothBubble;
import net.senink.seninkapp.ui.entity.DeviceInfo;
import net.senink.seninkapp.ui.entity.DeviceTypeName;
import net.senink.seninkapp.ui.entity.LocationName;
import net.senink.seninkapp.ui.entity.MobCodeInfor;
import net.senink.seninkapp.ui.smart.SmartCellItem;

/**
 * http请求
 * 
 * @author zhaojunfeng
 * @date 2015-12-16
 * 
 */
public class HttpUtils {
	
	private static final String TAG = "HttpUtils";
	protected static final int BUFFER_SIZE = 8 * 1024; // 8 Kb

	public static String submitPostData(Map<String, String> params,
			String encode) {
		byte[] data = getRequestData(params, encode).toString().getBytes();
		try {
			String pkey = "pkey=" + StringUtils.getPKey("000000");
			String addAccuUrl = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.REGISTER_USER_URL + pkey;
			LogUtils.i(TAG, "addAccuUrl==>" + addAccuUrl);
			URL url = new URL(addAccuUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();
			int response = httpURLConnection.getResponseCode();
			if (response == 200) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String submitPostData(String path,
			Map<String, String> params, String encode) {
		byte[] data = getRequestData(params, encode).toString().getBytes();
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoInput(true); 
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST"); 
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();
			int response = httpURLConnection.getResponseCode();
			if (response == 200) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static StringBuffer getRequestData(Map<String, String> params,
			String encode) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray());
		return resultData;
	}

	public static boolean isRegistered(String account) {
		String pkey = "&pkey=" + StringUtils.getPKey("000000");
		String url = Config.HTTP_HEADER + Config.SERVER_NAME
				+ Config.REGISTER_URL + "op=list&user=" + account + pkey;
		JSONObject obj = JSONUtil.getJson(url);
		LogUtils.i(TAG, "obj==>" + obj);
		if (obj != null) {
			try {
				return obj.getBoolean(JSONUtil.STATE);
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 邮箱是否已经注册
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String pkey = "&pkey=" + StringUtils.getPKey("000000");
		String url = Config.HTTP_HEADER + Config.SERVER_NAME
				+ Config.FORGET_PASSWORD_URL + email + pkey;
		JSONObject obj = JSONUtil.getJson(url);
		if (obj != null) {
			try {
				return obj.getBoolean(JSONUtil.STATE);
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 获取电量信息
	 * 
	 * @param index
	 * @param user
	 * @param t1
	 * @param t2
	 * @param unit
	 * @return
	 */
	public static JSONObject getPowersData(int index, String user, String t1,
			String t2, String unit) {
		String url = null;
		if (null == user) {
			return null;
		} else {
			url = user;
		}
		if (null != t1) {
			url = url + "&t1=" + t1;
		}
		if (null != t2) {
			url = url + "&t2=" + t2;
		}
		if (unit != null
				&& (unit.equals("t") || unit.equals("h") || unit.equals("d")
						|| unit.equals("m") || unit.equals("y"))) {
			url = url + "&t3=" + unit;
		} else {
			url = url + "&t3=t";
		}
		if (index > 0) {
			url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.POWER_DATA2_URL + url;
		} else {
			url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.POWER_DATA1_URL + url;
		}
		return JSONUtil.getJson(url);

	}

	/**
	 * cy修改通过json得到智能元的消息
	 * 
	 * @param context
	 * @param account
	 * @return
	 */
	public static List<SmartCellItem> getSmartCellItem(Context context,
													   String account) {
		ArrayList<SmartCellItem> list = new ArrayList<SmartCellItem>();
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.SMART_CELL + account + pkey;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.w(TAG, "obj==>" + obj);
			if (!obj.isNull("data")) {
				list = new ArrayList<SmartCellItem>();
				JSONArray rows = obj.getJSONArray("data");
				for (int i = 0; i < rows.length(); i++) {
					SmartCellItem cellItem = new SmartCellItem();
					JSONObject jsObject = (JSONObject) rows.get(i);

					if (!jsObject.isNull("title")) {
						cellItem.title = jsObject.getString("title");
					}
					if (!jsObject.isNull("platformid")) {
						cellItem.platformid = new String[1];
						cellItem.platformid[0] = jsObject
								.getString("platformid");
					}
					if (!jsObject.isNull("guid")) {
						cellItem.guid = jsObject.getString("guid");
					}
					if (!jsObject.isNull("content")) {
						cellItem.content = jsObject.getString("content");
					}
					if (!jsObject.isNull("icon")) {
						cellItem.icon = jsObject.getString("icon");
					}
					if (!jsObject.isNull("crc")) {
						cellItem.crc = jsObject.getString("crc");
					}
					if (!jsObject.isNull("visible")) {
						cellItem.visible = Integer.parseInt(jsObject
								.getString("visible"));
					}
					if (!jsObject.isNull("keyword")) {
						cellItem.keyword = jsObject.getString("keyword");
					}
					if (!jsObject.isNull("menuId")) {
						cellItem.menuId = Integer.parseInt(jsObject
								.getString("menuId"));
					}
					if (!jsObject.isNull("user")) {
						cellItem.user = jsObject.getString("user");
					}
					if (!jsObject.isNull("intro")) {
						cellItem.intro = jsObject.getString("intro");
					}
					if (!jsObject.isNull("version")) {
						cellItem.version = jsObject.getString("version");
					}
					if (!jsObject.isNull("menuName")) {
						cellItem.menuName = jsObject.getString("menuName");
					}
					try {
//						ArrayList<PISBase> pisBases = PISManager.getInstance()
//								.getPisSmartCellList();
//						for (int j = 0; j < pisBases.size(); j++) {
//							PISSmartCell pc = (PISSmartCell) pisBases.get(j);
//							if (pc.getGUID().equals(cellItem.guid)) {
//								cellItem.isInstall = true;
//								cellItem.pc = pc;
//								cellItem.instanceID = pc.getInstanceID();
//								cellItem.pisSmartCell = pc;
//								break;
//							}
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					list.add(cellItem);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取头像图片
	 * 
	 * @param context
	 * @param loginName
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImage(Context context, String loginName,
			String imagePath) {
		File file = new File(context.getFilesDir() + "/" + loginName);
		Bitmap bitmap = null;
		if (!file.exists()) {
			try {
				LogUtils.w(TAG, "imagePath==>" + imagePath);
				if (imagePath == null || imagePath.equals(""))
					return null;
				LogUtils.i(TAG, "imagePath==>" + imagePath);
				HttpGet httpRequest = new HttpGet(imagePath);
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				InputStream is = null;
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();
				}
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					copyStream(is, fos);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					fos.close();
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				file.deleteOnExit();
			}
		}
		LogUtils.i(TAG, "file.size==>" + file.length() / 1024f);
		try {
			if (new File(context.getFilesDir() + "/" + loginName).exists()) {
				Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				opt.inSampleSize = getInSampleSize(file.length());
				LogUtils.i(TAG, "opt.inSampleSize==>" + opt.inSampleSize
						+ " fsize==>" + file.length());
				InputStream imageStream = getStreamFromFile(new FileInputStream(
						file.getPath()));
				try {
					bitmap = BitmapFactory.decodeStream(imageStream, null, opt);
				} finally {
					imageStream.close();
				}
			}
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.ic_launcher);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.gc();
			try {
				Thread.sleep(2000);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return bitmap;
	}

	protected static InputStream getStreamFromFile(FileInputStream io)
			throws IOException {
		return new BufferedInputStream(io, BUFFER_SIZE);
	}

	public static int getInSampleSize(long fileSize) {
		int size = 0;
		if (fileSize > 4 * 1024 * 1024) {
			size = 10;
		} else if (fileSize > 3 * 1024 * 1024) {
			size = 4;
		} else if (fileSize > 2 * 1024 * 1024) {
			size = 3;
		} else if (fileSize > 500 * 1024) {
			size = 2;
		}
		return size;
	}

	public static void copyStream(InputStream is, OutputStream os)
			throws IOException {
		byte[] bytes = new byte[1024];
		while (true) {
			int count = is.read(bytes, 0, 1024);
			if (count == -1) {
				break;
			}
			os.write(bytes, 0, count);
		}
	}

	public static UserInfo getUserInfoItem(Context context, String account) {
		UserInfo userInfo = null;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.GET_USER_INFO + account + pkey;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, "url==>" + url);

			if (obj != null && !obj.isNull("data")) {
				JSONObject jsonObject = obj.getJSONObject("data");
				userInfo = new UserInfo();
				if (!jsonObject.isNull("msn")) {
					userInfo.msn = jsonObject.getString("msn");
				}
				if (!jsonObject.isNull("visible")) {
					userInfo.visible = jsonObject.getInt("visible");
				}
				if (!jsonObject.isNull("nickSrcMid")) {
					userInfo.nickSrcMid = jsonObject.getString("nickSrcMid");
				}
				if (!jsonObject.isNull("chineseName")) {
					userInfo.chineseName = jsonObject.getString("chineseName");
				}
				if (!jsonObject.isNull("tel")) {
					userInfo.tel = jsonObject.getString("tel");
				}
				if (!jsonObject.isNull("companyName")) {
					userInfo.companyName = jsonObject.getString("companyName");
				}
				if (!jsonObject.isNull("message")) {
					userInfo.message = jsonObject.getString("message");
				}
				if (!jsonObject.isNull("id")) {
					userInfo.id = jsonObject.getInt("id");
				}
				LogUtils.i(TAG, "userInfo.id==>" + userInfo.id);
				if (!jsonObject.isNull("groupId")) {
					userInfo.groupId = jsonObject.getInt("groupId");
				}
				if (!jsonObject.isNull("address")) {
					userInfo.address = jsonObject.getString("address");
				}
				if (!jsonObject.isNull("email")) {
					userInfo.email = jsonObject.getString("email");
				}
				if (!jsonObject.isNull("nickName")) {
					userInfo.nickName = jsonObject.getString("nickName");
				}
				if (!jsonObject.isNull("nickSrc")) {
					userInfo.nickSrc = jsonObject.getString("nickSrc");
				}
				if (!jsonObject.isNull("loginUser")) {
					userInfo.loginUser = jsonObject.getString("loginUser");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public static String submitModifyPasswordData(Context context,
			Map<String, String> params, String encode) {

		byte[] data = getRequestData(params, encode).toString().getBytes();
		try {
			String pkey = "pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String addAccuUrl = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.MODIFY_PASSWORD + pkey + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ "&password=" + params.get("password");
			LogUtils.i(TAG, "addAccuUrl==>" + addAccuUrl);
			URL url = new URL(addAccuUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();

			int response = httpURLConnection.getResponseCode();
			if (response == 200) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 在设置界面修改密码
	 * 
	 * @param context
	 * @param newPassword
	 * @return
	 */
	public static boolean submitModifyPasswordData(Context context,
			String newPassword) {
		boolean flag = false;
		try {
			String pkey = "pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER
					+ Config.SERVER_NAME
					+ Config.MODIFY_PASSWORD
					+ pkey
					+ "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ "&password="
					+ StringUtils.MD5(StringUtils.MD5(SharePreferenceUtils
							.getInstance(context).getPassword()))
					+ "&password2="
					+ StringUtils.MD5(StringUtils.MD5(newPassword));
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, "obj==>" + obj);
			if (!obj.isNull("state")) {
				flag = obj.getBoolean("state");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


	/**
	 * 通过找回密码方式修改密码
	 * @param context
	 * @param tel
	 * @param pwd
	 * @param code
     * @return
     */
	public static boolean modifyPassword(Context context, String tel,
			String pwd, String code) {
		boolean flag = false;
		try {
			String pkey = "pkey=" + StringUtils.getPKey(code);
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.MODIFY_PASSWORD_ONCODE_URL + pkey + "&user=" + tel
					+ "&password=" + StringUtils.MD5(StringUtils.MD5(pwd));
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, "modifyPassword():obj==>" + obj);
			if (!obj.isNull("state")) {
				flag = obj.getBoolean("state");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取所有绑定设备列表
	 * 
	 * @param context
	 * @param context
	 *
	 * @return
	 */
	public static List<DeviceInfo> getDevicesOnManager(Context context) {
		ArrayList<DeviceInfo> list = null;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.DEVICES_LIST_URL + pkey + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser();
			JSONObject obj = JSONUtil.getJson(url);
			// {"mac":"0000170000048135","classid":"000006000108","user":"seninktest","userId":"11761","location":null,"version":null,"name":"遥控器","passwd":"74be16979710d4c4e7c6647856088456","passwdDefault":null,"termID":"9284","title":null,"model":null,"distributor":null,"sourced":null,"content":null,"img1":"http:\/\/115.29.11.191\/uploads\/","icon":"http:\/\/115.29.11.191\/uploads\/","mader":null},
			LogUtils.i(TAG, "getDevicesOnManager(): obj==>" + obj);
			if (!obj.isNull("data")) {
				list = new ArrayList<DeviceInfo>();
				JSONArray rows = obj.getJSONArray("data");
				for (int i = 0; i < rows.length(); i++) {
					DeviceInfo cellItem = new DeviceInfo();
					JSONObject jsObject = (JSONObject) rows.get(i);

					if (!jsObject.isNull("title")) {
						cellItem.title = jsObject.getString("title");
					}
					if (!jsObject.isNull("name")) {
						cellItem.name = jsObject.getString("name");
					}
					if (!jsObject.isNull("user")) {
						cellItem.user = jsObject.getString("user");
					}
					if (!jsObject.isNull("classid")) {
						cellItem.classid = jsObject.getString("classid");
					}

					if (!jsObject.isNull("mac")) {
						cellItem.mac = jsObject.getString("mac");

					}
					if (!jsObject.isNull("location")) {
						cellItem.location = jsObject.getString("location");
					}
//					PISDevice pisDevice = PISManager.getInstance()
//							.getOnlineDeviceByMac(cellItem.mac);
//					String positionName = null;
//					if (pisDevice != null) {
//						positionName = SharePreferenceUtils.getLocationValue(
//								context, String.valueOf(pisDevice.mLocation));
//					}
//					if (null == positionName || "".equals(positionName)) {
//						positionName = context.getResources().getString(
//								R.string.no_know);
//						if (cellItem.location != null) {
//							positionName = cellItem.location;
//						}
//
//					}
//					cellItem.location = positionName;
					if (!jsObject.isNull("version")) {
						cellItem.version = jsObject.getString("version");
					}

					if (!jsObject.isNull("model")) {
						cellItem.model = jsObject.getString("model");

					}
					if (cellItem.model == null) {
						cellItem.model = "";
					}
					if (!jsObject.isNull("termID")) {
						cellItem.termID = jsObject.getInt("termID");
					}

					if (!jsObject.isNull("sourced")) {
						cellItem.sourced = jsObject.getString("sourced");
					}

					if (!jsObject.isNull("distributor")) {
						cellItem.distributor = jsObject
								.getString("distributor");
					}

					if (!jsObject.isNull("content")) {
						cellItem.content = jsObject.getString("content");
					}

					if (!jsObject.isNull("img1")) {
						cellItem.img1 = jsObject.getString("img1");
						LogUtils.i(TAG, "cellItem.img1 ==>" + cellItem.img1);

					}

					if (!jsObject.isNull("mader")) {
						cellItem.mader = jsObject.getString("mader");
					}
					if (cellItem.mader == null) {
						cellItem.mader = "";
					}
					if (!jsObject.isNull("icon")) {
						cellItem.icon = jsObject.getString("icon");
						LogUtils.i(TAG,
								"getDevicesOnManager():cellItem.icon==>"
										+ cellItem.icon);
					}
					list.add(cellItem);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取某个绑定设备信息（包含classid以及其他信息）
	 * 
	 * @param context
	 * @param mac
	 *            mac地址
	 * @return
	 */
	public static DeviceInfo getBindedDeviceInfor(Context context, String mac) {
		DeviceInfo cellItem = null;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.DEVICES_LIST_URL + pkey + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ "&mac=" + mac;
			JSONObject obj = JSONUtil.getJson(url);
			// {"mac":"0000170000048135","classid":"000006000108","user":"seninktest","userId":"11761","location":null,"version":null,"name":"遥控器","passwd":"74be16979710d4c4e7c6647856088456","passwdDefault":null,"termID":"9284","title":null,"model":null,"distributor":null,"sourced":null,"content":null,"img1":"http:\/\/115.29.11.191\/uploads\/","icon":"http:\/\/115.29.11.191\/uploads\/","mader":null},
			LogUtils.i(TAG, "getBindedDeviceInfor(): obj==>" + obj);
			if (!obj.isNull("data")) {
				JSONArray array = obj.getJSONArray("data");
				if (array.length() > 0) {
					JSONObject jsObject = array.getJSONObject(0);
					cellItem = new DeviceInfo();
					if (!jsObject.isNull("title")) {
						cellItem.title = jsObject.getString("title");
					}
					if (!jsObject.isNull("name")) {
						cellItem.name = jsObject.getString("name");
					}
					if (!jsObject.isNull("user")) {
						cellItem.user = jsObject.getString("user");
					}
					if (!jsObject.isNull("classid")) {
						cellItem.classid = jsObject.getString("classid");
					}

					if (!jsObject.isNull("mac")) {
						cellItem.mac = jsObject.getString("mac");

					}
					if (!jsObject.isNull("location")) {
						cellItem.location = jsObject.getString("location");
					}
//					PISDevice pisDevice = PISManager.getInstance()
//							.getOnlineDeviceByMac(cellItem.mac);
//					String positionName = null;
//					if (pisDevice != null) {
//						positionName = SharePreferenceUtils.getLocationValue(
//								context, String.valueOf(pisDevice.mLocation));
//					}
//					if (null == positionName || "".equals(positionName)) {
//						positionName = context.getResources().getString(
//								R.string.no_know);
//						if (cellItem.location != null) {
//							positionName = cellItem.location;
//						}
//
//					}
//					cellItem.location = positionName;
//					if (!jsObject.isNull("version")) {
//						cellItem.version = jsObject.getString("version");
//					}
//
//					if (!jsObject.isNull("model")) {
//						cellItem.model = jsObject.getString("model");
//
//					}
//					if (cellItem.model == null) {
//						cellItem.model = "";
//					}
//					if (!jsObject.isNull("termID")) {
//						cellItem.termID = jsObject.getInt("termID");
//					}
//
//					if (!jsObject.isNull("sourced")) {
//						cellItem.sourced = jsObject.getString("sourced");
//					}
//
//					if (!jsObject.isNull("distributor")) {
//						cellItem.distributor = jsObject
//								.getString("distributor");
//					}
//
//					if (!jsObject.isNull("content")) {
//						cellItem.content = jsObject.getString("content");
//					}
//
//					if (!jsObject.isNull("img1")) {
//						cellItem.img1 = jsObject.getString("img1");
//						LogUtils.i(TAG, "cellItem.img1 ==>" + cellItem.img1);
//
//					}
//
//					if (!jsObject.isNull("mader")) {
//						cellItem.mader = jsObject.getString("mader");
//					}
//					if (cellItem.mader == null) {
//						cellItem.mader = "";
//					}
//					if (!jsObject.isNull("icon")) {
//						cellItem.icon = jsObject.getString("icon");
//						LogUtils.i(TAG,
//								"getBindedDeviceInfor():cellItem.icon==>"
//										+ cellItem.icon);
//					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellItem;
	}

	/**
	 * 获取设备详情
	 * 
	 * @param context
	 * @param account
	 * @param classId
	 * @param mader
	 * @param model
	 * @return
	 */
	public static ArrayList<DeviceInfo> getDeviceInfoDetial(Context context,
			String account, String classId, String mader, String model,
			String mac) {

		ArrayList<DeviceInfo> list = null;
		LogUtils.i(TAG, "classId==>" + classId);
		try {
			String pkey = "pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.SHE_BEI_DETAIL + pkey + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ "&classid=" + classId + "&mader=" + mader + "&model="
					+ model + "&mac=" + mac;
			LogUtils.i(TAG, "getDeviceInfoDetial :url==>" + url);
			JSONObject obj = JSONUtil.getJson(url);
			// getDeviceInfoDetial
			// :obj==>{"data":[{"content":"<p>这里写你的详细内容<\/p>","icon":"http:\/\/115.29.11.191\/uploads\/","img1":"http:\/\/115.29.11.191\/uploads\/product\/20160219133012_7558.png","model":"SN7000108","title":"智能鞋垫","distributor":"立鑫智能","mader":null,"sourced":"中国深圳","classid":"000007000108"}],"state":true,"dataCount":1,"errorInfo":null,"error":0}
			LogUtils.i(TAG, "getDeviceInfoDetial :obj==>" + obj);
			if (!obj.isNull("data")) {
				list = new ArrayList<DeviceInfo>();
				JSONArray rows = obj.getJSONArray("data");
				for (int i = 0; i < rows.length(); i++) {
					DeviceInfo cellItem = new DeviceInfo();
					JSONObject jsObject = (JSONObject) rows.get(i);

					if (!jsObject.isNull("title")) {
						cellItem.title = jsObject.getString("title");
					}
					if (!jsObject.isNull("user")) {
						cellItem.user = jsObject.getString("user");
					}
					if (!jsObject.isNull("classid")) {
						cellItem.classid = jsObject.getString("classid");
					}

					if (!jsObject.isNull("mac")) {
						cellItem.user = jsObject.getString("mac");
					}

					if (!jsObject.isNull("location")) {
						cellItem.location = jsObject.getString("location");
					}

					if (!jsObject.isNull("version")) {
						cellItem.version = jsObject.getString("version");
					}

					if (!jsObject.isNull("model")) {
						cellItem.model = jsObject.getString("model");
					}

					if (!jsObject.isNull("termID")) {
						cellItem.termID = jsObject.getInt("termID");
					}

					if (!jsObject.isNull("sourced")) {
						cellItem.sourced = jsObject.getString("sourced");
					}

					if (!jsObject.isNull("distributor")) {
						cellItem.distributor = jsObject
								.getString("distributor");
					}

					if (!jsObject.isNull("content")) {
						cellItem.content = jsObject.getString("content");
					}

					if (!jsObject.isNull("img1")) {
						cellItem.img1 = jsObject.getString("img1");
					}

					if (!jsObject.isNull("mader")) {
						cellItem.mader = jsObject.getString("mader");
					}
					list.add(cellItem);
				}
			}

		} catch (Exception e) {
			LogUtils.i(TAG, "getDeviceInfoDetial :error==>" + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 授权邀请
	 * 
	 * @param context
	 * @param targetUser
	 * @return
	 */
	public static boolean isYaoQingSucced(Context context, String targetUser) {
		String pkey = "&pkey="
				+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
		String url = Config.HTTP_HEADER + Config.SERVER_NAME
				+ Config.YAO_QING_CHILD_ACCU + pkey + "&targetUser="
				+ targetUser + "&user="
				+ SharePreferenceUtils.getInstance(context).getCurrentUser();
		LogUtils.i(TAG, "url==>" + url);
		JSONObject obj = JSONUtil.getJson(url);
		if (obj != null) {
			try {
				return obj.getBoolean(JSONUtil.STATE);
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * 获取子账户列表
	 * 
	 * @param context
	 * @param parentid
	 * @return
	 */
	public static List<UserInfo> getChildList(Context context, int parentid) {
		List<UserInfo> infos = new ArrayList<UserInfo>();
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.GET_CHILD_LIST + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey + "&parentid=" + parentid;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.w(TAG, "obj==>" + obj);
			if (obj != null && !obj.isNull("data")) {
				JSONArray jsonArray = obj.getJSONArray("data");
				infos = new ArrayList<UserInfo>();
				for (int i = 0; i < jsonArray.length(); i++) {
					UserInfo userInfo = new UserInfo();
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					if (!jsonObject.isNull("msn")) {
						userInfo.msn = jsonObject.getString("msn");
					}
					if (!jsonObject.isNull("visible")) {
						userInfo.visible = jsonObject.getInt("visible");
					}
					if (!jsonObject.isNull("nickSrcMid")) {
						userInfo.nickSrcMid = jsonObject
								.getString("nickSrcMid");
					}
					if (!jsonObject.isNull("chineseName")) {
						userInfo.chineseName = jsonObject
								.getString("chineseName");
					}
					if (!jsonObject.isNull("tel")) {
						userInfo.tel = jsonObject.getString("tel");
					}
					if (!jsonObject.isNull("companyName")) {
						userInfo.companyName = jsonObject
								.getString("companyName");
					}
					if (!jsonObject.isNull("message")) {
						userInfo.message = jsonObject.getString("message");
					}
					if (!jsonObject.isNull("id")) {
						userInfo.id = jsonObject.getInt("id");
					}
					LogUtils.i(TAG, "userInfo.id==>" + userInfo.id);
					if (!jsonObject.isNull("groupId")) {
						userInfo.groupId = jsonObject.getInt("groupId");
					}
					if (!jsonObject.isNull("address")) {
						userInfo.address = jsonObject.getString("address");
					}
					if (!jsonObject.isNull("email")) {
						userInfo.email = jsonObject.getString("email");
					}
					if (!jsonObject.isNull("nickName")) {
						userInfo.nickName = jsonObject.getString("nickName");
					}
					if (!jsonObject.isNull("nickSrc")) {
						userInfo.nickSrc = jsonObject.getString("nickSrc");
					}
					if (!jsonObject.isNull("loginUser")) {
						userInfo.loginUser = jsonObject.getString("loginUser");
					}
					if (!jsonObject.isNull("nickSrc")) {
						userInfo.nickSrc = jsonObject.getString("nickSrc");
					}
					userInfo.content = context.getResources().getString(
							R.string.alert_you_can_fang_wen);
					infos.add(userInfo);
				}
				LogUtils.i(TAG, "size==>" + infos.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 获取父账号列表
	 * 
	 * @param context
	 * @param childId
	 * @return
	 */
	public static List<UserInfo> getParentList(Context context, int childId) {
		List<UserInfo> infos = new ArrayList<UserInfo>();
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.GET_PARENT_LIST + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey + "&sonid=" + childId;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.w(TAG, "ppp obj==>" + obj);
			if (obj != null && !obj.isNull("data")) {
				JSONArray jsonArray = obj.getJSONArray("data");
				infos = new ArrayList<UserInfo>();
				for (int i = 0; i < jsonArray.length(); i++) {
					UserInfo userInfo = new UserInfo();
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					if (!jsonObject.isNull("msn")) {
						userInfo.msn = jsonObject.getString("msn");
					}
					if (!jsonObject.isNull("visible")) {
						userInfo.visible = jsonObject.getInt("visible");
					}
					if (!jsonObject.isNull("nickSrcMid")) {
						userInfo.nickSrcMid = jsonObject
								.getString("nickSrcMid");
					}
					if (!jsonObject.isNull("chineseName")) {
						userInfo.chineseName = jsonObject
								.getString("chineseName");
					}
					if (!jsonObject.isNull("tel")) {
						userInfo.tel = jsonObject.getString("tel");
					}
					if (!jsonObject.isNull("companyName")) {
						userInfo.companyName = jsonObject
								.getString("companyName");
					}
					if (!jsonObject.isNull("message")) {
						userInfo.message = jsonObject.getString("message");
					}
					if (!jsonObject.isNull("id")) {
						userInfo.id = jsonObject.getInt("id");
					}
					LogUtils.i(TAG, "userInfo.id==>" + userInfo.id);
					if (!jsonObject.isNull("groupId")) {
						userInfo.groupId = jsonObject.getInt("groupId");
					}
					if (!jsonObject.isNull("address")) {
						userInfo.address = jsonObject.getString("address");
					}
					if (!jsonObject.isNull("email")) {
						userInfo.email = jsonObject.getString("email");
					}
					if (!jsonObject.isNull("nickName")) {
						userInfo.nickName = jsonObject.getString("nickName");
					}
					if (!jsonObject.isNull("nickSrc")) {
						userInfo.nickSrc = jsonObject.getString("nickSrc");
					}
					if (!jsonObject.isNull("loginUser")) {
						userInfo.loginUser = jsonObject.getString("loginUser");
					}
					if (!jsonObject.isNull("nickSrc")) {
						userInfo.nickSrc = jsonObject.getString("nickSrc");
					}
					userInfo.content = context.getResources().getString(
							R.string.alert_can_fang_wen);
					infos.add(userInfo);
				}
				LogUtils.i(TAG, "paren size==>" + infos.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 删除授权
	 * 
	 * @param context
	 * @param type
	 * @param id
	 * @return
	 */
	public static boolean delShouQuan(Context context, String type, int id) {
		boolean flag = false;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.DEL_SHOUQUAN + type + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey + "&targetId=" + id;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, "del obj==>" + obj);
			if (!obj.isNull("state")) {
				flag = obj.getBoolean("state");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取位置名称的列表
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static List<LocationName> getLocationAndSave(Context context) {
		ArrayList<LocationName> temp = new ArrayList<LocationName>();
		String locations = SharePreferenceUtils.getLocations(context);
		JSONObject obj = null;
		if (!TextUtils.isEmpty(locations)) {
			try {
				obj = new JSONObject(locations);
			} catch (JSONException e) {
				obj = null;
				e.printStackTrace();
			}
		}
		try {
			if (null == obj) {
				String pkey = "&pkey="
						+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
				String url = Config.HTTP_HEADER
						+ Config.SERVER_NAME
						+ Config.GET_LOCATION
						+ "user="
						+ SharePreferenceUtils.getInstance(context)
								.getCurrentUser() + pkey;
				obj = JSONUtil.getJson(url);
			}
			LogUtils.i(TAG, "getLocationAndSave():obj==>" + obj);
			if (obj != null && !obj.isNull("data")) {
				JSONArray jsonArray = obj.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					if (!jsonObject.isNull("location")) {
						String key = jsonObject.getString("location");
						if (key != null && !"".equals(key)) {
							if (!jsonObject.isNull("roomName")) {
								String location = jsonObject
										.getString("roomName");
								if (location != null) {
									SharePreferenceUtils.getInstance(context)
											.saveLocationValue(context, key,
													location);
									LocationName locationName = new LocationName();
									locationName.name = location;
									locationName.location = key;
									temp.add(locationName);
								}
							}
						}
					}

				}
				SharePreferenceUtils.saveLocations(context, obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 获取设备名称的列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<DeviceTypeName> getDeviceNameList(Context context) {
		ArrayList<DeviceTypeName> temp = new ArrayList<DeviceTypeName>();
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.GET_DEVICE_NAMES + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, "objffffffff==>" + obj);
			if (obj != null && !obj.isNull("data")) {
				JSONArray jsonArray = obj.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					DeviceTypeName deviceTypeName = new DeviceTypeName();
					temp.add(deviceTypeName);
					if (!jsonObject.isNull("groupname")) {
						deviceTypeName.groupname = jsonObject
								.getString("groupname");

					}
					if (!jsonObject.isNull("groupid")) {
						deviceTypeName.groupid = jsonObject.getInt("groupid");
					}
					if ("".equals(deviceTypeName.groupname)
							|| deviceTypeName.groupname == null) {
						deviceTypeName.groupname = context.getResources()
								.getString(R.string.no_know);
					}
					if (!jsonObject.isNull("names")) {
						JSONArray names = jsonObject.getJSONArray("names");
						for (int j = 0; j < names.length(); j++) {
							JSONObject jsonObject1 = (JSONObject) names.get(j);
							if (!jsonObject1.isNull("name")) {
								String dName = jsonObject1.getString("name");
								deviceTypeName.names.add(dName);
								LogUtils.i(TAG, "dName==>" + dName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 获取类型并保存
	 * 
	 * @param context
	 */
	@SuppressWarnings("static-access")
	public static void getTypeAndSave(Context context) {
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.GET_TYPE + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey;
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, "getTypeAndSave():obj==>" + obj);
			if (obj != null && !obj.isNull("data")) {
				JSONArray jsonArray = obj.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					if (!jsonObject.isNull("T1")) {
						String T1 = jsonObject.getString("T1");
						if (T1 != null && !"".equals(T1)) {
							if (!jsonObject.isNull("T2")) {
								String T2 = jsonObject.getString("T2");
								if (T2 != null && !"".equals(T2)) {
									String name = jsonObject.getString("name");
									if (name != null && !"".equals(name)) {
										String key = T1 + T2;
										SharePreferenceUtils.getInstance(
												context).saveLocationValue(
												context, key, name);
										LogUtils.w(TAG, "key==>" + key
												+ " name==>" + name);
									}
								}
							}
						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加房间的类型
	 * 
	 * @param context
	 * @param roomName
	 * @param location
	 * @param floor
	 * @return
	 */
	public static boolean addRoomType(Context context, String roomName,
			int location, int floor) {
		boolean flag = false;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.ADD_ROOMTYPE + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey + "&roomName=" + roomName + "&location=" + location
					+ "&floor=" + floor;
			LogUtils.i(TAG, "url==>" + url);
			JSONObject obj = JSONUtil.getJson(url);
			LogUtils.i(TAG, " obj==>" + obj);
			if (obj != null && !obj.isNull("state")) {
				flag = obj.getBoolean("state");
			}
			LogUtils.i(TAG, " flag==>" + flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 添加设备
	 * 
	 * @param context
	 * @param name
	 * @param typeId
	 * @return
	 */
	public static boolean addDeviceName(Context context, String name, int typeId) {
		boolean flag = false;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String url = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.ADD_DEVICE_NAME + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey + "&typeid=" + typeId + "&name=" + name;
//					+ "&location=" + SwitchDetailActivity.curLocation;
			LogUtils.i(TAG, "url==>" + url);
			JSONObject obj = JSONUtil.getJson(url);
			if (obj != null && !obj.isNull("state")) {
				flag = obj.getBoolean("state");
			}
			LogUtils.i("hxj", " flag==>" + flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 把新的头像图片提交服务器
	 * 
	 * @param context
	 * @param mp
	 * @return
	 */
	public static boolean uploadUserLogo(Context context, Bitmap mp) {
		boolean state = false;
		String pkey = "&pkey="
				+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
		String addAccuUrl = Config.HTTP_HEADER + Config.SERVER_NAME
				+ Config.MODIFY_USER_LOGO + pkey + "&user="
				+ SharePreferenceUtils.getInstance(context).getCurrentUser();
		byte[] bb = PictureUtils.getPicByte(mp);
		String json = Base64.encodeToString(bb, 2);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("nickSrc", json));
		try {
			String result = null; // 用来取得返回的String；
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(addAccuUrl);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				LogUtils.w(TAG, "result==>" + result);
				JSONObject jsonObject = new JSONObject(result);
				state = jsonObject.getBoolean("state");
				LogUtils.i(TAG, "getState==>" + state);
			}
		} catch (Exception e) {
		}
		return state;
	}

	public static String getScanedDeviceInfo(Context context, String scanMac) {
		JSONObject obj = null;
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String ScanDeviceInfoUrl = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.GET_SCAN_DEVICE_INFO + "user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey + "&mac=" + scanMac;
			try {
				LogUtils.e(TAG, "url==>" + ScanDeviceInfoUrl);
				obj = JSONUtil.getJson(ScanDeviceInfoUrl);
				LogUtils.e(TAG, " obj==>" + obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(obj);
	}

	public static boolean networkStateusOK(Context context) {
		boolean netStateus = false;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isAvailable() && networkInfo.isConnected()) {
					netStateus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// if (!netStateus) {
		// Toast.makeText(context,
		// context.getResources().getString(R.string.net_link_alert),
		// Toast.LENGTH_LONG).show();
		// }
		return netStateus;
	}

	/**
	 * 
	 * @param context
	 * @param bubble
	 * @return 返回结果：
	 */
	public static BlueToothBubble bindBlueToothDevice(Context context,
			BlueToothBubble bubble) {
		// macAddr = "000078655c114b0e";
		if (bubble != null && bubble.appearance != null
				&& !TextUtils.isEmpty(bubble.appearance.macAddr)) {
			String macAddr = bubble.appearance.macAddr;
			HttpClient httpclient = null;
			try {
				String pkey = "&pkey="
						+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
				String IP = Config.HTTP_HEADER + Config.SERVER_NAME
						+ Config.BIND_BLUETOOTH_DEVICE;
				String url = IP
						+ "&user="
						+ SharePreferenceUtils.getInstance(context)
								.getCurrentUser() + pkey;
				LogUtils.i(AddBlueToothDeviceActivity.TAG,
						"bindBlueToothDevice() : url = " + url);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair("macAddress", macAddr));
				httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				if (response.getStatusLine().getStatusCode() == 200) {
					String content = EntityUtils.toString(response.getEntity());
					LogUtils.i(TAG,
							"POSTsubmitUtil->bindBlueToothDevice():mac = "
									+ macAddr + ", content = " + content);
					JSONObject obj = new JSONObject(content);
					// {"data":{"bleId":32773,"Passwd":"74be16979710d4c4e7c6647856088456","classId":"000005000108","bleMac":"0000931500038135"},"state":true,"dataCount":4,"errorInfo":null,"error":0}
					if (obj != null && obj.has("state")) {
						bubble.state = obj.getBoolean("state");
						if (obj.getBoolean("state")) {
							if (obj.has("data")) {
								JSONObject object = obj.getJSONObject("data");
								if (object != null) {
									if (object.has("bleId")) {
										bubble.deviceId = object
												.getInt("bleId");
									}
									if (object.has("classId")) {
										bubble.setClassId(object
												.getString("classId"));
									}
								}
							}
						} else {
							if (obj.has("error")) {
								bubble.errorCode = obj.getInt("error");
							}
							if (obj.has("errorInfo")) {
								bubble.errorInfor = obj.getString("errorInfo");
							}
						}
					}
				}
			} catch (Exception e) {
				bubble = null;
				LogUtils.e(AddBlueToothDeviceActivity.TAG,
						"bindBlueToothDevice(): error = " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (httpclient != null) {
					httpclient.getConnectionManager().shutdown();
				}
			}
		}
		return bubble;
	}

	/**
	 * 获取蓝牙设备的classid
	 * 
	 * @param context
	 * @param bubble
	 * @return 返回结果：
	 */
	public static BlueToothBubble getClassID(Context context,
			BlueToothBubble bubble) {
		// macAddr = "000078655c114b0e";
		if (bubble != null && bubble.appearance != null
				&& !TextUtils.isEmpty(bubble.appearance.macAddr)) {
			String macAddr = bubble.appearance.macAddr;
			try {
				String pkey = "&pkey="
						+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
				String IP = Config.HTTP_HEADER + Config.SERVER_NAME
						+ Config.DEVICES_LIST_URL;
				String url = IP
						+ "&user="
						+ SharePreferenceUtils.getInstance(context)
								.getCurrentUser() + pkey + "&mac=" + macAddr;
				// {"data":[{"icon":"http:\/\/121.199.24.18\/uploads\/product\/20151028125401_7869.jpg","img1":"http:\/\/121.199.24.18\/uploads\/","passwd":null,"model":"SN5000208","passwdDefault":null,"location":null,"mac":"0000690000038135","mader":null,"termID":"1464","version":"0000690000038135","content":"s:16:\"<p>帅呆了<\/p>\";","title":"xinLight","name":"BLE冷暖灯","userId":null,"distributor":"Senink","user":null,"sourced":"中国
				// 中山","classid":"000005000208"}],"state":true,"dataCount":1,"errorInfo":null,"error":0}
				JSONObject obj = JSONUtil.getJson(url);
				if (obj.getBoolean("state")) {
					// LogUtils.i("aaa", "GETcLASS-》 obj = "+ obj.toString());
					JSONArray data = obj.getJSONArray("data");
					JSONObject content = data.getJSONObject(0);
					bubble.setClassId(content.getString("classid"));
					bubble.state = true;
				} else {
					bubble.state = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bubble;
	}

	/**
	 * 根据macAddress获取到对应的设备信息
	 * 
	 * @param context
	 * @param mac
	 * @return 返回结果：
	 */
	public static DeviceInfo getDeviceInfor(Context context, String mac) {
		DeviceInfo infor = null;
		LogUtils.i(TAG, "POSTsubmitUtil->bindBlueToothDevice():mac = " + mac);
		if (!TextUtils.isEmpty(mac)) {
			try {
				String pkey = "&pkey="
						+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
				String IP = Config.HTTP_HEADER + Config.SERVER_NAME
						+ Config.BIND_BLUETOOTH_DEVICE;
				String url = IP
						+ "&user="
						+ SharePreferenceUtils.getInstance(context)
								.getCurrentUser() + pkey;

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("macAddress", mac));
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				if (response.getStatusLine().getStatusCode() == 200) {
					String content = EntityUtils.toString(response.getEntity());
					LogUtils.i(TAG,
							"POSTsubmitUtil->bindBlueToothDevice():mac = "
									+ mac + ", content = " + content);
					JSONObject obj = new JSONObject(content);
					// {"data":{"bleId":32773,"Passwd":"74be16979710d4c4e7c6647856088456","classId":"000005000108","bleMac":"0000931500038135"},"state":true,"dataCount":4,"errorInfo":null,"error":0}
					if (obj != null && obj.has("state")) {
						if (obj.getBoolean("state")) {
							if (obj.has("data")) {
								JSONObject object = obj.getJSONObject("data");
								if (object != null) {
									infor = new DeviceInfo();
									if (object.has("classId")) {
										infor.type = CommonUtils
												.getDeviceTypeFromClassId(object
														.getString("classId"));
									}
									if (object.has("bleMac")) {
										infor.mac = object.getString("bleMac");
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return infor;
	}

	/**
	 * 获取连接蓝牙需要的deviceId和key
	 * 
	 * @param context
	 * @param uuid
	 * @return 返回结果：
	 */
	public static BLEInfor getDeviceIdKey(Context context, String uuid, String user, String pwd) {
		BLEInfor infor = null;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			String pkey = "&pkey=" + StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
			String IP = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.BIND_BLUETOOTH_INFOR;
			String url = IP + "&user="
					+ user
					+ pkey;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("uuid", uuid));

			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(response.getEntity());
				LogUtils.i(TAG, "POSTsubmitUtil->bindBlueToothDevice():uuid = "
						+ uuid + ", content = " + content);
				JSONObject obj = new JSONObject(content);
				if (obj != null && obj.has("state")) {
					infor = new BLEInfor();
					infor.state = obj.getBoolean("state");
					if (obj.getBoolean("state")) {
						if (obj.has("data")) {
							JSONObject object = obj.getJSONObject("data");
							if (object != null) {
								if (object.has("bleId")) {
									infor.deviceId = object.getInt("bleId");
								}
								if (object.has("bleKey")) {
									infor.bleKey = object.getString("bleKey");
								}
							}
						}
					} else {
						if (obj.has("error")) {
							infor.errorCode = obj.getInt("error");
						}
						if (obj.has("errorInfo")) {
							infor.errorInfor = obj.getString("errorInfo");
						}
					}
				}
			}
		}catch (HttpHostConnectException e) {
			httpclient.getConnectionManager().shutdown();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return infor;
	}

	/**
	 * 获取手机验证码
	 * 
	 * @param context
	 * @param telephoneNumber
	 *            手机号码
	 * @return 返回结果：
	 */
	public static MobCodeInfor getMobCode(Context context,
										  String telephoneNumber) {
		MobCodeInfor infor = null;
		try {
			String pkey = "pkey=" + StringUtils.getPKey("000000");
			String IP = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.TELEPHONE_MOBCODE_URL;
			String url = IP + pkey;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("tel", telephoneNumber));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(response.getEntity());
				LogUtils.i(TAG,
						"POSTsubmitUtil->getMobCode():telephoneNumber = "
								+ telephoneNumber + ", content = " + content);
				JSONObject obj = new JSONObject(content);
				// {"state":true,"data":{"info":"-105,1446108833","phoneNum":null,"mobCode":"8296"},"dataCount":3,"error":0,"errorInfo":null}
				if (obj != null && obj.has("state")) {
					infor = new MobCodeInfor();
					infor.state = obj.getBoolean("state");
					if (obj.getBoolean("state")) {
						if (obj.has("data")) {
							JSONObject object = obj.getJSONObject("data");
							if (object != null) {
								if (object.has("phoneNum")) {
									infor.telNumber = object
											.getString("phoneNum");
								}
								if (object.has("mobCode")) {
									infor.mobCode = object.getString("mobCode");
								}
								if (object.has("info")) {
									infor.infor = object.getString("info");
								}
							}
						}
					} else {
						if (obj.has("error")) {
							infor.errorCode = obj.getInt("error");
						}
						if (obj.has("errorInfo")) {
							infor.errorInfor = obj.getString("errorInfo");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infor;
	}

	/**
	 * 通过手机号码注册账号
	 * 
	 * @param context
	 * @param telephoneNumber
	 *            手机号码
	 * @return 返回结果：
	 */
	public static MobCodeInfor registerFromMobile(Context context,
			String telephoneNumber, String pwd, String mobCode) {
		MobCodeInfor infor = null;
		try {
			String pkey = "pkey=" + StringUtils.getPKey(mobCode);
			String IP = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.REGISTER_MOBILE_USER_URL;
			String url = IP + pkey;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("tel", telephoneNumber));
			nameValuePairs.add(new BasicNameValuePair("newpwd", StringUtils
					.MD5(StringUtils.MD5(pwd))));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				// {"state":true,"data":{"user":"18603003423","groupId":1,"parentid":0,"nickName":null,"nickSrc":null,"chineseName":null,"companyName":null,"address":null,"email":"18603003423@qq.com","tel":"18603003423","qq":null,"message":null},"dataCount":12,"error":null,"errorInfo":null}
				String content = EntityUtils.toString(response.getEntity());
				LogUtils.i(TAG,
						"POSTsubmitUtil->registerFromMobile():telephoneNumber = "
								+ telephoneNumber + ", content = " + content);
				JSONObject obj = new JSONObject(content);
				if (obj != null && obj.has("state")) {
					infor = new MobCodeInfor();
					infor.state = obj.getBoolean("state");
					if (obj.getBoolean("state")) {
						if (obj.has("data")) {
							JSONObject object = obj.getJSONObject("data");
							if (object != null) {
								UserInfo userInfor = new UserInfo();
								if (object.has("user")) {
									userInfor.loginUser = object
											.getString("user");
								}
								if (object.has("groupId")) {
									userInfor.groupId = object
											.getInt("groupId");
								}
								if (object.has("parentid")) {
									userInfor.parentId = object
											.getInt("parentid");
								}
								if (object.has("nickName")) {
									userInfor.nickName = object
											.getString("nickName");
								}
								if (object.has("nickSrc")) {
									userInfor.nickSrc = object
											.getString("nickSrc");
								}
								if (object.has("chineseName")) {
									userInfor.chineseName = object
											.getString("chineseName");
								}

								if (object.has("companyName")) {
									userInfor.companyName = object
											.getString("companyName");
								}
								if (object.has("address")) {
									userInfor.address = object
											.getString("address");
								}
								if (object.has("email")) {
									userInfor.email = object.getString("email");
								}
								if (object.has("tel")) {
									userInfor.tel = object.getString("tel");
								}
								if (object.has("qq")) {
									userInfor.qq = object.getString("qq");
								}
								if (object.has("message")) {
									userInfor.message = object
											.getString("message");
								}
								infor.userInfor = userInfor;
							}
						}
					} else {
						if (obj.has("error")) {
							infor.errorCode = obj.getInt("error");
						}
						if (obj.has("errorInfo")) {
							infor.errorInfor = obj.getString("errorInfo");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infor;
	}

	/**
	 * 通过手机号码找回密码之重置密码
	 * 
	 * @param context
	 * @param telephoneNumber
	 *            手机号码
	 * @return 返回结果：
	 */
	public static MobCodeInfor resetPWD(Context context,
			String telephoneNumber, String pwd, String mobCode) {
		MobCodeInfor infor = null;
		try {
			String pkey = "pkey=" + StringUtils.getPKey(mobCode);
			String IP = Config.HTTP_HEADER + Config.SERVER_NAME
					+ Config.MODIFY_PASSWORD_SETPWD_URL;
			String url = IP + pkey;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("tel", telephoneNumber));
			nameValuePairs.add(new BasicNameValuePair("newpwd", StringUtils
					.MD5(StringUtils.MD5(pwd))));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				// {"state":true,"data":[],"dataCount":0,"error":null,"errorInfo":null}
				String content = EntityUtils.toString(response.getEntity());
				LogUtils.i(TAG,
						"POSTsubmitUtil->getMobCode():telephoneNumber = "
								+ telephoneNumber + ", content = " + content);
				JSONObject obj = new JSONObject(content);
				if (obj != null && obj.has("state")) {
					infor = new MobCodeInfor();
					infor.state = obj.getBoolean("state");
					if (!obj.getBoolean("state")) {
						if (obj.has("error")) {
							infor.errorCode = obj.getInt("error");
						}
						if (obj.has("errorInfo")) {
							infor.errorInfor = obj.getString("errorInfo");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infor;
	}

	/**
	 * 修改用户信息
	 * 
	 * @param context
	 *            上下文
	 * @param infor
	 *            需要修改的用户信息
	 */
	public static boolean modifyUserInfor(Context context, UserInfo infor) {
		boolean success = false;
		if (infor != null) {
			try {
				String pkey = "pkey="
						+ StringUtils.getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
				String user = "&user="
						+ SharePreferenceUtils.getInstance(context)
								.getCurrentUser();
				String IP = Config.HTTP_HEADER + Config.SERVER_NAME
						+ Config.EDIT_USER_INFOR_URL;
				String url = IP + pkey + user;
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				if (!TextUtils.isEmpty(infor.email)) {
					nameValuePairs.add(new BasicNameValuePair("email",
							infor.email));
				}
				if (!TextUtils.isEmpty(infor.tel)) {
					nameValuePairs
							.add(new BasicNameValuePair("tel", infor.tel));
				}
				if (!TextUtils.isEmpty(infor.nickName)) {
					nameValuePairs.add(new BasicNameValuePair("nickName",
							infor.nickName));
				}
				if (!TextUtils.isEmpty(infor.companyName)) {
					nameValuePairs.add(new BasicNameValuePair("companyName",
							infor.companyName));
				}
				if (!TextUtils.isEmpty(infor.address)) {
					nameValuePairs.add(new BasicNameValuePair("address",
							infor.address));
				}
				if (!TextUtils.isEmpty(infor.qq)) {
					nameValuePairs.add(new BasicNameValuePair("qq", infor.qq));
				}
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				if (response.getStatusLine().getStatusCode() == 200) {
					// {"state":true,"data":{"id":"5","groupId":"2","loginUser":"testdev","password":null,"nickName":"???1","openid":null,"parentid":"0","type":"0","nickSrc":"member\/20160119205831_0009.png","chineseName":"xxxxx","firstName":null,"lastName":null,"memberLink":null,"email":"sdfxxxg@34qasd.com","tel":null,"mobile":null,"msn":null,"qq":"545454","prices":"0","address":"???","companyName":"\u5fc3\u80fd","registerIP":"121.34.130.21","registerTime":"1413257785","lastIP":null,"lastTime":"1453208311","loginNumber":"0","message":null,"description":null,"visible":"0","weixinOpenid":"ou7mas6eQALdmMw5FgKCaONVJN74","isWeixinBind":"1","bleKey":"5ozhn"},"dataCount":32,"error":0,"errorInfo":null}
					String content = EntityUtils.toString(response.getEntity());
					JSONObject obj = new JSONObject(content);
					if (obj != null && obj.has("state")) {
						success = obj.getBoolean("state");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	/**
	 * 上传手机串号、手机型号、手机版本、应该名称、应用版本、上传时间
	 * 
	 * @param context
	 * @return
	 */
	public static boolean uploadInforOnPhone(Context context) {
		boolean state = false;
		String uri = "http://www.touchome.net/webapi/bleinforecord.php";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("op", "1"));
		list.add(new BasicNameValuePair("p_phone_id", Utils
				.getMIEIOnTelephone(context)));
		list.add(new BasicNameValuePair("p_phone_model", Utils
				.getModelOnTelephone()));
		list.add(new BasicNameValuePair("p_app_os", "Android"));
		list.add(new BasicNameValuePair("p_os_version", Utils
				.getVersionOnSystem()));
		list.add(new BasicNameValuePair("p_app_name", Utils
				.getApplicationName(context)));
		list.add(new BasicNameValuePair("p_app_version", Utils
				.getVersionOnApplication(context)));
		list.add(new BasicNameValuePair("p_rec_time ", ""
				+ (System.currentTimeMillis() / 1000)));
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
			post.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse responese = client.execute(post);
			if (responese.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(responese.getEntity());
				LogUtils.i(TAG, "uploadInforOnPhone(): content = "+ content);
				JSONObject obj = new JSONObject(content);
				if (obj != null && obj.has("state")) {
					state = obj.getBoolean("state");
				}
			}
		} catch (Exception e) {
			LogUtils.i(TAG, "uploadInforOnPhone(): error = "+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return state;
	}

	/**
	 * 上传手机串号(、开始连接时间、结束连接时间、连接结果、应用版本、上传时间、蓝牙通讯记录ID、发送PIPA数据包个数、接收PIPA数据包个数、
	 * 发送PIPA数据总量、接收PIPA数据总量、记录数据时间、上传时间)
	 * 
	 * @param context
	 * @param map
	 * @param onlyBLE
	 *            是仅仅是蓝牙统计数据
	 * @return
	 */
	public static boolean uploadPipaInfor(Context context,
			Map<String, String> map) {
		boolean state = false;
		String uri = "http://www.touchome.net/webapi/bleinforecord.php";
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("op", "2"));
		list.add(new BasicNameValuePair("p_phone_id", Utils
				.getMIEIOnTelephone(context)));
		list.add(new BasicNameValuePair("p_pipa_data_time", ""
				+ (System.currentTimeMillis() / 1000)));
		if (map != null) {
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				list.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
			post.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse responese = client.execute(post);
			if (responese.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(responese.getEntity());
				LogUtils.i(TAG, "uploadPipaInfor(): content = "+ content);
				JSONObject obj = new JSONObject(content);
				if (obj != null && obj.has("state")) {
					state = obj.getBoolean("state");
				}
			}
		} catch (Exception e) {
			LogUtils.i(TAG, "uploadPipaInfor(): error = "+ e.getMessage());
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		list.clear();
		return state;
	}
}
