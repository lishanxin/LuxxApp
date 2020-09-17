package net.senink.seninkapp.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.telink.sig.mesh.model.DeviceInfo;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.telink.view.IconGenerator;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.setting.DeviceListActivity;

public class IconUtil {

    public static Drawable getPISIcon(Context context, PISBase infor){
        Drawable sld = null;
//			int resourceId = 0;
        PISDevice dev;
        PISxinColor light = (PISxinColor) infor;
        if (infor.ServiceType != PISBase.SERVICE_TYPE_GROUP) {
            dev = infor.getDeviceObject();
            if (dev != null) {
                if (infor.getT1() == 0x10 && infor.getT2() == 0x05) {
                    sld = ProductClassifyInfo.getProductStateListDrawable(context,
                            ProductClassifyInfo.CLASSID_EUREKA_CANDLE,
                            dev.getStatus(),
                            light.getLightStatus());
                } else {
                    sld = ProductClassifyInfo.getProductStateListDrawable(context,
                            dev.getClassString(),
                            dev.getStatus(),
                            light.getLightStatus());
                }
            } else {
                sld = ProductClassifyInfo.getProductStateListDrawable(context,
                        ProductClassifyInfo.CLASSID_DEFAULT,
                        0, 0);
            }
        } else {
            sld = context.getResources().getDrawable(ProductClassifyInfo.getProductResourceId(infor.getDeviceObject().getClassString()));
        }

        return sld;
    }


    public static int getTelinkDeviceIconResource(DeviceInfo device){
        final int deviceType = device.nodeInfo != null && device.nodeInfo.cpsData.lowPowerSupport() ? 1 : 0;
        return IconGenerator.getIcon(deviceType, device.getOnOff());
    }

}
