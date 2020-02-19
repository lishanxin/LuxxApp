package net.senink.seninkapp.ui.activity;

import java.util.List;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.view.CameraPreview;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISMCSManager.DeviceInfo;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;

import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.StepsView;

/**
 * 用于添加排插
 * 
 * @author zhaojunfeng
 * @date 2015-12-22
 */
public class AddSwitchActivity extends BaseActivity implements
		View.OnClickListener {
	private final static String TAG = "AddSwitchActivity";
	public final static int MSG_START_BINDING = 10;
	public final static int MSG_START_CONFIGING = 11;
	public final static int MSG_CONFIG_SUCCESS = 12;
	public final static int MSG_CONFIG_FAILED = 13;
	public final static int MSG_LINE_INIT = 14;
	private static final int MSG_DEVBIND_SUCCESS = 15;
	private static final int MSG_DEVBIND_FAILED = 16;
	// 返回按钮
	private Button backBtn;
	// 四个步骤的提示
	private TextView tvStep1, tvStep2, tvStep3, tvStep4;
	// 为绑定设备列表下的空白
	private View spaceView;
	// 确定按钮
	private Button okBtn;
	// 取消按钮
	private Button cancelBtn;
	// 动画组件
	private ImageView ivAnima2, ivAnima3;
	// 扫描二维码布局
	private FrameLayout scanLayout;
	// 输入wifi和密码的布局
	private RelativeLayout inputLayout;
	// ssid输入框
	private EditText etSSID;
	// KEY输入框
	private EditText etKey;
	private Camera mCamera;
	private CameraPreview mPreview;
	// 动画
	private AnimationDrawable anima2, anima3;
	// 标题
	private TextView tvTitle;
	// 进度线
	private StepsView stepsView;
	// 进度线右边的根布局
	private RelativeLayout contentLayout;
	// 进度线右边的根布局的初始高度
	private int originalHeight = 0;
	// 当前进度的颜色值
	private int currentColor = 0xff3583d8;
	private PISManager manager;
	private PISMCSManager mcm;
	// 已绑定设备的mac地址
	private List<String> mBindedDeviceMacList;
	// wifi的ssid
	private String connectedSSID;
	// wifi的key
	private String connectedpassword;
	private int failedCount = 0;
	// 扫描到的mac地址
	private String mac;
	private ImageScanner scanner;
	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LINE_INIT:
				setSteps(0, true);
				setVisiabilityOnScanLayout(true);
				setVisiabilityOnInputLayout(false);
				setVisiabilityOnSpaceView(false);
				startScan();
				break;
			case MSG_DEVBIND_SUCCESS:
				mHandler.removeMessages(MSG_DEVBIND_FAILED);
				setSteps(1, true);
				startAnima2();
				setVisiabilityOnInputLayout(false);
				setVisiabilityOnSpaceView(true);
				mHandler.sendEmptyMessage(MSG_START_BINDING);
				break;
			case MSG_DEVBIND_FAILED:
				mHandler.removeMessages(MSG_DEVBIND_FAILED);
				stopAnima2();
				setVisiabilityOnSpaceView(true);
				setVisiabilityOnInputLayout(false);
				setVisiabilityOnScanLayout(false);
				setSteps(1, false);
				break;
			case MSG_START_BINDING:
				mHandler.removeMessages(MSG_START_BINDING);
				setVisiabilityOnSpaceView(true);
				startAnima2();
				mHandler.sendEmptyMessage(MSG_START_CONFIGING);
				break;
			case MSG_START_CONFIGING:
				stopAnima2();
				startAnima3();
				setSteps(2, true);
				mHandler.sendEmptyMessage(1000);
				break;
			case MSG_CONFIG_SUCCESS:
				mHandler.removeMessages(MSG_DEVBIND_FAILED);
				stopAnima3();
				setSteps(3, true);
				setVisiabilityOnSpaceView(true);
				ToastUtils.showToast(AddSwitchActivity.this, R.string.addswitch_add_success);
				Intent it = new Intent(AddSwitchActivity.this,AddDevicesActivity.class);
				AddSwitchActivity.this.startActivity(it);
				AddSwitchActivity.this.finish();
				break;
			case MSG_CONFIG_FAILED:
				stopAnima3();
				mHandler.removeMessages(MSG_DEVBIND_FAILED);
				setVisiabilityOnSpaceView(true);
				setSteps(3, false);
				break;
			case 1000:
//				if (time++ < 19) {
//					// 发20次easyconfig
//					manager.easyConfigSsId(connectedSSID, connectedpassword);
//				} else if (time >= 20) {
//					total++;
//					time = 0;
//					manager.refresh();
//				}

				// 一共发（200+400+600+800+1000）＊20（次easyconfig）＊3(次discovery)
				// =180000毫秒 = 180秒

				if (total < 15) {
					if (total < 3) {
						sendEmptyMessageDelayed(1000, 200);
					} else if (total < 6) {
						sendEmptyMessageDelayed(1000, 400);
					} else if (total < 9) {
						sendEmptyMessageDelayed(1000, 600);
					} else if (total < 12) {
						sendEmptyMessageDelayed(1000, 800);
					} else {
						sendEmptyMessageDelayed(1000, 1000);
					}
				} else if (total >= 15) {
					// 绑定失败
					total = 888;
					time = 888;
					sendEmptyMessageDelayed(1001, 1000);
				}
				LogUtils.i(TAG, "=======total = " + total);
				break;
			case 1001:
				// 绑定失败
				if (total != 999) {
					mHandler.sendEmptyMessage(MSG_CONFIG_FAILED);
				}
				LogUtils.i(TAG, "=======1001");
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addswitch);
		manager = PISManager.getInstance();
		mcm = manager.getMCSObject();
		setData();
		initView();
		setCameraView();
		setListener();
	}

	private void setCameraView() {
		mCamera = getCameraInstance();
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
		mPreview = new CameraPreview(this,
				mCamera, previewCb, autoFocusCB);
		FrameLayout preview = (FrameLayout) findViewById(R.id.addswitch_scan_layout);
		preview.addView(mPreview);
		startScan();
	}

	private void startAnima2() {
		if (null == anima2) {
			anima2 = (AnimationDrawable) ivAnima2.getBackground();
		}
		anima2.start();
		ivAnima2.setVisibility(View.VISIBLE);
	}

	private void stopAnima2() {
		if (anima2 != null) {
			anima2.stop();
		}
		ivAnima2.setVisibility(View.GONE);
	}

	private void startAnima3() {
		if (null == anima3) {
			anima3 = (AnimationDrawable) ivAnima3.getBackground();
		}
		anima3.start();
		ivAnima3.setVisibility(View.VISIBLE);
	}

	private void stopAnima3() {
		if (anima3 != null) {
			anima3.stop();
		}
		ivAnima3.setVisibility(View.GONE);
	}

	/**
	 * 获取界面跳转时的传值
	 */
	private void setData() {
		if (null == mcm) {
			manager.DiscoverAll();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mcm = manager.getMCSObject();
				}
			}, 1000);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		manager.setOnPISChangedListener(this);
//		manager.setOnPipaRequestStatusListener(this);
	}
	
	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

	/*
	 * 设置监听器
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						int height = contentLayout.getHeight();
						if (originalHeight != height) {
							originalHeight = height;
							ViewGroup.LayoutParams param = stepsView
									.getLayoutParams();
							param.height = originalHeight;
							stepsView.setLayoutParams(param);
							getLocationsOnSteps();
						}
						LogUtils.i(TAG, "setListener():originalHeight = "
								+ originalHeight);
					}
				});
	}

	/*
	 * 初始化组件
	 */
	private void initView() {
		backBtn = (Button) findViewById(R.id.title_back);
		tvTitle = (TextView) findViewById(R.id.title_name);
		stepsView = (StepsView) findViewById(R.id.addswitch_step);
		tvStep1 = (TextView) findViewById(R.id.addswitch_step1_tip);
		tvStep2 = (TextView) findViewById(R.id.addswitch_step2_tip);
		tvStep3 = (TextView) findViewById(R.id.addswitch_step3_tip);
		tvStep4 = (TextView) findViewById(R.id.addswitch_step4_tip);
		ivAnima2 = (ImageView) findViewById(R.id.addswitch_anima2);
		ivAnima3 = (ImageView) findViewById(R.id.addswitch_anima3);
		contentLayout = (RelativeLayout) findViewById(R.id.addswitch_content_layout);
		spaceView = findViewById(R.id.addswitch_space);
		scanLayout = (FrameLayout) findViewById(R.id.addswitch_scan_layout);
		inputLayout = (RelativeLayout) findViewById(R.id.addswitch_input_layout);
		cancelBtn = (Button) findViewById(R.id.addswitch_btn_cancel);
		okBtn = (Button) findViewById(R.id.addswitch_btn_ok);
		etSSID = (EditText) findViewById(R.id.addswitch_ssid);
		etKey = (EditText) findViewById(R.id.addswitch_connect_pwd);

		scanLayout.setVisibility(View.VISIBLE);
		inputLayout.setVisibility(View.GONE);
		setSteps(0, true);
		setTitle();
		setVisiabilityOnInputLayout(false);
		setVisiabilityOnSpaceView(false);
	}

	private void getLocationsOnSteps() {
		int[] a2 = new int[2];
		int[] a3 = new int[2];
		int[] a4 = new int[2];
		int distance = 0;
		tvStep2.getLocationInWindow(a2);
		tvStep3.getLocationInWindow(a3);
		tvStep4.getLocationInWindow(a4);
		stepsView.setLocation(0, 30);
		distance = a3[1] - a2[1];
		stepsView.setLocation(1, a2[1] - distance);
		stepsView.setLocation(2, a3[1] - distance);
		stepsView.setLocation(3, a4[1] - distance);
		stepsView.invalidate();

	}

	/**
	 * 
	 * @param index
	 *            进行到哪个一步，从0开始
	 * @param success
	 *            当前的一步是否成功
	 */
	private void setSteps(int index, boolean success) {
		tvStep1.setTextColor(currentColor);
		if (success) {
			if (index >= 1) {
				tvStep2.setTextColor(currentColor);
			} else {
				tvStep2.setTextColor(Color.GRAY);
			}
			if (index >= 2) {
				tvStep3.setTextColor(currentColor);
			} else {
				tvStep3.setTextColor(Color.GRAY);
			}
			if (index >= 3) {
				tvStep4.setTextColor(currentColor);
			} else {
				tvStep4.setTextColor(Color.GRAY);
			}
			tvStep2.setText("绑定成功");
			tvStep4.setText("配置成功");
		} else {
			if (index > 1) {
				tvStep2.setTextColor(currentColor);
			} else {
				tvStep2.setTextColor(Color.RED);
			}
			if (index > 2) {
				tvStep3.setTextColor(currentColor);
			} else {
				tvStep3.setTextColor(Color.RED);
			}
			if (index > 3) {
				tvStep4.setTextColor(currentColor);
			} else {
				tvStep4.setTextColor(Color.RED);
			}
			if (index > 1) {
				tvStep2.setText("绑定成功");
			} else {
				tvStep2.setText("绑定失败");
			}
			tvStep4.setText("配置失败");
		}
		stepsView.setResult(index, success);
	}

	/**
	 * 设置空白布局是否可见
	 */
	private void setVisiabilityOnSpaceView(boolean isVisiable) {
		if (isVisiable) {
			spaceView.setVisibility(View.VISIBLE);
		} else {
			spaceView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置扫描布局是否可见
	 */
	private void setVisiabilityOnScanLayout(boolean isVisiable) {
		if (isVisiable) {
			scanLayout.setVisibility(View.VISIBLE);
		} else {
			scanLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ssid和key输入框的布局是否可见
	 */
	private void setVisiabilityOnInputLayout(boolean isVisiable) {
		if (isVisiable) {
			inputLayout.setVisibility(View.VISIBLE);
		} else {
			inputLayout.setVisibility(View.GONE);
		}
	}

	/*
	 * 设置标题名称
	 */
	private void setTitle() {
		backBtn.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.addswitch_title);
	}

	/**
	 * 二维码扫描返回数据
	 */
	public void handleDecode(String mac) {
		if (!TextUtils.isEmpty(mac)) {
			this.mac = "0000" + mac;
			setDefaultSSID();
			setVisiabilityOnScanLayout(false);
			setVisiabilityOnInputLayout(true);
		} else {
			startScan();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			Intent intent = new Intent(this, AddDevicesActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.addswitch_btn_cancel:
			setVisiabilityOnScanLayout(true);
			setVisiabilityOnInputLayout(false);
			mHandler.sendEmptyMessage(MSG_LINE_INIT);
			break;
		case R.id.addswitch_btn_ok:
			hideSofeKeyBoard();
			easyConfig();
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
//		manager.setOnPISChangedListener(null);
//		manager.setOnPipaRequestStatusListener(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearData();
	}

	/**
	 * 清理缓存数据
	 */
	private void clearData() {
		if (mBindedDeviceMacList != null) {
			mBindedDeviceMacList.clear();
			mBindedDeviceMacList = null;
		}
	}

	/**
	 * 获取已经绑定设备的mac地址列表
	 */
	private void getBindedDevices() {
		if (!TextUtils.isEmpty(mac)) {
			if (null == mcm) {
				setData();
			}
			if (mcm != null) {
//				mcm.getBindDevices(true);
			} else {
				mHandler.sendEmptyMessage(MSG_DEVBIND_FAILED);
			}
		} else {
			mHandler.sendEmptyMessage(MSG_DEVBIND_FAILED);
		}
	}

//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis == null) {
//			return;
//		}
//		if (pis instanceof PISMCSManager) {
//			PISMCSManager mPISMcm = (PISMCSManager) pis;
//			switch (reqType) {
//			case PISMCSManager.PIS_CMD_MCM_DEVBIND:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					mHandler.sendEmptyMessage(MSG_DEVBIND_SUCCESS);
//				} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_ERROR) {
//					mHandler.sendEmptyMessage(MSG_DEVBIND_FAILED);
//				}
//				break;
//			case PISMCSManager.PIS_CMD_MCM_DEVBINDGET:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					if (null == mcm) {
//						mcm = manager.getMCSObject();
//					}
//					if (mcm != null) {
////						List<String> mList = mcm.getBindedDeviceMacList();
////						String macs = StringUtils.getMacsString(mList);
////						SharePreferenceUtils.saveMacs(this, macs);
//					}
//					if (!TextUtils.isEmpty(mac)) {
////						DeviceInfo deviceinfo = new PISMCSManager.DeviceInfo();
////						deviceinfo.setMac(mac);
////						if (mPISMcm.getBindedDeviceMacList().contains(mac)) {
////							setVisiabilityOnScanLayout(false);
////							setVisiabilityOnInputLayout(false);
////							setVisiabilityOnSpaceView(true);
////							mHandler.sendEmptyMessage(MSG_DEVBIND_SUCCESS);
////						} else {
////							mPISMcm.BindDevice(deviceinfo, true);
////						}
//					} else {
//						mHandler.sendEmptyMessage(MSG_DEVBIND_FAILED);
//						setVisiabilityOnScanLayout(true);
//						setVisiabilityOnInputLayout(false);
//					}
//				} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_ERROR) {
//					if (failedCount >= 3) {
//						failedCount = 0;
//						mHandler.sendEmptyMessage(MSG_DEVBIND_FAILED);
//					} else {
//						failedCount++;
//					}
//				}
//				break;
//			}
//		} else if (pis instanceof PISDevice) {
//			switch (reqType) {
//			case PISDevice.PIS_CMD_DEVICE_INFO_GET:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE
//						&& mPISDevice != null) {
//					String addressMac = mPISDevice.getMacString()
//							.replace("-", "");
//					if (addressMac.equals(mac)) {
//						total = 999;
//						time = 999;
//						mHandler.removeMessages(1000);
//						mHandler.sendEmptyMessage(MSG_CONFIG_SUCCESS);
//						LogUtils.i(TAG, "=======onRequestResult=====success");
//					}
//				} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT
//						|| result == PipaRequest.REQUEST_RESULT_ERROR) {
//					LogUtils.i(TAG, "=======onRequestResult=====error");
//					mHandler.sendEmptyMessage(MSG_CONFIG_FAILED);
//				}
//				break;
//			default:
//				break;
//			}
//		}
//	}

	/**
	 * 开始配置
	 */
	private void easyConfig() {
		time = 0;
		total = 0;
		mHandler.removeMessages(1000);
		connectedpassword = etKey.getText().toString();
		connectedSSID = etSSID.getText().toString();
		if (TextUtils.isEmpty(connectedpassword)
				|| TextUtils.isEmpty(connectedSSID)) {
			ToastUtils.showToast(this, R.string.forget_pwd_input_error);
		} else {
			getBindedDevices();
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideSofeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 设置默认的ssid
	 */
	private void setDefaultSSID() {
		WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);;
		final WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		connectedSSID = mWifiInfo.getSSID();
		if (connectedSSID != null && connectedSSID.length() > 0) {
			if (connectedSSID.contains("\'") || connectedSSID.contains("\"")) {
				connectedSSID = connectedSSID.substring(1,
						connectedSSID.length() - 1);
			}
		}
		etSSID.setText(connectedSSID);
		etKey.setText("");
	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}
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
	private PISDevice mPISDevice;
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		if (pis instanceof PISDevice) {
//			mPISDevice = (PISDevice) pis;
//		}
//	}

	int time = 0;
	int total = 0;

	public boolean note_Intent(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		boolean isAvalible = false;
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// 当前网络不可用
			Toast.makeText(context.getApplicationContext(),
					R.string.no_internet, Toast.LENGTH_SHORT).show();
			return false;
		}

		if (networkinfo != null && networkinfo.isConnected()) {
			if (networkinfo.getState() == NetworkInfo.State.CONNECTED) {
				isAvalible = true;
			}
		}
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (!wifi) { // 提示使用wifi
			Toast.makeText(context.getApplicationContext(),
					R.string.please_use_wifi, Toast.LENGTH_SHORT).show();
		}
		return isAvalible;
	}

	/**
	 * 开始扫描码
	 */
	private void startScan() {
		if (barcodeScanned) {
			barcodeScanned = false;
			mCamera.setPreviewCallback(previewCb);
			mCamera.startPreview();
			previewing = true;
			mCamera.autoFocus(autoFocusCB);
		}
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					handleDecode(sym.getData());
					barcodeScanned = true;
				}
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			mHandler.postDelayed(doAutoFocus, 1000);
		}
	};
}