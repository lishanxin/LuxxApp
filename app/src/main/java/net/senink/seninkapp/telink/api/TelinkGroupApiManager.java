package net.senink.seninkapp.telink.api;

import android.content.Context;
import android.util.Log;

import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;
import com.telink.sig.mesh.model.MeshCommand;
import com.telink.sig.mesh.model.SigMeshModel;
import com.telink.sig.mesh.util.TelinkLog;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISBaseAdd;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.telink.model.TelinkOperation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TelinkGroupApiManager implements EventListener<String> {
    private static final String TAG = TelinkGroupApiManager.class.getSimpleName();
    private static TelinkGroupApiManager instance;
    private Context mContext;
    private AddDeviceToGroupSucceedCallback addDeviceToGroupSucceedCallback;
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
    private TelinkGroupApiManager(){
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_SUB_OP_CONFIRM, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_CTL_STATUS_NOTIFY, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_COMPLETE, this);
        EventBus.getDefault().register(this);
    }
    public void init(Context context) {
        mContext = context;
    }

    private SigMeshModel[] models = SigMeshModel.getDefaultSubList();
    private int modelIndex = 0;
    private int opGroupAdr;
    private int opType;
    // TelinkGroup
    private List<Group> groups = new ArrayList<>();
    private static final String TAG_CMD = "TAG_CMD";


    @Subscribe
    public void pisDeviceAdded(PISBaseAdd pisBaseAdd){

    }

//    // 添加组
//    public void addGroup(PISBase pisBase) {
//        List<Group> groups = MyApplication.getInstance().getMesh().groups;
//
//        Group group = new Group();
//        group.PISKeyString = pisBase.getPISKeyString();
//        group.address = getGroupAddress(group.PISKeyString);
//        group.name = pisBase.getName();
//        groups.add(group);
//
//        TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
//    }

    /**
     * 刷新TelinkGroups
     * @param srvsGroup
     * @return
     */
    public List<Group> refreshTelinkGroups(List<PISBase> srvsGroup) {
        List<Group> groups = new ArrayList<>();
        for (PISBase pisBase : srvsGroup) {
            Group group = new Group();
            group.PISKeyString = pisBase.getPISKeyString();
            group.address = getGroupAddress(group.PISKeyString);
            group.name = pisBase.getName();
            groups.add(group);
        }
        this.groups = groups;
        return groups;
    }

    public List<Group> getTelinkGroups(){
        return this.groups;
    }

    public Group getGroupByAddress(int address){
        for (Group info : groups) {
            if (info.address == address)
                return info;
        }
        return null;
    }

    // 删除组
    public void deleteGroup(int groupAddress){
        List<Group> groups = this.groups;
        Group group = getGroupByAddress(groupAddress);
        if(group != null){
            List<DeviceInfo> deviceInGroup = getDevicesInGroup(groupAddress);
            for (DeviceInfo deviceInfo : deviceInGroup) {
                deleteDeviceFromGroup(groupAddress, deviceInfo.meshAddress);
            }

            groups.remove(group);
            TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
        }
    }

    public void deleteTelinkGroupByPisKey(String pisKeyString){
        Group group = getTelinkGroupByPisKeyString(pisKeyString);
        if(group != null){
            deleteGroup(group.address);
        }
    }

    private Group getTelinkGroupByPisKeyString(String pisKeyString){
        if(pisKeyString == null) return null;
        List<Group> groups = this.groups;
        for (Group group : groups) {
            if(pisKeyString.equals(group.PISKeyString)){
                return group;
            }
        }
        return null;
    }

    public void deletePISGroup(String PISKeyString, PipaRequest.OnPipaRequestStatusListener listener){
        if(PISKeyString != null){
            PISBase infor = PISManager.getInstance().getPISObject(PISKeyString);
            deletePISGroup(infor, listener);
        }
    }

    public void deletePISGroup(PISBase infor, PipaRequest.OnPipaRequestStatusListener listener){
        if(infor == null) return;
        PISMCSManager mcm = PISManager.getInstance().getMCSObject();
        PipaRequest req = mcm.removeGroup(infor.getGroupId());
        req.setOnPipaRequestStatusListener(listener);
        mcm.request(req);
    }

    // 添加组内设备
    public void addDeviceToGroup(int groupAddress, int deviceMeshAddress, AddDeviceToGroupSucceedCallback callback){
        waitBindDeviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(deviceMeshAddress);
        setDeviceGroupInfo(groupAddress, 0, waitBindDeviceInfo);
        addDeviceToGroupSucceedCallback = callback;
    }

    // 删除组内设备
    public void deleteDeviceFromGroup(int groupAddress,int deviceMeshAddress){
        waitBindDeviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(deviceMeshAddress);
        setDeviceGroupInfo(groupAddress, 1, waitBindDeviceInfo);
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

    private DeviceInfo waitBindDeviceInfo;

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(NotificationEvent.EVENT_TYPE_SUB_OP_CONFIRM)) {
//            modelIndex++;
//            setNextModel();

        } else if (event.getType().equals(NotificationEvent.EVENT_TYPE_CTL_STATUS_NOTIFY)) {
//            refreshUI();
        } else if (event.getType().equals(MeshEvent.EVENT_TYPE_DISCONNECTED)) {
//            refreshUI();
        } else if (event.getType().equals(CommandEvent.EVENT_TYPE_CMD_COMPLETE)) {
            MeshCommand meshCommand = ((CommandEvent) event).getMeshCommand();
            if (meshCommand != null) {
                if (TAG_CMD.equals(meshCommand.tag)) {
                    if (meshCommand.rspCnt >= 1) {
                        modelIndex++;
                        setNextModel(waitBindDeviceInfo);
                    } else {
                        TelinkLog.e("set group sub error");
                    }
                }
            }


        }
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
            Group group = getGroupByAddress(opGroupAdr);
            if (opType == 0) {
                deviceInfo.subList.add(opGroupAdr);
                if(group != null){
                    group.subList.add(deviceInfo.meshAddress);
                }
            } else {
                deviceInfo.subList.remove((Integer) opGroupAdr);
                if(group != null){
                    group.subList.remove((Integer) deviceInfo.meshAddress);
                }
            }
            TelinkApiManager.getInstance().saveOrUpdateMesh(mContext);
            if(addDeviceToGroupSucceedCallback != null){
                addDeviceToGroupSucceedCallback.onAddDeviceToGroupSucceed();
            }
            EventBus.getDefault().post(new TelinkOperation(TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_SUCCEED));
//            getLocalDeviceGroupInfo(deviceInfo);

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
                EventBus.getDefault().post(new TelinkOperation(TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_FAIL));
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
        List<Group> groups = this.groups;
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
        List<Group> groups = this.groups;
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

    public boolean isGroupOn(Group group){
        if(group == null) return false;
        List<Integer> deviceAddress = group.subList;
        if(deviceAddress != null){
            for (Integer address : deviceAddress) {
                DeviceInfo deviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(address);
                if(deviceInfo != null && deviceInfo.getOnOff() == 1){
                    return true;
                }
            }
        }
        return false;
    }



    private int getGroupAddress(String pisGroupKey) {
        String[] keyArray = pisGroupKey.split("-");
        return Integer.parseInt(keyArray[1], 16) | 0xC000;
    }


    /**
     * 获取与Pis组绑定的Telink组
     * @param infor
     * @return
     */
    public Group getGroupByPisBase(PISBase infor) {
        if(infor == null || infor.ServiceType != PISBase.SERVICE_TYPE_GROUP) return null;
        List<Group> allGroups = this.groups;
        if(allGroups == null) return null;
        for (Group group : allGroups) {
            if(group.PISKeyString.equals(infor.getPISKeyString())){
                return group;
            }
        }
        return null;
    }

    public interface AddDeviceToGroupSucceedCallback{
        void onAddDeviceToGroupSucceed();
    }
}
