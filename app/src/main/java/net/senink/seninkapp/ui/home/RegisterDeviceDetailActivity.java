package net.senink.seninkapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISGWConfig.ClientInfo;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;

public class RegisterDeviceDetailActivity extends BaseActivity implements
		OnClickListener {
	private View backbtn, settingBtn;
//	private PISGWConfig pisgwConfig = null;
	private TextView text_in_title, register_device_detail_name,
			register_device_detail_connected_speed,
			register_device_detail_number, alert_white_black_area_switch_top,
			alert_white_black_area_switch_bottom, device_lebal;
	private ListView device_list;
	private LinearLayout info_area;
	PISManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_device_detail);
		try {
			initView();
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	@SuppressLint("DefaultLocale")
	private void initView() {
		pm = PISManager.getInstance();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);

		backbtn = findViewById(R.id.back);
		settingBtn = findViewById(R.id.home_setting);
		backbtn.setOnClickListener(this);
		settingBtn.setOnClickListener(this);
		// pisgwConfig = (PISGWConfig) getIntent().getSerializableExtra(
		// HomeActivity.VALUE_KEY);
//		pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
		text_in_title = (TextView) findViewById(R.id.text_in_title);
		text_in_title.setText(getResources().getString(R.string.lu_you_detail));
		register_device_detail_name = (TextView) findViewById(R.id.register_device_detail_name);
//		register_device_detail_name.setText(pisgwConfig.get_ssid());
		register_device_detail_connected_speed = (TextView) findViewById(R.id.register_device_detail_connected_speed);
		register_device_detail_number = (TextView) findViewById(R.id.register_device_detail_number);
		alert_white_black_area_switch_top = (TextView) findViewById(R.id.alert_white_black_area_switch_top);
		alert_white_black_area_switch_top.setOnClickListener(this);
		alert_white_black_area_switch_bottom = (TextView) findViewById(R.id.alert_white_black_area_switch_bottom);
		alert_white_black_area_switch_bottom.setOnClickListener(this);
		device_lebal = (TextView) findViewById(R.id.device_lebal);
		info_area = (LinearLayout) findViewById(R.id.info_area);

		device_list = (ListView) findViewById(R.id.device_list);

//		int N = pisgwConfig.getClientInfoList().size();
//		if (N > 0) {
//			String result = String.format("%.2f",
//					pisgwConfig.mDlkbpsTotal / 1024.0f);
//			register_device_detail_connected_speed.setText(result);
//		}
//		register_device_detail_number.setText(String.valueOf(N));
//		if (alert_white_black_area_switch_bottom.getVisibility() == View.VISIBLE) {
//			ConnectedPortlistAdapter connectedPortList = new ConnectedPortlistAdapter(
//					this, pisgwConfig.getClientInfoList());
//			device_list.setAdapter(connectedPortList);
//		} else {
//			ConnectedPortlistAdapter connectedPortList = new ConnectedPortlistAdapter(
//					this, pisgwConfig.getBlackClientInfoList());
//			device_list.setAdapter(connectedPortList);
//		}
		device_list.setOnItemClickListener(mOnItemClickListener);

	}

	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View itemView,
				int position, long id) {
//			ClientInfo clientInfo = (ClientInfo) itemView.getTag();
//			Intent intent = new Intent();
//			intent.setClass(RegisterDeviceDetailActivity.this,
//					ConnectedPortDetailActivity.class);
//			intent.putExtra(HomeActivity.VALUE_KEY, clientInfo);
//			startActivity(intent);
//			overridePendingTransition(0, 0);
		}

	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.home_setting:
//			Intent intent = new Intent().setClass(this, SettingActivity.class);
//			intent.putExtra(HomeActivity.VALUE_KEY,
//					pisgwConfig.getPISKeyString());
//			startActivity(intent);
//			overridePendingTransition(0, 0);
			break;
		case R.id.alert_white_black_area_switch_bottom:
//			ConnectedPortlistAdapter connectedPortList = new ConnectedPortlistAdapter(
//					this, pisgwConfig.getBlackClientInfoList());
//			device_list.setAdapter(connectedPortList);
//			alert_white_black_area_switch_top.setVisibility(View.VISIBLE);
//			alert_white_black_area_switch_bottom.setVisibility(View.GONE);
//			device_lebal.setVisibility(View.INVISIBLE);
//			info_area.setVisibility(View.GONE);
			break;
		case R.id.alert_white_black_area_switch_top:
//			connectedPortList = new ConnectedPortlistAdapter(this,
//					pisgwConfig.getClientInfoList());
//			device_list.setAdapter(connectedPortList);
//			register_device_detail_number.setText(String
//					.valueOf(connectedPortList.getCount()));
//			alert_white_black_area_switch_top.setVisibility(View.GONE);
//			alert_white_black_area_switch_bottom.setVisibility(View.VISIBLE);
//			device_lebal.setVisibility(View.VISIBLE);
//			info_area.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (pisgwConfig != null) {
//			pisgwConfig.getClientList();
//		}
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
//		try {
//			if (pisgwConfig != null
//					&& pis != null
//					&& pisgwConfig.getPISKeyString().equals(
//							pis.getPISKeyString())) {
//				initView();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
//		try {
//			if (pisgwConfig != null
//					&& pis != null
//					&& pisgwConfig.getPISKeyString().equals(
//							pis.getPISKeyString())) {
//				initView();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
//	public void onBackPressed() {
//		if (alert_white_black_area_switch_top.getVisibility() == View.VISIBLE) {
//			ConnectedPortlistAdapter connectedPortList = new ConnectedPortlistAdapter(
//					this, pisgwConfig.getClientInfoList());
//			device_list.setAdapter(connectedPortList);
//			register_device_detail_number.setText(String
//					.valueOf(connectedPortList.getCount()));
//			alert_white_black_area_switch_top.setVisibility(View.GONE);
//			alert_white_black_area_switch_bottom.setVisibility(View.VISIBLE);
//			device_lebal.setVisibility(View.VISIBLE);
//			info_area.setVisibility(View.VISIBLE);
//		} else {
//			super.onBackPressed();
//		}
//	}
}
