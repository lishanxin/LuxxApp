package net.senink.piservice.services;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;

import java.util.List;

import static net.senink.piservice.pis.PISBase.SERVICE_TYPE_SERVICE;

/**
 * Created by wensttu on 2016/11/17.
 */

public class PISXinColorLight extends PISBase{
    public static final byte PIS_CMD_LIGHT_RGBW_SET  = (byte)0x90;  //设置灯的颜色
    public static final byte PIS_CMD_LIGHT_ONOFF_SET = (byte)0x91;  //开、关灯
    public static final byte PIS_CMD_LIGHT_GET_STATE = (byte)0x92;  //获取灯的状态(RGBW，全为零时表示关状态)
    public static final byte PIS_CMD_LIGHT_BLINK_SET = (byte)0x93;  //控制灯闪烁
    public static final byte PIS_CMD_LIGHT_BLINK_GET = (byte)0x94;  //获取灯当前的闪烁参数

    public static final byte PIS_CMD_EFFECT_SET = (byte)0xAC;       //设置灯的效果

    /**
     * 消息类
     */
    public static final byte PIS_MSG_LIGHT_STATUS = (byte)0x30;  //当前灯的状态(可订阅)

    public PISXinColorLight(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo, SERVICE_TYPE_SERVICE);

        Commands.add(new PICommandProperty(PIS_CMD_LIGHT_RGBW_SET,
                "设置灯光",
                "设置灯的颜色",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LIGHT_ONOFF_SET,
                "开/关",
                "设置灯的开关",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LIGHT_GET_STATE,
                "获取状态",
                "获取灯的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LIGHT_BLINK_SET,
                "闪烁",
                "控制灯闪烁",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_LIGHT_BLINK_GET,
                "获取闪烁参数",
                "获取闪烁参数",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));



        Commands.add(new PICommandProperty(PIS_CMD_EFFECT_SET,
                "设置灯的效果",
                "设置灯的效果",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_LIGHT_STATUS,
                "状态消息",
                "灯当前的状态",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));

    }

    /**
     * 属性
     */
    private int LightStatus = XINCOLOR_STATUS_OFF;
    public int getLightStatus(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinColorLight)light).getLightStatus();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return LightStatus;
    }
    public void setLightStatus(int status){
        if (status < XINCOLOR_STATUS_OFF || status > XINCOLOR_STATUS_BLINK)
            return;
        LightStatus = status;
    }

    private int RedValue = 0;       //红光亮度
    private int GreenValue = 0;     //绿光亮度
    private int BlueValue = 0;      //蓝光亮度
    private int WhiteValue = 0;     //白光亮度
    private int YellowValue = 0;       //黄光亮度

    public int getRedValue(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinColorLight)light).getRedValue();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        else
            return RedValue;
    }
    public int getGreenValue(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinColorLight)light).getGreenValue();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return GreenValue;
    }
    public int getBlueValue(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinColorLight)light).getBlueValue();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return BlueValue;
    }
    public int getWhiteValue(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinColorLight)light).getWhiteValue();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return WhiteValue;
    }
    public int getYellowValue(){
        if (this.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            int rv = 0;
            List<PISBase> lights = this.getGroupObjects();
            for (PISBase light : lights){
                int rv1 = ((PISXinColorLight)light).getYellowValue();
                rv = (rv > rv1)?rv:rv1;
            }

            return rv;
        }
        return YellowValue;
    }

    public byte[] getColorBytes(boolean needSave){
        byte[] databytes = new byte[6];

        databytes[0] = (byte)(this.getRedValue() & 0xFF);
        databytes[1] = (byte)(this.getGreenValue() & 0xFF);
        databytes[2] = (byte)(this.getBlueValue() & 0xFF);
        databytes[3] = (byte)(this.getWhiteValue() & 0xFF);
        databytes[4] = (byte)(this.getYellowValue() & 0xFF);

        databytes[5] = (byte)((needSave?1:0)<<4 | (this.getLightStatus() & 0xF));

        return databytes;
    }

    public void setColorBytes(byte[] color){
        if (color == null || color.length != 6)
            return;

        RedValue = color[1] & 0xFF;
        GreenValue = color[2] & 0xFF;
        BlueValue = color[3] & 0xFF;
        WhiteValue = color[4] & 0xFF;
        YellowValue = color[5] & 0xFF;

        setLightStatus(color[0]&0xF);
    }

    public class XinColorBlinkData{
        int RedStart;
        int GreenStart;
        int BlueStart;
        int WhiteStart;
        int YellowStart;
        int RedEnd;
        int GreenEnd;
        int BlueEnd;
        int WhiteEnd;
        int YellowEnd;
        int Intevel;         //间隔时间
        int Times;           //次数
        int RampRate;        //渐变率，0-无渐变
    }

    private transient PISXinColorLight.XinColorBlinkData BlinkData;
    public PISXinColorLight.XinColorBlinkData getBlinkData(){
        return BlinkData;
    }
    public void setBlinkData(byte[] blinkBytes){
        if (blinkBytes == null || blinkBytes.length != 7)
            return;
        if (BlinkData == null)
            BlinkData = new PISXinColorLight.XinColorBlinkData();
        BlinkData.RedStart = blinkBytes[0] & 0xF;
        BlinkData.GreenStart = (blinkBytes[0] & 0xF0) >> 4;
        BlinkData.BlueStart = (blinkBytes[1] & 0xF);
        BlinkData.WhiteStart = (blinkBytes[1] & 0xF0) >> 4;

        BlinkData.RedEnd = blinkBytes[2] & 0xF;
        BlinkData.GreenEnd = (blinkBytes[2] & 0xF0) >> 4;
        BlinkData.BlueEnd = blinkBytes[3] & 0xF;
        BlinkData.WhiteStart = (blinkBytes[3] & 0xF0) >> 4;

        BlinkData.Intevel = blinkBytes[4];
        BlinkData.Times = blinkBytes[5];
        BlinkData.RampRate = blinkBytes[6];
    }
    public void setBlinkData(PISXinColorLight.XinColorBlinkData blink){
        if (blink == null)
            return;
        BlinkData = blink;
    }


    private static final int XINCOLOR_STATUS_OFF = 0;
    private static final int XINCOLOR_STATUS_ON  = 1;
    private static final int XINCOLOR_STATUS_BLINK = 2;

    @Override
    public void Initialization(){
        super.Initialization();
    }

    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISBase.PISCommandAckData ackData = (PISBase.PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_CMD_LIGHT_RGBW_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;

                        this.setColorBytes(ackData.rawRequestData.Data);

                        return;
                    }
                    case PIS_CMD_LIGHT_ONOFF_SET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;
                        this.setLightStatus(ackData.rawRequestData.Data[0]);
                        break;
                    case PIS_CMD_LIGHT_GET_STATE:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        this.setColorBytes(ackData.data);
                        break;
                    case PIS_CMD_LIGHT_BLINK_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;
                        this.setColorBytes(ackData.rawRequestData.Data);
                        break;
                    }
                    case PIS_CMD_LIGHT_BLINK_GET:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        this.setColorBytes(ackData.data);
                        break;
                    default:
                        break;
                }
            }
            break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISBase.PISCommandAckData ackData = (PISBase.PISCommandAckData)lPara;
                if (hPara == PIS_MSG_LIGHT_STATUS){
                    int state = (ackData.data[0] & 0xF0) >> 4;
                    System.arraycopy(ackData.data, 1, ackData.data, 0, ackData.data.length-1);
                    if (state == XINCOLOR_STATUS_ON || state == XINCOLOR_STATUS_OFF){
                        this.setColorBytes(ackData.data);
                    }else if (state == XINCOLOR_STATUS_BLINK){
                        this.setBlinkData(ackData.data);
                    }
                }
            }
            break;
            default:
                break;
        }

        super.onProcess(msg, hPara, lPara);
    }

    /**
     * 公有方法
     */
    public PipaRequest commitLightOnOff(boolean isOn){
        byte[] isOnByte = new byte[1];
        isOnByte[0] = (byte)(isOn?1:0);
        return new PipaRequest(this, PIS_CMD_LIGHT_ONOFF_SET, isOnByte, 1, true);
    }

    public PipaRequest commitLightColor(int red, int green, int blue, int white, int yellow, boolean needSave){
        byte[] colorBytes = new byte[6];
        colorBytes[0] = (byte)(red & 0xFF);
        colorBytes[1] = (byte)(green & 0xFF);
        colorBytes[2] = (byte)(blue & 0xFF);
        colorBytes[3] = (byte)(white & 0xFF);
        colorBytes[4] = (byte)(yellow & 0xFF);
        colorBytes[5] = (byte)(needSave?1:0);

        return new PipaRequest(this, PIS_CMD_LIGHT_RGBW_SET, colorBytes, 6, false);
    }


    public PipaRequest raiseLightRGBW(int redup, int greenup, int blueup, int whiteup, int yellowup, boolean needSave){
        return this.commitLightColor(this.getRedValue()+redup,
                this.getGreenValue() + greenup,
                this.getBlueValue() + blueup,
                this.getWhiteValue() + whiteup,
                this.getYellowValue() + yellowup,
                needSave);
    }

    /**
     *  降低颜色亮度
     */
    public PipaRequest reduceLightRGBW(int reddown, int greendown, int bluedown, int whitedown, int yellowdown, boolean needSave){
        return this.commitLightColor(this.getRedValue() - reddown,
                this.getGreenValue() - greendown,
                this.getBlueValue() - bluedown,
                this.getWhiteValue() - whitedown,
                this.getYellowValue() - yellowdown,
                needSave);
    }

    public PipaRequest updateLightStatus(){
        return new PipaRequest(this, PIS_CMD_LIGHT_GET_STATE, null, 0, true);
    }

    public PipaRequest commitLightBlink(int rStart, int gStart, int bStart, int wStart,
                                        int rEnd, int gEnd, int bEnd, int wEnd,
                                        int interval, int timers, int rate){
        byte[] blinkBytes = new byte[7];
        blinkBytes[0] = (byte)((rStart & 0xF) | ((gStart & 0xF) << 4));
        blinkBytes[1] = (byte)((bStart & 0xF) | ((wStart & 0xF) << 4));
        blinkBytes[2] = (byte)((rEnd & 0xF) | ((gEnd & 0xF) << 4));
        blinkBytes[3] = (byte)((bEnd & 0xF) | ((wEnd & 0xF) << 4));

        blinkBytes[4] = (byte)(interval & 0xFF);
        blinkBytes[5] = (byte)(timers & 0xFF);
        blinkBytes[6] = (byte)(rate & 0xFF);

        return new PipaRequest(this, PIS_CMD_LIGHT_BLINK_SET, blinkBytes, 7, true);
    }

    public PipaRequest updateLightBlink(){
        return new PipaRequest(this, PIS_CMD_LIGHT_BLINK_GET, null, 0, true);
    }

    public PipaRequest commitLightEffect(int mode){
        byte[] effectBytes = new byte[2];
        effectBytes[0] = (byte)mode;
        effectBytes[1] = 1;

        return new PipaRequest(this, PIS_CMD_EFFECT_SET, effectBytes, 2, true);
    }

}
