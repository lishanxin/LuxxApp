package net.senink.seninkapp.telink.api;

import android.content.Context;
import android.util.Log;

import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;
import com.telink.sig.mesh.model.SigMeshModel;

import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.GeneralDataManager;
import net.senink.seninkapp.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TelinkGroupApiManager {
    private static final String TAG = TelinkGroupApiManager.class.getSimpleName();
    private static TelinkGroupApiManager instance;
    private Context mContext;
    public static TelinkGroupApiManager getInstance() {
        if (instance == null) {
            synchronized (TelinkGroupApiManager.class) {
                if (instance == null) {
                    instance = new TelinkGroupApiManager();
                }
            }
        }
        return instance;
    }
    public void init(Context context) {
        mContext = context;
    }

    private SigMeshModel[] models = SigMeshModel.getDefaultSubList();
    private int modelIndex = 0;
    private int opGroupAdr;
    private int opType;

    private static final String TAG_CMD = "TAG_CMD";

    // 添加组
    public void addGroup(String groupName, PISBase object) {
        List<Group> groups = MyApplication.getInstance().getMesh().groups;
        Stack<Integer> deleted = MyApplication.getInstance().getMesh().deletedGroupAddress;

        Group group = new Group();
        group.address = getGroupAddress(groups, deleted);
        group.name = groupName;
        group.PISKeyString = object.getPISKeyString();
        groups.add(group);

        TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
    }

    private void bindGroupToPIS(Group group) {
        List<Group> groups = MyApplication.getInstance().getMesh().groups;
        List<PISBase> pisGroups = GeneralDataManager.getInstance().getPISGroups();
        for (PISBase pisGroup : pisGroups) {
            int pisAddr = pisGroup.getShortAddr();
            for (Group group1 : groups) {
            }
        }
    }

    // 删除组
    public void deleteGroup(int groupAddress){
        List<Group> groups = MyApplication.getInstance().getMesh().groups;
        for (Group group : groups) {
            if(groupAddress == group.address){
                List<DeviceInfo> deviceInGroup = getDevicesInGroup(groupAddress);
                for (DeviceInfo deviceInfo : deviceInGroup) {
                    deleteDeviceFromGroup(groupAddress, deviceInfo);
                }
                MyApplication.getInstance().getMesh().deletedGroupAddress.push(groupAddress);
                groups.remove(group);
                break;
            }
        }
        TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
    }

    // 添加组内设备
    public void addDeviceToGroup(int groupAddress,DeviceInfo deviceInfo){
        setDeviceGroupInfo(groupAddress, 1, deviceInfo);
    }

    // 删除组内设备
    public void deleteDeviceFromGroup(int groupAddress,DeviceInfo deviceInfo){
        setDeviceGroupInfo(groupAddress, 0, deviceInfo);
    }

    // 获取组内灯列表
    public List<DeviceInfo> getDevicesInGroup(int groupAddress) {

        List<DeviceInfo> localDevices = MyApplication.getInstance().getMesh().devices;
        List<DeviceInfo> innerDevices = new ArrayList<>();
        outer:
        for (DeviceInfo device : localDevices) {
            if (device.subList != null) {
                for (int groupAdr : device.subList) {
                    if (groupAdr == groupAddress) {
                        innerDevices.add(device);
                        continue outer;
                    }
                }
            }
        }
        return innerDevices;
    }



    /**
     * 灯组绑定
     * @param groupAddress
     * @param type         0 add, 1 delete
     */
    private void setDeviceGroupInfo(int groupAddress, int type, DeviceInfo deviceInfo) {
        Log.d(TAG, "Setting...");
        opGroupAdr = groupAddress;
        modelIndex = 0;
        this.opType = type;
        setNextModel(deviceInfo);
        TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
    }

    private void setNextModel(DeviceInfo deviceInfo) {
        if (modelIndex > models.length - 1) {
            if (opType == 0) {
                deviceInfo.subList.add(opGroupAdr);
            } else {
                deviceInfo.subList.remove((Integer) opGroupAdr);
            }
            TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
            getLocalDeviceGroupInfo(deviceInfo);

        } else {
            final int eleAdr = deviceInfo.getTargetEleAdr(models[modelIndex].modelId);
            if (eleAdr == -1) {
                modelIndex++;
                setNextModel(deviceInfo);
                return;
            }
            // if (!MeshService.getInstance().cfgCmdSubSet(opCode, address, eleAdr, opGroupAdr, models[modelIndex].modelId, true)) {
            if (!MeshService.getInstance().setSubscription(opType, deviceInfo.meshAddress, eleAdr, opGroupAdr, models[modelIndex].modelId, true, TAG_CMD)) {
                Log.d(TAG, "setting fail!");
            }
        }

    }

    public List<Group> getGroupsWithDevice(int telinkAddress) {

        DeviceInfo deviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(telinkAddress);
        return getGroupsWithDevice(deviceInfo);
    }

    // 获取灯具所绑定的所有组
    public List<Group> getGroupsWithDevice(DeviceInfo deviceInfo){
        List<Group> boundGroup = new ArrayList<>();
        List<Group> groups = MyApplication.getInstance().getMesh().groups;
        if (deviceInfo.subList == null || deviceInfo.subList.size() == 0) {
            return boundGroup;
        }
        outer:
        for (Group group : groups) {
            for (int groupAdr : deviceInfo.subList) {
                if (groupAdr == group.address) {
                    boundGroup.add(group);
                    continue outer;
                }
            }
        }
        return boundGroup;
    }

    // 获取灯具所绑定的灯组信息
    private void getLocalDeviceGroupInfo(DeviceInfo deviceInfo) {
        List<Group> groups = MyApplication.getInstance().getMesh().groups;
        if (deviceInfo.subList == null || deviceInfo.subList.size() == 0) {
            for (Group group : groups) {
                group.selected = false;
            }
            return;
        }
        outer:
        for (Group group : groups) {
            group.selected = false;
            for (int groupAdr : deviceInfo.subList) {
                if (groupAdr == group.address) {
                    group.selected = true;
                    continue outer;
                }
            }
        }

    }



    private int getGroupAddress(List<Group> groups, Stack<Integer> deletedGroupAddress) {
        if (deletedGroupAddress.isEmpty()) {
            int i = groups.size();
            return i | 0xC000;
        } else {
            return deletedGroupAddress.pop();
        }
    }


}