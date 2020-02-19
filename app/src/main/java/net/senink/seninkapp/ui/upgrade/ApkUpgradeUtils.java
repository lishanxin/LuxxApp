package net.senink.seninkapp.ui.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.net.HTTPRequest;
import net.senink.seninkapp.net.HTTPResponse;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.Config;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.StringUtils;

/**
 * 用于版本升级的工具类
 * 
 * @author zhaojunfeng
 * @date 2015-10-27
 */
public class ApkUpgradeUtils {
	private static final String TAG = "ApkUpgradeUtils";
	//检查新版的url
	public static final String UPGRADE_URL = "http://www.touchome.net/m/version1.xml";
	// 版本号
	public float version;
	// 版本名称
	public String apkName;
	// 新版本更新内容
	public String apkIntroduce;
	// 新版本的下载地址
	public String apkUrl;
	// 上下文
	private Context context;
	// 是否有新版
	private boolean hasNew = false;
    //返回数据是否正确
	private boolean state = false;
	//错误码
	private int errorCode = -1;
	//错误原因
	private String errorInfor = null;

	public ApkUpgradeUtils(Context context) {
		this.context = context;
		
	}

	/**
	 * 检查版本是否更新
	 * @return
	 */
	public boolean ugrade(boolean isJSon){
		if (isJSon) {
			getUpgradeInfor(context);
			hasNew = isNew();
		} else {
			HTTPRequest httpRequest = new HTTPRequest();
			try {
				HTTPResponse response = httpRequest.get(UPGRADE_URL);
				InputStream instream = response.getStream();
				hasNew = upgradeOnSAX(instream);
			} catch (IOException e) {
				hasNew = false;
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		}
		return hasNew;
	}
	
	/**
	 * 通过sax解析判断出是否有版本更新
	 * @param instream
	 * @return
	 */
	public boolean upgradeOnSAX(InputStream instream) {
		boolean hasVers = false;
		SAXParserFactory factory = SAXParserFactory.newInstance();// 创建SAX解析工厂
		try {
			SAXParser paser = factory.newSAXParser();
			ApkUpgradePaser upgradeParse = new ApkUpgradePaser();// 创建事件处理程序
			paser.parse(instream, upgradeParse);// 开始解析
			instream.close();
			hasVers = isNew();
		} catch (ParserConfigurationException e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		} catch (SAXException e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}// 创建SAX解析器
		catch (IOException e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
		return hasVers;
	}

	/**
	 * 检查当前版本是否小于新的版本号
	 * @return
	 */
	private boolean isNew(){
		boolean hasVers = false;
		if (context != null) {
			PackageInfo infor = CommonUtils.getApkInfor(context);
			float verName = 0;
			try {
				verName = Float.parseFloat(infor.versionName);
				if (verName < version) {
					hasVers = true;
				}
			} catch (NumberFormatException e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		}
		return hasVers;
	}
	
	/**
	 * 获取手机验证码
	 * 
	 * @param context
	 *     手机号码
	 * @return 返回结果：
	 */
	public void getUpgradeInfor(Context context) {
		try {
			String pkey = "&pkey="
					+ StringUtils.getPKey(SharePreferenceUtils.getInstance(context)
							.getPassword());
			String IP = Config.HTTP_HEADER + Config.SERVER_NAME + Config.UPGRADE_APK_URL;
			String url = IP + "&user="
					+ SharePreferenceUtils.getInstance(context).getCurrentUser()
					+ pkey;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(response.getEntity());
				LogUtils.i(TAG, "ApkUpgradeUtils->getUpgradeInfor():content = " + content);
				JSONObject obj = new JSONObject(content);
				if (obj != null && obj.has("state")) {
					state = obj.getBoolean("state");
					if (obj.getBoolean("state")) {
						if (obj.has("data")) {
							JSONObject object = obj.getJSONObject("data");
							if (object != null) {
								if (object.has("version")) {
									try{
										  version = Float.parseFloat(object.getString("version"));
										}catch(NumberFormatException e){
											version = 0;
										}
								}
								if (object.has("name")) {
									apkName = object.getString("name");
								}
								if (object.has("url")) {
									apkUrl = object.getString("url");
								}
								if (object.has("intro")) {
									apkIntroduce = object.getString("intro");
								}
							}
						}
					} else {
						if (obj.has("error")) {
							errorCode = obj.getInt("error");
						}
						if (obj.has("errorInfo")) {
							errorInfor = obj.getString("errorInfo");
						}
					}
				}
			}
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}
	
	/**
	 * sax解析
	 * @author zhaojunfeng
	 *
	 */
	public final class ApkUpgradePaser extends DefaultHandler {
		private String tagName;

		@Override
		public void startDocument() throws SAXException {

		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			tagName = localName;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (tagName != null) {
				String data = new String(ch, start, length);
				if ("version".equals(tagName)) {
					try{
					  version = Float.parseFloat(data);
					}catch(NumberFormatException e){
						version = 0;
					}
				} else if ("name".equals(tagName)) {
					apkName = data;
				}else if ("url".equals(tagName)) {
					apkUrl = data;
				}else if ("intro".equals(tagName)) {
					apkIntroduce = data;
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			tagName = null;
		}
	}
}
