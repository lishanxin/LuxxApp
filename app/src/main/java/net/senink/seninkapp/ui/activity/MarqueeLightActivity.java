package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.MarqueLightAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISMarquee;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

/**
 * 跑马灯下添加灯的界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-20
 */
public class MarqueeLightActivity extends BaseActivity implements
		View.OnClickListener {
	// 加载框显示的最长时间
	private final static int SHOW_MAX_TIME = 60000;
	// 延迟发送消息时间
	private final static int REQUEST_TIME = 2000;
	// 点击item时发送消息
	public final static int MSG_ITEM_CLICK = 101;
	private static final int MSG_BIND_FAILED = 10;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 组列表
	private PullToRefreshListView mRefreshListView;
	private ListView listView;
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	// 适配器
	private MarqueLightAdapter adapter;

	private PISManager manager = null;
	// 蓝牙管理器
	private MeshController controller;
	// 跑马灯对象
//	private PISMarquee mPisMarquee;
	// 是否添加的是组
	private boolean isGroup;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageModel.MSG_GET_DEVICES:
				mRefreshListView.onRefreshComplete();
				refreshListView();
				break;
			case MSG_ITEM_CLICK:
				showLoadingDialog();
//				if (msg.obj != null && mPisMarquee != null) {
//					if (isGroup) {
//						PISBase group = (PISBase) msg.obj;
//						mPisMarquee.setGroup((int) group.groupId, true);
//					} else {
//						PISBase base = (PISBase) msg.obj;
//						if (TextUtils.isEmpty(base.macAddr)) {
//							sendEmptyMessage(MSG_BIND_FAILED);
//						} else {
//							mPisMarquee.addLight(base.macAddr, (short)base.mServiceID,
//									true);
//						}
//					}
//				}
				break;
			case MSG_BIND_FAILED:
				hideLoadingDialog();
				ToastUtils.showToast(MarqueeLightActivity.this,
						R.string.marquee_light_add_failed);
				break;
			}
		}
	};
//
//	private onFeedbackListener listener = new onFeedbackListener() {
//
//		@Override
//		public void onConnected() {
//
//		}
//
//		@Override
//		public void onNetSecurity() {
//
//		}
//
//		@Override
//		public void onDisconnected() {
//			CommonUtils.backToMain(MarqueeLightActivity.this);
//		}
//
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lightedit);
		manager = PISManager.getInstance();
		controller = MeshController.getInstance(this);
		initView();
		setData();
		setListener();
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null) {
			Intent itent = getIntent();
			String key = itent.getStringExtra(MessageModel.ACTIVITY_VALUE);
			isGroup = itent.getBooleanExtra("isGroup", false);
			if (key != null) {
//				PISBase base = PISManager.cacheMap.get(key);
//				if (base.getT1() == PISConstantDefine.PIS_SMART_CELL_T1
//						&& base.getT2() == PISConstantDefine.PIS_SMART_CELL_T2) {
//					PISSmartCell cell = (PISSmartCell) base;
//					if (cell.getGUID().equals(PISSmartCell.GUID_MARQUEE_LIGHT)) {
//						mPisMarquee = (PISMarquee) cell;
//					} else {
//
//					}
//				}
			}
		}
		refreshListView();
	}

	private void refreshListView() {
		if (isGroup) {
//			if (null == adapter) {
//				adapter = new MarqueLightAdapter(MarqueeLightActivity.this,
//						getGroups(), mHandler);
//				listView.setAdapter(adapter);
//			} else {
//				adapter.setList(getGroups());
//				adapter.notifyDataSetChanged();
//			}
		} else {
			List<PISBase> list = getLights();
			if (null == adapter) {
				adapter = new MarqueLightAdapter(MarqueeLightActivity.this,
						list, mHandler);
				listView.setAdapter(adapter);
			} else {
				adapter.setList(list);
				adapter.notifyDataSetChanged();
			}
		}
	}

	private SparseArray<PISBase> getGroups() {
		SparseArray<PISBase> list = null;
//		SparseArray<PISBase> groups = manager.getDeviceGroupInfors();
//		if (null == groups || groups.size() == 0) {
//			PISMCSManager mcm = manager.getMCSObject();
//			if (mcm != null) {
//				mcm.getGroupsOnDevice();
//			}
//		} else {
//			list = new SparseArray<PISBase>();
//			int size = groups.size();
//			for (int i = 0; i < size; i++) {
//				PISBase group = groups.valueAt(i);
//				if (group.getT1() == PISConstantDefine.PIS_XINCOLOR_T1
//						&& group.getT2() == PISConstantDefine.PIS_XINCOLOR_T2) {
//					list.append(group.groupId, group);
//				}
//			}
//		}
		return list;
	}

	/**
	 * 过滤已经添加过的灯设备
	 * 
	 * @return
	 */
	private List<PISBase> getLights() {
//		ArrayList<PISDevice> devices = manager.getPisDeviceList();
//		List<PISBase> bases = null;
		List<PISBase> list = new ArrayList<PISBase>();
//		if (devices != null) {
//			if (mPisMarquee != null) {
//				bases = mPisMarquee.getAllLights();
//			}
//			for (PISDevice device : devices) {
//				if (device.getPIServices() != null
//						&& device.getPIServices().size() > 0) {
//					PISBase base = device.getPIServices().get(0);
//					if (base.mT1 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T1
//							&& base.mT2 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T2) {
//						boolean exist = false;
//						base.macAddr = device.macAddr;
//						if (bases != null) {
//							for (PISBase pis : bases) {
//								if (pis.getPISKeyString().equals(
//										base.getPISKeyString())
//										|| (!TextUtils.isEmpty(pis.macAddr)
//												&& !TextUtils
//														.isEmpty(base.macAddr) && pis.macAddr
//													.equals(base.macAddr))) {
//									exist = true;
//									break;
//								}
//							}
//						}
//						if (!exist) {
//							list.add(base);
//						}
//					}
//				}
//			}
//		}
		return list;
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		mRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				manager.DiscoverAll();
				mHandler.sendEmptyMessageDelayed(MessageModel.MSG_GET_DEVICES,
						REQUEST_TIME);
			}
		});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		ivLoading = (ImageView) findViewById(R.id.lightedit_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.lightedit_loading_layout);
		mRefreshListView = (PullToRefreshListView) findViewById(R.id.lightedit_grouplist);
		mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView = mRefreshListView.getRefreshableView();
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.marquee_add);
		backBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
		mHandler.removeMessages(MSG_BIND_FAILED);
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		anima.start();
		loadingLayout.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(MSG_BIND_FAILED, SHOW_MAX_TIME);
	}

	/**
	 * 显示加载动画
	 */
	private void hideLoadingDialog() {
		if (anima != null) {
			anima.stop();
		}
		loadingLayout.setVisibility(View.GONE);
		mHandler.removeMessages(MSG_BIND_FAILED);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			if (loadingLayout.getVisibility() == View.VISIBLE) {
				hideLoadingDialog();
			}else{
			MarqueeLightActivity.this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			}
			break;
		default:
			break;
		}
	}

//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		if (netConnStatus == PISConstantDefine.PIPA_NET_STATUS_DISCONNECTED) {
//			Toast.makeText(MarqueeLightActivity.this, R.string.disconnect,
//					Toast.LENGTH_SHORT).show();
//		}
//	}

	@Override
	protected void onStart() {
		super.onStart();
		String location = SharePreferenceUtils.getLocationValue(this,
				String.valueOf(0));
		if (null == location) {
			new Thread() {
				@Override
				public void run() {
//					PISManager.locations = HttpUtils
//							.getLocationAndSave(MarqueeLightActivity.this);
				}
			}.start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
//		manager.setOnPISChangedListener(this);
//		controller.setonFeedbackListener(listener);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		if (controller != null) {
//			controller.setonFeedbackListener(null);
//		}
//		manager.setOnPipaRequestStatusListener(null);
//		manager.setOnPISChangedListener(null);
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
//		if (!isGroup && pis.mT1 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T1
//				&& pis.mT2 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T2) {
//			boolean exist = false;
//			if (adapter != null) {
//				List<PISBase> bases = adapter.getLights();
//				if (bases != null && bases.size() > 0) {
//					for (PISBase base : bases) {
//						if (pis.getPISKeyString()
//								.equals(base.getPISKeyString())
//								|| (!TextUtils.isEmpty(pis.macAddr)
//										&& !TextUtils.isEmpty(base.macAddr) && pis.macAddr
//											.equals(base.macAddr))) {
//							exist = true;
//							break;
//						}
//					}
//				}
//			}
//			if (!exist) {
//				refreshListView();
//			}
//		}
//	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		super.onDestroy();
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis.mT1 == PISConstantDefine.PIS_SMART_CELL_T1
//				&& pis.mT2 == PISConstantDefine.PIS_SMART_CELL_T2) {
//			if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_DEV_ADD) {
//				hideLoadingDialog();
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					ToastUtils.showToast(this,
//							R.string.marquee_light_add_success);
//					PISManager.cacheMap.put(mPisMarquee.getPISKeyString(),
//							mPisMarquee);
//					setResult(RESULT_OK);
//					backBtn.performClick();
//				} else {
//					ToastUtils.showToast(this,
//							R.string.marquee_light_add_failed);
//				}
//			} else if (reqType == PISMarquee.PIS_CMD_HORSE_RACE_GROUP_SET) {
//				hideLoadingDialog();
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					ToastUtils.showToast(this,
//							R.string.marquee_set_group_success);
//					PISManager.cacheMap.put(mPisMarquee.getPISKeyString(),
//							mPisMarquee);
//					setResult(RESULT_OK);
//					backBtn.performClick();
//				} else {
//					ToastUtils.showToast(this,
//							R.string.marquee_set_group_failed);
//				}
//			}
//		} else if (pis.mT1 == PISConstantDefine.PIS_MCM_T1
//				&& pis.mT2 == PISConstantDefine.PIS_MCM_T2) {
//			if (reqType == PISDevice.PIS_CMD_GROUP_GET
//					&& result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				refreshListView();
//			}
//		}
//	}
}