package net.senink.seninkapp.ui.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

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

public class AdvanceSettingActivity extends BaseActivity implements
		OnClickListener {
	private View backbt, more_setting, work_mode_item, net_lianjia_item,
			ju_yu_net_item, wu_xian_item;
	private TextView work_mode, net_lianjia, ju_yu_net, wu_xian;
	public static final int BO_HAO_NET_MODE = 1;
	public static final int WU_XIAN_MODE = 2;
	public static final int YOU_XIAN_MODE = 3;
	public static int mode = BO_HAO_NET_MODE;

	public static final int STATIC_IP = 4;
	public static final int DONG_TAI_IP = 5;
	public static int net_mode = STATIC_IP;

	PISManager pm;
	private ProgressDialog progressDialog;
	private PISDevice pisDevice;
//	private PISGWConfig pisgwConfig = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance_settings_layout);
		initView();
	}

	private void initView() {
		backbt = findViewById(R.id.back);
		// saveBtn = findViewById(R.id.save);
		backbt.setOnClickListener(this);
		// saveBtn.setOnClickListener(this);
		more_setting = findViewById(R.id.more_setting);
		more_setting.setOnClickListener(this);
		work_mode_item = findViewById(R.id.work_mode_item);
		work_mode_item.setOnClickListener(this);
		work_mode = (TextView) findViewById(R.id.work_mode);
		net_lianjia_item = findViewById(R.id.net_lianjia_item);
		net_lianjia_item.setOnClickListener(this);
		ju_yu_net_item = findViewById(R.id.ju_yu_net_item);
		ju_yu_net_item.setOnClickListener(this);
		wu_xian_item = findViewById(R.id.wu_xian_item);
		wu_xian_item.setOnClickListener(this);
		net_lianjia = (TextView) findViewById(R.id.net_lianjia);
		ju_yu_net = (TextView) findViewById(R.id.ju_yu_net);
		wu_xian = (TextView) findViewById(R.id.wu_xian);
		pm = PISManager.getInstance();
//		pisgwConfig = (PISGWConfig) pm.getPisByKey(getIntent().getStringExtra(
//				HomeActivity.VALUE_KEY));
//		pisDevice = (PISDevice) pm.getPisByKey(HomeActivity.device_value_key);
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
//		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);
//		progressDialog.setMessage(getResources().getString(R.string.saving));
//
//		ju_yu_net.setText(pisgwConfig.get_lanIP());
//		wu_xian.setText(pisgwConfig.mSSID);
	}

	private void showMode() {
		String modeDesc = getResources().getString(R.string.no_link);
		if (mode == BO_HAO_NET_MODE) {
			work_mode_item.setVisibility(View.VISIBLE);
			net_lianjia_item.setVisibility(View.GONE);
			ju_yu_net_item.setVisibility(View.VISIBLE);
			wu_xian_item.setVisibility(View.VISIBLE);
			modeDesc = getResources().getString(R.string.dia_net);
		} else if (mode == WU_XIAN_MODE) {
			work_mode_item.setVisibility(View.VISIBLE);
			net_lianjia_item.setVisibility(View.GONE);
			ju_yu_net_item.setVisibility(View.VISIBLE);
			wu_xian_item.setVisibility(View.VISIBLE);
			modeDesc = getResources().getString(R.string.no_link);
		} else if (mode == YOU_XIAN_MODE) {
			work_mode_item.setVisibility(View.VISIBLE);
			net_lianjia_item.setVisibility(View.VISIBLE);
			ju_yu_net_item.setVisibility(View.VISIBLE);
			wu_xian_item.setVisibility(View.VISIBLE);
			modeDesc = getResources().getString(R.string.has_link);
		}
		work_mode.setText(modeDesc);

		if (net_mode == DONG_TAI_IP) {
			net_lianjia.setText(R.string.change_ip);
		} else if (net_mode == STATIC_IP) {
			net_lianjia.setText(R.string.static_ip);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
//			pisgwConfig.getWanInfo();
//			pisgwConfig.getLanInfo();
//			pisgwConfig.getAPMod();
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}

		showMode();

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.save:
			pop.dismiss();
			progressDialog.show();
			pisDevice.reset();
			break;
		case R.id.more_setting:
			popupMenuList(view);
			break;
		case R.id.work_mode_item:
			Intent intent = new Intent(this,
					AdvanceSelectWorkingModeActivity.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			break;
		case R.id.net_lianjia_item:
			intent = new Intent(this, AdvanceNetActivity.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			break;
		case R.id.ju_yu_net_item:
			intent = new Intent(this, LanSetting.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);
			break;
		case R.id.wu_xian_item:
			intent = new Intent(this, WirelessAdvanceSettingActivity.class);
			intent.putExtra(HomeActivity.VALUE_KEY,
					getIntent().getStringExtra(HomeActivity.VALUE_KEY));
			startActivity(intent);

			break;
		case R.id.chong_qi:
			progressDialog.show();
			pisDevice.reboot();
			break;
		case R.id.hui_fu_setting:
			progressDialog.show();
//			pisDevice.factoryReset(true);
			break;
		default:
			break;
		}

	}

	PopupWindow pop;

	@SuppressLint("InflateParams")
	private void popupMenuList(View view) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View popupView = inflater.inflate(R.layout.advance_more_menu, null);
		initMenu(popupView);
		pop = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, false);
		pop.setBackgroundDrawable(new ColorDrawable(this.getResources()
				.getColor(R.color.home_menu_bg)));
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAsDropDown(view);
	}

	private void initMenu(View popupView) {
		popupView.findViewById(R.id.save).setOnClickListener(this);
		popupView.findViewById(R.id.chong_qi).setOnClickListener(this);
		popupView.findViewById(R.id.hui_fu_setting).setOnClickListener(this);

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
//			case PISDevice.PIS_CMD_DEVICE_REBOOT:
//			case PISDevice.PIS_CMD_DEVICE_FACTORYRESET:
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
//
//	public void onGWConfigRequestResult(PISGWConfig pis, int reqType, int result) {
//		// 得到区别PISBase对象唯一的标识，可以用来匹配相应的服务对象关联的UI组件，
//		// PISManager中有getPisByKey可以通过KeyString直接得到服务对象
//		switch (reqType) {
//		case PISGWConfig.PIS_CMD_CONFIG_GET_WAN:
//		case PISGWConfig.PIS_CMD_CONFIG_GET_LAN:
//		case PISGWConfig.PIS_CMD_CONFIG_GET_APMODE:
//			pisgwConfig = pis;
//			initView();
//			break;
//		}
//
//	}
}