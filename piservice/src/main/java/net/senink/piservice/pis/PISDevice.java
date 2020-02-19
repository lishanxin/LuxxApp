package net.senink.piservice.pis;

import android.text.TextUtils;
import android.util.SparseArray;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.struct.LightTimerItem;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by wensttu on 2016/6/20.
 */
public class PISDevice extends PISBase {
    public static final String TAG = "PISDevice";
    public static final byte PIS_CMD_DEVICE_INFO_GET = (byte)0x90;
    public static final byte PIS_CMD_DEVICE_INFO_SET = (byte)0x91;
    public static final byte PIS_CMD_DEVICE_FWUPDATE = (byte)0x92;
    public static final byte PIS_CMD_DEVICE_WHOAMI = (byte)0x93;
    public static final byte PIS_CMD_DEVICE_REPLACE = (byte)0x94;
    public static final byte PIS_CMD_DEVICE_POWER_CAPACITY_GET = (byte)0x95;
    public static final byte PIS_CMD_DEVICE_REBOOT = (byte)0x96;
    public static final byte PIS_CMD_DEVICE_TIME_GET = (byte)0x97;
    public static final byte PIS_CMD_DEVICE_TIME_SET = (byte)0x98;

    public static final byte PIS_CMD_DEVICE_TIMER_SET    = (byte)0x99;
    public static final byte PIS_CMD_DEVICE_TIMER_GET    = (byte)0x9A;
    public static final byte PIS_CMD_DEVICE_TIMER_COUNT  = (byte)0x9B;
    public static final byte PIS_CMD_DEVICE_TIMER_DELETE = (byte)0x9C;

    public static final byte PIS_CMD_DEVICE_INS_SMART = (byte)0xA2;
    public static final byte PIS_CMD_DEVICE_UNINS_SMART = (byte)0xA3;
    public static final byte PIS_CMD_DEVICE_RESET = (byte)0xAA;
    public static final byte PIS_CMD_DEVICE_FACTORYRESET = (byte)0xAB;

    public static final byte PIS_CMD_DEVICE_EXINFO_GET = (byte)0xA0;
    public static final byte PIS_CMD_DEVICE_EXINFO_SET = (byte)0xA1;

    public static final byte PIS_CMD_DEVICE_WAKEUP     = (byte)0xF0;

    public static final byte PIS_MSG_FWUPDATE_PROGRESS = (byte)0x30;
    public static final byte PIS_MSG_FWUPDATE_COMPLETED = (byte)0x31;

    public transient int fwUpdateProgress = 0;
    public transient boolean updateCompleted = false;

    private transient long currentTime = 0;

    private byte[] ClassId = new byte[6];
    private byte[] Version = new byte[4];


    private void init(){
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_INFO_GET,
                "获取设备信息",
                "Get device infomation, include classid,MAC address, etc.",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_INFO_SET,
                "设置设备信息",
                "Set device Infomation",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_WHOAMI,
                "Who am I",
                "when device recive the command, it should indecate self",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_REPLACE,
                "替换设备",
                "when recive the command, the device do somethine for it be replace",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_POWER_CAPACITY_GET,
                "获取电源管理信息",
                "Get device power capacity, from 0 ~ 100 per",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_REBOOT,
                "重启设备",
                "when recive the command, the device reboot self",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_TIME_GET,
                "time sync",
                "",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_TIME_SET,
                "time get",
                "",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_TIMER_GET,
                "Timer get",
                "",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_TIMER_SET,
                "Timer set",
                "",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_TIMER_COUNT,
                "Timer count",
                "",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_TIMER_DELETE,
                "Timer delete",
                "",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_INS_SMART,
                "安装智能元",
                "install smart cell plug-in",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_UNINS_SMART,
                "删除智能元",
                "uninstall smart cell plug-in",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_RESET,
                "复位设备",
                "when recive the command, the device reset self",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_FACTORYRESET,
                "恢复出厂",
                "Restore factory settings",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_DEVICE_WAKEUP,
                "唤醒设备",
                "Wakeup the device if it on sleep mode",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Messages.add(new PICommandProperty(PIS_MSG_FWUPDATE_PROGRESS,
                "固件升级进度",
                "indecate the progress of firmware upgrade",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_FWUPDATE_COMPLETED,
                "固件升级完成",
                "the device complate update progress",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
    }

    public PISDevice(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo, SERVICE_TYPE_DEVICE);

        init();
    }

    public PISDevice(PIAddress addr, PIServiceInfo srvInfo, String mac){
        super(addr, srvInfo, SERVICE_TYPE_DEVICE);

        mMacAddress = ByteUtilBigEndian.StringToByte(mac);

        init();
    }

    /**
     * 设备相关属性
     */
    private transient String m_macString = null;
    public String getMacString(){
        if (mMacAddress == null)
            return null;
        try {
            if (m_macString == null)
                m_macString = ByteUtilBigEndian.macAddrToStrWithoutSeparator(mMacAddress);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return m_macString;
    }

    public byte[] getMacByte(){
        return mMacAddress;
    }

    public void setMac(byte[] mac){
        if (mac != null && mac.length == 8) {
            if (mMacAddress == null)
                mMacAddress = new byte[8];
            System.arraycopy(mac, 0, mMacAddress, 0, mMacAddress.length);
        }
    }

    public String getClassString(){
        return ByteUtilBigEndian.classIdToStr(ClassId);
    }
    public byte[] getClassByte(){
        return ClassId;
    }
    public void setClassId(byte[] clsid){
        if (clsid != null && clsid.length == ClassId.length)
            System.arraycopy(clsid, 0, ClassId, 0, ClassId.length);
    }

    public String getVersionString(){
        return String.format("%d.%d.%d", Version[0], Version[1], Version[2] +  (Version[3] << 8));
    }
    public byte[] getVersionByte(){
        return Version;
    }
    public void setVersion(byte[] verBytes){
        Version = verBytes;
    }


    public ArrayList<PISBase> getPIServices() {
        return (ArrayList<PISBase>)PISManager.getInstance().PIServicesWithQuery(this,
                PISManager.EnumServicesQueryBaseonDevice);
    }

    /**
     * PIPA相关操作
     */
    /**处理设备信息*/
    private void processDeviceInfo(byte[] ackData){
        byte[] macBytes = new byte[8];
        System.arraycopy(ackData, 0, macBytes, 0, 8);
        this.setMac(macBytes);
        byte[] locBytes = new byte[2];
        System.arraycopy(ackData, 8, locBytes, 0, 2);
        this.setLocation(locBytes);
        byte[] clsBytes = new byte[6];
        System.arraycopy(ackData, 10, clsBytes, 0, 6);
        this.setClassId(clsBytes);
        byte[] verBytes = new byte[4];
        System.arraycopy(ackData, 16, verBytes, 0, 4);
        this.setVersion(verBytes);
    }

    @Override
    public void Initialization(){
        super.Initialization();

        if (mMacAddress == null && getStatus() != PISBase.SERVICE_STATUS_ONLINE){
            PipaRequest macReq = updateDeviceInfo();
            macReq.setRetry(100);
            request(macReq);

        }
        setStatus(PISBase.SERVICE_STATUS_ONLINE);
    }

    public long getDeviceTime(){
        return currentTime;
    }

    protected HashMap<Integer, LightTimerItem> timerList;
    public List<LightTimerItem> getTimerList(){
        if (timerList == null)
            timerList = new HashMap<>();

        return new ArrayList<>(timerList.values());
    }

    public LightTimerItem getTimerItem(int tid){
        if (timerList == null){
            throw new NullPointerException("timerList is null");
        }

        return timerList.get(tid);
    }

    private void proessTimerData(byte[] ackData){
        if (ackData == null || ackData.length < 7)
            return;

        LightTimerItem ti = null;

        if (timerList == null)
            timerList = new HashMap<>();

        try{
            int tid = (ackData[0]&0xF0);
            ti = timerList.get(tid);

            if (ti != null){
                timerList.remove(tid);
            }
            ti = new LightTimerItem(ackData);
            timerList.put(ti.getTimerId(), ti);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {

        switch (msg)
        {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK:
            {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch((int)ackData.Command)
                {
                    case PIS_CMD_DEVICE_FWUPDATE: {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED){
                            LogUtils.i(TAG, "Firmware update successed");
                        }
                    }
                    break;
                    case PIS_CMD_DEVICE_WHOAMI: {
                        setStatus(PISBase.SERVICE_STATUS_ONLINE);

                        try {
                            List<PISBase> srvs = this.getPIServices();
                            for (PISBase srv : srvs) {
                                setStatus(PISBase.SERVICE_STATUS_ONLINE);
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                    case PIS_CMD_DEVICE_INFO_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        processDeviceInfo(ackData.data);
                    }
                    break;
                    case PIS_CMD_DEVICE_INS_SMART:
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED)
                            LogUtils.i(TAG, "Smartcell install successed!");
                        break;
                    case PIS_CMD_DEVICE_UNINS_SMART: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                    }
                    break;
                    case PIS_CMD_DEVICE_INFO_SET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        processDeviceInfo(ackData.rawRequestData.Data);
                        break;
                    case PIS_CMD_DEVICE_EXINFO_GET:
                        break;
                    case PIS_CMD_DEVICE_TIME_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;

                        try {
                            currentTime = ByteUtilLittleEndian.getInt(ackData.data);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                        break;
                    case PIS_CMD_DEVICE_TIME_SET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        try {
                            if (ackData.rawRequestData != null)
                                currentTime = ByteUtilLittleEndian.getInt(ackData.rawRequestData.Data);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case PIS_CMD_DEVICE_TIMER_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        proessTimerData(ackData.data);
                    }
                    break;
                    case PIS_CMD_DEVICE_TIMER_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        ackData.rawRequestData.Data[0] = ackData.data[0];
                        proessTimerData(ackData.rawRequestData.Data);

                    }
                    break;
                    case PIS_CMD_DEVICE_TIMER_COUNT:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        try {
                            int timerCount = ackData.data[0];
                            if (timerList == null)
                                timerList = new HashMap<>();
                            if (timerCount == 0) {
                                timerList.clear();
                                break;
                            }
                            ArrayList<Integer> tids = new ArrayList<>();
                            HashMap<Integer, LightTimerItem> aaa = (HashMap<Integer, LightTimerItem>)timerList.clone();
                            for (int i = 0; i < timerCount; i++) {
                                int tid = (ackData.data[i + 1] & 0xF0);
                                if (tid != 0)
                                    tids.add(tid);
                                if (timerList.get(tid) == null) {
                                    LightTimerItem lti = new LightTimerItem(tid);
                                    timerList.put(lti.getTimerId(), lti);
                                }
                            }

                            for (LightTimerItem ti : aaa.values()){
                                if (!tids.contains(ti.getTimerId()))
                                    timerList.remove(ti.getTimerId());

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                    case PIS_CMD_DEVICE_TIMER_DELETE:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        try{
                            timerList.remove((int)ackData.rawRequestData.Data[0]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                    case PIS_CMD_DEVICE_REBOOT:
                    case PIS_CMD_DEVICE_RESET:
                    case PIS_CMD_DEVICE_FACTORYRESET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        PISManager.getInstance().removePISObject(this);
                        break;

                }
            }
            break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISCommandAckData ackData = (PISCommandAckData) lPara;
                if (hPara == PIS_MSG_FWUPDATE_PROGRESS){
                    this.fwUpdateProgress = ackData.data[0];
                }else if (hPara == PIS_MSG_FWUPDATE_COMPLETED) {
                    this.fwUpdateProgress = ackData.data[0];
                    this.updateCompleted = ackData.data[1]!=0;
                }
                break;
            }
        }

        super.onProcess(msg, hPara, lPara);
    }

    /**
     * 设备命令
     */
    public PipaRequest reboot(){
        return new PipaRequest(this, PIS_CMD_DEVICE_REBOOT, null, 0, true);
    }

    public PipaRequest reset(){
        return new PipaRequest(this, PIS_CMD_DEVICE_FACTORYRESET, null, 0, true);
    }

    public PipaRequest updateDeviceInfo(){
        return new PipaRequest(this, PIS_CMD_DEVICE_INFO_GET, null, 0, true);
    }

    public PipaRequest whoami(){
        return new PipaRequest(this, PIS_CMD_DEVICE_WHOAMI, null, 0, true);
    }

    public PipaRequest commitTime(int timeStamp){
        byte[] timeBytes = new byte[4];

        timeBytes[0] = (byte)(timeStamp & 0xFF);
        timeBytes[1] = (byte)((timeStamp & 0xFF00) >> 8);
        timeBytes[2] = (byte)((timeStamp & 0xFF0000) >> 16);
        timeBytes[3] = (byte)((timeStamp & 0xFF000000) >> 24);
        return new PipaRequest(this, PIS_CMD_DEVICE_TIME_SET, timeBytes, 4, true);
    }

    public PipaRequest updateDeviceFirmware(int version){
        this.fwUpdateProgress = 0;
        this.updateCompleted = false;
        byte[] dataBytes = new byte[20];


        return null;
    }

    public PipaRequest installSmartcell(byte[] guid){
        try {
            String hexStr = ByteUtilBigEndian.getHexString(guid);

            return new PipaRequest(this, PIS_CMD_DEVICE_INS_SMART, hexStr.getBytes(), hexStr.length(), true);

        }catch (Exception ex){
            return null;
        }


    }

    public PipaRequest uninstallSmartcell(byte[] guid){
        try{
            String hexStr = ByteUtilBigEndian.getHexString(guid);

            return new PipaRequest(this, PIS_CMD_DEVICE_UNINS_SMART, hexStr.getBytes(), hexStr.length(), true);
        }catch (Exception ex){
            return null;
        }
    }

    //public int timerCount = 0;
    public PipaRequest commitTimerItem(int cycle, int timestamp, int srvid, PipaRequestData reqData){
        LightTimerItem ti = new LightTimerItem(LightTimerItem.INVAILD_TIMER_ID,
                cycle,
                timestamp,
                srvid,
                reqData);

        byte[] timerBytes = ti.getBytes();

        return new PipaRequest(this, PIS_CMD_DEVICE_TIMER_SET, timerBytes, timerBytes.length, true);
    }

    public PipaRequest removeTimerItem(int timerId){
        byte[] timerBytes = new byte[1];

        timerBytes[0] = (byte)(timerId & 0xFF);
        return new PipaRequest(this, PIS_CMD_DEVICE_TIMER_DELETE, timerBytes, 1, true);
    }

    public PipaRequest fecthTimer(int timerId){
        byte[] timerBytes = new byte[1];

        timerBytes[0] = (byte)(timerId & 0xFF);
        return new PipaRequest(this, PIS_CMD_DEVICE_TIMER_GET, timerBytes, 1, true);
    }

    public PipaRequest fecthTimerCount(){
        return new PipaRequest(this, PIS_CMD_DEVICE_TIMER_COUNT, null, 0, true);
    }

    public PipaRequest wakeupDevice(int timeStamp){
        byte[] timeBytes = new byte[4];

        timeBytes[0] = (byte)(timeStamp & 0xFF);
        timeBytes[1] = (byte)((timeStamp & 0xFF00) >> 8);
        timeBytes[2] = (byte)((timeStamp & 0xFF0000) >> 16);
        timeBytes[3] = (byte)((timeStamp & 0xFF000000) >> 24);

        return new PipaRequest(this, PIS_CMD_DEVICE_WAKEUP, timeBytes, 4, true);
    }
    public void setStatus(int newStatus){
        if (newStatus == this.getStatus())
            return;
        if (newStatus == PISBase.SERVICE_STATUS_ONLINE){
            Date curDate = new Date();
            Calendar nowDate = Calendar.getInstance();
            nowDate.setTime(curDate);
            int curSecond = nowDate.get(Calendar.HOUR_OF_DAY) * 3600 +
                    nowDate.get(Calendar.MINUTE) * 60 +
                    (1000 * 24 * 3600);
            request(commitTime(curSecond));
        }
        List<PISBase> srvs = this.getPIServices();
        for (PISBase srv : srvs){
            if (srv.getStatus() != newStatus)
                srv.setStatus(newStatus);
        }
        if (newStatus == PISBase.SERVICE_STATUS_OFFLINE)
            currentTime = 0;

        super.setStatus(newStatus);
    }

}
