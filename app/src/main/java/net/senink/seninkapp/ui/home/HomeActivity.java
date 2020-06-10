package net.senink.seninkapp.ui.home;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pinm.PINMoMC.PinmOverMC;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.ActivityManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.Foreground;

import net.senink.seninkapp.GeneralDataManager;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;

import net.senink.seninkapp.fragment.LightControlFragment;
import net.senink.seninkapp.fragment.RemoterFragment;
import net.senink.seninkapp.fragment.SwitchFragment;

import net.senink.seninkapp.telink.AppSettings;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.model.Mesh;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.ui.activity.AddDevicesActivity;
import net.senink.seninkapp.ui.activity.LoginActivity;
import net.senink.seninkapp.ui.activity.MipcaCaptureActivity;
import net.senink.seninkapp.ui.upgrade.ApkUpgradeUtils;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.Config;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.SortUtils;

import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.ProductClassifyView;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.update.PgyUpdateManager;
import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.light.LeBluetooth;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.light.parameter.AutoConnectParameters;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;
import com.telink.sig.mesh.util.MeshUtils;
import com.telink.sig.mesh.util.TelinkLog;
import com.telink.sig.mesh.util.UnitConvert;
//import com.umeng.analytics.MobclickAgent;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.PinmOverBLE;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.util.CrashHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomeActivity extends BaseActivity implements OnClickListener, EventListener<String> {
	private static final String TAG = "HomeActivity";
	private static final ArrayList<String[]> mProductClasses = null;
	/**跳转至 Login Activity*/
	public static final int REQUEST_CODE_LOGIN = 1;
	public static final int REQUEST_DEVICE_ADD = 2;
	public static final int REQUEST_QRCODE_SCAN = 3;
	private static final int PERMISSIONS_REQUEST_ALL = 0x1231;
	String[] permissionsOfBlueTooth = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.BLUETOOTH_ADMIN};
	private Handler delayHandler = new Handler();
	private View btnAddDevice;
	// 返回按钮
	private Button backBtn;
	// logo按钮
	private ImageButton logoBtn;
	// 标题名称
	private TextView tvTitle;
	// 蓝牙未连接上的提示
	private RelativeLayout connectedTip = null;
	private ImageView bleConnectAnim = null;
	private AnimationDrawable anim = null;
	// 升级框
	private View upgradeDialog;
	// 添加按钮
	private ImageButton addBtn;

	private SparseArray<Fragment> fragments = new SparseArray<>(5);

	private FragmentManager manager;
	private LinearLayout fragmentManager;
	// 自定义分类按钮
	private ProductClassifyView classifyView;

	private View emptyListView;

	public static final String VALUE_KEY = "value_key";

	private PISManager pm;

	private int curShowListType = PISManager.PISERVICE_CATEGORY_LIGHT;

	private BroadcastReceiver mPinmReceiver = null;
	private BroadcastReceiver mPMReceiver = null;

	// 触屏监听器
//	private GestureDetector detector = null;
	// 后台切换监听器
	private static Foreground.Listener foregroundListener = null;
	/** 做标签，记录当前是哪个fragment */
	public int MARK = 0;
	/** 定义手势两点之间的最小距离 */
	public static final int DISTANT = 50;

	public static HomeActivity activity;
	private static Context context;

	// 获取取蓝牙信息成功
//	private static final int MSG_GET_BLEINFOR_SUCCESS = 1;
	// 获取蓝牙信息失败
	private static final int MSG_GET_BLEINFOR_FAILED = 2;
	// 有版本升级
	private static final int MSG_HAS_UPGRADE_VERSION = 3;
	// 显示未连接提示
//	private static final int MSG_SHOW_TIP = 4;
	// 关闭未连接提示
	private static final int MSG_HIDE_TIP = 5;
	// 刷新页面
	private static final int MSG_UPDATE_VIEW = 6;

	// 添加设备或者分组的弹出框
	private PopupWindow addDialog = null;

	// 检测升级的工具类
	private ApkUpgradeUtils upgradeUtils = null;

	private Handler handler = new Handler();
	private boolean isServiceCreated = false;
	private AlertDialog mWaitingDialog;

	/**注册APPLICATION的前后台切换时间监听*/
	static {
		foregroundListener = new Foreground.Listener() {
			@Override
			public void onBecameForeground() {
//				PISManager.getInstance().Start();
				LogUtils.i("Foreground", "onBecameForeground===========>!");
			}

			@Override
			public void onBecameBackground() {
//				PISManager.getInstance().Stop();

				if (PISManager.getDefaultContext() != null){
					String userPath = SharePreferenceUtils.getUserDataPath(PISManager.getDefaultContext(),
							SharePreferenceUtils.FILENAME_PISMANAGER,
							PISManager.getInstance().getUserObject().loginUser);
					PISManager.SavePISManagerObject(PISManager.getInstance(), userPath);
				}
				LogUtils.i("Foreground", "onBecameBackground===========>!");
			}
		};
		Foreground.get().addListener(foregroundListener);
	}

//	final private int REQUEST_CODE_ASK_PHONE_STATE = 122;
	final private int REQUEST_CODE_ASK_BLUETOOTH = 123;
	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		try {
			Dialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
					.setMessage(message)
					.setPositiveButton("OK", okListener)
					.setNegativeButton("Cancel", null)
					.create();
			alertDialog.show();
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}
	}

	/**检查相应权限*/
	private int permissionsChecker(){
		return 0;
	}

	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_HIDE_TIP:
					removeMessages(MSG_HIDE_TIP);
					setVisibilityOnTip(false, 0);
					break;
				// 版本升级提示框
				case MSG_HAS_UPGRADE_VERSION:
					if (upgradeUtils != null) {
						CommonUtils.showUpgradeDialog(HomeActivity.this,
								upgradeDialog, upgradeUtils.apkName,
								upgradeUtils.apkUrl);
					}
					break;
				case MSG_UPDATE_VIEW: {
					try{
						classifyView.updateSelectState(curShowListType);
						changeFragment(curShowListType);
						refreshListView();
					}catch (Exception e){
						PgyCrashManager.reportCaughtException(HomeActivity.this, e);
					}
				}
			}
		}
	};

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
//			case REQUEST_CODE_ASK_PHONE_STATE:
			case REQUEST_CODE_ASK_BLUETOOTH: {
				boolean isGranted = true;
				for (int result : grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED){
						isGranted = false;
						break;
					}
				}
				if (isGranted) {
					if (!checkAllPermission()) {
						requestAllPermission();
						return;
					}
					initObject(null);

					try {
						startTelinkService();
						PinmInterface pif = PISManager.getInstance().getPinmObjectWithType(PinmInterface.TYPE_CSRMESH);
						if (pif != null && pif.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED) {
							setVisibilityOnTip(true, R.string.home_connect_ble_tip);
						}
					} catch (Exception e) {
						PgyCrashManager.reportCaughtException(HomeActivity.this, e);
					}
				} else {
					// Permission Denied
// 显示缺失权限提示
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setTitle(R.string.help_title);
					builder.setMessage(R.string.permission_help);

					// 拒绝, 退出应用
					builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(HomeActivity.this, "Permission Denied, Application exited", Toast.LENGTH_SHORT)
									.show();
							finish();
						}
					});

					builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intent.setData(Uri.parse("package:" + getPackageName()));
							startActivity(intent);
						}
					});

					builder.show();

				}
			}
				break;

			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void startTelinkService(){
		// 开启Telink的sdk
		TelinkApiManager.getInstance().startMeshService(HomeActivity.this, HomeActivity.this);
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		/**初始化蒲公英SDK*/
		pgyerInit();
		CrashHandler.getInstance().init(this);
		try {
			registerAllReceiver();
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}
		/**初始化UI*/
		try {
			setContentView(R.layout.activity_home);
			initView();
//			detector = new GestureDetector(this);
			context = this;
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}


		/**获取默认账号，如果没有则启动LoginActivity*/
		/**如果默认账号为特定账号，则代表其为本地模式进行操作（或者具有本地模式标志）*/
		UserInfo uInfo = null;
		try {
			/**初始化PISManager*/
			String user = SharePreferenceUtils.getInstance(this).getCurrentUser();
			if (user != null) {
				uInfo = SharePreferenceUtils.getInstance(this).loadUser(user);
				String userPath = SharePreferenceUtils.getUserDataPath(HomeActivity.this,
						SharePreferenceUtils.FILENAME_PISMANAGER,
						uInfo.loginUser);
				pm = PISManager.loadPISManagerObject(this, userPath);
			}

			if (pm == null) {
				pm = PISManager.getInstance();
				startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
				return;
			}
			GeneralDataManager.getInstance().init(this, pm);
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
			startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
			return;
		}

		/**友盟数据处理*/
//		AnalyticsConfig.enableEncrypt(true);
//		MobclickAgent.setSessionContinueMillis(30 * 60 * 1000);

		/**初始化相关工具类*/
//		mUserService = new UserService(this);



		if (checkAllPermission()) {
			//初始化相关数据
			initObject(uInfo);
			startTelinkService();
		}
		else{
			requestAllPermission();
		}


		if (!LeBluetooth.getInstance().isSupport(getApplicationContext())) {
			finish();
		}

		Mesh mesh = MyApplication.getInstance().getMesh();
		if (mesh.devices != null) {
			for (DeviceInfo deviceInfo : mesh.devices) {
				deviceInfo.setOnOff(-1);
				deviceInfo.lum = 0;
				deviceInfo.temp = 0;
			}
		}

		Intent serviceIntent = new Intent(this, MeshService.class);
		startService(serviceIntent);
		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
		MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS, this);
		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_MESH_EMPTY, this);
		MyApplication.getInstance().addEventListener(MeshController.EVENT_TYPE_SERVICE_CREATE, this);
		MyApplication.getInstance().addEventListener(MeshController.EVENT_TYPE_SERVICE_DESTROY, this);
		MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_PROCESSING, this);
		MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_COMPLETE, this);
		MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_ERROR_BUSY, this);
		MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_KICK_OUT_CONFIRM, this);
		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);

	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void onPermissionChecked() {
		TelinkLog.d("permission check pass");
		delayHandler.removeCallbacksAndMessages(null);
		delayHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 开启Telink的sdk
				TelinkApiManager.getInstance().startMeshService(HomeActivity.this, HomeActivity.this);
			}
		}, 1000);

	}

	@Override
	protected void onResume() {
		super.onResume();
		getTelinkOnOff();
		try {
			if (!checkAllPermission()) {
				return;
			}
			if (classifyView != null) {
				List<Integer> onlineCats = pm.getOnlineCategories();
				if (onlineCats.size() == 0) {
					curShowListType = PISManager.PISERVICE_CATEGORY_LIGHT;
					classifyView.click(PISManager.PISERVICE_CATEGORY_LIGHT);
				} else {
					classifyView.updateView(onlineCats);
					if (onlineCats.size() > 1) {
						classifyView.setVisibility(View.VISIBLE);
					} else {
						classifyView.setVisibility(View.GONE);
					}
					if (!onlineCats.contains(curShowListType))
						curShowListType = onlineCats.get(0);
					classifyView.click(curShowListType);
				}
			}


		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}
		TelinkApiManager.getInstance().autoConnectToDevices(this);
		TelinkLog.d("main resume -- service created: " + isServiceCreated);
		if (!LeBluetooth.getInstance().isEnabled()) {
			showBleOpenDialog();
		} else {
			if (isServiceCreated) {
//                MeshService.getInstance().getOnOff(0xFFFF, 0, null);
				this.autoConnect(false);
			}
		}

//		MobclickAgent.onResume(this);
	}

	android.support.v7.app.AlertDialog.Builder mDialogBuilder;

	private void showBleOpenDialog() {
		if (mDialogBuilder == null) {
			mDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
			mDialogBuilder.setCancelable(false);
			mDialogBuilder.setMessage("Enable Bluetooth!");
			mDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			mDialogBuilder.setPositiveButton("enable", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LeBluetooth.getInstance().enable(HomeActivity.this);
				}
			});
		}

		mDialogBuilder.show();
	}

	long preTime = 0;
	public final int REFRESH = 2001;

	@Override
	protected void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
		PgyFeedbackShakeManager.unregister();
//		pm.setOnPISChangedListener(null);
//		pm.setOnPipaRequestStatusListener(null);
//		if (controller != null) {
//			controller.setonFeedbackListener(null);
//		}
//		if (null == PISManager.locations || PISManager.locations.size() == 0) {
//			// 获取位置列表
//			new Thread() {
//				@Override
//				public void run() {
//					PISManager.locations = HttpUtils
//							.getLocationAndSave(HomeActivity.this);
//				}
//			}.start();
//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		PinmInterface pif = PISManager.getInstance().getPinmObjectWithType(PinmInterface.TYPE_CSRMESH);
		if (pif != null) {
			if (pif.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
				setVisibilityOnTip(true, R.string.home_connect_ble_tip);
			else
				PISManager.getInstance().DiscoverAll();
		}
	}

	@Subscribe
	public void refreshLightListView(TelinkDataRefreshEntry opr){
		refreshListView();
		if(opr.isGotLightStatus()){
			setVisibilityOnTip(false, 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		unregisterAllReceiver();
		PISManager.getInstance().Stop();
		PgyUpdateManager.unregister();
		myHandler.removeMessages(MSG_GET_BLEINFOR_FAILED);
//		myHandler.removeMessages(MSG_GET_BLEINFOR_SUCCESS);
		PgyCrashManager.unregister();
		GeneralDataManager.getInstance().unInit();
//		pm = null;
//		System.exit(0);

		MyApplication.getInstance().removeEventListener(this);
		handler.removeCallbacksAndMessages(null);

		MyApplication.getInstance().removeEventListener(this);
		handler.removeCallbacksAndMessages(null);
		if (!ActivityManager.getInstance().isApplicationForeground()){
			Intent serviceIntent = new Intent(this, MeshService.class);
			stopService(serviceIntent);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if (requestCode == REQUEST_CODE_LOGIN) {
			switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
				case RESULT_OK:    //登录成功后由登录界面返回
//					Bundle b = intent.getExtras();  //data为B中回传的Intent
//					UserInfo uInfo = (UserInfo) b.getSerializable("userinfo");

					try {
						pm = PISManager.getInstance(HomeActivity.this);
						GeneralDataManager.getInstance().init(this, pm);
						if (pm == null) {
							throw new NetworkErrorException("network invaild");
						}

						if (checkAllPermission())
							initObject(pm.getUserObject());
						else{
							requestAllPermission();
						}
					} catch (Exception e) {
						PgyCrashManager.reportCaughtException(HomeActivity.this, e);
					}
					break;
				case RESULT_CANCELED:
					finish();
					break;
				default:
					break;

			}
		}
		else if (requestCode == REQUEST_DEVICE_ADD){
			PISManager.getInstance().DiscoverAll();
		}
	}

	public class PINMReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent){
			if (intent.getAction().equals(PinmInterface.PINM_CONNECT_ACTION)){
				Bundle bundle = intent.getExtras();
				PinmInterface pif = PISManager.getInstance()
						.getPinmObject((int)bundle.get(PinmInterface.PINM_CONNECT_ExtraObject));
				int newStatus = bundle.getInt(PinmInterface.PINM_CONNECT_ExtraStatus);
				if (pif == null || pif.getConnectType() == PinmInterface.TYPE_MC)
					return;
				if (newStatus == PinmInterface.PINM_CONNECT_STATUS_CONNECTED){
					setVisibilityOnTip(false, 0);
					pm.DiscoverAll();
					if (myHandler != null) {
						myHandler.removeMessages(MSG_UPDATE_VIEW);
						myHandler.sendEmptyMessageDelayed(MSG_UPDATE_VIEW, 800);
					}
				}else{
					setVisibilityOnTip(true, R.string.home_connect_ble_tip);
					myHandler.sendEmptyMessageDelayed(MSG_UPDATE_VIEW, 0);
				}
			}else if(intent.getAction().equals(TelinkApiManager.REFRESH_DEVICES)){
				if (myHandler != null) {
					myHandler.removeMessages(MSG_UPDATE_VIEW);
					myHandler.sendEmptyMessageDelayed(MSG_UPDATE_VIEW, 800);
				}
			}

		}
	}


	/**
	 * 提示
	 * @param isVisialbe
	 * @param resId
	 */
	private void setVisibilityOnTip(boolean isVisialbe, int resId) {
		if (connectedTip == null) {
			try {
				connectedTip = (RelativeLayout) findViewById(R.id.title_bluetooth_tip);
				bleConnectAnim = (ImageView) connectedTip.findViewById(R.id.bluetooth_connecting);
				anim = (AnimationDrawable) bleConnectAnim.getBackground();
			}catch(NullPointerException e){
				PgyCrashManager.reportCaughtException(HomeActivity.this, e);
				return;
			}
		}

		if (isVisialbe) {
			LogUtils.i("mash", "show ble connecting tips");
			TextView tv = (TextView)connectedTip.findViewById(R.id.bluetooth_text);
			tv.setText(resId);
			bleConnectAnim.setVisibility(View.VISIBLE);
			connectedTip.setVisibility(View.VISIBLE);
			anim.start();

		} else {
			LogUtils.i("mash", "hide ble connecting tips");
			bleConnectAnim.setVisibility(View.GONE);
			connectedTip.setVisibility(View.GONE);
			anim.stop();

		}
	}

	/**
	 * 将fragment切换至指定的页面
	 * @param categoriy 0-插座类，1-灯光类，2-桥接器，3-遥控器，4-可穿戴
     */
	private void changeFragment(int categoriy){
		android.app.FragmentTransaction ft = manager.beginTransaction();
		if (categoriy != curShowListType){
			ft.hide(fragments.get(curShowListType));
		}
		ft.show(fragments.get(categoriy));
		ft.commitAllowingStateLoss();
		curShowListType = categoriy;

//		for (int i = 0; i < fragments.size(); i++){
//			if (categoriy == fragments.keyAt(i))
//				ft.show(fragments.valueAt(i));
//			else
//				ft.hide(fragments.valueAt(i));
//		}
//		ft.commitAllowingStateLoss();
//		curShowListType = categoriy;
	}
	/**
	 * 设置监听器
	 */
	private void setListener() {
		btnAddDevice.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		classifyView.setOnItemClickListener(new ProductClassifyView.OnItemClickListner() {

			@Override
			public void onClick(View view, int which) {
				try {
					changeFragment(which);
					refreshListView();

				}catch (Exception e){
					PgyCrashManager.reportCaughtException(HomeActivity.this, e);
				}
			}
		});
	}

	private void onPIServiceChange(List<String> strKeys, int changeStatus){

		boolean needUpdate = false;
		//判断新的PISbase对象是否带来新的分类
		List<Integer> onlieCats = pm.getOnlineCategories();
		if (onlieCats.size() > 1) {
			classifyView.updateView(onlieCats);
			classifyView.setVisibility(View.VISIBLE);
		} else {
			classifyView.setVisibility(View.GONE);
		}

		SparseIntArray categorise = pm.getSupportCategories();
		for (String pisKey : strKeys) {
			/**判断是否有属于当前Fragment的PISBase对象，如果有则刷新界面*/
			PISBase pis = pm.getPISObject(pisKey);
			if (pis == null || categorise == null)
				continue;
			int cls = pis.getT1() & 0xFF | ((pis.getT2() & 0xFF) << 8);
			if (categorise.get(cls, 0) != 0) {
				int cat = categorise.get(cls, PISManager.PISERVICE_CATEGORY_LIGHT);
				if (onlieCats.size() == 1) {
					curShowListType = cat;
					classifyView.click(curShowListType);
				}
				if (cat == curShowListType) {
					needUpdate = true;
					break;
				}
			}
		}
		if (needUpdate) {
			refreshListView();
		}
	}

	private void registerAllReceiver(){
		if (mPinmReceiver == null){
			mPinmReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if(!intent.getAction().equals(PinmInterface.PINM_CONNECT_ACTION))
						return;
					Bundle bundle = intent.getExtras();
					if (bundle == null)
						return;
//					PinmInterface pif = (PinmInterface)bundle.get(PinmInterface.PINM_CONNECT_ExtraObject);
					PinmInterface pif = PISManager.getInstance()
							.getPinmObjectWithType((int)bundle.get(PinmInterface.PINM_CONNECT_ExtraObject));
					if (pif == null)
						return;
					int newStatus = bundle.getInt(PinmInterface.PINM_CONNECT_ExtraStatus);
					pm = PISManager.getInstance();
					GeneralDataManager.getInstance().init(HomeActivity.this, pm);
					boolean allConnected = true;
					for (int i = 0; i < pm.getPinmCount(); i++){
						PinmInterface pi = pm.getPinmObject(i);
						if (pi.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED) {
							allConnected = false;
							break;
						}
					}

					switch (newStatus) {
						case PinmInterface.PINM_CONNECT_STATUS_CONNECTED: {
							if (allConnected)
								setVisibilityOnTip(false, 0);
							pm.DiscoverAll();
							net.senink.piservice.util.LogUtils.i(TAG, "Discover All ......................");
							if (pif.getConnectType() == PinmInterface.TYPE_CSRMESH) {
								LogUtils.i(TAG, "Mesh network connected!!!");
							}
							if (pif.getConnectType() == PinmInterface.TYPE_MC){
								LogUtils.i(TAG, "MC Service connected111");
							}
						}
						break;
						case PinmInterface.PINM_CONNECT_STATUS_DISCONNECTED:{
							if (pif.getConnectType() == PinmInterface.TYPE_MC && pm.hasCloudConnect())
								setVisibilityOnTip(true, R.string.home_connect_net_tip);
							if (pif.getConnectType() == PinmInterface.TYPE_CSRMESH) {
								setVisibilityOnTip(true, R.string.home_connect_ble_tip);
								Foreground.exitToHome();
							}
						}
						break;
						case PinmInterface.PINM_CONNECT_STATUS_CONNECT_TIMEOUT:
							break;
						case PinmInterface.PINM_CONNECT_STATUS_CONNECTING:
							break;
						case PinmInterface.PINM_CONNECT_STATUS_USERPASSWD_ERROR:
							break;
					}
				}
			};
		}

		if (mPMReceiver == null){
			mPMReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if(intent.getAction().equals(PISManager.PISERVICE_INFO_CHANGE_ACTION)){
						try {
							String strKey = intent.getStringExtra(PISManager.PIS_ExtraObject);
							if (strKey == null || pm == null)
								return;
							PISBase pis = pm.getPISObject(strKey);
							SparseIntArray categorise = pm.getSupportCategories();

							if (pis == null)
								return;
							int intType = pis.getIntegerType();
							if (categorise.get(intType, 0) == 0)
								return;
							if (categorise.get(intType) == curShowListType)
								refreshListView();
						}catch (Exception e){
							PgyCrashManager.reportCaughtException(HomeActivity.this, e);
						}
						return;
					}
					if(intent.getAction().equals(PISManager.PISERVICE_CHANGE_ACTION)) {
						try {
							//判断该PISBase对象是否属于当前的Fragment页面
							Bundle bundle = intent.getExtras();
							if (bundle == null)
								return;
							int newStatus = bundle.getInt(PISManager.CHANGE_ExtraEvent);
							List<String> strKeys = (List<String>) bundle.get(PISManager.PIS_ExtraObject_List);
							if (strKeys == null || strKeys.size() == 0)
								return;
							onPIServiceChange(strKeys, newStatus);

						} catch (Exception e) {
							PgyCrashManager.reportCaughtException(HomeActivity.this, e);
						}
					}

				}
			};
			this.getApplicationContext().registerReceiver(mPMReceiver,
					new IntentFilter(PISManager.PISERVICE_CHANGE_ACTION));
			this.getApplicationContext().registerReceiver(mPMReceiver,
					new IntentFilter(PISManager.PISERVICE_INFO_CHANGE_ACTION));
			this.getApplicationContext().registerReceiver(mPinmReceiver,
					new IntentFilter(PinmInterface.PINM_CONNECT_ACTION));

		}
	}

	private void unregisterAllReceiver(){
		this.getApplicationContext().unregisterReceiver(mPMReceiver);
		this.getApplicationContext().unregisterReceiver(mPinmReceiver);
	}

	private void pgyerInit(){
		try {
			PgyCrashManager.register(this);
			//PgyUpdateManager.register(this);
			// 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
			//PgyFeedbackShakeManager.setShakingThreshold(600);

			// 以对话框的形式弹出
			//PgyFeedbackShakeManager.register(HomeActivity.this);

//			ApplicationInfo appInfo = this.getPackageManager()
//					.getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
//			System.out.println("PGYER_APPID:" + appInfo.metaData.getString("PGYER_APPID"));

//			PgyUpdateManager.register(HomeActivity.this,
//					new UpdateManagerListener() {
//
//						@Override
//						public void onUpdateAvailable(final String result) {
//
//							// 将新版本信息封装到AppBean中
//							final AppBean appBean = getAppBeanFromString(result);
//							new AlertDialog.Builder(HomeActivity.this)
//									.setTitle(R.string.soft_update_title)
//									.setMessage(R.string.soft_update_info)
//									.setNegativeButton(
//											R.string.soft_update_updatebtn,
//											new DialogInterface.OnClickListener() {
//
//												@Override
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//													startDownloadTask(
//															HomeActivity.this,
//															appBean.getDownloadURL());
//												}
//											}).show();
//						}
//
//						@Override
//						public void onNoUpdateAvailable() {
//							LogUtils.i(TAG, "当前已经是最新版本");
//						}
//					});
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}
	}

	private void connectPipa(){
		/**连接云服务*/
		UserInfo uInfo = PISManager.getInstance().getUserObject();
		if (uInfo == null){
			throw new IllegalArgumentException("UserInfo cannot be null");
		}
		PinmOverMC pom = (PinmOverMC)pm.getPinmObjectWithType(PinmInterface.TYPE_MC);
		if (pm.hasCloudConnect()){
			PinmOverMC.MCParameter newPara = null;

			if (pom == null) {
				newPara = (PinmOverMC.MCParameter)PinmOverMC.CreateParameter();
				newPara.Username = uInfo.loginUser;
				newPara.Password = StringUtils.MD5(StringUtils.MD5(uInfo.pwd));
				newPara.HostUrl = Config.HOST_IP;
				pom = new PinmOverMC(newPara);
			}
			else{
				PinmOverMC.MCParameter para = (PinmOverMC.MCParameter)pom.getParameter();
				if (para.Username.compareTo(uInfo.loginUser) != 0 ||
						para.Password.compareTo(StringUtils.MD5(StringUtils.MD5(uInfo.pwd))) != 0) {
					newPara = (PinmOverMC.MCParameter)PinmOverMC.CreateParameter();
					newPara.Username = StringUtils.MD5(StringUtils.MD5(uInfo.pwd));
					newPara.Password = uInfo.pwd;
					newPara.HostUrl = Config.HOST_IP;
				}else
					newPara = para;
			}
			pm.Connect(pom, newPara, HomeActivity.this);
		}else if (pom != null){
			pm.Disconnect(pom);
		}
		/**连接蓝牙*/
		if (uInfo.bleId != 0 && uInfo.bleKey != null){
			PinmOverBLE.BLEParameter newPara = null;
			PinmOverBLE pob = (PinmOverBLE)pm.getPinmObjectWithType(PinmInterface.TYPE_CSRMESH);
			if (pob == null) {
				newPara = (PinmOverBLE.BLEParameter)PinmOverBLE.CreateParameter();
				newPara.BleId = pm.getUserObject().bleId;
				newPara.BleKeyString = pm.getUserObject().bleKey;
				pob = new PinmOverBLE(newPara);
			}else {
				newPara = (PinmOverBLE.BLEParameter)pob.getParameter();
				if (newPara.BleId != pm.getUserObject().bleId ||
						newPara.BleKeyString.compareTo(pm.getUserObject().bleKey) != 0){
					newPara.BleId = pm.getUserObject().bleId;
					newPara.BleKeyString = pm.getUserObject().bleKey;
				}
			}
			pm.Connect(pob, newPara, HomeActivity.this);
			if (pob.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
				setVisibilityOnTip(true, R.string.home_connect_ble_tip);
		}else{
			HttpUserInfo hui = new HttpUserInfo();
			HttpUserInfo.bleHttpRequest req = (HttpUserInfo.bleHttpRequest)hui.updateBleInfo(this);
			req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
				@Override
				public void onRequestStart(HttpRequest req) {
				}

				@Override
				public void onRequestResult(HttpRequest req) {
					if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED){
						ToastUtils.showToast(HomeActivity.this, R.string.home_bluetooth_info_invalid);
						return;
					}
					PinmOverBLE.BLEParameter newPara = null;
					HttpUserInfo.bleHttpRequest bleReq = (HttpUserInfo.bleHttpRequest)req;
					PinmOverBLE blePinmConnect = (PinmOverBLE) pm.getPinmObjectWithType(PinmInterface.TYPE_CSRMESH);
					if (blePinmConnect == null) {
						newPara = (PinmOverBLE.BLEParameter)PinmOverBLE.CreateParameter();
						newPara.BleId = bleReq.bleId;
						newPara.BleKeyString = bleReq.bleKey;
						blePinmConnect = new PinmOverBLE(newPara);
					} else {
						newPara = (PinmOverBLE.BLEParameter)blePinmConnect.getParameter();
						newPara.BleId = bleReq.bleId;
						newPara.BleKeyString = bleReq.bleKey;
					}
					if (pm.getUserObject() != null) {
						pm.getUserObject().bleId = bleReq.bleId;
						pm.getUserObject().bleKey = bleReq.bleKey;
					}
					pm.Connect(blePinmConnect, newPara, HomeActivity.this);
					if (blePinmConnect.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
						setVisibilityOnTip(true, R.string.home_connect_ble_tip);

				}
			});
			PISHttpManager.getInstance(this).request(req);
		}

	}


	private boolean checkPermission(String permission){
		//检查是否具有BLE访问权限
		try {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				int hasWriteContactsPermission = checkSelfPermission(permission);
				if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
			return true;
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}

		return true;
	}





	private boolean checkAllPermission(){
//		String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//				Manifest.permission.READ_PHONE_STATE};
		String[] permissions = permissionsOfBlueTooth;
		for (String permission : permissions){
			if (!checkPermission(permission))
				return false;
		}
		return true;
	}

	private void requestPermission(String[] permissions){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(permissions, REQUEST_CODE_ASK_BLUETOOTH);
		}
	}

	private void requestAllPermission(){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
//			, Manifest.permission.READ_PHONE_STATE}
 			String[] permissions = permissionsOfBlueTooth;
			requestPermissions(permissions, REQUEST_CODE_ASK_BLUETOOTH);
		}
	}

	private void initObject(UserInfo uInfo){
		setFragments();
		if (classifyView != null)
			classifyView.setVisibility(View.GONE);
		setListener();

		//连接蓝牙设备
		try {
			connectPipa();
			pm.Start();
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}
	}
	/**
	 * 初始化组件
	 */
	private void initView() {
		manager = getFragmentManager();
		btnAddDevice = findViewById(R.id.home_btn_add_device);
		emptyListView = findViewById(R.id.empty_page);
		fragmentManager = (LinearLayout) findViewById(R.id.fragment_manager);
		backBtn = (Button) findViewById(R.id.title_back);
		logoBtn = (ImageButton) findViewById(R.id.title_logo);
		tvTitle = (TextView) findViewById(R.id.title_name);
		classifyView = (ProductClassifyView) findViewById(R.id.home_classify);
		upgradeDialog = findViewById(R.id.home_dialog_bg);
		addBtn = (ImageButton) findViewById(R.id.title_add);

		addBtn.setVisibility(View.VISIBLE);
		emptyListView.setVisibility(View.GONE);
		backBtn.setVisibility(View.GONE);
		logoBtn.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.title_device);
		upgradeDialog.setVisibility(View.GONE);
	}

	/**
	 * 设置片段
	 */
	private void setFragments() {
		if (fragments == null)
			fragments = new SparseArray<>(5);
		fragments.clear();
		fragments.put(PISManager.PISERVICE_CATEGORY_SOCKET,
				manager.findFragmentById(R.id.home_switchfragment));
		fragments.put(PISManager.PISERVICE_CATEGORY_LIGHT,
				manager.findFragmentById(R.id.home_lightfragment));
		fragments.put(PISManager.PISERVICE_CATEGORY_BRIDGE,
				manager.findFragmentById(R.id.home_blueLinerfragment));
		fragments.put(PISManager.PISERVICE_CATEGORY_REMOTER,
				manager.findFragmentById(R.id.home_remoterfragment));

		android.app.FragmentTransaction ft = manager.beginTransaction();
		for(int i = 0; i < fragments.size(); i++){
			ft.hide(fragments.valueAt(i));
		}
		ft.commitAllowingStateLoss();
	}

	private void refreshListView() {
		if(pm==null)
			return;
		/**刷新不能太频繁*/
		if (System.currentTimeMillis() - preTime < 10)
			return;
		preTime = System.currentTimeMillis();

		try{
			//更新ProductClassifyView状态
			// TODO LEE 等列表获取
			//更新Fragments状态
			ArrayList<PISBase[]> list = new ArrayList<PISBase[]>();

			List<GeneralDeviceModel[]> generalDeviceModels = GeneralDataManager.getInstance().getGeneralDeviceAndGroups(curShowListType);

			Fragment fragment = fragments.get(curShowListType);
			if (fragment == null){
				return;
//				throw new IllegalArgumentException("fragment is null, curShowListType = " + curShowListType);
			}
			switch (curShowListType){
				case PISManager.PISERVICE_CATEGORY_SOCKET:
					((SwitchFragment) fragment).setSwitchAdapter(list);
					break;
				case PISManager.PISERVICE_CATEGORY_LIGHT:
					((LightControlFragment) fragment).setLightAdapter(generalDeviceModels);
					break;
				case PISManager.PISERVICE_CATEGORY_BRIDGE:
					break;
				case PISManager.PISERVICE_CATEGORY_REMOTER:
					((RemoterFragment) fragment).setRemoterAdapter(list);
					break;
			}
		}
		catch (Exception e){
			PgyCrashManager.reportCaughtException(HomeActivity.this, e);
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:
//			pm.Stop();
			finish();
			break;
		case R.id.menu_add_device:

		case R.id.home_btn_add_device:
			startActivity(new Intent(this, MipcaCaptureActivity.class));
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		// 添加设备
		case R.id.title_add:
			// TODO LEE 添加设备按钮
			startActivityForResult(new Intent(this, AddDevicesActivity.class), REQUEST_DEVICE_ADD);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		default:
			break;
		}

	}


	private void autoConnect(boolean update) {
		TelinkLog.d("main auto connect");
		List<DeviceInfo> deviceInfoList = MyApplication.getInstance().getMesh().devices;

		Set<String> targets = new HashSet<>();
		if (deviceInfoList != null) {
			for (DeviceInfo deviceInfo : deviceInfoList) {
				targets.add(deviceInfo.macAddress);
			}
		}

		AutoConnectParameters parameters = AutoConnectParameters.getDefault(targets);
		parameters.setScanMinPeriod(0);
		if (update) {
			MeshService.getInstance().updateAutoConnectParams(parameters);
		} else {
			MeshService.getInstance().autoConnect(parameters);
		}

	}

//	public void showWaitingDialog(String tip) {
//		if (mWaitingDialog == null) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			View dialogView = LayoutInflater.from(this).inflate(R.layout.view_dialog_waiting, null);
//			waitingTip = dialogView.findViewById(R.id.waiting_tips);
//			builder.setView(dialogView);
//			builder.setCancelable(false);
//			mWaitingDialog = builder.create();
//		}
//		if (waitingTip != null) {
//			waitingTip.setText(tip);
//		}
//		mWaitingDialog.show();
//	}

	public void dismissWaitingDialog() {
		if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
			mWaitingDialog.dismiss();
		}
	}

	/**
	 * 设置添加设备或者分组的弹出框
	 * 
	 * @param view
	 */
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void setPopupWindow(View view) {
		View v = LayoutInflater.from(HomeActivity.this).inflate(
				R.layout.popup_add, null);
		Button addDeviceBtn = (Button) v.findViewById(R.id.popup_adddevice);
		Button addGroupBtn = (Button) v.findViewById(R.id.popup_addgroup);
		addDeviceBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PinmInterface pif = pm.getPinmObjectWithType(PinmInterface.TYPE_CSRMESH);
				if (pif == null || pif.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
					ToastUtils.showToast(HomeActivity.this, "蓝牙设备尚未连接成功！");
				else {
					startActivity(new Intent(HomeActivity.this,
							AddDevicesActivity.class));
					HomeActivity.this.overridePendingTransition(
							R.anim.anim_in_from_right, R.anim.anim_out_to_left);
				}
			}
		});
		addGroupBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (null == mcm) {
//					mcm = pm.getPisMcmObject();
//				}
//				if (mcm != null) {
//					String str = HomeActivity.this
//							.getString(R.string.home_newgroup);
//					short groupId = 0;
//					if (pm != null && pm.getDeviceGroupInfors() != null) {
//						groupId = (short) (pm.getDeviceGroupInfors().size() + 1);
//					} else {
//						groupId++;
//					}
//					if (curShowListType == SWITCH_TYPE) {
//						mcm.addDeviceGroup(groupId, (byte) 0x10, (byte) 0x02,
//								str, true);
//					} else if (curShowListType == BUBBLE_TYPE) {
//						mcm.addDeviceGroup(groupId, (byte) 0x10, (byte) 0x03,
//								str, true);
//					}
//				}
			}
		});
		addDialog = new PopupWindow(v, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		addDialog.setOutsideTouchable(true);
		addDialog.setFocusable(true);
		addDialog.setBackgroundDrawable(new BitmapDrawable());

	}

	@Override
	public void performed(Event<String> event) {
		switch (event.getType()) {
			case MeshEvent.EVENT_TYPE_MESH_EMPTY:
				TelinkLog.d(TAG + "#EVENT_TYPE_MESH_EMPTY");
				break;
			case MeshEvent.EVENT_TYPE_DISCONNECTED:
				handler.removeCallbacksAndMessages(null);
				break;
			case MeshEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN:
				// get all device on off status when auto connect success
//
				getTelinkOnOff();
				break;
			case MeshController.EVENT_TYPE_SERVICE_DESTROY:
				TelinkLog.d(TAG + "#EVENT_TYPE_SERVICE_DESTROY");
				break;
			case MeshController.EVENT_TYPE_SERVICE_CREATE:
				TelinkLog.d(TAG + "#EVENT_TYPE_SERVICE_CREATE");
				autoConnect(false);
				isServiceCreated = true;
				break;
			case CommandEvent.EVENT_TYPE_CMD_PROCESSING:
//                showWaitingDialog("CMD processing");
				break;
			case CommandEvent.EVENT_TYPE_CMD_COMPLETE:
				dismissWaitingDialog();
				break;

			case CommandEvent.EVENT_TYPE_CMD_ERROR_BUSY:
				TelinkLog.w("CMD busy");
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toastMsg("CMD fail, Busy!");
                    }
                });*/
				break;

			case NotificationEvent.EVENT_TYPE_KICK_OUT_CONFIRM:
				autoConnect(true);
				break;
		}
	}

	public void getTelinkOnOff(){
		if(MeshService.getInstance() == null)return;
		AppSettings.ONLINE_STATUS_ENABLE = MeshService.getInstance().getOnlineStatus();
		if (!AppSettings.ONLINE_STATUS_ENABLE) {
			MeshService.getInstance().getOnOff(0xFFFF, 0, null);
		}
		sendTimeStatus();
	}


	public void sendTimeStatus() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				long time = MeshUtils.getTaiTime();
				int offset = UnitConvert.getZoneOffset();
				final int eleAdr = 0xFFFF;
				MeshService.getInstance().sendTimeStatus(eleAdr, 1, time, offset, null);
			}
		}, 1500);

	}
}
