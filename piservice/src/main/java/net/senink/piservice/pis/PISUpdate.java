package net.senink.piservice.pis;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by wensttu on 2016/6/22.
 */
public class PISUpdate extends PISBase {
    public class PISFirmwareInfo implements Serializable {
        byte[] classId;
        int version;
        int length;

        public String getClassIdString(){
            return ByteUtilBigEndian.classIdToStr(classId);
        }

        public String getVersionString(){
            return String.format(Locale.getDefault(), "%d.%d.%d",
                    (version & 0xFF000000) >> 24,
                    (version & 0xFF0000) >> 16,
                    (version & 0xFFFF));
        }
    }
    public static final byte PIS_CMD_FWUPDATE_CHECK = (byte)0x90;
    public static final byte PIS_CMD_FWUPDATE_GET = (byte)0x91;
    public static final byte PIS_CMD_FWUPDATE_UPDATE = (byte)0x92;

    public static final byte PIS_MSG_FWUPDATE_PROGRESS = (byte)0x30;
    public static final byte PIS_MSG_FWUPDATE_COMPLETED = (byte)0x31;

    public List<PISFirmwareInfo> firmwareInfoList;
    public PISUpdate(PIAddress addr, PIServiceInfo si){
        super(addr, si, SERVICE_TYPE_SYSTEM);

        Commands.add(new PICommandProperty(PIS_CMD_FWUPDATE_CHECK,
                "Get Device's the newest firmware info",
                "Get Device's the newest firmware info",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_FWUPDATE_UPDATE,
                "upgrade firmware for the device",
                "upgrade firmware for the device",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_FWUPDATE_GET,
                "get firmware from cloud server",
                "get firmware from cloud server",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Messages.add(new PICommandProperty(PIS_MSG_FWUPDATE_COMPLETED,
                "完成消息",
                "当前设备固件成功完成升级",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_FWUPDATE_PROGRESS,
                "progress of firmware upgrade",
                "progress of firmware upgrade",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));

        firmwareInfoList = new ArrayList<PISFirmwareInfo>();
    }

    /**
     * PIPA 处理
     */
    private void processFirmwareCheck(byte[] ackData){
        this.firmwareInfoList.clear();
        byte[] numBytes = new byte[2];
        System.arraycopy(ackData, 0, numBytes, 0, 2);
        int num = ByteUtilLittleEndian.getInt(numBytes);

        for (int i=0; i<num; i++){
            byte[] clsBytes = new byte[6];
            System.arraycopy(ackData, 2 + i*18, clsBytes, 0, 6);
            byte[] verBytes = new byte[4];
            System.arraycopy(ackData, 2 + i*18 + 6, verBytes, 0, 4);
            byte[] lenBytes = new byte[4];
            System.arraycopy(ackData, 2 + i*18 + 10, lenBytes, 0, 4);
            byte[] crcBytes = new byte[4];
            System.arraycopy(ackData, 2 + i*18 + 14, crcBytes, 0, 4);

            PISFirmwareInfo fi = new PISFirmwareInfo();
            fi.classId = clsBytes;
            fi.version = ByteUtilLittleEndian.getInt(verBytes);
            fi.length  = ByteUtilLittleEndian.getInt(lenBytes);

            firmwareInfoList.add(fi);
        }
    }

    protected void onProcess(int msg, int hPara, Object lPara) {
        LogUtils.i("PISUpdate", "PISUpdate:onProcess");
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch(ackData.Command){
                    case PIS_CMD_FWUPDATE_CHECK: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null || ackData.data.length == 0)
                            break;
                        processFirmwareCheck(ackData.data);
                    }
                    break;
                    case PIS_CMD_FWUPDATE_UPDATE:  {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED)
                            LogUtils.i("PISUpdate", "starting update firmware");
                    }
                    break;
                }
            }
            break;
        }
        super.onProcess(msg, hPara, lPara);
    }

    /**
     * PISUpdate 方法
     */
    public PipaRequest firmwareCheck(byte[] cls){
        return new PipaRequest(this, PIS_CMD_FWUPDATE_CHECK, cls, 6, true);
    }

    public PipaRequest updateDeviceFirmware(PISFirmwareInfo info, PISDevice dev){
        if (info == null || dev == null)
            throw new IllegalArgumentException("参数不能为null");

        byte[] dataBytes = new byte[20];

        dataBytes[0] = (byte)(dev.getPanId() & 0xFF);
        dataBytes[1] = (byte)((dev.getPanId() & 0xFF00) >> 8);
        dataBytes[2] = (byte)(dev.getShortAddr() & 0xFF);
        dataBytes[3] = (byte)((dev.getShortAddr() & 0xFF00) >> 8);
        dataBytes[4] = (byte)(dev.getServiceId() & 0xFF);
        dataBytes[5] = (byte)((dev.getServiceId() & 0xFF00) >> 8);

        System.arraycopy(dev.getClassByte(), 0, dataBytes, 6, 6);
        System.arraycopy(ByteUtilLittleEndian.getBytes(info.version), 0, dataBytes, 12, 4);
        System.arraycopy(ByteUtilLittleEndian.getBytes(info.length), 0, dataBytes, 16, 4);

        return new PipaRequest(this, PIS_CMD_FWUPDATE_UPDATE, dataBytes, 20, true);
    }

}
