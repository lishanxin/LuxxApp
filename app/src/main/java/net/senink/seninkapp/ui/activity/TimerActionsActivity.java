package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.TimerActionsAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SortUtils;

/**
 * 智能控制之设备与动作界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-06
 */

public class TimerActionsActivity extends BaseActivity implements
		View.OnClickListener {
	private static final String TAG = "TimerActionsActivity";
	// 刷新界面
	private final static int MSG_REFRESH_VIEW = 10;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	private GridView mGridView;
	// 定时器的对象
//	private PISSmartCell infor;
	private PISManager manager;
	private TimerActionsAdapter mAdapter;
	private short cmd;
	private String mac;
	private short servId;
	private byte[] cmdContent;
	private String deviceName;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_VIEW:
				refreshViews();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeractions);
		manager = PISManager.getInstance();
		initView();
		setData();
		setListener();
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null) {
			Intent intent = getIntent();
			String key = intent.getStringExtra(MessageModel.ACTIVITY_VALUE);
			try {
//				infor = (PISSmartCell) PISManager.cacheMap.get(key);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PISBase base = mAdapter.getItem(position);
				Intent it = null;
				deviceName = base.getName();
				if ((base.ServiceType != PISBase.SERVICE_TYPE_GROUP)
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
					it = new Intent();
					it.setClass(TimerActionsActivity.this,
							LightLEDDetailActivity.class);
					it.putExtra(MessageModel.ACTIVITY_VALUE,
							base.getPISKeyString());
//					PISManager.cacheMap.put(base.getPISKeyString(), base);
				} else if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_COLOR) {
					it = new Intent();
					it.setClass(TimerActionsActivity.this,
							LightRGBDetailActivity.class);
					it.putExtra(MessageModel.ACTIVITY_VALUE,
							base.getPISKeyString());
//					PISManager.cacheMap.put(base.getPISKeyString(), base);
				} else if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_COLOR) {
					it = new Intent();
					it.setClass(TimerActionsActivity.this,
							LightRGBDetailActivity.class);
					it.putExtra("isgroup", true);
//					it.putExtra(MessageModel.ACTIVITY_VALUE,
//							((PisDeviceGroup)base).groupId);
				} else if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
					it = new Intent();
					it.setClass(TimerActionsActivity.this,
							LightLEDDetailActivity.class);
					it.putExtra("isgroup", true);
					it.putExtra(MessageModel.ACTIVITY_VALUE,base.getPISKeyString());
				} else if (base.getT1() == PISConstantDefine.PIS_MAJOR_SYSTEM
						&& base.getT2() == PISConstantDefine.PIS_SYSTEM_SMARTCELL) {
//					String guid = ((PISSmartCell) base).getGUID();
//					if (PISSmartCell.GUID_PARENT_CONTROL.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_FLOW_CONTROL.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_BANDWIDTH_CONTROL.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_VIDEO_SPEEDUP.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_GAME_SPEEDUP.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_AD_INTERCEPT.equals(guid)) {
//					} else if (PISSmartCell.GUID_TIMER_GWCONFIG.equals(guid)
//							|| PISSmartCell.GUID_TIMER_SWITCH.equals(guid)) {
//						it = new Intent();
//						PISManager.cacheMap.put(base.getPISKeyString(), base);
//						it.putExtra(MessageModel.ACTIVITY_VALUE,
//								base.getPISKeyString());
//						it.setClass(TimerActionsActivity.this,
//								TimerListActivity.class);
//					}
				} else if (base.getT1() == PISConstantDefine.PIS_MAJOR_ELECTRICIAN
						&& (base.getT2() == PISConstantDefine.PIS_ELECTRICIAN_NORMAL ||
						base.getT2() == PISConstantDefine.PIS_ELECTRICIAN_SWITCH_POWER)) {
					it = new Intent();
					it.setClass(TimerActionsActivity.this,
							SwitchDetailActivity.class);
//					PISManager.cacheMap.put(base.getPISKeyString(), base);
					it.putExtra(HomeActivity.VALUE_KEY, base.getPISKeyString());
				}
				if (it != null) {
					it.putExtra("isTimer", true);
					startActivityForResult(it, Constant.REQUEST_CODE_TIMER_CMD);
					overridePendingTransition(R.anim.anim_in_from_right,
							R.anim.anim_out_to_left);
				}
			}
		});
	}

	/**
	 * 初始化时间组件
	 */

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		mGridView = (GridView) findViewById(R.id.timerdeviceaction_gridview);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.addtimer_action);
		backBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			if (cmdContent != null) {
				Intent it = new Intent();
				it.putExtra("mac", mac);
				it.putExtra("cmd", cmd);
				it.putExtra("servId", servId);
				it.putExtra("cmdContent", cmdContent);
				it.putExtra("name", deviceName);
				setResult(RESULT_OK, it);
			}
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新界面
	 */
	private void refreshViews() {
//		if (infor != null) {
//			List<PISBase> bases = SortUtils.getBases(infor,
//					manager.getPisDeviceList());
//			ArrayList<PISBase> list = manager.getPisSmartCellList();
//			if (list != null && list.size() > 0) {
//				if (!TextUtils.isEmpty(infor.getSupportMacAddr())) {
//					if (PISSmartCell.DEFAULT_MACADDR.equals(infor
//							.getSupportMacAddr())) {
//						for (PISBase base : list) {
//							if (!base.getPISKeyString().equals(
//									infor.getPISKeyString())) {
//								bases.add(base);
//							}
//						}
//					}
//				}
//			}
//			if (infor.getGUID().equals(PISSmartCell.GUID_TIMER_GWCONFIG)
//					&& manager.getDeviceGroupInfors() != null
//					&& manager.getDeviceGroupInfors().size() > 0) {
//				int size = manager.getDeviceGroupInfors().size();
//				SparseArray<PisDeviceGroup> groups = manager
//						.getDeviceGroupInfors();
//				for (int i = 0; i < size; i++) {
//					bases.add(groups.valueAt(i));
//				}
//			}
//			if (null == mAdapter) {
//				mAdapter = new TimerActionsAdapter(TimerActionsActivity.this,
//						bases);
//				mGridView.setAdapter(mAdapter);
//			} else {
//				mAdapter.setList(bases);
//				mAdapter.notifyDataSetChanged();
//			}
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
//		manager.setOnPISChangedListener(this);
		mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		manager.setOnPipaRequestStatusListener(null);
//		manager.setOnPISChangedListener(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i(TAG, "onActivityResult(): resultCode = " + resultCode
				+ ", requestCode = " + requestCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == Constant.REQUEST_CODE_TIMER_CMD) {
				mac = data.getStringExtra("mac");
				cmd = data.getShortExtra("cmd", (short) 0);
				servId = data.getShortExtra("servId", (short) 0);
				cmdContent = data.getByteArrayExtra("cmdContent");
				LogUtils.i(TAG, "onActivityResult(): macAddr = " + mac
						+ ",servId = " + servId);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		// if (isNew) {
//		// mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
//		// }
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//			if (pis.mT1 == PISConstantDefine.PIS_SMART_CELL_T1
//					&& pis.mT2 == PISConstantDefine.PIS_SMART_CELL_T2) {
//				if (reqType == PISSmartCell.PIS_CMD_SMART_GET_INFO) {
//					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
//				}
//			} else if (pis.mT1 == PISConstantDefine.PIS_DEVICE_T1
//					&& pis.mT2 == PISConstantDefine.PIS_DEVICE_T2) {
//				if (reqType == PISDevice.PIS_CMD_DEVICE_INFO_GET) {
//					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
//				}
//			}
//		}
//	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}

}