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
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinCenter;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISBlueLinker;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.Utils;

/**
 * 用于连接其他wifi
 * 
 * @author zhaojunfeng
 * @date 2015-08-25
 */
public class WifiOtherConnectActivity extends BaseActivity implements
		View.OnClickListener {
	// 标题名称
	private TextView tvTitle;
	// 标题的中的取消按钮
	private Button cancelBtn;
	// 标题中的添加按钮
	private Button addBtn;
	// 输入密码框
	private EditText etPwd;
	// 输入ssid框
	private EditText etSSID;
	// 网关类
	private PISXinCenter linker;

	private ProgressDialog dialog;
	// 请求是否超时
	private boolean isTimeout = false;
	private PISManager manager;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageModel.MSG_WIFI_TIMEOUT:
				isTimeout = true;
				dialog.dismiss();
				Toast.makeText(WifiOtherConnectActivity.this,
						R.string.wifi_input_failed, Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifiother_connect);
		manager = PISManager.getInstance();
		initView();
		initDialog();
		setData();
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
		WifiOtherConnectActivity.this.finish();
		WifiOtherConnectActivity.this.overridePendingTransition(
				R.anim.anim_in_from_left, R.anim.anim_out_to_right);
	}

	/**
	 * 初始化加载框
	 */
	private void initDialog() {
		dialog = Utils.createProgressDialog(WifiOtherConnectActivity.this,
				R.string.wifi_loading);
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
		}
	}

	/**
	 * 设置添加按钮是否可用
	 * 
	 * @param enable
	 */
	private void setAddBtn(boolean enable) {
		if (enable) {
			String ssid = etSSID.getText().toString();
			String key = etPwd.getText().toString();
			if (ssid != null && key != null) {
				addBtn.setEnabled(enable);
				addBtn.setTextColor(Color.WHITE);
			}
		} else {
			addBtn.setTextColor(0xff5da2ec);
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
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.wifi_title_name);
		addBtn = (Button) findViewById(R.id.wifi_title_add);
		cancelBtn = (Button) findViewById(R.id.wifi_title_cancel);
		etSSID = (EditText) findViewById(R.id.wifi_other_connect_ssid);
		etPwd = (EditText) findViewById(R.id.wifi_other_connect_pwd);
		tvTitle.setText(R.string.wifi);
		setAddBtn(false);
	}

	@Override
	public void onBackPressed() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		} else {
			cancelBtn.performClick();
		}
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
			if (linker != null) {
				String pwd = etPwd.getText().toString();
				String ssid = etSSID.getText().toString();
				if (pwd != null) {
					pwd = pwd.trim();
				}
				if (ssid != null) {
					ssid = ssid.trim();
				}
				if (pwd != null && !"".equals(pwd) && ssid != null
						&& !"".equals(ssid)) {
					mHandler.postDelayed(timer, MessageModel.MAX_TIME);
//					linker.setSSIDKEY(ssid, pwd, true);
					dialog.show();
				} else {
					Toast.makeText(this, R.string.input_not_null,
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, R.string.input_not_null, Toast.LENGTH_LONG)
						.show();
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
		isTimeout = false;
		if (linker != null) {
//			PISManager.cacheMap.put(linker.getPISKeyString(), linker);
		}
		cancelTimer();
		super.onDestroy();
	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		if (netConnStatus == PISConstantDefine.PIPA_NET_STATUS_DISCONNECTED) {
//			dialog.dismiss();
//			Toast.makeText(WifiOtherConnectActivity.this, R.string.disconnect,
//					Toast.LENGTH_SHORT).show();
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
//						dialog.dismiss();
//						Toast.makeText(WifiOtherConnectActivity.this,
//								R.string.wifi_name_failed, Toast.LENGTH_LONG)
//								.show();
//					}
//				}else if (blinker.result == 2) {
//					//重置wifi密码
//					if (!isTimeout) {
//						cancelTimer();
//						dialog.dismiss();
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
	
	/**
	 * 设置ssid和key输入错误的提示
	 */
	private void setErrorTipOnSSIDOrKey() {
		if (!isTimeout) {
			cancelTimer();
			dialog.dismiss();
			Toast.makeText(WifiOtherConnectActivity.this,
					R.string.wifi_input_failed, Toast.LENGTH_LONG)
					.show();
		}
	}
}