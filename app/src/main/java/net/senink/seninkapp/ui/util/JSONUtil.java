package net.senink.seninkapp.ui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import net.senink.seninkapp.ui.fusion.FusionField;

/**
 * JSON
 */
public class JSONUtil {

	/**
	 * 用户解析数据
	 */
	public static final String URL_VERSION_USERINFO = "http://www.senink.net/senink/cn/member_app.php?op=list&user=";
	public static final String DATA_SOURCE = "data";
	public static final String USERNAME = "loginUser";
	public static final String NICKNAME = "nickName";
	public static final String EMAIL = "email";
	private static final String CHARSET = HTTP.UTF_8;

	/**
	 * 网关解析数据
	 */
	public static final String URL_VERSION_GATEWAY = "http://www.senink.net/Senink_home_SDK/sample_code/gatewayList.php?op=list&user=";
	public static final String MAC_ADDR = "mac";
	public static final String CLASSID = "classid";
	public static final String USER_NAME = "user";
	public static final String LOCATION = "location";
	public static final String VERSION = "version";
	public static final String NAME = "name";
	public static final String AUTHUSER = "authUser";
	public static final String AUTHPWD = "authPasswd";
	public static final String TERMID = "termID";
	public static final String Binded = "binded";
	public static final String TITLE = "title";
	public static final String DISTRI = "distributor";
	public static final String SOURCED = "sourced";
	public static final String CONTENT = "content";
	public static final String STATE = "state";
	public static final String ERRORINFOR = "errorInfo";
	public static final String DATA = "data";
	public static final String USER_NICKNAME = "nickName";
	public static final String DATA_COUNT = "dataCount";
	public static final String DATA_USER = "loginUser";
	public static final String DATA_PWD = "password";
	public static final String DATA_NICKNAME = "nickName";
	public static final String DATA_NICKSRC = "nickSrc";
	public static final String DATA_EMAIL = "email";
	public static final String DATA_TELEPHONE = "tel";
	public static final String DATA_ADDRESS = "address";
	//device
	public static final String DEVICE_MADER="mader";
	public static final String DEVICE_IMGSRC="img1";
	public static final String DEVICE_MODE="model"; 
	public static final String MAC="mac"; 
	
	public static final String FLAG = "Flag";
	public static final String mac ="MAC";
	public static final String ClassID = "classid";
	public static final String PASSWD = "passwd";
	public static final String SSID = "SSID";
	public static final String WIFIPWD = "WIFIpwd";
	
	
	/**
	 * [{"mac":"000078995c114b08","classid":"000000000101","user":"test10",
	 * "location"
	 * :null,"version":"00000001","name":"Client","authUser":null,"authPasswd"
	 * :null,"termID":"119","binded":1,"title":"\u7f51\u5173","model":"R001",
	 * "distributor":"senink","sourced":"\u4e2d\u56fd\u6df1\u5733","content":"
	 */

	/**
	 * {"state":true,"data":
	 * {"id":"108","groupId":"1","loginUser":"testper","password"
	 * :"d9b1d7db4cd6e70935368a1efb10e377"
	 * ,"nickName":"\u6d4b\u8bd5\u5458","nickSrc"
	 * :null,"chineseName":null,"firstName"
	 * :null,"lastName":null,"memberLink":null
	 * ,"email":"robot_lj@sina.com","tel":
	 * null,"mobile":null,"msn":null,"qq":null
	 * ,"openid":null,"parentid":"0","address"
	 * :null,"companyName":null,"registerIP"
	 * :"116.24.216.124","registerTime":"1394015337"
	 * ,"lastIP":null,"lastTime":"1396510018"
	 * ,"loginNumber":"0","message":null,"description":null,"visible":"0" },
	 * "dataCount":27,"errorInfo":null }
	 * 
	 * @param url
	 * @return
	 */
	public static JSONObject getJson(String url) {
		DefaultHttpClient httpClient = null;
		JSONObject object = null;
		try {
			String result = null;
			httpClient = new DefaultHttpClient();
			/**
			 * Cy修改
			 */
			 KeyStore trustStore = KeyStore.getInstance(KeyStore
	                    .getDefaultType());
	            trustStore.load(null, null);
			
			SocketFactory socketFactory = new SSLSocketFactoryEx(trustStore);
	        Scheme sch = new Scheme("https", socketFactory, 443);
	        httpClient.getConnectionManager().getSchemeRegistry().register(sch);
			/**
			 * cy修改结束
			 */
			HttpGet request = new HttpGet(url);
			HttpResponse response = httpClient.execute(request);
			result = EntityUtils.toString(response.getEntity(), CHARSET);
			Log.i("hxj","result--- :"+result);
			result = JSONTokener(result);
			object = new JSONObject(result);
		} catch (Exception e) {
			object = null;
			e.printStackTrace();
		}finally{
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return object;
	}
	
	public static JSONObject getJsonNew(String url) {
		try {
			String result = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = httpClient.execute(request);
//			SDCardUtils.saveInformation(response.getEntity().getContent());
			result = EntityUtils.toString(response.getEntity(), CHARSET);
			Log.i("hxj","result--- :"+result);
			result = JSONTokener(result);
			
//			if (FusionField.DEBUG) {
//				Log.i(FusionField.TAG,"getJson:result:"+result);
//			}
//			Log.i("hxj","getJson:resultresult :"+result.substring(result.indexOf(JSONUtil.DATA_COUNT)));
			
			JSONObject object = new JSONObject(result);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getJsonHttp(String jsonUrl) {
		  String strResult = "";
		  /* 建立HTTP Get联机 */
		  // HttpGet httpRequest = new HttpGet(jsonUrl);
		  HttpGet httpRequest = new HttpGet(jsonUrl);
		  try {
		   /* 发出HTTP request */
		   HttpResponse httpResponse = new DefaultHttpClient()
		     .execute(httpRequest);
		   /* 若状态码为200 ok */
		   if (httpResponse.getStatusLine().getStatusCode() == 200) {
		    /* 取出响应字符串 */
		    strResult = EntityUtils.toString(httpResponse.getEntity());
		    /* 删除多余字符 */
		    // strResult = eregi_replace("(/r/n|/r|/n|/n/r)","",strResult);
		    Log.i("hello", strResult);
		   }
		  } catch (Exception e) {
		  }
		  return strResult;
		}

	public static String getJsonRdLine(String jsonUrl) {
		  StringBuffer strB = new StringBuffer();
		  try {
		   URL url1 = new URL(jsonUrl.replace(" ", ""));// 去空格
		   URLConnection conn = url1.openConnection();
		   // Get the response
		   InputStreamReader r = new InputStreamReader(conn.getInputStream());
		   BufferedReader rd = new BufferedReader(r);
		   File file = new File(SDCardUtils.PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
//		   FileOutputStream fps = new FileOutputStream(file, false);
//			OutputStreamWriter osw = new OutputStreamWriter(fps);
		   String line;
		   while ((line = rd.readLine()) != null) {
		    strB.append(line);
//		    osw.write(line);
		   }
//		   osw.close();
		   rd.close();
		  } catch (Exception e) {
		  }
		  return strB.toString();
		}

	public static JSONArray append(JSONArray toArrays, JSONArray fromArrays) {
		for (int i = 0; i < fromArrays.length(); i++) {
			toArrays.put(getJsonObjByIndex(fromArrays, i));
		}
		return toArrays;
	}

	public static String JSONTokener(String result) {
		// consume an optional byte order mark (BOM) if it exists
		if (result != null) {
			result.trim();
		}
		if (result != null && result.startsWith("/ufeff")) {
			result = result.substring(1);
		}
		if (result!=null && result.indexOf("{") > 0) {
			if (result.length()>0) {
				result.trim();
			}
			result = result.substring(result.indexOf("{"), result.length());
		}
		return result;
	}

	/**
	 * @param obj
	 * @param jsonArray
	 * @return
	 */
	public static JSONArray appendTop(JSONObject obj, JSONArray jsonArray) {
		JSONArray array = new JSONArray();
		try {
			array.put(0, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			array.put(getJsonObjByIndex(jsonArray, i));
		}
		return array;
	}

	/**
	 * @param obj
	 * @param keyname
	 * @return null if JSONException
	 */
	public static JSONObject getJsonObject(JSONObject obj, String keyname) {
		JSONObject jsonObj = null;
		try {
			jsonObj = obj.getJSONObject(keyname);
		} catch (JSONException e) {
			return null;
		}
		return jsonObj;
	}

	/**
	 * @param obj
	 * @param keyname
	 * @return
	 */
	public static JSONArray getJsonArrays(JSONObject obj, String keyname) {
		JSONArray jsonArray = null;
		try {
			if (obj != null) {
				jsonArray = obj.getJSONArray(keyname);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	/**
	 * @param array
	 * @param index
	 * @return
	 */
	public static JSONObject getJsonObjByIndex(JSONArray array, int index) {
		JSONObject jsonObj = null;
		try {
			jsonObj = array.getJSONObject(index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	/**
	 * @param obj
	 * @param keyname
	 * @return int.
	 */
	public static int getInt(JSONObject obj, String keyname) {
		int res = 0;
		if (obj != null) {
			try {
				res = obj.getInt(keyname);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public static double getDouble(JSONObject obj, String keyname) {
		double res = 0;
		if (obj != null) {
			try {
				res = obj.getDouble(keyname);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * @param obj
	 * @param keyname
	 * @return String.
	 */
	public static String getString(JSONObject obj, String keyname) {
		String res = null;
		if (obj != null) {
			try {
				res = obj.getString(keyname);
			} catch (JSONException e) {
				Log.e("no json value", e.toString());
				res = "";
			}
		}
		return res;
	}

	/**
	 * @param obj
	 * @param keyname
	 * @return String.
	 */
	public static boolean getBoolean(JSONObject obj, String keyname) {
		boolean res = false;
		if (obj != null) {
			try {
				res = obj.getBoolean(keyname);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * @param arrays
	 * @param word
	 * @return String.
	 */
	public static String getHrefByRel(JSONArray arrays, String word) {
		String href = null;
		if (arrays == null)
			return null;
		for (int i = 0; i < arrays.length(); i++) {
			JSONObject link = JSONUtil.getJsonObjByIndex(arrays, i);
			String rel = JSONUtil.getString(link, "rel");
			int start = rel.lastIndexOf("/") + 1;
			String keyWord = rel.substring(start, rel.length());
			if (keyWord.equals(word)) {
				href = JSONUtil.getString(link, "href");
				break;
			}
		}
		return href;
	}

	/**
	 * @param value
	 * @return
	 */
	public static boolean isNull(String value) {
		boolean b = false;
		if (value == null || value.equals("null") || value.length() <= 0) {
			b = true;
		}
		return b;
	}

	public static boolean isNull(JSONObject value) {
		boolean b = false;
		if (value == null || value.equals("null")) {
			b = true;
		}
		return b;
	}

	public static JSONObject createJSONObject(String str) {
		JSONObject obj;
		try {
			obj = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
			obj = null;
		}
		return obj;
	}

	/**
	 * @param str
	 * @return
	 */
	public static JSONArray createJSONOArray(String str) {
		JSONArray array;
		try {
			array = new JSONArray(str);
		} catch (JSONException e) {
			e.printStackTrace();
			array = null;
		}
		return array;
	}

	/**
	 * JsonArray2String.
	 * 
	 * @param array
	 * @return
	 */
	public static String JsonArray2String(JSONArray array) {
		String res = null;
		if (array != null) {
			res = array.toString();
		}
		return res;
	}

	public static JSONArray array2JsonArray(ArrayList<String> arrays) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < arrays.size(); i++) {
			try {
				array.put(i, arrays.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	public static JSONArray array2JsonArray(String[] arrays) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < arrays.length; i++) {
			try {
				array.put(i, arrays[i]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}
	
	/**
	 * getBoolean.
	 * 
	 * @param result
	 * @return boolean
	 */
	public static boolean getBoolean(String result,String key) {
		if (FusionField.DEBUG) {
			Log.i(FusionField.TAG, "getBoolean: RESULT : "+result);
		}
		if (result != null && result.length() > 0) {
			JSONObject obj = createJSONObject(result);
			try {
				if (obj != null && obj.getBoolean(key)) {
					return true;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}
	/**
	 * getString.
	 * 
	 * @param result
	 * @return String
	 */
	public static String getString(String result,String key) {
		if (true) {
			Log.i("hxj", "getString: RESULT : "+result);
		}
		if (result != null && result.length() > 0) {
			JSONObject obj = createJSONObject(result);
			try {
				if (obj != null) {
					return obj.getString(key);
				}
			} catch (JSONException e) {
				return null;
			}
		}
		return null;
	}
}
