package net.senink.seninkapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import net.senink.piservice.pis.PISManager;

public class DataService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hxj", "service onCreate");
		PISManager.getInstance();
		new Thread(runnable).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		PISManager pm = PISManager.getInstance();
//		pm.disConnect(this);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
//			PISManager.locations = HttpUtils.getLocationAndSave(DataService.this);
//			HttpUtils.getTypeAndSave(DataService.this);
		}
	};

	@SuppressLint("SimpleDateFormat")
	public static String getNowTime() {
		Date currentTime = new Date();
		currentTime.setTime(java.lang.System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getNowDate() {
		Date currentTime = new Date();
		currentTime.setTime(java.lang.System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
}
