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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.datepicker.OnWheelChangedListener;
import com.datepicker.OnWheelScrollListener;
import com.datepicker.WheelView;
import com.datepicker.adapter.NumericWheelAdapter;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISXinInsole;
//import com.senink.seninkapp.core.PISXinInsole.TimeAction;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

/**
 * 智能控制之定时器添加界面
 * 
 * @author zhaojunfeng
 * @date 2016-02-01
 */
public class AddInsoleTimerActivity extends BaseActivity implements
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
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private AnimationDrawable anima;
	// 重复开关
	private CheckBox cbRepeat;
	// 加热
	private CheckBox cbHeat;
	private PISManager manager;
	private WheelView mins;
	private WheelView hours;
//	private PISXinInsole mXinInsole;
	// 定时器任务id
	private byte taskId = 0;
	// 定时任务对象
//	private TimeAction mTimeAction;
	private int currentTime = 0;
	private boolean isScrolling = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TIMEOUT:
				removeMessages(MSG_TIMER_ADD_FAILED);
				removeMessages(MSG_TIMEOUT);
				hideLoadingDialog();
				ToastUtils.showToast(AddInsoleTimerActivity.this,
						R.string.addtimer_commit_failed);
				break;
			case MSG_TIMER_ADD_SUCCESS:
				removeMessages(MSG_TIMER_ADD_FAILED);
				ToastUtils.showToast(AddInsoleTimerActivity.this,
						R.string.addtimer_add_success);
				setResult(RESULT_OK);
				backBtn.performClick();
				break;
			case MSG_TIMER_ADD_FAILED:
				hideLoadingDialog();
				removeMessages(MSG_TIMER_ADD_FAILED);
				ToastUtils.showToast(AddInsoleTimerActivity.this,
						R.string.addtimer_add_failed);
				break;
			case MSG_TIMER_MODIFY_SUCCESS:
				ToastUtils.showToast(AddInsoleTimerActivity.this,
						R.string.addtimer_modify_success);
				setResult(RESULT_OK);
				backBtn.performClick();
				break;
			case MSG_TIMER_MODIFY_FAILED:
				ToastUtils.showToast(AddInsoleTimerActivity.this,
						R.string.addtimer_modify_failed);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addinsoletimer);
		manager = PISManager.getInstance();
		setData();
		initView();
		setView();
		initTimePicker();
		setListener();
	}

	/**
	 * 给组件赋值
	 */
	private void setView() {
//		if (mTimeAction != null) {
//			cbHeat.setChecked(mTimeAction.isHeated);
//			cbRepeat.setChecked(mTimeAction.repeat);
//		}
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		if (getIntent() != null) {
			Intent intent = getIntent();
			String key = intent.getStringExtra(MessageModel.ACTIVITY_VALUE);
			taskId = intent.getByteExtra("taskid", (byte) 0);
			try {
//				mXinInsole = (PISXinInsole) PISManager.cacheMap.get(key);
//				if (mXinInsole != null && mXinInsole.getTask(taskId) != null) {
//					mTimeAction = mXinInsole.getTask(taskId);
//					if (mTimeAction != null) {
//						currentTime = mTimeAction.time;
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
	}

	/**
	 * 初始化时间组件
	 */
	private void initTimePicker() {
		int curHours = 0;
		int curMinutes = 0;
//		if (mTimeAction != null) {
//			String time = StringUtils.getTime(this, mTimeAction.time);
//			if (TextUtils.isEmpty(time)) {
//				String[] times = time.split(":");
//				curHours = Integer.parseInt(times[0]);
//				curMinutes = Integer.parseInt(times[1]);
//			}
//		}
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
				if (!isScrolling) {
					currentTime = mins.getCurrentItem() * 60
							+ hours.getCurrentItem() * 3600;
				}
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				isScrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				isScrolling = false;
			}
		};

		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		saveBtn = (Button) findViewById(R.id.title_finished);
		ivLoading = (ImageView) findViewById(R.id.addinsoletimer_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.addinsoletimer_loading_layout);
		cbHeat = (CheckBox) findViewById(R.id.addinsoletime_heat);
		cbRepeat = (CheckBox) findViewById(R.id.addinsoletime_repeat);
		// 设置标题内容
		setTitle();
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
				showLoadingDialog();
				save();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 保存定时器
	 */
	private void save() {
//		if (mXinInsole != null) {
//			byte reserved = 0;
//			if (mTimeAction != null) {
//				reserved = mTimeAction.reserved;
//			}
//			mXinInsole.setTimer(taskId, currentTime, cbHeat.isChecked(),
//					cbRepeat.isChecked(), true, reserved, true);
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
//		if (pis.mT1 == (byte) PISConstantDefine.PIS_INSOLE_T1
//				&& pis.mT2 == PISConstantDefine.PIS_INSOLE_T2) {
//			if (reqType == PISXinInsole.PIS_CMD_INSOLE_TIMEACTION_SET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_TIMER_ADD_SUCCESS);
//				} else {
//					mHandler.sendEmptyMessage(MSG_TIMER_ADD_FAILED);
//				}
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