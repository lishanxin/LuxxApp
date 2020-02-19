package net.senink.seninkapp.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;

public class StaticIpSetting extends BaseActivity implements OnClickListener {
	private View backbt, saveBtn;
//	private PISGWConfig pisgwConfig = null;
	private EditText ip_address, yan_ma, wang_guan_ip, dns, bei_yong_ip;
	PISManager pm;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.static_ip_setting);
		initView();
	}

	private void initView() {
		ip_address = (EditText) findViewById(R.id.ip_address);
		yan_ma = (EditText) findViewById(R.id.yan_ma);
		wang_guan_ip = (EditText) findViewById(R.id.wang_guan_ip);
		dns = (EditText) findViewById(R.id.dns);
		bei_yong_ip = (EditText) findViewById(R.id.bei_yong_ip);

		backbt = findViewById(R.id.back);
		saveBtn = findViewById(R.id.save);
		backbt.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		pm = PISManager.getInstance();
//		pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
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
//			progressDialog.show();
//			pisgwConfig.setWanInfoWired(PISGWConfig.WAN_IP_GET_TYPE_STATIC,
//					String.valueOf(ip_address.getText()),
//					String.valueOf(yan_ma.getText()),
//					String.valueOf(wang_guan_ip.getText()),
//					String.valueOf(dns.getText()),
//					String.valueOf(bei_yong_ip.getText()));
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
//		switch (pis.mPisType) {
//		case PISConstantDefine.PIS_TYPE_DEVICE:
//
//			break;
//		case PISConstantDefine.PIS_TYPE_UPDATE:
//
//			break;
//		case PISConstantDefine.PIS_TYPE_MCM:
//
//			break;
//		case PISConstantDefine.PIS_TYPE_GW_CONFIG:
//				onGWConfigRequestResult((PISGWConfig) pis, reqType, result);
//			break;
//		case PISConstantDefine.PIS_TYPE_SMART_CELL:
//
//			break;
//		case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_ENABLE:
//
//			break;
//		case PISConstantDefine.PIS_TYPE_SWITCH_LIGHT_DISABLE:
//
//			break;
//		case PISConstantDefine.PIS_TYPE_UNKNOW:
//
//			break;
//
//		default:
//			break;
//		}
//		}else if (pis instanceof PISDevice) {
//			switch (reqType) {
//			case PISDevice.PIS_CMD_DEVICE_RESET:
//				Log.i("hxj", "result==>" + result);
//				if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					try {
//						progressDialog.dismiss();
//						this.startActivity(new Intent(this, SettingActivity.class));
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
