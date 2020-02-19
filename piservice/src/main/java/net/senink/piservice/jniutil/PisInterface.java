package net.senink.piservice.jniutil;

import android.app.Activity;
import android.os.DeadObjectException;
import android.os.SystemClock;

import com.csr.mesh.DataModelApi;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.struct.PayloadDiscovery;
import net.senink.piservice.util.LogUtils;

public class PisInterface {

	static public native String hello();

	static public native void pisProcSet();

	static public native int pisConnect(int mode, String user, String passwd,
			String host);

	static public native int pisConnStatus(int netId);

	static public native int pisConnectFlag(int netId);

	static public native int pisDiscover(int count, PayloadDiscovery discovery);

	static public native int pisPairing(PIServiceInfo si, PIAddress pa);

	static public native int pisSubscribe(int pairId, int msg, boolean isFlag);

	static public native int pisRequest(int pairId, int reqType, int reqLength,
			byte[] ReqData, int flag);

	static public native int pisDisConnect(int netId);

	static public native int pisGetConnectCount();
    
	static public native int pisEasyConfigSSID(String ssid, String key);

	static public native int bleReceive(int deviceId, byte[] data);

	static public native int bleConnect(int deviceId, String passwd);

	static public native int bleConnected(short isConnected);

	static public native int bleStatus();
	
	static public native void resetDataFromJNI();
	
	static public native int getCountOnRequests();
	static public native int getLensOnRequests();
	static public native int getCountOnRecieve();
	static public native int getLensOnRecieve();
	static {
		System.loadLibrary("seninkjni");
	}

	/*
	 * private final static void SeOnResultInner(int msg, Object hPara, Object
	 * result,int pariID) { Handler mainHandler = getMainHandle(); if (null !=
	 * mainHandler) { PipaResponseInner response = new PipaResponseInner(msg,
	 * hPara,result, pariID); Message message = Message.obtain(mainHandler,
	 * SE_MSG_RESULT,response); mainHandler.sendMessage(message); }
	 * 
	 * }
	 */

	/**
	 * Pipa消息处理函数
	 * @param msg       消息类型
	 * @param hPara		高位参数
	 * @param result	低位参数
	 * @param pairID	配对号
     * @throws Exception
     */
	static final Object pismanager_onprocess_lock = new Object();
	public static void PisProcessMsgFromJni(int msg, int hPara,
			byte[] result, int pairID) throws Exception {
//		LogUtils.i("mesh-connect", " process data from object[" + pairID + "]");
		try {
			synchronized (pismanager_onprocess_lock) {
				PISManager pm = PISManager.getInstance();
				if (pm != null) {
					pm.onProcess(msg, hPara, result, pairID);
				}else{
					LogUtils.e("PInterface", "PISManager is null");
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 把mesh数据转换为pipa包后发送命令
	 * 
	 * @param deviceId
	 *            设备id
	 * @param data
	 *            命令内容
	 */
	static private long broadcastTime = 0;
	private static void PisSendDataFromJni(int deviceId, byte[] data) {
		LogUtils.i("mesh-connect", " send data to device[" + deviceId + "]");
		try {
			if (deviceId == 0) {
				if (System.currentTimeMillis() - broadcastTime < 1000)
					return;
				broadcastTime = System.currentTimeMillis();
			}
			MeshController.getInstance(PISManager.getDefaultContext()).sendOrder(deviceId, data);
//			DataModelApi.sendData(deviceId, data, false);
		}catch (DeadObjectException e){
			MeshController.getInstance(PISManager.getDefaultContext()).stop();
			MeshController.getInstance(PISManager.getDefaultContext()).start();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
