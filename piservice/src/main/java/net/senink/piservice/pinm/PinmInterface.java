package net.senink.piservice.pinm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wensttu on 2016/6/20.
 */
public abstract class PinmInterface{
    private static final String TAG = "PinmInterface";

    /**
     * 网络类型
     */
    public static final int TYPE_MC = 1;
    public static final int TYPE_UC = 2;
    public static final int TYPE_CSRMESH = 3;

    /**
     * PINM连接事件
     */
    public static final String PINM_CONNECT_ACTION = "net.senink.pinm.connect";

    public static final String PINM_CONNECT_ExtraObject = "PINM_OBJECT";
    public static final String PINM_CONNECT_ExtraStatus = "PINM_STATUS";

    /**
     * PINM网络状态
     */
    public static final int PINM_CONNECT_STATUS_INVAILD = -1;
    public static final int PINM_CONNECT_STATUS_INIT = 1;
    public static final int PINM_CONNECT_STATUS_CONNECTED = 2;
    public static final int PINM_CONNECT_STATUS_DISCONNECTED = 3;
    public static final int PINM_CONNECT_STATUS_CONNECTING = 4;
    public static final int PINM_CONNECT_STATUS_DISCONNECTING = 5;

    public static final int PINM_CONNECT_STATUS_USERPASSWD_ERROR = 0x10;  //用户名或者密码错误
    public static final int PINM_CONNECT_STATUS_CONNECT_TIMEOUT = 0x11;   //连接超时
    /**
     * 连接结果
     */
    public static final int PINM_RESULT_NETWORK_EXISTED = -3;
    public static final int PINM_RESULT_NETWORK_INVAILD = -2;
    public static final int PINM_RESULT_FAILED = -1;
    public static final int PINM_RESULT_SUCCESSED = 1;


    protected int ConnectType = 0;
    protected transient int Status = PINM_CONNECT_STATUS_INVAILD;
    protected transient int NetId = 0;

    protected transient Context mContext = null;
    protected PinmParameter mParameter = null;
    protected PinmInterface(@NonNull PinmParameter para){
        mParameter = para;
        mContext = null;
        NetId = 0;

    }

    public static PinmParameter CreateParameter(){
        return null;
    }
    public PinmParameter getParameter() {
        return mParameter;
    }
    public int connect(@NonNull Context context, @NonNull PinmParameter para){
        mParameter = para;
        mContext = context;

        return PINM_RESULT_SUCCESSED;
    }
    public abstract int disconnect();
//    public abstract void updateParameter(PinmParameter para);
    public int getStatus(){
        if (NetId != 0){
            return PisInterface.pisConnStatus(NetId);
        }
        return PinmInterface.PINM_CONNECT_STATUS_INVAILD;
    }

    /**
     * 返回当前连接的连接标志
     * @return 0 - 当前连接需要被关闭，1 - 当前连接需要被建立， 0xFF - 无效的标志
     */
    public int getConnectFlag(){
        if (NetId != 0)
            return PisInterface.pisConnectFlag(NetId);
        else
            return 0xFF;
    }

    /**
     * 序列化对象输出函数
     * @param out 输出流
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException{
        out.defaultWriteObject();
        out.writeInt(ConnectType);
    }

    /**
     * 反序列化对象输入函数
     * @param in 输入流
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        ConnectType = in.readInt();
        NetId = 0;

    }

    public int getConnectType(){
        return ConnectType;
    }

    public boolean hasNewStatus(){
        if (this.NetId == 0 || mContext == null) {
            Status = PinmInterface.PINM_CONNECT_STATUS_INVAILD;
            return false;
        }
        int newStatus = PisInterface.pisConnStatus(this.NetId);
        if (newStatus != Status) {
            Status = newStatus;
            return true;
        }

        return false;
    }

    public int getNetId(){
        return NetId;
    }

    public void setNetId(int nid){
        NetId = nid;
    }


    public boolean isBelongTheConnect(PISBase pis){
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static abstract class PinmParameter implements Serializable {

        public abstract boolean isEqual(@NonNull PinmParameter para);
    }
}


