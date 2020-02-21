package net.senink.seninkapp;

/**
 * @author: Li Shanxin
 * @date: 2020/2/21
 * @description:
 */

import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.telink.model.TelinkBase;

/**
 * 通用灯及灯组数据模型，用于区别piservice于TelinkSDK不同的灯组
 */
public class GeneralDeviceModel {
    private PISBase pisBase;
    private TelinkBase telinkBase;
    private boolean isTelink;

    public GeneralDeviceModel(PISBase pisBase) {
        this.pisBase = pisBase;
        isTelink = false;
    }

    public GeneralDeviceModel(TelinkBase telinkBase) {
        this.telinkBase = telinkBase;
        isTelink = true;
    }

    public PISBase getPisBase() {
        return pisBase;
    }

    public void setPisBase(PISBase pisBase) {
        this.pisBase = pisBase;
    }

    public TelinkBase getTelinkBase() {
        return telinkBase;
    }

    public void setTelinkBase(TelinkBase telinkBase) {
        this.telinkBase = telinkBase;
    }

    public boolean isTelink() {
        return isTelink;
    }
}
