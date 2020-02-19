package net.senink.piservice.pinm.PINMoMC;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.pinm.PINMoBLE.PinmOverBLE;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.struct.UserInfo;

import java.io.Serializable;

/**
 * Created by wensttu on 2016/6/20.
 */
public class PinmOverMC extends PinmInterface{

//    private MCParameter mParameter = null;

    public static PinmParameter CreateParameter(){
        return new MCParameter();
    }

    public PinmOverMC(MCParameter para){
        super(para);
        MCParameter mPara = (MCParameter)mParameter;
        mPara.Username = para.Username;
        mPara.Password = para.Password;
        mPara.HostUrl  = para.HostUrl;

        ConnectType = PinmInterface.TYPE_MC;

    }

    @Override
    public boolean isBelongTheConnect(PISBase pis){
        if (pis.getPanId() == 0x100)
            return false;
        else
            return true;
    }

    @Override
    public int connect(@NonNull Context context, PinmParameter para)
            throws IllegalArgumentException{
        if (mParameter == null)
            mParameter = para;
        MCParameter mcPara = (MCParameter)mParameter;
        mContext = context;
        //判断参数是否完整
        if (para != null){
            if(!(para instanceof MCParameter))
                throw new IllegalArgumentException("error parameter class");
            mcPara = (MCParameter)para;
        }

        if (mcPara.Username == null || mcPara.Password == null || mcPara.HostUrl == null)
            throw new IllegalArgumentException("the connect property are incompleted");

        //判断当前的网络状态
        if (!PinmInterface.isNetworkConnected(mContext)){
            return PINM_RESULT_NETWORK_INVAILD;
        }
        try{
            if (NetId > 0) {
                if (!mParameter.isEqual(para)){
                    if (PisInterface.pisConnStatus(NetId) == PINM_CONNECT_STATUS_CONNECTED) {
                        disconnect();
                    }
                    mParameter = para;
                }
                if (getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
                    NetId = PisInterface.pisConnect(ConnectType, mcPara.Username, mcPara.Password, mcPara.HostUrl);
            }
            else {
                NetId = PisInterface.pisConnect(ConnectType, mcPara.Username, mcPara.Password, mcPara.HostUrl);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        Status = PINM_CONNECT_STATUS_CONNECTING;

        return PinmInterface.PINM_RESULT_SUCCESSED;
    }

    @Override
    public int disconnect() {
        if (NetId == 0)
            return PINM_RESULT_NETWORK_INVAILD;
        return PisInterface.pisDisConnect(NetId);
    }

    public static class MCParameter extends PinmParameter{
        public String Username;
        public String Password;
        public String HostUrl;

        @Override
        public boolean isEqual(@NonNull PinmParameter para){
            if (!(para instanceof MCParameter))
                return false;
            MCParameter mcPara = (MCParameter)para;
            if (mcPara.Username.compareTo(Username) != 0)
                return false;
            if (mcPara.Password.compareTo(Password) != 0)
                return false;
            if (mcPara.HostUrl.compareTo(HostUrl) != 0)
                return false;

            return true;
        }
    }
}
