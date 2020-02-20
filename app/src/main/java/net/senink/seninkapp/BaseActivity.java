package net.senink.seninkapp;


import android.app.Activity;
import android.os.Bundle;

import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.util.TelinkLog;

import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.seninkapp.ui.util.Utils;

public class BaseActivity extends Activity  {
	private final static String TAG = "BaseActivity";
	static MyApplication application;

	protected int activityMode = 0;
	protected PipaRequest lastRequest = null;
	protected PipaRequestData lastRequestData = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (MyApplication) getApplication();
//		application.addActivity(this);
//		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
//                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        Utils.printMemoryInfor("MAT", getClass().getName() + ":onCreate()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Utils.printMemoryInfor("MAT", getClass().getName() + ":onResume()");
//		POSTsubmitUtil.networkStateusOK(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Utils.printMemoryInfor("MAT", getClass().getName() + ":onRestart()");
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
//		application.removeActivity(this);
	}

	@Override
	protected void onStop() {
		Utils.printMemoryInfor("MAT", getClass().getName() + ":onStop()");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Utils.printMemoryInfor("MAT", getClass().getName() + ":onDestroy()");
//		application.removeActivity(this);
//		unregisterReceiver(mHomeKeyEventReceiver);
		System.gc();
	}

	public static void updateMark(int updateLebel) {
		application.setLabel(updateLebel);
	}

	public static int getUpdateMark() {

		return application.getLabel();
	}



//	 private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
//	        String SYSTEM_REASON = "reason";
//	        String SYSTEM_HOME_KEY = "homekey";
//
//	        @Override
//	        public void onReceive(Context context, Intent intent) {
//	            String action = intent.getAction();
//	            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//	                String reason = intent.getStringExtra(SYSTEM_REASON);
//	                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
//	                	SharePreferenceUtils.setBackGroundRun(BaseActivity.this, true);
//	                	Activity act = ((MyApplication) getApplication()).getHomeActivity();
////	                	if (act != null) {
////							((HomeActivity)act).onBackPressed();
////						}
//	                }
//	            }
//	        }
//	    };
}
