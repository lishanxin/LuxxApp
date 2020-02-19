package net.senink.piservice.services;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;

import java.util.List;

/**
 * Created by wensttu on 2016/6/22.
 */
public class PISXinLight extends PISBase {
    public static final byte PIS_CMD_LED_ONOFF_SET    = (byte)0x91; /**< 设置灯开关*/
    public static final byte PIS_CMD_LED_COLDWARM_SET = (byte)0xA0; /**< 设置冷暖色*/
    public static final byte PIS_CMD_LED_GET_STATE    = (byte)0xA2; /**< 获取灯状态*/
    public static final byte PIS_CMD_LED_BLINK_SET    = (byte)0xA3; /**< 设置灯闪烁*/
    public static final byte PIS_CMD_LED_BLINK_GET    = (byte)0xA4; /**< 获取灯闪烁*/

    public static final byte PIS_CMD_EFFECT_SET = (byte)0xAC;       //设置灯的效果

    public static final byte PIS_MSG_LED_STATUS = (byte)0x30; /**< 订阅灯状态*/

    public PISXinLight(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo, SERVICE_TYPE_SERVICE);

        Commands.add(new PICommandProperty(PIS_CMD_LED_ONOFF_SET,
                "设置灯开关",
                "设置灯开关",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LED_COLDWARM_SET,
                "色温设置",
                "设置冷暖色",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LED_GET_STATE,
                "获取状态",
                "获取灯的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LED_BLINK_SET,
                "闪烁",
                "控制灯闪烁",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LED_BLINK_GET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_EFFECT_SET,
                "设置灯的效果",
                "设置灯的效果",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_LED_STATUS,
                "状态消息",
                "灯当前的状态",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));

    }

    public static final int XINLIGHT_STATUS_OFF = 0;
    public static final int XINLIGHT_STATUS_ON  = 1;
    public static final int XINLIGHT_STATUS_BLINK = 2;

    /**
     * 属性
     */
    private int LightStatus = XINLIGHT_STATUS_OFF;
    public int getLightStatus(){
        if (ServiceType == PISBase.SERVICE_TYPE_GROUP){
            LightStatus = XINLIGHT_STATUS_OFF;
            List<PISBase> srvs = getGroupObjects();
            for (PISBase srv : srvs){
                PISXinLight light = (PISXinLight)srv;
                if (light.getLightStatus() > LightStatus)
                    LightStatus = light.getLightStatus();
            }
        }
        return LightStatus;
    }
    public void setLightStatus(int status){
        if (status < XINLIGHT_STATUS_OFF || status > XINLIGHT_STATUS_BLINK)
            return;
        LightStatus = status;
    }

    private int ColdValue = 0;
    private int WarmValue = 0;

    public int getCold(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinLight)light).getCold();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return ColdValue;
    }
    public int getWarm(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinLight)light).getWarm();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return WarmValue;
    }

    public byte[] getLightByte(boolean needSave){
        byte[] dataBytes = new byte[2];

        dataBytes[0] = (byte)(this.getCold() & 0xFF);
        dataBytes[1] = (byte)(this.getWarm() & 0xFF);
        dataBytes[2] = (byte)(needSave?1:0);

        return dataBytes;
    }

    public void setLightByte(byte[] lightByte){
        if (lightByte == null || lightByte.length < 2)
            throw new IllegalArgumentException("参数错误");

        ColdValue = (lightByte[0] & 0xFF);
        WarmValue = (lightByte[1] & 0xFF);

//        if (ColdValue + WarmValue > 0)
//            LightStatus = XINLIGHT_STATUS_ON;
//        else
//            LightStatus = XINLIGHT_STATUS_OFF;
    }

    public class XinLightBlinkData{
        public int ColdStart;
        public int WarmStart;
        public int ColdEnd;
        public int WarmEnd;
        public int Intevel;         //间隔时间
        public int Times;           //次数
        public int RampRate;        //渐变率，0-无渐变
    }
    private XinLightBlinkData BlinkData;
    public XinLightBlinkData getBlinkData(){
        return BlinkData;
    }

    public void setBlinkData(XinLightBlinkData blink){
        if (blink == null)
            throw new IllegalArgumentException("参数不能为空");
        BlinkData = blink;
    }

    public void setBlinkData(byte[] blinkByte){
        if (blinkByte == null || blinkByte.length != 7)
            throw new IllegalArgumentException("参数不能为空");
        if (BlinkData == null)
            BlinkData = new XinLightBlinkData();
        BlinkData.ColdStart = blinkByte[0] & 0xFF;
        BlinkData.WarmStart = blinkByte[1] & 0xFF;
        BlinkData.ColdEnd   = blinkByte[2] & 0xFF;
        BlinkData.WarmEnd   = blinkByte[3] & 0xFF;

        BlinkData.Intevel   = blinkByte[4];
        BlinkData.Times     = blinkByte[5];
        BlinkData.RampRate  = blinkByte[6];
    }

    private void processLedStateData(byte[] stBytes){
        int mSt = stBytes[0] & 0xF;
        if (mSt < XINLIGHT_STATUS_OFF || mSt > XINLIGHT_STATUS_BLINK)
            return;
        LightStatus = mSt;
        switch (LightStatus){
            case XINLIGHT_STATUS_OFF:
            case XINLIGHT_STATUS_ON:
                byte[] lightByte = new byte[2];
                System.arraycopy(stBytes, 1, lightByte, 0, 2);
                this.setLightByte(lightByte);
                break;
            case XINLIGHT_STATUS_BLINK:
                byte[] blinkByte = new byte[7];
                System.arraycopy(stBytes, 1, blinkByte, 0, 7);
                this.setBlinkData(blinkByte);
                break;
        }

    }

    /**
     * PIPA 消息处理函数
     * @param msg   消息
     * @param hPara 高位参数
     * @param lPara 低位参数
     */
    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_CMD_LED_COLDWARM_SET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setLightByte(ackData.rawRequestData.Data);
                        break;
                    case PIS_CMD_LED_ONOFF_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setLightStatus(ackData.rawRequestData.Data[0]);
                        break;
                    }
                    case PIS_CMD_LED_GET_STATE:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.processLedStateData(ackData.data);
                        break;
                    case PIS_CMD_LED_BLINK_SET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setBlinkData(ackData.rawRequestData.Data);
                        break;
                    case PIS_CMD_LED_BLINK_GET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setBlinkData(ackData.data);
                        break;
                    default:
                        break;
                }
            }
            break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_MSG_LED_STATUS:
                        this.processLedStateData(ackData.data);
                        break;
                    default:
                        break;
                }
            }
            break;
            default:
                break;
        }

        super.onProcess(msg, hPara, lPara);
    }

    /**
     * 开关冷暖灯
     * @param isOn 需要设置的状态
     * @return PipaRequest对象
     */
    public PipaRequest commitLightStatus(boolean isOn){
        byte[] dataBytes = new byte[1];
        dataBytes[0] = (byte)(isOn?1:0);
        return new PipaRequest(this, PIS_CMD_LED_ONOFF_SET, dataBytes, 1, true);
    }

    /**
     * 设置灯的亮度及色温
     * @param cold  冷色的亮度
     * @param warm  暖色的亮度
     * @param needSave 是否需要存储所设置的亮度
     * @return PipaRequest对象
     */
    public PipaRequest commitLightCW(int cold, int warm, boolean needSave){
        byte[] dataBytes = new byte[3];

        dataBytes[0] = (byte)(cold & 0xFF);
        dataBytes[1] = (byte)(warm & 0xFF);
        dataBytes[2] = (byte)(needSave?1:0);

        return new PipaRequest(this, PIS_CMD_LED_COLDWARM_SET, dataBytes, 3, false);
    }

    /**
     * 设置灯的闪烁
     * @param coldStart     冷光亮度起始值
     * @param warmStart     暖光亮度起始值
     * @param coldEnd       冷光亮度结束值
     * @param warmEnd       暖光亮度结束值
     * @param inteval       闪烁的间隔
     * @param timers        闪烁的次数
     * @param rate          0 - 闪烁，非0表示渐变的速率
     * @return PipaRequest对象
     */
    public PipaRequest commitLightBlink(int coldStart, int warmStart,
                                        int coldEnd, int warmEnd,
                                        int inteval, int timers, int rate){
        byte[] dataBytes = new byte[7];

        dataBytes[0] = (byte)(coldStart & 0xFF);
        dataBytes[1] = (byte)(warmStart & 0xFF);
        dataBytes[2] = (byte)(coldEnd & 0xFF);
        dataBytes[3] = (byte)(warmEnd & 0xFF);
        dataBytes[4] = (byte)(inteval & 0xFF);
        dataBytes[5] = (byte)(timers & 0xFF);
        dataBytes[6] = (byte)(rate & 0xFF);

        return new PipaRequest(this, PIS_CMD_LED_BLINK_SET, dataBytes, 7, true);

    }


    /**
     * 获取灯的状态
     * @return PipaRequest对象
     */
    public PipaRequest updateLightStatus(){
        return new PipaRequest(this, PIS_CMD_LED_GET_STATE, null, 0, true);
    }

    /**
     * 获取灯闪烁的相关信息
     * @return PipaRequest对象
     */
    public PipaRequest updateLightBlink(){
        return new PipaRequest(this, PIS_CMD_LED_BLINK_GET, null, 0, true);
    }

    public PipaRequest commitLightEffect(int mode){
        byte[] effectBytes = new byte[2];
        effectBytes[0] = (byte)mode;
        effectBytes[1] = 1;

        return new PipaRequest(this, PIS_CMD_EFFECT_SET, effectBytes, 2, true);
    }
}
