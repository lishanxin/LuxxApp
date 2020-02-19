package net.senink.seninkapp.ui.activity;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.R;
import net.senink.seninkapp.codescan.zxing.camera.CameraManager;
import net.senink.seninkapp.codescan.zxing.decoding.DecodeThread;
import net.senink.seninkapp.codescan.zxing.decoding.InactivityTimer;
import net.senink.seninkapp.codescan.zxing.view.ViewfinderResultPointCallback;
import net.senink.seninkapp.codescan.zxing.view.ViewfinderView;


import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.home.SelectWorkingModeActivity;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.StringUtils;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
@SuppressLint("HandlerLeak")
public class MipcaCaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private ProgressDialog progressDialog;
	private PISManager mPISManager;
	private PISMCSManager mPisMCM;
	private MeshController controller;
	private MeshController.onFeedbackListener listener = new MeshController.onFeedbackListener() {

		@Override
		public void onNetSecurity() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnected() {
			
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		controller = MeshController.getInstance(this);
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		Button mButtonBack = (Button) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MipcaCaptureActivity.this.finish();
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.scan_processing_text));

		mPISManager = PISManager.getInstance();
		mPisMCM = mPISManager.getMCSObject();
//		mPISManager.setOnPISChangedListener(this);
//		mPISManager.setOnPipaRequestStatusListener(this);
		initScanView();
	}

	@SuppressWarnings("deprecation")
	private void initScanView() {
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		controller.setonFeedbackListener(listener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		controller.setonFeedbackListener(null);
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString != null && resultString.length() > 0) {
			// 暂停扫描
			if (handler != null) {
				handler.quitSynchronously();
			}
			CameraManager.get().closeDriver();
			viewfinderView.stopScane();
			// 处理扫描结果
			processDeviceInfo("0000" + resultString);
			// processScanResult("0000" + resultString);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * process the scaned result
	 * 
	 * */
	private String classid;
	private String mac;
	private String passwd;
	private String network_configs_key;

	private void processDeviceInfo(String deviceInfo) {
		if (TextUtils.isEmpty(deviceInfo)) {
			bondDevice();
		} else {
			addDeviceFailedInfo();
		}
	}

	private void bondDevice() {
//		if (mPisMCM != null) {
//			mPisMCM.getBindDevices(true);
//		} else {
//			noLoginInfo();
//		}
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	String connectedSSID;
//	String connectedpassword;
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
//					if ("000002000102".equals(classid)) {
//						addDeviceSuccessedInfo();
//						return;
//					} else if ("000002000101".equals(classid)
//							|| "000001000203".equals(classid)) {
//						if (mPISManager == null) {
//							mPISManager = PISManager.getInstance();
//						}
//						easyConfig();
//					} else if ("000001000101".equals(classid)) {
//						handler.removeMessages(1002);
//						handler.sendEmptyMessageDelayed(1002, 1000);
//					} else {
//						addDeviceFailedInfo();
//					}
//				} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					addDeviceFailedInfo();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR) {
//					addDeviceFailedInfo();
//				}
//				break;
//
//			case PISMCSManager.PIS_CMD_MCM_DEVBINDGET:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
////					if (mPISMcm != null) {
////						List<String> mList = mPISMcm.getBindedDeviceMacList();
////						String macs = StringUtils.getMacsString(mList);
////						SharePreferenceUtils.saveMacs(this, macs);
////					}
////					if (mac != null && mac.length() > 0) {
////						DeviceInfo deviceinfo = new PISMCSManager.DeviceInfo();
////						deviceinfo.setClassID(classid);
////						deviceinfo.setMac(mac);
////						deviceinfo.setPassword(passwd);
////
////						if (mPISMcm.getBindedDeviceMacList().contains(mac)) {
////							if ("000002000102".equals(classid)) {
////								addDeviceSuccessedInfo();
////							} else if ("000002000101".equals(classid)
////									|| "000001000203".equals(classid)) {
////								easyConfig();
////							}
////						} else if ("000002000101".equals(classid)
////								|| "000001000203".equals(classid)) {
////							mPISMcm.BindDevice(deviceinfo, true);
////						} else {
////							addDeviceFailedInfo();
////						}
////					} else {
////						addDeviceFailedInfo();
////					}
//				} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					addDeviceFailedInfo();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR) {
//					addDeviceFailedInfo();
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
//					Log.e("Aaron Lau", "addressMac --> " + addressMac
//							+ " \n mac --> " + mac);
//					if (addressMac.equals(mac)) {
//						total = 999;
//						time = 999;
//						addDeviceSuccessedInfo();
//						handler.removeMessages(1000);
//					}
//				} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					addDeviceFailedInfo();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR) {
//					addDeviceFailedInfo();
//				}
//				break;
//			default:
//				break;
//			}
//		}
//
//	}

	@SuppressLint("InflateParams")
	private void easyConfig() {
		// 获取当前已连接的wifi的SSID和password
		WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		final WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
//		connectedSSID = mWifiInfo.getSSID();
//
//		/**
//		 * if (connectedSSID != null && connectedSSID.length() > 0) {
//		 * connectedSSID = connectedSSID.substring(1, connectedSSID.length() -
//		 * 1); }
//		 */
//
//		if (connectedSSID != null && connectedSSID.length() > 0) {
//			if (connectedSSID.contains("\'") || connectedSSID.contains("\"")) {
//				connectedSSID = connectedSSID.substring(1,
//						connectedSSID.length() - 1);
//			}
//		}

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(
				R.layout.new_device_input_password, null);
		final EditText inputSSID = (EditText) layout
				.findViewById(R.id.new_device_ssid);
//		inputSSID.setText(connectedSSID);
		final EditText inputPassword = (EditText) layout
				.findViewById(R.id.new_device_pwd);
		inputPassword.setFocusable(true);
		inputPassword.setFocusableInTouchMode(true);
		inputPassword.requestFocus();
		progressDialog.dismiss();
		AlertDialog.Builder inputBuilder = new AlertDialog.Builder(this);
		inputBuilder.setTitle(R.string.add_device_input_pwd)
				.setInverseBackgroundForced(true).setView(layout)
				.setNegativeButton(android.R.string.cancel, null);
		inputBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 调用easyconfig（）； 发送当前wifi的SSID，pwd，
						// 给排插 1秒发一次，发10次
						time = 0;
						total = 0;
						if (handler == null) {
							handler = new CaptureActivityHandler(
									MipcaCaptureActivity.this, decodeFormats,
									characterSet);
						}
						handler.removeMessages(1000);
//						connectedpassword = inputPassword.getText().toString();
						handler.sendEmptyMessage(1000);
						hintKbTwo();
						progressDialog
								.setMessage(getString(R.string.configing_device));
						progressDialog.show();
					}
				});
		inputBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						restartDecode();
					}
				});
		inputBuilder.show();
	}

	private void hintKbTwo() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		// if (PISConstantDefine.PIPA_NET_STATUS_CONNECTED == netConnStatus) {
//		// // 2.5 成功，发discovery，获取GWCofing服务
//		// // addRounterCount = 0;
//		// if(handler != null){
//		// handler.removeMessages(1002);
//		// handler.sendEmptyMessageDelayed(1002, 500);
//		// }
//		// } else if (PISConstantDefine.PIPA_NET_STATUS_DISCONNECTED ==
//		// netConnStatus) {
//		// networkDisconnectInfo();
//		// } else {
//		// addDeviceFailedInfo();
//		// }
//
//	}
//


	private PISDevice mPISDevice;
//
//	@Override
//	public void onPIServiecInfoChange(PISBase obj){
//
//	}
//	@Override
//	public void onPIServiceChange(List<PISBase> ServiceList, int changeState){
////	public void onPIServicesChange(PISBase pis, boolean isNew) {
//		for (PISBase pis : ServiceList){
////			if (pis instanceof PISDevice) {
////				mPISDevice = (PISDevice) pis;
////				if (mPISDevice != null) {
////					mPISDevice.getDevInfo();
////				}
////			} else if (pis instanceof PISGWConfig) {
////				// 调到快速设置页面
////				Intent intent = new Intent(this, SelectWorkingModeActivity.class);
////				if (network_configs_key != null && network_configs_key.length() > 0) {
////					intent.putExtra(HomeActivity.VALUE_KEY, network_configs_key);
////				}
////				intent.putExtra("isAddDevice", true);
////				startActivity(intent);
////				overridePendingTransition(0, 0);
////			} else if (pis instanceof PISMCSManager) {
////				// 绑定设备（mac， pwd， classid）
////				// bondDevice(classid, mac, passwd);
////			}
//		}
//
//
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

	private void addDeviceFailedInfo() {
		progressDialog.dismiss();
		showErrorDialogInfo(getString(R.string.add_device_failed));
	}

	private void noLoginInfo() {
		progressDialog.dismiss();
		showErrorDialogInfo(getString(R.string.please_login));
	}

	private void addDeviceSuccessedInfo() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		showSuccessDialogInfo(getString(R.string.add_device_successed));
	}

	AlertDialog mAlertDialog = null;

	private void showErrorDialogInfo(final String info) {
		if (mAlertDialog == null) {
			AlertDialog.Builder scanBuilder = new AlertDialog.Builder(this);
			scanBuilder.setTitle(R.string.scan_dialog_title);
			scanBuilder.setMessage(info);
			mAlertDialog = scanBuilder.create();
			scanBuilder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (getString(R.string.please_login).equals(info)) {
								MipcaCaptureActivity.this.finish();
							} else {
								restartDecode();
							}
						}
					});
			scanBuilder.show();
		}
	}

	private void restartDecode() {
		try {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		handler = null;
		handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		handler.state = State.PREVIEW;
		CameraManager.get().requestPreviewFrame(
				handler.decodeThread.getHandler(), R.id.decode);
		CameraManager.get().requestAutoFocus(handler, R.id.auto_focus);
		this.drawViewfinder();

		decodeFormats = null;
		characterSet = null;
		mAlertDialog = null;
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.scan_processing_text));
	}

	private void showSuccessDialogInfo(String info) {
		if (mAlertDialog == null) {
			AlertDialog.Builder scanBuilder = new AlertDialog.Builder(this);
			scanBuilder.setTitle(R.string.scan_dialog_title);
			scanBuilder.setMessage(info);
			mAlertDialog = scanBuilder.create();
			scanBuilder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							MipcaCaptureActivity.this.finish();
						}
					});
			scanBuilder.show();
		}
	}

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	class CaptureActivityHandler extends Handler {
		private final MipcaCaptureActivity activity;
		private final DecodeThread decodeThread;
		private State state;

		public CaptureActivityHandler(MipcaCaptureActivity activity,
				Vector<BarcodeFormat> decodeFormats, String characterSet) {
			this.activity = activity;
			decodeThread = new DecodeThread(activity, decodeFormats,
					characterSet, new ViewfinderResultPointCallback(
							activity.getViewfinderView()));
			decodeThread.start();
			state = State.SUCCESS;
			// Start ourselves capturing previews and decoding.
			CameraManager.get().startPreview();
			restartPreviewAndDecode();
		}

		@Override
		public void handleMessage(Message message) {
			if (mPISManager == null) {
				mPISManager = PISManager.getInstance();
			}
			switch (message.what) {
			case R.id.auto_focus:
				// Log.d(TAG, "Got auto-focus message");
				// When one auto focus pass finishes, start another. This is the
				// closest thing to
				// continuous AF. It does seem to hunt a bit, but I'm not sure
				// what else to do.
				if (state == State.PREVIEW) {
					CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
				}
				break;
			case R.id.restart_preview:
				restartPreviewAndDecode();

				break;
			case R.id.decode_succeeded:
				state = State.SUCCESS;
				Bundle bundle = message.getData();

				Bitmap barcode = bundle == null ? null : (Bitmap) bundle
						.getParcelable(DecodeThread.BARCODE_BITMAP);

				activity.handleDecode((Result) message.obj, barcode);
				break;
			case R.id.decode_failed:
				// We're decoding as fast as possible, so when one decode fails,
				// start another.
				state = State.PREVIEW;
				CameraManager.get().requestPreviewFrame(
						decodeThread.getHandler(), R.id.decode);
				break;
			case R.id.return_scan_result:
				activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
				activity.finish();
				break;
			case R.id.launch_product_query:
				String url = (String) message.obj;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				activity.startActivity(intent);
				break;
			case 1000:
				if (time++ < 19) {
					// 发20次easyconfig
//					mPISManager
//							.easyConfigSsId(connectedSSID, connectedpassword);
				} else if (time >= 20) {
					total++;
					time = 0;
					// 发1次discovery
					// 不需要延迟
					mPISManager.DiscoverAll();
					// 需要延迟
					// sendEmptyMessageDelayed(1004, 1000);
				}

				// 一共发（200+400+600+800+1000）＊20（次easyconfig）＊3(次discovery)
				// =180000毫秒 = 180秒

				if (total < 15) {
					/*
					 * if(total < 3){ sendEmptyMessageDelayed(1000, 200); } else
					 * if(total < 6){ sendEmptyMessageDelayed(1000, 400); } else
					 * if(total < 9){ sendEmptyMessageDelayed(1000, 600); } else
					 * if(total < 12){ sendEmptyMessageDelayed(1000, 800); }
					 * else { sendEmptyMessageDelayed(1000, 1000); }
					 */
					/**
					 * CY修改
					 */
					sendEmptyMessageDelayed(1000, 200);
				} else if (total >= 15) {
					// 绑定失败
					total = 888;
					time = 888;
					sendEmptyMessageDelayed(1001, 1000);
				}

				break;
			case 1001:
				// 绑定失败
				if (total != 999) {
					addDeviceFailedInfo();
				}
				break;
			case 1004:
				mPISManager.DiscoverAll();
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
				break;
			default:
				break;
			}
		}

		public void quitSynchronously() {
			state = State.DONE;
			CameraManager.get().stopPreview();
			Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
			quit.sendToTarget();
			try {
				decodeThread.join();
			} catch (InterruptedException e) {
				// continue
			}

			// Be absolutely sure we don't send any queued up messages
			removeMessages(R.id.decode_succeeded);
			removeMessages(R.id.decode_failed);
		}

		public void restartPreviewAndDecode() {
			if (state == State.SUCCESS) {
				state = State.PREVIEW;
				CameraManager.get().requestPreviewFrame(
						decodeThread.getHandler(), R.id.decode);
				CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
				activity.drawViewfinder();
			}
		}
	}
}