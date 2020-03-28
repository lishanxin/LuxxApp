package net.senink.seninkapp.telink.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;

import com.telink.sig.mesh.ble.AdvertisingDevice;
import com.telink.sig.mesh.ble.MeshScanRecord;
import com.telink.sig.mesh.ble.UnprovisionedDevice;
import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.event.ScanEvent;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.light.ProvisionDataGenerator;
import com.telink.sig.mesh.light.PublicationStatusParser;
import com.telink.sig.mesh.light.ScanParameters;
import com.telink.sig.mesh.light.UuidInfo;
import com.telink.sig.mesh.light.parameter.KeyBindParameters;
import com.telink.sig.mesh.light.parameter.ProvisionParameters;
import com.telink.sig.mesh.model.DeviceBindState;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.NodeInfo;
import com.telink.sig.mesh.model.NotificationInfo;
import com.telink.sig.mesh.model.PublishModel;
import com.telink.sig.mesh.model.SigMeshModel;
import com.telink.sig.mesh.model.message.config.PubSetMessage;
import com.telink.sig.mesh.util.TelinkLog;

import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.telink.SharedPreferenceHelper;
import net.senink.seninkapp.telink.model.Mesh;
import net.senink.seninkapp.telink.model.PrivateDevice;
import net.senink.seninkapp.telink.model.ProvisioningDevice;
import net.senink.seninkapp.telink.view.BaseRecyclerViewAdapter;
import net.senink.seninkapp.telink.view.DeviceProvisionListAdapter;
import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Li Shanxin
 * @date: 2020/2/20
 * @description:
 */
public class TelinkApiManager implements EventListener<String> {
    private static final String TAG = TelinkApiManager.class.getSimpleName();
    private static TelinkApiManager instance;
    private boolean isServiceCreated = false;
    private Context mContext;
    private List<ProvisioningDevice> devices;
    private Mesh mesh;
    private DeviceProvisionListAdapter mListAdapter;
    private UnprovisionedDevice targetDevice;
    private ProvisioningDevice pubSettingDevice;

    private Handler delayedHandler = new Handler();

    public static TelinkApiManager getInstance(){
        if(instance == null){
            synchronized (TelinkApiManager.class){
                if(instance == null){
                    instance = new TelinkApiManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        devices = new ArrayList<>();
        mesh = MyApplication.getInstance().getMesh();
        mContext = context;
    }

    public List<ProvisioningDevice> getFoundDevices(){
        return devices;
    }

    public void startMeshService(Activity activity, EventListener<String> listener){
        Intent serviceIntent = new Intent(activity, MeshService.class);
        activity.startService(serviceIntent);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_MESH_EMPTY, this);
        MyApplication.getInstance().addEventListener(MeshController.EVENT_TYPE_SERVICE_CREATE, this);
        MyApplication.getInstance().addEventListener(MeshController.EVENT_TYPE_SERVICE_DESTROY, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_PROCESSING, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_COMPLETE, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_ERROR_BUSY, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_KICK_OUT_CONFIRM, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_PROVISION_SUCCESS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_PROVISION_FAIL, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_KEY_BIND_SUCCESS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_KEY_BIND_FAIL, this);
        MyApplication.getInstance().addEventListener(ScanEvent.DEVICE_FOUND, this);
    }


    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case MeshController.EVENT_TYPE_SERVICE_CREATE:
                TelinkLog.d(TAG + "#EVENT_TYPE_SERVICE_CREATE");
                isServiceCreated = true;
                break;
            case MeshController.EVENT_TYPE_SERVICE_DESTROY:
                TelinkLog.d(TAG + "-- service destroyed event");
                break;
            case ScanEvent.DEVICE_FOUND:
                AdvertisingDevice device = ((ScanEvent) event).advertisingDevice;
                onDeviceFound(device);
                break;
            case MeshEvent.EVENT_TYPE_PROVISION_SUCCESS:
                onProvisionSuccess((MeshEvent) event);
                break;
            case MeshEvent.EVENT_TYPE_PROVISION_FAIL:
                onProvisionFail((MeshEvent) event);
                startScanTelink();
                break;
            case MeshEvent.EVENT_TYPE_KEY_BIND_SUCCESS:
                if (onKeyBindSuccess((MeshEvent) event)) {
                    TelinkLog.d("set device time publish");
                    // waiting for publication status
                } else {
                    startScanTelink();
                }
                break;
            case MeshEvent.EVENT_TYPE_KEY_BIND_FAIL:
                onKeyBindFail((MeshEvent) event);
                startScanTelink();
                break;
            case NotificationEvent.EVENT_TYPE_PUBLICATION_STATUS:
                NotificationInfo notificationInfo = ((NotificationEvent) event).getNotificationInfo();
                byte[] params = notificationInfo.params;
                final PublicationStatusParser.StatusInfo statusInfo = PublicationStatusParser.create().parse(params);

                ProvisioningDevice pvingDevice = getDeviceByMeshAddress(notificationInfo.srcAdr);
                if (pvingDevice != null && statusInfo != null && statusInfo.status == 0) {
                    delayedHandler.removeCallbacks(pubSetTimeoutTask);
                    pvingDevice.state = ProvisioningDevice.STATE_PUB_SET_SUCCESS;
                    mListAdapter.notifyDataSetChanged();
                    startScanTelink();
                }
                break;
        }
    }


    private void onDeviceFound(AdvertisingDevice device) {
        int address = mesh.pvIndex + 1;

//        int address = 0x6666;
        TelinkLog.d("alloc address: " + address);

        ProvisioningDevice pvDevice = new ProvisioningDevice();
        pvDevice.uuid = null;
        pvDevice.macAddress = device.device.getAddress();
        pvDevice.state = ProvisioningDevice.STATE_PROVISIONING;
        pvDevice.unicastAddress = address;
        pvDevice.advertisingDevice = device;
//        pvDevice.elementCnt = remote.elementCnt;
        pvDevice.failReason = null;
        devices.add(pvDevice);
        if(mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 绑定蓝牙
     * @param device pvDevice.advertisingDevice
     * @param address  pvDevice.unicastAddress
     */
    private void bindBlue(AdvertisingDevice device, int address){
        targetDevice = new UnprovisionedDevice(device, address);
        byte[] provisionData = ProvisionDataGenerator.getProvisionData(mesh.networkKey, mesh.netKeyIndex, mesh.ivUpdateFlag, mesh.ivIndex, address);
        ProvisionParameters parameters = ProvisionParameters.getDefault(provisionData, new UnprovisionedDevice(device, address));
        MeshService.getInstance().startProvision(parameters);
    }


    public DeviceProvisionListAdapter getFoundDevicesAdapter(AddBlueToothDeviceActivity context) {
        this.mListAdapter = new DeviceProvisionListAdapter(context, devices);
        this.mListAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (devices == null || position >= devices.size()) return;
                ProvisioningDevice device = devices.get(position);
                if(device == null) return;
                bindBlue(device.advertisingDevice, device.unicastAddress);
            }
        });
        return this.mListAdapter;
    }


    // 扫描蓝牙
    public void startScanTelink() {
        ScanParameters parameters = ScanParameters.getDefault(false, true);
        parameters.setScanTimeout(20 * 1000);

        if (devices.size() != 0) {
            String[] excludeMacs = new String[devices.size()];
            for (int i = 0; i < devices.size(); i++) {
                excludeMacs[i] = devices.get(i).macAddress;
            }
            parameters.setExcludeMacs(excludeMacs);
        }
//        parameters.setIncludeMacs(new String[]{"FF:FF:BB:CC:DD:53"});
        MeshService.getInstance().startScan(parameters);
    }

    private void onProvisionSuccess(MeshEvent event) {

        DeviceInfo remote = event.getDeviceInfo();
        remote.bindState = DeviceBindState.BINDING;

        mesh.insertDevice(remote);
        mesh.pvIndex = remote.meshAddress + remote.elementCnt - 1;
        mesh.saveOrUpdate(mContext);


        ProvisioningDevice pvDevice = getDeviceByMac(remote.macAddress);
        if (pvDevice == null) return;
        pvDevice.uuid = null;
        pvDevice.macAddress = remote.macAddress;
        pvDevice.state = ProvisioningDevice.STATE_BINDING;
        pvDevice.unicastAddress = remote.meshAddress;
        pvDevice.elementCnt = remote.elementCnt;
        pvDevice.failReason = null;
        mListAdapter.notifyDataSetChanged();

        // check if private mode opened
        final boolean privateMode = SharedPreferenceHelper.isPrivateMode(mContext);

        // check if device support fast bind
        boolean fastBind = false;
        if (privateMode && targetDevice != null && targetDevice.scanRecord != null) {
            PrivateDevice device = getPrivateDevice(targetDevice.scanRecord);

            if (device != null) {
                TelinkLog.d("private device");
                NodeInfo nodeInfo = new NodeInfo();

                nodeInfo.nodeAdr = remote.meshAddress;
                nodeInfo.elementCnt = remote.elementCnt;
                nodeInfo.deviceKey = remote.deviceKey;
                final byte[] cpsData = device.getCpsData();
                nodeInfo.cpsData = NodeInfo.CompositionData.from(cpsData);
                nodeInfo.cpsDataLen = cpsData.length;
                remote.nodeInfo = nodeInfo;
                fastBind = true;
            } else {
                TelinkLog.d("private device null");
            }
        }

        KeyBindParameters parameters = KeyBindParameters.getDefault(remote,
                mesh.appKey, mesh.appKeyIndex, mesh.netKeyIndex, fastBind);

        MeshService.getInstance().startKeyBind(parameters);

    }

    private ProvisioningDevice getDeviceByMac(String mac) {
        if (devices == null) return null;
        for (ProvisioningDevice deviceInfo : devices) {
            if (deviceInfo.macAddress.equals(mac))
                return deviceInfo;
        }
        return null;
    }
    private PrivateDevice getPrivateDevice(byte[] scanRecord) {
        if (scanRecord == null) return null;
        MeshScanRecord sr = MeshScanRecord.parseFromBytes(scanRecord);
        byte[] serviceData = sr.getServiceData(ParcelUuid.fromString(UuidInfo.PROVISION_SERVICE_UUID.toString()));
        if (serviceData == null) return null;
        return PrivateDevice.filter(serviceData);
    }

    private void onProvisionFail(MeshEvent event) {
        DeviceInfo deviceInfo = event.getDeviceInfo();

        ProvisioningDevice pvDevice = getDeviceByMac(deviceInfo.macAddress);
        if (pvDevice == null) return;
        pvDevice.state = ProvisioningDevice.STATE_PROVISION_FAIL;
        pvDevice.unicastAddress = deviceInfo.meshAddress;
//        pvDevice.elementCnt = deviceInfo.elementCnt;
        pvDevice.failReason = null;
        mListAdapter.notifyDataSetChanged();

    }

    /**
     * @return state saved
     */
    private boolean onKeyBindSuccess(MeshEvent event) {
        DeviceInfo remote = event.getDeviceInfo();
        DeviceInfo local = mesh.getDeviceByMacAddress(remote.macAddress);
        if (local == null) return false;

        ProvisioningDevice deviceInList = getDeviceByMac(remote.macAddress);
        if (deviceInList == null) return false;

        deviceInList.state = ProvisioningDevice.STATE_BIND_SUCCESS;
        deviceInList.nodeInfo = remote.nodeInfo;
        mListAdapter.notifyDataSetChanged();

        local.bindState = DeviceBindState.BOUND;
        local.boundModels = remote.boundModels;
        local.nodeInfo = remote.nodeInfo;
        mesh.saveOrUpdate(mContext);

        return setPublish(deviceInList, local);

    }

    private boolean setPublish(ProvisioningDevice provisioningDevice, DeviceInfo local) {
        int modelId = SigMeshModel.SIG_MD_TIME_S.modelId;
        int pubEleAdr = local.getTargetEleAdr(modelId);
        if (pubEleAdr != -1) {

            PublishModel pubModel = new PublishModel(pubEleAdr, modelId, 0xFFFF, 30 * 1000);
            final int appKeyIndex = MyApplication.getInstance().getMesh().appKeyIndex;
            PubSetMessage pubSetMessage = PubSetMessage.createDefault(local.meshAddress,
                    pubModel.address, appKeyIndex, pubModel.period, pubModel.modelId, true);
            boolean result = MeshService.getInstance().setPublication(local.meshAddress, pubSetMessage, null);
            if (result) {
                pubSettingDevice = provisioningDevice;
                delayedHandler.postDelayed(pubSetTimeoutTask, 5 * 1000);
            }
            return result;
        } else {
            return false;
        }

    }

    private Runnable pubSetTimeoutTask = new Runnable() {
        @Override
        public void run() {
            if (pubSettingDevice != null) {
                pubSettingDevice.state = ProvisioningDevice.STATE_PUB_SET_SUCCESS;
                mListAdapter.notifyDataSetChanged();
                startScanTelink();
            }

        }
    };

    private void onKeyBindFail(MeshEvent event) {
        DeviceInfo remote = event.getDeviceInfo();
        DeviceInfo local = mesh.getDeviceByMacAddress(remote.macAddress);
        if (local == null) return;

        ProvisioningDevice deviceInList = getDeviceByMac(remote.macAddress);
        if (deviceInList == null) return;

        deviceInList.state = ProvisioningDevice.STATE_BIND_FAIL;
        mListAdapter.notifyDataSetChanged();

        local.bindState = DeviceBindState.UNBIND;
        local.boundModels = remote.boundModels;
        mesh.saveOrUpdate(mContext);
    }

    private ProvisioningDevice getDeviceByMeshAddress(int meshAddress) {
        if (devices == null) return null;
        for (ProvisioningDevice deviceInfo : devices) {
            if (deviceInfo.unicastAddress == meshAddress)
                return deviceInfo;
        }
        return null;
    }
}
