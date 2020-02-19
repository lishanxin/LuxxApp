package net.senink.piservice.services;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.util.ByteUtilLittleEndian;

/**
 * Created by wensttu on 2016/6/23.
 */
public class PISXinSwitch extends PISBase {
    private static final long serialVersionUID = -8876492266027275131L;
    // private SwitchLcsPara mLcsPara;
    private boolean SwitchStatus;        // 开关状态
    private float Power;                 // 功率
    private int ComputeFreq;            // 功率计算频率。
    private int SampleFreq;             // 采样频率
    private boolean BackLightStatus;    // 背光状态

    public static final byte PIS_CMD_KGTG_LCS_GET = (byte)0x90;
    public static final byte PIS_CMD_KGTG_LCS_SET = (byte)0x91;
    public static final byte PIS_CMD_KGTG_POWER_GET = (byte)0x92;
    public static final byte PIS_CMD_KGTG_POWER_CALCFREQ_SET = (byte)0x93;
    public static final byte PIS_CMD_KGTG_POWER_CALCFREQ_GET = (byte)0x94;
    public static final byte PIS_CMD_KGTG_POWER_SAMPFREQ_SET = (byte)0x95;
    public static final byte PIS_CMD_KGTG_POWER_SAMPFREQ_GET = (byte)0x96;
    public static final byte PIS_CMD_KGTG_POWER_CHANGERATE_SET = (byte)0x97;
    public static final byte PIS_CMD_KGTG_POWER_CHANGERATE_GET = (byte)0x98;
    public static final byte PIS_CMD_KGTG_POWER_ALARM_GET = (byte)0x98;
    //设置和读取排插背光灯状态
    public static final byte PIS_CMD_BACKLIGHT_GET = (byte)0x99;           //读取背光当前状态
    public static final byte PIS_CMD_BACKLIGHT_SET = (byte)0x9A;           //设置背光当前状态 1开，0关
    public static final byte PIS_MSG_BACKLIGHT_STATUS = (byte)0x33;        //订阅背光状态，多人联动可能

    public static final byte PIS_MSG_KGTG_POWER = (byte)0x30;
    public static final byte PIS_MSG_KGTG_STATUS = (byte)0x31;
    public static final byte PIS_MSG_KGTG_ALARM = (byte)0x32;

    public PISXinSwitch(PIAddress addr, PIServiceInfo srvInfo) {
        super(addr, srvInfo, SERVICE_TYPE_SERVICE);

        Commands.add(new PICommandProperty(PIS_CMD_KGTG_LCS_GET,
                "设置灯开关",
                "设置灯开关",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_LCS_SET,
                "色温设置",
                "设置冷暖色",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_GET,
                "获取状态",
                "获取灯的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_CALCFREQ_SET,
                "闪烁",
                "控制灯闪烁",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_CALCFREQ_GET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_SAMPFREQ_SET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_SAMPFREQ_GET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_CHANGERATE_SET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_CHANGERATE_GET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_KGTG_POWER_ALARM_GET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_BACKLIGHT_GET,
                "状态消息",
                "灯当前的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_BACKLIGHT_SET,
                "状态消息",
                "灯当前的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_MSG_BACKLIGHT_STATUS,
                "状态消息",
                "灯当前的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Messages.add(new PICommandProperty(PIS_MSG_KGTG_POWER,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_KGTG_STATUS,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_KGTG_ALARM,
                "状态消息",
                "灯当前的状态",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
    }

    /**
     * setter/getter
     */

    public boolean getSwitchStatus(){
        return SwitchStatus;
    }
    public void setSwitchStatus(boolean isOn){
        SwitchStatus = isOn;
    }

    public void setPower(byte[] powerBytes){
        if (powerBytes == null || powerBytes.length != 4)
            return;
        Power = ByteUtilLittleEndian.getFloat(powerBytes);
    }

    public void setComputeFreq(byte[] computerBytes){
        if (computerBytes == null || computerBytes.length != 4)
            return;
        ComputeFreq = ByteUtilLittleEndian.getInt(computerBytes);
    }

    public void setSampleFreq(byte[] sampleBytes){
        if (sampleBytes == null || sampleBytes.length != 4)
            return;
        SampleFreq = ByteUtilLittleEndian.getInt(sampleBytes);
    }

    public void setBackLightStatus(byte[] backBytes){
        if (backBytes == null || backBytes.length != 1)
            return;
        BackLightStatus = (backBytes[0]==1);
    }

    public float getPower(){
        return Power;
    }

    /**
     * PIPA消息处理
     */
    protected void onProcess(int msg, int hPara, Object lPara) {

        PISManager pm = PISManager.getInstance();
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch(ackData.Command) {
                    case PIS_CMD_KGTG_LCS_GET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setSwitchStatus((ackData.data[0] == 1)?true:false);
                        break;
                    case PIS_CMD_KGTG_POWER_GET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null ||
                                ackData.data.length < 4)
                            break;
                        this.setPower(ackData.data);
                        break;
                    case PIS_CMD_KGTG_LCS_SET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setSwitchStatus((ackData.rawRequestData.Data[0] == 1)?true:false);
                        break;
                    case PIS_CMD_KGTG_POWER_CALCFREQ_GET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setComputeFreq(ackData.data);
                        break;
                    case PIS_CMD_KGTG_POWER_CALCFREQ_SET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setComputeFreq(ackData.rawRequestData.Data);
                        break;
                    case PIS_CMD_KGTG_POWER_CHANGERATE_GET:
                        break;
                    case PIS_CMD_KGTG_POWER_CHANGERATE_SET:
                        break;
                    case PIS_CMD_KGTG_POWER_SAMPFREQ_GET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setSampleFreq(ackData.data);
                        break;
                    case PIS_CMD_KGTG_POWER_SAMPFREQ_SET:
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setSampleFreq(ackData.rawRequestData.Data);
                        break;
                    case PIS_CMD_BACKLIGHT_SET: {
                        if(hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setBackLightStatus(ackData.rawRequestData.Data);
                    }
                    break;
                    case PIS_CMD_BACKLIGHT_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setBackLightStatus(ackData.data);
                    }
                    break;
                }

            }
            break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command){
                    case PIS_MSG_KGTG_POWER:
                        this.setPower(ackData.data);
                        break;
                    case PIS_MSG_KGTG_STATUS:
                        this.setSwitchStatus((ackData.data[0]&0x1)==1);
                        break;
                    case PIS_MSG_BACKLIGHT_STATUS:
                        this.setBackLightStatus(ackData.data);
                        break;
                }
            }
            break;
        }
        super.onProcess(msg, hPara, lPara);
    }

    /**
     * PISXinSwitch public API
     */

    /**
     * 获取功率采样频率
     * @return
     */
    public PipaRequest updateSampleFrequercy(){
        return new PipaRequest(this, PIS_CMD_KGTG_POWER_SAMPFREQ_GET, null, 0, true);
    }

    /**
     * 设置功率采样频率
     * @param frequercy 采样频率
     * @return
     */
    public PipaRequest commitSampleFrequercy(int frequercy){
        byte[] dataBytes = new byte[4];

        dataBytes = ByteUtilLittleEndian.getBytes(frequercy);

        return new PipaRequest(this, PIS_CMD_KGTG_POWER_SAMPFREQ_SET, dataBytes, 4, true);
    }

    /**
     * 获取开光状态信息
     * @return
     */
    public PipaRequest updateSwitchStatus(){
        return new PipaRequest(this, PIS_CMD_KGTG_LCS_GET, null, 0, true);
    }

    /**
     * 设置开光状态
     * @param isOn true - 导通开关，false - 断开开关
     * @return PipaRequest对象
     */
    public PipaRequest commitSwitchStatus(boolean isOn){
        byte[] dataBytes = new byte[1];

        dataBytes[0] = (byte)(isOn?1:0);

        return new PipaRequest(this, PIS_CMD_KGTG_LCS_SET, dataBytes, 1, true);
    }

    /**
     * 获取当前功率值
     * @return PipaRequest
     */
    public PipaRequest updatePower(){
        return new PipaRequest(this, PIS_CMD_KGTG_POWER_GET, null, 0, true);
    }

    /**
     * 设置功率计算频率
     * @return PipaRequest
     */
    public PipaRequest updateComputerFrequercy(){
        return new PipaRequest(this, PIS_CMD_KGTG_POWER_CALCFREQ_GET, null, 0, true);
    }

    /**
     * 设置功率计算频率
     * @param frequercy 计算频率
     * @return PipaRequest
     */
    public PipaRequest commitComputerFrequercy(int frequercy){
        byte[] dataBytes = ByteUtilLittleEndian.getBytes(frequercy);

        return new PipaRequest(this, PIS_CMD_KGTG_POWER_CALCFREQ_SET, dataBytes, 4, true);
    }

    /**
     * 获取背光状态
     * @return PipaRquest
     */
    public PipaRequest updateBackLightStatus(){
        return new PipaRequest(this, PIS_CMD_BACKLIGHT_GET, null, 0, true);
    }

    /**
     * 设置背光状态
     * @param isOn  true - 开启背光，false - 关闭背光
     * @return PipaRequest
     */
    public PipaRequest commitBackLightStatus(boolean isOn){
        byte[] dataBytes = new byte[1];

        dataBytes[0] = (byte)(isOn?1:0);

        return new PipaRequest(this, PIS_CMD_BACKLIGHT_SET, dataBytes, 1, true);
    }

}
