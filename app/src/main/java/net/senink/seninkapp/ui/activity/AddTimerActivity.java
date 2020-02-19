package net.senink.seninkapp.ui.activity;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.datepicker.OnWheelChangedListener;
import com.datepicker.OnWheelScrollListener;
import com.datepicker.WheelView;
import com.datepicker.adapter.NumericWheelAdapter;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISGWConfigTimer;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PISSwitchTimer;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.TimerTaskInfor;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

/**
 * 智能控制之定时器添加界面
 * 
 * @author zhaojunfeng
 * @date 2015-12-31
 */
public class AddTimerActivity extends BaseActivity implements
		View.OnClickListener {
	private final static String TAG = "AddTimerActivity";
	// 添加成功
	private final static int MSG_TIMER_ADD_SUCCESS = 10;
	// 添加失败
	private final static int MSG_TIMER_ADD_FAILED = 11;
	// 修改成功
	private final static int MSG_TIMER_MODIFY_SUCCESS = 12;
	// 修改失败
	private final static int MSG_TIMER_MODIFY_FAILED = 13;

	// 加载框显示的最长时间
	private final static int SHOW_MAX_TIME = 30000;
	// 超时消息
	private final static int MSG_TIMEOUT = 9;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	//
	// 添加按钮
	private Button saveBtn;
	//定时器时间
	private TextView tvTime;
	//定时器的设备
	private TextView tvName;
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	// 重复条件布局
	private RelativeLayout conditionLayout;
	// 设备与动作布局
	private RelativeLayout actionLayout;
	// 定时器的对象
//	private PISSmartCell infor;
	private boolean timeScrolled = false;
	private String currentTime = null;
	// 定时器的日期
	private String dateTime = null;
	// 需要设置设备的mac地址
	private String macAddr = null;
	// 需要设置的命令
	private byte cmd = 0;
	// 需要设置的对应设备下的服务id
	private short serviceId = 0;
	// 需要设置的命令内容
	private byte[] cmdContent;
	private PISManager manager;
	private WheelView mins;
	private WheelView hours;
	private TimerTaskInfor taskInfor;
	// 定时器任务id
	private byte taskId = -1;
	private String deviceName;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TIMEOUT:
				removeMessages(MSG_TIMER_ADD_FAILED);
				removeMessages(MSG_TIMEOUT);
				hideLoadingDialog();
				ToastUtils.showToast(AddTimerActivity.this,
						R.string.addtimer_commit_failed);
				break;
			case MSG_TIMER_ADD_SUCCESS:
				removeMessages(MSG_TIMER_ADD_FAILED);
				ToastUtils.showToast(AddTimerActivity.this,
						R.string.addtimer_add_success);
				setResult(RESULT_OK);
				backBtn.performClick();
				break;
			case MSG_TIMER_ADD_FAILED:
				hideLoadingDialog();
				removeMessages(MSG_TIMER_ADD_FAILED);
				ToastUtils.showToast(AddTimerActivity.this,
						R.string.addtimer_add_failed);
				break;
			case MSG_TIMER_MODIFY_SUCCESS:
				ToastUtils.showToast(AddTimerActivity.this,
						R.string.addtimer_modify_success);
				setResult(RESULT_OK);
				backBtn.performClick();
				break;
			case MSG_TIMER_MODIFY_FAILED:
				ToastUtils.showToast(AddTimerActivity.this,
						R.string.addtimer_modify_failed);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addtimer);
		manager = PISManager.getInstance();
		setData();
		initView();
		setView(true);
		initTimePicker();
		setListener();
	}

	/**
	 * 
	 */
	private void setView(boolean isFirst) {
		if (!TextUtils.isEmpty(dateTime)) {
			String time = StringUtils.getDay(this, dateTime.getBytes());
			tvTime.setText(time==null?"":time);
		}
		if (isFirst) {
			if (taskInfor != null) {
				PISBase base = SortUtils.getPISBase(taskInfor.macAddr, taskInfor.serviceId);
				if (base != null) {
					deviceName = base.getName() == null?"":base.getName();
				}
			}
		}
		tvName.setText(deviceName);
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null) {
			Intent intent = getIntent();
			String key = intent.getStringExtra(MessageModel.ACTIVITY_VALUE);
			taskId = intent.getByteExtra("taskid", (byte) -1);
			try {
//				infor = (PISSmartCell) PISManager.cacheMap.get(key);
//				if (infor != null && infor.tasks.get(taskId) != null) {
//					taskInfor = infor.tasks.get(taskId);
//					if (taskInfor.time != null) {
//						currentTime = StringUtils.getTime(taskInfor.time);
//						dateTime = StringUtils.getDate(taskInfor.time);
//					}
//				}
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
		saveBtn.setOnClickListener(this);
		conditionLayout.setOnClickListener(this);
		actionLayout.setOnClickListener(this);
	}

	/**
	 * 初始化时间组件
	 */
	private void initTimePicker() {
		int curHours = 0;
		int curMinutes = 0;
		if (taskInfor != null) {
			int[] data = StringUtils.getHourAndMinute(StringUtils
					.getTime(taskInfor.time));
			curHours = data[0];
			curMinutes = data[1];
		}
		hours = (WheelView) findViewById(R.id.addtimer_hour);
		hours.setViewAdapter(
				new NumericWheelAdapter(this, 0, 23, "%02d", true), true);
		hours.setCyclic(true);

		mins = (WheelView) findViewById(R.id.addtimer_mins);
		mins.setViewAdapter(
				new NumericWheelAdapter(this, 0, 59, "%02d", false), false);
		mins.setCyclic(true);
		if (!(curHours >= 0 && curHours <= 23 && curMinutes >= 0 && curMinutes <= 59)
				|| (curHours == 0 && curMinutes >= 0)) {
			Calendar c = Calendar.getInstance();
			curHours = c.get(Calendar.HOUR_OF_DAY);
			curMinutes = c.get(Calendar.MINUTE);
		}
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);
		hours.setWheelBackground(Color.TRANSPARENT);
		mins.setWheelBackground(Color.TRANSPARENT);
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					setTime();
				}
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				setTime();
			}
		};

		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
	}

	private void setTime() {
		String hour = hours.getCurrentItem() < 10 ? (String.valueOf(0) + hours
				.getCurrentItem()) : String.valueOf(hours.getCurrentItem());
		String min = mins.getCurrentItem() < 10 ? (String.valueOf(0) + mins
				.getCurrentItem()) : String.valueOf(mins.getCurrentItem());
		currentTime = hour + ":" + min;
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		saveBtn = (Button) findViewById(R.id.title_finished);
		ivLoading = (ImageView) findViewById(R.id.addtimer_loading);
		tvName = (TextView) findViewById(R.id.addtimer_name);
		tvTime = (TextView) findViewById(R.id.addtimer_time);
		loadingLayout = (RelativeLayout) findViewById(R.id.addtimer_loading_layout);
		actionLayout = (RelativeLayout) findViewById(R.id.addtimer_action_layout);
		conditionLayout = (RelativeLayout) findViewById(R.id.addtimer_condition_layout);
		// 设置标题内容
		setTitle();
		if (taskId > 0) {
			actionLayout.setVisibility(View.GONE);
		}else{
			actionLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.addtimer_add);
		saveBtn.setText(R.string.save);
		saveBtn.setBackgroundColor(Color.TRANSPARENT);
		backBtn.setVisibility(View.VISIBLE);
		saveBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示加载动画
	 */
	private void showLoadingDialog() {
		mHandler.removeMessages(MSG_TIMEOUT);
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		if (loadingLayout != null) {
			loadingLayout.setVisibility(View.VISIBLE);
		}
		anima.start();
		mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, SHOW_MAX_TIME);
	}

	/**
	 * 显示加载动画
	 */
	private void hideLoadingDialog() {
		if (anima != null) {
			anima.stop();
		}
		if (loadingLayout != null) {
			loadingLayout.setVisibility(View.GONE);
		}
		mHandler.removeMessages(MSG_TIMEOUT);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			if (loadingLayout.getVisibility() == View.GONE) {
				finish();
				overridePendingTransition(R.anim.anim_in_from_left,
						R.anim.anim_out_to_right);
			}
			break;
		case R.id.title_finished:
			if (loadingLayout.getVisibility() == View.GONE) {
				if (TextUtils.isEmpty(macAddr) && serviceId == 0 && taskId <= 0) {
					ToastUtils.showToast(this, R.string.addtimer_not_setting);
				} else {
					showLoadingDialog();
					save();
				}
			}
			break;
		case R.id.addtimer_action_layout:
			intent = new Intent(AddTimerActivity.this,
					TimerActionsActivity.class);
//			if (infor != null) {
//				PISManager.cacheMap.put(infor.getPISKeyString(), infor);
//				intent.putExtra(MessageModel.ACTIVITY_VALUE,
//						infor.getPISKeyString());
//			}
			startActivityForResult(intent, Constant.REQUEST_CODE_TIMER_ACTION);
			break;
		case R.id.addtimer_condition_layout:
			intent = new Intent(AddTimerActivity.this,
					TimerConditionActivity.class);
			if (!TextUtils.isEmpty(dateTime)) {
				intent.putExtra("date",dateTime);
			}
			startActivityForResult(intent,
					Constant.REQUEST_CODE_TIMER_CONDITION);
			break;
		default:
			break;
		}
		if (intent != null) {
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
		}
	}

	/**
	 * 保存定时器
	 */
	private void save() {
		String time = null;
		if (TextUtils.isEmpty(dateTime)) {
			dateTime = "xxxxxxxxxxxxxxx";
		}
		if (TextUtils.isEmpty(currentTime)) {
			setTime();
		}
		time = dateTime + currentTime;
		if (null == cmdContent && taskInfor != null) {
			if (taskInfor != null) {
				cmdContent = taskInfor.cmdData;
				cmd = taskInfor.cmd;
			}
		}
//		if (infor.getGUID().equals(PISSmartCell.GUID_TIMER_GWCONFIG)) {
//			if (taskId >= 0) {
//				((PISGWConfigTimer) infor).updateTimerTask(taskId, time, cmd,
//						cmdContent, true);
//			} else {
//				if (TextUtils.isEmpty(macAddr)) {
//					mHandler.sendEmptyMessage(MSG_TIMER_ADD_FAILED);
//				}else{
//				   ((PISGWConfigTimer) infor).addTimerTask(macAddr, serviceId,
//						time, cmd, cmdContent, true);
//			   }
//			}
//		} else if (infor.getGUID().equals(PISSmartCell.GUID_TIMER_SWITCH)) {
//			if (taskId >= 0) {
//				((PISSwitchTimer) infor).updateTimerTask(taskId, time, cmd,
//						cmdContent, true);
//			} else {
//				if (TextUtils.isEmpty(macAddr)) {
//					mHandler.sendEmptyMessage(MSG_TIMER_ADD_FAILED);
//				}else{
//				((PISSwitchTimer) infor).addTimerTask(macAddr, serviceId, time,
//						cmd, cmdContent, true);
//				}
//			}
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPipaRequestStatusListener(this);
//		manager.setOnPISChangedListener(this);
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
			if (requestCode == Constant.REQUEST_CODE_TIMER_CONDITION) {
				dateTime = data.getStringExtra("repeat");
				setView(false);
				LogUtils.i(TAG, "onActivityResult(): dateTime = " + dateTime);
			} else if (requestCode == Constant.REQUEST_CODE_TIMER_ACTION) {
				macAddr = data.getStringExtra("mac");
				cmd = (byte) (data.getShortExtra("cmd", (short) 0) & 0xff);
				serviceId = data.getShortExtra("servId", (short) 0);
				cmdContent = data.getByteArrayExtra("cmdContent");
				deviceName = data.getStringExtra("name");
				deviceName = deviceName==null?"":deviceName;
				setView(false);
				LogUtils.i(TAG, "onActivityResult(): macAddr = " + macAddr);
			}
		}
		hideLoadingDialog();
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
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis.mPisType == PISConstantDefine.PIS_TYPE_SMART_CELL) {
//			if (reqType == PISGWConfigTimer.PIS_CMD_TIMER_DATA_GET
//					|| reqType == PISSwitchTimer.PIS_CMD_TIMER_DATA_GET) {
//				mHandler.sendEmptyMessage(MessageModel.MSG_GET_DEVICES);
//			} else if (reqType == PISGWConfigTimer.PIS_CMD_TIMER_ADD) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_TIMER_ADD_SUCCESS);
//				} else {
//					mHandler.sendEmptyMessage(MSG_TIMER_ADD_FAILED);
//				}
//				mHandler.removeMessages(MSG_TIMEOUT);
//				hideLoadingDialog();
//			} else if (reqType == PISGWConfigTimer.PIS_CMD_TIMER_UPD
//					|| reqType == PISSwitchTimer.PIS_CMD_TIMER_UPD) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_TIMER_MODIFY_SUCCESS);
//				} else {
//					mHandler.sendEmptyMessage(MSG_TIMER_MODIFY_FAILED);
//				}
//				mHandler.removeMessages(MSG_TIMEOUT);
//				hideLoadingDialog();
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
		if (loadingLayout.getVisibility() == View.GONE) {
			backBtn.performClick();
		} else {
            loadingLayout.setVisibility(View.GONE);
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
//	public void onNetworkStatusChange(int netConnStatus) {
//		// TODO Auto-generated method stub
//
//	}

}