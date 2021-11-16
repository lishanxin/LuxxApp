package net.senink.seninkapp.telink.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.telink.sig.mesh.ble.AdvertisingDevice;
import com.telink.sig.mesh.ble.MeshScanRecord;
import com.telink.sig.mesh.ble.UnprovisionedDevice;
import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.event.ScanEvent;
import com.telink.sig.mesh.light.LeBluetooth;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.light.ProvisionDataGenerator;
import com.telink.sig.mesh.light.PublicationStatusParser;
import com.telink.sig.mesh.light.ScanParameters;
import com.telink.sig.mesh.light.UuidInfo;
import com.telink.sig.mesh.light.parameter.AutoConnectParameters;
import com.telink.sig.mesh.light.parameter.KeyBindParameters;
import com.telink.sig.mesh.light.parameter.ProvisionParameters;
import com.telink.sig.mesh.model.CommonMeshCommand;
import com.telink.sig.mesh.model.DeviceBindState;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.NodeInfo;
import com.telink.sig.mesh.model.NotificationInfo;
import com.telink.sig.mesh.model.PublishModel;
import com.telink.sig.mesh.model.SigMeshModel;
import com.telink.sig.mesh.model.message.config.PubSetMessage;
import com.telink.sig.mesh.util.MeshUtils;
import com.telink.sig.mesh.util.TelinkLog;
import com.telink.sig.mesh.util.UnitConvert;

import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.telink.AppSettings;
import net.senink.seninkapp.telink.SharedPreferenceHelper;
import net.senink.seninkapp.telink.model.Mesh;
import net.senink.seninkapp.telink.model.PrivateDevice;
import net.senink.seninkapp.telink.model.ProvisioningDevice;
import net.senink.seninkapp.telink.model.TelinkOperation;
import net.senink.seninkapp.telink.view.BaseRecyclerViewAdapter;
import net.senink.seninkapp.telink.view.DeviceProvisionListAdapter;
import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity;
import net.senink.seninkapp.ui.home.TelinkDataRefreshEntry;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Li Shanxin
 * @date: 2020/2/20
 * @description:
 */
public class TelinkApiManager implements EventListener<String> {
    public static final String REFRESH_DEVICES = "TELINK_REFRESH_DEVICES";
    private static final String TAG = TelinkApiManager.class.getSimpleName();
    private static TelinkApiManager instance;
    private boolean isServiceCreated = false;
    private Context mContext;
    private List<ProvisioningDevice> devices;
    private List<String> boundDevices;
    private Mesh mesh;
    private DeviceProvisionListAdapter mListAdapter;
    private UnprovisionedDevice targetDevice;
    private ProvisioningDevice pubSettingDevice;
    private Handler delayedHandler = new Handler();
    private Handler addDeviceActivityHandler = null;
    private boolean isAutoBind = false;
    private boolean isStopScan;
    public static final String IS_TELINK_KEY = "isTelinkKey";
    public static final String IS_TELINK_DEVICE_KEY = "isTelinkDeviceKey";
    public static final String IS_TELINK_GROUP_KEY = "isTelinkGroup";
    public static final String TELINK_ADDRESS = "TELINKADDRESS";
    public static final String TELINK_Timer_Action_Data = "telink_timer_action_data";

    private static Date expiredTime;

    public static TelinkApiManager getInstance() {
        if (instance == null) {
            synchronized (TelinkApiManager.class) {
                if (instance == null) {
                    instance = new TelinkApiManager();
                }
            }
        }
        if(expiredTime != null){
            if(new Date().before(expiredTime)){
                return instance;
            }else{
                return null;
            }
        }else{
            return instance;
        }

    }

    public void init(Context context) {
        devices = new ArrayList<>();
        mesh = MyApplication.getInstance().getMesh();
        mContext = context;
        connectedListener = new MeshService.OnCheckConnectedListener() {
            @Override
            public void onCheckConnected(boolean isConnected) {
                if(!isConnected){

                }
            }
        };
        addEventListener();

        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            expiredTime = dateFormat2.parse("2021-12-01 00:00:01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // 检测是否需要刷新
    private MeshService.OnCheckConnectedListener connectedListener;


    public List<ProvisioningDevice> getFoundDevices() {
        return devices;
    }

    public void startMeshService(Activity activity, EventListener<String> listener) {
        Intent serviceIntent = new Intent(activity.getApplicationContext(), MeshService.class);
        activity.getApplicationContext().startService(serviceIntent);
    }

    public void addEventListener() {
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
        MyApplication.getInstance().addEventListener(ScanEvent.SCAN_TIMEOUT, this);
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case MeshController.EVENT_TYPE_SERVICE_CREATE:
                TelinkLog.d(TAG + "#EVENT_TYPE_SERVICE_CREATE");
                isServiceCreated = true;
                MeshService.getInstance().setCheckConnectedListener(connectedListener);
                autoConnect(false);
//                _startScanTelink();
                break;
            case MeshController.EVENT_TYPE_SERVICE_DESTROY:
                TelinkLog.d(TAG + "-- service destroyed event");
                break;
            case ScanEvent.DEVICE_FOUND:
                AdvertisingDevice device = ((ScanEvent) event).advertisingDevice;
                onDeviceFound(device);
                break;
            case ScanEvent.SCAN_TIMEOUT:
                if(devices != null && devices.size() > 0) return;
                if(isStopScan) return;
                _startScanTelink();
                break;
            case MeshEvent.EVENT_TYPE_PROVISION_SUCCESS:
                onProvisionSuccess((MeshEvent) event);
                break;
            case MeshEvent.EVENT_TYPE_PROVISION_FAIL:
                onProvisionFail((MeshEvent) event);
                _startScanTelink();
                break;
            case MeshEvent.EVENT_TYPE_KEY_BIND_SUCCESS:
                Intent intent = new Intent(REFRESH_DEVICES);
                mContext.sendBroadcast(intent);
                if (onKeyBindSuccess((MeshEvent) event)) {
                    TelinkLog.d("set device time publish");
                } else {
//                    _startScanTelink();
                }
                break;
            case MeshEvent.EVENT_TYPE_KEY_BIND_FAIL:
                onKeyBindFail((MeshEvent) event);
                _startScanTelink();
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
                    _startScanTelink();
                }
                break;
            case NotificationEvent.EVENT_TYPE_KICK_OUT_CONFIRM:
                autoConnect(true);
                break;
            case MeshEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN:
                // get all device on off status when auto connect success
//
                AppSettings.ONLINE_STATUS_ENABLE = MeshService.getInstance().getOnlineStatus();
                if (!AppSettings.ONLINE_STATUS_ENABLE) {
                    MeshService.getInstance().getOnOff(0xFFFF, 0, null);
                }
                sendTimeStatus();
                break;
        }
    }

    public void stopScan(){
        this.isStopScan = true;
    }

    public void sendTimeStatus() {
        MyApplication.getInstance().getOfflineCheckHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long time = MeshUtils.getTaiTime();
                int offset = UnitConvert.getZoneOffset();
                final int eleAdr = 0xFFFF;
                MeshService.getInstance().sendTimeStatus(eleAdr, 1, time, offset, null);
            }
        }, 1500);

    }


    private void onDeviceFound(AdvertisingDevice device) {
        Log.d(TAG, "onDeviceFound");
        EventBus.getDefault().post(new TelinkOperation(TelinkOperation.DEVICE_FOUND));
        EventBus.getDefault().post(new TelinkDataRefreshEntry(true));
        if (mesh == null) {
            mesh = MyApplication.getInstance().getMesh();
        }
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
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
        targetDevice = new UnprovisionedDevice(device, address);
//        devices.add(targetDevice);
        byte[] provisionData = ProvisionDataGenerator.getProvisionData(mesh.networkKey, mesh.netKeyIndex, mesh.ivUpdateFlag, mesh.ivIndex, address);
        ProvisionParameters parameters = ProvisionParameters.getDefault(provisionData, targetDevice);
//        MeshService.getInstance().startProvision(parameters);
        if(this.isAutoBind){
            startProvision(parameters);
        }
    }

    public ProvisionParameters getProvisionParameters(AdvertisingDevice device, int address) {
        Log.d(TAG, "getProvisionParameters");
        UnprovisionedDevice targetDevice = new UnprovisionedDevice(device, address);
        byte[] provisionData = ProvisionDataGenerator.getProvisionData(mesh.networkKey, mesh.netKeyIndex, mesh.ivUpdateFlag, mesh.ivIndex, address);
        ProvisionParameters parameters = ProvisionParameters.getDefault(provisionData, targetDevice);
        return parameters;
    }


    /**
     * 绑定蓝牙
     *
     */
    private void bindBlue(ProvisioningDevice proDevice){
        Log.d(TAG, "bindBlue");
        if (proDevice == null) return;
        AdvertisingDevice device = proDevice.advertisingDevice;
        int address = proDevice.unicastAddress;
        isStopScan = true;
        targetDevice = new UnprovisionedDevice(device, address);
        byte[] provisionData = ProvisionDataGenerator.getProvisionData(mesh.networkKey, mesh.netKeyIndex, mesh.ivUpdateFlag, mesh.ivIndex, address);
        ProvisionParameters parameters = ProvisionParameters.getDefault(provisionData, new UnprovisionedDevice(device, address));
        startProvision(parameters);
    }


    public DeviceProvisionListAdapter getFoundDevicesAdapter(AddBlueToothDeviceActivity context, Handler handler) {
        Log.d(TAG, "getFoundDevicesAdapter");
        this.mListAdapter = new DeviceProvisionListAdapter(context, devices, handler);
        this.mListAdapter.setListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (devices == null || position >= devices.size()) return;
                ProvisioningDevice device = devices.get(position);
                bindBlue(device);
            }
        });
        return this.mListAdapter;
    }

    private int timeout = 7 * 1000;
    private void _startScanTelink(){
        Log.d(TAG, "_startScanTelink");
//        delayedHandler.removeCallbacks(recyclerScan);
        if(isStopScan){
            return;
        }
//        delayedHandler.postDelayed(recyclerScan, timeout);
        ScanParameters parameters = ScanParameters.getDefault(false, true);
        parameters.setScanTimeout(timeout);

        List<String> excludeList = new ArrayList<>();
        for (ProvisioningDevice device : devices) {
            excludeList.add(device.macAddress);
        }

        excludeList.addAll(boundDevices);
        String[] excludeMacs = excludeList.toArray(new String[0]);
        if (excludeList.size() > 0) {
            parameters.setExcludeMacs(excludeMacs);
        }
//        parameters.setIncludeMacs(new String[]{"FF:FF:BB:CC:DD:53"});
        MeshService.getInstance().startScan(parameters);
    }

    public void startScanTelink(boolean isAutoBind, Handler handler) {
        Log.d(TAG, "startScanTelink isAutoBind:" + isAutoBind);
        this.isAutoBind = isAutoBind;
        addDeviceActivityHandler = handler;
        startScanTelink();
    }

    public boolean startAutoBindOnCreate(Handler handler){
        Log.d(TAG, "startAutoBindOnCreate");
        this.isAutoBind = true;
        addDeviceActivityHandler = handler;
        addDeviceActivityHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_TELINK_LINE_INIT);
        if(checkHaveTelinkDevice()){
            bindBlue(devices.get(0));
            return true;
        }else{
            return false;
        }
    }

    public boolean checkHaveTelinkDevice(){
        Log.d(TAG, "checkHaveTelinkDevice");
        return devices.size() > 0 && devices.get(0) != null;
    }

    public void startScanTelinkOnCreate(boolean isTelinkAutoConnect, Handler mHandler) {
        Log.d(TAG, "startScanTelinkOnCreate");
        this.isAutoBind = isTelinkAutoConnect;
        addDeviceActivityHandler = mHandler;
        if(!checkHaveTelinkDevice()){
            startScanTelink();
        }
    }

    // 扫描蓝牙
    public void startScanTelink() {
        Log.d(TAG, "startScanTelink");
        isStopScan = false;
        TelinkApiManager.getInstance().clearFoundDevice();

        boundDevices = new ArrayList<>();
        for (DeviceInfo boundDevice : MyApplication.getInstance().getMesh().devices) {
            if(boundDevice.getOnOff() != -1){
                boundDevices.add(boundDevice.macAddress);
            }
        }
        _startScanTelink();
    }

    public void clearFoundDevice() {
        Log.d(TAG, "clearFoundDevice");
        if (devices != null) {
            devices.clear();
            if(mListAdapter != null){
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void onProvisionSuccess(MeshEvent event) {
        Log.d(TAG, "onProvisionSuccess");
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
        Log.d(TAG, "getDeviceByMac");
        if (devices == null) return null;
        for (ProvisioningDevice deviceInfo : devices) {
            if (deviceInfo.macAddress.equals(mac))
                return deviceInfo;
        }
        return null;
    }

    private PrivateDevice getPrivateDevice(byte[] scanRecord) {
        Log.d(TAG, "getPrivateDevice");
        if (scanRecord == null) return null;
        MeshScanRecord sr = MeshScanRecord.parseFromBytes(scanRecord);
        byte[] serviceData = sr.getServiceData(ParcelUuid.fromString(UuidInfo.PROVISION_SERVICE_UUID.toString()));
        if (serviceData == null) return null;
        return PrivateDevice.filter(serviceData);
    }

    private void onProvisionFail(MeshEvent event) {
        Log.d(TAG, "onProvisionFail");
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
        Log.d(TAG, "onKeyBindSuccess");
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

        if(this.isAutoBind){
            if(addDeviceActivityHandler != null){
                final Message message = new Message();
                message.what = AddBlueToothDeviceActivity.MSG_TELINK_CONFIG_SUCCESS_AUTO_CONNECT;
                message.obj = local;
                addDeviceActivityHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if( MeshService.getInstance().isDeviceConnected()){
                            addDeviceActivityHandler.sendMessage(message);
                        }else{
                            addDeviceActivityHandler.sendMessageDelayed(message, 4000);
                        }
                    }
                }, 1000);
            }
        }
        getInstance().setCommonCommand(local.meshAddress, CommonMeshCommand.getSyncTimeCommand());
        return setPublish(deviceInList, local);

    }

    private void startProvision(ProvisionParameters parameters){
        Log.d(TAG, "startProvision");
        addDeviceActivityHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_TELINK_LINE_INIT);
        MeshService.getInstance().startProvision(parameters);
    }

    private boolean setPublish(ProvisioningDevice provisioningDevice, DeviceInfo local) {
        Log.d(TAG, "setPublish");
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
            Log.d(TAG, "pubSetTimeoutTask");
            if (pubSettingDevice != null) {
                pubSettingDevice.state = ProvisioningDevice.STATE_PUB_SET_SUCCESS;
                mListAdapter.notifyDataSetChanged();
                _startScanTelink();
            }
        }
    };

//    private Runnable recyclerScan = new Runnable() {
//        @Override
//        public void run() {
//            Log.d(TAG, "recyclerScan");
//            if(!isStopScan && devices.size() == 0){
//                _startScanTelink();
//            }
//        }
//    };


    private void onKeyBindFail(MeshEvent event) {
        Log.d(TAG, "onKeyBindFail");
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
        Log.d(TAG, "getDeviceByMeshAddress");
        if (devices == null) return null;
        for (ProvisioningDevice deviceInfo : devices) {
            if (deviceInfo.unicastAddress == meshAddress)
                return deviceInfo;
        }
        return null;
    }

    public void setCommonCommand(int hslEleAdr, byte[] command) {
        Log.d(TAG, "setCommonCommand");
        MeshService.getInstance().setCommonCommand(hslEleAdr,
                false, command);
    }


    // 设置亮度
    public void setProgress() {

    }

    // 灯具开关
    @Deprecated
    public void setSwitchLightOnOff(int hslEleAdr, boolean isOn) {
        Log.d(TAG, "setSwitchLightOnOff");
        if(MeshService.getInstance() != null){
            MeshService.getInstance().setOnOff(hslEleAdr, (byte) (isOn ? 1 : 0), !AppSettings.ONLINE_STATUS_ENABLE, !AppSettings.ONLINE_STATUS_ENABLE ? 1 : 0, 0, (byte) 0, null);
        }
    }

    /**
     * 检测设备是否连接
     */
    private void checkDeviceConnected() {

    }

    private void autoConnect(boolean update) {
        Log.d(TAG, "autoConnect");
        TelinkLog.d("main auto connect");
        List<DeviceInfo> deviceInfoList = MyApplication.getInstance().getMesh().devices;

        Set<String> targets = new HashSet<>();
        if (deviceInfoList != null) {
            for (DeviceInfo deviceInfo : deviceInfoList) {
                targets.add(deviceInfo.macAddress);
            }
        }

        AutoConnectParameters parameters = AutoConnectParameters.getDefault(targets);
        parameters.setScanMinPeriod(0);
        if (update) {
            MeshService.getInstance().updateAutoConnectParams(parameters);
        } else {
            MeshService.getInstance().autoConnect(parameters);
        }

    }

    /**
     * 灯具连接状态刷新，刷新灯具连接状态，并进行连接后，才可进行命令操作。
     *
     * @param context
     */
    public void autoConnectToDevices(Context context) {
        Log.d(TAG, "autoConnectToDevices");
        if (!LeBluetooth.getInstance().isEnabled()) {
            LeBluetooth.getInstance().enable(context);
        } else {
            if (isServiceCreated) {
                autoConnect(false);
            }
        }
    }



    public void saveOrUpdateMesh(Context context){
        MyApplication.getInstance().getMesh().saveOrUpdate(context);
    }

}
