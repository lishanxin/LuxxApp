package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.TimerListAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISGWConfigTimer;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PISSwitchTimer;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.TimerTaskInfor;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

/**
 * 智能控制之网关定时器和开关定时器控制界面
 * 
 * @author zhaojunfeng
 * @date 2015-12-31
 */
public class TimerListActivity extends BaseActivity implements
		View.OnClickListener {
	private final static String TAG = "TimerListActivity";
	// 超时消息
	private final static int MSG_TIMEOUT = 10;
	// 删除成功
	private final static int MSG_DELETE_SUCCESS = 11;
	// 删除失败
	private final static int MSG_DELETE_FAILED = 12;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 添加按钮
	private ImageButton addBtn;
	// 保存按钮
	private Button saveBtn;
	// 开关
	private CheckBox cbSwitch;
	// 组列表
	private ListView listView;
	// 加载动画组件
	private ImageView ivLoading;
	//背景的定时图标
	private ImageView ivIcon;
	// 加载框布局
	private RelativeLayout loadingLayout;
	// 加载动画
	private AnimationDrawable anima;
	// 适配器
	private TimerListAdapter adapter;
	private PISManager manager = null;
	// 定时器的对象
//	private PISSmartCell infor;
	// 命令字
	private short cmd;
	// 命令内容
	private byte[] cmdContent;
	// 是否从定时器添加中的设备与动作界面跳转而来
	private boolean isTimer;
	//是否是第一次创建界面
	private boolean isFirst = true;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TIMEOUT:
				removeMessages(MSG_TIMEOUT);
				hideLoadingDialog();
				break;
			case MessageModel.MSG_GET_DEVICES:
				removeMessages(MSG_TIMEOUT);
				hideLoadingDialog();
				refreshTimers();
				if (adapter.getCount() > 0) {
					ivIcon.setVisibility(View.GONE);
				}else{
					ivIcon.setVisibility(View.VISIBLE);
				}
				break;
			case MSG_DELETE_FAILED:
				removeMessages(MSG_TIMEOUT);
				hideLoadingDialog();
				ToastUtils.showToast(TimerListActivity.this,
						R.string.del_smart_fail);
				break;
			case MSG_DELETE_SUCCESS:
				refreshTimers();
				removeMessages(MSG_TIMEOUT);
				hideLoadingDialog();
				ToastUtils.showToast(TimerListActivity.this,
						R.string.del_smart_succ);
				sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timerlist);
		manager = PISManager.getInstance();
		isTimer = getIntent().getBooleanExtra("isTimer", false);
		initView();
		showLoadingDialog();
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
//		if (infor != null) {
//			cbSwitch.setChecked(infor.getIsRun());
//			infor.getSmartCellState();
//		}
	}

	/**
	 * 更新列表信息
	 */
	private void refreshTimers() {
		SparseArray<TimerTaskInfor> list = null;
//		if (infor != null) {
//			list = infor.tasks;
//		}
////		if (null == adapter) {
//			if (isTimer) {
//				adapter = new TimerListAdapter(this, infor, list,true);
//			} else {
//				adapter = new TimerListAdapter(this, infor, list);
//			}
//			adapter.setStatus(cbSwitch.isChecked());
//			listView.setAdapter(adapter);
////		} else {
////			adapter.setList(list);
////			adapter.notifyDataSetChanged();
////			adapter.notifyDataSetInvalidated();
////		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		if (isTimer) {
			saveBtn.setOnClickListener(this);
		} else {
			addBtn.setOnClickListener(this);
		}
		cbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (adapter != null) {
					adapter.setStatus(isChecked);
				}
//				LogUtils.i("CCCC", "infor.status = "+ infor.getIsRun());
//				if (infor != null && !isTimer) {
//					if (isChecked) {
//						infor.start();
//					} else {
//						infor.stop();
//					}
//				}
			}
		});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		addBtn = (ImageButton) findViewById(R.id.title_add);
		ivLoading = (ImageView) findViewById(R.id.timer_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.timer_loading_layout);
		listView = (ListView) findViewById(R.id.timer_listview);
		cbSwitch = (CheckBox) findViewById(R.id.timer_switch);
		saveBtn = (Button) findViewById(R.id.title_finished);
		ivIcon = (ImageView) findViewById(R.id.timer_icon);
		// 设置标题内容
		setTitle();
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.timer);
		backBtn.setVisibility(View.VISIBLE);
		if (isTimer) {
			saveBtn.setVisibility(View.VISIBLE);
			saveBtn.setText(R.string.save);
			saveBtn.setBackgroundColor(Color.TRANSPARENT);
		} else {
			addBtn.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
//		mHandler.removeMessages(PISBase.MSG_MODIFY_DEVICE_FAILED);
//		if (null == anima) {
//			anima = (AnimationDrawable) ivLoading.getBackground();
//		}
//		anima.start();
//		loadingLayout.setVisibility(View.VISIBLE);
//		mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, SHOW_MAX_TIME);
	}

	/**
	 * 显示加载动画
	 */
	private void hideLoadingDialog() {
		if (anima != null) {
			anima.stop();
		}
		loadingLayout.setVisibility(View.GONE);
		mHandler.removeMessages(MSG_TIMEOUT);
	}

	private void getData(boolean isfreshed) {
//		if (infor != null) {
//			if (infor.getGUID().equals(PISSmartCell.GUID_TIMER_GWCONFIG)) {
//				if (((PISGWConfigTimer) infor).tasks.size() > 0 && !isfreshed) {
//					mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//				}
//				((PISGWConfigTimer) infor).getTaskInfors();
//			} else if (infor.getGUID().equals(PISSmartCell.GUID_TIMER_SWITCH)) {
//				if (((PISSwitchTimer) infor).tasks.size() > 0 && !isfreshed) {
//					mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//				}
//				((PISSwitchTimer) infor).getTaskInfors();
//			}
//		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
//			if (loadingLayout.getVisibility() != View.VISIBLE) {
//				if (isTimer) {
//					if (infor != null && cmdContent != null) {
//						Intent it = new Intent();
//						it.putExtra("mac", infor.getSupportMacAddr());
//						it.putExtra("cmd", cmd);
//						it.putExtra("servId", infor.mServiceID);
//						it.putExtra("cmdContent", cmdContent);
//						setResult(RESULT_OK, it);
//					}
//				}else{
//					PISManager.getInstance().upgradePISSmartCell(infor);
//				}
//				finish();
//				overridePendingTransition(R.anim.anim_in_from_left,
//						R.anim.anim_out_to_right);
//			}
			break;
		case R.id.title_add:
			if (loadingLayout.getVisibility() != View.VISIBLE) {
//				Intent intent = new Intent(this, AddTimerActivity.class);
//				intent.putExtra(MessageModel.ACTIVITY_VALUE,
//						infor.getPISKeyString());
//				startActivityForResult(intent, Constant.REQUEST_CODE_ADDTIMER);
//				overridePendingTransition(R.anim.anim_in_from_right,
//						R.anim.anim_out_to_left);
			}
			break;
		case R.id.title_finished:
			save();
			break;
		default:
			break;
		}
	}

	/**
	 * 保存命令字和命令内容
	 */
	private void save() {
		cmdContent = null;
		cmdContent = new byte[1];
//		if (cbSwitch.isChecked()) {
//			cmdContent[0] = (byte) (0x01 & 0x0f);
//			cmd = PISSmartCell.PIS_CMD_SMART_START;
//		} else {
//			cmdContent[0] = (byte) (0x00 & 0x0f);
//			cmd = PISSmartCell.PIS_CMD_SMART_STOP;
//		}
		ToastUtils.showToast(this, R.string.scene_detail_save_success);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
//		manager.setOnPISChangedListener(this);
		if (isFirst) {
			getData(false);
			isFirst = false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i(TAG, "onActivityResult(): resultCode = " + resultCode
				+ ", requestCode = " + requestCode);
		if (requestCode == Constant.REQUEST_CODE_ADDTIMER) {
			if (resultCode == RESULT_OK) {
//				getData(true);
				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
			}
		} else {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
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
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis.mPisType == PISConstantDefine.PIS_TYPE_SMART_CELL) {
//			if (reqType == PISGWConfigTimer.PIS_CMD_TIMER_DATA_GET
//					|| reqType == PISSwitchTimer.PIS_CMD_TIMER_DATA_GET) {
//				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//			} else if (reqType == PISSwitchTimer.PIS_CMD_TIMER_DEL) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_DELETE_SUCCESS);
//				} else {
//					mHandler.sendEmptyMessage(MSG_DELETE_FAILED);
//				}
//			}else if(reqType == PISSmartCell.PIS_CMD_SMART_GET_STAT){
//				cbSwitch.setChecked(infor.getIsRun());
//				LogUtils.i("CCCC",
//						"PIS_CMD_SMART_GET_STAT, infor.getIsRun() = "
//								+ infor.getIsRun());
//			}
//		}
//	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			hideLoadingDialog();
		} else {
			backBtn.performClick();
		}
	}
//
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