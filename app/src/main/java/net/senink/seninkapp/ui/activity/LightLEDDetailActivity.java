package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinLight;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.ByteUtilLittleEndian;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.sqlite.SceneDao;
import net.senink.seninkapp.ui.cache.CacheManager;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.util.ByteUtilBigEndian;
import net.senink.seninkapp.ui.util.ByteUtilLittleEndian;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.PictureUtils;
import net.senink.seninkapp.ui.util.RGBConfigUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;

/**
 * 用设置灯或者灯组的界面 （在主界面直接进入此界面）
 * 
 * @author zhaojunfeng
 * @date 2015-12-12
 */
public class LightLEDDetailActivity extends BaseActivity implements
		View.OnClickListener{
	private static final String TAG = "LightLEDDetailActivity";
	private final static int MSG_TIMER_LIGHTNESS = 10;
	private final static int MSG_TIMER_COLDWARM = 11;
	private final static int MSG_TIME_OUT = 1;
	// 开关命令超时
	private final static int MSG_SWITCH_TIMEOUT = 2;
	private final static int LOADING_MAX_TIME = 10000;
	// 界面跳转时的请求码
	public final static int REQUEST_CODE = 2000;
	// 界面跳转到场景模式设置界面时的请求码
	public final static int REQUEST_SCENE_CODE = 2001;
	public final static int REQUEST_SETTING_CODE = 2002;
	// 发送修改冷暖色命令
	private final static int MSG_SEND_ORDER_COLDWARM = 5000;
	// 延迟发送冷暖色命令
	private final static int DELEY_TIME = 300;

	// 发送第一条冷暖色命令的起始时间
	private long START_TIME = 0;
	private boolean lightness_timer_enable = false;
	private boolean coldwarm_timer_enable = false;
	// 标题名称
	private TextView tvTitle;
	// 标题的中的返回按钮
	private Button backBtn;
	// 开关状态
	private CheckBox switcher;
	// 亮度调节器
	private SeekBar whiteBar;
	// 加载框
	private ImageView ivLoading;
	// 加载动画
	private AnimationDrawable anima;
	// 冷暖度调节器
	private SeekBar coldWarmBar;
	private ImageButton moreBtn;
	private RelativeLayout scene1Layout, scene2Layout, scene3Layout,
			scene4Layout, scene5Layout;
	private TextView tvSceneName1, tvSceneName2, tvSceneName3, tvSceneName4,
			tvSceneName5;
	private ImageView ivSelected1, ivSelected2, ivSelected3, ivSelected4,
			ivSelected5;
	private ImageView ivLoadding1, ivLoadding2, ivLoadding3, ivLoadding4,
			ivLoadding5;
	private AnimationDrawable anima1, anima2, anima3, anima4, anima5;
	// 白光滚动轴左边的图片
	private Button ivLeftOnBright;
	// 白光滚动轴右边的图片
	private ImageView ivRightOnBright;
	// 冷暖色滚动轴左边的图片
	private Button ivLeftOnColdOrWarm;
	// 冷暖色滚动轴右边的图片
	private ImageView ivRightOnColdOrWarm;
	// 某个组的信息
	private PISBase groupInfor = null;
	// 设置按钮
	private Button settingBtn;
	// 保存按钮
	private Button saveBtn;
	// 是否是分组的
	private boolean isGroup = false;
	// 组id
	private short groupId = -1;
	private PISManager manager;
	// 传递过来的pisbase对象
	private PISXinLight inforOnLED;
	// 是否是第一次进入该界面
	private boolean isFirst = true;
	// 开关状态改变是否发送命令
	private boolean isSended = true;
	// 蓝牙管理器
	private MeshController controller;
	// 缓存管理器
	private CacheManager cacheManager;
	// 常用的几种场景模式
	private List<SceneBean> scenes;
	// 存放场景模式的dao类
	private SceneDao mSceneDao;
	// 是否是定时器界面跳转过来
	private boolean isTimer;
	// 需要提交定时器的命令字
	private short cmd;
	// 需要提交定时器的命令内容
	private byte[] cmdContent;
	// 最近一次保存的命令字
	private short tempCmd;
	// 最近一次保存的内容
	private byte[] tempCmdContent;
	// 被选中的中场景模式
	private int selectedIndex = -1;
	// 统计开关命令ack返回的数量
	private int countOnStatus = 0;
	// 统计设置颜色命令ack返回的数量
	private int countOnColdWarm = 0;
	// 传值的pisbase对象的piskeystring字符串
	private String key = null;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TIME_OUT:
				stopLoadding(false);
				setEnable(true);
				break;
			case MSG_SWITCH_TIMEOUT:
				removeMessages(MSG_SWITCH_TIMEOUT);
				hideLoading();
				ToastUtils.showToast(LightLEDDetailActivity.this,
						R.string.switch_set_failed);
				break;
			case MSG_SEND_ORDER_COLDWARM:
				sendColdWarmOrder(false);
				break;
				case MSG_TIMER_LIGHTNESS:
					if (lightness_timer_enable)
						mHandler.sendEmptyMessageDelayed(MSG_TIMER_LIGHTNESS, 300);
					sendColdWarmOrder(false);
					break;
				case MSG_TIMER_COLDWARM:
					if (coldwarm_timer_enable)
						mHandler.sendEmptyMessageDelayed(MSG_TIMER_COLDWARM, 300);
					sendColdWarmOrder(false);
					break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lightleddetail);
		manager = PISManager.getInstance();
		cacheManager = CacheManager.getInstance();
		mSceneDao = new SceneDao(this);
		initView();
		setData();
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setView();
		setScenes();

		//从设备更新数据
		PipaRequest req = inforOnLED.updateLightStatus();
		req.setRetry(2);
		req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(PipaRequest req) {

			}

			@Override
			public void onRequestResult(PipaRequest req) {
				if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
					updateView();
			}
		});
		inforOnLED.request(req);

		if (inforOnLED.getName() == null || inforOnLED.getName().length() == 0){
			PipaRequest nameReq = inforOnLED.updatePISInfo();
			nameReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
				@Override
				public void onRequestStart(PipaRequest req) {

				}

				@Override
				public void onRequestResult(PipaRequest req) {
					if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
						updateView();
				}
			});
			inforOnLED.request(nameReq);
		}

	}
	/**
	 * 获取传值
	 */
	private void setData() {
		Intent intent = getIntent();
		if (intent != null) {
			String key = intent.getStringExtra("keystring");
			if (key != null){
				inforOnLED = (PISXinLight)manager.getPISObject(key);
			}
		}
	}


	/**
	 * 获取场景信息，如果缓存中不存在则从数据库中取，如果缓存中存在则直接使用数据
	 */
	private void setScenes() {
		if (null == cacheManager.getLEDUsedScenes()) {
			cacheManager.setScenes(mSceneDao.getAllScenes(PISManager.getInstance().getUserObject().loginUser));
		}
		scenes = cacheManager.getLEDUsedScenes();
		if (null == scenes) {
			scenes = new ArrayList<SceneBean>();
		}
		addDefaultScenes();
		setScenesView();
	}

	private void addDefaultScenes() {
		int size = 5 - scenes.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				SceneBean bean = new SceneBean();
				bean.t1 = PISConstantDefine.PIS_MAJOR_LIGHT;
				bean.t2 = PISConstantDefine.PIS_LIGHT_LIGHT;
				if (i == 0) {
					bean.sceneName = "场景一";
					bean.picture = "0";
					bean.bright = 255;
					bean.coldwarm = 0;
				} else if (i == 1) {
					bean.sceneName = "场景二";
					bean.picture = "1";
					bean.bright = 255;
					bean.coldwarm = 255;
				} else if (i == 2) {
					bean.sceneName = "场景三";
					bean.picture = "2";
					bean.bright = 127;
					bean.coldwarm = 127;
				} else if (i == 3) {
					bean.sceneName = "场景四";
					bean.picture = "3";
					bean.bright = 12;
					bean.coldwarm = 12;
				} else if (i == 4) {
					bean.sceneName = "场景五";
					bean.picture = "4";
					bean.bright = 50;
					bean.coldwarm = 50;
				}
				bean.used = i;
				bean.user = PISManager.getInstance().getUserObject().loginUser;
				bean.rgb = 0;
				bean.id = (int) mSceneDao.insertScene(bean);
				if (size == 5) {
					scenes.add(bean);
				} else {
					scenes.add(i, bean);
				}
			}
			cacheManager.setScenes(mSceneDao.getAllScenes(PISManager.getInstance().getUserObject().loginUser));
		}
	}

	private void setScenesView() {
		if (null == scenes || scenes.size() == 0) {
			scene1Layout.setVisibility(View.INVISIBLE);
			scene2Layout.setVisibility(View.INVISIBLE);
			scene3Layout.setVisibility(View.INVISIBLE);
			scene4Layout.setVisibility(View.INVISIBLE);
			scene5Layout.setVisibility(View.INVISIBLE);
			tvSceneName1.setText("");
			tvSceneName2.setText("");
			tvSceneName3.setText("");
			tvSceneName4.setText("");
			tvSceneName5.setText("");
		} else {
			int size = scenes.size();
			SceneBean bean0 = null, bean1 = null, bean2 = null, bean3 = null, bean4 = null;
			if (size > 0) bean0 = scenes.get(0);
			if (size > 0) bean1 = scenes.get(1);
			if (size > 0) bean2 = scenes.get(2);
			if (size > 0) bean3 = scenes.get(3);
			if (size > 0) bean4 = scenes.get(4);
			setSceneBtn(scene1Layout, tvSceneName1, bean0);
			setSceneBtn(scene2Layout, tvSceneName2, bean1);
			setSceneBtn(scene3Layout, tvSceneName3, bean2);
			setSceneBtn(scene4Layout, tvSceneName4, bean3);
			setSceneBtn(scene5Layout, tvSceneName5, bean4);
		}
	}

	@SuppressWarnings("deprecation")
	private void setSceneBtn(RelativeLayout ib, TextView tv, SceneBean bean) {
		if (bean != null) {
			Bitmap bm = null;
			if (!TextUtils.isEmpty(bean.picture) && bean.picture.length() > 1) {
				Bitmap defaultBm = ((BitmapDrawable) getResources()
						.getDrawable(R.drawable.icon_default_big_scene1))
						.getBitmap();
				bm = PictureUtils.scalePicture(bean.picture,
						defaultBm.getWidth(), defaultBm.getHeight());
				bm = SDCardUtils.getRoundRectBitmap(bm, 10.0f);
			}
			if (null == bm) {
				bm = PictureUtils.getBigDefaultBitmap(this, bean.picture);
			}
			try {
				ib.setBackground(new BitmapDrawable(getResources(), bm));
			} catch (Exception e) {
				ib.setBackgroundDrawable(new BitmapDrawable(getResources(), bm));
			}

			ib.setVisibility(View.VISIBLE);
			tv.setText(bean.sceneName == null ? "" : bean.sceneName);
		} else {
			ib.setVisibility(View.INVISIBLE);
			tv.setText("");
		}
		ib.setTag(bean);
	}

	/**
	 * 发送冷暖色命令
	 * 
	 * @param isScened
	 *            是否是场景命令
	 */
	private void sendColdWarmOrder(boolean isScened) {
		countOnColdWarm = 0;
		int white = whiteBar.getProgress();
		if (white < 2) {
			white = 2;
		}
		int progress = coldWarmBar.getProgress();
		int warm = white * progress / 255;
		int cold = white - warm;

		if (isTimer) {
			setTimerCmdOnColor(cold, warm);
			return;
		}
		if (!switcher.isChecked()) {
			switcher.setChecked(true);
		}
		PipaRequest cwReq = inforOnLED.commitLightCW(cold, warm, true);
		if (isScened){
			cwReq.NeedAck = true;
			cwReq.setRetry(2);
			cwReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
				@Override
				public void onRequestStart(PipaRequest req) {
					startLoadding();
				}

				@Override
				public void onRequestResult(PipaRequest req) {
					mHandler.removeMessages(MSG_TIME_OUT);
					if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
						stopLoadding(true);
					else
						stopLoadding(false);
					setEnable(true);
				}
			});
		}
		inforOnLED.request(cwReq);
	}

	/**
	 * 设置监听器
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void setListener() {
		// cancelBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		ivLeftOnBright.setOnClickListener(this);
		ivRightOnBright.setOnClickListener(this);
		ivLeftOnColdOrWarm.setOnClickListener(this);
		ivRightOnColdOrWarm.setOnClickListener(this);
		scene1Layout.setOnClickListener(this);
		scene2Layout.setOnClickListener(this);
		scene3Layout.setOnClickListener(this);
		scene4Layout.setOnClickListener(this);
		scene5Layout.setOnClickListener(this);
		moreBtn.setOnClickListener(this);
		if (isTimer) {
			saveBtn.setOnClickListener(this);
		} else {
			settingBtn.setOnClickListener(this);
		}
		switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!switcher.isPressed())
					return;
				PipaRequest btnReq = inforOnLED.commitLightStatus(switcher.isChecked());
				btnReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
					@Override
					public void onRequestStart(PipaRequest req) {
						showLoading();
					}

					@Override
					public void onRequestResult(PipaRequest req) {
						hideLoading();
						PISXinLight light = (PISXinLight)req.object;
						if (inforOnLED.ServiceType != PISBase.SERVICE_TYPE_GROUP)
							switcher.setChecked(light.getLightStatus()==PISXinLight.XINLIGHT_STATUS_ON);
						if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED){
							ToastUtils.showToast(LightLEDDetailActivity.this,
									R.string.switch_set_failed);
						}
					}
				});
				inforOnLED.request(btnReq);
			}
		});
		whiteBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				sendColdWarmOrder(false);
				lightness_timer_enable = false;
				mHandler.removeMessages(MSG_TIMER_LIGHTNESS);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				lightness_timer_enable = true;
				mHandler.removeMessages(MSG_TIMER_LIGHTNESS);
				mHandler.sendEmptyMessage(MSG_TIMER_LIGHTNESS);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
		coldWarmBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				sendColdWarmOrder(false);
				coldwarm_timer_enable = false;
				mHandler.removeMessages(MSG_TIMER_COLDWARM);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				coldwarm_timer_enable = true;
				mHandler.removeMessages(MSG_TIMER_COLDWARM);
				mHandler.sendEmptyMessage(MSG_TIMER_COLDWARM);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
	}

	/**
	 * 设置白光进度条的进度
	 * 
	 * @param isLeft
	 *            是左边点击还是右边
	 */
	private void setProgressOnWhite(boolean isLeft) {
		int progress = whiteBar.getProgress();
		int max = whiteBar.getMax();
		if (isLeft) {
			progress = progress - max / 50;
		} else {
			progress += max / 50;
		}
		if (progress < 0) {
			progress = 0;
		} else if (progress > max) {
			progress = max;
		}
		sendColdWarmOrder(false);
		whiteBar.setProgress(progress);
	}

	/**
	 * 设置冷暖色进度条的进度
	 * 
	 * @param isLeft
	 *            是左边点击还是右边
	 */
	private void setProgressOnColdWarm(boolean isLeft) {
		int progress = coldWarmBar.getProgress();
		int max = coldWarmBar.getMax();
		if (isLeft) {
			progress = progress - max / 50;
		} else {
			progress += max / 50;
		}
		if (progress < 0) {
			progress = 0;
		} else if (progress > max) {
			progress = max;
		}
		coldWarmBar.setProgress(progress);
		sendColdWarmOrder(false);

	}

	/**
	 * 设置加载动画
	 */
	private void showLoading() {
		ivLoading.setVisibility(View.VISIBLE);
		anima.start();
	}

	/**
	 * 设置加载动画
	 */
	private void hideLoading() {
		ivLoading.setVisibility(View.GONE);
		anima.stop();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		switcher = (CheckBox) findViewById(R.id.light_switcher);
		whiteBar = (SeekBar) findViewById(R.id.light_bright_seekbar);
		settingBtn = (Button) findViewById(R.id.title_setting);
		saveBtn = (Button) findViewById(R.id.title_finished);
		coldWarmBar = (SeekBar) findViewById(R.id.light_coldwarm_seekbar);
		ivLeftOnBright = (Button) findViewById(R.id.light_bright_left);
		ivRightOnBright = (ImageView) findViewById(R.id.light_bright_right);
		ivLeftOnColdOrWarm = (Button) findViewById(R.id.light_coldwarm_left);
		ivRightOnColdOrWarm = (ImageView) findViewById(R.id.light_coldwarm_right);
		ivSelected1 = (ImageView) findViewById(R.id.light_scene1_selected);
		ivSelected2 = (ImageView) findViewById(R.id.light_scene2_selected);
		ivSelected3 = (ImageView) findViewById(R.id.light_scene3_selected);
		ivSelected4 = (ImageView) findViewById(R.id.light_scene4_selected);
		ivSelected5 = (ImageView) findViewById(R.id.light_scene5_selected);
		ivLoadding1 = (ImageView) findViewById(R.id.light_scene1_loadding);
		ivLoadding2 = (ImageView) findViewById(R.id.light_scene2_loadding);
		ivLoadding3 = (ImageView) findViewById(R.id.light_scene3_loadding);
		ivLoadding4 = (ImageView) findViewById(R.id.light_scene4_loadding);
		ivLoadding5 = (ImageView) findViewById(R.id.light_scene5_loadding);
		scene1Layout = (RelativeLayout) findViewById(R.id.light_scene1_layout);
		scene2Layout = (RelativeLayout) findViewById(R.id.light_scene2_layout);
		scene3Layout = (RelativeLayout) findViewById(R.id.light_scene3_layout);
		scene4Layout = (RelativeLayout) findViewById(R.id.light_scene4_layout);
		scene5Layout = (RelativeLayout) findViewById(R.id.light_scene5_layout);
		moreBtn = (ImageButton) findViewById(R.id.light_sceneMore_layout);
		tvSceneName1 = (TextView) findViewById(R.id.light_sceneName1);
		tvSceneName2 = (TextView) findViewById(R.id.light_sceneName2);
		tvSceneName3 = (TextView) findViewById(R.id.light_sceneName3);
		tvSceneName4 = (TextView) findViewById(R.id.light_sceneName4);
		tvSceneName5 = (TextView) findViewById(R.id.light_sceneName5);
		ivLoading = (ImageView) findViewById(R.id.light_loadding);
		setAnimaDrawable();
	}

	private void setAnimaDrawable() {
		anima1 = (AnimationDrawable) ivLoadding1.getBackground();
		anima2 = (AnimationDrawable) ivLoadding2.getBackground();
		anima3 = (AnimationDrawable) ivLoadding3.getBackground();
		anima4 = (AnimationDrawable) ivLoadding4.getBackground();
		anima5 = (AnimationDrawable) ivLoadding5.getBackground();
		anima = (AnimationDrawable) ivLoading.getBackground();
	}

	private boolean isLoadding = false;

	private void startLoadding() {
		isLoadding = true;
		if (selectedIndex == 0) {
			ivLoadding1.setVisibility(View.VISIBLE);
			anima1.start();
		} else if (selectedIndex == 1) {
			ivLoadding2.setVisibility(View.VISIBLE);
			anima2.start();
		} else if (selectedIndex == 2) {
			ivLoadding3.setVisibility(View.VISIBLE);
			anima3.start();
		} else if (selectedIndex == 3) {
			ivLoadding4.setVisibility(View.VISIBLE);
			anima4.start();
		} else if (selectedIndex == 4) {
			ivLoadding5.setVisibility(View.VISIBLE);
			anima5.start();
		}
		mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, LOADING_MAX_TIME);
	}

	private void stopLoadding(boolean success) {
		isLoadding = false;
		if (inforOnLED.ServiceType == PISBase.SERVICE_TYPE_GROUP) {
			success = true;
		}
		if (selectedIndex == 0) {
			anima1.stop();
			ivLoadding1.setVisibility(View.GONE);
			if (success) {
				ivSelected1.setVisibility(View.VISIBLE);
				ivSelected2.setVisibility(View.GONE);
				ivSelected3.setVisibility(View.GONE);
				ivSelected4.setVisibility(View.GONE);
				ivSelected5.setVisibility(View.GONE);
			} else {
				ivSelected1.setVisibility(View.GONE);
			}
		} else if (selectedIndex == 1) {
			anima2.stop();
			ivLoadding2.setVisibility(View.GONE);
			if (success) {
				ivSelected2.setVisibility(View.VISIBLE);
				ivSelected1.setVisibility(View.GONE);
				ivSelected3.setVisibility(View.GONE);
				ivSelected4.setVisibility(View.GONE);
				ivSelected5.setVisibility(View.GONE);
			} else {
				ivSelected2.setVisibility(View.GONE);
			}
		} else if (selectedIndex == 2) {
			anima3.stop();
			ivLoadding3.setVisibility(View.GONE);
			if (success) {
				ivSelected3.setVisibility(View.VISIBLE);
				ivSelected1.setVisibility(View.GONE);
				ivSelected2.setVisibility(View.GONE);
				ivSelected4.setVisibility(View.GONE);
				ivSelected5.setVisibility(View.GONE);
			} else {
				ivSelected3.setVisibility(View.GONE);
			}
		} else if (selectedIndex == 3) {
			anima4.stop();
			ivLoadding4.setVisibility(View.GONE);
			if (success) {
				ivSelected4.setVisibility(View.VISIBLE);
				ivSelected1.setVisibility(View.GONE);
				ivSelected2.setVisibility(View.GONE);
				ivSelected3.setVisibility(View.GONE);
				ivSelected5.setVisibility(View.GONE);
			} else {
				ivSelected4.setVisibility(View.GONE);
			}
		} else if (selectedIndex == 4) {
			anima5.stop();
			ivLoadding5.setVisibility(View.GONE);
			if (success) {
				ivSelected5.setVisibility(View.VISIBLE);
				ivSelected1.setVisibility(View.GONE);
				ivSelected2.setVisibility(View.GONE);
				ivSelected3.setVisibility(View.GONE);
				ivSelected4.setVisibility(View.GONE);
			} else {
				ivSelected5.setVisibility(View.GONE);
			}
		}
	}

	private void setEnable(boolean enable) {
		scene1Layout.setEnabled(enable);
		scene2Layout.setEnabled(enable);
		scene3Layout.setEnabled(enable);
		scene4Layout.setEnabled(enable);
		scene5Layout.setEnabled(enable);
	}

	/**
	 * 设置标题的组件
	 */
	private void setView() {
		updateView();
		backBtn.setVisibility(View.VISIBLE);
//		if (isTimer) {
//			saveBtn.setVisibility(View.VISIBLE);
//			saveBtn.setBackgroundColor(Color.TRANSPARENT);
//			saveBtn.setText(R.string.save);
//		} else {
//			settingBtn.setVisibility(View.VISIBLE);
//		}
		settingBtn.setVisibility(View.VISIBLE);
		setWhiteBar();
	}

	/**
	 * 设置白光滚动轴的最大值
	 */
	private void setWhiteBar() {
		whiteBar.setMax(RGBConfigUtils.MAX_WHITE);
	}

	/**
	 * 更新界面
	 */
	private void updateView() {
		if (inforOnLED != null) {
			int progress = inforOnLED.getCold() + inforOnLED.getWarm();
			int cold = inforOnLED.getCold();
			if (progress > 255) {
				progress = 255;
			} else if (progress < 0) {
				progress = 0;
			}
			if (cold > 255) {
				cold = 255;
			} else if (cold < 0) {
				cold = 0;
			}
			isSended = false;
			whiteBar.setProgress(progress);
			coldWarmBar.setProgress(cold);
			switcher.setChecked(inforOnLED.getLightStatus() == PISXinLight.XINLIGHT_STATUS_ON);
			isSended = true;
			tvTitle.setText(inforOnLED.getName() == null ? ""
					: inforOnLED.getName());
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.light_bright_left:
			setProgressOnWhite(true);
			break;
		case R.id.light_bright_right:
			setProgressOnWhite(false);
			break;
		case R.id.light_coldwarm_left:
			setProgressOnColdWarm(true);
			break;
		case R.id.light_coldwarm_right:
			setProgressOnColdWarm(false);
			break;
		case R.id.light_scene1_layout:
			if (!isLoadding) {
				selectedIndex = 0;
				if (!isTimer) {
					ivSelected1.setVisibility(View.GONE);
					setEnable(false);
				}
				setSceneMode(scene1Layout);
			}
			break;
		case R.id.light_scene2_layout:
			if (!isLoadding) {
				selectedIndex = 1;
				if (!isTimer) {
					ivSelected2.setVisibility(View.GONE);
//					startLoadding();
					setEnable(false);
				}
				setSceneMode(scene2Layout);
			}
			break;
		case R.id.light_scene3_layout:
			if (!isLoadding) {
				selectedIndex = 2;
				if (!isTimer) {
					ivSelected3.setVisibility(View.GONE);
//					startLoadding();
					setEnable(false);
				}
				setSceneMode(scene3Layout);
			}
			break;

		case R.id.light_scene4_layout:
			if (!isLoadding) {
				selectedIndex = 3;
				if (!isTimer) {
					ivSelected4.setVisibility(View.GONE);
//					startLoadding();
					setEnable(false);
				}
				setSceneMode(scene4Layout);
			}
			break;
		case R.id.light_scene5_layout:
			if (!isLoadding) {
				selectedIndex = 4;
				if (!isTimer) {
					ivSelected5.setVisibility(View.GONE);
//					startLoadding();
					setEnable(false);
				}
				setSceneMode(scene5Layout);
			}
			break;

		case R.id.light_sceneMore_layout:
			if (!isTimer) {
				intent = new Intent(this, SceneSettingActivity.class);
				if (inforOnLED != null) {
					intent.putExtra(MessageModel.ACTIVITY_VALUE,
							inforOnLED.getPISKeyString());
				}
//				intent.putExtra("isgroup", isGroup);
//				intent.putExtra("groupid", groupId);
				startActivityForResult(intent, REQUEST_SCENE_CODE);
				overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			}
			break;
		// 返回按钮
		case R.id.title_back:
			if (isTimer) {
				if (cmdContent != null) {
					Intent it = new Intent();
					if (isGroup) {
						if (groupInfor != null) {
							short groupId = (short)groupInfor.getShortAddr();
							byte[] ids = ByteUtilBigEndian
									.shortToByteArr(groupId);
							groupId = ByteUtilLittleEndian.byteArrToShort(ids);
							it.putExtra("servId", groupId);
							it.putExtra("mac", "FFFFFFFFFFFFFFFF");
						}
					} else {
						if (inforOnLED != null) {
							it.putExtra("mac", inforOnLED.getDeviceObject().getMacString());
							it.putExtra("servId", inforOnLED.getServiceId());
						}
					}
					it.putExtra("cmd", cmd);
					it.putExtra("cmdContent", cmdContent);
					setResult(RESULT_OK, it);
				}
			} else {
//				if (isGroup && groupInfor != null) {
//					manager.addDeviceGroup(groupInfor);
//				} else {
//					inforOnLED.mOnOff = switcher.isChecked();
//					manager.upgradePISBaseLight(inforOnLED);
//				}
			}
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.title_finished:
			save();
			break;
		case R.id.title_setting:
			if (inforOnLED != null) {
				intent = new Intent(this, LightSettingActivity.class);
				intent.putExtra(MessageModel.PISBASE_KEYSTR,
						inforOnLED.getPISKeyString());
				startActivityForResult(intent, REQUEST_SETTING_CODE);
				overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 保存命令内容和命令字
	 */
	private void save() {
		cmdContent = null;
		cmd = tempCmd;
		cmdContent = tempCmdContent;
		ToastUtils.showToast(this, R.string.scene_detail_save_success);
	}

	private void setSceneMode(RelativeLayout btn) {
		if (btn.getTag() != null) {
			SceneBean bean = (SceneBean) btn.getTag();
			isSended = false;
			whiteBar.setProgress(bean.bright);
			coldWarmBar.setProgress(bean.coldwarm);
			sendColdWarmOrder(true);
		}
	}

//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SCENE_CODE && resultCode == RESULT_OK) {
			SceneBean bean = (SceneBean) data.getSerializableExtra("scene");
			if (bean.t1 == PISConstantDefine.PIS_MAJOR_LIGHT
					&& bean.t2 == PISConstantDefine.PIS_LIGHT_LIGHT
					&& inforOnLED != null) {
				whiteBar.setProgress(bean.bright);
				coldWarmBar.setProgress(bean.coldwarm);
				sendColdWarmOrder(false);
			}
			setScenes();
		}
		if (requestCode == REQUEST_SETTING_CODE && resultCode == RESULT_OK){
			int result = data.getIntExtra("result", -1);
			if (result == 3){
				finish();
			}else{
				tvTitle.setText(inforOnLED.getName());
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 缓存定时器需要的开关命令和命令内容
	 */
	private void setTimerCmdOnSwitch() {
		tempCmd = PISXinLight.PIS_CMD_LED_ONOFF_SET;
		tempCmdContent = new byte[1];
		if (switcher.isChecked()) {
			tempCmdContent[0] = (byte) (0x01 & 0x0f);
		} else {
			tempCmdContent[0] = (byte) (0x00 & 0x0f);
		}
	}

	/**
	 * 缓存定时器需要的冷暖色命令和命令内容
	 */
	private void setTimerCmdOnColor(int cold, int warm) {
		tempCmdContent = null;
		tempCmd = PISXinLight.PIS_CMD_LED_COLDWARM_SET;
//		tempCmdContent = PISLightLED.getCmdContentOnColdWarm(cold, warm, true);
	}



	private void setInfor() {
//		if (!TextUtils.isEmpty(key) && PISManager.cacheMap.get(key) != null) {
//			PISBase base = PISManager.cacheMap.get(key);
//			inforOnLED = (PISXinLight) base;
//			inforOnLED.getLightState(true);
//		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isTimer = false;
		mHandler.removeMessages(MSG_SWITCH_TIMEOUT);
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

}
