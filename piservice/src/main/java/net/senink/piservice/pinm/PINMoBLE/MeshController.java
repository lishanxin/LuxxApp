package net.senink.piservice.pinm.PINMoBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;import android.util.SparseArray;
import android.util.SparseIntArray;

import com.csr.mesh.AttentionModelApi;
import com.csr.mesh.ConfigModelApi;
import com.csr.mesh.DataModelApi;
import com.csr.mesh.MeshService;

import net.senink.piservice.BuildConfig;
import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.pinm.PINMoBLE.interfaces.AssociationListener;
import net.senink.piservice.pinm.PINMoBLE.interfaces.BlueToothLightListener;
import net.senink.piservice.pinm.PINMoBLE.interfaces.DataListener;
import net.senink.piservice.pinm.PINMoBLE.interfaces.InfoListener;
import net.senink.piservice.pinm.PINMoBLE.interfaces.RemovedListener;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeshController implements BlueToothLightListener {
	public static final String TAG = "MeshController";
	public static final int ATTENTION_DURATION_MS = 20000;
	public static final int MAX_TIME = 5000;
	public  static final int CHK_TIME = 500;
	private static final long RSSI_THRESHOLD = -70;

	private static long delayConnect = CHK_TIME*10;

	// The address to send packets to.
	private int mSendDeviceId = 0x10000;
	private SparseIntArray mDeviceIdtoUuidHash = new SparseIntArray();
	private SparseArray<String> mUuidHashToAppearance = new SparseArray<String>();
	private HashSet<String> whiteListToxinClass = new HashSet<String>();
	private HashSet<String> mConnectedDevices = new HashSet<String>();
	private HashMap<String, BluetoothDevice> mDevicePool = new HashMap<String, BluetoothDevice>();
	private HashMap<String, Long> mRSSIPool = new HashMap<String, Long>();
	private HashMap<String, Long> mDeviceConnectTimes = new HashMap<String, Long>();
	private HashMap<String, Long> mDeviceLastLiveTimes = new HashMap<String, Long>();
	private MeshService mService = null;
	private String mConnectedBridge = null;
	public int mAssociationTransactionId = -1;
	public boolean mAutoWeakupDevice = false;
	public boolean mAllowEnableBluetooth = true;

	public static int bleDeviceId = (int) 0x8000;
	public static String bleKey = "-1";
	private onFeedbackListener feedbackListener;
	private int mRemovedUuidHash;
	private int mRemovedDeviceId;
	private InfoListener mInfoListener;
	private AssociationListener mAssListener;
	private RemovedListener mRemovedListener;
	byte[] vid;
	byte[] pid;
	byte[] version;
	private Context mContext;
	private static MeshController control;
	// 是否连接异常
	// private boolean hasProblem = true;
	/**
	 * Handle messages from mesh service.
	 */
	private static Handler mMeshHandler = null;

	public static boolean isConnected = false;
	private static boolean isStarted = false;
	private static int bleOldStatus = BluetoothAdapter.STATE_ON;
//	public PinmOverBLE mPinmBle = null;
	// 是否已经断开服务
//	private boolean isStopOnService = false;
    //开始连接时间
//	private long startTimeOnConnect;
	//连接成功或者失败或者超时的时间
	private long endTimeOnConnect;
	// 蓝牙通讯记录ID(自增量)
	private long bleIdOnRecord = 0;    
	// 连接结果(成功或失败)
	private String connectResult = null; 
	// 失败原因
	private String failedReason = null;     
    // 请求连接耗时(无论成功或失败都要有值)            
	private long requestTime = 0;    
	// 断开时间
	private long disconnectTime = 0; 
	// 断开原因(如失败则断开时间和断开原因则不需要填写)
	private String disconnectReason = null;   
	// 本次通讯时长
	private long recordTime = 0;                              
    // 发送PIPA数据包个数
	private long countOnSend = 0; 
	// 接收PIPA数据包个数
	private long countOnReceive = 0;         
	// 发送PIPA数据总量
	private long sumOnSend = 0;    
	// 接收PIPA数据总量
	private long sumOnReceive = 0; 
	//连接成功的时间
	private long connectTime = 0;
	private static ExecutorService threadPool;


	private MeshController(Context act) {
		if (act != null) {
			mContext = act; // (activity.getApplication()).getHomeActivity();
//				bleIdOnRecord = SharePreferenceUtils.getLongValue(activity, Constant.BLE_COM_ID);
			Intent bindIntent = new Intent(mContext, MeshService.class);
			act.startService(bindIntent);

			whiteListToxinClass.add("xinLight");
			whiteListToxinClass.add("xinColor");
			whiteListToxinClass.add("xinCenter");
			whiteListToxinClass.add("xinBulb");
			whiteListToxinClass.add("LuxxLight");

//			whiteListToxinClass.add("xinLeanTool");
		}
	}

	public static MeshController getInstance(Context act) {
		if (null == control) {
//			if (PISManager.getDefaultContext() != null)
//				control = new MeshController((Activity)PISManager.getDefaultContext());
//			else
				control = new MeshController(act);
		}
		if (null == threadPool) {
			threadPool = Executors.newFixedThreadPool(5);
		}
		return control;
	}

	/**
	 * 开始连接CSRMesh网络
	 */
	public void start() {
		if (isStarted)
			return;
		isStarted = true;

		PisInterface.bleConnected((short)PinmInterface.PINM_CONNECT_STATUS_CONNECTING);

		//检查是否完成初始化动作（绑定服务）
		try {
			Intent bindIntent = new Intent(mContext, MeshService.class);
			LogUtils.e("mesh-connect", "begin to bind mesh service!!!");

			if (!mContext.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE))
				LogUtils.e("mesh-connect", "bind mesh service failed!!!" + mContext);
			else
				LogUtils.e("mesh-connect", "bind mesh service successed!!!" + mContext);
		} catch (Exception exc) {
			Log.e("mesh-connect", "bind mesh service threw exception!", exc);
		}
		mRSSIPool.clear();
		mDevicePool.clear();
		mConnectedDevices.clear();
	}

	/**
	 * 关闭CSRMesh连接
	 */
	public void stop() {
		if (!isStarted)
			return;
		isStarted = false;

		cancelTimer();

		isConnected = false;
		try {
			BluetoothAdapter.getDefaultAdapter().stopLeScan(mScanCallBack);
			if (mService != null) {
				setContinuousScanning(false);
				mService.disconnectBridge();
//				mService.setHandler(null);
			}
			if (mConnectedBridge != null)
				mConnectedBridge = null;
			LogUtils.e("mesh-connect", "unbind mesh service!!!" + mContext);
			control = null;
			mContext.unbindService(mServiceConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (feedbackListener != null) {
			feedbackListener.onDisconnected();
		}
	}

	private void connect() {

		if (mMeshHandler == null || mService == null)
			return;

		if (mConnectedDevices.size() > 5 || mDevicePool.size() == 0)
			return;

		if (mConnectedDevices.size() == 1 && (mConnectedBridge == null || !mAutoWeakupDevice))
			return;

		try {
			//按照RSSI大小排序
			List<String> list = new LinkedList<String>();
			list.addAll(mRSSIPool.keySet());

			Collections.sort(list, new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					Long lhsRssi = mRSSIPool.get(lhs);
					Long rhsRssi = mRSSIPool.get(rhs);

					if (lhsRssi == null || lhsRssi == null)
						return -1;
					if (lhsRssi < rhsRssi)
						return 1;
					else if (lhsRssi == rhsRssi)
						return 0;
					else
						return -1;
				}
			});


			for (int i = 0; i < list.size(); i++){
				String devAddress = list.get(i);
				if (!mConnectedDevices.contains(devAddress)){
					BluetoothDevice dev = mDevicePool.get(devAddress);
					Long rssiValue = mRSSIPool.get(devAddress);
					if (dev != null && rssiValue != null &&
							(rssiValue > RSSI_THRESHOLD || delayConnect < CHK_TIME)){
						LogUtils.i("mesh-connect", "begin to connect[ " +
								dev.getAddress() + "]" +
								" rssi[" + rssiValue + "]" );
						long curTime = System.currentTimeMillis();
						mDeviceConnectTimes.put(devAddress, curTime);
						mService.connectBridge(dev);
						if (mConnectedBridge == null)
							PisInterface.bleConnected((short)PinmInterface.PINM_CONNECT_STATUS_CONNECTING);
						delayConnect = CHK_TIME*5;
						mConnectedDevices.add(devAddress);
						break;
					}
				}
			}
			delayConnect -= CHK_TIME;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTimer() {
		if (mMeshHandler != null) {
			mMeshHandler.removeMessages(CHK_TIME);
			mMeshHandler.postDelayed(timerThread, CHK_TIME);
		}
	}

	private Runnable timerThread = new Runnable() {

		@Override
		public void run() {
//			mContext.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
					if (!isStarted ) {
						setTimer();
						return;
					}
					//检查蓝牙状态
					BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
					int blestatus = blueadapter.getState();
					if (blueadapter != null && bleOldStatus != blestatus) {
						switch (blestatus){
							case BluetoothAdapter.STATE_TURNING_OFF:
								LogUtils.i("mesh-connect", "bluetooth adapter turning off");
								break;
							case BluetoothAdapter.STATE_OFF:
								if (mConnectedBridge == null)
									blueadapter.stopLeScan(mScanCallBack);
								if (mAllowEnableBluetooth)
									mAllowEnableBluetooth = blueadapter.enable();
								break;
							case BluetoothAdapter.STATE_TURNING_ON:
								LogUtils.i("mesh-connect", "bluetooth adapter turning on");
								break;
							case BluetoothAdapter.STATE_ON:
								if (blueadapter.startLeScan(mScanCallBack))
									LogUtils.i("mesh-connect", "bluetooth adapter start scan successed");
								else
									LogUtils.i("mesh-connect", "bluetooth adapter start scan failed");
								break;
						}
						bleOldStatus = blestatus;
						setTimer();
						return;
					}

					//如果长时间未发现任何设备，则重启蓝牙

					//连接蓝牙bridge设备，并检查连接的状态
					connect();

					//检查自动唤醒连接的状态
					HashMap<String, BluetoothDevice> bleDevices =
							(HashMap<String, BluetoothDevice>)mDevicePool.clone();
					for (String s:bleDevices.keySet()){
						if (mConnectedBridge != null && mConnectedBridge.compareTo(s) == 0){
							continue;
						}
						try {
							Long liveTime = mDeviceLastLiveTimes.get(s);
							if (liveTime != null){
								long lastLiveSpen = System.currentTimeMillis() - liveTime;
								if (lastLiveSpen > 5 * 1000 && mDevicePool.keySet().contains(s)){
									mDevicePool.remove(s);
									mRSSIPool.remove(s);
								}
							}
							Long startTime = mDeviceConnectTimes.get(s);
							if (startTime != null){
								long consumingTime = System.currentTimeMillis() - startTime;
								if (consumingTime > 20 * 1000 /*10S*/ && mConnectedDevices.contains(s)){
									LogUtils.i("mesh-connect", "[" + s + "]timerout: disconnect the device");
									mConnectedDevices.remove(s);
									try {
										mService.disconnectBridge(s);
									}catch (IllegalArgumentException e){
//										mService.disconnectBridge();
										//复位蓝牙设备？？
										e.printStackTrace();
									}
									delayConnect = CHK_TIME * 10;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					//下一次的定时操作
					setTimer();
				}
//			});
//		}
	};

	private void cancelTimer() {
		if (mMeshHandler != null) {
			mMeshHandler.removeCallbacks(timerThread);
		}
	}

	/**
	 * Callback for changes to the state of the connection.
	 */
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((MeshService.LocalBinder) rawBinder).getService();
			if (mService != null) {
				LogUtils.w(TAG, "Loop is " + Looper.getMainLooper() + "threadid = " + Thread.currentThread().getId());
				mMeshHandler = new MeshHandler(Looper.getMainLooper());

				mService.setHandler(mMeshHandler);
				mService.setMeshListeningMode(true, true);
				LogUtils.i(TAG, "ttl = " + mService.getTTL());

				setTimer();
			}
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (!adapter.startLeScan(mScanCallBack)) {
				LogUtils.i("mesh-connect", " start LE device scan failed");
			}else
				LogUtils.i("mesh-connect", " start LE device scan successed");
		}

		public void onServiceDisconnected(ComponentName classname) {
			isConnected = false;
			PisInterface.bleConnected((short)PinmInterface.PINM_CONNECT_STATUS_DISCONNECTED);
			isStarted = false;
			mConnectedBridge = null;
			mMeshHandler.removeCallbacksAndMessages(null);
			setContinuousScanning(false);

			mService.setMeshListeningMode(false, false);
			mService.setHandler(null);
			mService = null;


			if (feedbackListener != null) {
				feedbackListener.onDisconnected();
			}
		}
	};

	private String meshDeviceLocalName (byte[] adv_data) {
		ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
		while (buffer.remaining() > 2) {
			byte length = buffer.get();
			if (length == 0)
				break;
			byte type = buffer.get();
			length -= 1;
			switch (type) {
				case 0x08: // Short local device name
				case 0x09: // Complete local device name
					byte sb[] = new byte[length];
					buffer.get(sb, 0, length);
					String localName = new String(sb).trim();

					return localName;
				case (byte) 0xFF: // Manufacturer Specific Data
					short manufacturer = buffer.getShort();
					length -= 2;
					break;
				default: // skip
					break;
			}
			if (length > 0) {
				buffer.position(buffer.position() + length);
			}
		}
		return null;
	}
	private boolean containXinClass(String devName){
		for(String s:whiteListToxinClass){
			if (devName.startsWith(s))
				return true;
		}
		return false;
	}
	private LeScanCallback mScanCallBack = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
				try {
					String deviceName = meshDeviceLocalName(scanRecord);
					String deviceAddr = device.getAddress();

					if (deviceName == null || deviceAddr == null)
						return;

					//调试代码，确保连接设备是指定MAC地址设备，以验证其低功耗唤醒功能
					if (mDevicePool.size() < 5 &&
							((containXinClass(deviceName) && mConnectedBridge == null) ||
									(mAutoWeakupDevice && mConnectedBridge != null))) {
						mDevicePool.put(deviceAddr, device);
						LogUtils.i("mesh-connect", "device[" + device.getName() + "," + deviceName +
								"] Mac[" + deviceAddr + "] in array[" + mDevicePool.size() +
								"] rssi["+ rssi + "]");
					}
					if (mDevicePool.containsKey(deviceAddr)) {
						mRSSIPool.put(deviceAddr, (long) rssi);
						mDeviceLastLiveTimes.put(deviceAddr, System.currentTimeMillis());
						mService.processMeshAdvert(device, scanRecord, rssi);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	};

	private class MeshHandler extends Handler {

		public MeshHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			int uuidHash = 0;
			switch (msg.what) {
				case MeshService.MESSAGE_LE_CONNECTED: {
					String bridgeAddress = msg.getData().getString(MeshService.EXTRA_DEVICE_ADDRESS);
					int numConnections = msg.getData().getInt(MeshService.EXTRA_NUM_CONNECTIONS);

					if (mConnectedBridge != null) {
						if (mConnectedBridge.compareTo(bridgeAddress) != 0) {
							LogUtils.i("mesh-connect", "auto weakup bridge MAC = " + bridgeAddress);
							mService.disconnectBridge(bridgeAddress);
						}
					} else {
						mConnectedBridge = bridgeAddress;
						isConnected = true;


						if (!mAutoWeakupDevice)
							BluetoothAdapter.getDefaultAdapter().stopLeScan(mScanCallBack);

						if (!mConnectedDevices.contains(bridgeAddress))
							mConnectedDevices.add(bridgeAddress);
						if (!mDevicePool.containsKey(bridgeAddress)) {
							BluetoothDevice dev = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bridgeAddress);
							if (dev != null) {
								mDevicePool.put(bridgeAddress, dev);
							}
						}

						try {
							PisInterface.bleConnected((short) PinmInterface.PINM_CONNECT_STATUS_CONNECTED);
							setMeshParameter(bleDeviceId, bleKey);
						} catch (Exception e) {
							PisInterface.bleConnected((short) PinmInterface.PINM_CONNECT_STATUS_DISCONNECTED);
							if (mConnectedBridge != null) {
								mService.disconnectBridge(mConnectedBridge);
							}
							e.printStackTrace();
						}

					}

					if (feedbackListener != null) {
						feedbackListener.onConnected();
					}

					LogUtils.i("mesh-connect", "=========connected bridge MAC address ="
							+ bridgeAddress + ",numConnections = " + numConnections);
					break;
				}
				case MeshService.MESSAGE_BRIDGE_CONNECT_TIMEOUT:
				case MeshService.MESSAGE_LE_DISCONNECTED: {
					String address = msg.getData().getString(
							MeshService.EXTRA_DEVICE_ADDRESS);
					if (mConnectedBridge != null &&
							address != null &&
							mConnectedBridge.compareTo(address) == 0) {
						isConnected = false;
						PisInterface.bleConnected((short) PinmInterface.PINM_CONNECT_STATUS_DISCONNECTED);

						mConnectedBridge = null;

						if (!mAutoWeakupDevice && isStarted)
							BluetoothAdapter.getDefaultAdapter().startLeScan(mScanCallBack);

						if (feedbackListener != null) {
							feedbackListener.onDisconnected();
						}
						delayConnect = CHK_TIME * 10;
					}
					if (!TextUtils.isEmpty(address)) {
						if (mDevicePool.containsKey(address)) {
							mDevicePool.remove(address);
						}
						if (mRSSIPool.containsKey(address)) {
							mRSSIPool.remove(address);
						}
						if (mConnectedDevices.contains(address))
							mConnectedDevices.remove(address);
					}
					if (mConnectedBridge != null && mService != null) {
						mService.setControllerAddress(bleDeviceId);
						mService.setNetworkPassPhrase(bleKey);
						mService.setContinuousLeScanEnabled(true);
					}
					LogUtils.i("mesh-connect", "======disconnect ===== ");

					break;
				}
				case MeshService.MESSAGE_LE_DISCONNECT_COMPLETE: {
//				int numConnections = msg.getData().getInt(
//						MeshService.EXTRA_NUM_CONNECTIONS);
//
					isConnected = false;
					PisInterface.bleConnected((short) PinmInterface.PINM_CONNECT_STATUS_DISCONNECTED);
					mConnectedBridge = null;
					delayConnect = CHK_TIME * 10;

					mDevicePool.clear();
					mConnectedDevices.clear();
					mRSSIPool.clear();

//				setOffline();
//				setInfors(isConnected, 1, true);

					if (!mAutoWeakupDevice && isStarted)
						BluetoothAdapter.getDefaultAdapter().startLeScan(mScanCallBack);

					if (feedbackListener != null) {
						feedbackListener.onDisconnected();
					}

					LogUtils.i("mesh-connect", "======disconnect complete=====");

					break;
				}
				case MeshService.MESSAGE_REQUEST_BT:
					BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
					LogUtils.i("mesh-connect", "======MESSAGE_REQUEST_BT======[" + blueadapter.getState() + "]");
					switch (blueadapter.getState()) {
						case 10: {
							stop();
							break;
						}
						case 11: {
							break;
						}
						case 12: {
							start();
							break;
						}
						case 13: {
							break;
						}
					}
//				init();
					break;
				case MeshService.MESSAGE_TIMEOUT:
					int expectedMsg = msg.getData().getInt(
							MeshService.EXTRA_EXPECTED_MESSAGE);
					int id;
					int meshRequestId;
					if (msg.getData().containsKey(MeshService.EXTRA_UUIDHASH_31)) {
						id = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
					} else {
						id = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
					}
					meshRequestId = msg.getData().getInt(
							MeshService.EXTRA_MESH_REQUEST_ID);
					onMessageTimeout(expectedMsg, id, meshRequestId);
					break;
				case MeshService.MESSAGE_DEVICE_DISCOVERED: {
					ParcelUuid uuid = msg.getData().getParcelable(
							MeshService.EXTRA_UUID);
					uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
					int rssi = msg.getData().getInt(MeshService.EXTRA_RSSI);
					int ttl = msg.getData().getInt(MeshService.EXTRA_TTL);
					if (mRemovedListener != null && mRemovedUuidHash == uuidHash) {
						mRemovedListener.onDeviceRemoved(mRemovedDeviceId, true);
						mRemovedListener = null;
						mRemovedUuidHash = 0;
						mRemovedDeviceId = 0;
						mService.setDeviceDiscoveryFilterEnabled(false);
						removeCallbacks(removeDeviceTimeout);
					} else if (mAssListener != null) {
						mAssListener.newUuid(uuid.getUuid(), uuidHash, rssi, ttl);
					}
					break;
				}
				case MeshService.MESSAGE_DEVICE_APPEARANCE: {
					byte[] appearance = msg.getData().getByteArray(
							MeshService.EXTRA_APPEARANCE);
					String shortName = msg.getData().getString(
							MeshService.EXTRA_SHORTNAME);
					if (shortName != null) {
						byte[] data = ByteUtilBigEndian.stringToBytes(shortName);
						shortName = new String(data);
					}
//				LogUtils.i("mesh-connect", "======unassociated device["+ shortName +"]");
					uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
					if (mRemovedListener != null && mRemovedUuidHash == uuidHash) {
						mRemovedListener.onDeviceRemoved(mRemovedDeviceId, true);
						mRemovedListener = null;
						mRemovedUuidHash = 0;
						mRemovedDeviceId = 0;
						mService.setDeviceDiscoveryFilterEnabled(false);
						removeCallbacks(removeDeviceTimeout);
					} else if (mAssListener != null) {
						mUuidHashToAppearance.put(uuidHash, shortName);
						mAssListener.newAppearance(uuidHash, appearance, shortName);
					}
					break;
				}
				case MeshService.MESSAGE_DEVICE_ASSOCIATED: {
					int deviceId = msg.getData()
							.getInt(MeshService.EXTRA_DEVICE_ID);
					uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
					if (mDeviceIdtoUuidHash.get(deviceId) == 0) {
						if (uuidHash != 0) {
							mDeviceIdtoUuidHash.put(deviceId, uuidHash);
						}
						ConfigModelApi.getInfo(deviceId,
								ConfigModelApi.DeviceInfo.MODEL_LOW);
					}
					break;
				}
				case MeshService.MESSAGE_CONFIG_DEVICE_INFO: {
					int deviceId = msg.getData()
							.getInt(MeshService.EXTRA_DEVICE_ID);
					uuidHash = mDeviceIdtoUuidHash.get(deviceId);
					ConfigModelApi.DeviceInfo infoType = ConfigModelApi.DeviceInfo
							.values()[msg.getData().getByte(
							MeshService.EXTRA_DEVICE_INFO_TYPE)];
					if (infoType == ConfigModelApi.DeviceInfo.MODEL_LOW) {
						if (uuidHash != 0) {
							mDeviceIdtoUuidHash.removeAt(mDeviceIdtoUuidHash
									.indexOfKey(deviceId));
							String shortName = mUuidHashToAppearance.get(uuidHash);
							if (shortName != null) {
								mUuidHashToAppearance.remove(uuidHash);
							}
							if (mAssListener != null) {
								mAssListener.deviceAssociated(uuidHash, true);
							}
						}
					} else if (infoType == ConfigModelApi.DeviceInfo.VID_PID_VERSION) {
						vid = msg.getData().getByteArray(
								MeshService.EXTRA_VID_INFORMATION);
						pid = msg.getData().getByteArray(
								MeshService.EXTRA_PID_INFORMATION);
						version = msg.getData().getByteArray(
								MeshService.EXTRA_VERSION_INFORMATION);
						if (mInfoListener != null) {
							mInfoListener.onDeviceInfoReceived(vid, pid, version,
									deviceId, true);
						}

					}
					break;
				}
				case MeshService.MESSAGE_FIRMWARE_VERSION:
					mInfoListener.onFirmwareVersion(
							msg.getData().getInt(MeshService.EXTRA_DEVICE_ID),
							msg.getData().getInt(MeshService.EXTRA_VERSION_MAJOR),
							msg.getData().getInt(MeshService.EXTRA_VERSION_MINOR),
							true);
					mInfoListener = null;
					break;
				case MeshService.MESSAGE_ASSOCIATING_DEVICE:
					if (mAssListener != null) {
						int progress = msg.getData().getInt(
								MeshService.EXTRA_PROGRESS_INFORMATION);
						String message = msg.getData().getString(
								MeshService.EXTRA_PROGRESS_MESSAGE);
						mAssListener.associationProgress(progress, message);
					}
					break;
				case MeshService.MESSAGE_RECEIVE_BLOCK_DATA: {
					int deviceID = msg.getData()
							.getInt(MeshService.EXTRA_DEVICE_ID);
					byte[] data = msg.getData()
							.getByteArray(MeshService.EXTRA_DATA);
					int destDeviceId = msg.getData().getInt(MeshService.EXTRA_DEST_DEVICE_ID);
					if (destDeviceId == bleDeviceId) {
						LogUtils.w("mesh-connect", "recv data from [" + deviceID + "] to " + destDeviceId);
						PisInterface.bleReceive(deviceID, data);
					} else
						LogUtils.w("mesh-connect", "======other device[" + destDeviceId + "] data");

				}
				break;
				case MeshService.MESSAGE_PACKET_NOT_SENT: {
					//the Bundle not include any data
//					int desDeviceId = msg.getData().getInt(MeshService.EXTRA_DEST_DEVICE_ID);
//					int srcDeviceId = msg.getData().getInt(MeshService.EXTRA_SRC_DEVICE_ID);
//					LogUtils.i("mesh-connect", "======MESSAGE_PACKET_NOT_SENT from["+
//							srcDeviceId + "] TO [" + desDeviceId + "]");
//
//					Bundle bundle = msg.getData();
//					Set<String> keySet = bundle.keySet();
//					for(String key : keySet){
//						LogUtils.i("mesh-connect", "Key[" + key + "] value[" + bundle.get(key) + "]");
//					}
					break;
				}
				default: {
					LogUtils.i("mesh-connect", "======UNKONW Message===== [" + msg.what + "]");
				}
			}
		}
	}

	/**
	 * Called when a response is not seen to a sent command.
	 * 
	 * @param expectedMessage
	 *            The message that would have been received in the Handler if
	 *            there hadn't been a timeout.
	 */
	private void onMessageTimeout(int expectedMessage, int id, int tid) {
		switch (expectedMessage) {
		case MeshService.MESSAGE_DEVICE_ASSOCIATED:
		case MeshService.MESSAGE_CONFIG_MODELS:
			mAssociationTransactionId = -1;
			if (mAssListener != null) {
				mAssListener.deviceAssociated(id, false);
			}
			if (mInfoListener != null) {
				mInfoListener.onDeviceConfigReceived(false);
			}
			break;
		case MeshService.MESSAGE_FIRMWARE_VERSION:
			if (mInfoListener != null) {
				mInfoListener.onFirmwareVersion(0, 0, 0, false);
			}
			break;
		case MeshService.MESSAGE_CONFIG_DEVICE_INFO:
			if (mDeviceIdtoUuidHash.size() > 0) {
				int uuidHash = mDeviceIdtoUuidHash.valueAt(0);
				mDeviceIdtoUuidHash.removeAt(0);
				if (mAssListener != null) {
					mAssListener.deviceAssociated(uuidHash, true);
				}
			}
			if (mInfoListener != null) {
				mInfoListener.onDeviceConfigReceived(false);
				mInfoListener.onDeviceInfoReceived(new byte[0], new byte[0],
						new byte[0], 0, false);
			}
			break;
		}
	}

//	public void setOffline() {
//		PISManager manager = PISManager.getInstance();
//		if (manager != null) {
//			manager.setAllPisOffline();
//		}
//	}

	private Runnable removeDeviceTimeout = new Runnable() {
		@Override
		public void run() {
			if (mRemovedListener != null) {
				mRemovedListener.onDeviceRemoved(mRemovedDeviceId, false);
				mRemovedListener = null;
				mRemovedUuidHash = 0;
				mRemovedDeviceId = 0;
				mService.setDeviceDiscoveryFilterEnabled(false);
			}
		}
	};

	public void setMeshParameter(int meshId, String meshKey){
		MeshController.bleKey = meshKey;
		MeshController.bleDeviceId = meshId;
		if (mService != null) {
			mService.setControllerAddress(meshId);
			mService.setNetworkPassPhrase(meshKey);
			mService.setContinuousLeScanEnabled(true);
		}
	}

	@Override
	public void discoverDevices(boolean enabled, AssociationListener listener) {
		if (enabled) {
			mAssListener = listener;
		} else {
			mAssListener = null;
		}

		// avoiding crashes
		if (mService != null)
			mService.setDeviceDiscoveryFilterEnabled(enabled);
	}

	@Override
	public boolean associateDevice(int uuidHash, String shortCode) {
		try {
			if (shortCode == null) {
				mAssociationTransactionId = mService.associateDevice(uuidHash,
						0, false);
				return true;
			} else {
				int decodedHash = MeshService
						.getDeviceHashFromShortcode(shortCode);
				if (decodedHash == uuidHash) {
					mAssociationTransactionId = mService.associateDevice(
							uuidHash,
							MeshService.getAuthorizationCode(shortCode), true);
					return true;
				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void removeDevice(RemovedListener listener) {
	}

	@Override
	public void requestModelsSupported(InfoListener listener) {
		mInfoListener = listener;
		ConfigModelApi.getInfo(mSendDeviceId,
				ConfigModelApi.DeviceInfo.MODEL_LOW);
	}

	@Override
	public void setAttentionEnabled(boolean enabled) {
		AttentionModelApi.setState(mSendDeviceId, enabled,
				ATTENTION_DURATION_MS);
	}

	@Override
	public void removeDeviceLocally(RemovedListener removedListener) {
	}

	@Override
	public void getDeviceData(DataListener listener) {
		mService.setContinuousLeScanEnabled(true);
		DeviceInfoProtocol.requestDeviceInfo(mSendDeviceId);
	}

	public Handler getMeshHandler() {
		return mMeshHandler;
	}

	@Override
	public void setContinuousScanning(boolean enabled) {
		mService.setContinuousLeScanEnabled(enabled);
	}

	@Override
	public void postRunnable(Runnable checkScanInfoRunnable) {
		getMeshHandler().post(checkScanInfoRunnable);
	}

	@Override
	public void removeRunnable(Runnable checkScanInfoRunnable) {
		getMeshHandler().removeCallbacks(checkScanInfoRunnable);
	}

	@Override
	public void setSecurity(String networkKeyPhrase) {
		if (feedbackListener != null) {
			this.feedbackListener.onNetSecurity();
		}
	}

	public void setonFeedbackListener(onFeedbackListener listener) {
		this.feedbackListener = listener;
	}

	public interface onFeedbackListener {
		public void onConnected();

		public void onNetSecurity();

		public void onDisconnected();
	}

	public interface onTimeListener {
		public void onTimeOut();

		public void resetList();
	}

	@Override
	public void sendOrder(int deviceID, byte[] data) throws DeadObjectException{
//		LogUtils.i("mesh-connect", "======Send Data to["+deviceID+"]");
		byte data2[] = new byte[3];

		// candle bug ， data = 0x78,0xad,0x01
		if ( data[2] == 0x78 )
		{

			data2[0] = (byte)0x78;
			data2[1] = (byte)0xad;
			data2[2] = data[4];
			DataModelApi.sendData(deviceID, data2, false);
		}
		else
		{
			DataModelApi.sendData(deviceID, data, false);
		}

	}

	public void setNextDeviceId(int id) {
		if (mService != null) {
			mService.setNextDeviceId(id);
		}
	}
	
	/**
	 * 保存统计信息
	 */
//	private void saveInfors(){
//	  SharePreferenceUtils.saveInfors(activity, getInfor(false));
//	}
	
	/**
	 * 设置统计数据变量
	 * @param isConnected
	 * @param reasonOnFailed
	 *     0:成功
	 *     1：断开
	 *     2：csr超时
	 *     3：计时超时
	 * @param isDisconnected 
	 *        是否是断开
	 */
//	private void setInfors(boolean isConnected,int reasonOnFailed,boolean isDisconnected){
//	    endTimeOnConnect = System.currentTimeMillis() / 1000;
//		if (isConnected) {
//			connectResult = "成功";
//			failedReason = "";
//		}else{
//			disconnectTime = 0;
//			connectResult = "失败";
//			if(reasonOnFailed == 1){
//				failedReason = "断开";
//			}else if(reasonOnFailed == 2){
//				failedReason = "csr超时";
//			}else if(reasonOnFailed == 3){
//				failedReason = "计时超时";
//			}else{
//				failedReason = "";
//			}
//		}
////	    requestTime = endTimeOnConnect - startTimeOnConnect;
//		if (isDisconnected) {
//			disconnectTime = System.currentTimeMillis() / 1000;
//			disconnectReason = "蓝牙断开";
//		}else{
//			disconnectTime = 0;
//		}
//		recordTime = System.currentTimeMillis() / 1000 - connectTime;
//	    setPipaInfor();
//		PISManager.resetDataFromJNI();
//		Thread  thread = new Thread(new UploadInforThread(activity, getInfor(true)));
//		threadPool.execute(thread);
//	}
//
//	private void setPipaInfor() {
//		long[] dataOnSend = PISManager.dataOnRequest();
//	    long[] dataOnReceive = PISManager.dataOnRecieve();
//		countOnSend = dataOnSend[0];
//		sumOnSend = dataOnSend[1];
//		countOnReceive = dataOnReceive[0];
//		sumOnReceive = dataOnReceive[1];
//	}
	/**
	 * 把统计数据封装成集合
	 * @param isAdd
	 * @return
	 */
//	private Map<String,String> getInfor(boolean isAdd){
//		if (isAdd) {
//			bleIdOnRecord ++;
//		}
//		setPipaInfor();
//		Map<String,String> infors = new HashMap<String, String>();
//		infors.put(Constant.BLE_COM_ID,""+bleIdOnRecord);
////		infors.put(Constant.CONNECT_TIME_START,""+startTimeOnConnect);
//		infors.put(Constant.CONNECT_TIME_END,""+endTimeOnConnect);
//		infors.put(Constant.CONNECT_RESULT,connectResult);
//		infors.put(Constant.CONNECT_FAILED_REASON,failedReason);
////		infors.put(Constant.CONNECT_COST_TIME,""+requestTime);
//		infors.put(Constant.DISCONNECT_TIME,""+disconnectTime);
//		infors.put(Constant.DISCONNECT_REASON,disconnectReason);
//		infors.put(Constant.CONNECT_TIME,""+recordTime);
//		infors.put(Constant.COUNT_ON_SEND_DATA,""+countOnSend);
//		infors.put(Constant.COUNT_ON_RECIEVER_DATA,""+countOnReceive);
//		infors.put(Constant.SUM_ON_SEND_DATA,""+sumOnSend);
//		infors.put(Constant.SUM_ON_RECIEVE_DATA,""+sumOnReceive);
//		return infors;
//	}
//
//	/**
//	 * 统计统计数据
//	 * @author ryan
//	 *
//	 */
//	private class UploadInforThread implements Runnable{
//		private Map<String,String> infors = null;
//		private Context context;
//		public UploadInforThread(Context mContext,Map<String,String> map){
//			this.context = mContext;
//			this.infors = map;
//		}
//		@Override
//		public void run() {
//			HttpUtils.uploadPipaInfor(context, infors);
//		}
//	}
}
