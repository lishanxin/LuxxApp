package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.LightDetailGroupAdapter;
import net.senink.seninkapp.adapter.ScenesAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISLightRGB;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.sqlite.SceneDao;
import net.senink.seninkapp.ui.cache.CacheManager;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.listview.SwipeMenu;
import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;

/**
 * 用设置灯组的界面
 * 
 * @author zhaojunfeng
 * @date 2015-07-15
 */
public class LightGroupSettingActivity extends BaseActivity implements
		View.OnClickListener {
	// 场景界面请求码
	private static final int REQUEST_CODE_SCENE = 10;
	// 编辑界面请求码
	private static final int REQUEST_CODE_EDIT = 11;
	// 修改名称或者位置界面请求码
	private static final int REQUEST_CODE_MODIFYNAME = 12;

	private static final int MSG_DELETE_FAILED = 13;
	// 删除组失败
	private static final int MSG_DELETE_GROUP_FAILED = 14;
	public static final String TAG = "LightGroupSettingActivity";
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 灯的名称
	private TextView nameBtn;
	// 灯名称的布局
	private RelativeLayout nameLayout;
	// 开关状态
	private CheckBox switcher;
	// 滚动组件
	private ScrollView scroller;
	// 删除按钮
	private Button deleteBtn;
	// 提示
	private TextView tvTip;
	// 组提示
	private ImageButton editBtn;
	// 组列表
	private SwipeMenuListView listView;
	// 场景的添加按钮
	private ImageButton addSceneBtn;
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	// 场景列表
	private SwipeMenuListView sceneListView;
	// 适配器
	private LightDetailGroupAdapter adapter;
	// 传递过来的pisbase对象
	private PISBase infor;
	// 该组内最后一个设备的PISLightRGBW对象
	private PISxinColor light;
	// 该组内最后一个设备的PISLightLED对象
	private PISXinLight lightOnLED;
	private PISManager manager = null;
	// 获取信息
	private PISMCSManager mcm;
	// 组id
	private short groupId = -1;
	// 灯组是否是led
	private boolean isLED = false;
	// 蓝牙管理器
	private MeshController controller;
	// 从数据库中获取场景信息的类
	private SceneDao mSceneDao;
	// 场景的适配器
	private ScenesAdapter mSceneAdapter;
	// 场景模式的缓存管理类
	private CacheManager cacheManger;
    //是否是删除组
	private boolean isDelete = false;
	//要解绑的服务
	private PISBase mSelectedPISBase;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DELETE_FAILED:
				ToastUtils.showToast(LightGroupSettingActivity.this,
						R.string.del_smart_fail);
				break;
			case MessageModel.MSG_GET_DEVICES:
				mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
				if (null != infor) {
//					ArrayList<PISBase> bases = SortUtils
//							.sortPISBase(infor.infors);
//					if (null == adapter) {
//						adapter = new LightDetailGroupAdapter(
//								LightGroupSettingActivity.this, bases,
//								infor.groupId);
//						listView.setAdapter(adapter);
//					} else {
//						adapter.setList(bases);
//						adapter.notifyDataSetChanged();
//					}
//					PISBase base = null;
//					boolean isNull = true;
//					if (bases != null && bases.size() > 0) {
//						base = bases.get(0);
//						if (base != null) {
//							if (base instanceof PISLightLED) {
//								lightOnLED = (PISLightLED) base;
//							} else {
//								light = (PISLightRGB) base;
//							}
//							isNull = false;
//						}
//					}
//					if (isNull) {
//						light = null;
//						lightOnLED = null;
//					}
//					setLight(base);
				} else {
					if (null == adapter) {
						adapter = new LightDetailGroupAdapter(
								LightGroupSettingActivity.this, null, (short) 0);
						listView.setAdapter(adapter);
					} else {
						adapter.setList(null);
						adapter.notifyDataSetChanged();
					}
					setLight(null);
				}
				setView();
				break;
			case MSG_DELETE_GROUP_FAILED:
				removeMessages(MSG_DELETE_GROUP_FAILED);
				hideLoadingDialog();
				ToastUtils.showToast(LightGroupSettingActivity.this,
						R.string.del_smart_fail);
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
//			// setSecurity();
//		}
//
//		@Override
//		public void onDisconnected() {
//			CommonUtils.backToMain(LightGroupSettingActivity.this);
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lightgroupsetting);
		controller = MeshController.getInstance(this);
		manager = PISManager.getInstance();
		cacheManger = CacheManager.getInstance();
		mSceneDao = new SceneDao(this);
		initView();
		setData();
		setListener();
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (manager != null) {
			mcm = manager.getMCSObject();
		}
		if (getIntent() != null) {
			Intent intent = getIntent();
			String keyStr = intent.getStringExtra(MessageModel.PISBASE_KEYSTR);
			infor = PISManager.getInstance().getPISObject(keyStr);
			intent.removeExtra(MessageModel.PISBASE_KEYSTR);
		}
	}

	/**
	 * 给各个组件赋值
	 */
	private void setView() {
		if (infor != null) {
			if (isLED) {
				if (lightOnLED != null) {
					switcher.setChecked(lightOnLED.getLightStatus()==PISXinLight.XINLIGHT_STATUS_ON);
				} else {
					switcher.setChecked(false);
				}
			} else {
				if (light != null) {
					switcher.setChecked(light.getLightStatus() == PISXinLight.XINLIGHT_STATUS_ON);
				} else {
					switcher.setChecked(false);
				}
			}
		}
		if (isLED) {
			tvTitle.setText(R.string.lightgroup_title_circle);
			tvTip.setText(R.string.lightgroup_tip_circle);
		} else {
			tvTip.setText(R.string.lightgroup_tip_bubble);
			tvTitle.setText(R.string.lightgroup_title_bubble);
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		setListViewListener();
		nameLayout.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		// cancelBtn.setOnClickListener(this);
		nameBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		addSceneBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
//				if (isLED) {
//					if (lightOnLED != null) {
//						lightOnLED.setGroupLightState(groupId, isChecked, true);
//					}
//				} else {
//					if (light != null) {
//						light.setGroupLightState(groupId, isChecked, true);
//					}
//				}
			}
		});
		setSceneListViewListener();
	}

	private void setSceneListViewListener() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem editItem = new SwipeMenuItem(
						getApplicationContext());
				editItem.setBackground(new ColorDrawable(0xFFFD430A));
				editItem.setWidth(Utils.dpToPx(50,
						LightGroupSettingActivity.this.getResources()));
				editItem.setTitle(R.string.delete);
				editItem.setTitleSize(18);
				editItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(editItem);
			}
		};
		sceneListView.setMenuCreator(creator);
		sceneListView
				.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(int position,
							SwipeMenu menu, int index) {
						switch (index) {
						case 0:
							if (mSceneAdapter.getCount() > 6) {
								try {
									SceneBean bean = mSceneAdapter
											.getItem(position);
									int result = mSceneDao
											.delecteScene(bean.id);
									if (result > 0) {
										cacheManger.removeScene(bean);
										mSceneAdapter.removeItem(bean);
									} else {
										mHandler.sendEmptyMessage(MSG_DELETE_FAILED);
									}
								} catch (Exception e) {
									PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
								}
							} else {
								ToastUtils.showToast(
										LightGroupSettingActivity.this,
										R.string.lightgroup_less_six);
							}
							break;
						}
						return false;
					}
				});

		sceneListView
				.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

					@Override
					public void onSwipeStart(int position) {
						// swipe start
					}

					@Override
					public void onSwipeEnd(int position) {
						// swipe end
					}
				});
		sceneListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SceneBean bean = mSceneAdapter.getItem(position);
				Intent it = new Intent(LightGroupSettingActivity.this,
						SceneDetailActivity.class);
				it.putExtra("scene", bean);
				startActivityForResult(it, REQUEST_CODE_SCENE);
				overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			}
		});
	}

	/**
	 * 设置场景
	 */
	private void setScenes() {
		List<SceneBean> beans = null;
		if (isLED) {
			if (null == cacheManger.getLEDScenes()) {
				cacheManger.setScenes(mSceneDao
						.getAllScenes(SharePreferenceUtils.getInstance(this)
								.getCurrentUser()));
			}
			beans = cacheManger.getLEDScenes();
		} else {
			if (null == cacheManger.getRGBScenes()) {
				cacheManger.setScenes(mSceneDao
						.getAllScenes(SharePreferenceUtils.getInstance(this)
								.getCurrentUser()));
			}
			beans = cacheManger.getRGBScenes();
		}
		mSceneAdapter = new ScenesAdapter(this, beans);
		sceneListView.setAdapter(mSceneAdapter);
	}

	/**
	 * 从组列表中选取其中一个灯作为控制灯开关的对象
	 */
	public void setLight(PISBase base) {
		if (base != null) {
			if (isLED) {
				lightOnLED = (PISXinLight) base;
				if (lightOnLED != null) {
					lightOnLED.updateLightStatus();
				}
			} else {
				light = (PISxinColor) base;
				if (light != null) {
//					light.getLightState(true);
					PipaRequest req = light.updateLightStatus();
					light.request(req);
				}
			}
		} else {
			if (isLED) {
				lightOnLED = null;
			} else {
				light = null;
			}
		}
	}

	/**
	 * 设置设备列表的监听器
	 */
	private void setListViewListener() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(0xFFFD430A));
				deleteItem.setWidth(dp2px(50));
				deleteItem.setTitle(R.string.delete);
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);
		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					mSelectedPISBase = adapter.getInfor(position);
					showLoadingDialog();
					PISManager.getInstance().removePISObject(mSelectedPISBase);
//					manager.removeDevice(groupId, mSelectedPISBase);
					break;
				}
				return false;
			}
		});

		// set SwipeListener
		listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
			
			}

			@Override
			public void onSwipeEnd(int position) {
			
			}
		});
	}

	/**
	 * 值的转换
	 * 
	 * @param dp
	 * @return
	 */
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		nameLayout = (RelativeLayout) findViewById(R.id.lightgroupsetting_name_layout);
		nameBtn = (TextView) findViewById(R.id.lightgroupsetting_groupname);
		switcher = (CheckBox) findViewById(R.id.lightgroupsetting_switch);
		scroller = (ScrollView) findViewById(R.id.lightgroupsetting_scroller);
		editBtn = (ImageButton) findViewById(R.id.lightgroupsetting_edit);
		listView = (SwipeMenuListView) findViewById(R.id.lightgroupsetting_grouplist);
		deleteBtn = (Button) findViewById(R.id.lightgroupsetting_delete);
		ivLoading = (ImageView) findViewById(R.id.lightgroupsetting_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.lightgroupsetting_loading_layout);
		tvTip = (TextView) findViewById(R.id.lightgroup_tip);
		// 场景
		addSceneBtn = (ImageButton) findViewById(R.id.lightgroupsetting_addscene);
		sceneListView = (SwipeMenuListView) findViewById(R.id.lightgroupsetting_scenes);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		backBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			if (ev.getAction() == MotionEvent.BUTTON_BACK) {
				hideLoadingDialog();
			}
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		anima.start();
		loadingLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示加载动画
	 */
	private void hideLoadingDialog() {
		if (anima != null) {
			anima.stop();
		}
		loadingLayout.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.lightgroupsetting_addscene:
			if (mSceneAdapter.getCount() >= 12) {
				ToastUtils.showToast(this, R.string.lightgroup_more);
				return;
			}
			intent = new Intent(LightGroupSettingActivity.this,
					SceneDetailActivity.class);
			if (isLED) {
				intent.putExtra("t1", PISConstantDefine.PIS_MAJOR_LIGHT);
				intent.putExtra("t2", PISConstantDefine.PIS_LIGHT_LIGHT);
			} else {
				intent.putExtra("t1", PISConstantDefine.PIS_MAJOR_LIGHT);
				intent.putExtra("t2", PISConstantDefine.PIS_LIGHT_COLOR);
			}
			startActivityForResult(intent, REQUEST_CODE_SCENE);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		case R.id.lightgroupsetting_edit:
			intent = new Intent(LightGroupSettingActivity.this,
					LightEditActivity.class);
			if (infor != null) {
				intent.putExtra(MessageModel.ACTIVITY_VALUE, groupId);
			}
			intent.putExtra("isgroup", true);
			LightGroupSettingActivity.this.startActivityForResult(intent,
					REQUEST_CODE_EDIT);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		// 返回按钮
		case R.id.title_back:
			refreshScenes();
			LightGroupSettingActivity.this.finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.lightgroupsetting_delete:
			if (mcm != null && infor != null) {
				if (adapter.getCount() == 0) {
					deleteGroup();
				} else {
					isDelete = true;
//					infor.unbindAllServicesOnGroup(infor.groupId);
				}
				showLoadingDialog();
				mHandler.sendEmptyMessageDelayed(MSG_DELETE_GROUP_FAILED,
						Constant.TIMEOUT_TIME);
			}
			break;
		// 设置灯的名称
		case R.id.lightgroupsetting_groupname:
		case R.id.lightgroupsetting_name_layout:
			intent = new Intent(LightGroupSettingActivity.this,
					ModifyNameActivity.class);
			intent.putExtra("isgroup", true);
			if (infor != null) {
//				intent.putExtra("groupid", infor.groupId);
			}
			startActivityForResult(intent, REQUEST_CODE_MODIFYNAME);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		default:
			break;
		}
	}

	private void deleteGroup() {
		if (mcm != null && infor != null) {
//			mcm.deleteDeviceGroup(infor.groupId, true);
		}
	}

//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}

	@Override
	protected void onResume() {
		super.onResume();
//		controller.setonFeedbackListener(listener);
//		setScenes();
//		manager.setOnPipaRequestStatusListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		if (controller != null) {
//			controller.setonFeedbackListener(null);
//		}
//		manager.setOnPipaRequestStatusListener(null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshViews();
	}

	private void refreshScenes() {
		List<SceneBean> list;
		if (isLED) {
			list = cacheManger.getLEDScenes();
		} else {
			list = cacheManger.getRGBScenes();
		}
		if (list != null && list.size() > 0) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				SceneBean bean = list.get(i);
				bean.used = i;
				mSceneDao.upgradeScene(bean);
			}
			cacheManger.setScenes(mSceneDao.getAllScenes(SharePreferenceUtils
					.getInstance(this).getCurrentUser()));
		}
	}
	
	private void refreshViews() {
//		infor = manager.getSomeoneGroup(groupId);
//		if (infor != null) {
//			nameBtn.setText(infor.mName == null ? "" : infor.mName);
//			if (infor.mT1 == PISConstantDefine.PIS_LED_LIGHT_T1
//					&& infor.mT2 == PISConstantDefine.PIS_LED_LIGHT_T2) {
//				isLED = true;
//			} else {
//				isLED = false;
//			}
////			infor = manager.upgradeGroup(infor);
//			mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SCENE) {
			if (resultCode == RESULT_OK) {
				if (isLED) {
					mSceneAdapter.setList(cacheManger.getLEDScenes());
				} else {
					mSceneAdapter.setList(cacheManger.getRGBScenes());
				}
				mSceneAdapter.notifyDataSetChanged();
			}
		} else if (requestCode == REQUEST_CODE_EDIT) {
			if (resultCode == RESULT_OK) {
				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
			}
		} else if (requestCode == REQUEST_CODE_MODIFYNAME) {
//			infor = manager.getSomeoneGroup(infor.groupId);
//			nameBtn.setText(infor.mName == null ? "" : infor.mName);
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
//
//	}
//
//	@Override
//	protected void onDestroy() {
//		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
//		isDelete = false;
//		super.onDestroy();
//	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}

//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (reqType == PISMCSManager.PIS_CMD_MCM_DEL_GROUP) {
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				hideLoadingDialog();
//				mHandler.removeMessages(MSG_DELETE_GROUP_FAILED);
//				Intent intent = new Intent(LightGroupSettingActivity.this,
//						HomeActivity.class);
//				LightGroupSettingActivity.this.startActivity(intent);
//				backBtn.performClick();
//			} else {
//				mHandler.sendEmptyMessage(MSG_DELETE_GROUP_FAILED);
//			}
//			isDelete = false;
//		} else if (reqType == PISBase.PIS_CMD_GROUP_GET) {
//			LogUtils.i(TAG, "PISBase.PIS_CMD_GROUP_GET ::: isDelete = "+ isDelete + ",result == "+(result == PipaRequest.REQUEST_RESULT_COMPLETE));
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//				if (isDelete) {
////					manager.upgradeGroup(pis);
////					infor = manager.getSomeoneGroup(infor.groupId);
////					if (infor != null && infor.infors != null) {
////						if (infor.infors.size() == 0) {
////							deleteGroup();
////						} else {
////							mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
////						}
////					}
//				} else {
//					boolean isRefresh = true;
////					if (mSelectedPISBase != null && mSelectedPISBase.getPISKeyString().equals(pis.getPISKeyString())) {
////						LogUtils.i(TAG, "mSelectedPISBase.getPISKeyString().equals(pis.getPISKeyString())");
////						LogUtils.i(TAG, "pis.mac == "+pis.macAddr);
////						pis.macAddr = mSelectedPISBase.macAddr;
////						isRefresh = (isOnGroup(pis)?false:true);//如果在组里面，代表没有删除成功就不刷新
////						hideLoadingDialog();
////						manager.upgradeGroup(pis);
////						mSelectedPISBase = null;
////					}
////					if (isRefresh) {
////						infor = manager.getSomeoneGroup(groupId);
////						mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
////					}
//				}
//			}
//		} else if (reqType == PISBase.PIS_CMD_GROUP_UNSET) {
//			LogUtils.i(TAG, "PISBase.PIS_CMD_GROUP_UNSET ::: isDelete = "+ isDelete + ",result == "+(result == PipaRequest.REQUEST_RESULT_COMPLETE));
//			if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
////				if (isDelete) {
////					pis.getGroupsOnDevice();
////				}else{
////					if (mSelectedPISBase != null && mSelectedPISBase.getPISKeyString().equals(pis.getPISKeyString())) {
////						LogUtils.i(TAG, "mac == "+ pis.macAddr+ ",infor.infors.containsKey = "+ infor.infors.containsKey(pis.macAddr));
////						pis.macAddr = mSelectedPISBase.macAddr;
////						refreshServices(pis);
////						mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
////						scroller.scrollBy(0, -5);
////						ToastUtils.showToast(LightGroupSettingActivity.this,
////								R.string.lightgroup_unbind_success);
////						mSelectedPISBase = null;
////					}
////				}
//			} else if(result == PipaRequest.REQUEST_RESULT_TIMEOUT){
//				LogUtils.i(TAG, "PIS_CMD_GROUP_UNSET timeout");
//				if (mSelectedPISBase != null && mSelectedPISBase.getPISKeyString().equals(pis.getPISKeyString())) {
////					pis.getGroupsOnDevice();
//				}else{
//					hideLoadingDialog();
//				}
//			}else {
//				LogUtils.i(TAG, "PIS_CMD_GROUP_UNSET failed");
//				if (!isDelete) {
//					if (mSelectedPISBase != null && mSelectedPISBase.getPISKeyString().equals(pis.getPISKeyString())) {
//						mSelectedPISBase = null;
//						hideLoadingDialog();
//						ToastUtils.showToast(LightGroupSettingActivity.this,
//								R.string.lightgroup_unbind_failed);
//					}
//				}else{
//					isDelete = false;
//				}
//			}
//			hideLoadingDialog();
//		}else if (isLED && lightOnLED != null) {
////			if (pis.getT1() == PISConstantDefine.PIS_XINLIGHT_T1
////					&& pis.getT2() == PISConstantDefine.PIS_XINLIGHT_T2
////					&& (pis.getPISKeyString().equals(
////							lightOnLED.getPISKeyString()) || (!TextUtils
////							.isEmpty(pis.macAddr)
////							&& !TextUtils.isEmpty(lightOnLED.macAddr) && lightOnLED.macAddr
////								.equals(pis.macAddr)))) {
////				if (reqType == PISXinLight.PIS_CMD_LED_ONOFF_SET
////						|| reqType == PISXinLight.PIS_MSG_LED_STATUS
////						|| reqType == PISXinLight.PIS_CMD_LED_GET_STATE) {
////					// switcher.setChecked(lightOnLED.isOn());
////				}
////			}
//		} else if (!isLED && light != null) {
////			if (pis.getT1() == PISConstantDefine.PIS_XINCOLOR_T1
////					&& pis.getT2() == PISConstantDefine.PIS_XINCOLOR_T2
////					&& (pis.getPISKeyString().equals(light.getPISKeyString()) || (!TextUtils
////							.isEmpty(pis.macAddr)
////							&& !TextUtils.isEmpty(light.macAddr) && light.macAddr
////								.equals(pis.macAddr)))) {
////				if (reqType == PISXinLight.XINLIGHT_STATUS_ON
////						|| reqType == PISxinColor.PIS_MSG_LIGHT_STATUS
////						|| reqType == PISxinColor.PIS_CMD_LIGHT_GET_STATE) {
////					switcher.setChecked(light.getLightStatus()==PISxinColor.XINCOLOR_STATUS_ON);
////				}
////			}
//		}
//	}

	/**
	 * 判断某个服务是否在组里面
	 * @param pis
	 * @return
	 */
	private boolean isOnGroup(PISBase pis) {
		boolean isOn = false;
//		if (pis.groupIds != null && pis.groupIds.length > 0) {
//			for (int i = 0; i < pis.groupIds.length; i++) {
//				if (pis.groupIds[i] == groupId) {
//					isOn = true;
//					LogUtils.i(TAG, "TEST ----is on group");
//					break;
//				}
//			}
//		}else{
//			LogUtils.i(TAG, "TEST ----ids.lenght == 0");
//		}
		return isOn;
	}

	private void refreshServices(PISBase pis) {
//		if (infor != null && infor.infors != null && infor.infors.size() > 0) {
//			if (!TextUtils.isEmpty(pis.macAddr) && infor.infors.containsKey(pis.macAddr) ) {
//				upgradePisBase(pis);
//				infor.infors.remove(pis.macAddr);
//				manager.addDeviceGroup(infor);
//			}else if(TextUtils.isEmpty(pis.macAddr)){
//				Iterator<Entry<String, PISBase>> iterator = infor.infors.entrySet().iterator();
//				while(iterator.hasNext()){
//					Entry<String, PISBase> entry = iterator.next();
//					PISBase base = entry.getValue();
//					if (base.getPISKeyString().equals(pis.getPISKeyString())) {
//						infor.infors.remove(entry.getKey());
//						manager.addDeviceGroup(infor);
//						break;
//					}
//				}
//			}
//		}
	}
	
//	private void upgradePisBase(PISBase base){
//		int id = infor.groupId;
//		if (base.groupIds != null && base.groupIds.length > 0) {
//			for (int i = 0; i< base.groupIds.length;i++) {
//				if (base.groupIds[i] == id) {
//					base.groupIds[i] = -1;
//					manager.upgradePISBaseLight(base);
//					break;
//				}
//			}
//		}
//	}
}