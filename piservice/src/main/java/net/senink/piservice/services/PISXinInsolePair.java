package net.senink.piservice.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.SparseArray;

import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;

import java.util.HashMap;

/**
 * Created by wensttu on 2016/8/30.
 */
public class PISXinInsolePair extends PISXinInsoles {

    public PISXinInsolePair(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo);

        this.ServiceType = SERVICE_TYPE_GROUP;
        mSolesPair = new PISXinInsoles[2];
    }

    private PISXinInsoles[] mSolesPair = null;
    public PISXinInsoles getLeftInsole(){
        if (mSolesPair != null)
            return mSolesPair[0];
        return null;
    }
    public PISXinInsoles getRightInsole(){
        if (mSolesPair != null)
            return mSolesPair[1];
        return null;
    }

    @Override
    public void Initialization(){
        super.Initialization();

        if (mSolesPair != null && mSolesPair[0] != null && mSolesPair[1] != null)
            this.setStatus(SERVICE_STATUS_ONLINE);
        else{
            this.getGroupObjects();
        }
    }

}
