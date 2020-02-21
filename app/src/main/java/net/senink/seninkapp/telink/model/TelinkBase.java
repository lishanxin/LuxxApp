package net.senink.seninkapp.telink.model;

import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;

/**
 * @author: Li Shanxin
 * @date: 2020/2/21
 * @description:
 */
public class TelinkBase {
    private Group group;
    private DeviceInfo device;
    private boolean isDevice;

    public TelinkBase(Group group) {
        this.group = group;
        isDevice = false;
    }

    public TelinkBase(DeviceInfo device) {
        this.device = device;
        isDevice = true;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public void setDevice(DeviceInfo device) {
        this.device = device;
    }

    public boolean isDevice() {
        return isDevice;
    }
}
