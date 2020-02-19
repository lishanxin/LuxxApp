package net.senink.seninkapp.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.activity.MipcaCaptureActivity;

public class InternetAccountSetting extends BaseActivity implements
		OnClickListener {
	private View backbt, saveBtn;
//	private PISGWConfig pisgwConfig = null;
	private EditText accu, password, mac_address;
	PISManager pm;
	private CheckBox mac_ke_long;
	private ProgressDialog progressDialog;
	private PISDevice pisDevice;
	private boolean isAddDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_account_setting);
		isAddDevice = getIntent().getBooleanExtra("isAddDevice", false);
		initView();
	}

	private void initView() {
		accu = (EditText) findViewById(R.id.accu);
		password = (EditText) findViewById(R.id.password);
		mac_address = (EditText) findViewById(R.id.mac_address);
		mac_ke_long = (CheckBox) findViewById(R.id.mac_ke_long);
		mac_ke_long.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mac_address.setVisibility(View.VISIBLE);
				} else {
					mac_address.setVisibility(View.GONE);
				}
			}
		});
		backbt = findViewById(R.id.back);
		saveBtn = findViewById(R.id.save);
		backbt.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		pm = PISManager.getInstance();
//		pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pisDevice = (PISDevice) pm.getPisByKey(HomeActivity.device_value_key);
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.saving));

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.save:
			String macAddress = null;
//			String onOrOff = PISGWConfig.WAN_MAC_CLONE_OFF;
//			if (mac_ke_long.isChecked()) {
//				macAddress = String.valueOf(mac_address.getText());
//				onOrOff = PISGWConfig.WAN_MAC_CLONE_ON;
//			}
//			pisgwConfig.setWanInfoPppoe(String.valueOf(accu.getText()),
//					String.valueOf(password.getText()), onOrOff, macAddress);
			progressDialog.show();
			break;
		default:
			break;
		}

	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis == null) {
//			return;
//		}
//		if (pis instanceof PISGWConfig) {
//			switch (pis.mPisType) {
//			case PISConstantDefine.PIS_TYPE_DEVICE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_UPDATE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_MCM:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_GW_CONFIG:
//				onGWConfigRequestResult((PISGWConfig) pis, reqType, result);
//				break;
//			case PISConstantDefine.PIS_TYPE_SMART_CELL:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_ENABLE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_DISABLE:
//
//				break;
//			case PISConstantDefine.PIS_TYPE_UNKNOW:
//
//				break;
//
//			default:
//				break;
//			}
//		} else if (pis instanceof PISDevice) {
//			switch (reqType) {
//			case PISDevice.PIS_CMD_DEVICE_RESET:
//				if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					try {
//						progressDialog.dismiss();
//						if (isAddDevice) {
//							Intent intent = new Intent(this,
//									MipcaCaptureActivity.class);
//							intent.putExtra("isQuickSetting", true);
//							intent.putExtra("result",
//									PipaRequest.REQUEST_RESULT_COMPLETE);
//							startActivity(intent);
//						} else {
//							this.startActivity(new Intent(this,
//									SettingActivity.class));
//						}
//						overridePendingTransition(0, 0);
//						finish();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					finish();
//				}
//				break;
//			}
//		}
//
//	}
//
//	public void onGWConfigRequestResult(PISGWConfig pis, int reqType, int result) {
//		// 得到区别PISBase对象唯一的标识，可以用来匹配相应的服务对象关联的UI组件，
//		// PISManager中有getPisByKey可以通过KeyString直接得到服务对象
//		switch (reqType) {
//		case PISGWConfig.PIS_CMD_CONFIG_SET_WAN:
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//				pisgwConfig.setAPModStandard();
//			} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//					|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//				try {
//					progressDialog.dismiss();
//					this.startActivity(new Intent(this, SettingActivity.class));
//					overridePendingTransition(0, 0);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				finish();
//			}
//			break;
//		case PISGWConfig.PIS_CMD_CONFIG_SET_APMODE:
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//				if (getIntent()
//						.getStringExtra(
//								SelectWorkingModeActivity.KUAI_OR_GAO_JI_SETTING_SWICTH) != null) {
//					pisDevice.reset();
//				} else {
//					try {
//						progressDialog.dismiss();
//						this.startActivity(new Intent(this,
//								SettingActivity.class));
//						overridePendingTransition(0, 0);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					finish();
//				}
//
//			} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//					|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//				try {
//					progressDialog.dismiss();
//					this.startActivity(new Intent(this, SettingActivity.class));
//					overridePendingTransition(0, 0);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				finish();
//			}
//			break;
//		case PISDevice.PIS_CMD_DEVICE_RESET:
//			Log.i("hxj", "result==>" + result);
//			if (result == PipaRequest.REQUEST_RESULT_ERROR
//					|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//					|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				try {
//					progressDialog.dismiss();
//					this.startActivity(new Intent(this, SettingActivity.class));
//					overridePendingTransition(0, 0);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				finish();
//			}
//			break;
//		}
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// TODO Auto-generated method stub
//
//	}
}
