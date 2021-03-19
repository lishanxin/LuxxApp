package net.senink.seninkapp.ui.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.GeneralDataManager;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.telink.view.IconGenerator;
import net.senink.seninkapp.ui.IconUtil;
import net.senink.seninkapp.ui.cache.CacheManager;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.entity.DeviceInfo;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.PictureUtils;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.light.MeshService;

import net.senink.seninkapp.R;


import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.listview.SwipeMenu;
import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;

public class DeviceListActivity extends BaseActivity implements
		OnClickListener {
	public final static String TAG = "DeviceListActivity";
	public final static int REQUEST_CODE_DEVICEINFO = 1001;
	// 加载框显示的最长时间
	private final static int SHOW_MAX_TIME = 10000;
	// 获取数据超时
	private final static int MSG_TIMEOUT = 10;
	// 刷新图片数据
	private final static int MSG_REFRESH_IMAGEURL = 11;
	// 刷新列表
	private final static int MSG_REFRESH_VIEWS = 12;
	// 解绑失败
	protected static final int MSG_DEVUNBIND_FAILED = 13;
	// 解绑成功
	protected static final int MSG_DEVUNBIND_SUCCESS = 14;
	// 获取设备对应的图片的url
	private static final int MSG_GET_PICTURE = 15;
	private Button backBtn;
	private TextView tvTitle;
	private ImageView ivTitle;
	private SwipeMenuListView listView;
	private ImageView ivLoading;
	private RelativeLayout loadingLayout;
	private PISManager pm;
	private PISMCSManager mcm;
	private DeviceListsAdater adapter;
	private List<GeneralDeviceModel> list;
	private AnimationDrawable anima;

	private GeneralDeviceModel mSelectedDev;
	// 线程池
	private ExecutorService threadPool;
//	private onFeedbackListener listener = new onFeedbackListener() {
//
//		@Override
//		public void onNetSecurity() {
//
//		}
//
//		@Override
//		public void onDisconnected() {
//           mHandler.sendEmptyMessage(MSG_REFRESH_VIEWS);
//		}
//
//		@Override
//		public void onConnected() {
//			 mHandler.sendEmptyMessage(MSG_REFRESH_VIEWS);
//		}
//	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_VIEWS:
				if (msg.arg1 != 1) {
					mHandler.removeMessages(MSG_TIMEOUT);
				}
				list = getAllGeneralDevices();
				if (adapter == null) {
					adapter = new DeviceListsAdater();
					listView.setAdapter(adapter);
				}
				hideLoadingDialog();
				break;
			case MSG_DEVUNBIND_SUCCESS:
				hideLoadingDialog();
				mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
				if (mSelectedDev != null) {
					adapter.removeItem(mSelectedDev);
//					list.remove(mSelectedDev);
//					adapter.notifyDataSetChanged();
				}
				ToastUtils.showToast(DeviceListActivity.this,
						R.string.del_smart_succ);
				break;
			case MSG_DEVUNBIND_FAILED:
				hideLoadingDialog();
				pm.DiscoverAll();
				sendEmptyMessageDelayed(MSG_REFRESH_VIEWS, 1000);
				mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
				ToastUtils.showToast(DeviceListActivity.this,
						R.string.del_smart_fail);
				break;
			case MSG_GET_PICTURE:
				DeviceInfo infor = (DeviceInfo) msg.obj;
				if (!TextUtils.isEmpty(infor.mac)
						&& !TextUtils.isEmpty(infor.classid)) {
					Thread mThread = new Thread(new ImageUrlThread(infor.mac,
							infor.classid));
					setThreadPool();
					threadPool.execute(mThread);
				}
				break;
			case MSG_TIMEOUT:
				hideLoadingDialog();
				if (adapter != null && adapter.getCount() <= 0) {
					ToastUtils.showToast(DeviceListActivity.this,
							R.string.devicelist_tip);
				}
				mHandler.removeMessages(MSG_TIMEOUT);
				break;
			case MSG_REFRESH_IMAGEURL:
				list = getAllGeneralDevices();
				if (null != adapter) {
					adapter.notifyDataSetChanged();
				} else {
					adapter = new DeviceListsAdater();
					listView.setAdapter(adapter);
				}
				break;
			}
		}
	};

	// Todo lee
	private List<GeneralDeviceModel> getAllGeneralDevices(){
//		List<com.telink.sig.mesh.model.DeviceInfo> telinkDevices = MyApplication.getInstance().getMesh().devices;
//		List<PISDevice> pisDevices = pm.AllDevices();
//		List<GeneralDeviceModel> generalDeviceModels = new ArrayList<>();
//		if(telinkDevices != null){
//			for(int i = 0; i< telinkDevices.size(); i++){
//				generalDeviceModels.add(new GeneralDeviceModel(new TelinkBase(telinkDevices.get(i))));
//			}
//		}
//		if(pisDevices != null){
//			for(int i = 0; i< pisDevices.size(); i++){
//				generalDeviceModels.add(new GeneralDeviceModel(pisDevices.get(i)));
//			}
//		}
		return GeneralDataManager.getInstance().getGeneralDevice();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devicelist);
		pm = PISManager.getInstance();
		mcm = pm.getMCSObject();
		setThreadPool();
//		controller = MeshController.getInstance(this);

		mHandler.sendEmptyMessage(MSG_REFRESH_VIEWS);
		initView();
		setListener();
	}

	private void setThreadPool() {
		if (null == threadPool) {
			threadPool = Executors.newFixedThreadPool(5);
		}
	}

	private void setMcM() {
//		if (mcm.getBindedDeviceMacList().size() == 0) {
//			mcm.setBindedDeviceMacList(CacheManager.bindedMacs);
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
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

	@Override
	protected void onPause() {
		super.onPause();
//		controller.setonFeedbackListener(null);
//		pm.setOnPISChangedListener(null);
//		pm.setOnPipaRequestStatusListener(null);
//		saveMacs(macs);
	}

	@SuppressWarnings("unused")
	private void saveBitmap(String classId, Bitmap bm) {
		String path = PictureUtils.saveBitmap(bm, classId, classId);
		CacheManager.imageUrls.put(classId, path);
		SharePreferenceUtils.saveValue(
				DeviceListActivity.this.getApplicationContext(), classId, path);
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		ivTitle = (ImageView) findViewById(R.id.title_logo_center);
		backBtn = (Button) findViewById(R.id.title_back);
		ivLoading = (ImageView) findViewById(R.id.device_loading);
		loadingLayout = (RelativeLayout) findViewById(R.id.device_loading_layout);
		listView = (SwipeMenuListView) findViewById(R.id.device_list);
		tvTitle.setText(R.string.se_bei_list_title);

		tvTitle.setVisibility(View.GONE);
		ivTitle.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.VISIBLE);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		setListViewListener();
	}

	/**
	 * 发送消息获取图片url
	 * 
	 * @param mac
	 * @param classId
	 */
	private void getPicutre(String mac, String classId) {
		if (!TextUtils.isEmpty(mac) && !TextUtils.isEmpty(classId)) {
			DeviceInfo infor = new DeviceInfo();
			infor.mac = mac;
			infor.classid = classId;
			getPicutre(infor);
		}
	}

	/**
	 * 发送消息获取图片url
	 * 
	 * @param infor
	 */
	private void getPicutre(DeviceInfo infor) {
		if (infor != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_GET_PICTURE;
			msg.obj = infor;
			mHandler.sendMessage(msg);
		}
	}

	private class ImageUrlThread implements Runnable {
		private String classId;
		private String mac;

		public ImageUrlThread(String mac, String classid) {
			this.mac = mac;
			this.classId = classid;
		}

		@Override
		public void run() {
			if (!TextUtils.isEmpty(classId)) {
				ArrayList<DeviceInfo> list = HttpUtils.getDeviceInfoDetial(
						DeviceListActivity.this, null, classId, "", "", mac);
				if (list != null && list.size() > 0 && list.get(0) != null) {
					DeviceInfo infor = list.get(0);
					CacheManager.imageUrls.put(classId, infor.img1);
					mHandler.sendEmptyMessage(MSG_REFRESH_IMAGEURL);
				}
			}
		}
	}
//
//	private class DeviceThread implements Runnable {
//		private String mac;
//
//		public DeviceThread(String mac) {
//			this.mac = mac;
//		}
//
//		@Override
//		public void run() {
//			DeviceInfo infor = HttpUtils.getBindedDeviceInfor(
//					DeviceListActivity.this, mac);
//			if (infor != null) {
//				SharePreferenceUtils.saveValue(DeviceListActivity.this, mac,
//						infor.classid);
//				updateList(infor);
//				getPicutre(infor);
//			}
//		}
//	}

	/**
	 * 刷新设备列表
	 * 
	 * @param
	 */
//	private void updateList(DeviceInfo infor) {
//		if (!TextUtils.isEmpty(infor.mac) && list != null && list.size() > 0) {
//			for (PISDevice dev : list) {
////				String macAddr = CommonUtils.getStringOnMacAddr(dev.macAddr);
////				if (infor.mac.equals(macAddr)) {
////					dev.setClassID(infor.classid);
////					dev.mName = infor.name;
////				}
//			}
//		}
//	}

	private void setListViewListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				GeneralDeviceModel device = list.get(arg2);
				if(device.isTelink()){
					Intent intent = new Intent(DeviceListActivity.this,
							DeviceInfoActivity.class);
					intent.putExtra(MessageModel.TELINK_BASE_KEYSTR,
							device.getTelinkBase().getDevice().meshAddress);
					intent.putExtra(TelinkApiManager.IS_TELINK_KEY, device.isTelink());
					startActivityForResult(intent, REQUEST_CODE_DEVICEINFO);
					overridePendingTransition(R.anim.anim_in_from_right,
							R.anim.anim_out_to_left);
				}else{
					Intent intent = new Intent(DeviceListActivity.this,
							DeviceInfoActivity.class);
					intent.putExtra(MessageModel.PISBASE_KEYSTR,
							device.getPisBase().getPISKeyString());
					intent.putExtra(TelinkApiManager.IS_TELINK_KEY, device.isTelink());
					startActivityForResult(intent, REQUEST_CODE_DEVICEINFO);
					overridePendingTransition(R.anim.anim_in_from_right,
							R.anim.anim_out_to_left);
				}


//				PISManager.cacheMap.put(device.getMacAddr(), device);
//				Intent it = new Intent(DeviceListActivity.this,
//						DeviceInfoActivity.class);
//				it.putExtra("mac", device.macAddr);
//				Bitmap bm = getBitmapFromImageView(view);
//				CacheManager.saveItemPicture(bm);
//				startActivity(it);
//				overridePendingTransition(R.anim.anim_in_from_right,
//						R.anim.anim_out_to_left);
			}
		});
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(0xFFFD430A));
				deleteItem.setWidth(Utils.dpToPx(50,
						DeviceListActivity.this.getResources()));
				deleteItem.setTitle(R.string.delete);
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);
		listView.setOnItemChangeListener(new SwipeMenuListView.OnItemChangeListener() {

			@Override
			public void onItemTextChange(int pos, List<TextView> tvList) {

			}

			@Override
			public void onItemIconChange(int pos, List<ImageView> ivList) {

			}
		});
		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					try {
						mSelectedDev = adapter.getItem(position);
					}catch (IndexOutOfBoundsException e){
						mHandler.sendEmptyMessage(MSG_DEVUNBIND_SUCCESS);
						return false;
					}
					if(mSelectedDev.isTelink()){
						com.telink.sig.mesh.model.DeviceInfo telinkDeviceInfo = mSelectedDev.getTelinkBase().getDevice();
						telinkKickOut(telinkDeviceInfo);
					}else{
						// TODO LEE 删除灯
						PISDevice selectPisDevice = mSelectedDev.getPisBase().getDeviceObject();
						pm = PISManager.getInstance();
						mcm = pm.getMCSObject();
						if (selectPisDevice == null || pm == null || mcm == null){
							ToastUtils.showToast(DeviceListActivity.this,
									R.string.devicelist_error_unknow);
							return true;
						}
						showDeleteDialog();

						PipaRequest resetReq = mcm.unbindDevice(selectPisDevice.getMacByte());
						resetReq.setRetry(3);
						resetReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
							@Override
							public void onRequestStart(PipaRequest req) {

							}

							@Override
							public void onRequestResult(PipaRequest req) {
								if(req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
									mHandler.sendEmptyMessage(MSG_DEVUNBIND_SUCCESS);
								else
									mHandler.sendEmptyMessage(MSG_DEVUNBIND_FAILED);
							}
						});
						selectPisDevice.request(resetReq);

						if (selectPisDevice.getStatus() == PISBase.SERVICE_STATUS_ONLINE){
							//reset the device
							PipaRequest ubReq = selectPisDevice.reset(); // mcm.unbindDevice(mSelectedDev.getMacByte());
							ubReq.setRetry(2);
							mcm.request(ubReq);
						}
					}

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

	Handler delayHandler = new Handler();

	private void telinkKickOut(final com.telink.sig.mesh.model.DeviceInfo deviceInfo) {
//        if (MeshService.getInstance().kickOut(deviceInfo))
		list.remove(mSelectedDev);
		adapter.notifyDataSetChanged();
		boolean kickDirect = deviceInfo.macAddress.equals(MeshService.getInstance().getCurDeviceMac());
		boolean kickSent = MeshService.getInstance().resetNode(deviceInfo.meshAddress, null);
		delayHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onKickOutFinish(deviceInfo);
				}
			}, 2 * 1000);
	}
	private void onKickOutFinish(com.telink.sig.mesh.model.DeviceInfo deviceInfo) {
		delayHandler.removeCallbacksAndMessages(null);
		MeshService.getInstance().removeNodeInfo(deviceInfo.meshAddress);
		MyApplication.getInstance().getMesh().removeDeviceByMeshAddress(deviceInfo.meshAddress);
		MyApplication.getInstance().getMesh().saveOrUpdate(getApplicationContext());
		mHandler.sendEmptyMessage(MSG_REFRESH_IMAGEURL);
	}

	/**
	 * 删除加载框
	 */
	private void showDeleteDialog() {
		mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
		if (null == anima) {
			anima = (AnimationDrawable) ivLoading.getBackground();
		}
		anima.start();
		loadingLayout.setVisibility(View.VISIBLE);
		setTimeoutOnMessage();
	}

	private void setTimeoutOnMessage() {
		mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
		Message msg = mHandler.obtainMessage(MSG_DEVUNBIND_FAILED, 1, -1);
		mHandler.sendMessageDelayed(msg, SHOW_MAX_TIME);
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
		if (loadingLayout.getVisibility() == View.VISIBLE) {
			hideLoadingDialog();
		} else {
			backBtn.performClick();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		}
	}

//	private Bitmap bm = null;
	public class DeviceListsAdater extends BaseAdapter {
		public DeviceListsAdater() {
		}

		@Override
		public int getCount() {
			if (null == list) {
				list = new ArrayList<GeneralDeviceModel>();
			}
			return list.size();
		}

		@Override
		public GeneralDeviceModel getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void removeItem(GeneralDeviceModel deviceInfo) {
//			pm.removePISObject(deviceInfo);
			list.remove(deviceInfo);
			notifyDataSetChanged();
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			GeneralDeviceModel generalDeviceModel = getItem(position);
			if (convertView == null) {
				holder = new Holder();
				convertView = LayoutInflater.from(DeviceListActivity.this)
						.inflate(R.layout.device_list_item, null);
				holder.nameTv = (TextView) convertView.findViewById(R.id.name);
				holder.positionTv = (TextView) convertView
						.findViewById(R.id.position);
				holder.macTv = (TextView) convertView.findViewById(R.id.mac);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.deviceInfo = generalDeviceModel;
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if(generalDeviceModel.isTelink()){
				com.telink.sig.mesh.model.DeviceInfo telinkDeviceInfo = generalDeviceModel.getTelinkBase().getDevice();
				holder.macTv.setText(telinkDeviceInfo.macAddress);
				final int deviceType = telinkDeviceInfo.nodeInfo != null && telinkDeviceInfo.nodeInfo.cpsData.lowPowerSupport() ? 1 : 0;
				holder.icon.setImageResource(IconGenerator.getIcon(deviceType, telinkDeviceInfo.getOnOff()));
				holder.nameTv.setText(telinkDeviceInfo.getDeviceName());
			}else{
				PISDevice deviceInfo = (PISDevice) generalDeviceModel.getPisBase().getDeviceObject();
				try {
					List<PISBase> srvs = deviceInfo.getPIServices();
					if (srvs != null && srvs.size()>0){
						holder.nameTv.setText(srvs.get(0).getName());
					}
					else
						holder.nameTv.setText(deviceInfo.getName());
					String location = SharePreferenceUtils.getLocationValue(
							DeviceListActivity.this, "" + deviceInfo.getLocation());
					if (TextUtils.isEmpty(location)) {
						location = DeviceListActivity.this.getResources().getString(
								R.string.no_know);
					}
					holder.positionTv.setText(location);
					String macTemp = DeviceListActivity.this.getResources().getString(
							R.string.no_know);
					if (!TextUtils.isEmpty(deviceInfo.getMacString())) {
						macTemp = deviceInfo.getMacString().replaceAll(":", "-");
					}
					holder.macTv.setText(macTemp);
					// TODO LEE2 灯具图标
					try {
						Drawable sld = IconUtil.getPISIcon(DeviceListActivity.this, generalDeviceModel.getPisBase());
						if (sld != null)
							holder.icon.setImageDrawable(sld);
					} catch (ClassCastException e) {
						PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
					} catch (Exception e) {
						PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
					}

//					if (deviceInfo.getStatus() != PISBase.SERVICE_STATUS_ONLINE) {
//						setGrayOnBackgroud(holder.icon, 0);
//					} else {
//						setGrayOnBackgroud(holder.icon, 1);
//					}

				}catch (Exception e){
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}

			return convertView;
		}

		public class Holder {
			TextView nameTv;
			TextView positionTv;
			ImageView icon;
//			PISDevice deviceInfo;
			GeneralDeviceModel deviceInfo;
			TextView macTv;
		}
	}

	private void setGrayOnBackgroud(ImageView iv, int value) {
		ColorMatrix matrix = new ColorMatrix();
		if (value == 0) {
			matrix.setSaturation(0);
		}
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		iv.setColorFilter(filter);
	}

//	private void refreshListView(PISDevice dev) {
//		if (list != null && !list.isEmpty() && dev != null
//				&& !TextUtils.isEmpty(dev.macAddr)) {
//			int size = list.size();
//			for (int i = 0; i < size; i++) {
//				PISDevice device = list.get(i);
//				String mac = CommonUtils.getStringOnMacAddr(device.macAddr);
//				String macAddr = CommonUtils.getStringOnMacAddr(dev.macAddr);
//				if (mac.equals(macAddr)
//						&& device.getPISKeyString().equals(
//								dev.getPISKeyString())) {
//					if (dev.getPIServices() != null
//							&& dev.getPIServices().size() > 0) {
//						PISBase base = dev.getPIServices().get(0);
//						dev.mName = base.mName;
//						dev.mLocation = base.mLocation;
//						dev.mStatus = base.mStatus;
//					}
//					list.set(i, dev);
//					adapter.notifyDataSetChanged();
//					break;
//				}
//			}
//		}
//	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//		if (pis.mPisType == PISConstantDefine.PIS_TYPE_MCM) {
//			if (reqType == PISMCSManager.PIS_CMD_MCM_DEVBINDGET) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE
//						&& mcm != null) {
//					hideLoadingDialog();
//					List<String> mList = mcm.getBindedDeviceMacList();
//					saveMacs(mList);
//					mHandler.sendEmptyMessage(MSG_REFRESH_VIEWS);
//					LogUtils.i(TAG,
//							"onRequestResult():PISMCSManager.PIS_CMD_MCM_DEVBINDGET ");
//				}
//			} else if (reqType == PISMCSManager.PIS_CMD_MCM_DEVUNBIND) {
//				mcm.getBindDevices(true);
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					LogUtils.i(TAG,
//							"onRequestResult():PISMCSManager.PIS_CMD_MCM_DEVUNBIND succcess ");
//					mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
//				} else {
//					LogUtils.i(TAG,
//							"onRequestResult():PISMCSManager.PIS_CMD_MCM_DEVUNBIND failed ");
//					if (!isReset) {
//						mHandler.sendEmptyMessage(MSG_DEVUNBIND_FAILED);
//					}else{
//						mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
//						isReset = false;
//					}
//				}
//			}
//		} else if (pis.mPisType == PISConstantDefine.PIS_TYPE_DEVICE) {
//			if (reqType == PISDevice.PIS_CMD_DEVICE_INFO_GET) {
//				if(result == PipaRequest.REQUEST_RESULT_COMPLETE){
//					PISDevice device = (PISDevice) pis;
//					refreshListView(device);
//					LogUtils.i(TAG,
//							"onRequestResult():PISMCSManager.PIS_CMD_DEVICE_INFO_GET ");
//				}
//			} else if (reqType == PISDevice.PIS_CMD_DEVICE_FACTORYRESET) {
//				mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					LogUtils.i(TAG,
//							"onRequestResult():PISDevice.PIS_CMD_DEVICE_FACTORYRESET success ");
//					pm.removeDevice(pis);
//					mcm.unBindDevice(mSelectedDev.macAddr, true);
//					mcm.getBindDevices(true);
//				} else if(result == PipaRequest.REQUEST_RESULT_TIMEOUT){
//					setTimeoutOnMessage();
//				}else{
//					LogUtils.i(TAG,
//							"onRequestResult():PISDevice.PIS_CMD_DEVICE_FACTORYRESET failed ");
//					mHandler.sendEmptyMessage(MSG_DEVUNBIND_FAILED);
//				}
//			}
//		}
//	}
//
//	private void saveMacs(List<String> mList) {
//		String macs = StringUtils.getMacsString(mList);
//		SharePreferenceUtils.saveMacs(this, macs);
//	}
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
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//
//	}
//
//	class LoadIcon implements Runnable {
//		private String iconUrl;
//		private ImageView imageView;
//		Bitmap bit;
//
//		public LoadIcon(ImageView imageView, String iconUrl) {
//			this.iconUrl = iconUrl;
//			this.imageView = imageView;
//		}
//
//		@Override
//		public void run() {
//			if (iconUrl != null) {
//				bit = HttpUtils.getImage(DeviceListActivity.this,
//						String.valueOf(iconUrl.hashCode()), iconUrl);
//				if (bit != null) {
//					int radius = (bit.getWidth() < bit.getHeight() ? bit
//							.getWidth() : bit.getHeight()) / 2;
//					bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							imageView.setImageBitmap(bit);
//						}
//					});
//				}
//			}
//		}
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		threadPool.shutdown();
		threadPool = null;
		if (list != null) {
			list.clear();
		}
		adapter = null;
		mHandler.removeMessages(MSG_TIMEOUT);
		mHandler.removeMessages(MSG_DEVUNBIND_FAILED);
	}
  
}
