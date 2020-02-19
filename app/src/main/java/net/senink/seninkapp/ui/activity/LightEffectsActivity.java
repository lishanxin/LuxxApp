package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISLightRGB;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.core.PISLightRGB.LightBlink;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.CircleView;
import net.senink.seninkapp.ui.view.ColorCircle;
import net.senink.seninkapp.ui.view.ProgressNumberView;

/**
 * 灯的特效设置界面
 * 
 * @author zhaojunfeng
 * @date 2015-07-14
 */
@SuppressLint("ClickableViewAccessibility")
public class LightEffectsActivity extends BaseActivity implements
		View.OnClickListener {
	private static final String TAG = "LightEffectsActivity";
	// 加载框显示最大时长
	private static final int DELAY_TIME = 30000;
	// 加载框显示超时消息
	private static final int MSG_ORDER_FAILED = 120;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 闪烁调节器
	private SeekBar flashBar;
	// 模式切换文本内容
	private TextView tvTip;
	// 闪烁和渐变的切换按钮
	private ImageView flashOrGradientBtn;
	// 循环次数调节器
	private SeekBar cycleBar;
	// 开始颜色的组件
	private CircleView startColorBtn;
	// 结束颜色的组件
	private CircleView endColorBtn;
	// 闪烁中的慢
	private TextView tvFlashSlow;
	// 闪烁中的中
	private TextView tvFlashMinddle;
	// 闪烁中的快
	private TextView tvFlashFast;
	// 闪烁次数显示组件
	private ProgressNumberView numberView;
	// 颜色圈
	private ColorCircle colorCircle;
	// 颜色圈的布局
	private RelativeLayout circleLayout;
	// 开始的颜色值
	private int startColorValue = 0;
	// 结束的颜色值
	private int endColorValue = 0;
	// 保存按钮
	private Button saveBtn;
	// 是否是渐变
	private boolean isGradient = false;
	/*
	 * 1: 点中开始颜色按钮 2：点中结束颜色按钮
	 */
	private int colorIndex = 0;
	// 传递过来的pisbase对象
	private PISxinColor infor;
	// 是否是分组的
	private boolean isGroup = true;
	// 某个组的信息
	private PISBase groupInfor = null;
	// 白光的值
	private int whiteValue = 0;
	// 组id
	private short groupId = -1;
	// 是否是led
	private boolean isLED = false;
	// 传递过来的pisbase对象
	private PISXinLight inforOnLED;
	// 填充布局
	private View spaceView;
	private PISManager manager = null;
	// 蓝牙管理器
	private MeshController controller;
	// 开始的冷色
	private int cold1 = 0;
	// 结束的冷色
	private int cold2 = 0;
	// 开始的暖色
	private int warm1 = 0;
	// 结束的暖色
	private int warm2 = 0;
	// 命令加载框
	private ProgressDialog dialog;
	// 是否是定时器界面跳转过来
	private boolean isTimer;
	// 需要提交定时器的命令字
	private short cmd;
	// 需要提交定时器的命令内容
	private byte[] cmdContent;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ORDER_FAILED:
				ToastUtils.showToast(LightEffectsActivity.this,
						R.string.lighteffects_set_failed);
				dialog.dismiss();
				try {
					mHandler.removeCallbacks(timer);
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
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
//			CommonUtils.backToMain(LightEffectsActivity.this);
//		}
//	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lighteffects);
		manager = PISManager.getInstance();
		controller = MeshController.getInstance(this);
		dialog = Utils.createProgressDialog(LightEffectsActivity.this,
				R.string.wifi_loading);
		isTimer = getIntent().getBooleanExtra("isTimer", false);
		initView();
		setData();
		setColdWarm();
		setListener();
	}

	/**
	 * 当界面是light或者led时，一些组件需要显示或者隐藏
	 */
	private void setColdWarm() {
		if (!isGroup) {
			if (isLED) {
				circleLayout.setVisibility(View.GONE);
				spaceView.setVisibility(View.VISIBLE);
			} else {
				circleLayout.setVisibility(View.VISIBLE);
				spaceView.setVisibility(View.GONE);
			}
		} else {
			circleLayout.setVisibility(View.VISIBLE);
			spaceView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置各个组件的数据
	 */
	private void setView() {
//		if (infor != null && infor.getLightBlinkInfor() != null) {
//			LightBlink blink = infor.getLightBlinkInfor();
//			int startColor = Color.rgb(blink.redStart, blink.greenStart,
//					blink.blueStart);
//			int endColor = Color.rgb(blink.redEnd, blink.greenEnd,
//					blink.blueEnd);
//			if (!(blink.redStart == 0 && blink.greenStart == 0 && blink.blueStart == 0)) {
//				startColorBtn.changeColor(startColor);
//			}
//			if (!(blink.redEnd == 0 && blink.greenEnd == 0 && blink.blueEnd == 0)) {
//				endColorBtn.changeColor(endColor);
//			}
//			int interval = blink.interval;
//			int time = blink.time;
//			if (time < 0) {
//				time = 0;
//			} else if (time > 255) {
//				time = 255;
//			}
//			if (interval < 0) {
//				interval = 0;
//			} else if (interval > 255) {
//				interval = 255;
//			}
//			cycleBar.setProgress(time);
//			if (blink.blinkRate == 1) {
//				isGradient = true;
//				flashOrGradientBtn.setTag(1);
//				flashOrGradientBtn.performClick();
//			} else {
//				isGradient = false;
//				flashOrGradientBtn.setTag(2);
//				if (interval > 50) {
//					interval = 50;
//				}
//			}
//			flashBar.setProgress(interval);
//		} else {
//			isGradient = false;
//			flashOrGradientBtn.setTag(2);
//			flashOrGradientBtn.performClick();
//			flashBar.setProgress(0);
//			cycleBar.setProgress(0);
//		}
	}

	/**
	 * 获取传值
	 */
	private void setData() {
		Intent itent = getIntent();

		if (itent != null) {
			String key = getIntent()
					.getStringExtra(MessageModel.PISBASE_KEYSTR);
			infor = (PISxinColor) PISManager.getInstance().getPISObject(key);
//
//				isGroup = itent.getBooleanExtra("isgroup", false);
//				whiteValue = itent.getIntExtra("white", 0);
//				try {
//					if (isGroup) {
//						groupId = itent.getShortExtra(MessageModel.ACTIVITY_VALUE,
//								(short) 0);
//					} else {
//						if (getIntent() != null
//								&& getIntent().getStringExtra(MessageModel.PISBASE_KEYSTR) != null) {
//							String key = getIntent()
//									.getStringExtra(MessageModel.PISBASE_KEYSTR);
//							infor = (PISxinColor) PISManager.getInstance().getPISObject(key);
//						}
//					}
//				} catch (Exception ex) {
//					ex.printStackTrace();
//			}
		}
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		flashOrGradientBtn.setOnClickListener(this);
		if (isTimer) {
			saveBtn.setOnClickListener(this);
		}
		cycleBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				numberView.updateView(progress, cycleBar.getMax());
			}
		});
		flashBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (infor != null) {
					setLightOrder();
					mHandler.postDelayed(timer, DELAY_TIME);
				} else if (inforOnLED != null) {
					sendOrderOnLED();
					mHandler.postDelayed(timer, DELAY_TIME);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int max = flashBar.getMax();
				if (progress < 1) {
					setFlashTextColor(-1);
				} else if (2 * max / 3 <= progress) {
					setFlashTextColor(2);
				} else if (max / 3 <= progress) {
					setFlashTextColor(1);
				} else if (max / 3 > progress) {
					setFlashTextColor(0);
				}
			}
		});
		startColorBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					colorIndex = 1;
					return true;
				}
				return false;
			}
		});
		endColorBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					colorIndex = 2;
					return true;
				}
				return false;
			}
		});

		colorCircle.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				if (action == MotionEvent.ACTION_DOWN
						|| action == MotionEvent.ACTION_MOVE) {
					int x = (int) event.getX();
					int y = (int) event.getY();

					ColorCircle circleView = (ColorCircle) v;

					int pixelColor = 0;
					try {
						pixelColor = circleView.getPixelColorAt(x, y);
						LogUtils.i("Ryan",
								"LightSettingActivity-> ontouchListener(): pixelColor = "
										+ pixelColor);
					} catch (IndexOutOfBoundsException e) {
						return true;
					}
					if (Color.TRANSPARENT != pixelColor) {
						if (colorIndex == 1) {
							startColorValue = pixelColor;
							startColorBtn.changeColor(pixelColor);
							int red0 = Color.red(pixelColor);
							int green0 = Color.green(pixelColor);
							int blue0 = Color.blue(pixelColor);
							LogUtils.i(TAG, "start === R = " + red0 + ", g = "
									+ green0 + ", b = " + blue0);
						} else if (colorIndex == 2) {
							endColorValue = pixelColor;
							endColorBtn.changeColor(pixelColor);
							int red1 = Color.red(pixelColor);
							int green1 = Color.green(pixelColor);
							int blue1 = Color.blue(pixelColor);
							LogUtils.i(TAG, "end == R = " + red1 + ", g = "
									+ green1 + ", b = " + blue1);
						}
					}
					if (Color.alpha(pixelColor) < 0xFF) {
						return true;
					}
					circleView.setCursor(x, y);
					circleView.invalidate();
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		saveBtn = (Button) findViewById(R.id.title_finished);
		startColorBtn = (CircleView) findViewById(R.id.lighteffects_color_start_ibtn);
		endColorBtn = (CircleView) findViewById(R.id.lighteffects_color_end_ibtn);
		flashBar = (SeekBar) findViewById(R.id.lighteffects_blink_seekbar);
		cycleBar = (SeekBar) findViewById(R.id.lighteffects_cycle_seekbar);
		tvFlashMinddle = (TextView) findViewById(R.id.lighteffects_flash_middle);
		tvFlashSlow = (TextView) findViewById(R.id.lighteffects_flash_slow);
		tvFlashFast = (TextView) findViewById(R.id.lighteffects_flash_fast);
		colorCircle = (ColorCircle) findViewById(R.id.lighteffects_colorcircle);
		circleLayout = (RelativeLayout) findViewById(R.id.lighteffects_circle_layout);
		tvFlashSlow = (TextView) findViewById(R.id.lighteffects_flash_slow);
		tvFlashFast = (TextView) findViewById(R.id.lighteffects_flash_fast);
		tvFlashMinddle = (TextView) findViewById(R.id.lighteffects_flash_middle);
		flashBar = (SeekBar) findViewById(R.id.lighteffects_blink_seekbar);
		tvTip = (TextView) findViewById(R.id.lighteffects_tip_name);
		flashOrGradientBtn = (ImageView) findViewById(R.id.lighteffects_gradientbtn);
		numberView = (ProgressNumberView) findViewById(R.id.lightsetting_cycle_number);
		spaceView = findViewById(R.id.lighteffects_view);
		// 1代表默认的闪烁，2代表渐变
		flashOrGradientBtn.setTag(1);
		// 设置标题内容
		setTitle();
		changeFlashOrGradient(true);
	}

	/**
	 * 设置“快”、“中”、“慢”三个文字的颜色
	 * 
	 * @param index
	 *            2:慢 1：中 0：快
	 */
	private void setFlashTextColor(int index) {
		if (index == 2) {
			tvFlashSlow.setTextColor(0xFF017AFF);
			tvFlashMinddle.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
			tvFlashFast.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
		} else if (index == 1) {
			tvFlashSlow.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
			tvFlashMinddle.setTextColor(0xFF017AFF);
			tvFlashFast.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
		} else if (index == 0) {
			tvFlashSlow.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
			tvFlashMinddle.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
			tvFlashFast.setTextColor(0xFF017AFF);
		} else {
			tvFlashSlow.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
			tvFlashMinddle.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
			tvFlashFast.setTextColor(getResources().getColor(
					R.color.light_currentcolor_textcolor));
		}
	}

	/**
	 * 切换 渐变或者快闪的进度条
	 */
	private void changeFlashOrGradient(boolean isFlashed) {
		isGradient = !isFlashed;
		flashBar.setProgress(0);
	}

	/**
	 * 切换两侧的文字内容
	 */
	private void changeAsideText(boolean isFlashed) {
		if (isFlashed) {
			tvTip.setText(R.string.blink);
		} else {
			tvTip.setText(R.string.lightsetting_change_slowly);
		}
	}

	/**
	 * 设置标题的组件
	 */
	private void setTitle() {
		tvTitle.setText(R.string.lightsetting_effects);
		backBtn.setVisibility(View.VISIBLE);
		if (isTimer) {
			saveBtn.setVisibility(View.VISIBLE);
			saveBtn.setBackgroundColor(Color.TRANSPARENT);
			saveBtn.setText(R.string.save);
		}
	}

	/**
	 * 保存命令内容和命令字
	 */
	private void save() {
		cmdContent = null;
		cmd = PISxinColor.PIS_CMD_LIGHT_BLINK_SET;
		if (startColorValue == 0) {
			startColorValue = startColorBtn.getColor();
		}
		if (endColorValue == 0) {
			endColorValue = endColorBtn.getColor();
		}
		int[] startColors = Utils.getRGB(startColorValue);
		int[] endColors = Utils.getRGB(endColorValue);
		int rampRate = 0;
		if (isGradient) {
			rampRate = 1;
		}
		int time = cycleBar.getProgress();
		int interval = 0;
		if (isGradient) {
			if (interval < 5) {
				interval = 5;
			}
		} else {
			if (interval < 1) {
				interval = 1;
			}
		}
//		cmdContent = PISxinColor.getBlinkCmdOnLight(startColors[0],
//				startColors[1], startColors[2], whiteValue, endColors[0],
//				endColors[1], endColors[2], whiteValue, interval, time,
//				rampRate);
		ToastUtils.showToast(this, R.string.scene_detail_save_success);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.title_back:
			if (isTimer) {
				Intent it = new Intent();
				it.putExtra("cmd", cmd);
				it.putExtra("cmdContent", cmdContent);
				setResult(RESULT_OK, it);
			}
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		// 渐变闪烁的切换按钮
		case R.id.lighteffects_gradientbtn:
			int tag = (Integer) v.getTag();
			if (tag == 2) {
				changeAsideText(true);
				changeFlashOrGradient(true);
				v.setTag(1);
			} else {
				changeAsideText(false);
				changeFlashOrGradient(false);
				v.setTag(2);
			}
			break;
		case R.id.title_finished:
			save();
			break;
		default:
			break;
		}
	}

	private Runnable timer = new Runnable() {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(MSG_ORDER_FAILED);
		}
	};

	/**
	 * 设置单个灯的特效
	 */
	private void sendOrderOnLED() {
		if (inforOnLED != null) {
			int rampRate = 0;
			if (isGradient) {
				rampRate = 1;
			}
//			inforOnLED.setLightBlink(cold1, warm1, cold2, warm2,
//					flashBar.getProgress(), cycleBar.getProgress(), rampRate,
//					true);
		}
	}

	/**
	 * 设置单个灯的特效
	 */
	private void setLightOrder() {
		if (infor != null) {
			if (startColorValue == 0) {
				startColorValue = startColorBtn.getColor();
			}
			if (endColorValue == 0) {
				endColorValue = endColorBtn.getColor();
			}
			int[] startColors = Utils.getRGB(startColorValue);
			int[] endColors = Utils.getRGB(endColorValue);
			int rampRate = 0;
			if (isGradient) {
				rampRate = 1;
			}
			int time = cycleBar.getProgress();
			int interval = flashBar.getProgress();
			if (isGradient) {
				if (interval < 5) {
					interval = 5;
				}
			} else {
				if (interval < 1) {
					interval = 1;
				}
			}
			infor.request(infor.commitLightBlink(startColors[0], startColors[1],
						startColors[2], whiteValue, endColors[0], endColors[1],
						endColors[2], whiteValue, interval, time, rampRate));
//			if (!isGroup) {
//				infor.setLightBlink(startColors[0], startColors[1],
//						startColors[2], whiteValue, endColors[0], endColors[1],
//						endColors[2], whiteValue, interval, time, rampRate);
//			} else {
//				infor.setGroupLightBlink(groupId, startColors[0],
//						startColors[1], startColors[2], whiteValue,
//						endColors[0], endColors[1], endColors[2], whiteValue,
//						interval, time, rampRate);
//			}
		}
	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}

	@Override
	protected void onResume() {
		super.onResume();
//		groupInfor = PISManager.getInstance().getSomeoneGroup(groupId);
//		if (groupInfor != null) {
//			if (groupInfor.infors != null && groupInfor.infors.size() > 0) {
//				PISBase base = groupInfor.getPISBase();
//				if (base != null) {
//					infor = (PISLightRGB)groupInfor.getPISBase();
//				} else {
//					infor = null;
//				}
//			} else {
//				groupInfor.getGroupsOnDevice(groupInfor.groupId);
//			}
//		}
//		if (infor != null) {
//			infor.getLightBlink();
//		} else if (inforOnLED != null) {
//			inforOnLED.getLightBlink();
//		}
//		controller.setonFeedbackListener(listener);
//		manager.setOnPipaRequestStatusListener(this);
//		manager.setOnPISChangedListener(this);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (controller != null) {
			controller.setonFeedbackListener(null);
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if((reqType == PISMCSManager.PIS_MSG_MCM_GROUPSRVADD
//				|| reqType == PISMCSManager.PIS_MSG_MCM_GROUPSRVDEL
//				|| reqType == PISBase.PIS_CMD_GROUP_GET)
//				&& result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//			if (manager.getSomeoneGroup(groupId) != null) {
//				groupInfor = manager.getSomeoneGroup(groupId);
//				if (groupInfor != null && groupInfor.infors.size() > 0) {
//					PISBase base = groupInfor.getPISBase();
//					if (base != null) {
//						infor = (PISLightRGB)base;
//					}else{
//						infor = null;
//					}
//				}else{
//					infor = null;
//				}
//			}
//		}else if (pis.mT1 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T1
//				&& pis.mT2 == PISConstantDefine.PIS_BLUETOOTH_LIGHT_T2) {
//			if (reqType == PISLightRGB.PIS_CMD_LIGHT_BLINK_SET) {
//				if (result == PipaRequest.REQUEST_RESULT_ERROR) {
//					ToastUtils.showToast(LightEffectsActivity.this,
//							R.string.lighteffects_set_failed);
//					dialog.dismiss();
//					try {
//						mHandler.removeCallbacks(timer);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}else if(reqType == PISLightRGB.PIS_CMD_LIGHT_BLINK_SET){
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					dialog.dismiss();
//					mHandler.removeCallbacks(timer);
//				}
//			}else if(reqType == PISLightRGB.PIS_CMD_LIGHT_BLINK_GET){
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					setView();
//				}
//			}
//		}
//	}
}