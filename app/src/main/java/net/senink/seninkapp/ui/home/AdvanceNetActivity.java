package net.senink.seninkapp.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;

public class AdvanceNetActivity extends BaseActivity implements
		OnClickListener {
	private View backbt, nextBtn;
	private RadioButton dong_tai_ip, static_ip;
	// private int networkType = -1;
	PISManager pm;
	private ProgressDialog progressDialog;
	// private PISDevice pisDevice;
//	private PISGWConfig pisgwConfig = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance_select_net_mode);
		initView();
	}

	private void initView() {
		try {
			dong_tai_ip = (RadioButton) findViewById(R.id.dong_tai_ip);
			static_ip = (RadioButton) findViewById(R.id.static_ip);

			dong_tai_ip.setOnClickListener(this);
			static_ip.setOnClickListener(this);

			backbt = findViewById(R.id.back);
			nextBtn = findViewById(R.id.selecet_working_mode_next);
			backbt.setOnClickListener(this);
			nextBtn.setOnClickListener(this);
			pm = PISManager.getInstance();
//			pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent()
//					.getStringExtra(HomeActivity.VALUE_KEY));
//			// pisDevice = (PISDevice) pm
//			// .getPisByKey(HomeActivity.device_value_key);
//			pm.setOnPISChangedListener(this);
//			pm.setOnPipaRequestStatusListener(this);
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog
					.setMessage(getResources().getString(R.string.saving));
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.dong_tai_ip:
			AdvanceSettingActivity.net_mode = AdvanceSettingActivity.DONG_TAI_IP;
			progressDialog.show();
//			pisgwConfig.setWanInfoWired(PISGWConfig.WAN_IP_GET_TYPE_DHCP,
//					pisgwConfig.mWanIP, pisgwConfig.mWanMask,
//					pisgwConfig.mWanGateway, pisgwConfig.mWanPrimaryDNS,
//					pisgwConfig.mWanSecondDNS);
			break;
		case R.id.static_ip:
			AdvanceSettingActivity.net_mode = AdvanceSettingActivity.STATIC_IP;
			Intent intent = new Intent(this, StaticIpSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			overridePendingTransition(0, 0);
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
//				Log.i("hxj", "result==>" + result);
//				if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					try {
//						progressDialog.dismiss();
//						this.startActivity(new Intent(this,
//								SettingActivity.class));
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
//			Log.i("hxj", "result==>" + result);
//			if (result == PipaRequest.REQUEST_RESULT_ERROR
//					|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//					|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				try {
//					progressDialog.dismiss();
//					finish();
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
