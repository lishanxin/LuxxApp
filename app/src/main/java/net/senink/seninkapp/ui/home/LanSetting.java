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

public class LanSetting extends BaseActivity implements OnClickListener {
	private View backbt, saveBtn;
//	private PISGWConfig pisgwConfig = null;
	private EditText ip_address, yan_ma, main_dns, backup_dns, start_adress,
			end_adress, during;
	PISManager pm;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lan_setting);
		initView();
	}

	private void initView() {
		ip_address = (EditText) findViewById(R.id.ip_address);
		yan_ma = (EditText) findViewById(R.id.yan_ma);

		main_dns = (EditText) findViewById(R.id.main_dns);

		backup_dns = (EditText) findViewById(R.id.backup_dns);

		start_adress = (EditText) findViewById(R.id.start_adress);

		end_adress = (EditText) findViewById(R.id.end_adress);
		during = (EditText) findViewById(R.id.during);

		backbt = findViewById(R.id.back);
		saveBtn = findViewById(R.id.save);
		backbt.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		pm = PISManager.getInstance();
		String key = getIntent().getStringExtra(HomeActivity.VALUE_KEY);
//		pisgwConfig = (PISGWConfig) pm.getPisByKey(key);
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.saving));
		initData();

	}

	private void initData() {
//		ip_address.setText(pisgwConfig.mLanIP);
//		yan_ma.setText(pisgwConfig.mLanMask);
//
//		main_dns.setText(pisgwConfig.mLanDHCPPrimaryDNS);
//
//		backup_dns.setText(pisgwConfig.mLanDHCPSecondDNS);
//
//		start_adress.setText(pisgwConfig.mLanDHCPStartIP);
//
//		end_adress.setText(pisgwConfig.mLanDHCPEndIP);
//		during.setText(pisgwConfig.mLanDhcpLease);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.save:
//			boolean result = pisgwConfig.setLanInfo(
//					String.valueOf(ip_address.getText()),
//					String.valueOf(yan_ma.getText()),
//					String.valueOf(start_adress.getText()),
//					String.valueOf(end_adress.getText()),
//					String.valueOf(yan_ma.getText()),
//					String.valueOf(main_dns.getText()),
//					String.valueOf(backup_dns.getText()),
//					String.valueOf(ip_address.getText()),
//					String.valueOf(during.getText()));
//			if (result) {
//				progressDialog.show();
//			}
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
//		case PISGWConfig.PIS_CMD_CONFIG_SET_LAN:
//			Log.i("hxj", "result==>" + result);
//			if (result == PipaRequest.REQUEST_RESULT_ERROR
//					|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//					|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				try {
//					progressDialog.dismiss();
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
