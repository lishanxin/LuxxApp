package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinCenter;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISBlueLinker;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;

/**
 * 用于连接wifi
 * 
 * @author zhaojunfeng
 * @date 2015-08-25
 */
public class WifiConnectActivity extends BaseActivity implements
		View.OnClickListener{
	// 标题名称
	private TextView tvTitle;
	// 标题的中的取消按钮
	private Button cancelBtn;
	// 标题中的添加按钮
	private Button addBtn;
	// 输入密码框
	private EditText etPwd;
	// ssid
	private String ssid;
	// 网关类
	private PISXinCenter linker;
	// 请求是否超时
	private boolean isTimeout = false;
	private ProgressDialog progressDialog;
	private PISManager manager;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageModel.MSG_WIFI_TIMEOUT:
				cancelTimer();
				isTimeout = true;
				progressDialog.dismiss();
				ToastUtils.showToast(WifiConnectActivity.this,
						R.string.wifi_input_failed);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wificonnect);
		manager = PISManager.getInstance();
		initView();
		initDialog();
		setData();
		setView();
		setListener();
	}

	/**
	 * 返回上级界面
	 * 
	 * @param isReseted
	 */
	private void back(boolean isReseted) {
		Intent it = new Intent();
		it.putExtra(MessageModel.WIFI_REBOOT, isReseted);
		setResult(RESULT_OK, it);
		WifiConnectActivity.this.finish();
		WifiConnectActivity.this.overridePendingTransition(
				R.anim.anim_in_from_left, R.anim.anim_out_to_right);
	}

	/**
	 * 初始化加载框
	 */
	private void initDialog() {
		progressDialog = Utils.createProgressDialog(WifiConnectActivity.this,
				R.string.wifi_loading);
	}

	/**
	 * 设置ssid和key输入错误的提示
	 */
	private void setErrorTipOnSSIDOrKey() {
		if (!isTimeout) {
			cancelTimer();
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				ToastUtils.showToast(WifiConnectActivity.this,
						R.string.wifi_input_failed);
			}
		}
	}
	
	private void setView() {
		if (ssid != null) {
			tvTitle.setText(ssid);
		} else {
			tvTitle.setText(R.string.wifi);
		}
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		Intent intent = getIntent();
		if (intent != null
				&& intent.getStringExtra(MessageModel.ACTIVITY_VALUE) != null) {
			String value = intent.getStringExtra(MessageModel.ACTIVITY_VALUE);
//			PISBase base = PISManager.cacheMap.get(value);
//			if (base != null && base instanceof PISBlueLinker) {
//				linker = (PISXinCenter) base;
//			}
			ssid = intent.getStringExtra("ssid");
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		cancelBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		etPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setAddBtn(true);
			}
		});
	}

	/**
	 * 设置添加按钮是否可用
	 * 
	 * @param enable
	 */
	private void setAddBtn(boolean enable) {
		addBtn.setEnabled(enable);
		if (enable) {
			addBtn.setTextColor(Color.WHITE);
		} else {
			addBtn.setTextColor(0xff5da2ec);
		}
	}

	@Override
	public void onBackPressed() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		} else {
			cancelBtn.performClick();
		}
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.wifi_title_name);
		addBtn = (Button) findViewById(R.id.wifi_title_add);
		cancelBtn = (Button) findViewById(R.id.wifi_title_cancel);
		etPwd = (EditText) findViewById(R.id.wifi_connect_pwd);
		setAddBtn(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.wifi_title_cancel:
			back(false);
			break;
		case R.id.wifi_title_add:
			isTimeout = false;
			if (linker != null && ssid != null) {
				String pwd = etPwd.getText().toString();
				if (pwd != null && !"".equals(pwd)) {
					pwd = pwd.trim();
					progressDialog.show();
					mHandler.postDelayed(timer, MessageModel.MAX_TIME);
//					linker.setSSIDKEY(ssid, pwd, true);
				} else {
					ToastUtils.showToast(this, R.string.alert_password_no_null);
				}
			}
			break;
		}
	}

	private Runnable timer = new Runnable() {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(MessageModel.MSG_WIFI_TIMEOUT);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		manager.setOnPipaRequestStatusListener(null);
	}

	@Override
	protected void onDestroy() {
		if (linker != null) {
//			PISManager.cacheMap.put(linker.getPISKeyString(), linker);
		}
		cancelTimer();
		isTimeout = false;
		super.onDestroy();
	}

	/**
	 * 取消定时
	 */
	private void cancelTimer() {
		try {
			mHandler.removeCallbacks(timer);
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		if (netConnStatus == PISConstantDefine.PIPA_NET_STATUS_DISCONNECTED) {
//			progressDialog.dismiss();
//			ToastUtils.showToast(WifiConnectActivity.this, R.string.disconnect);
//		}
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//
//	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (reqType == PISBlueLinker.PIS_MSG_AP_CONFIG_STATE) {
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				PISBlueLinker blinker = (PISBlueLinker) pis;
//				if (blinker.result == 1) {
//					//ssid输入错误
//					if (!isTimeout) {
//						cancelTimer();
//						progressDialog.dismiss();
//						ToastUtils.showToast(WifiConnectActivity.this,
//								R.string.wifi_name_failed);
//					}
//				}else if (blinker.result == 2) {
//					//重置wifi密码
//					if (!isTimeout) {
//						cancelTimer();
//						progressDialog.dismiss();
//						back(true);
//					}
//				}else if (blinker.result == 3) {
//					setErrorTipOnSSIDOrKey();
//				}
//			}
//		}else if (reqType == PISBlueLinker.PIS_CMD_AP_SSID_KEY_SET) {
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				setErrorTipOnSSIDOrKey();
//			}
//		}
//	}
}