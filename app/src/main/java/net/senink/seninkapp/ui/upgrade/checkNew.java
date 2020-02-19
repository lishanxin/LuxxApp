package net.senink.seninkapp.ui.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;

public class checkNew extends Thread {

	private Context mContext;
	private int cmMark;
	int versionCode = 0;
	HashMap<String, String> mHashMap;

	public checkNew(Context context, int cMark) {
		mContext = context;
		cmMark = cMark;
	}

	public void run() {

		// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
		try {
			versionCode = mContext.getPackageManager().getPackageInfo(
					"com.senink.seninkapp", 0).versionCode;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}

		// Toast.makeText(mContext,versionCode, Toast.LENGTH_LONG).show();
		// 把version.xml放到网络上，然后获取文件信息

		// InputStream inStream =
		// ParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");

		// InputStream is = url.openStream();
		// 解析XML文件由于XML文件比较小，因此使用DOM方式进行解析
		// ------------------------------------------------------------------------
		InputStream inStream = null;
		try {
			URL urlnew = new URL("http://www.touchome.net/m/version.xml");
			URLConnection connection = urlnew.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}

		// --------------------------------------------
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
		if (null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			Log.i("xxxx", Integer.toString(serviceCode));
			// 版本判断
			if (serviceCode > versionCode) {
				System.out.println("需要更新");
				// 检查更新
				// checkUpdateDownload();
				// manager.showNoticeDialog();
				// 检查更新结束

				Message msg = meHandler.obtainMessage();
				msg.what = 80000;
				meHandler.sendMessage(msg);
			} else {

				if (cmMark == 0) {
					Message msg = meHandler.obtainMessage();
					msg.what = 70000;
					meHandler.sendMessage(msg);
				} else {
				}

				// return false;
			}
		}

	}

	@SuppressLint("HandlerLeak")
	Handler meHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 70000:

				Toast.makeText(mContext, "不需要更新", Toast.LENGTH_LONG).show();

				break;

			case 80000:

				UpdateManager manager = new UpdateManager(mContext);
				manager.checkUpdate();

				break;
			}
		}
	};

}
