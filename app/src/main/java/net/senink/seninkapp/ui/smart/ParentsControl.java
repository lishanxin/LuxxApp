package net.senink.seninkapp.ui.smart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISFamilyControl;
//import com.senink.seninkapp.core.PISFamilyControl.DeviceInfo;
//import com.senink.seninkapp.core.PISFamilyControl.TimePeriodInfo;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.home.HomeActivity;

public class ParentsControl extends BaseActivity implements OnClickListener {
	private View backbt, addTimer, addDevice;;
	private LinearLayout parents_control_timer_list,
			parents_control_device_list;
//	PISFamilyControl familyControl;
//	PISManager pm;
//	PISSmartCell smartItem;
	public static int curClickPosition = -1;
	private TextView parents_control_duration_total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parents_control_layout);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
//		if (familyControl != null) {
//			familyControl.getInfo();
//		}

	}

	LayoutInflater inflater;

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void initView() {
		parents_control_duration_total = (TextView) findViewById(R.id.parents_control_duration_total);
		backbt = findViewById(R.id.back);
		addTimer = findViewById(R.id.parents_control_add_timer);
		addDevice = findViewById(R.id.parents_control_add_device);
		addDevice.setOnClickListener(this);
		addTimer.setOnClickListener(this);
		backbt.setOnClickListener(this);
		parents_control_timer_list = (LinearLayout) findViewById(R.id.parents_control_timer_list);
		parents_control_timer_list.removeAllViews();
		parents_control_device_list = (LinearLayout) findViewById(R.id.parents_control_device_list);
		parents_control_device_list.removeAllViews();
		inflater = LayoutInflater.from(this);
//		pm = PISManager.getInstance();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		smartItem = (PISSmartCell) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		if (smartItem.getSmartCellChildObject() instanceof PISFamilyControl) {
//			familyControl = (PISFamilyControl) smartItem
//					.getSmartCellChildObject();
//			int N = familyControl.getTimePeriodList().size();
//			for (int i = 0; i < N; i++) {
//				TimePeriodInfo timeInfo = familyControl.getTimePeriodList()
//						.get(i);
//				View view = inflater.inflate(R.layout.parent_control_time_item,
//						null);
//				view.setTag(timeInfo);
//				view.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						curClickPosition = parents_control_timer_list
//								.indexOfChild(v);
//						Intent intent = new Intent(ParentsControl.this,
//								AddTimer.class);
//						intent.putExtra(HomeActivity.VALUE_KEY,
//								smartItem.getPISKeyString());
//						startActivity(intent);
//						overridePendingTransition(0, 0);
//					}
//				});
//				view.setOnLongClickListener(new OnLongClickListener() {
//
//					@Override
//					public boolean onLongClick(View v) {
//						deleteAlert((TimePeriodInfo) v.getTag());
//						return true;
//					}
//				});
//				TextView timeTv = (TextView) view.findViewById(R.id.time);
//				timeTv.setText(timeInfo.getStartTime() + "--"
//						+ timeInfo.getEndTime());
//				LinearLayout weekGroup = (LinearLayout) view
//						.findViewById(R.id.week_group);
//				parents_control_timer_list.addView(view);
//				for (int j = 0; j < weekGroup.getChildCount(); j++) {
//					if (timeInfo.getRepeatDay() != null
//							&& timeInfo.getRepeatDay().contains(
//									String.valueOf(j))) {
//						weekGroup.getChildAt(j).setBackgroundColor(Color.GREEN);
//					} else {
//						weekGroup.getChildAt(j).setBackgroundDrawable(null);
//					}
//				}
//			}
//
//			int N1 = familyControl.getDeviceInfoList().size();
//			for (int i = 0; i < N1; i++) {
//				DeviceInfo deviceInfo = familyControl.getDeviceInfoList()
//						.get(i);
//				View view = inflater.inflate(
//						R.layout.add_smart_device_list_item, null);
//				view.setTag(deviceInfo);
//				view.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						curClickPosition = parents_control_device_list
//								.indexOfChild(v);
//						Intent intent = new Intent(ParentsControl.this,
//								AddDevice.class);
//						intent.putExtra(HomeActivity.VALUE_KEY,
//								smartItem.getPISKeyString());
//						startActivity(intent);
//						overridePendingTransition(0, 0);
//					}
//				});
//				view.setOnLongClickListener(new OnLongClickListener() {
//
//					@Override
//					public boolean onLongClick(View v) {
//						deleteAlert((DeviceInfo) v.getTag());
//						return true;
//					}
//				});
//				TextView device_name = (TextView) view
//						.findViewById(R.id.device_name);
//				device_name.setText(deviceInfo.getName());
//				ImageView right = (ImageView) view.findViewById(R.id.right);
//				right.setImageResource(R.drawable.icon_arrow);
//				parents_control_device_list.addView(view);
//			}
//			parents_control_duration_total.setText(getResources().getString(
//					R.string.parents_control_duration_total)
//					+ familyControl.getTotalTime()+getResources().getString(R.string.min_lebal) );
//		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.parents_control_add_timer:
//			curClickPosition = -1;
//			Intent intent = new Intent(this, AddTimer.class);
//			intent.putExtra(HomeActivity.VALUE_KEY, smartItem.getPISKeyString());
//			startActivity(intent);
//			overridePendingTransition(0, 0);
			break;
		case R.id.parents_control_add_device:
//			intent = new Intent(this, AddDevice.class);
//			intent.putExtra(HomeActivity.VALUE_KEY, smartItem.getPISKeyString());
//			startActivity(intent);
//			overridePendingTransition(0, 0);
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
//				case PISFamilyControl.PIS_CMD_SMART_DEL_TIME_PERIOD:
//				case PISFamilyControl.PIS_CMD_SMART_DEL_LIST:
//					if (familyControl != null) {
//						familyControl.getInfo();
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
//		initView();
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
//	private void deleteAlert(final Object obj) {
//
//		AlertDialog.Builder builder = new Builder(this);
//		builder.setMessage(getResources().getString(R.string.del_device_alert));
//		builder.setTitle(getResources().getString(R.string.tip));
//		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (obj instanceof TimePeriodInfo) {
//					familyControl.requestDelTimePeriod((TimePeriodInfo) obj);
//				} else if (obj instanceof DeviceInfo) {
//					familyControl
//							.requestDelDeviceFromList(((DeviceInfo) obj)._mac);
//				}
//			}
//		});
//
//		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//			}
//		});
//		builder.create().show();
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// TODO Auto-generated method stub
//
//	}
}