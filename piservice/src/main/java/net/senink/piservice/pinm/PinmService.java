package net.senink.piservice.pinm;

/**
 * Created by wensttu on 2016/8/13.
 */

import android.app.Service;


/**
 * Created by wensttu on 2016/8/4.
 */

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.SparseArray;

import net.senink.piservice.pis.PISManager;


public class PinmService extends Service {
    //为日志工具设置标签
    private static String TAG = "PinmService";

    private LocalBinder mBinder = new LocalBinder();

    private SparseArray<PinmInterface> pinmList;

    //该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
    @Override
    public void onCreate() {
        pinmList = new SparseArray<>();


        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {


        super.onStart(intent, startId);
    }
    @Override
    public void onDestroy() {
        for (int i = 0; i < pinmList.size(); i++){
            PinmInterface pinm = pinmList.valueAt(i);
            if (pinm != null && pinm.getStatus() == PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
                pinm.disconnect();
        }

        super.onDestroy();
    }
    //其他对象通过bindService 方法通知该Service时该方法被调用
    @Override
    public IBinder onBind(Intent intent) {

        try {
            if (mBinder == null)
                mBinder = new LocalBinder();

        }catch (Exception e){
            e.printStackTrace();
        }
        return mBinder;
    }
    //其它对象通过unbindService方法通知该Service时该方法被调用
    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public void registerPinmConnect(PinmInterface pinm){
        if (pinm == null){
            return;
        }
        if (pinmList.get(pinm.getConnectType()) != null)
            pinmList.remove(pinm.getConnectType());
        pinmList.put(pinm.getConnectType(), pinm);
    }

    public PinmInterface getPinmObject(int PINM_TYPE){
        if (pinmList == null)
            return null;
        try {
            return pinmList.get(PINM_TYPE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void connect(int PINM_TYPE, PinmInterface.PinmParameter para){
        try {
            PinmInterface pinm = pinmList.get(PINM_TYPE);
            if (pinm != null) {
                pinm.connect(PISManager.getDefaultContext(), para);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnect(int PINM_TYPE){
        try {
            PinmInterface pinm = pinmList.get(PINM_TYPE);
            if (pinm != null) {
                pinm.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateParameter(int PINM_TYPE, PinmInterface.PinmParameter para){
        try {
            PinmInterface pinm = pinmList.get(PINM_TYPE);
            if (pinm != null){
                pinm.connect(PISManager.getDefaultContext(), para);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public class LocalBinder extends Binder {
        public PinmService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PinmService.this;
        }
    }

}