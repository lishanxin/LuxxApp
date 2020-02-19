package net.senink.piservice.pinm.PINMoBLE;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.util.LogUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by wensttu on 2016/6/20.
 */
public class PinmOverBLE extends PinmInterface{
    private static final String DevicePrefix = "xin";
    private static final String TAG = "PinmOverBLE";

    private transient MeshController controller;

    public static PinmParameter CreateParameter(){
        return new BLEParameter();
    }


    public PinmOverBLE(BLEParameter para){
        super(para);

        ConnectType = PinmInterface.TYPE_CSRMESH;

    }


    @Override
    public boolean isBelongTheConnect(PISBase pis){
        if (pis.getPanId() == 0x100)
            return true;
        else
            return false;
    }

    @Override
    public int connect(@NonNull Context context, PinmParameter para) {
        try{
            BLEParameter blePara = (BLEParameter)mParameter;
            mContext = context;

            //判断参数是否完整
            if (para != null){
                if(!(para instanceof BLEParameter))
                    throw new IllegalArgumentException("error parameter class");
                blePara = (BLEParameter)para;
            }

            if (controller == null){
                controller = MeshController.getInstance(mContext);
            }

            mParameter = blePara;
            if (MeshController.isConnected) {
                controller.setMeshParameter(blePara.BleId, blePara.BleKeyString);
            }
            else{
                MeshController.bleDeviceId = blePara.BleId;
                MeshController.bleKey = blePara.BleKeyString;
                LogUtils.i(TAG, "Begin to connect CSRMesh, bleDeviceId = " + MeshController.bleDeviceId);

                NetId = PisInterface.bleConnect(blePara.BleId, blePara.BleKeyString);
//                controller.mPinmBle = this;
                controller.start();
//                PisInterface.bleConnected((short) PinmInterface.PINM_CONNECT_STATUS_CONNECTING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return PinmInterface.PINM_RESULT_SUCCESSED;
    }

    @Override
    public int disconnect() {
        if (controller == null)
            return PinmInterface.PINM_RESULT_FAILED;
        controller.stop();
        PisInterface.pisDisConnect(NetId);
        PisInterface.bleConnected((short)PinmInterface.PINM_CONNECT_STATUS_DISCONNECTED);
        return 0;
    }

    public static class BLEParameter extends PinmParameter{
        public int BleId = 0;
        public String BleKeyString = null;
        public String MacString = null;

        @Override
        public boolean isEqual(@NonNull PinmParameter para){
            if (!(para instanceof BLEParameter))
                return false;
            BLEParameter mcPara = (BLEParameter)para;
            if (mcPara.BleKeyString.compareTo(BleKeyString) != 0)
                return false;
            if (mcPara.BleId != BleId)
                return false;
            return true;
        }
    }

}
