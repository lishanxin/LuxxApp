package net.senink.seninkapp.ui.smart;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBandwidthControl;
//import com.senink.seninkapp.core.PISBandwidthControl.UserInfo;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISFamilyControl;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISGWConfig.NetBWGroup;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.home.HomeActivity;

public class KuanDaiControlDeviceActivity extends BaseActivity implements
		OnClickListener {
	private View backbt;
	private ListView kuanDaiDeviceList;
	private ProgressDialog progressDialog;
//	PISBandwidthControl pisBandwidthControl;
	PISManager pm;
//	PISSmartCell smartItem;
//	DeviceAdapter adapter;
//	PISGWConfig pig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kuan_dai_control_device_layout);
		initView();

	}

	private void initView() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.saving));

		pm = PISManager.getInstance();
//		smartItem = (PISSmartCell) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pig = smartItem.getPisGwConfigObject();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		if (smartItem.getSmartCellChildObject() instanceof PISBandwidthControl) {
//			pisBandwidthControl = (PISBandwidthControl) smartItem
//					.getSmartCellChildObject();
//			pisBandwidthControl.requestGetUserLimitList();
//		}

		backbt = findViewById(R.id.back);
		kuanDaiDeviceList = (ListView) findViewById(R.id.kuan_dai_control);
		backbt.setOnClickListener(this);
		// adapter = new DeviceAdapter();
//		kuanDaiDeviceList.setAdapter(adapter);

		kuanDaiDeviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}

		});
	}

//	private class DeviceAdapter extends BaseAdapter {
//		private ArrayList<UserInfo> userInfos;
//		PISGWConfig pisgwConfig;
//
//		public DeviceAdapter(PISGWConfig pisgwConfig) {
//			userInfos = pisBandwidthControl.getUserLimitList();
//			this.pisgwConfig = pisgwConfig;
//			// ArrayList<ClientInfo> clientInfos =
//			// pisgwConfig.getClientInfoList();
//			// for (int i = 0; i < userInfos.size(); i++) {
//			// for (int j = 0; j < clientInfos.size(); j++) {
//			// if (clientInfos.get(j).getMac()
//			// .equals(userInfos.get(i).getMac())) {
//			// userInfos.get(i).name = clientInfos.get(j).getName();
//			// break;
//			// }
//			// }
//			// }
//
//		}
//
//		@Override
//		public int getCount() {
//			return userInfos.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return userInfos.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return 0;
//		}
//
//		@SuppressLint({ "InflateParams", "DefaultLocale" })
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			UserInfo userInfo = (UserInfo) getItem(position);
//			TextView speed = null;
//			TextView device_name = null;
//			SeekBar seekBar = null;
//			if (convertView == null) {
//				convertView = LayoutInflater.from(
//						KuanDaiControlDeviceActivity.this).inflate(
//						R.layout.kuan_dai_control_item, null);
//			}
//			speed = (TextView) convertView.findViewById(R.id.speed);
//			NetBWGroup nb = pisgwConfig.get_NetBWGList().get(
//					userInfo.getGroupId()-1);
//			if (nb != null) {
//				String result = String.format("%.2f",
//						(nb.downloadSpeedMax + nb.uploadSpeedMax) / 1024.0f);
//				speed.setText(result
//						+ getResources().getString(R.string.total_data_unit)
//						+ " kbps");
//			} else {
//				speed.setText("0 kbps");
//			}
//
//			device_name = (TextView) convertView.findViewById(R.id.name);
//			device_name.setText(userInfo.getName());
//			seekBar = (SeekBar) convertView.findViewById(R.id.seekbar);
//			seekBar.setProgress(userInfo.getGroupId());
//			seekBar.setTag(userInfo);
//			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//				@Override
//				public void onStopTrackingTouch(SeekBar seekBar) {
//					UserInfo info = (UserInfo) seekBar.getTag();
//					info.setGroupId(seekBar.getProgress());
//					pisBandwidthControl.requestAddUserLimit(info);
//				}
//
//				@Override
//				public void onStartTrackingTouch(SeekBar seekBar) {
//
//				}
//
//				@Override
//				public void onProgressChanged(SeekBar seekBar, int progress,
//						boolean fromUser) {
//
//				}
//			});
//			return convertView;
//		}
//
//	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}

	}

//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//
//		if (pis == null) {
//			return;
//		}
//		if (pis instanceof PISSmartCell) {
//			switch (reqType) {
//			case PISFamilyControl.PIS_CMD_SMART_ADD_TIME_PERIOD:
//				if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					try {
//						progressDialog.dismiss();
//					} catch (Exception e) {
//						PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
//					}
//					finish();
//				}
//				break;
//			case PISBandwidthControl.PIS_CMD_SMART_BWCTRL_GET_USRLIST:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					// adapter = new DeviceAdapter((PISGWConfig) pis);
//					// kuanDaiDeviceList.setAdapter(adapter);
//					pig.getFlowCtrGroup();
//
//				}
//				break;
//			case PISBandwidthControl.PIS_CMD_SMART_BWCTRL_ADD_USR:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					pisBandwidthControl.requestGetUserLimitList();
//				}
//				break;
//			}
//		} else if (pis instanceof PISGWConfig) {
//			switch (reqType) {
//			case PISGWConfig.PIS_CMD_CONFIG_GET_NETBWGRP:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					adapter = new DeviceAdapter((PISGWConfig) pis);
//					kuanDaiDeviceList.setAdapter(adapter);
//					// pig.get_NetBWGList();
//					pig = (PISGWConfig) pis;
//
//				}
//				break;
//
//			default:
//				break;
//			}
//
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
