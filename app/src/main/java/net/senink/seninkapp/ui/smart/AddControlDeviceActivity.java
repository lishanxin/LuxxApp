package net.senink.seninkapp.ui.smart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISFamilyControl;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.home.HomeActivity;

public class AddControlDeviceActivity extends BaseActivity implements OnClickListener {
	private View backbt;
	private ListView deviceList;
	private ProgressDialog progressDialog;
//	PISFamilyControl familyControl;
	PISManager pm;
//	PISSmartCell smartItem;
	DeviceAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_device_layout);
		initView();

	}

	private void initView() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(R.string.saving));

		pm = PISManager.getInstance();
//		smartItem = (PISSmartCell) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		if (smartItem.getSmartCellChildObject() instanceof PISFamilyControl) {
//			familyControl = (PISFamilyControl) smartItem
//					.getSmartCellChildObject();
//		}

		backbt = findViewById(R.id.back);
		deviceList = (ListView) findViewById(R.id.device_list_for_parents_control);
		backbt.setOnClickListener(this);
		adapter=new DeviceAdapter();
		deviceList.setAdapter(adapter);

		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}

		});
	}

	private class DeviceAdapter extends BaseAdapter {

		public DeviceAdapter() {
		}

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView label = null;
			TextView device_name = null;
			ImageView right = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(AddControlDeviceActivity.this).inflate(
						R.layout.add_smart_device_list_item, null);
			}
			label = (TextView) convertView.findViewById(R.id.label);
			label.setVisibility(View.GONE);
			device_name = (TextView) convertView.findViewById(R.id.device_name);
			right = (ImageView) convertView.findViewById(R.id.right);
			return convertView;
		}

	}

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
//
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
//				break;
//			case PISConstantDefine.PIS_TYPE_SMART_CELL:
//				switch (reqType) {
//				case PISFamilyControl.PIS_CMD_SMART_ADD_TIME_PERIOD:
//					if (result == PipaRequest.REQUEST_RESULT_ERROR
//							|| result == PipaRequest.REQUEST_RESULT_TIMEOUT
//							|| result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//						try {
//							progressDialog.dismiss();
//						} catch (Exception e) {
//							net.senink.seninkapp
//						}
//						finish();
//					}
//					break;
//				}
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
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// TODO Auto-generated method stub
//
//	}
}
