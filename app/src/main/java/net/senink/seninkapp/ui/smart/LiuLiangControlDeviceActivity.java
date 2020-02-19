package net.senink.seninkapp.ui.smart;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISFamilyControl;
//import com.senink.seninkapp.core.PISFlowControl;
//import com.senink.seninkapp.core.PISFlowControl.UserInfo;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISGWConfig.ClientInfo;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.home.HomeActivity;

public class LiuLiangControlDeviceActivity extends BaseActivity implements
		OnClickListener {
	private View backbt;
	private ListView kuanDaiDeviceList;
	private ProgressDialog progressDialog;
//	PISFamilyControl familyControl;
//	PISManager pm;
//	PISSmartCell smartItem;
//	DeviceAdapter adapter;
//	PISGWConfig pig;
//	PISFlowControl pisFlowControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lin_liang_control_device_layout);
		initView();

	}

	private void initView() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.loading));
		progressDialog.show();
//		pm = PISManager.getInstance();
//		smartItem = (PISSmartCell) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pig = smartItem.getPisGwConfigObject();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		if (smartItem.getSmartCellChildObject() instanceof PISFlowControl) {
//			pisFlowControl = (PISFlowControl) smartItem
//					.getSmartCellChildObject();
//			pisFlowControl.requestGetUserLimitList();
//		}

		backbt = findViewById(R.id.back);
		kuanDaiDeviceList = (ListView) findViewById(R.id.liu_liang_control);
		backbt.setOnClickListener(this);

		kuanDaiDeviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(new Intent(LiuLiangControlDeviceActivity.this,
						LiuLiangSettingActivity.class));

			}

		});
	}

//	private class DeviceAdapter extends BaseAdapter {
//		private ArrayList<UserInfo> list;
//
//		public DeviceAdapter(ArrayList<UserInfo> list) {
//			this.list = list;
//		}
//
//		@Override
//		public int getCount() {
//			return list.size();
//		}
//
//		@Override
//		public UserInfo getItem(int arg0) {
//			return list.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return 0;
//		}
//
//		@SuppressLint("InflateParams")
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			TextView speed = null;
//			TextView device_name = null;
//			UserInfo info = getItem(position);
//			if (convertView == null) {
//				convertView = LayoutInflater.from(
//						LiuLiangControlDeviceActivity.this).inflate(
//						R.layout.liu_liang_control_item, null);
//			}
//			speed = (TextView) convertView.findViewById(R.id.speed);
//			speed.setText(info.getFlowLimit()
//					+ getResources().getString(R.string.total_data_unit));
//			device_name = (TextView) convertView.findViewById(R.id.name);
//			device_name.setText(info.getName());
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
//		if (pis == null) {
//			return;
//		}
//
//		if (pis instanceof PISSmartCell) {
//			switch (reqType) {
//			case PISFlowControl.PIS_CMD_SMART_FLCTRL_GET_USRLIST:
//				smartItem = (PISSmartCell) pis;
//				if (smartItem.getSmartCellChildObject() instanceof PISFlowControl) {
//					pisFlowControl = (PISFlowControl) smartItem
//							.getSmartCellChildObject();
//					if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//						pig.getClientList();
//					}
//				}
//				break;
//			}
//		} else if (pis instanceof PISGWConfig) {
//			switch (reqType) {
//			case PISGWConfig.PIS_CMD_CONFIG_GET_CLILIST:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					pig = (PISGWConfig) pis;
//					ArrayList<UserInfo> uList = pisFlowControl
//							.getUserLimitList();
//					ArrayList<ClientInfo> list = pig.getClientInfoList();
//
//					int N = uList.size();
//					int N1 = list.size();
//					for (int i = 0; i < N1; i++) {
//						ClientInfo info = list.get(i);
//						boolean flag = false;
//						for (int j = 0; j < N; j++) {
//							UserInfo userInfo = uList.get(j);
//							if (info.getMac().equals(userInfo.getMac())) {
//								flag = true;
//								break;
//							}
//						}
//						if (!flag) {
//							UserInfo userInfo = new UserInfo();
//							userInfo._name = info._name;
//							userInfo._mac = info._mac;
//							userInfo.setFlowLimit(0);
//							uList.add(userInfo);
//						}
//					}
//
//					adapter = new DeviceAdapter(uList);
//					kuanDaiDeviceList.setAdapter(adapter);
//
//				}
//				if (progressDialog != null && progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
//				break;
//
//			default:
//				break;
//			}
//
//		}
//		if (result == PipaRequest.REQUEST_RESULT_ERROR
//				|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//			try {
//				progressDialog.dismiss();
//			} catch (Exception e) {
//				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
//			}
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
